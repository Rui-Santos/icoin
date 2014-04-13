package com.icoin.trading.infrastructure.joda;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.joda.time.DateTime;

import java.lang.reflect.Constructor;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-3-18
 * Time: PM9:26
 * To change this template use File | Settings | File Templates.
 */
public final class JodaTimeConverter implements Converter {

    @Override
    @SuppressWarnings("unchecked")
    public boolean canConvert(final Class type) {
        return (type != null) && DateTime.class.getPackage().equals(type.getPackage());
    }

    @Override
    public void marshal(final Object source, final HierarchicalStreamWriter writer,
                        final MarshallingContext context) {
        writer.setValue(source.toString());
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object unmarshal(final HierarchicalStreamReader reader,
                            final UnmarshallingContext context) {
        try {
            final Class requiredType = context.getRequiredType();
            final Constructor constructor = requiredType.getConstructor(Object.class);
            return constructor.newInstance(reader.getValue());
        } catch (final Exception e) {
            throw new RuntimeException(String.format(
                    "Exception while deserializing a Joda Time object: %s", context.getRequiredType().getSimpleName()), e);
        }
    }

}