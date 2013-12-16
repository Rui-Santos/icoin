package com.icoin.trading.tradeengine.infrastructure.persistence.mongo.converters;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.axonframework.serializer.SerializationException;
import org.joda.money.BigMoney;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 13-12-15
 * Time: AM11:32
 * To change this template use File | Settings | File Templates.
 */
public final class JodaMoneyConverter implements Converter {

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canConvert(Class type) {
        return type != null && BigMoney.class.getPackage().equals(type.getPackage());
    }

    @Override
    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
        writer.setValue(source.toString());
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        final Class requiredType = context.getRequiredType();
        try {
            if (BigMoney.class.isAssignableFrom(requiredType)) {
                return BigMoney.parse(reader.getValue());
            } else if (Money.class.isAssignableFrom(requiredType)) {
                return Money.parse(reader.getValue());
            } else if (CurrencyUnit.class.isAssignableFrom(requiredType)) {
                return CurrencyUnit.of(reader.getValue());
            }

            throw new SerializationException(String.format("Cannot support deserializing Type : %s", requiredType.getSimpleName()));
        } catch (Exception e) { // NOSONAR
            throw new SerializationException(String.format(
                    "An exception occurred while deserializing a Joda Money object: %s",
                    requiredType.getSimpleName()), e);
        }
    }
}
