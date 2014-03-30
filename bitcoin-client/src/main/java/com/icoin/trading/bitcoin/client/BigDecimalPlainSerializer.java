package com.icoin.trading.bitcoin.client;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.math.BigDecimal;

/**
 * Serialize BigDecimals using its toPlainString method. Avoids having
 * BigDecimal values written in scientific notation, which bitcoind doesn't
 * accept.
 */
public class BigDecimalPlainSerializer extends JsonSerializer<BigDecimal> {

    @Override
    public void serialize(BigDecimal value, JsonGenerator jgen,
                          SerializerProvider provider) throws IOException,
            JsonProcessingException {
        jgen.writeNumber(value.toPlainString());
    }

}
