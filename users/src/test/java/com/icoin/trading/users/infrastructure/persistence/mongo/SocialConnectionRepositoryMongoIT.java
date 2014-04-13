package com.icoin.trading.users.infrastructure.persistence.mongo;

import com.icoin.trading.users.domain.model.social.SocialConnection;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 13-6-27
 * Time: AM12:17
 * To change this template use File | Settings | File Templates.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:META-INF/spring/users-persistence-mongo.xml")
@ActiveProfiles("dev")
public class SocialConnectionRepositoryMongoIT {
    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private MongoTemplate mongoOps;

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private SocialConnectionRepositoryMongo repository;

    private SocialConnection jeffrey, joeyTwitter, joeyFacebook, johnny, tommy, cj;

    @Before
    public void setup() {
        mongoOps.remove(new Query(), SocialConnection.class);
        jeffrey = create("joey", "twitter", "@JeffreyHyman", "joey r.", 2);
        joeyTwitter = create("joey", "twitter", "@joey_ramones", "joey r.", 1);
        joeyFacebook = create("joey", "facebook", "joey.ramones", "joey r.", 1);
        johnny = create("johnny", "facebook", "JohnnyRamones", "johnny r.", 1);
        tommy = create("tommy", "twitter", "@joey_ramones", "joey r.", 1);
        cj = create("cj", "fake", "c-j", "cj", 1);
        List<SocialConnection> connections = Arrays.asList(
                jeffrey,
                joeyTwitter,
                joeyFacebook,
                johnny,
                tommy,
                cj
        );

        mongoOps.insert(connections, SocialConnection.class);
    }

    private SocialConnection create(String userId,
                                    String providerId,
                                    String providerUserId,
                                    String displayName,
                                    int rank) {

        SocialConnection c = new SocialConnection();
        c.setUserId(userId);
        c.setDisplayName(displayName);
        c.setProviderId(providerId);
        c.setProviderUserId(providerUserId);
        c.setRank(rank);
        return c;
    }

    @Test
    public void shouldReturnMultipleConnections() {
        Map<String, List<String>> map = new HashMap<String, List<String>>();
        map.put("twitter", Arrays.asList("@JeffreyHyman", "@joey_ramones"));
        map.put("facebook", Arrays.asList("joey.ramones"));

        List<SocialConnection> connections = repository.findConnections("joey", map);
        assertThat(connections, containsInAnyOrder(jeffrey, joeyTwitter, joeyFacebook));
    }

    @Test
    public void shouldReturnTheUserIds() {
        List<String> userIds = repository.findUserIds("twitter", "@joey_ramones");
        assertNotNull(userIds);

        assertThat(userIds, containsInAnyOrder(joeyTwitter.getUserId(), tommy.getUserId()));
    }

    @Test
    public void shouldReturnTheSetOfUserIds() {
        Set<String> providedIds = new HashSet<String>();
        providedIds.add("joey.ramones");
        providedIds.add("JohnnyRamones");

        Set<String> userIds = repository.findUserIds("facebook", providedIds);
        assertNotNull(userIds);
        assertEquals(2, userIds.size());
        assertEquals("[joey, johnny]", userIds.toString());
    }

    @Test
    public void shouldReturnTheDefaultRank() {
        int rank = repository.findMaxRank("deedee", "twitter");
        assertEquals(1, rank);
    }

    @Test
    public void shouldReturnTheMaxRankForAProvider() {
        int rank = repository.findMaxRank("joey", "twitter");
        assertEquals(3, rank);
    }

    @Test
    public void shouldReturnNullIfTheConnectionIsNotFound() {
        SocialConnection conn = repository.findConnection("a", "b", "c");
        assertNull(conn);
    }

    @Test
    public void shouldFindPrimaryConnection() {
        SocialConnection conn = repository.findPrimaryConnection("joey", "twitter");
        assertNotNull("Connection not found", conn);
        assertEquals("twitter", conn.getProviderId());
        assertEquals("@joey_ramones", conn.getProviderUserId());
    }

    @Test
    public void shouldFindConnection() {
        SocialConnection conn = repository.findConnection("joey", "facebook", "joey.ramones");
        assertThat(conn, notNullValue());
        assertThat(conn.getProviderId(), equalTo("facebook"));
        assertThat(conn.getProviderUserId(), equalTo("joey.ramones"));
    }

    @Test
    public void shouldListTheConnectionsForUserAndProviderSortByRank() {
        List<SocialConnection> connections =
                repository.findConnections("joey", "twitter");

        assertThat(connections, containsInAnyOrder(joeyTwitter, jeffrey));
    }

    @Test
    public void shouldListTheConnectionsForUserSortByProviderAndRank() {
        List<SocialConnection> connections =
                repository.findConnections("joey");

        assertThat(connections, containsInAnyOrder(joeyTwitter, jeffrey, joeyFacebook));
    }

    @Test
    public void shouldUpdateTheConnection() {
        //SocialConnection userConn = factory.createConnection("cj", "cj");

        SocialConnection conn = repository.findConnection("joey", "twitter", "@JeffreyHyman");
        assertEquals("joey r.", conn.getDisplayName());

        repository.save(conn);

        SocialConnection conn2 = repository.findConnection("joey", "twitter", "@JeffreyHyman");
        assertEquals("joey r.", conn2.getDisplayName());
    }

    @Test
    public void shouldRemoveTheConnection() {
        repository.remove("joey", "twitter", "@JeffreyHyman");

        SocialConnection conn = repository.findConnection("joey", "twitter", "@JeffreyHyman");
        assertNull("Connection not removed", conn);
    }

    @Test
    public void shouldRemoveTheConnectionForAProvider() {
        repository.remove("joey", "twitter");

        List<SocialConnection> conn = repository.findConnections("joey", "twitter");
        assertEquals(0, conn.size());
    }
}
