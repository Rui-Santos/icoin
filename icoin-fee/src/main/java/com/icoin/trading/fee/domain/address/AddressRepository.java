package com.icoin.trading.fee.domain.address;

import com.homhon.base.domain.repository.GenericCrudRepository;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-3-26
 * Time: PM10:15
 * To change this template use File | Settings | File Templates.
 */
public interface AddressRepository extends GenericCrudRepository<Address, String> {
    List<Address> findAllUnpicked();

    Address findOneUnpicked();

    Address findByAddress(String address);

    List<Address> findByAccount(String account);
}
