package com.icoin.trading.fee.infrastructure.coin;

import com.icoin.trading.bitcoin.client.BitcoinRpcOperations;
import com.icoin.trading.bitcoin.client.response.ValidateAddressResponse;
import com.icoin.trading.fee.domain.AddressService;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-3-26
 * Time: PM10:58
 * To change this template use File | Settings | File Templates.
 */
@Component
public class BitcoinAddressService implements AddressService {
    private BitcoinRpcOperations operations;

    @Override
    public boolean validate(String address) {
        final ValidateAddressResponse response = operations.validateAddress(address);
        return response.getResult().getValid();
    }

    @Override
    public String generate(String account) {
        return operations.getNewAddress(account).getResult();
    }

    public void setOperations(BitcoinRpcOperations operations) {
        this.operations = operations;
    }
}
