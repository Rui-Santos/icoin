/*
 * Copyright (c) 2010-2012. Axon Framework
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

package com.icoin.trading.tradeengine.query.transaction;

import com.homhon.base.domain.ValueObject;

/**
 * @author Jettro Coenradie
 */
public enum TransactionState implements ValueObject<TransactionState> {
    STARTED, CONFIRMED, CANCELLED, EXECUTED, PARTIALLY_EXECUTED;

    @Override
    public boolean sameValueAs(TransactionState transactionState) {
        return this == transactionState;
    }

    @Override
    public TransactionState copy() {
        return this;
    }
}
