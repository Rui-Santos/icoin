package com.icoin.trading.coin.interfaces.commission.ws;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-3-11
 * Time: PM11:54
 * To change this template use File | Settings | File Templates.
 */
//import java.io.FileInputStream;
//import java.security.KeyStore;
//
//import javax.ws.rs.core.Response;
//
//import com.icoin.trading.service.CommissionService;
//import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
//import org.apache.cxf.jaxrs.client.WebClient;
//import org.apache.http.HttpEntity;
//import org.apache.http.HttpResponse;
//import org.apache.http.client.methods.HttpGet;
//import org.apache.http.conn.scheme.Scheme;
//import org.apache.http.conn.ssl.SSLSocketFactory;
//import org.apache.http.impl.client.DefaultHttpClient;
//import org.apache.http.message.BasicHeader;

public final class CommissionServiceTest {

//    private static final String CLIENT_CONFIG_FILE = "com/icoin/coin/ClientConfig.xml";
//    private static final String BASE_SERVICE_URL =
//            "https://localhost:9008/customerservice/customers";
//
//    private CommissionServiceTest() {
//    }
//
//    public static void main(String args[]) throws Exception {
//        String keyStoreLoc = "/var/products/icoin/coin/src/main/resources/com/icoin/coin/privatekeys/clientKeystore.jks";
//
//        KeyStore keyStore = KeyStore.getInstance("JKS");
//        keyStore.load(new FileInputStream(keyStoreLoc), "cspass".toCharArray());
//
//	        /*
//	         * Send HTTP GET request to query customer info using portable HttpClient
//	         * object from Apache HttpComponents
//	         */
//        SSLSocketFactory sf = new SSLSocketFactory(keyStore, "ckpass", keyStore);
//        Scheme httpsScheme = new Scheme("https", 9008, sf);
//
//        System.out.println("Sending HTTPS GET request to query customer info");
//        DefaultHttpClient httpclient = new DefaultHttpClient();
//        httpclient.getConnectionManager().getSchemeRegistry().register(httpsScheme);
//        HttpGet httpget = new HttpGet(BASE_SERVICE_URL + "/123");
//        BasicHeader bh = new BasicHeader("Accept" , "text/xml");
//        httpget.addHeader(bh);
//        HttpResponse response = httpclient.execute(httpget);
//        HttpEntity entity = response.getEntity();
//        entity.writeTo(System.out);
//        httpclient.getConnectionManager().shutdown();

	        /*
             *  Send HTTP PUT request to update customer info, using CXF WebClient method
	         *  Note: if need to use basic authentication, use the WebClient.create(baseAddress,
	         *  username,password,configFile) variant, where configFile can be null if you're
	         *  not using certificates.
	         */
//        System.out.println("\n\nSending HTTPS PUT to update customer name");
//        WebClient wc = WebClient.create(BASE_SERVICE_URL, CLIENT_CONFIG_FILE);
//        Customer customer = new Customer();
//        customer.setId(123);
//        customer.setName("Mary");
//        Response resp = wc.put(customer);
//
//	        /*
//	         *  Send HTTP POST request to add customer, using JAXRSClientProxy
//	         *  Note: if need to use basic authentication, use the JAXRSClientFactory.create(baseAddress,
//	         *  username,password,configFile) variant, where configFile can be null if you're
//	         *  not using certificates.
//	         */
//        System.out.println("\n\nSending HTTPS POST request to add customer");
//        CommissionService proxy = JAXRSClientFactory.create(BASE_SERVICE_URL, CommissionService.class,
//                CLIENT_CONFIG_FILE);
//        customer = new Customer();
//        customer.setName("Jack");
//        resp = wc.post(customer);

//        System.out.println("\n");
//        System.exit(0);
//    }
}