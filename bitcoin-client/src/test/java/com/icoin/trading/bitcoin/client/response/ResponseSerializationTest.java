package com.icoin.trading.bitcoin.client.response;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-3-24
 * Time: PM11:12
 * To change this template use File | Settings | File Templates.
 */

import com.fasterxml.jackson.databind.ObjectMapper;
import com.icoin.trading.bitcoin.client.JsonExtra;
import org.apache.commons.io.IOUtils;
import org.hamcrest.Matcher;
import org.junit.Test;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.util.Map;

import static com.icoin.trading.bitcoin.client.Tests.jsonResource;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * Tests json serialization and deserialization of response objects.
 *
 * @author Claus Nielsen
 */
public class ResponseSerializationTest {

    private ObjectMapper objectMapper = new ObjectMapper();


    /**
     * Reveals a bug in Jackson.
     *
     * @throws Exception
     * @JsonUnwrapped conflicts with @JsonAnySetter/@JsonAnyGetter.
     * See https://github.com/FasterXML/jackson-annotations/issues/10
     * <p/>
     * Some serialization tests have been disabled by prefixing some
     * of the sample file names with an underscore.
     * <p/>
     * When the Jackson bug has been fixed remove this test and it's
     * resource file "_DOUBLE_UNWRAP.json" and re-enable other tests
     * by removing the underscore prefix from sample file names.
     */
    @Test
    public void testDoubleUnwrapped() throws Exception {

//        mockServer()
//                .expect(requestTo(Tests.url + "account/rate_limit_status.json"))
//                .andExpect(method(GET))
//                .andRespond(withSuccess(jsonResource("rateLimitStatus"), MediaType.APPLICATION_JSON));

        final Resource resource = jsonResource("special/_DOUBLE_UNWRAP");
        String jsonSample = IOUtils.toString(resource.getInputStream());
        ListUnspentResult obj = objectMapper.readValue(jsonSample, ListUnspentResult.class);
        String roundtrippedJson = objectMapper.writeValueAsString(obj);
        ListUnspentResult obj2 = objectMapper.readValue(roundtrippedJson, ListUnspentResult.class);
        assertThat("json -> obj -> json roundtrip serialization failed for " + resource.getFilename() + ".",
                obj, (Matcher) equalTo(obj2));
    }

    /**
     * Performs serializatio test for all json sample files in
     * src/test/resources/sampleResponse/.
     * <p/>
     * Tests that each json sample can be deserialized to the type also given in
     * the file name, and serialized back to the original.<br>
     * Also checks that all fields are mapped explicitly (ie. that "otherFilds"
     * isn't used).
     * <p/>
     * Skips files with a name beginning with an underscore are skipped, making
     * it possible to (temporarily) disable serialization tests of some samples.
     * <p/>
     * Following response samples are currently skipped:
     * <dl>
     * <dt>_DOUBLE_UNWRAP.json</dt>
     * <dd>Not a real response sample, but a constructed json objects which
     * helps check if an Jackson bug has been fixed. See
     * {@link #testDoubleUnwrapped()}.</dd>
     * <dt>_GetAddedNodeInfo</dt>
     * <dd>The bitcoind getaddednodeinfo method returns an array or an object
     * depending on the input. I think that is a bug - see
     * https://github.com/bitcoin/bitcoin/issues/2467. If that is accepted and
     * fixed the sample should be changed. If the bug report is rejected the
     * current sample is correct and the BitcoindClient code should be fixed.</dd>
     * <dt>_ListAccountsResponse.json</dt>
     * <dd>ListAccountsResult stores account balances in a map which doesn't
     * preserve order, and so we cannot do roundtrip serialization to the exact
     * same json. When we try the serialization output IS equivalent to the
     * sample, so you could argue that it's bug in the test.</dd>
     * <dt>_ListUnspentResponse.json</dt>
     * <dd>ListUnspentResult is not yet finished - it should be shortly.</dd>
     * <dt>_ListAddressGroupingsResponse.json</dt>
     * <dd>BigDecimal values are serialized using scientific notation.<br>
     * When jackson-databind commit 8a8322b493fe67059d8a46718dde8185266c8c0c
     * "Added serialization feature for writing BigDecimal in plain form" is
     * included in a Jackson release this should be fairly easy to fix.</dd>
     * </dl>
     *
     * @throws Exception
     */
    @Test
    public void testSerialization() throws Exception {
        ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
        Resource[] dataResources = resourcePatternResolver
                .getResources("classpath*:com/icoin/trading/bitcoin/client/data/*.json");
        for (Resource resource : dataResources) {
            doSerializationRoundtrip(resource);
        }
    }

    /**
     * Perform serialization roundtrip testing of the given file.
     *
     * @param resource - file with sample data in json format. The file name must
     *                 follow a fixed format including the respons class name - see
     *                 {@link #extractResponseClassName(Resource)}.
     * @throws Exception
     */
    private void doSerializationRoundtrip(Resource resource) throws Exception {

        String className = extractResponseClassName(resource);
        String jsonSample = IOUtils.toString(resource.getInputStream());
        @SuppressWarnings("unchecked")
        Class<? extends BitcoinJsonRpcResponse<?>> responseType = (Class<? extends BitcoinJsonRpcResponse<?>>) Class.forName("com.icoin.trading.bitcoin.client.response." + className);

        // Deserialize and re-serialize
        BitcoinJsonRpcResponse<?> response = objectMapper.readValue(jsonSample, responseType);
        String roundtrippedJson = objectMapper.writeValueAsString(response);
        BitcoinJsonRpcResponse<?> response2 = objectMapper.readValue(roundtrippedJson, responseType);
        assertThat("json -> obj -> json roundtrip serialization failed for " + resource.getFilename() + ".",
                response, (Matcher) equalTo(response2));

        // Check that all fields are explicitly mapped.
        assertThat("Some fields in response not explicitly mapped (see otherFields): " + response.toString(), response.getOtherFields().size(), equalTo(0));

        Object resultObject = response.getResult();
        if (resultObject instanceof JsonExtra) {
            JsonExtra result = (JsonExtra) resultObject;
            Map<String, Object> otherFields = result.getOtherFields();
            if (otherFields.size() > 0) {
                StringBuilder sb = new StringBuilder("Some fields in result not explicitly mapped: ");
                int fieldNo = 0;
                for (String field : otherFields.keySet()) {
                    if (fieldNo++ > 0) sb.append(", ");
                    sb.append(field);
                }
                fail(sb.append(".").toString());
            }
        }
    }


    /**
     * Extracts the response class name from the name of the sample
     * file. Sample files must be named
     * &lt;ResponseClassName&gt;_&lt;test sequence or description&gt;.json,
     * where the _&lt;test sequence or description&gt; is optional.
     * It is used when there are more than one sample response of
     * the same response class.
     *
     * @param resource
     * @return String - response class name
     */
    private String extractResponseClassName(Resource resource) {
        String fileName = resource.getFilename();
        int endIndex = fileName.indexOf("_");
        if (endIndex == -1) endIndex = fileName.indexOf(".");
        return fileName.substring(0, endIndex);
    }


}