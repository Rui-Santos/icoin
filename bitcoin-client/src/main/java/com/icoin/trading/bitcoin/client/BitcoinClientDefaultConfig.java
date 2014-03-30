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

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

/**
 * Default BitcoinRpcClient configuration.
 * <p/>
 * Prerequisite properties:
 * <bl>
 * <li>bitcoind.client.host</li>
 * <li>bitcoind.client.port</li>
 * <li>bitcoind.client.user</li>
 * <li>bitcoind.client.passwor</li>
 * </bl>
 *
 * @author Claus Nielsen
 */
@Configuration
@PropertySource("classpath:/com/icoin/trading/bitcoin/client/bitcoin-client.properties")
public class BitcoinClientDefaultConfig {

    @Value("${bitcoin.client.host}")
    private String host;

    @Value("${bitcoin.client.port}")
    private String port;

    @Value("${bitcoin.client.user}")
    private String user;

    @Value("${bitcoin.client.password}")
    private String password;


    @Bean
    public BitcoinRpcOperations bitcoindClient() {
        BitcoinRpcOperations bitcoindClient = new BitcoinRpcTemplate("http://" + host + ":" + port, restTemplate());
        return bitcoindClient;
    }


    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(requestFactory());
        restTemplate.setErrorHandler(errorHandler());
        return restTemplate;
    }


    private ResponseErrorHandler errorHandler() {
        return new BitcoinJsonRpcErrorHandler();
    }


    private ClientHttpRequestFactory requestFactory() {
        return new HttpComponentsClientHttpRequestFactory(httpClient());
    }


    private HttpClient httpClient() {
        DefaultHttpClient httpClient = new DefaultHttpClient();
        httpClient.setCredentialsProvider(credentialsProvicer());
        return httpClient;
    }


    private CredentialsProvider credentialsProvicer() {
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(
                new AuthScope(host, Integer.valueOf(port)),
                new UsernamePasswordCredentials(user, password));
        return credsProvider;
    }


    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
}
