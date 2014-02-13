package com.icoin.trading.users.application;

import com.icoin.trading.users.domain.PasswordResetTokenGenerator;
import org.apache.commons.lang3.RandomStringUtils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import static com.homhon.util.Asserts.hasLength;
import static com.homhon.util.Asserts.notNull;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-2-12
 * Time: PM10:09
 * To change this template use File | Settings | File Templates.
 */
public class SimplePasswordResetTokenGenerator implements PasswordResetTokenGenerator {

    private final MessageDigest digest;

    private SimplePasswordResetTokenGenerator() {
        try {
            this.digest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("No such hashing algorithm", e);
        }
    }

    @Override
    public String generate(String userName, String ip, Date date) {
        hasLength(userName);
        notNull(date);

        String randomString = RandomStringUtils.randomAlphanumeric(32);

        try {
            String s = randomString + userName + date.getTime() + ip;
            byte[] bytes = s.getBytes("UTF-8");
            byte[] generated = digest.digest(bytes);
            return new String(generated);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("No such encoding", e);
        }
    }
}
