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
package com.icoin.trading.bitcoin.client.response;

import com.icoin.trading.bitcoin.client.JsonExtra;


/**
 * A bitcoind JSON RPC response.
 *
 * @param <RT> type of the result field.
 */
public abstract class BitcoinJsonRpcResponse<RT> extends JsonExtra {

    private RT result;
    private BitcoinError error;
    private String id;

    protected BitcoinJsonRpcResponse() {
    }

    public BitcoinJsonRpcResponse(RT result, BitcoinError error, String id) {
        this.result = result;
        this.error = error;
        this.id = id;
    }

    public RT getResult() {
        return result;
    }

    public BitcoinError getError() {
        return error;
    }

    public String getId() {
        return id;
    }
}
