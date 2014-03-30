/*
 * Copyright (C) 2013, Claus Nielsen, cn@cn-consult.dk
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package com.icoin.trading.bitcoin.client;

import com.icoin.trading.bitcoin.client.request.AddNodeAction;
import com.icoin.trading.bitcoin.client.request.BitcoinJsonRpcRequest;
import com.icoin.trading.bitcoin.client.request.TemplateRequest;
import com.icoin.trading.bitcoin.client.response.*;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Boolean.FALSE;
import static java.util.Collections.EMPTY_LIST;

/**
 * Implements bitcoind client providing java style functions for calling bitcoind rest-rpc methods.
 *
 * @author Claus Nielsen
 */
public class BitcoinRpcTemplate implements BitcoinRpcOperations {


    private String url;


    private RestTemplate restTemplate;


    /**
     * @param url          url for calling bitcoind server.
     * @param restTemplate rest template to call rpc server.
     */
    public BitcoinRpcTemplate(String url, RestTemplate restTemplate) {
        this.url = url;
        this.restTemplate = restTemplate;
    }

    /**
     * @return url for calling bitcoind server.
     */
    @Override
    public String getUrl() {
        return url;
    }

    public RestTemplate getRestTemplate() {
        return restTemplate;
    }

    /**
     * Add a nrequired-to-sign multisignature address to the wallet.
     * <p/>
     * Each key is a bitcoin address or hex-encoded public key. If <code>account</code>
     * is specified, the new address is assigned to the given account.
     *
     * @param nrequired - number of signatures required.
     * @param keys      - keys which may sign. Each is a bitcoin address or a hex-encoded public key.
     * @param account   optional. If given the new address is assigned to this account.
     * @return {@link StringResponse}.
     */
    @Override
    public StringResponse addMultiSigAddress(int nrequired, List<String> keys, String account) {
        List<Object> params = new ArrayList<Object>();
        params.add(nrequired);
        params.add(keys);
        if (account != null) params.add(account);
        return jsonRpc("addmultisigaddress", params, StringResponse.class);
    }


    /**
     * Attempts add or remove <node> from the addnode list or try a connection
     * to &lt;node&gt; once.
     *
     * @param node   - host name or IP addres
     * @param action - what to do
     * @return {@link VoidResponse}
     * @see #getAddedNodeInfo(Boolean, String)
     * @since bitcoind 0.8
     */
    @Override
    public VoidResponse addNode(String node, AddNodeAction action) {
        List<Object> params = new ArrayList<Object>();
        params.add(node);
        params.add(action.toString());
        return jsonRpc("addnode", params, VoidResponse.class);
    }


    /**
     * Safely copies wallet.dat to destination.
     * <p/>
     * Destination can be a directory or a path with filename.
     *
     * @param destination - directory or filename.
     * @return {@link VoidResponse}
     */
    @Override
    public VoidResponse backupWallet(String destination) {
        List<Object> params = new ArrayList<Object>();
        params.add(destination);
        return jsonRpc("backupwallet", params, VoidResponse.class);

    }


    /**
     * Creates a multi-signature address.
     * <p/>
     * This is just like "addmultisigaddress" but instead of adding the multisig
     * address/redeemScript to the wallet, returns them in a object.
     *
     * @param nRequired - number of signatures required.
     * @param keys      -
     * @return {@link CreateMultiSigResponse}
     */
    @Override
    public CreateMultiSigResponse createMultiSig(Integer nRequired, String[] keys) {
        List<Object> params = new ArrayList<Object>();
        params.add(nRequired);
        if (keys != null) params.add(keys);
        return jsonRpc("createmultisig", params, CreateMultiSigResponse.class);
    }


    /**
     * Creates a raw transaction for spending given inputs.
     * <p/>
     * Create a transaction spending given {@link TransactionOutputRef}, for
     * sending to given address(es).<br>
     * Note that the transaction's inputs are not signed, and it is not stored
     * in the wallet or transmitted to the network.<br>
     *
     * @param txOutputs        - transaction outputs to spend
     * @param addressAndAmount - recipient and amount
     * @return {@link StringResponse} containing hex-encoded raw
     *         transaction.
     */
    @Override
    public StringResponse createRawTransaction(List<TransactionOutputRef> txOutputs, AddressAndAmount... addressAndAmount) {
        Map<String, BigDecimal> recipients = new HashMap<String, BigDecimal>();
        for (AddressAndAmount aaa : addressAndAmount) {
            String address = aaa.getAddress();
            BigDecimal amount = aaa.getAmount();
            if (recipients.containsKey(address)) {
                amount = recipients.get(address).add(amount);
            }
            recipients.put(address, amount);
        }
        List<Object> params = new ArrayList<Object>();
        params.add(txOutputs);
        params.add(recipients);
        return jsonRpc("createrawtransaction", params, StringResponse.class);
    }


    /**
     * Produces a human-readable JSON object for a raw transaction
     *
     * @param rawTransaction
     * @return {@link DecodeRawTransactionResponse}
     */
    @Override
    public DecodeRawTransactionResponse decodeRawTransaction(String rawTransaction) {
        List<Object> params = new ArrayList<Object>();
        params.add(rawTransaction);
        return jsonRpc("decoderawtransaction", params, DecodeRawTransactionResponse.class);
    }


    /**
     * Reveals the private key corresponding to the given bitcoin address.
     * <p/>
     * Requires unlocked wallet.
     *
     * @param bitcoinAddress
     * @return {@link StringResponse}
     */
    @Override
    public StringResponse dumpPrivateKey(String bitcoinAddress) {
        List<String> params = new ArrayList<String>();
        params.add(bitcoinAddress);
        return jsonRpc("dumpprivkey", params, StringResponse.class);
    }


    /**
     * Encrypts the wallet with the given pass phrase.
     *
     * @param passPhrase
     * @return {@link VoidResponse}
     */
    @Override
    public VoidResponse encryptWallet(String passPhrase) {
        List<String> params = new ArrayList<String>();
        params.add(passPhrase);
        return jsonRpc("encryptwallet", params, VoidResponse.class);
    }


    /**
     * Returns the account associated with the given address.
     *
     * @param bitcoinAddress
     * @return {@link StringResponse}
     */
    @Override
    public StringResponse getAccount(String bitcoinAddress) {
        List<String> params = new ArrayList<String>();
        params.add(bitcoinAddress);
        return jsonRpc("getaccount", params, StringResponse.class);
    }

    /**
     * Gets the current bitcoin address for receiving payments to the given account.
     *
     * @param account
     * @return {@link StringResponse}
     */
    @Override
    public StringResponse getAccountAddress(String account) {
        List<String> params = new ArrayList<String>();
        params.add(account);
        return jsonRpc("getaccountaddress", params, StringResponse.class);
    }


    /**
     * Returns information about the given added node, or all added nodes (note
     * that onetry addnodes are not listed here).
     *
     * @param dns  - If dns is false, only a list of added nodes will be
     *             provided, otherwise connected information will also be
     *             available.
     * @param node - optional (may be null).
     * @return {@link GetAddedNodeInfoResponse}
     * @since bitcoind 0.8
     */
    @Override
    public GetAddedNodeInfoResponse getAddedNodeInfo(Boolean dns, String node) {
        // TODO When calling with dns=false an object is returned; when calling with dns=true an array is returned.
        // TODO Currently only the array case (dns=true) is handled - see  https://github.com/bitcoin/bitcoin/issues/2467
        // TODO If bitcoind isn't changed (bug 2467) implement special serialization of the response in _GetAddedNodeInfoResponse_dnsArgFalse.json
        List<Object> params = new ArrayList<Object>();
        params.add(dns);
        if (node != null) params.add(node);
        return jsonRpc("getaddednodeinfo", params, GetAddedNodeInfoResponse.class);
    }


    /**
     * Returns the list of addresses for the given account.
     *
     * @param account
     * @return {@link StringArrayResponse} with bitcoin addresses.
     */
    @Override
    public StringArrayResponse getAddressesByAccount(String account) {
        List<Object> params = new ArrayList<Object>();
        params.add(account);
        return jsonRpc("getaddressesbyaccount", params, StringArrayResponse.class);
    }


    /**
     * Gets the balance of the given account or the server's total balance.
     *
     * @param account - optional (may be null). If specified, returns the balance in
     *                the account. If not, returns the server's total available
     *                balance.
     * @param minConf - optional (may be null). Minim number of confirmations.
     * @return {@link BigDecimalResponse}
     */
    @Override
    public BigDecimalResponse getBalance(String account, Integer minConf) {
        List<Object> params = new ArrayList<Object>();
        if (account != null || minConf != null) params.add(account);
        if (minConf != null) params.add(minConf);
        return jsonRpc("getbalance", params, BigDecimalResponse.class);
    }


    /**
     * Returns information about the given block hash.
     *
     * @param hash - block hash
     * @return {@link GetBlockResponse}
     */
    @Override
    public GetBlockResponse getBlock(String hash) {
        List<Object> params = new ArrayList<Object>();
        params.add(hash);
        return jsonRpc("getblock", params, GetBlockResponse.class);
    }


    /**
     * Returns the number of blocks in the longest block chain.
     *
     * @return {@link LongResponse} with number of blocks in the longest block chain.
     */
    @Override
    public LongResponse getBlockCount() {
        return jsonRpc("getblockcount", EMPTY_LIST, LongResponse.class);
    }


    /**
     * Returns hash of block in best-block-chain at given index.
     *
     * @param index
     * @return {@link StringResponse} with block hash.
     */
    @Override
    public StringResponse getBlockHash(Long index) {
        List<Object> params = new ArrayList<Object>();
        params.add(index);
        return jsonRpc("getblockhash", params, StringResponse.class);
    }


    /**
     * Gets a block template.
     *
     * @param templateRequest
     * @return {@link GetBlockResponse}
     */
    @Override
    public GetBlockTemplateResponse getBlockTemplate(TemplateRequest templateRequest) {
        List<Object> params = new ArrayList<Object>();
        params.add(templateRequest);
        return jsonRpc("getblocktemplate", params, GetBlockTemplateResponse.class);
    }


    /**
     * Returns the number of connections to other nodes.
     *
     * @return {@link IntegerResponse} with number of connections.
     */
    @Override
    public IntegerResponse getConnectionCount() {
        return jsonRpc("getconnectioncount", EMPTY_LIST, IntegerResponse.class);
    }


    /**
     * Returns the proof-of-work difficulty as a multiple of the minimum difficulty.
     *
     * @return {@link LongResponse} with difficulty.
     */
    @Override
    public IntegerResponse getDifficulty() {
        return jsonRpc("getdifficulty", EMPTY_LIST, IntegerResponse.class);
    }


    /**
     * Returns true or false whether bitcoind is currently generating hashes.
     *
     * @return {@link BooleanResponse}, true if generating.
     */
    @Override
    public BooleanResponse getGenerate() {
        return jsonRpc("getgenerate", EMPTY_LIST, BooleanResponse.class);
    }


    /**
     * Returns a recent hashes per second performance measurement while generating.
     *
     * @return {@link LongResponse} with hashes per second.
     */
    @Override
    public LongResponse getHashesPerSecond() {
        return jsonRpc("gethashespersec", EMPTY_LIST, LongResponse.class);
    }


    /**
     * Gets various state info.
     *
     * @return {@link GetInfoResponse}
     */
    @Override
    public GetInfoResponse getInfo() {
        return jsonRpc("getinfo", EMPTY_LIST, GetInfoResponse.class);
    }


    /**
     * Gets mining-related information.
     *
     * @return {@link GetMiningInfoResponse} - mining-related information.
     */
    @Override
    public GetMiningInfoResponse getMiningInfo() {
        return jsonRpc("getmininginfo", EMPTY_LIST, GetMiningInfoResponse.class);
    }


    /**
     * Returns a new bitcoin address for receiving payments. If
     * <code>account</code> is specified (recommended), it is added to the
     * address book so payments received with the address will be credited to
     * <code>account</code>.
     *
     * @param account - account to associate with the new address.
     * @return {@link StringResponse} with the new address.
     */
    @Override
    public StringResponse getNewAddress(String account) {
        List<Object> params = new ArrayList<Object>();
        if (account != null) params.add(account);
        return jsonRpc("getnewaddress", params, StringResponse.class);
    }


    /**
     * Returns data about each connected node.
     *
     * @return {@link GetPeerInfoResponse}
     * @since bitcoind 0.7
     */
    @Override
    public GetPeerInfoResponse getPeerInfo() {
        return jsonRpc("getpeerinfo", EMPTY_LIST, GetPeerInfoResponse.class);
    }


    /**
     * Returns all transaction ids in memory pool.
     *
     * @return {@link StringArrayResponse} with transaction ids.
     * @since bitcoind 0.7
     */
    @Override
    public StringArrayResponse getRawMemPool() {
        return jsonRpc("getrawmempool", EMPTY_LIST, StringArrayResponse.class);
    }


    /**
     * Returns raw transaction representation for given transaction id.
     *
     * @param txId - transaction id
     * @return {@link StringResponse} with hex encoded raw transaction.
     * @since bitcoind 0.7
     */
    @Override
    public StringResponse getRawTransaction(String txId) {
        List<Object> params = new ArrayList<Object>();
        params.add(txId);
        return jsonRpc("getrawtransaction", params, StringResponse.class);
    }


    /**
     * Returns raw transaction representation for given transaction id.
     *
     * @param txId - transaction id
     * @return {@link GetRawTransactionResponse}
     * @since bitcoind 0.7
     */
    @Override
    public GetRawTransactionResponse getRawTransaction_verbose(String txId) {
        List<Object> params = new ArrayList<Object>();
        params.add(txId);
        params.add(1); // verbose
        return jsonRpc("getrawtransaction", params, GetRawTransactionResponse.class);
    }


    /**
     * Returns the total amount received by addresses with <code>account</code>
     * in transactions with at least <code>minconf</code> confirmations.
     *
     * @param account
     * @param minConf - optional, default 1
     * @return {@link BigDecimalResponse}
     * @since bitcoind 0.3.24
     */
    @Override
    public BigDecimalResponse getReceivedByAccount(String account, Integer minConf) {
        List<Object> params = new ArrayList<Object>();
        params.add(account == null ? "" : account);
        params.add(firstNotNull(minConf, 1));
        return jsonRpc("getreceivedbyaccount", params, BigDecimalResponse.class);
    }


    /**
     * Returns the total amount received by the given address in transactions
     * with at least <code>minconf</code> confirmations. While some might
     * consider this obvious, value reported by this only considers
     * <b>receiving</b> transactions. It does not check payments that have been
     * made <b>from</b> this address. In other words, this is not
     * "getAddressBalance". Works only for addresses in the local wallet,
     * external addresses will always show 0.
     *
     * @param address - bitcoin address
     * @param minConf - optional, default 1
     * @return {@link BigDecimalResponse}
     */
    @Override
    public BigDecimalResponse getReceivedByAddress(String address, Integer minConf) {
        List<Object> params = new ArrayList<Object>();
        params.add(address == null ? "" : address);
        params.add(firstNotNull(minConf, 1));
        return jsonRpc("getreceivedbyaddress", params, BigDecimalResponse.class);
    }


    /**
     * Gets data regarding the transaction with the given id.
     *
     * @param txId - transaction id
     * @return {@link GetTransactionResponse}
     */
    @Override
    public GetTransactionResponse getTransaction(String txId) {
        List<String> params = new ArrayList<String>();
        params.add(txId);
        return jsonRpc("gettransaction", params, GetTransactionResponse.class);
    }


    /**
     * Returns details about an unspent transaction output.
     *
     * @param txId              - transaction id
     * @param n                 - output number
     * @param includeMemoryPool - optional, default true.
     * @return {@link GetTxOutResponse}
     */
    @Override
    public GetTxOutResponse getTxOut(String txId, Integer n, Boolean includeMemoryPool) {
        List<Object> params = new ArrayList<Object>();
        params.add(txId);
        params.add(n);
        params.add(firstNotNull(includeMemoryPool, true));
        return jsonRpc("gettxout", params, GetTxOutResponse.class);
    }


    /**
     * Returns statistics about the unspent transaction output set.
     *
     * @return
     */
    @Override
    public GetTxOutSetInfoResponse getTxOutSetInfo() {
        return jsonRpc("gettxoutsetinfo", EMPTY_LIST, GetTxOutSetInfoResponse.class);
    }


    /**
     * Returns formatted hash data to work on.
     *
     * @return {@link GetWorkResponse} - true if succesfull.
     */
    @Override
    public GetWorkResponse getWork() {
        return jsonRpc("getwork", EMPTY_LIST, GetWorkResponse.class);
    }


    /**
     * Tries to solve the block.
     *
     * @param data - block data
     * @return {@link BooleanResponse} - true if succesfull.
     */
    @Override
    public BooleanResponse getWork(String data) {
        List<Object> params = new ArrayList<Object>();
        params.add(data);
        return jsonRpc("getwork", params, BooleanResponse.class);
    }


    /**
     * Gets help for a command or lists commands.
     *
     * @param command - optional. If null a list of available commands is returned.
     * @return help for the given command or list of commands.
     */
    @Override
    public StringResponse help(String command) {
        List<Object> params = new ArrayList<Object>();
        if (command != null) params.add(command);
        return jsonRpc("help", params, StringResponse.class);
    }


    /**
     * Adds a private key (as returned by dumpPrivKey) to the wallet. This may
     * take a while, as a rescan is done, looking for existing transactions.
     * Optional [rescan] parameter added in 0.8.0.
     * <p/>
     * Requires unlocked wallet.
     *
     * @param key
     * @param label  - optional label
     * @param rescan - optional, default true.
     * @return {@link VoidResponse}
     */
    @Override
    public VoidResponse importPrivateKey(String key, String label, Boolean rescan) {
        List<Object> params = new ArrayList<Object>();
        params.add(key);
        params.add(firstNotNull(label, ""));
        params.add(firstNotNull(rescan, true));
        return jsonRpc("importprivkey", params, VoidResponse.class);
    }


    /**
     * Fills the keypool.
     * <p/>
     * Requires unlocked wallet.
     *
     * @return {@link VoidResponse}
     */
    @Override
    public VoidResponse keyPoolRefill() {
        return jsonRpc("keypoolrefill", EMPTY_LIST, VoidResponse.class);
    }


    /**
     * Returns account names and balances.
     *
     * @param minConf - minimum number of confirmations for included transactions,
     *                default 1.
     * @return {@link ListAccountsResponse}
     */
    @Override
    public ListAccountsResponse listAccounts(Integer minConf) {
        List<Object> params = new ArrayList<Object>();
        params.add(firstNotNull(minConf, 1));
        return jsonRpc("listaccounts", params, ListAccountsResponse.class);
    }


    /**
     * Lists groups of addresses which have had their common ownership made
     * public by common use as inputs or as the resulting change in past
     * transactions.
     *
     * @return {@link ListAddressGroupingsResponse}
     * @since bitcoind 0.7
     */
    @Override
    public ListAddressGroupingsResponse listAddressGroupings() {
        return jsonRpc("listaddressgroupings", EMPTY_LIST, ListAddressGroupingsResponse.class);
    }


    /**
     * Returns list of temporarily unspendable outputs.
     *
     * @return {@link ListLockUnspentResponse}
     * @since bitcoind 0.8
     */
    @Override
    public ListLockUnspentResponse listLockUnspent() {
        return jsonRpc("listlockunspent", EMPTY_LIST, ListLockUnspentResponse.class);
    }


    /**
     * Gets amount received for each account.
     *
     * @param minConf      - optional, default 1.
     * @param includeEmpty - optional, default false.
     * @return {@link ListReceivedByAccountResponse}
     */
    @Override
    public ListReceivedByAccountResponse listReceivedByAccount(Integer minConf, Boolean includeEmpty) {
        List<Object> params = new ArrayList<Object>();
        params.add(firstNotNull(minConf), Integer.valueOf(1));
        params.add(firstNotNull(includeEmpty, FALSE));
        return jsonRpc("listreceivedbyaccount", params, ListReceivedByAccountResponse.class);
    }


    /**
     * Gets amount received for each address.
     * <p/>
     * To get a list of accounts on the system call with minConf = 0 and includeEmpty = true.
     *
     * @param minConf      - optional, default 1.
     * @param includeEmpty - optional, default false.
     * @return {@link ListReceivedByAddressResponse}
     */
    @Override
    public ListReceivedByAddressResponse listReceivedByAddress(Integer minConf, Boolean includeEmpty) {
        List<Object> params = new ArrayList<Object>();
        params.add(firstNotNull(minConf), Integer.valueOf(1));
        params.add(firstNotNull(includeEmpty, FALSE));
        return jsonRpc("listreceivedbyaddress", params, ListReceivedByAddressResponse.class);
    }


    /**
     * Gets all transactions in blocks since block <code>blockhash</code>, or
     * all transactions if omitted.
     *
     * @param blockHash           - optional (may be null)
     * @param targetConfirmations - optional (may be null)
     * @return {@link ListSinceBlockResponse}
     */
    public ListSinceBlockResponse listSinceBlock(String blockHash, Integer targetConfirmations) {
        List<Object> params = new ArrayList<Object>();
        if (blockHash != null || targetConfirmations != null) params.add(blockHash);
        if (targetConfirmations != null) params.add(targetConfirmations);
        return jsonRpc("listsinceblock", params, ListSinceBlockResponse.class);
    }


    /**
     * Returns up to <code>count</code> most recent transactions skipping the
     * first <code>from</code> transactions for account <code>account</code>.
     *
     * @param account - optional (may be null). If not provided will return recent
     *                transaction from all accounts.
     * @param count   - optional (may be null). Maximum number of transaction to
     *                return. Default 10.
     * @param from    - optional (may be null). Number of transactions to skip.
     *                Default 0.
     * @return {@link ListTransactionsResponse}
     */
    @Override
    public ListTransactionsResponse listTransactions(String account, Integer count, Integer from) {
        List<Object> params = new ArrayList<Object>();
        params.add(account);
        params.add(firstNotNull(count, 10));
        params.add(firstNotNull(from, 0));
        return jsonRpc("listtransactions", params, ListTransactionsResponse.class);
    }


    /**
     * Lists unspent transaction outputs with between minConf and maxConf
     * (inclusive) confirmations. Optionally filtered to only include transaction
     * outputs paid to specified addresses.<br>
     *
     * @param minConf - optional minimum number of confirmations. Default 1.
     * @param maxConf - optional maximum number of confirmations. Default 999999.
     * @param address - optional address(es) limiting the output to transaction
     *                outputs paid to those addresses.
     * @return {@link ListUnspentResponse}
     */
    @Override
    public ListUnspentResponse listUnspent(Integer minConf, Integer maxConf, String... address) {
        List<Object> params = new ArrayList<Object>();
        params.add(firstNotNull(minConf, Integer.valueOf(1)));
        params.add(firstNotNull(maxConf, Integer.valueOf(999999)));
        params.add(address);
        return jsonRpc("listunspent", params, ListUnspentResponse.class);
    }


    /**
     * Updates list of temporarily unspendable outputs.
     *
     * @param unlock    - unlock (true) or lock (false)
     * @param txOutputs - references to transaction outputs to lock or unlock
     * @return {@link BooleanResponse}
     * @since bitcoind 0.8
     */
    @Override
    public BooleanResponse lockUnspent(Boolean unlock, TransactionOutputRef[] txOutputs) {
        List<Object> params = new ArrayList<Object>();
        params.add(unlock);
        params.add(txOutputs);
        return jsonRpc("lockunspent", params, BooleanResponse.class);
    }


    /**
     * Move from one account in your wallet to another.
     *
     * @param fromAccount
     * @param toAccount
     * @param amount
     * @param minConf     - Optional (may be null). Minimum confirmations. Default 1.
     * @param comment     - optional (may be null)
     * @return
     */
    @Override
    public BooleanResponse move(String fromAccount, String toAccount, BigDecimal amount, Integer minConf, String comment) {
        List<Object> params = new ArrayList<Object>();
        params.add(fromAccount);
        params.add(toAccount);
        params.add(amount);
        params.add(firstNotNull(minConf, 1));
        if (comment != null) params.add(comment);
        return jsonRpc("move", params, BooleanResponse.class);
    }


    /**
     * Sends the given amount to the given address, ensuring the account has a
     * valid balance using <code>minconf</code> confirmations. Returns the
     * transaction id if successful.
     * <p/>
     * Requires unlocked wallet.
     *
     * @param account
     * @param address   - recipient's bitcoin address
     * @param amount    - bitcoins
     * @param minConf   - optional (may be null). Minimum number of confirmations for
     *                  consumed transaction outputs. Default 1.
     * @param comment   - optional (may be null). Text for the transactions comment
     *                  field
     * @param commentTo - optional (may be null). Text for the transactions to: field
     * @return String with transaction number
     */
    @Override
    public StringResponse sendFrom(String account, String address, BigDecimal amount, Integer minConf, String comment, String commentTo) {
        List<Object> params = new ArrayList<Object>();
        params.add(account);
        params.add(address);
        params.add(amount.setScale(SCALE));
        params.add(firstNotNull(minConf, 1));
        if (comment != null || commentTo != null) params.add(comment);
        if (commentTo != null) params.add(commentTo);
        return jsonRpc("sendfrom", params, StringResponse.class);
    }


    /**
     * Sends to many recipients.
     *
     * @param fromAccount
     * @param addressesAndAmounts - recipients and amounts
     * @param minConf             - optional (may be null). Minimum number of confirmations.
     *                            Default 1.
     * @param commment            - optional (may be null)
     * @return {@link StringResponse} with transaction id, if successful.
     */
    @Override
    public StringResponse sendMany(String fromAccount, AddressAndAmount[] addressesAndAmounts, Integer minConf, String commment) {
        List<Object> params = new ArrayList<Object>();
        params.add(fromAccount);
        Map<String, BigDecimal> recipients = new HashMap<String, BigDecimal>();
        for (AddressAndAmount aaa : addressesAndAmounts) {
            String address = aaa.getAddress();
            BigDecimal amount = aaa.getAmount();
            if (recipients.containsKey(address)) {
                amount = recipients.get(address).add(amount);
            }
            recipients.put(address, amount);
        }
        params.add(recipients);
        params.add(firstNotNull(minConf, 1));
        if (commment != null) params.add(commment);
        return jsonRpc("sendmany", params, StringResponse.class);
    }


    /**
     * Submits raw transaction to local node and network.
     *
     * @param hex - transaction data (serialized, hex-encoded)
     * @return {@link StringResponse} with transaction id, if successful.
     * @since bitcoind 0.7
     */
    @Override
    public StringResponse sendRawTransaction(String hex) {
        List<Object> params = new ArrayList<Object>();
        params.add(hex);
        return jsonRpc("sendrawtransaction", params, StringResponse.class);
    }


    /**
     * Sends bitcoins to the given address.
     *
     * @param address   - bitcoin address
     * @param amount    - bitcoins
     * @param comment   - optional (may be null). Text for the transactions comment field
     * @param commentTo - optional (may be null). Text for the transactions to: field
     * @return String with transaction number
     */
    @Override
    public StringResponse sendToAddress(String address, BigDecimal amount, String comment, String commentTo) {
        List<Object> params = new ArrayList<Object>();
        params.add(address);
        params.add(amount.setScale(SCALE));
        if (comment != null || commentTo != null) params.add(comment);
        if (commentTo != null) params.add(commentTo);
        return jsonRpc("sendtoaddress", params, StringResponse.class);
    }


    /**
     * Sets the account associated with the given address. Assigning an address
     * that is already assigned to the same account will create a new address
     * associated with that account.
     *
     * @param address - bitcoin address
     * @param account - the account to set
     * @return {@link VirtualMachineError}
     */
    @Override
    public VoidResponse setAccount(String address, String account) {
        List<Object> params = new ArrayList<Object>();
        params.add(address);
        params.add(account);
        return jsonRpc("setaccount", params, VoidResponse.class);
    }


    /**
     * Turnes generation on or off.
     *
     * @param generate     - turn generation on (true) or off (false).
     * @param genProcLimit - optional (may be null). Generation is limited to
     *                     <code>genProcLimit</code> processors, -1 is unlimited.
     * @return {@link VoidResponse}
     */
    @Override
    public VoidResponse setGenerate(Boolean generate, Integer genProcLimit) {
        List<Object> params = new ArrayList<Object>();
        params.add(generate);
        if (genProcLimit != null) params.add(genProcLimit);
        return jsonRpc("setgenerate", params, VoidResponse.class);
    }


    /**
     * Sets transaction fee.
     *
     * @param amount - transaction fee.
     * @return {@link BooleanResponse}
     */
    @Override
    public BooleanResponse setTxFee(BigDecimal amount) {
        List<Object> params = new ArrayList<Object>();
        params.add(amount.setScale(SCALE));
        return jsonRpc("settxfee", params, BooleanResponse.class);
    }


    /**
     * Sign a message with the private key of an address.
     * <p/>
     * Requires unlocked wallet.
     *
     * @param address - bitcoin address.
     * @param message - the message to sign.
     * @return {@link StringResponse} with the signed message in the result
     *         field.
     */
    @Override
    public StringResponse signMessage(String address, String message) {
        List<Object> params = new ArrayList<Object>();
        params.add(address);
        params.add(message);
        return jsonRpc("signmessage", params, StringResponse.class);
    }


    /**
     * Signs inputs for raw transaction (serialized, hex-encoded).
     * <p/>
     * <p/>
     * nReturns json object with keys: hex : raw transaction with signature(s)
     * (hex-encoded string) complete : 1 if transaction has a complete set of
     * signature (0 if not)
     * <p/>
     * Requires unlocked wallet.
     * <p/>
     * {"result":"signrawtransaction <hex string> [{\
     * "txid\":txid,\"vout\":n,\"scriptPubKey\":hex,\"redeemScript\":hex},...]
     * [<privatekey1>,...] [sighashtype=\"ALL\"]\n
     *
     * @param hex            - raw unsigned transaction.
     * @param requiredTxOuts - optional (may be null). An array of previous transaction
     *                       outputs that this transaction depends on but may not yet be in
     *                       the block chain
     * @param privKeys       - optional (may be null). An array of base58-encoded private
     *                       keys that, if given, will be the only keys used to sign the
     *                       transaction.
     * @param sigHash        - optional (may be null).
     * @return
     * @since bitcoind 0.7
     */
    // TODO signrawtransaction <hexstring> [{"txid":txid,"vout":n,"scriptPubKey":hex},...] [<privatekey1>,...] Adds signatures to a raw transaction and returns the resulting raw transaction. Y/N
    // TODO Define type for requiredTxOuts
    // TODO Test using args 2..4
    @Override
    public SignRawTransactionResponse signRawTransaction(String hex, Object[] requiredTxOuts, String[] privKeys, SignatureHashAlgorithm sigHash) {
        List<Object> params = new ArrayList<Object>();
        params.add(hex);
        params.add(requiredTxOuts);
        params.add(privKeys);
        params.add(sigHash == null ? null : sigHash.toString());
        return jsonRpc("signrawtransaction", params, SignRawTransactionResponse.class);
    }


    /**
     * Stop bitcoin server.
     *
     * @return
     */
    @Override
    public VoidResponse stop() {
        return jsonRpc("stop", EMPTY_LIST, VoidResponse.class);
    }


    // TODO submitblock <hex data> [optional-params-obj]


    /**
     * Returns information about the given bitcoin address.
     *
     * @param address
     * @return {@link ValidateAddressResponse}
     */
    @Override
    public ValidateAddressResponse validateAddress(String address) {
        List<Object> params = new ArrayList<Object>();
        params.add(address);
        return jsonRpc("validateaddress", params, ValidateAddressResponse.class);
    }


    /**
     * Verifies a signed message.
     *
     * @param address
     * @param signature
     * @param message
     * @return {@link BooleanResponse}
     */
    @Override
    public BooleanResponse verifyMessage(String address, String signature, String message) {
        List<Object> params = new ArrayList<Object>();
        params.add(address);
        params.add(signature);
        params.add(message);
        return jsonRpc("verifymessage", params, BooleanResponse.class);
    }


    /**
     * Removes the wallet encryption key from memory, locking the wallet.
     * <p/>
     * After calling this method, you will need to call walletPassPhrase
     * again before being able to call any methods which require the wallet
     * to be unlocked.
     *
     * @return {@link VoidResponse}
     */
    @Override
    public VoidResponse walletLock() {
        return jsonRpc("walletlock", EMPTY_LIST, VoidResponse.class);
    }


    /**
     * Unlocks the wallet for the number of seconds given.
     * <p/>
     * Stores the wallet decryption key in memory for <code>timeout</code>
     * seconds.
     *
     * @param passPhrase
     * @param timeout    number of seconds the encryption key is stored in memory,
     *                   keeping the wallet unlocked.
     * @return {@link VoidResponse}
     */
    @Override
    public VoidResponse walletPassPhrase(String passPhrase, int timeout) {
        List<Object> params = new ArrayList<Object>();
        params.add(passPhrase);
        params.add(Integer.valueOf(timeout));
        return jsonRpc("walletpassphrase", params, VoidResponse.class);
    }


    /**
     * Changes the wallet passphrase from <code>oldpassphrase</code> to
     * <code>newpassphrase</code>.
     *
     * @param oldPassPhrase
     * @param newPassPhrase
     * @return {@link VoidResponse}
     */
    @Override
    public VoidResponse walletPassPhraseChange(String oldPassPhrase, String newPassPhrase) {
        List<String> params = new ArrayList<String>();
        params.add(oldPassPhrase);
        params.add(newPassPhrase);
        return jsonRpc("walletpassphrasechange", params, VoidResponse.class);
    }


    /**
     * Performs a JSON-RPC call specifying the given method and parameters and
     * returning a response of the given type.
     *
     * @param method
     * @param params
     * @param responseType
     * @return json response converted to the given type
     */
    private <T> T jsonRpc(String method, List<?> params, Class<T> responseType) {
        BitcoinJsonRpcRequest request = new BitcoinJsonRpcRequest(method, params);
        return restTemplate.postForObject(url, request, responseType);
    }

    @SafeVarargs
    public static <T> T firstNotNull(T... args) {
        for (T arg : args) {
            if (arg != null)
                return arg;
        }
        return null;
    }
}
