package com.icoin.trading.users.domain.model.function;

import com.homhon.mongo.domainsupport.modelsupport.entity.AuditAwareEntitySupport;
import org.springframework.data.mongodb.core.index.Indexed;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: jihual
 * Date: 1/28/14
 * Time: 6:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class UserPasswordReset extends AuditAwareEntitySupport<UserPasswordReset, String, Long> {
    private String ip;
    @Indexed
    private String username;
    @Indexed
    private String userId;

    //need to reset each time in case of the change of email
    @Indexed
    private String email;
    //userid date random string
    @Indexed(unique = true)
    private String token;
    private Date expirationDate;
    private boolean used;


    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public boolean isUsed() {
        return used;
    }

    private void setUsed(boolean used) {
        this.used = used;
    }

    public void used() {
        used = true;
    }
}