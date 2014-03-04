<?php

namespace Money;

class Bitcoin {
#const BITCOIN_NODE = '173.224.125.222'; // w001.mo.us temporary
const BITCOIN_NODE = '50.97.137.37';
static private $pending = array();

public static function update() {
// update all nodes
$list = \DB::DAO('Money_Bitcoin_Host')->search(null);
  foreach($list as $bean) {
    $bean->Last_Update = \DB::i()->now();
    $client = \Controller::Driver('Bitcoin', $bean->Money_Bitcoin_Host__);
    if (!$client->isValid()) continue;
    $info = $client->getInfo();
    if (!$info) {
    $bean->Status = 'down';
    $bean->commit();
    continue;
  }

if (($info['generate']) && ($bean->Generate == 'N')) {
$client->setGenerate(false);
} elseif ((!$info['generate']) && ($bean->Generate != 'N')) {
$client->setGenerate(true);
}

$bean->Version = $info['version'];
$bean->Coins = (int)round($info['balance'] * 100000000);
$bean->Connections = $info['connections'];
$bean->Blocks = $info['blocks'];
$bean->Hashes_Per_Sec = $info['hashespersec'];
$bean->Status = 'up';
$bean->commit();

if (is_null($bean->Address)) { // get in addr (generate if needed)
$list = $client->getAddressesByLabel('_DEFAULT');
if ($list) {
$bean->Address = $list[0];
} else {
$bean->Address = $client->getNewAddress('_DEFAULT');
}
$bean->commit();
}

if (($bean->Keep_Empty == 'Y') && ($bean->Coins > 100000000)) {
// empty it!
$addr = self::getNullAddr();
try {
$client->sendToAddress($addr, $bean->Coins / 100000000);
} catch(\Exception $e) {
// try smaller amount (maybe failed because of fee)
try {
$c = $bean->Coins / 100000000;
$c = round($c/4, 2);
if ($c > 0)
$client->sendToAddress($addr, $c);
} catch(\Exception $e) {
// give up
}
}
}

if ($bean->Coins > (500*100000000)) {
// more than 500 coins on this host, shuffle some~
$client->sendToAddress($client->getNewAddress(), (mt_rand(18,20000)/100));
}
}
}

public static function getRate() {
$ticker = \Money\Trade::ticker('BTC','EUR');
$btc = \DB::DAO('Currency')->searchOne(array('Currency__' => 'BTC'));

$btc->Ex_Bid = 1/$ticker['vwap']['value'];
$btc->Ex_Ask = 1/$ticker['vwap']['value'];
$btc->commit();

\DB::DAO('Currency_History')->insert(array('Currency__' => $btc->Currency__, 'Date' => gmdate('Y-m-d'), 'Ex_Bid' => $btc->Ex_Bid, 'Ex_Ask' => $btc->Ex_Ask));
}

public static function mergeSmallOutputs() {
$transaction = \DB::i()->transaction();
$lock = \DB::i()->lock('Money_Bitcoin_Available_Output');

$list = \DB::DAO('Money_Bitcoin_Available_Output')->search(array('Available' => 'Y', new \DB\Expr('`Value` < 100000000')), null, array(5));
if (count($list) < 3) return false;

$list[] = \DB::DAO('Money_Bitcoin_Available_Output')->searchOne(['Available' => 'Y', new \DB\Expr('`Value` > 100000000')]);

$input = array();
$amount = 0;
foreach($list as $bean) {
$key = \DB::DAO('Money_Bitcoin_Permanent_Address')->searchOne(array('Money_Bitcoin_Permanent_Address__' => $bean->Money_Bitcoin_Permanent_Address__));
if (!$key) throw new \Exception('Unusable output');
$tmp = array(
'privkey' => \Internal\Crypt::decrypt($key->Private_Key),
'tx' => $bean->Hash,
'N' => $bean->N,
'hash' => $bean->Money_Bitcoin_Permanent_Address__,
'amount' => $bean->Value,
'input_source' => $bean->Money_Bitcoin_Available_Output__,
);
$input[] = $tmp;
$amount += $bean->Value;
$bean->Available = 'N';
$bean->commit();
}
$output = \Money\Bitcoin::getNullAddr();
$output = \Util\Bitcoin::decode($output);
if (!$output) return false;

$tx = \Util\Bitcoin::makeNormalTx($input, $amount, $output, $output);
self::publishTransaction($tx);
return $transaction->commit();
}

public static function splitBigOutputs() {
$transaction = \DB::i()->transaction();
$lock = \DB::i()->lock('Money_Bitcoin_Available_Output');

$bean = \DB::DAO('Money_Bitcoin_Available_Output')->searchOne(array('Available' => 'Y', new \DB\Expr('`Value` > 1000000000')));
if (!$bean) return;

$input = array();
$amount = 0;

$key = \DB::DAO('Money_Bitcoin_Permanent_Address')->searchOne(array('Money_Bitcoin_Permanent_Address__' => $bean->Money_Bitcoin_Permanent_Address__));
if (!$key) throw new \Exception('Unusable output');
$tmp = array(
'privkey' => \Internal\Crypt::decrypt($key->Private_Key),
'tx' => $bean->Hash,
'N' => $bean->N,
'hash' => $bean->Money_Bitcoin_Permanent_Address__,
'amount' => $bean->Value,
'input_source' => $bean->Money_Bitcoin_Available_Output__,
);
$input[] = $tmp;
$amount += $bean->Value;
$bean->Available = 'N';
$bean->commit();

$output1 = \Util\Bitcoin::decode(\Money\Bitcoin::getNullAddr());
$output2 = \Util\Bitcoin::decode(\Money\Bitcoin::getNullAddr());

$tx = \Util\Bitcoin::makeNormalTx($input, round(mt_rand($amount*0.4, $amount*0.6)), $output1, $output2);
self::publishTransaction($tx);
return $transaction->commit();
}

public static function getTxInput($amount, $inputs = array()) {
// get input that covers at least $amount
$tx_list = array();
$total = 0;
if ($amount <= 0) throw new \Exception('Invalid TX amount');

// check for forced inputs
foreach($inputs as $input) {
$bean = \DB::DAO('Money_Bitcoin_Available_Output')->searchOne(array('Hash' => $input['hash'], 'N' => $input['n']));
if (!$bean) continue; // not a valid input
$total += $bean->Value;
$tx_list[$bean->Money_Bitcoin_Available_Output__] = $bean;
$bean->Available = 'N';
$bean->commit();
if (count($tx_list) > 5) break; // even only one input is enough to invalidate the old tx, let's grab 5
}

while(true) {
if ($total == $amount) break;
if (($total > $amount) && ($total - $amount > 1000000)) break;
// need more inputs
$skip_ok = false;
if (count($tx_list) >= 3) {
// need more inputs, and need those *fast*, take the largest that would fit our remaining balance
$bean = \DB::DAO('Money_Bitcoin_Available_Output')->searchOne(array('Available' => 'Y', new \DB\Expr('`Money_Bitcoin_Available_Output__` NOT IN ('.\DB::i()->quote(array_keys($tx_list), \DB::QUOTE_LIST).')'), new \DB\Expr('`Value` > '.($amount - $total))), array(new \DB\Expr('RAND()')));
if (!$bean) {
// take largest one
$bean = \DB::DAO('Money_Bitcoin_Available_Output')->searchOne(array('Available' => 'Y', new \DB\Expr('`Money_Bitcoin_Available_Output__` NOT IN ('.\DB::i()->quote(array_keys($tx_list), \DB::QUOTE_LIST).')')), array('Value' => 'DESC'));
}
if (!$bean)
$bean = \DB::DAO('Money_Bitcoin_Available_Output')->searchOne(array(new \DB\Expr('`Money_Bitcoin_Available_Output__` NOT IN ('.\DB::i()->quote(array_keys($tx_list), \DB::QUOTE_LIST).')')), array(new \DB\Expr('RAND()')));
} elseif ($tx_list) {
$bean = \DB::DAO('Money_Bitcoin_Available_Output')->searchOne(array('Available' => 'Y', new \DB\Expr('`Money_Bitcoin_Available_Output__` NOT IN ('.\DB::i()->quote(array_keys($tx_list), \DB::QUOTE_LIST).')')), array(new \DB\Expr('RAND()')));
} else {
$bean = \DB::DAO('Money_Bitcoin_Available_Output')->searchOne(array('Available' => 'Y'), array(new \DB\Expr('RAND()')));
}

if (!$bean) {
$skip_ok = true;
if ($tx_list) {
$bean = \DB::DAO('Money_Bitcoin_Available_Output')->searchOne(array(new \DB\Expr('`Money_Bitcoin_Available_Output__` NOT IN ('.\DB::i()->quote(array_keys($tx_list), \DB::QUOTE_LIST).')')), array(new \DB\Expr('RAND()')));
} else {
$bean = \DB::DAO('Money_Bitcoin_Available_Output')->searchOne(null, array(new \DB\Expr('RAND()')));
}
}

if (!$bean) throw new \Exception('No available output for this TX');
// check if really available
if (!$skip_ok) {
$out = \DB::DAO('Money_Bitcoin_Block_Tx_Out')->searchOne(array('Hash' => $bean->Hash, 'N' => $bean->N));
if ($out) {
if ($out->Claimed == 'Y') {
$bean->Available = 'N';
$bean->commit();
continue;
}
}
}
$total += $bean->Value;
$tx_list[$bean->Money_Bitcoin_Available_Output__] = $bean;
}

$input = array();
foreach($tx_list as $bean) {
$key = \DB::DAO('Money_Bitcoin_Permanent_Address')->searchOne(array('Money_Bitcoin_Permanent_Address__' => $bean->Money_Bitcoin_Permanent_Address__));
if (!$key) throw new \Exception('Unusable output');
$tmp = array(
'privkey' => \Internal\Crypt::decrypt($key->Private_Key),
'tx' => $bean->Hash,
'N' => $bean->N,
'hash' => $bean->Money_Bitcoin_Permanent_Address__,
'amount' => $bean->Value,
);
$input[] = $tmp;
$bean->Available = 'N';
$bean->commit();
}
shuffle($input); // randomize inputs order
return $input;
}

public static function getPaymentAddr($payment_id) {
$private = \Util\Bitcoin::genPrivKey();
$info = \Util\Bitcoin::decodePrivkey($private);

$insert = array(
'Money_Bitcoin_Permanent_Address__' => $info['hash'],
'Money_Bitcoin_Host__' => null,
'Money_Merchant_Transaction_Payment__' => $payment_id,
'Private_Key' => \Internal\Crypt::encrypt($private),
'Created' => \DB::i()->now(),
'Used' => 'Y',
'Callback' => 'Money/Merchant/Transaction::bitcoinEvent'
);

if (!\DB::DAO('Money_Bitcoin_Permanent_Address')->insert($insert)) return false;

return \Money\Bitcoin\Address::byHash($info['hash']);
}

public static function getNullAddr($priv = false) {
$private = \Util\Bitcoin::genPrivKey();
$info = \Util\Bitcoin::decodePrivkey($private);
$address = \Util\Bitcoin::encode($info);

$insert = array(
'Money_Bitcoin_Permanent_Address__' => $info['hash'],
'Money_Bitcoin_Host__' => null,
'User_Wallet__' => null,
'Private_Key' => \Internal\Crypt::encrypt($private),
'Created' => \DB::i()->now(),
'Used' => 'Y',
);

if (!\DB::DAO('Money_Bitcoin_Permanent_Address')->insert($insert)) return false;

if ($priv) return array('priv' => $private, 'info' => $info, 'address' => $address);

return $address;
}

public static function getVerboseAddr($wallet, $description, $ipn = null, $user = null, $callback = null) {
if ($wallet && $wallet['Currency__'] != 'BTC') return false;

$private = \Util\Bitcoin::genPrivKey();
$info = \Util\Bitcoin::decodePrivkey($private);
$address = \Util\Bitcoin::encode($info);

$insert = array(
'Money_Bitcoin_Permanent_Address__' => $info['hash'],
'Money_Bitcoin_Host__' => null,
'User_Wallet__' => $wallet ? $wallet->getId() : null,
'Private_Key' => \Internal\Crypt::encrypt($private),
'Created' => \DB::i()->now(),
'Description' => $description,
'Ipn' => $ipn,
'Used' => 'Y', // do not use it for normal purposes
'Callback' => $callback
);
if (!is_null($user)) $insert['User_Rest__'] = $user->getRestId();

if (!\DB::DAO('Money_Bitcoin_Permanent_Address')->insert($insert)) return false;

return $address;
}

public static function getPermanentAddr($wallet, $user = null) {
if ($wallet['Currency__'] != 'BTC') return false;

$unused = \DB::DAO('Money_Bitcoin_Permanent_Address')->searchOne(array('User_Wallet__' => $wallet->getId(), 'Used' => 'N'));
if ($unused) {
if (strlen($unused->Money_Bitcoin_Permanent_Address__) != 40) return $unused->Money_Bitcoin_Permanent_Address__;
return \Util\Bitcoin::encode(array('version' => 0, 'hash' => $unused->Money_Bitcoin_Permanent_Address__));
}

$private = \Util\Bitcoin::genPrivKey();
$info = \Util\Bitcoin::decodePrivkey($private);
$address = \Util\Bitcoin::encode($info);

$insert = array(
'Money_Bitcoin_Permanent_Address__' => $info['hash'],
'Money_Bitcoin_Host__' => null,
'User_Wallet__' => $wallet->getId(),
'Private_Key' => \Internal\Crypt::encrypt($private),
'Created' => \DB::i()->now(),
);
if (!is_null($user)) $insert['User_Rest__'] = $user->getRestId();

if (!\DB::DAO('Money_Bitcoin_Permanent_Address')->insert($insert)) return false;

return $address;
}

/**
* Create a bitcoin address with some dynamic configuration, like autoselling, mails, etc...
* @param \User\Wallet $wallet
* @param array        $options
* @param \User        $user
* @return bool|string
* @throws \TokenException
*/
public static function getAddrWithOptions(\User\Wallet $wallet, array $options = [], \User $user = null) {
if ($wallet['Currency__'] != 'BTC') throw new \TokenException('Invalid currency provided', 'invalid_source_currency');

// filter fields in options
// autosell: bool Sell bitcoins when received
// email: bool Send email either when receiving bitcoins (no autosell) or once sold
// data: string custom data returned in the mail
// currency: string The currency used for autosell, default to default wallet
$filtered_options = [];
$fields = ['autosell' => 'bool', 'email' => 'bool', 'data' => 'string', 'currency' => 'string'];
foreach ($fields as $field => $type) {
if (isset($options[$field])) {
$value = $options[$field];
switch ($type) {
case 'bool':
$value = (bool)$value;
break;
default:
case 'string':
// truncate strings to 128 chars
$value = substr((string)$value, 0, 128);
break;
}
$filtered_options[$field] = $value;
}
}

if (isset($filtered_options['autosell']) && $filtered_options['autosell']) {
if (!isset($filtered_options['currency'])) {
throw new \TokenException('Missing currency for autosell', 'autosell_missing_currency');
}
}

// check currency if set
if (isset($filtered_options['currency'])) {
// check if that currency exists
$cur = \Currency::get($filtered_options['currency']);
if (!$cur || $cur->isVirtual()) {
throw new \TokenException('Invalid currency or virtual currency', 'invalid_target_currency');
}
}

// generate a new bitcoin address
$private = \Util\Bitcoin::genPrivKey();
$info = \Util\Bitcoin::decodePrivkey($private);
$address = \Util\Bitcoin::encode($info);

$insert = array(
'Money_Bitcoin_Permanent_Address__' => $info['hash'],
'Money_Bitcoin_Host__' => null,
'User_Wallet__' => $wallet->getId(),
'Private_Key' => \Internal\Crypt::encrypt($private),
'Created' => \DB::i()->now(),
'Description' => json_encode($filtered_options),
'Used' => 'Y', // do not use it for normal purposes
'Callback' => 'Money/Bitcoin::optionAddrEvent'
);
// if the call was done through the API
if (!is_null($user)) $insert['User_Rest__'] = $user->getRestId();

if (!\DB::DAO('Money_Bitcoin_Permanent_Address')->insert($insert)) {
throw new \TokenException('Couldn\'t create bitcoin address, please contact mtgox', 'unknown_error');
};

return $address;
}

public static function optionAddrEvent($addr, $hash_n, $block, $amount) {
// ignore until we have enough confirmations
if (!$block) return;

$options = json_decode($addr->Description, true);
/** @var $source_wallet \User\Wallet */
$source_wallet = \User\Wallet::byId($addr->User_Wallet__);

// manage autosell
if (isset($options['autosell']) && $options['autosell']) {
$callback = null;
if (isset($options['email']) && $options['email']) {
$callback = 'Money/Bitcoin::optionAddrSellEmail';
if ($options['data']) {
$callback .= '|' . $options['data'];
}
}
\Money\Trade::addOrder($source_wallet->getUser(), 'ask', $amount, $options['currency'], [], null, $callback);
} else {
// send email with details about the transaction
if (isset($options['email']) && $options['email']) {
$mail_page = \Registry::getInstance()->OptionAddrBlockEmail ?: 'mail/option_addr_bitcoin_rcvd.mail';
$mail_data = [
'_HASH'   => $hash_n,
'_BLOCK'  => $block,
'_AMOUNT' => $amount
];
if (isset($options['data'])) $mail_data['_DATA'] = $options['data'];
\Tpl::userMail($mail_page, $source_wallet->getUser(), $mail_data);
}
}
}

public static function optionAddrSellEmail($user, $oid, $type, $data = null) {
$user = \User::byId($user, false, true);
$trade_info = \Money\Trade::getOrderExecutionResult($user, $oid, $type == 'bid');
$mail_page = \Registry::getInstance()->OptionAddrOrderEmail ?: 'mail/option_addr_bitcoin_sold.mail';
$mail_data = [
'_TRADE_INFO' => $trade_info,
];
if ($data) $mail_data['_DATA'] = $data;
return \Tpl::userMail($mail_page, $user, $mail_data);
}

public static function checkOrders() {
// check data in Money_Bitcoin_Order to see if any order is completed
$db = \DB::i();
$list = $db['Money_Bitcoin_Order']->search(array('Status' => 'pending'));
$clients = array();

foreach($list as $bean) {
if (!isset($clients[$bean->Money_Bitcoin_Host__])) $clients[$bean->Money_Bitcoin_Host__] = \Controller::Driver('Bitcoin', $bean->Money_Bitcoin_Host__);
$client = $clients[$bean->Money_Bitcoin_Host__];
$total = (int)round($client->getReceivedByAddress($bean->Address, 3) * 100000000); // 3 confirmations

if ($bean->Coins == $total) { // nothing moved
if ($db->dateRead($bean->Expires) < time()) {
$bean->Status = 'expired';
$bean->commit();
continue;
}
}
$bean->Coins = $total;
$total += $bean->Coins_Extra;
if ($bean->Total <= $total) {
// payment complete!
$bean->Status = 'ok';
$bean->commit();

// mark order paid
$order = \Order::byId($bean->Order__);
if ($order->isPaid()) continue; // ?!
$info = array(
'method' => 'BITCOIN',
'class' => 'Bitcoin',
'stamp' => time(),
);
$order->paid($info);
continue;
}

$total_nc = (int)round($client->getReceivedByAddress($bean->Address, 0) * 100000000);
$bean->Coins_NC = $total_nc;
$bean->commit();
}
}

public static function getAddressForOrder($order) {
$total = $order->getTotal();
if ($total->getCurrency()->Currency__ != 'BTC') return false;
$btc = $total['value'];

$bean = \DB::DAO('Money_Bitcoin_Order')->searchOne(array('Order__' => $order->getId()));
if ($bean) {
if ($bean->Status != 'pending') return false;
$bean->Total = ((int)round($btc * 100))*1000000;
if ($bean->Address != '') {
$bean->commit();
return $bean;
} elseif ($bean->Coins == $bean->Coins_NC) {
$bean->Coins_Extra = $bean->Coins;
$bean->Coins = 0;
$bean->Coins_NC = 0;
// find a (new) random host
$host = \DB::DAO('Money_Bitcoin_Host')->searchOne(array('Status' => 'up', 'Allow_Order' => 'Y'), array(new \DB\Expr('RAND()')));
if (!$host) return false; // no available host right now
$client = \Controller::Driver('Bitcoin', $host->Money_Bitcoin_Host__);
$addr = $client->getNewAddress('ORDER:'.$order->getId());
// update
$bean->Address = $addr;
$bean->commit();
return $bean;
}
}

// find a random host
$host = \DB::DAO('Money_Bitcoin_Host')->searchOne(array('Status' => 'up', 'Allow_Order' => 'Y'), array(new \DB\Expr('RAND()')));
if (!$host) return false; // no available host right now

$client = \Controller::Driver('Bitcoin', $host->Money_Bitcoin_Host__);
$addr = $client->getNewAddress('ORDER:'.$order->getId());

// new entry
$db = \DB::i();
$uuid = \System::uuid();
$insert = array(
'Money_Bitcoin_Order__' => $uuid,
'Order__' => $order->getId(),
'Money_Bitcoin_Host__' => $host->Money_Bitcoin_Host__,
'Address' => $addr,
'Coins' => 0,
'Total' => ((int)round($btc * 100)) * 1000000,
'Created' => $db->now(),
'Expires' => $db->dateWrite(time()+(86400*10)),
);
$db['Money_Bitcoin_Order']->insert($insert);
$bean = $db['Money_Bitcoin_Order'][$uuid];
if (!$bean) return false;

return $bean;
}

public static function sendAmount($address, $amount, $green = null, $inputs = array(), $fee = 0) {
if ($amount instanceof \Internal\Price) $amount = $amount->convert('BTC', null, \Currency::DIRECTION_OUT)->getIntValue();
if ($fee instanceof \Internal\Price) $fee = $fee->convert('BTC', null, \Currency::DIRECTION_OUT)->getIntValue();

$transaction = \DB::i()->transaction();
$lock = \DB::i()->lock('Money_Bitcoin_Available_Output');

$address = \Util\Bitcoin::decode($address);
if (!$address) throw new \Exception('Invalid bitcoin address');
$remainder = \Util\Bitcoin::decode(self::getNullAddr());
if (!$remainder) throw new \Exception('Failed to create output TX');

$input = self::getTxInput($amount+$fee, $inputs);

if (!is_null($green)) {
// green send
// default=d47c1c9afc2a18319e7b78762dc8814727473e90
$tmp_total = 0;
foreach($input as $tmp) $tmp_total += $tmp['amount'];
$key = \DB::DAO('Money_Bitcoin_Permanent_Address')->searchOne(array('Money_Bitcoin_Permanent_Address__' => $green));
if (!$key) throw new \Exception('Invalid green address for transaction');
// intermediate tx
$tx = \Util\Bitcoin::makeNormalTx($input, $tmp_total, array('hash' => $green), array('hash' => $green));
$txid = self::publishTransaction($tx);
\DB::DAO('Money_Bitcoin_Available_Output')->insert(array('Money_Bitcoin_Available_Output__' => \System::uuid(), 'Money_Bitcoin_Permanent_Address__' => $green, 'Value' => $tmp_total, 'Hash' => $txid, 'N' => 0, 'Available' => 'N'));
// final tx
$tx = \Util\Bitcoin::makeNormalTx(array(array('amount' => $tmp_total, 'tx' => $txid, 'N' => 0, 'privkey' => \Internal\Crypt::decrypt($key->Private_Key), 'hash' => $green)), $amount, $address, $remainder);
$txid = self::publishTransaction($tx);
} else {
$tx = \Util\Bitcoin::makeNormalTx($input, $amount, $address, $remainder, $fee);
$txid = self::publishTransaction($tx);
}

if (!$transaction->commit()) return false;

return $txid;

// find a node with enough coins
$node = \DB::DAO('Money_Bitcoin_Host')->searchOne(array('Status' => 'up', new \DB\Expr('`Coins` >= '.\DB::i()->quote($amount))), array(new \DB\Expr('RAND()')));
if (!$node) return false;
$client = \Controller::Driver('Bitcoin', $node->Money_Bitcoin_Host__);
return $client->sendToAddress($address, $amount/100000000);
}

public function getWalletHost() {
throw new \Exception('Method is deprecated');
}

public static function parseVersion($v) {
if ($v == 0) return '[unknown]';
if ($v > 10000) {
// [22:06:18] <ArtForz> new is major * 10000 + minor * 100 + revision
$rem = floor($v / 100);
$proto = $v - ($rem*100);
$v = $rem;
} else {
// [22:06:05] <ArtForz> old was major * 100 + minor
$proto = 0;
}
foreach(array('revision','minor','major') as $type) {
$rem = floor($v / 100);
$$type = $v - ($rem * 100);
$v = $rem;
}
// build string
return $major . '.' . $minor . '.' . $revision . ($proto?('[.'.$proto.']'):'');
}

public static function _Route_getStats($path) {
switch($path) {
case 'version':
$req = 'SELECT `Version`, COUNT(1) AS `Count` FROM `Money_Bitcoin_Node` WHERE `Status` != \'down\' GROUP BY `Version`';
$sqlres = \DB::i()->query($req);
$res = array();
while($row = $sqlres->fetch_assoc()) {
$res[self::parseVersion($row['Version'])] += $row['Count'];
}
break;
case 'ua':
$req = 'SELECT `User_Agent`, COUNT(1) AS `Count` FROM `Money_Bitcoin_Node` WHERE `Status` != \'down\' GROUP BY `User_Agent`';
$sqlres = \DB::i()->query($req);
$res = array();
while($row = $sqlres->fetch_assoc()) {
$res[$row['User_Agent']] += $row['Count'];
}
break;
case 'nodes':
$req = 'SELECT COUNT(1) AS `Count` FROM `Money_Bitcoin_Node` WHERE `Last_Seen` > DATE_SUB(NOW(), INTERVAL 6 HOUR)';
$sqlres = \DB::i()->query($req);
$row = $sqlres->fetch_assoc();
header('Content-Type: text/plain');
echo $row['Count'];
exit;
case 'accepting':
$req = 'SELECT `Status`, COUNT(1) AS `Count` FROM `Money_Bitcoin_Node` WHERE `Last_Seen` > DATE_SUB(NOW(), INTERVAL 6 HOUR) GROUP BY  `Status`';
$sqlres = \DB::i()->query($req);
$res = array();
while($row = $sqlres->fetch_assoc()) {
$res[$row['Status']] = $row['Count'];
}
$res['total_known'] = $res['up'] + $res['down'];
$res['total'] = $res['total_known'] + $res['unknown'];
$res['rate_accepting'] = $res['up'] / $res['total_known'];
break;
case 'bootstrap':
// select a set of peers appropriate as seed
$limit = 50;
if (isset($_GET['limit'])) {
$limit = (int)$_GET['limit'];
if ($limit < 1) $limit = 1;
if ($limit > 10000) $limit = 10000;
}
$req = 'SELECT * FROM `Money_Bitcoin_Node` WHERE `Status` = \'up\' AND `Last_Checked` > DATE_SUB(NOW(), INTERVAL 6 HOUR) AND `Version` >= 31500 AND (`Last_Down` IS NULL OR `Last_Down` < DATE_SUB(NOW(), INTERVAL 2 WEEK)) AND `First_Seen` < DATE_SUB(NOW(), INTERVAL 2 WEEK) ORDER BY RAND() LIMIT '.$limit;
$sqlres = \DB::i()->query($req);
if ($sqlres->num_rows == 0) {
$req = 'SELECT * FROM `Money_Bitcoin_Node` WHERE `Status` = \'up\' AND `Last_Checked` > DATE_SUB(NOW(), INTERVAL 6 HOUR) AND `Version` >= 31500 ORDER BY RAND() LIMIT '.$limit;
$sqlres = \DB::i()->query($req);
}
$res = array();
while($row = $sqlres->fetch_assoc()) {
$res[] = array(
'ipv4' => $row['IP'],
'port' => $row['Port'],
'version' => $row['Version'],
'version_str' => self::parseVersion($row['Version']),
'user_agent' => $row['User_Agent'],
'timestamp' => \DB::i()->dateRead($row['Last_Checked']),
);
}
break;
case 'geomap':
// select all nodes
$req = 'SELECT `IP`, `Status`, `Version` FROM `Money_Bitcoin_Node` WHERE `Last_Seen` > DATE_SUB(NOW(), INTERVAL 3 HOUR)';
$sqlres = \DB::i()->query($req);
header('Content-Type: application/json');
echo '[';
$first = true;
$geoip = \ThirdParty\Geoip::getInstance();
while($row = $sqlres->fetch_assoc()) {
$res = array('ipv4' => $row['IP'], 'version' => $row['Version'], 'status' => $row['Status']);
$record = $geoip->lookup($row['IP'], false);
if (!$record) continue;
if (!isset($record['latitude'])) continue;
$res['latitude'] = $record['latitude'];
$res['longitude'] = $record['longitude'];
if ($first) {
$first = false;
} else {
echo ',';
}
echo json_encode($res);
}
echo ']';
exit;
case 'full':
// select all nodes
$req = 'SELECT * FROM `Money_Bitcoin_Node`';
$sqlres = \DB::i()->query($req);
header('Content-Type: application/json');
echo '[';
$first = true;
while($row = $sqlres->fetch_assoc()) {
if ($first) {
$first = false;
} else {
echo ',';
}
echo json_encode($row);
}
echo ']';
exit;
case 'bitcoin.kml':
header('Content-Type: application/vnd.google-earth.kml+xml');
// check cache
$cache = \Cache::getInstance();
$data = $cache->get('bitcoin.kml_full');
if ($data) {
echo $data;
exit;
}
// select all nodes
$out = fopen('php://temp', 'w');
fwrite($out, "<?xml version=\"1.0\" encoding=\"UTF-8\"?".">\n");
fwrite($out, '<kml xmlns="http://www.opengis.net/kml/2.2" xmlns:gx="http://www.google.com/kml/ext/2.2" xmlns:kml="http://www.opengis.net/kml/2.2" xmlns:atom="http://www.w3.org/2005/Atom">'."\n");
fwrite($out, "<Document>\n<name>Bitcoin nodes in the world</name>\n");
// styles
fwrite($out, "<Style id=\"up\"><IconStyle><Icon><href>http://maps.google.com/mapfiles/kml/paddle/grn-blank.png</href></Icon></IconStyle></Style>\n");
fwrite($out, "<Style id=\"down\"><IconStyle><Icon><href>http://maps.google.com/mapfiles/kml/paddle/red-blank.png</href></Icon></IconStyle></Style>\n");
fwrite($out, "<Style id=\"unknown\"><IconStyle><Icon><href>http://maps.google.com/mapfiles/kml/paddle/wht-blank.png</href></Icon></IconStyle></Style>\n");
$req = 'SELECT `IP`, `Status`, `Version` FROM `Money_Bitcoin_Node` WHERE `Last_Seen` > DATE_SUB(NOW(), INTERVAL 3 HOUR) ORDER BY `Status`';
$geoip = \ThirdParty\Geoip::getInstance();
$folder = '';
$sqlres = \DB::i()->query($req);
while($row = $sqlres->fetch_assoc()) {
// lookup
$record = $geoip->lookup($row['IP'], false);
if (!$record) continue;
if (!isset($record['latitude'])) continue;

if ($folder != $row['Status']) {
if ($folder) fwrite($out, "</Folder>\n");
$folder = $row['Status'];
fwrite($out, "<Folder><name>Bitcoin Nodes in status ".$folder."</name>\n");
}
fwrite($out, "<Placemark><name>".$row['IP']."</name><description><![CDATA[<p>IP: ".$row['IP']."</p><p>Version: ".self::parseVersion($row['Version'])."</p>]]></description><styleUrl>#".$folder."</styleUrl>");
fwrite($out, "<Point><coordinates>".$record['longitude'].",".$record['latitude']."</coordinates></Point></Placemark>\n");
}
fwrite($out, "</Folder>\n</Document>\n</kml>\n");
rewind($out);
$data = stream_get_contents($out);
fclose($out);
$cache->set('bitcoin.kml_full', $data, 1800);
echo $data;
exit;
default:
header('HTTP/1.0 404 Not Found');
die('Not available');
}
header('Content-Type: application/json');
echo json_encode($res);
exit;
}

public static function checkNodes($sched) {
// get nodes to check
$db = \DB::i();
$list = $db['Money_Bitcoin_Node']->search(array(new \DB\Expr('`Next_Check` < NOW()')), array(new \DB\Expr('`Status` IN (\'up\', \'unknown\') DESC'), 'Last_Checked' => 'ASC'), array(701));
if (count($list) == 701) {
$sched->busy();
array_pop($list);
}

$end_time = (floor(time()/60)*60)+50;

$nodes = new Bitcoin\Nodes();
$info = array();
$up = array();

$nodes->on(null, 'ready', function($key) use (&$info, $nodes, $db, &$up) {
$node = $info[$key];
$node->Version = $nodes->getVersion($key);
$node->User_Agent = $nodes->getUserAgent($key);
$node->Status = 'up';
$node->Last_Seen = $db->now();
$node->Last_Checked = $db->now();
$node->Next_Check = $db->dateWrite(time()+(1800));
$node->commit();
$up[$key] = true;
$nodes->getAddr($key); // initiate loading of addrs
});

$nodes->on(null, 'error', function($key, $error) use (&$info, $db, &$up) {
if ($up[$key]) return; // probably getaddr failed
$node = $info[$key];
$node->Status = 'down';
$node->Last_Checked = $db->now();
$node->Next_Check = $db->dateWrite(time()+(3600*24));
$node->Last_Down = $db->now();
$node->Last_Error = $error;
if ($db->dateRead($node->Last_Seen) < (time() - (3600*24))) { // no news for 24 hours, drop it
$node->delete();
return;
}
$node->commit();
});

$nodes->on(null, 'addr', function($key, $addr_list) use (&$info, $nodes, $db) {
$node = $info[$key];
if (count($addr_list) > 1000) {
$node->Addresses = 0;
$node->commit();
return;
}
$node->Addresses = count($addr_list);
$node->commit();
foreach($addr_list as $addr) {
$bean = $db['Money_Bitcoin_Node']->searchOne(array('IP' => $addr['ipv4'], 'Port' => $addr['port']));
if ($bean) {
$bean->Last_Seen = $db->now();
$bean->commit();
continue;
}

$db['Money_Bitcoin_Node']->insert(array(
'IP' => $addr['ipv4'],
'Port' => $addr['port'],
'Next_Check' => $db->now(),
'First_Seen' => $db->now(),
'Last_Seen' => $db->now(),
));
}

$nodes->close($key);
});

foreach($list as $node) {
if ($node->Port < 1024) {
$node->Status = 'down';
$node->Last_Checked = $db->now();
$node->Next_Check = $db->dateWrite(time()+(3600*24));
$node->Last_Down = $db->now();
if ($db->dateRead($node->Last_Seen) < (time() - (3600*24))) { // no news for 24 hours, drop it
$node->delete();
return;
}
$node->Last_Error = 'invalid_port';
$node->commit();
continue;
}
$key = 'node_'.$node->Money_Bitcoin_Node__;
$info[$key] = $node;
if (!$nodes->connect($key, $node->IP, $node->Port)) {
$node->Status = 'down';
$node->Last_Checked = $db->now();
$node->Next_Check = $db->dateWrite(time()+(3600*24));
$node->Last_Down = $db->now();
if ($db->dateRead($node->Last_Seen) < (time() - (3600*24))) { // no news for 24 hours, drop it
$node->delete();
return;
}
$node->Last_Error = 'invalid_address';
$node->commit();
}
}

while($nodes->wait());
}

public static function importBlockClaim($hash, $n, $tx) {
$trx = \DB::DAO('Money_Bitcoin_Block_Tx_Out')->searchOne(array('Hash' => $hash, 'N' => $n));
if (!$trx) throw new \Exception('Claim from unknown trx: '.$hash.':'.$n);
$trx->Claimed = 'Y';
$trx->commit();
\DB::DAO('Money_Bitcoin_Available_Output')->delete(array('Hash' => $hash, 'N' => $n));
return true;
}

public static function parseScriptPubKey($pubkey) {
if (preg_match('/^([0-9a-f]{1,130}) OP_CHECKSIG$/', $pubkey, $matches)) {
return array('hash' => \Util\Bitcoin::decodePubkey($matches[1]), 'pubkey' => $matches[1]);
}
if (preg_match('/^OP_DUP OP_HASH160 ([0-9a-f]{40}) OP_EQUALVERIFY OP_CHECKSIG.*$/', $pubkey, $matches)) {
return array('hash' => array('hash' => $matches[1], 'version' => 0));
}
\Debug::exception(new \Exception('WEIRD scriptPubKey - dropping it: '.$pubkey));
return array('hash' => ['hash' => '0000000000000000000000000000000000000000', 'version' => 0]);
}

public static function importBlock($id) {
$peer = \Controller::Driver('Bitcoin', 'b54f4d35-dd1c-43aa-9096-88e37a83bda3');
$block = $peer->getBlock($id);

$transaction = \DB::i()->transaction();

// insert block
$data = array(
'Money_Bitcoin_Block__' => $block['hash'],
'Parent_Money_Bitcoin_Block__' => $block['prev_block'],
'Depth' => $id,
'Version' => $block['version'],
'Mrkl_Root' => $block['mrkl_root'],
'Time' => \DB::i()->dateWrite($block['time']),
'Bits' => $block['bits'],
'Nonce' => $block['nonce'],
'Size' => $block['size'],
);
\DB::DAO('Money_Bitcoin_Block')->insert($data);

$retry = 0;
while($block['tx']) {
$tx = array_shift($block['tx']);
$tmp = \DB::DAO('Money_Bitcoin_Block_Tx')->search(array('Hash' => $tx['hash']));
if ($tmp) continue; // skip duplicate TXs
$tx['block'] = $id;
$data = array(
'Hash' => $tx['hash'],
'Block' => $block['hash'],
'Version' => $tx['version'],
'Lock_Time' => $tx['lock_time'],
'size' => $tx['size'],
);
\DB::DAO('Money_Bitcoin_Block_Tx')->insert($data);
\DB::DAO('Money_Bitcoin_Tx')->delete(array('Money_Bitcoin_Tx__' => $data['Hash']));
\DB::DAO('Money_Bitcoin_Tx_In')->delete(array('Hash' => $data['Hash']));
\DB::DAO('Money_Bitcoin_Tx_Out')->delete(array('Hash' => $data['Hash']));

$watch = null;
$taint = null;
$taint_c = 0;

try {
foreach($tx['in'] as $n => $in) {
$data = array(
'Hash' => $tx['hash'],
'N' => $n,
'Prev_Out_Hash' => $in['prev_out']['hash'],
'Prev_Out_N' => $in['prev_out']['n'],
);
if ($in['coinbase']) {
$data['CoinBase'] = $in['coinbase'];
} else {
$data['scriptSig'] = $in['scriptSig'];
self::importBlockClaim($in['prev_out']['hash'], $in['prev_out']['n'], $tx);
}
//	 \DB::DAO('Money_Bitcoin_Block_Tx_In')->insert($data);
}
} catch(\Exception $e) {
// retry later
if ($retry++ > 10) throw $e;
$block['tx'][] = $tx;
continue;
}

if (!is_null($taint)) $taint = (int)floor($taint/$taint_c);

foreach($tx['out'] as $n => $out) {
$data = array(
'Hash' => $tx['hash'],
'N' => $n,
'Value' => round($out['value']*100000000),
);
$addr = self::parseScriptPubKey($out['scriptPubKey']);
$data['Addr'] = $addr['hash']['hash'];
\DB::DAO('Money_Bitcoin_Block_Tx_Out')->insert($data);
if (isset(\DB::DAO('Money_Bitcoin_Permanent_Address')[$data['Addr']])) {
$data['Money_Bitcoin_Process_Tx_Out__'] = \System::uuid();
\DB::DAO('Money_Bitcoin_Process_Tx_Out')->insert($data, true);
}
}
}

$transaction->commit();
}

public static function importBlocks($scheduler) {
// determine last imported block
$block = \DB::DAO('Money_Bitcoin_Block')->searchOne(null, array('Depth' => 'DESC'));
if ($block) {
$block_id = $block->Depth + 1;
} else {
$block_id = 0;
}
// read blocks from b54f4d35-dd1c-43aa-9096-88e37a83bda3
$peer = \Controller::Driver('Bitcoin', 'b54f4d35-dd1c-43aa-9096-88e37a83bda3');

$info = $peer->getInfo();
if ($info['errors']) {
// reschedule for in one hour
$scheduler->busy(3600);
throw new \Exception('Can\'t import blocks: '.$info['errors']);
}

$last_block = $peer->getCurrentBlock()-5; // 5 confirmations
if ($last_block < $block_id) {
// nothing new here
//	 self::runAddrTriggers();
return;
}

$deadline = time()+50;
$c = 0;

while($block_id <= $last_block) {
try {
self::importBlock($block_id);
} catch(\Exception $e) {
mail('mark@ookoo.org', 'BLOCK IMPORT ERROR', $e->getMessage()."\n\n".$e);
$scheduler->busy(600);
return;
// empty all!
$db = \DB::i();
$db->query('TRUNCATE `Money_Bitcoin_Block`');
//	 $db->query('TRUNCATE `Money_Bitcoin_Block_Addr`');
$db->query('TRUNCATE `Money_Bitcoin_Block_Tx`');
//	 $db->query('TRUNCATE `Money_Bitcoin_Block_Tx_In`');
$db->query('TRUNCATE `Money_Bitcoin_Block_Tx_Out`');
}
$block_id++;
if ((time() > $deadline) || ($c++>49)) {
$scheduler->busy(0);
break;
}
}

// run addr triggers
//	 self::runAddrTriggers();
}

public static function insertMisingAvailableOutputs($addr) {
// search all unclaimed on this addr
$list = \DB::DAO('Money_Bitcoin_Process_Tx_Out')->search(array('Addr' => $addr, 'Claimed' => 'N'));
foreach($list as $bean) {
$insert = array(
'Money_Bitcoin_Available_Output__' => \System::uuid(),
'Money_Bitcoin_Permanent_Address__' => $bean->Addr,
'Value' => $bean->Value,
'Hash' => $bean->Hash,
'N' => $bean->N,
);
\DB::DAO('Money_Bitcoin_Available_Output')->insert($insert, true);
}
}

public static function runAddrTriggers() {
// lookup tx out with Trigger = new
$list = \DB::DAO('Money_Bitcoin_Process_Tx_Out')->search(array('Trigger' => 'new'), null, array(500)); // limit to 500
//	 $main_transaction = \DB::i()->transaction();

foreach($list as $bean) {
$transaction = \DB::i()->transaction();
$bean->reloadForUpdate();
if ($bean->Trigger != 'new') {
// rollback, exit
unset($transaction);
continue;
}
$bean->Trigger = 'executed';
$bean->commit();

$tx = $bean->Hash.':'.$bean->N;

$addr_str = \Util\Bitcoin::encode(array('version' => 0, 'hash' => $bean->Addr));
$wallet_info = \DB::DAO('Money_Bitcoin_Permanent_Address')->searchOne(array('Money_Bitcoin_Permanent_Address__' => $bean->Addr));

$redirect_value = null;
if ($wallet_info) $redirect_value = $wallet_info->Redirect;

$base_tx_data = \DB::DAO('Money_Bitcoin_Block_Tx')->searchOne(array('Hash' => $bean->Hash));
$base_block_data = \DB::DAO('Money_Bitcoin_Block')->searchOne(['Money_Bitcoin_Block__' => $base_tx_data->Block]);

if (($wallet_info) && (!is_null($wallet_info->Private_Key)) && ($redirect_value == 'none')) {
$insert = array(
'Money_Bitcoin_Available_Output__' => \System::uuid(),
'Money_Bitcoin_Permanent_Address__' => $bean->Addr,
'Value' => $bean->Value,
'Hash' => $bean->Hash,
'N' => $bean->N,
'Block' => $base_block_data->Depth,
);
\DB::DAO('Money_Bitcoin_Available_Output')->insert($insert, true);
}

if ($redirect_value == 'fixed') {
// redirect funds
$target = $wallet_info->Redirect_Value;
$pub = \Util\Bitcoin::decode($target);
$tx = \Util\Bitcoin::makeNormalTx(array(array('amount' => $bean->Value, 'tx' => $bean->Hash, 'N' => $bean->N, 'privkey' => \Internal\Crypt::decrypt($wallet_info->Private_Key), 'hash' => $bean->Addr)), $bean->Value, $pub, $pub);
self::publishTransaction($tx);
$transaction->commit();
continue;
}

if (($wallet_info) && (!is_null($wallet_info->Callback))) {
try {
$cb = explode('::', str_replace('/', '\\', $wallet_info->Callback));
call_user_func($cb, $wallet_info, $tx, $base_tx_data->Block, \Internal\Price::spawnInt($bean->Value,'BTC'));
} catch(\Exception $e) {
\Debug::exception($e);
unset($transaction);
continue;
}
}

if (($wallet_info) && (!is_null($wallet_info->Ipn))) {
$base_tx_data = \DB::DAO('Money_Bitcoin_Block_Tx')->searchOne(array('Hash' => $bean->Hash));
$post = array(
'description' => $wallet_info->Description,
'tx' => $tx,
'block' => $base_tx_data->Block,
'status' => 'confirmed',
'amount_int' => $bean->Value,
'item' => 'BTC',
'addr' => \Util\Bitcoin::encode(array('version' => 0, 'hash' => $wallet_info->Money_Bitcoin_Permanent_Address__)),
);
\Scheduler::oneshotUrl($wallet_info->Ipn, $post, null, null, null, $wallet_info->User_Rest__);
}

if (($wallet_info) && (!is_null($wallet_info->User_Wallet__))) {
$wallet_info->Used = 'Y';
$wallet_info->commit();
$wallet = \User\Wallet::byId($wallet_info->User_Wallet__);
if (($wallet) && ($wallet['Currency__'] == 'BTC')) {
// WALLET REDIRECT CODE 1
if ((!is_null($wallet_info->Private_Key)) && ($wallet_info->Redirect == 'wallet') && ($bean->Value > 100000)) {
// redirect funds
$target = self::getVerboseAddr($wallet, $wallet_info->Description);
$pub = \Util\Bitcoin::decode($target);
try {
$tx = \Util\Bitcoin::makeNormalTx(array(array('amount' => $bean->Value, 'tx' => $bean->Hash, 'N' => $bean->N, 'privkey' => \Internal\Crypt::decrypt($wallet_info->Private_Key), 'hash' => $bean->Addr)), $bean->Value, $pub, $pub);
} catch(\Exception $e) {
mail('mark@tibanne.com', 'FAILED TO GENERATE REDIRECT TX', 'Error '.$e->getMessage().' on: '.$wallet_info->Money_Bitcoin_Permanent_Address__."\n".print_r($bean->getProperties(), true));
throw $e;
}
self::publishTransaction($tx);
$transaction->commit();
continue;
}
// search for already add
$nfo = \DB::DAO('User_Wallet_History')->searchOne(array('Reference_Type' => 'Money_Bitcoin_Block_Tx_Out', 'Reference' => $tx));
if (!$nfo) {
$wallet->deposit(\Internal\Price::spawnInt($bean->Value, 'BTC'), $addr_str.(is_null($wallet_info->Description)?'':"\n".$wallet_info->Description), 'deposit', 'Money_Bitcoin_Block_Tx_Out', $tx);
if ($wallet['Balance']['value'] > 10000) $wallet->getUser()->aml('Balance in bitcoin is over 10000', 2); // force AML
\Money\Trade::updateUserOrders($wallet->getUser());
}
}
}

$transaction->commit();
}

//	 $main_transaction->commit();

return count($list);
}

public static function getAddressBalance($addr) {
$res = \Internal\Price::spawn(0,'BTC');
$list = \DB::DAO('Money_Bitcoin_Block_Tx_Out')->search(['Addr'=>$addr['hash']]);
foreach($list as $bean)
$res->add(\Internal\Price::spawnInt($bean->Value, 'BTC'));
return $res;
}

public static function getAddressOutputs($addr) {
// get all unclaimed outputs for that addr
$list = \DB::DAO('Money_Bitcoin_Block_Tx_Out')->search(array('Addr' => $addr['hash'], 'Claimed' => 'N'));
$final = array();
foreach($list as $bean) $final[] = $bean->getProperties();
return $final;
}

public static function claimPrivateSha256($wallet, $priv, $desc = null) {
return self::claimPrivate($wallet, \Util\Bitcoin::hash_sha256($priv), $desc);
}

public static function claimWalletFile($wallet, $data, $desc = null) {
$keys = \Util\Bitcoin::scanWalletFile($data);
if (!$keys) return array();

$res = array();

foreach($keys as $key) {
$tmp = self::claimPrivate($wallet, $key, $desc);
if (!$tmp) continue;
$res[] = $tmp;
}
return $res;
}

public static function claimPrivate($wallet, $priv, $desc = null) {
// get all the funds sent to that private addr and record it for future deposits
if (strlen($priv) != 32) throw new \Exception('The private key must be 32 bytes');

// check if privkey is within range
$pk_num = gmp_init(bin2hex($priv), 16);
if (gmp_cmp($pk_num, '0') <= 0) return false;
if (gmp_cmp($pk_num, gmp_init('FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEBAAEDCE6AF48A03BBFD25E8CD0364141', 16)) >= 0) return false;

$pub = \Util\Bitcoin::decodePrivkey($priv);
$addr = \Util\Bitcoin::encode($pub);
$outs = \Money\Bitcoin::getAddressOutputs($pub);

$find = \DB::DAO('Money_Bitcoin_Permanent_Address')->searchOne(array('Money_Bitcoin_Permanent_Address__' => $pub['hash']));
if ($find) {
if (!is_null($find->Private_Key)) return false; // already got this one
$find->Private_Key = \Internal\Crypt::encrypt($priv);
$find->Redirect = 'wallet';
$find->Used = 'Y';
$find->commit();
$wallet = \User\Wallet::byId($find->User_Wallet__);
} else {
$insert = array(
'Money_Bitcoin_Permanent_Address__' => $pub['hash'],
'Money_Bitcoin_Host__' => null,
'Private_Key' => \Internal\Crypt::encrypt($priv),
'Description' => $desc,
'Redirect' => 'nulladdr',
'Used' => 'Y',
);
if (!is_null($wallet)) {
$insert['User_Wallet__'] = $wallet->getId();
$insert['Redirect'] = 'wallet';
}
\DB::DAO('Money_Bitcoin_Permanent_Address')->insert($insert);
}

$total = 0;
if ($outs) {
if (is_null($wallet)) {
$out = self::getNullAddr();
} else {
$out = self::getVerboseAddr($wallet, $desc);
}
$outpub = \Util\Bitcoin::decode($out);
$input = array();
foreach($outs as $t) {
$input[] = array('amount' => $t['Value'], 'tx' => $t['Hash'], 'N' => $t['N'], 'privkey' => $priv, 'hash' => $pub['hash']);
$total += $t['Value'];
}

$tx = \Util\Bitcoin::makeNormalTx($input, $total, $outpub, $outpub);
self::publishTransaction($tx);
}
return array('amount' => \Internal\Price::spawnInt($total, 'BTC'), 'address' => $addr);
}

public static function makeNormalTx($input, $amount, $final_output, $remainder, $fee = 0) {
// make a normal tx, merge inputs if preferable
$res = array();
while(count($input) > 5) {
// merge some inputs
$xinput = array();
$output = self::getNullAddr(true);

// merge as many inputs as we can in a single tx
while(true) {
$extra = array_shift($input);
if (is_null($extra)) break;
$tinput = $xinput;
$tinput[] = $extra;
$total = 0;
foreach($tinput as $t) $total+=$t['amount'];
$ttx = \Util\Bitcoin::makeNormalTx($tinput, $total, $output['info'], $output['info']);
if (strlen($ttx) >= 1000) break;
$xinput[] = $extra;
}
if (!is_null($extra))
array_unshift($input, $extra);
$total = 0;
foreach($xinput as $t) $total += $t['amount'];
$ttx = \Util\Bitcoin::makeNormalTx($xinput, $total, $output['info'], $output['info']);
$res[] = $ttx;
$thash = bin2hex(strrev(\Util\Bitcoin::hash_sha256(\Util\Bitcoin::hash_sha256($ttx))));
$input[] = array(
'amount' => $total,
'tx' => $thash,
'N' => 0,
'privkey' => $output['priv'],
'hash' => $output['info']['hash'],
);
\DB::DAO('Money_Bitcoin_Available_Output')->insert(array('Money_Bitcoin_Available_Output__' => \System::uuid(), 'Money_Bitcoin_Permanent_Address__' => $output['info']['hash'], 'Value' => $total, 'Hash' => $thash, 'N' => 0, 'Available' => 'N'));
}
// do the final tx
$res[] = \Util\Bitcoin::makeNormalTx($input, $amount, $final_output, $remainder, $fee);
return $res;
}

public static function publishTransaction($txs) {
// generate tx id
if (!is_array($txs)) $txs = array($txs);
foreach($txs as $tx) {
$txid = bin2hex(strrev(\Util\Bitcoin::hash_sha256(\Util\Bitcoin::hash_sha256($tx))));
$insert = array(
'Hash' => $txid,
'Blob' => base64_encode($tx),
'Created' => \DB::i()->now(),
);
\DB::DAO('Money_Bitcoin_Pending_Tx')->insert($insert);
self::$pending[$txid] = $tx;
}
return $txid;
}

public static function broadcastPublished() {
if (!self::$pending) return;
\Controller::MQ('RabbitMQ')->invoke('Money/Bitcoin::broadcastPublished', ['txs' => self::$pending]);
self::$pending = [];
}

public static function _MQ_broadcastPublished($info) {
$list = $info['txs'];
$node = new \Money\Bitcoin\Node(self::BITCOIN_NODE);
foreach($list as $tx) {
$node->pushTx($tx);
}
$node->getAddr(); // force sync
}

public static function broadcastTransactions() {
$list = \DB::DAO('Money_Bitcoin_Pending_Tx')->search(array(new \DB\Expr('`Last_Broadcast` < DATE_SUB(NOW(), INTERVAL 30 MINUTE)')), ['Last_Broadcast' => 'ASC'], array(100));
if (!$list) return;

//	 $ip = gethostbyname('relay.eligius.st');
$ip = gethostbyname('mtgox.relay.eligius.st');
$node = new \Money\Bitcoin\Node(self::BITCOIN_NODE);
$peer = \Controller::Driver('Bitcoin', 'b54f4d35-dd1c-43aa-9096-88e37a83bda3');
$el_todo = array();

foreach($list as $bean) {
// check if successful
$success = \DB::DAO('Money_Bitcoin_Block_Tx')->searchOne(array('Hash' => $bean->Hash));
if ($success) {
$bean->delete();
continue;
}
$bean->Last_Broadcast = \DB::i()->now();
if ((\DB::i()->dateRead($bean->Created) < (time()-7000)) && ($bean->Eligius == 'N')) {
try {
if (!$el_node) $el_node = new \Money\Bitcoin\Node($ip);
$el_node->pushTx(base64_decode($bean->Blob));
$bean->Eligius = 'P';
} catch(\Exception $e) {
// too bad
}
} elseif ($bean->Eligius == 'P') {
$bean->Eligius = 'Y';
$el_todo[] = $bean->Hash;
}
try {
$bean->Last_Result = $peer->sendRawTransaction(bin2hex(base64_decode($bean->Blob)));
} catch(\Exception $e) {
$bean->Last_Result = $e->getMessage();
}
$bean->commit();
$node->pushTx(base64_decode($bean->Blob));
}

$node->getAddr(); // force sync reply from bitcoin daemon so we know the stuff went through
if ($el_node) $el_node->getAddr();
if ($el_todo) {
$ssh = new \Network\SSH($ip);
if (!$ssh->authKeyUuid('freetxn', '14a70b11-5f36-4890-82ca-5de820882c7f')) {
mail('mark@tibanne.com,luke+eligius@dashjr.org', 'SSH connection to freetxn@'.$ip.' failed', 'Used ssh key 14a70b11-5f36-4890-82ca-5de820882c7f, but couldn\'t login to push those txs:'."\n".implode("\n", $el_todo));
return; // failed
}
foreach($el_todo as $tx) {
$channel = $ssh->channel();
$channel->exec($tx);
$channel->wait();
}
}
}

/**
* Returns the total amount of bitcoins in the world based on that last block generated
*
* @return int The total amount of bitcoins
*/
public static function getTotalCount() {
// get total count of BTC in the world based on latest block #
$last_block = \DB::DAO('Money_Bitcoin_Block')->searchOne(null, ['Depth'=>'DESC']);
$current = $last_block->Depth;

// this is a chunk of blocks, bitcoins generated per chunk start at 50 and halve every chunks
$block_size = 210000;

// first compute the total amount of bitcoins for the chunks that are fully done
$full_block_count = floor($current / $block_size);
$full_block_coeff = (1 - pow(0.5, $full_block_count)) * 100;

// those are the bitcoins on the full block chunks
$total_bitcoins = $full_block_coeff * $block_size;

// then for the last chunk
$last_block_coeff = pow(0.5, $full_block_count + 1) * 100;
$total_bitcoins += $last_block_coeff * ($current - ($full_block_count * $block_size));

return $total_bitcoins;
}

public static function _Route_bitcoind($path) {
$post = file_get_contents('php://input');
$post = json_decode($post, true);
if (!$post) return;
$method = $post['method'];
$params = $post['params'];
$id = $post['id']?:\System::uuid();
try {
throw new \Exception('Meh: '.$method);
die(json_encode(array('result' => $res, 'id' => $id)));
} catch(\Exception $e) {
die(json_encode(array('error' => $e->getMessage(), 'id' => $id)));
}
}

public static function _Route_handleTx() {
// posted by halfnode with a TX
$tx_bin = pack('H*', $_POST['tx']);
$tx = \Util\Bitcoin::parseTx($tx_bin);
if (!$tx) die('BAD TX');
$hash = $tx['hash'];
$dao = \DB::DAO('Money_Bitcoin_Tx');
if (isset($dao[$hash])) die('DUP');
if (\DB::DAO('Money_Bitcoin_Block_Tx')->countByField(array('Hash' => $hash))) die('DUP(blockchain)');

$insert = array(
'Money_Bitcoin_Tx__' => $hash,
'Data' => base64_encode($tx_bin),
'Size' => strlen($tx_bin),
);
$dao->insert($insert);

foreach($tx['in'] as $i => $txin) {
\DB::DAO('Money_Bitcoin_Tx_In')->insert(array(
'Hash' => $hash,
'N' => $i,
'Prev_Out_Hash' => $txin['prev_out']['hash'],
'Prev_Out_N' => $txin['prev_out']['n'],
'scriptSig' => $txin['scriptSig'],
'Addr' => $txin['addr'],
));
}

foreach($tx['out'] as $i => $txout) {
\DB::DAO('Money_Bitcoin_Tx_Out')->insert(array(
'Hash' => $hash,
'N' => $i,
'Value' => $txout['value_int'],
'scriptPubKey' => $txout['scriptPubKey'],
'Addr' => $txout['addr'],
));

// check if one of our addrs
$info = \DB::DAO('Money_Bitcoin_Permanent_Address')->searchOne(array('Money_Bitcoin_Permanent_Address__' => $txout['addr']));
if (($info) && (!is_null($info->Callback))) {
$cb = explode('::', str_replace('/', '\\', $info->Callback));
call_user_func($cb, $info, $hash.':'.$i, null, \Internal\Price::spawnInt($txout['value_int'],'BTC'));
}
if (($info) && (!is_null($info->Ipn))) {
$post = array(
'description' => $info->Description,
'tx' => $hash.':'.$i,
'status' => 'published',
'amount_int' => $txout['value_int'],
'item' => 'BTC',
'addr' => \Util\Bitcoin::encode(array('version' => 0, 'hash' => $info->Money_Bitcoin_Permanent_Address__)),
);
\Scheduler::oneshotUrl($info->Ipn, $post, null, null, null, $info->User_Rest__);
}

// REDIRECT CODE 2
if (($info) && (!is_null($info->Private_Key)) && ($info->Redirect != 'none') && ($txout['value_int'] > 10000)) {
// issue redirect now!
switch($info->Redirect) {
case 'wallet':
$wallet = \User\Wallet::byId($info->User_Wallet__);
$target = self::getVerboseAddr($wallet, $info->Description);
break;
case 'fixed':
$target = $info->Redirect_Value;
break;
case 'nulladdr':
$target = self::getNullAddr();
break;
}
$pub = \Util\Bitcoin::decode($target);
$tx = \Util\Bitcoin::makeNormalTx(array(array('amount' => $txout['value_int'], 'tx' => $hash, 'N' => $i, 'privkey' => \Internal\Crypt::decrypt($info->Private_Key), 'hash' => $txout['addr'])), $txout['value_int'], $pub, $pub);
self::publishTransaction($tx);
//	 self::broadcastPublished();
}
}
die('OK');
}

public static function getTablesStruct() {
return array(
'Money_Bitcoin_Host' => array(
'Money_Bitcoin_Host__' => 'UUID',
'Name' => array('type' => 'VARCHAR', 'size' => 16, 'null' => false),
'IP' => array('type' => 'VARCHAR', 'size' => 39, 'null' => false, 'key' => 'UNIQUE:IP'),
'Address' => array('type' => 'VARCHAR', 'size' => 35, 'null' => true),
'Version' => array('type' => 'INT', 'size' => 10, 'unsigned' => true),
'Coins' => array('type' => 'BIGINT', 'size' => 20, 'unsigned' => true), /* stored in smallest unit of coin */
'Connections' => array('type' => 'INT', 'size' => 10, 'unsigned' => true),
'Blocks' => array('type' => 'BIGINT', 'size' => 20, 'unsigned' => true),
'Hashes_Per_Sec' => array('type' => 'BIGINT', 'size' => 20, 'unsigned' => true),
'Status' => array('type' => 'ENUM', 'values' => array('up','down'), 'default' => 'down'),
'Last_Update' => array('type' => 'DATETIME', 'null' => true),
'Keep_Empty' => array('type' => 'ENUM', 'values' => array('Y','N','E'), 'default' => 'N'), /* if set, any money on there will be sent somewhere else. E=exclude */
'Allow_Order' => array('type' => 'ENUM', 'values' => array('Y','N'), 'default' => 'Y'), /* should we use this node for incoming payments? */
'Generate' => array('type' => 'ENUM', 'values' => array('Y','N'), 'default' => 'Y'),
'Stamp' => array('type' => 'TIMESTAMP', 'null' => false),
),
'Money_Bitcoin_Tx' => array(
'Money_Bitcoin_Tx__' => array('type' => 'CHAR', 'size' => 64, 'null' => false, 'key' => 'PRIMARY'),
'Data' => array('type' => 'LONGTEXT', 'null' => false),
'Network' => array('type' => 'VARCHAR', 'size' => 32, 'default' => 'bitcoin', 'key' => 'Network'),
'Size' => array('type' => 'INT', 'unsigned' => true, 'size' => 10, 'null' => false),
'Stamp' => array('type' => 'TIMESTAMP', 'null' => false),
),
'Money_Bitcoin_Tx_In' => array(
'Hash' => array('type' => 'CHAR', 'size' => 64, 'null' => false, 'key' => 'UNIQUE:Key'),
'N' => array('type' => 'INT', 'size' => 10, 'unsigned' => true, 'key' => 'UNIQUE:Key'),
'Prev_Out_Hash' => array('type' => 'CHAR', 'size' => 64, 'null' => false),
'Prev_Out_N' => array('type' => 'INT', 'size' => 10, 'unsigned' => true),
'CoinBase' => array('type' => 'TEXT', 'null' => true),
'scriptSig' => array('type' => 'TEXT', 'null' => true),
'Addr' => array('type' => 'CHAR', 'size' => 40, 'null' => true, 'key' => 'Addr'),
'_keys' => array(
'Prev_Out' => array('Prev_Out_Hash','Prev_Out_N'),
),
),
'Money_Bitcoin_Tx_Out' => array(
'Hash' => array('type' => 'CHAR', 'size' => 64, 'null' => false, 'key' => 'UNIQUE:Key'),
'N' => array('type' => 'INT', 'size' => 10, 'unsigned' => true, 'key' => 'UNIQUE:Key'),
'Value' => array('type' => 'BIGINT', 'size' => 20, 'unsigned' => true, 'null' => false),
'scriptPubKey' => array('type' => 'TEXT'),
'Addr' => array('type' => 'CHAR', 'size' => 40, 'null' => true, 'key' => 'Addr'),
),
'Money_Bitcoin_Permanent_Address' => array(
'Money_Bitcoin_Permanent_Address__' => array('type' => 'CHAR', 'size' => 40, 'key' => 'PRIMARY'),
'Money_Bitcoin_Host__' => 'UUID/N',
'User_Wallet__' => 'UUID/N',
'User_Rest__' => 'UUID/N',
'Money_Merchant_Transaction_Payment__' => 'UUID/N',
'Private_Key' => array('type' => 'VARCHAR', 'size' => 255, 'null' => true),
'Redirect' => array('type' => 'ENUM', 'values' => array('wallet','fixed','nulladdr','none'), 'default' => 'none'), // wallet => redirect to new addr on same wallet
'Redirect_Value' => array('type' => 'VARCHAR', 'size' => 35, 'null' => true),
'Description' => array('type' => 'VARCHAR', 'size' => 255, 'null' => true),
'Ipn' => array('type' => 'VARCHAR', 'size' => 255, 'null' => true),
'Callback' => array('type' => 'VARCHAR', 'size' => 255, 'null' => true),
'Used' => array('type' => 'ENUM', 'values' => array('Y','N'), 'default' => 'N'),
'Created' => array('type' => 'DATETIME', 'null' => false),
'Stamp' => array('type' => 'TIMESTAMP', 'null' => false),
'_keys' => array(
'Unused_Addr_Key' => array('User_Wallet__','Used'),
'User_Wallet__' => ['User_Wallet__'],
),
),
'Money_Bitcoin_Available_Output' => array( // list available funds
'Money_Bitcoin_Available_Output__' => 'UUID',
'Money_Bitcoin_Permanent_Address__' => array('type' => 'CHAR', 'size' => 40, 'key' => 'Money_Bitcoin_Permanent_Address__'),
'Network' => array('type' => 'VARCHAR', 'size' => 32, 'default' => 'bitcoin', 'key' => 'Network'),
'Value' => array('type' => 'BIGINT', 'size' => 20, 'unsigned' => true, 'null' => false),
'Hash' => array('type' => 'CHAR', 'size' => 64, 'null' => false, 'key' => 'UNIQUE:Key'),
'N' => array('type' => 'INT', 'size' => 10, 'unsigned' => true, 'key' => 'UNIQUE:Key'),
'Block' => array('type' => 'INT', 'size' => 10, 'unsigned' => true, 'key' => 'Block'),
'Available' => array('type' => 'ENUM', 'values' => array('Y','N'), 'default' => 'Y'),
'Stamp' => array('type' => 'TIMESTAMP', 'null' => false),
),
'Money_Bitcoin_Order' => array(
'Money_Bitcoin_Order__' => 'UUID',
'Order__' => 'UUID',
'Money_Bitcoin_Host__' => 'UUID',
'Address' => array('type' => 'VARCHAR', 'size' => 35, 'null' => true), /* generated only for this order */
'Coins' => array('type' => 'BIGINT', 'size' => 20, 'unsigned' => true),
'Coins_NC' => array('type' => 'BIGINT', 'size' => 20, 'unsigned' => true),
'Coins_Extra' => array('type' => 'BIGINT', 'size' => 20, 'unsigned' => true),
'Total' => array('type' => 'BIGINT', 'size' => 20, 'unsigned' => true),
'Created' => array('type' => 'DATETIME', 'null' => false),
'Expires' => array('type' => 'DATETIME', 'null' => false),
'Status' => array('type' => 'ENUM', 'values' => array('pending','expired','ok'), 'default' => 'pending'),
'Stamp' => array('type' => 'TIMESTAMP', 'null' => false),
'_keys' => array(
'@Order__' => array('Order__'),
'@Address' => array('Address'),
),
),
'Money_Bitcoin_Wallet' => array(
'Money_Bitcoin_Wallet__' => 'UUID',
'User__' => 'UUID',
'Money_Bitcoin_Host__' => 'UUID',
'Address' => array('type' => 'VARCHAR', 'size' => 35, 'null' => true),
'Coins' => array('type' => 'BIGINT', 'size' => 20, 'unsigned' => true),
'Coins_NC' => array('type' => 'BIGINT', 'size' => 20, 'unsigned' => true),
'Withdrawn_Coins' => array('type' => 'BIGINT', 'size' => 21, 'unsigned' => false),
'Refresh' => array('type' => 'DATETIME', 'null' => false),
'Stamp' => array('type' => 'TIMESTAMP', 'null' => false),
'_keys' => array(
'@User__' => array('User__'),
'@Address' => array('Address'),
),
),
'Money_Bitcoin_Node' => array(
'Money_Bitcoin_Node__' => NULL,
'IP' => array('type' => 'VARCHAR', 'size' => 15, 'null' => false, 'key' => 'UNIQUE:Unique_Host'),
'Port' => array('type' => 'INT', 'size' => 5, 'unsigned' => true, 'key' => 'UNIQUE:Unique_Host'),
'Version' => array('type' => 'INT', 'unsigned' => true, 'size' => 10),
'User_Agent' => array('type' => 'VARCHAR', 'size' => 256, 'null' => true),
'Status' => array('type' => 'ENUM', 'values' => array('up','down','unknown'), 'default' => 'unknown'),
'Addresses' => array('type' => 'INT', 'unsigned' => true, 'size' => 10, 'default' => 0),
'Last_Checked' => array('type' => 'DATETIME'),
'Next_Check' => array('type' => 'DATETIME'),
'First_Seen' => array('type' => 'DATETIME'),
'Last_Seen' => array('type' => 'DATETIME'),
'Last_Down' => array('type' => 'DATETIME', 'null' => true, 'default' => NULL),
'Last_Error' => array('type' => 'VARCHAR', 'size' => 32, 'null' => true),
'Stamp' => array('type' => 'TIMESTAMP', 'null' => false),
'_keys' => [
'Next_Check' => ['Next_Check'],
'Status' => ['Status'],
],
),
'Money_Bitcoin_Pending_Tx' => array(
'Hash' => array('type' => 'CHAR', 'size' => 64, 'null' => false, 'key' => 'PRIMARY'),
'Network' => array('type' => 'VARCHAR', 'size' => 32, 'default' => 'bitcoin', 'key' => 'Network'),
'Blob' => array('type' => 'LONGTEXT'),
'Eligius' => array('type' => 'ENUM', 'values' => array('Y','P','N'), 'default' => 'N'),
'Created' => array('type' => 'DATETIME'),
'Input_Total' => ['type' => 'BIGINT', 'size' => 20, 'unsigned' => true, 'null' => true],
'Last_Broadcast' => array('type' => 'DATETIME'),
'Last_Result' => ['type' => 'VARCHAR', 'size' => 128, 'null' => true],
'Stamp' => array('type' => 'TIMESTAMP', 'null' => false),
),
'Money_Bitcoin_Block' => array(
'Money_Bitcoin_Block__' => array('type' => 'CHAR', 'size' => 64, 'null' => false, 'key' => 'PRIMARY'),
'Parent_Money_Bitcoin_Block__' => array('type' => 'CHAR', 'size' => 64, 'null' => false, 'key' => 'Parent_Money_Bitcoin_Block__'),
'Depth' => array('type' => 'BIGINT', 'size' => 20, 'null' => false, 'key' => 'Depth'),
'Network' => array('type' => 'VARCHAR', 'size' => 32, 'default' => 'bitcoin', 'key' => 'Network'),
'Version' => array('type' => 'INT', 'size' => 10, 'unsigned' => true),
'Mrkl_Root' => array('type' => 'CHAR', 'size' => 64, 'null' => false),
'Time' => array('type' => 'DATETIME'),
'Bits' => array('type' => 'INT', 'size' => 10, 'unsigned' => true),
'Nonce' => array('type' => 'INT', 'size' => 10, 'unsigned' => true),
'Size' => array('type' => 'INT', 'size' => 10, 'unsigned' => true),
'Status' => array('type' => 'ENUM', 'values' => array('pending','confirmed','dropped'), 'default' => 'confirmed', 'null' => false),
),
'Money_Bitcoin_Process_Tx_Out' => [
'Money_Bitcoin_Process_Tx_Out__' => 'UUID',
'Hash' => array('type' => 'CHAR', 'size' => 64, 'null' => false, 'key' => 'UNIQUE:Key'),
'N' => array('type' => 'INT', 'size' => 10, 'unsigned' => true, 'key' => 'UNIQUE:Key'),
'Value' => array('type' => 'BIGINT', 'size' => 20, 'unsigned' => true, 'null' => false),
'scriptPubKey' => array('type' => 'TEXT'),
'Addr' => array('type' => 'CHAR', 'size' => 40, 'null' => false, 'key' => 'Addr'),
'Trigger' => array('type' => 'ENUM', 'values' => array('new','executed','nil'), 'default' => 'new'),
'_keys' => array(
'Trigger' => array('Trigger'),
),
],
'Money_Bitcoin_Block_Tx' => array(
'Hash' => array('type' => 'CHAR', 'size' => 64, 'null' => false, 'key' => 'UNIQUE:Hash'),
'Block' => array('type' => 'CHAR', 'size' => 64, 'null' => false, 'key' => 'Block'),
'Version' => array('type' => 'INT', 'size' => 10, 'unsigned' => true),
'Lock_Time' => array('type' => 'INT', 'size' => 10, 'unsigned' => true),
'Size' => array('type' => 'INT', 'size' => 10, 'unsigned' => true),
),
/*	 'Money_Bitcoin_Block_Tx_In' => array(
'Hash' => array('type' => 'CHAR', 'size' => 64, 'null' => false, 'key' => 'UNIQUE:Key'),
'N' => array('type' => 'INT', 'size' => 10, 'unsigned' => true, 'key' => 'UNIQUE:Key'),
'Prev_Out_Hash' => array('type' => 'CHAR', 'size' => 64, 'null' => false),
'Prev_Out_N' => array('type' => 'INT', 'size' => 10, 'unsigned' => true),
'CoinBase' => array('type' => 'TEXT', 'null' => true),
'scriptSig' => array('type' => 'TEXT', 'null' => true),
'Addr' => array('type' => 'CHAR', 'size' => 40, 'null' => true, 'key' => 'Addr'),
'_keys' => array(
'Prev_Out' => array('Prev_Out_Hash','Prev_Out_N'),
),
),*/
'Money_Bitcoin_Block_Tx_Out' => array(
'Hash' => array('type' => 'CHAR', 'size' => 64, 'null' => false, 'key' => 'UNIQUE:Key'),
'N' => array('type' => 'INT', 'size' => 10, 'unsigned' => true, 'key' => 'UNIQUE:Key'),
'Value' => array('type' => 'BIGINT', 'size' => 20, 'unsigned' => true, 'null' => false),
'scriptPubKey' => array('type' => 'TEXT'),
'Addr' => array('type' => 'CHAR', 'size' => 40, 'null' => false, 'key' => 'Addr'),
'Claimed' => array('type' => 'ENUM', 'values' => array('Y','N'), 'default' => 'N'),
'Trigger' => array('type' => 'ENUM', 'values' => array('new','executed','nil'), 'default' => 'new'),
'_keys' => array(
'Trigger' => array('Trigger'),
),
),
/*	 'Money_Bitcoin_Block_Addr' => array(
'Addr' => array('type' => 'CHAR', 'size' => 40, 'null' => false, 'key' => 'PRIMARY'),
'Network' => array('type' => 'VARCHAR', 'size' => 32, 'default' => 'bitcoin', 'key' => 'Network'),
'Pubkey' => array('type' => 'CHAR', 'size' => 130, 'null' => true),
'Balance' => array('type' => 'BIGINT', 'size' => 20, 'unsigned' => true, 'null' => false),
'Watch' => array('type' => 'VARCHAR', 'size' => 128, 'null' => true, 'default' => NULL, 'key' => 'Watch'),
'Taint' => array('type' => 'BIGINT', 'size' => 20, 'unsigned' => true, 'null' => true, 'default' => NULL),
'Clean' => array('type' => 'VARCHAR', 'size' => 64, 'null' => true, 'default' => NULL, 'key' => 'Clean'),
), */
'Money_Bitcoin_Vanity' => array(
'Money_Bitcoin_Vanity__' => array('type' => 'VARCHAR', 'size' => 35, 'null' => false, 'key' => 'PRIMARY'),
'Private_Key' => array('type' => 'VARCHAR', 'size' => 255, 'null' => false),
),
);
}
}

\Scheduler::schedule('MoneyBitcoinUpdate', '10min', 'Money/Bitcoin::update');
\Scheduler::schedule('MoneyBitcoinCheckOrders', '5min', 'Money/Bitcoin::checkOrders');
\Scheduler::schedule('MoneyBitcoinGetRate', array('daily', '5i'), 'Money/Bitcoin::getRate');
\Scheduler::schedule('MoneyBitcoinCheckNodes', '10min', 'Money/Bitcoin::checkNodes');
\Scheduler::schedule('MoneyBitcoinImportBlocks', '1min', 'Money/Bitcoin::importBlocks');
\Scheduler::schedule('MoneyBitcoinAddrTriggers', '1min', 'Money/Bitcoin::runAddrTriggers');
\Scheduler::schedule('MoneyBitcoinBroadcastTxs', '1min', 'Money/Bitcoin::broadcastTransactions');
\Scheduler::schedule('MoneyBitcoinMergeSmallOutputs', '10min', 'Money/Bitcoin::mergeSmallOutputs');
\Scheduler::schedule('MoneyBitcoinSplitBigOutputs', '10min', 'Money/Bitcoin::splitBigOutputs');
\DB::i()->validateStruct(Bitcoin::getTablesStruct());

?>
