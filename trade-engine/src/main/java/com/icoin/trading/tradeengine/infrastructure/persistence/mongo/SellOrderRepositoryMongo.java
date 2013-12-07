package com.icoin.trading.tradeengine.infrastructure.persistence.mongo;

import com.icoin.trading.tradeengine.domain.model.order.SellOrder;
import com.icoin.trading.tradeengine.domain.model.order.SellOrderRepository;
import org.springframework.data.repository.CrudRepository;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 13-12-6
 * Time: PM9:35
 * To change this template use File | Settings | File Templates.
 */
@SuppressWarnings("unused")
public interface SellOrderRepositoryMongo extends SellOrderRepository,
        SellOrderRepositoryMongoCustom,
        CrudRepository<SellOrder, String> {
}
