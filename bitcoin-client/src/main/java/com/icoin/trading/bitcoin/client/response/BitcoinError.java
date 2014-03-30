package com.icoin.trading.bitcoin.client.response;


import com.icoin.trading.bitcoin.client.ValueObject;

/**
 * Error code and -message.
 */
public class BitcoinError extends ValueObject {

    private Integer code;
    private String message;

    private BitcoinError() {
    }

    public BitcoinError(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
