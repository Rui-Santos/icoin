package com.icoin.trading.bitcoin.client;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-3-24
 * Time: PM11:19
 * To change this template use File | Settings | File Templates.
 */
public class Tests {

    public static final String url = "http://localhost:4003";

    public static Resource jsonResource(String file) {
        ClassPathResource classPathResource =
                new ClassPathResource("/com/icoin/trading/bitcoin/client/data/" + file + ".json");
        return classPathResource;
    }

    public static MockRestServiceServer mockServer() {
        final BitcoinRpcTemplate template = new BitcoinRpcTemplate(url, new RestTemplate());
        return MockRestServiceServer.createServer(template.getRestTemplate());
    }

}
