package com.icoin.trading.tradeengine.infrastructure.persistence.mongo;

import com.icoin.trading.tradeengine.domain.model.order.Order;
import com.icoin.trading.tradeengine.domain.model.order.OrderRepository;
import org.springframework.data.repository.CrudRepository;

/**
 * Created with IntelliJ IDEA.
 * User: jihual
 * Date: 1/10/14
 * Time: 12:30 PM
 * To change this template use File | Settings | File Templates.
 */
public interface OrderRepositoryMongo extends OrderRepository,
        OrderRepositoryMongoCustom,
        CrudRepository<Order, String> {
} 