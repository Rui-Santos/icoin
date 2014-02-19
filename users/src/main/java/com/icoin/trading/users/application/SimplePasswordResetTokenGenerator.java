package com.icoin.trading.users.application;

import com.icoin.trading.users.domain.PasswordResetTokenGenerator;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

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
@Service
public class SimplePasswordResetTokenGenerator implements PasswordResetTokenGenerator {
    private static final char[] HEX = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
    };

    private final MessageDigest digest;

    private static char[] hex(byte[] bytes) {
        final int nBytes = bytes.length;
        char[] result = new char[2 * nBytes];

        int j = 0;
        for (int i = 0; i < nBytes; i++) {
            // Char for top 4 bits
            result[j++] = HEX[(0xF0 & bytes[i]) >>> 4];
            // Bottom 4
            result[j++] = HEX[(0x0F & bytes[i])];
        }

        return result;
    }

    public SimplePasswordResetTokenGenerator() {
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
            return new String(hex(generated));
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("No such encoding", e);
        }
    }
}