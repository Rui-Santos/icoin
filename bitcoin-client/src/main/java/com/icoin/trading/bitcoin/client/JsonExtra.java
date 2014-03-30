/**
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
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA. */
package com.icoin.trading.bitcoin.client;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * "MixIn" adding an otherFields map for json fields not explicitly mapped.
 */
public abstract class JsonExtra extends ValueObject {

    private Map<String, Object> otherFields = new HashMap();

    /**
     * Sets name and value of other (unknown) JSON fields.
     *
     * @param field
     * @param value
     */
    @JsonAnySetter
    @SuppressWarnings("unused") // Is used by Jackson
    private void set(String field, Object value) {
        otherFields.put(field, value);
    }


    /**
     * Gets names and values of all other (unknown) JSON fields.
     *
     * @return Names and values of other fields available.
     */
    @JsonAnyGetter
    public Map<String, Object> getOtherFields() {
        return Collections.unmodifiableMap(otherFields);
    }


}
