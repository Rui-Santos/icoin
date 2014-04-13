package com.icoin.trading.users.domain.model.social;

import com.homhon.mongo.domainsupport.modelsupport.entity.VersionedEntitySupport;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;

/**
 * The Mongodb collection for the spring social connections.
 *
 * @author Carlo P. Micieli
 */

@CompoundIndexes({
        @CompoundIndex(name = "connections_rank_idx", def = "{'userId': 1, 'providerId': 1, 'rank': 1}", unique = true),
        @CompoundIndex(name = "connections_primary_idx", def = "{'userId': 1, 'providerId': 1, 'providerUserId': 1}", unique = true)
})
public class SocialConnection extends VersionedEntitySupport<SocialConnection, String, Long> {
    private String userId;

    //    @NotEmpty
    String providerId;

    String providerUserId;

    //    @Range(min = 1, max = 2)
    int rank; //not null
    String displayName;
    String profileUrl;
    String imageUrl;

    //    @NotEmpty
    String accessToken;

    String secret;
    String refreshToken;
    Long expireTime;


    public String getUserId() {
        return userId;
    }


    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }


    public String getProviderUserId() {
        return providerUserId;
    }

    public void setProviderUserId(String providerUserId) {
        this.providerUserId = providerUserId;
    }


    public int getRank() {
        return rank;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }


    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }


    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }


    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }


    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }


    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }


    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }


    public Long getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Long expireTime) {
        this.expireTime = expireTime;
    }
}