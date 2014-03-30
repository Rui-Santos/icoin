package com.icoin.trading.bitcoin.client;

import com.icoin.trading.bitcoin.client.request.AddNodeAction;
import com.icoin.trading.bitcoin.client.request.TemplateRequest;
import com.icoin.trading.bitcoin.client.response.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-3-24
 * Time: PM9:36
 * To change this template use File | Settings | File Templates.
 */
public interface BitcoinRpcOperations {
    public static final int SCALE = 8;

    /**
     * @return url for calling bitcoind server.
     */
    String getUrl();

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
    StringResponse addMultiSigAddress(int nrequired, List<String> keys,
                                      String account);

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
    VoidResponse addNode(String node, AddNodeAction action);

    /**
     * Safely copies wallet.dat to destination.
     * <p/>
     * Destination can be a directory or a path with filename.
     *
     * @param destination - directory or filename.
     * @return {@link VoidResponse}
     */
    VoidResponse backupWallet(String destination);

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
    CreateMultiSigResponse createMultiSig(Integer nRequired, String[] keys);

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
    StringResponse createRawTransaction(List<TransactionOutputRef> txOutputs,
                                        AddressAndAmount... addressAndAmount);

    /**
     * Produces a human-readable JSON object for a raw transaction
     *
     * @param rawTransaction
     * @return {@link DecodeRawTransactionResponse}
     */
    DecodeRawTransactionResponse decodeRawTransaction(String rawTransaction);

    /**
     * Reveals the private key corresponding to the given bitcoin address.
     * <p/>
     * Requires unlocked wallet.
     *
     * @param bitcoinAddress
     * @return {@link StringResponse}
     */
    StringResponse dumpPrivateKey(String bitcoinAddress);

    /**
     * Encrypts the wallet with the given pass phrase.
     *
     * @param passPhrase
     * @return {@link VoidResponse}
     */
    VoidResponse encryptWallet(String passPhrase);

    /**
     * Returns the account associated with the given address.
     *
     * @param bitcoinAddress
     * @return {@link StringResponse}
     */
    StringResponse getAccount(String bitcoinAddress);

    /**
     * Gets the current bitcoin address for receiving payments to the given account.
     *
     * @param account
     * @return {@link StringResponse}
     */
    StringResponse getAccountAddress(String account);

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
    GetAddedNodeInfoResponse getAddedNodeInfo(Boolean dns, String node);

    /**
     * Returns the list of addresses for the given account.
     *
     * @param account
     * @return {@link StringArrayResponse} with bitcoin addresses.
     */
    StringArrayResponse getAddressesByAccount(String account);

    /**
     * Gets the balance of the given account or the server's total balance.
     *
     * @param account - optional (may be null). If specified, returns the balance in
     *                the account. If not, returns the server's total available
     *                balance.
     * @param minConf - optional (may be null). Minim number of confirmations.
     * @return {@link BigDecimalResponse}
     */
    BigDecimalResponse getBalance(String account, Integer minConf);

    /**
     * Returns information about the given block hash.
     *
     * @param hash - block hash
     * @return {@link GetBlockResponse}
     */
    GetBlockResponse getBlock(String hash);

    /**
     * Returns the number of blocks in the longest block chain.
     *
     * @return {@link LongResponse} with number of blocks in the longest block chain.
     */
    LongResponse getBlockCount();

    /**
     * Returns hash of block in best-block-chain at given index.
     *
     * @param index
     * @return {@link StringResponse} with block hash.
     */
    StringResponse getBlockHash(Long index);

    /**
     * Gets a block template.
     *
     * @param templateRequest
     * @return {@link GetBlockResponse}
     */
    GetBlockTemplateResponse getBlockTemplate(TemplateRequest templateRequest);

    /**
     * Returns the number of connections to other nodes.
     *
     * @return {@link IntegerResponse} with number of connections.
     */
    IntegerResponse getConnectionCount();

    /**
     * Returns the proof-of-work difficulty as a multiple of the minimum difficulty.
     *
     * @return {@link LongResponse} with difficulty.
     */
    IntegerResponse getDifficulty();

    /**
     * Returns true or false whether bitcoind is currently generating hashes.
     *
     * @return {@link BooleanResponse}, true if generating.
     */
    BooleanResponse getGenerate();

    /**
     * Returns a recent hashes per second performance measurement while generating.
     *
     * @return {@link LongResponse} with hashes per second.
     */
    LongResponse getHashesPerSecond();

    /**
     * Gets various state info.
     *
     * @return {@link GetInfoResponse}
     */
    GetInfoResponse getInfo();

    /**
     * Gets mining-related information.
     *
     * @return {@link GetMiningInfoResponse} - mining-related information.
     */
    GetMiningInfoResponse getMiningInfo();

    /**
     * Returns a new bitcoin address for receiving payments. If
     * <code>account</code> is specified (recommended), it is added to the
     * address book so payments received with the address will be credited to
     * <code>account</code>.
     *
     * @param account - account to associate with the new address.
     * @return {@link StringResponse} with the new address.
     */
    StringResponse getNewAddress(String account);

    /**
     * Returns data about each connected node.
     *
     * @return {@link GetPeerInfoResponse}
     * @since bitcoind 0.7
     */
    GetPeerInfoResponse getPeerInfo();

    /**
     * Returns all transaction ids in memory pool.
     *
     * @return {@link StringArrayResponse} with transaction ids.
     * @since bitcoind 0.7
     */
    StringArrayResponse getRawMemPool();

    /**
     * Returns raw transaction representation for given transaction id.
     *
     * @param txId - transaction id
     * @return {@link StringResponse} with hex encoded raw transaction.
     * @since bitcoind 0.7
     */
    StringResponse getRawTransaction(String txId);

    /**
     * Returns raw transaction representation for given transaction id.
     *
     * @param txId - transaction id
     * @return {@link GetRawTransactionResponse}
     * @since bitcoind 0.7
     */
    GetRawTransactionResponse getRawTransaction_verbose(String txId);

    /**
     * Returns the total amount received by addresses with <code>account</code>
     * in transactions with at least <code>minconf</code> confirmations.
     *
     * @param account
     * @param minConf - optional, default 1
     * @return {@link BigDecimalResponse}
     * @since bitcoind 0.3.24
     */
    BigDecimalResponse getReceivedByAccount(String account, Integer minConf);

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
    BigDecimalResponse getReceivedByAddress(String address, Integer minConf);

    /**
     * Gets data regarding the transaction with the given id.
     *
     * @param txId - transaction id
     * @return {@link GetTransactionResponse}
     */
    GetTransactionResponse getTransaction(String txId);

    /**
     * Returns details about an unspent transaction output.
     *
     * @param txId              - transaction id
     * @param n                 - output number
     * @param includeMemoryPool - optional, default true.
     * @return {@link GetTxOutResponse}
     */
    GetTxOutResponse getTxOut(String txId, Integer n, Boolean includeMemoryPool);

    /**
     * Returns statistics about the unspent transaction output set.
     *
     * @return
     */
    GetTxOutSetInfoResponse getTxOutSetInfo();

    /**
     * Returns formatted hash data to work on.
     *
     * @return {@link GetWorkResponse} - true if succesfull.
     */
    GetWorkResponse getWork();

    /**
     * Tries to solve the block.
     *
     * @param data - block data
     * @return {@link BooleanResponse} - true if succesfull.
     */
    BooleanResponse getWork(String data);

    /**
     * Gets help for a command or lists commands.
     *
     * @param command - optional. If null a list of available commands is returned.
     * @return help for the given command or list of commands.
     */
    StringResponse help(String command);

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
    VoidResponse importPrivateKey(String key, String label, Boolean rescan);

    /**
     * Fills the keypool.
     * <p/>
     * Requires unlocked wallet.
     *
     * @return {@link VoidResponse}
     */
    VoidResponse keyPoolRefill();

    /**
     * Returns account names and balances.
     *
     * @param minConf - minimum number of confirmations for included transactions,
     *                default 1.
     * @return {@link ListAccountsResponse}
     */
    ListAccountsResponse listAccounts(Integer minConf);

    /**
     * Lists groups of addresses which have had their common ownership made
     * public by common use as inputs or as the resulting change in past
     * transactions.
     *
     * @return {@link ListAddressGroupingsResponse}
     * @since bitcoind 0.7
     */
    ListAddressGroupingsResponse listAddressGroupings();

    /**
     * Returns list of temporarily unspendable outputs.
     *
     * @return {@link ListLockUnspentResponse}
     * @since bitcoind 0.8
     */
    ListLockUnspentResponse listLockUnspent();

    /**
     * Gets amount received for each account.
     *
     * @param minConf      - optional, default 1.
     * @param includeEmpty - optional, default false.
     * @return {@link ListReceivedByAccountResponse}
     */
    ListReceivedByAccountResponse listReceivedByAccount(Integer minConf,
                                                        Boolean includeEmpty);

    /**
     * Gets amount received for each address.
     * <p/>
     * To get a list of accounts on the system call with minConf = 0 and includeEmpty = true.
     *
     * @param minConf      - optional, default 1.
     * @param includeEmpty - optional, default false.
     * @return {@link ListReceivedByAddressResponse}
     */
    ListReceivedByAddressResponse listReceivedByAddress(Integer minConf,
                                                        Boolean includeEmpty);

    /**
     * Gets all transactions in blocks since block <code>blockhash</code>, or
     * all transactions if omitted.
     *
     * @param blockHash           - optional (may be null)
     * @param targetConfirmations - optional (may be null)
     * @return {@link ListSinceBlockResponse}
     */
    ListSinceBlockResponse listSinceBlock(String blockHash, Integer targetConfirmations);

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
    ListTransactionsResponse listTransactions(String account, Integer count,
                                              Integer from);

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
    ListUnspentResponse listUnspent(Integer minConf, Integer maxConf,
                                    String... address);

    /**
     * Updates list of temporarily unspendable outputs.
     *
     * @param unlock    - unlock (true) or lock (false)
     * @param txOutputs - references to transaction outputs to lock or unlock
     * @return {@link BooleanResponse}
     * @since bitcoind 0.8
     */
    BooleanResponse lockUnspent(Boolean unlock, TransactionOutputRef[] txOutputs);

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
    BooleanResponse move(String fromAccount, String toAccount,
                         BigDecimal amount, Integer minConf, String comment);

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
    StringResponse sendFrom(String account, String address, BigDecimal amount,
                            Integer minConf, String comment, String commentTo);

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
    StringResponse sendMany(String fromAccount,
                            AddressAndAmount[] addressesAndAmounts, Integer minConf,
                            String commment);

    /**
     * Submits raw transaction to local node and network.
     *
     * @param hex - transaction data (serialized, hex-encoded)
     * @return {@link StringResponse} with transaction id, if successful.
     * @since bitcoind 0.7
     */
    StringResponse sendRawTransaction(String hex);

    /**
     * Sends bitcoins to the given address.
     *
     * @param address   - bitcoin address
     * @param amount    - bitcoins
     * @param comment   - optional (may be null). Text for the transactions comment field
     * @param commentTo - optional (may be null). Text for the transactions to: field
     * @return String with transaction number
     */
    StringResponse sendToAddress(String address, BigDecimal amount,
                                 String comment, String commentTo);

    /**
     * Sets the account associated with the given address. Assigning an address
     * that is already assigned to the same account will create a new address
     * associated with that account.
     *
     * @param address - bitcoin address
     * @param account - the account to set
     * @return {@link VirtualMachineError}
     */
    VoidResponse setAccount(String address, String account);

    /**
     * Turnes generation on or off.
     *
     * @param generate     - turn generation on (true) or off (false).
     * @param genProcLimit - optional (may be null). Generation is limited to
     *                     <code>genProcLimit</code> processors, -1 is unlimited.
     * @return {@link VoidResponse}
     */
    VoidResponse setGenerate(Boolean generate, Integer genProcLimit);

    /**
     * Sets transaction fee.
     *
     * @param amount - transaction fee.
     * @return {@link BooleanResponse}
     */
    BooleanResponse setTxFee(BigDecimal amount);

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
    StringResponse signMessage(String address, String message);

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
    SignRawTransactionResponse signRawTransaction(String hex,
                                                  Object[] requiredTxOuts, String[] privKeys,
                                                  SignatureHashAlgorithm sigHash);

    /**
     * Stop bitcoin server.
     *
     * @return
     */
    VoidResponse stop();

    /**
     * Returns information about the given bitcoin address.
     *
     * @param address
     * @return {@link ValidateAddressResponse}
     */
    ValidateAddressResponse validateAddress(String address);

    /**
     * Verifies a signed message.
     *
     * @param address
     * @param signature
     * @param message
     * @return {@link BooleanResponse}
     */
    BooleanResponse verifyMessage(String address, String signature,
                                  String message);

    /**
     * Removes the wallet encryption key from memory, locking the wallet.
     * <p/>
     * After calling this method, you will need to call walletPassPhrase
     * again before being able to call any methods which require the wallet
     * to be unlocked.
     *
     * @return {@link VoidResponse}
     */
    VoidResponse walletLock();

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
    VoidResponse walletPassPhrase(String passPhrase, int timeout);

    /**
     * Changes the wallet passphrase from <code>oldpassphrase</code> to
     * <code>newpassphrase</code>.
     *
     * @param oldPassPhrase
     * @param newPassPhrase
     * @return {@link VoidResponse}
     */
    VoidResponse walletPassPhraseChange(String oldPassPhrase,
                                        String newPassPhrase);
}
