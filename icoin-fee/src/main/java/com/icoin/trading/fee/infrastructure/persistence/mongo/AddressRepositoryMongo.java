package com.icoin.trading.fee.infrastructure.persistence.mongo;

import com.homhon.base.domain.repository.GenericCrudRepository;
import com.icoin.trading.fee.domain.address.Address;
import com.icoin.trading.fee.domain.address.AddressRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-3-26
 * Time: PM10:40
 * To change this template use File | Settings | File Templates.
 */
public interface AddressRepositoryMongo extends AddressRepository,
        GenericCrudRepository<Address, String>,
        PagingAndSortingRepository<Address, String> {

    @Query(value = "{ 'picked' : true ")
    List<Address> findAllUnpicked();

    @Query(value = "{ 'picked' : true ")
    Address findOneUnpicked();
}
