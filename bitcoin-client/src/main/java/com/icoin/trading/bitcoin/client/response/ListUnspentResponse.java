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


/**
 * Response object returned by BitconidClient's listUnspent method.
 * <p/>
 * Holds an array of {@link ListUnspentResult} objects, each of which has data
 * about one unspent transaction output.
 *
 * @author Claus Nielsen
 */

public class ListUnspentResponse extends BitcoinJsonRpcResponse<ListUnspentResult[]> {
    public ListUnspentResponse(ListUnspentResult[] result, BitcoinError error, String id) {
        super(result, error, id);
    }
}
