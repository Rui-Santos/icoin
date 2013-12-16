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

package com.icoin.trading.tradeengine;

import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.domain.GenericMessage;
import org.hamcrest.BaseMatcher;

import static org.apache.commons.lang3.Validate.notNull;

/**
 * @author Jettro Coenradie
 */
public abstract class GenericMatcher<T> extends BaseMatcher<T> {
    protected Class<T> type;

    protected GenericMatcher(Class<T> type) {
        notNull(type);
        this.type = type;
    }

    @Override
    public final boolean matches(Object o) {
        if (o == null) {
            return false;
        }

        T payload;


        //command
        if ((CommandMessage.class.isAssignableFrom(o.getClass()))) {
            CommandMessage<T> message = (CommandMessage<T>) o;
            payload = message.getPayload();
        } else if ((GenericMessage.class.isAssignableFrom(o.getClass()))) {  //event
            GenericMessage<T> message = (GenericMessage<T>) o;
            payload = message.getPayload();
        } else {
            if (!type.isAssignableFrom(o.getClass())) return false;
            payload = (T) o;
        }


        return doMatches(payload);
    }

    protected boolean doMatches(T payload) {
        return true;
    }
}
