/**
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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.icoin.trading.bitcoin.client.exception.InvalidAddressException;
import com.icoin.trading.bitcoin.client.request.AddNodeAction;
import com.icoin.trading.bitcoin.client.request.TemplateRequest;
import com.icoin.trading.bitcoin.client.response.*;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Test against running bitcoind.
 * <p/>
 * The tests in this class are meant to be tweaked to match a running
 * bitcoind and the addresses in it, and to be executed manually.
 * <p/>
 * They are <i>not</i> executed as part of the build or other
 * automated test.
 *
 * @author Claus Nielsen
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {BitcoinClientDefaultConfig.class})
//@ContextConfiguration(classes = {BitcoindClientTestConfig.class})
public class BitcoindClientIntegrationTest {

    @Autowired
    private BitcoinRpcOperations bc;

    private ObjectMapper om = new ObjectMapper();


    @Test
    public void testAddMultiSigAddress() throws Exception {
        List<String> keys = new ArrayList<String>();
        keys.add("mj3QxNUyp4Ry2pbbP19tznUAAPqFvDbRFq");
        StringResponse addMultiSigAddress = bc.addMultiSigAddress(1, keys, "ACCOUNT1");
        print(addMultiSigAddress);
    }


    @Test
    public void testAddNode() throws Exception {
        VoidResponse addNode = bc.addNode("faucet.bitcoin.st", AddNodeAction.ADD);
        print(addNode);
    }


    @Test
    public void testBackupWallet() throws Exception {
        VoidResponse backupWallet = bc.backupWallet("C:\\wallet.backup");
        print(backupWallet);
    }


    @Test
    public void testCreateMultiSig() throws Exception {
        CreateMultiSigResponse createMultiSig = bc.createMultiSig(1, new String[]{"mj3QxNUyp4Ry2pbbP19tznUAAPqFvDbRFq"});
        print(createMultiSig);
    }


    @Test
    public void testCreateRawTransaction() throws Exception {
        AddressAndAmount aaa = new AddressAndAmount("mhDntXDMhjVGvvKCSaTrS5u8xPW6taE7W4", BigDecimal.valueOf(1000000L, 8));
        List<TransactionOutputRef> txOuts = new ArrayList<TransactionOutputRef>();
        txOuts.add(new TransactionOutputRef("280acc1c3611fee83331465c715b0da2d10b65733a688ee2273fdcc7581f149b", 0));
        StringResponse createRawTransactionResponse = bc.createRawTransaction(txOuts, aaa, aaa);
        print(createRawTransactionResponse);

    }


    @Test
    public void testDecodeRawTransaction() throws Exception {
        DecodeRawTransactionResponse decodeRawTransactionResponse = bc.decodeRawTransaction("01000000019b141f58c7dc3f27e28e683a73650bd1a20d5b715c463133e8fe11361ccc0a280000000000ffffffff0100c2eb0b000000001976a91426ab1c83e2a8269b7007baf0244151cca4c5e3fd88ac00000000");
        print(decodeRawTransactionResponse);
    }


    @Test
    public void testDecodeRawTransaction_signedTransaction() throws Exception {
        DecodeRawTransactionResponse decodeRawTransactionResponse = bc.decodeRawTransaction("01000000019b141f58c7dc3f27e28e683a73650bd1a20d5b715c463133e8fe11361ccc0a28000000006a473044022011a55030de6225d16b0f0c8854a324cbbbf0f9ef92d1b0b18696b403d7c3ccbc0220331ad3f476ee016849185138e68ba33d29a684f0ec014cd7f05e3d406412b4c4012103b72d2e7dcf317a8d26e64172e80ac88754e31dad59ec25c2fbfdb082f0288aa6ffffffff0100c2eb0b000000001976a91426ab1c83e2a8269b7007baf0244151cca4c5e3fd88ac00000000");
        print(decodeRawTransactionResponse);
    }


    @Test
    public void testDumpPrivateKey() throws Exception {
        StringResponse dumpPrivateKeyResponse = bc.dumpPrivateKey("mxphxiG4Ggjb3bKFbeFnsuK2qde3541S93");
        print(dumpPrivateKeyResponse);
    }


    @Test
    public void testEncryptWallet() throws Exception {
        VoidResponse encryptWalletResponse = bc.encryptWallet("popidop");
        print(encryptWalletResponse);
    }


    @Test
    public void testGetAccount() throws Exception {
        StringResponse getAccountResponse = bc.getAccount("mgbeN4hujE2NRq58mCWsfHf8WsfTPJRMXr");
        print(getAccountResponse);
    }

    @Test
    public void testGetAccount2() throws Exception {
        StringResponse getAccountResponse = bc.getAccount("mgbeN4hujE2NRq58mCWsfHf8WsfTPJRMXr");
        print(getAccountResponse);
    }


    @Test
    public void testGetAccountAddress() throws Exception {
        StringResponse accountAddressResponse = bc.getAccountAddress("cocoo");
        print(accountAddressResponse);
    }

    @Test
    public void testGetAddedNodeInfo() throws Exception {
        bc.addNode("faucet.bitcoin.st", AddNodeAction.ONE_TRY);
        GetAddedNodeInfoResponse addedNodeInfoResponse = bc.getAddedNodeInfo(true, "faucet.bitcoin.st");
        print(addedNodeInfoResponse);
    }


    @Test
    public void testGetAddedNodeInfo_noDns() throws Exception {
        GetAddedNodeInfoResponse addedNodeInfoResponse = bc.getAddedNodeInfo(false, "faucet.bitcoin.st");
        print(addedNodeInfoResponse);
    }


    @Test
    public void testGetAddressesByAccount() throws Exception {
        StringArrayResponse addressesByAccount = bc.getAddressesByAccount("clanie");
        print(addressesByAccount);
    }


    @Test
    public void testGetBalance() throws Exception {
        BigDecimalResponse balance = bc.getBalance("clanie", null);
        print(balance);
    }


    @Test
    public void testGetBlock() throws Exception {
        GetBlockResponse block = bc.getBlock("00000000d0ef0ae127a9371bf7657a1dbd597a6e7dc9e46eea68b74e68602ffb");
        print(block);
    }


    @Test
    public void testGetBlockCount() throws Exception {
        LongResponse count = bc.getBlockCount();
        print(count);
    }


    @Test
    public void testGetBlockHash() throws Exception {
        StringResponse hash = bc.getBlockHash(4L);
        print(hash);
    }


    @Test
    public void testGetBlockTemplate() throws Exception {
        GetBlockTemplateResponse blockTemplate = bc.getBlockTemplate(new TemplateRequest(new String[]{"longpoll", "coinbasetxn", "coinbasevalue", "proposal", "serverlist", "workid"}, "template"));
        print(blockTemplate);
    }


    @Test
    public void testGetConnectionCount() throws Exception {
        IntegerResponse count = bc.getConnectionCount();
        print(count);
    }


    @Test
    public void testGetDifficulty() throws Exception {
        IntegerResponse count = bc.getDifficulty();
        print(count);
    }


    @Test
    public void testGetGenerate() throws Exception {
        BooleanResponse generate = bc.getGenerate();
        print(generate);
    }


    @Test
    public void testGetHashesPerSecond() throws Exception {
        LongResponse hps = bc.getHashesPerSecond();
        print(hps);
    }


    @Test
    public void testGetInfo() throws Exception {
        GetInfoResponse info = bc.getInfo();
        print(info);
    }


    @Test
    public void testGetMiningInfo() throws Exception {
        GetMiningInfoResponse info = bc.getMiningInfo();
        print(info);
    }


    @Test
    public void testGetNewAddress() throws Exception {
        StringResponse newAddress = bc.getNewAddress("BitEater");
        print(newAddress);
    }


    @Test
    public void testGetPeerInfo() throws Exception {
        GetPeerInfoResponse info = bc.getPeerInfo();
        print(info);
    }


    @Test
    public void testGetRawMemPool() throws Exception {
        StringArrayResponse response = bc.getRawMemPool();
        print(response);
    }


    @Test
    public void testGetRawTransaction() throws Exception {
        StringResponse response = bc.getRawTransaction("9922eee42642f603ffeb28575de81972ebd9defc2d44e74a45066ef4a47692be");
        print(response);
    }


    @Test
    public void testGetRawTransaction_verbose() throws Exception {
        GetRawTransactionResponse response = bc.getRawTransaction_verbose("9922eee42642f603ffeb28575de81972ebd9defc2d44e74a45066ef4a47692be");
        print(response);
    }


    @Test
    public void testGetReceivedByAccount() throws Exception {
        BigDecimalResponse received = bc.getReceivedByAccount("clanie", null);
        print(received);
    }


    @Test
    public void testGetReceivedByAddress() throws Exception {
        BigDecimalResponse received = bc.getReceivedByAddress("mj3QxNUyp4Ry2pbbP19tznUAAPqFvDbRFq", null);
        print(received);
    }


    @Test
    public void testGetTransaction() throws Exception {
        GetTransactionResponse transactionResponse = bc.getTransaction("9e8485aed75a0e0c8b1bbcda5f3e1426a7da914cb5732f73dd8bd6128344a608");
        print(transactionResponse);
    }


    @Test
    public void testGetTxOut() throws Exception {
        GetTxOutResponse txOut = bc.getTxOut("9e8485aed75a0e0c8b1bbcda5f3e1426a7da914cb5732f73dd8bd6128344a608", 1, null);
        print(txOut);
    }


    @Test
    public void testGetTxOutSetInfo() throws Exception {
        GetTxOutSetInfoResponse txOutSetInfo = bc.getTxOutSetInfo();
        print(txOutSetInfo);
    }


    @Test
    public void testGetWork() throws Exception {
        GetWorkResponse work = bc.getWork();
        print(work);
    }


    @Test
    public void testGetWork_withData() throws Exception {
        BooleanResponse work = bc.getWork("000000020bb8b12222df37797da7a14c1e2d94e259dce954c1afaab54c09e8ef00000000fd5eceea3634400d5e5ece722814e81a4f2f2f95c033059d4f57e341ee9fb4bb51613a361c0b106700000000000000800000000000000000000000000000000000000000000000000000000000000000000000000000000080020000");
        print(work);
    }


    @Test
    public void testHelp() throws Exception {
        StringResponse helpResponse = bc.help("sendmany");
        print(helpResponse);
    }


    @Test
    public void testHelp_listCommands() throws Exception {
        StringResponse helpResponse = bc.help(null);
        print(helpResponse);
    }

    @Ignore
    @Test
    public void testImportPrivateKey() throws Exception {
        VoidResponse importPrivateKeyResponse = bc.importPrivateKey("cV48j141Jf5nAdEftRxRbGGXGpzDixw94aDjJBYniidUAPbAQZfB", null, true);
        print(importPrivateKeyResponse);
    }


    @Test
    public void testKeyPoolRefill() throws Exception {
        VoidResponse response = bc.keyPoolRefill();
        print(response);
    }


    @Test
    public void testListAccounts() throws Exception {
        ListAccountsResponse listAccounts = bc.listAccounts(null);
        print(listAccounts);
    }


    @Test
    public void testListAddressGroupings() throws Exception {
        ListAddressGroupingsResponse addressGroupings = bc.listAddressGroupings();
        print(addressGroupings);
    }


    @Test
    public void testListLockUnspent() throws Exception {
        ListLockUnspentResponse lockUnspent = bc.listLockUnspent();
        print(lockUnspent);
    }


    @Test
    public void testListReceivedByAccount() throws Exception {
        ListReceivedByAccountResponse listReceivedByAccountResponse = bc.listReceivedByAccount(0, true);
        print(listReceivedByAccountResponse);
    }


    @Test
    public void testListReceivedByAddress() throws Exception {
        ListReceivedByAddressResponse listReceivedByAddressResponse = bc.listReceivedByAddress(0, true);
        print(listReceivedByAddressResponse);
    }


    @Test
    public void testListSinceBlock() throws Exception {
        ListSinceBlockResponse list = bc.listSinceBlock(null, null);
        print(list);
    }


    @Test
    public void testListTransactions() throws Exception {
        ListTransactionsResponse listTransactions = bc.listTransactions("clanie", null, null);
        print(listTransactions);
    }


    @Test
    public void testListUnspent() throws Exception {
        ListUnspentResponse listUnspentResponse = bc.listUnspent(0, 999999, "mj3QxNUyp4Ry2pbbP19tznUAAPqFvDbRFq", "mprSidR7coMDYzfnTXdq6taxDZyEb3fopo");
        print(listUnspentResponse);
    }


    @Test
    public void testLockUnspent() throws Exception {
        BooleanResponse response = bc.lockUnspent(true, new TransactionOutputRef[]{new TransactionOutputRef("0e4616e9d5d8270c219c8c675b35215ba80c137d7e588f6b54cfddc9d648dda8", 0)});
        print(response);
    }


    @Test
    public void testMove() throws Exception {
        BooleanResponse move = bc.move("clanie", "cn@cn-consult", BigDecimal.valueOf(0.1d), null, null);
        print(move);
    }


    @Test
    public void testSendFrom() throws Exception {
        StringResponse sendFrom = bc.sendFrom("clanie", "mwswEtw6t2ziSjsfip62FPg84NXGsJ5H2o", BigDecimal.valueOf(0.01d), 10, "Comment", "CommentTO");
        print(sendFrom);
    }


    @Test
    public void testSendMany() throws Exception {
        StringResponse sendMany = bc.sendMany("clanie", new AddressAndAmount[]{
                new AddressAndAmount("mrhz5ZgSF3C1BSdyCKt3gEdhKoRL5BNfJV", BigDecimal.valueOf(0.1d)),
                new AddressAndAmount("mwswEtw6t2ziSjsfip62FPg84NXGsJ5H2o", BigDecimal.valueOf(0.2d))
        }, null, null);
        print(sendMany);
    }


    @Test
    public void testSendRawTransaction() throws Exception {
        ListUnspentResponse listUnspentResponse = bc.listUnspent(0, 999999);

        ListUnspentResult[] unspent = listUnspentResponse.getResult();
        TransactionOutputRef[] txOutputs = new TransactionOutputRef[]{unspent[0].getTxRef(), unspent[1].getTxRef()};
        print(listUnspentResponse);
        StringResponse rawTransaction = bc.createRawTransaction(Arrays.asList(txOutputs),
                new AddressAndAmount("mrhz5ZgSF3C1BSdyCKt3gEdhKoRL5BNfJV", BigDecimal.valueOf(0.1d)),
                new AddressAndAmount("mwswEtw6t2ziSjsfip62FPg84NXGsJ5H2o", BigDecimal.valueOf(0.2d)));
        print(rawTransaction);
        SignRawTransactionResponse signedRawTransaction = bc.signRawTransaction(rawTransaction.getResult(), null, null, null);
        print(signedRawTransaction);
        StringResponse sendRawTransaction = bc.sendRawTransaction(signedRawTransaction.getResult().getHex());
        print(sendRawTransaction);
    }

    //tested for successful tx
    @Test
    public void testSendToAddress() throws Exception {
        //9f6a1ecef635c5ee80125b789d9fbe6d07f7b1bbd81595492eba7e7bbca96653
        StringResponse sendToAddress = bc.sendToAddress("mhDntXDMhjVGvvKCSaTrS5u8xPW6taE7W4", BigDecimal.valueOf(0.1d), "Comment", "CommentTO");
        print(sendToAddress);
    }

    //tested for failed tx
    @Test(expected = InvalidAddressException.class)
    public void testSendToInvalidAddress() throws Exception {
        //9f6a1ecef635c5ee80125b789d9fbe6d07f7b1bbd81595492eba7e7bbca96653
        StringResponse sendToAddress = bc.sendToAddress("mhDntXDMhjVGvvKCSaTrS5u8xPW6taE7W45", BigDecimal.valueOf(0.1d), "Comment", "CommentTO");
        print(sendToAddress);
    }

    //tested
    @Test
    public void testSetAccount() throws Exception {
        VoidResponse setAccount = bc.setAccount("mgbeN4hujE2NRq58mCWsfHf8WsfTPJRMXr", "test");
        print(setAccount);
    }


    @Test
    public void testSetGenerate() throws Exception {
        VoidResponse setGenerate = bc.setGenerate(false, null);
        print(setGenerate);
    }


    @Test
    public void testSetTxFee() throws Exception {
        BooleanResponse setTxFeeResponse = bc.setTxFee(BigDecimal.valueOf(0.00001d));
        print(setTxFeeResponse);
    }


    @Test
    public void testSignMessage() throws Exception {
        StringResponse signMessage = bc.signMessage("mj3QxNUyp4Ry2pbbP19tznUAAPqFvDbRFq", "We love Bitcoin");
        print(signMessage);
    }


    @Test
    public void testSignRawTransaction() throws Exception {
        SignRawTransactionResponse signRawTransaction = bc.signRawTransaction("01000000019b141f58c7dc3f27e28e683a73650bd1a20d5b715c463133e8fe11361ccc0a280000000000ffffffff0100c2eb0b000000001976a91426ab1c83e2a8269b7007baf0244151cca4c5e3fd88ac00000000", null, null, null);
        print(signRawTransaction);
    }


    @Test
    public void testStop() throws Exception {
        VoidResponse stopResponse = bc.stop();
        print(stopResponse);
    }


    // TODO submitblock <hex data> [optional-params-obj]


    @Test
    public void testValidateAddress() throws Exception {
        ValidateAddressResponse validateAddressResponse = bc.validateAddress("mj3QxNUyp4Ry2pbbP19tznUAAPqFvDbRFq");
        print(validateAddressResponse);
    }


    //tested
    @Test
    public void testValidateAddress_invalid() throws Exception {
        ValidateAddressResponse validateAddressResponse = bc.validateAddress("2j3QxNUyp4Ry2pbbP19tznUAAPqFvDbRFq");
        print(validateAddressResponse);
    }

    //tested
    @Test
    public void testVerifyMessage() throws Exception {
        BooleanResponse verifyMessage = bc.verifyMessage("mj3QxNUyp4Ry2pbbP19tznUAAPqFvDbRFq", "IPmHnxzFa8bKD0Tt/0uT+3ak+8g+ToxEhivc49ciJgA3wuQWSMyc2OdTL/AooRXQ7qtCMkp4NXZ/dw0vBI6fPAs=", "We love Bitcoin");
        print(verifyMessage);
    }


    @Test
    public void testWalletLock() throws Exception {
        VoidResponse walletLockResponse = bc.walletLock();
        print(walletLockResponse);
    }


    @Test
    public void testWalletPassPhrase() throws Exception {
        VoidResponse walletPassPhraseResponse = bc.walletPassPhrase("popidop", 99999999);
        print(walletPassPhraseResponse);
    }


    @Test
    public void testWalletPassPhraseChange() throws Exception {
        VoidResponse walletPassPhraseChangeResponse = bc.walletPassPhraseChange("tiotop", "popidop");
        print(walletPassPhraseChangeResponse);
    }


    /**
     * Prints the given object as json.
     *
     * @param o - object to print.
     * @throws Exception
     */
    private void print(Object o) throws Exception {
        System.out.println(om.writeValueAsString(o));
    }
}
