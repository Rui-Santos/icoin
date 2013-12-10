<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%--
  ~ Copyright (c) 2010-2012. Axon Framework
  ~
  ~ Licensed under the Apache License, Version 2.0 (the "License");
  ~ you may not use this file except in compliance with the License.
  ~ You may obtain a copy of the License at
  ~
  ~     http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing, software
  ~ distributed under the License is distributed on an "AS IS" BASIS,
  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~ See the License for the specific language governing permissions and
  ~ limitations under the License.
  --%>

<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<table id="hor-minimalist-b">
    <thead>
    <tr>
        <th>Type</th>
        <th>Count</th>
        <th>Price</th>
        <th>Remaining</th>
        <th>User</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${orderBook.orders}" var="order">
        <tr>
            <td><c:out value='${order.type}'/></td>
            <td><c:out value='${order.tradeAmount}'/></td>
            <td><c:out value='${order.itemPrice}'/></td>
            <td><c:out value='${order.itemRemaining}'/></td>
            <td><c:out value='${order.userId}'/></td>
        </tr>
    </c:forEach>
    </tbody>
</table>