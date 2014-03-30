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
package com.icoin.trading.bitcoin.client.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.icoin.trading.bitcoin.client.JsonExtra;

/**
 * Arguments for BitcoindClient's getBlockTemplate method.
 *
 * @see <a href="https://en.bitcoin.it/wiki/BIP_0022">Bitcoin Improvement Proposal</a>
 */
public class TemplateRequest extends JsonExtra {

    private String[] capabilities;

    @JsonInclude(Include.NON_EMPTY)
    private String mode;

    /**
     * Constructor.
     *
     * @param capabilities - SHOULD contain a list of the following, to indicate client-side support:
     *                     "longpoll", "coinbasetxn", "coinbasevalue", "proposal", "serverlist",
     *                     "workid", and any of the mutations (see <a href="https://en.bitcoin.it/wiki/BIP_0022">Bitcoin Improvement Proposal</a>).
     * @param mode         - MUST be "template" or omitted (null).
     */
    public TemplateRequest(String[] capabilities, String mode) {
        this.capabilities = capabilities;
        this.mode = mode;
    }

    public String[] getCapabilities() {
        return capabilities;
    }

    public String getMode() {
        return mode;
    }
}
