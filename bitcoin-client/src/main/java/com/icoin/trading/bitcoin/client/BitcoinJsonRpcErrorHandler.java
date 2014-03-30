/*
 * Copyright (C) 2013, Claus Nielsen, cn@cn-consult.dk
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package com.icoin.trading.bitcoin.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.icoin.trading.bitcoin.client.exception.BitcoinClientException;
import com.icoin.trading.bitcoin.client.exception.BitcoinException;
import com.icoin.trading.bitcoin.client.exception.BitcoinServerException;
import com.icoin.trading.bitcoin.client.exception.InvalidAddressException;
import com.icoin.trading.bitcoin.client.exception.MethodNotFoundException;
import com.icoin.trading.bitcoin.client.exception.WalletEncryptionException;
import com.icoin.trading.bitcoin.client.response.BitcoinErrorResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.UnknownHttpStatusCodeException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * Handles error responses from bitcoind.
 * <p/>
 * When bitcoind returns an client- or server-error response (an HTTP 4xx
 * or 5xx response) this handler will throw an BitcoinClient- or
 * BitcoinServerException including the response body as an {@link BitcoinErrorResponse}.<br>
 * If the response body isn't valid JSON, or if parsing it fails for
 * any reason, an BitcoinException is thrown. It will indicate which
 * HTTP status code was received.
 * <p/>
 * In case of other HTTP errors an BitcoinException is also thrown. It
 * will <b>not</b> include the response body, but it will include
 * whatever exception Spring's {@link DefaultResponseErrorHandler} would
 * have thrown as it's cause.
 *
 * @author Claus Nielsen
 */
public class BitcoinJsonRpcErrorHandler extends DefaultResponseErrorHandler {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public void handleError(ClientHttpResponse response) throws IOException {
        HttpStatus statusCode = getHttpStatusCode(response);
        switch (statusCode.series()) {
            case SERVER_ERROR: {
                BitcoinErrorResponse errorResponse = parseResponse(response, statusCode);
                switch (errorResponse.getError().getCode()) {
                    // Comments are observed error messages for each code.
                    case -1:
                        // a multisignature address must require at least one key to redeem
                        // no full public key for address <bitcoinaddress>
                        // createrawtransaction [{\"txid\":txid,\"vout\":n},...] {address:amount,...}\nCreate a transaction ...
                        throw new BitcoinServerException(errorResponse);
                    case -4:
                        // Private key for address <bitcoinaddress> is not known
                        // Wallet backup failed!
                        // Error adding key to wallet
                        throw new BitcoinServerException(errorResponse);
                    case -5:
                        // Invalid Bitcoin address
                        throw new InvalidAddressException(errorResponse);
                    case -13:
                        // Error: Please enter the wallet passphrase with walletpassphrase first.
                        throw new BitcoinServerException(errorResponse);
                    case -14:
                        // Error: The wallet passphrase entered was incorrect.
                        throw new BitcoinServerException(errorResponse);
                    case -15:
                        // Error: running with an unencrypted wallet, but walletpassphrasechange was called.
                        // Error: running with an encrypted wallet, but encryptwallet was called.
                        throw new WalletEncryptionException(errorResponse);
                    case -17:
                        // Error: Wallet is already unlocked.
                        throw new BitcoinServerException(errorResponse);
                    default:
                        throw new BitcoinServerException(errorResponse);
                }
            }
            case CLIENT_ERROR: {
                BitcoinErrorResponse errorResponse = parseResponse(response, statusCode);
                switch (errorResponse.getError().getCode()) {
                    case -32601:
                        // Method not found
                        throw new MethodNotFoundException(errorResponse);
                    default:
                        throw new BitcoinClientException(errorResponse);
                }
            }
            default:
                try {
                    super.handleError(response);
                } catch (Exception cause) {
                    throw new BitcoinException(cause);
                }
        }
    }


    /**
     * Parses the response body, deserializing it into an BitcoinJsonRpcErrorResponse object.
     * <p/>
     * If parsing fails an BitcoinException containing the given HTTP error code is thrown.
     *
     * @param response
     * @param statusCode
     * @return BitcoinJsonRpcErrorResponse
     * @throws BitcoinException
     */
    private BitcoinErrorResponse parseResponse(ClientHttpResponse response, HttpStatus statusCode) {
        String body = new String(getResponseBody(response), getCharset(response));
        try {
            return objectMapper.readValue(body, BitcoinErrorResponse.class);
        } catch (IOException ioe) {
            throw new BitcoinException("Received an HTTP " + statusCode.value() + " " + statusCode.getReasonPhrase() + ". Response parsing failed.", ioe);
        }
    }


    private HttpStatus getHttpStatusCode(ClientHttpResponse response) throws IOException {
        HttpStatus statusCode;
        try {
            statusCode = response.getStatusCode();
        } catch (IllegalArgumentException ex) {
            throw new UnknownHttpStatusCodeException(response.getRawStatusCode(),
                    response.getStatusText(), response.getHeaders(), getResponseBody(response), getCharset(response));
        }
        return statusCode;
    }


    private byte[] getResponseBody(ClientHttpResponse response) {
        try {
            InputStream responseBody = response.getBody();
            if (responseBody != null) {
                return FileCopyUtils.copyToByteArray(responseBody);
            }
        } catch (IOException ex) {
            // ignore
        }
        return new byte[0];
    }


    private Charset getCharset(ClientHttpResponse response) {
        HttpHeaders headers = response.getHeaders();
        MediaType contentType = headers.getContentType();
        Charset charset = null;
        if (contentType != null) charset = contentType.getCharSet();
        if (charset == null) charset = Charset.forName("ISO-8859-1");
        return charset;
    }


}
