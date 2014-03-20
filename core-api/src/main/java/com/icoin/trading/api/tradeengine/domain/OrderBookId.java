/*
 * Copyright (c) 2012. Axon Framework
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.icoin.trading.api.tradeengine.domain;

import org.axonframework.domain.IdentifierFactory;

import java.io.Serializable;

import static com.homhon.util.Asserts.notNull;

/**
 * @author Jettro Coenradie
 */
public class OrderBookId implements Serializable {
    private static final long serialVersionUID = -7842002574176005113L;

    private String identifier;

    public OrderBookId() {
        this.identifier = IdentifierFactory.getInstance().generateIdentifier();
    }

    public OrderBookId(String identifier) {
        notNull(identifier, "Identifier may not be null");
        this.identifier = identifier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OrderBookId that = (OrderBookId) o;

        return identifier.equals(that.identifier);

    }

    @Override
    public int hashCode() {
        return identifier.hashCode();
    }

    @Override
    public String toString() {
        return identifier;
    }
}
