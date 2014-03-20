package com.icoin.trading.tradeengine.infrastructure.listener;

import com.icoin.money.converter.JodaMoneyConverter;
import com.icoin.trading.model.ExecutedTrade;
import com.icoin.trading.model.TradeType;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.json.JettisonMappedXmlDriver;
import org.apache.cxf.jaxrs.client.WebClient;
import org.joda.money.BigMoney;
import org.joda.money.CurrencyUnit;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static com.homhon.util.TimeUtils.currentTime;

public final class ExecutedExternalListener {

    private static final String CLIENT_CONFIG_FILE = "com/icoin/ws/client/ClientConfig.xml";
    private static final String BASE_SERVICE_URL =
            "https://localhost:9000/customerservice/customers";


    private ExecutedExternalListener() {
    }

    public static void main(String args[]) throws Exception {
        post();
    }

    public static void post() throws Exception {
        XStream xStream = new XStream(new JettisonMappedXmlDriver());
//        XStream xStream = new XStream(new JsonHierarchicalStreamDriver());
        xStream.setMode(XStream.NO_REFERENCES);
        xStream.alias("ExecutedTrade", ExecutedTrade.class);
        xStream.registerConverter(new JodaMoneyConverter());
        try {
//            System.out.println(JAXBContext.newInstance(ExecutedTrade.class).getClass());
            //JSONProvider jsonProvider = new JSONProvider();

            System.out.println("\n\nSending HTTPS POST request to add customer");
            WebClient wc = WebClient.create(BASE_SERVICE_URL, CLIENT_CONFIG_FILE);
            ExecutedTrade executedTrade = new ExecutedTrade();
            executedTrade.setCoinId("BTC");
            executedTrade.setBuyCommission(BigMoney.of(CurrencyUnit.of("XPM"), 10.8764));
            executedTrade.setTradeTime(currentTime());
            executedTrade.setTradeType(TradeType.SELL);

            wc.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
            wc.type(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON);

            Response resp = wc.post(xStream.toXML(executedTrade));


            System.out.println(xStream.toXML(executedTrade));
            System.out.println(resp.getStatus());

            System.out.println("\n");
        } finally {

        }
    }

}

