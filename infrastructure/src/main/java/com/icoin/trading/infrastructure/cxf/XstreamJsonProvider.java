package com.icoin.trading.infrastructure.cxf;

import com.icoin.money.converter.JodaMoneyConverter;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.json.JettisonMappedXmlDriver;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-3-13
 * Time: PM4:02
 * To change this template use File | Settings | File Templates.
 */
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
@Provider
public class XStreamJsonProvider implements MessageBodyReader<Object>,
        MessageBodyWriter<Object> {
    private static final String DEFAULT_ENCODING = "utf-8";

    @Override
    public boolean isWriteable(Class<?> type, Type genericType,
                               Annotation[] annotations, MediaType mediaType) {
        if (!(MediaType.APPLICATION_JSON_TYPE.isCompatible(mediaType) || MediaType.APPLICATION_OCTET_STREAM_TYPE
                .equals(mediaType))) {
            return false;
        }

        return true;
    }

    @Override
    public long getSize(Object t, Class<?> type, Type genericType,
                        Annotation[] annotations, MediaType mediaType) {
        // I'm being lazy - should compute the actual size
        return -1;
    }


    @Override
    public boolean isReadable(final Class<?> type, final Type genericType, final Annotation[] annotations,
                              final MediaType mediaType) {
        if (!MediaType.APPLICATION_JSON_TYPE.isCompatible(mediaType)) {
            return false;
        }
        // return type.getAnnotation(XStreamAlias.class) != null;
        // return xstream != null;
        return true;
    }

    protected static String getCharsetAsString(final MediaType m) {
        if (m == null) {
            return DEFAULT_ENCODING;
        }
        String result = m.getParameters().get("charset");
        return (result == null) ? DEFAULT_ENCODING : result;
    }

    @Override
    public Object readFrom(Class<Object> type, Type genericType,
                           Annotation[] annotations, MediaType mediaType,
                           MultivaluedMap<String, String> httpHeaders, InputStream stream)
            throws IOException, WebApplicationException {
        // add error handling, etc.
        String encoding = getCharsetAsString(mediaType);
        XStream xStream = new XStream(new JettisonMappedXmlDriver());
        xStream.registerConverter(new JodaMoneyConverter());
        return xStream.fromXML(new InputStreamReader(stream, encoding));
    }

    @Override
    public void writeTo(Object o, Class<?> type, Type genericType,
                        Annotation[] annotations, MediaType mediaType,
                        MultivaluedMap<String, Object> httpHeaders, OutputStream stream)
            throws IOException, WebApplicationException {
        // deal with thread safe use of xstream, etc.
        String encoding = getCharsetAsString(mediaType);
        XStream xStream = new XStream(new JettisonMappedXmlDriver());
        xStream.setMode(XStream.NO_REFERENCES);
        xStream.registerConverter(new JodaMoneyConverter());
        // add safer encoding, error handling, etc.
        xStream.toXML(o, new OutputStreamWriter(stream, encoding));
    }
}

