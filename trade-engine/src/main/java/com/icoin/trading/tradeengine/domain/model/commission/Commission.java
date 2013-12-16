/*
 * Copyright 2011-2012 the original author or authors.
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
package com.icoin.trading.tradeengine.domain.model.commission;

import com.homhon.base.domain.model.ValueObjectSupport;
import com.homhon.base.domain.model.probability.Probability;

/**
 * @author Slawek
 */
public class Commission<U> extends ValueObjectSupport<Commission> {
    private Probability commission;
    private U unit;
    private String description;


    public Commission(Probability commission, U unit, String description) {
        this.commission = commission;
        this.unit = unit;
        this.description = description;
    }

    public Probability getCommission() {
        return commission;
    }

    public U getUnit() {
        return unit;
    }

    public String getDescription() {
        return description;
    }
}
