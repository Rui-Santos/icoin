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

import com.icoin.trading.bitcoin.client.request.BitcoinJsonRpcRequest;
import com.icoin.trading.bitcoin.client.response.ListUnspentResponse;
import com.icoin.trading.bitcoin.client.response.ListUnspentResult;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

/**
 * BitcoindClient configuration for isolated unit testing.
 * <p/>
 * RestTemplate is mocked out, so no calls are actually made to bitcoind when
 * using this configuration, but requests are captured so that tests can verify
 * if bitcoind is called as expected.
 *
 * @author Claus Nielsen
 */
@Configuration
public class BitcoindClientTestConfig {

    //cocoo:mhDntXDMhjVGvvKCSaTrS5u8xPW6taE7W4
    //from:mu9swUt181rk3D4K9Kv2xSYrCTMUC8FFUS
    //received_init:mhF9kAXq8wz9uBE7vyi4ExgDjhnpUokhuW
    //test:mgbeN4hujE2NRq58mCWsfHf8WsfTPJRMXr
    //test1:mudPzKDeM9WpcE1ayPfKoV8g4dkBsLhiyZ
    //test3:mubYt7BdSsm37yFkMbhn2PZcCnkGemqV3p
    //to:mnrFZqV5pdyQAjyRXDWYmXbWppvAcQZEcv

    private static final String BITCOIND_HOST = "192.168.1.102";
    private static final int BITCOIND_PORT = 8333;
    private String url;

    @Bean
    public BitcoinRpcOperations getBitcoindClient() {
        url = "http://" + BITCOIND_HOST + ":" + BITCOIND_PORT;
        BitcoinRpcOperations bitcoinRpcOperations = new BitcoinRpcTemplate(url, getRestTemplate());
        return bitcoinRpcOperations;
    }

    @Bean
    public RestTemplate getRestTemplate() {
        final RestTemplate restTemplate = Mockito.mock(RestTemplate.class);

        final ListUnspentResult[] result = new ListUnspentResult[2];
        final ListUnspentResponse response =
                new ListUnspentResponse(result, null, null);
        result[1] = new ListUnspentResult();


        final BitcoinJsonRpcRequest listunspent = new BitcoinJsonRpcRequest("listunspent",
                Arrays.asList(Integer.valueOf(0), Integer.valueOf(999999), new String[0]));
        when(restTemplate.postForObject(eq(url),
                eq(listunspent),
                eq(ListUnspentResponse.class))).thenReturn(response);

        return restTemplate;
    }

}
