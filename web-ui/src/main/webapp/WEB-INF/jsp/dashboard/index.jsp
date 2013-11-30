<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
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
<html>
<head>
    <title>This is your dashboard</title>
</head>
<body>
<content tag="title">Dashboard</content>
<content tag="tagline">Your overview of everything you have and want to know</content>
<div class="row">
    <div class="span7">
        <h2>Portfolio</h2>

        <p>Here you see what you have and what is reserved.</p>

        <div class="row">
            <div class="span7">
                <h3>Money</h3>
            </div>
        </div>
        <div class="row">
            <div class="span3"><strong>Available</strong></div>
            <div class="span4"><c:out value="${portfolio.amountOfMoney}"/></div>
        </div>
        <div class="row">
            <div class="span3"><strong>Reserved</strong></div>
            <div class="span4"><c:out value="${portfolio.reservedAmountOfMoney}"/></div>
        </div>
        <div class="row">
            <div class="span7">
                <h3>Trade items</h3>
                <table class="zebra-striped condensed-table">
                    <thead>
                    <tr>
                        <th>Name</th>
                        <th>Amount</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="item" items="${portfolio.itemsInPossession}">
                        <tr>
                            <td><c:out value="${item.value.coinName}"/></td>
                            <td><c:out value="${item.value.amount}"/></td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
        <div class="row">
            <div class="span7">
                <h3>Trade items reserved</h3>
                <table class="zebra-striped condensed-table">
                    <thead>
                    <tr>
                        <th>Name</th>
                        <th>Amount</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="item" items="${portfolio.itemsReserved}">
                        <tr>
                            <td><c:out value="${item.value.coinName}"/></td>
                            <td><c:out value="${item.value.amount}"/></td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
    <div class="span7">
        <h2>Transactions</h2>

        <p>Here you see your current transactions.</p>

        <table class="zebra-striped condensed-table">
            <thead>
            <tr>
                <th>Coin</th>
                <th>Type</th>
                <th># Items</th>
                <th>Price</th>
                <th>Executed</th>
                <th>State</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="item" items="${transactions}">
                <tr>
                    <td><c:out value="${item.coinName}"/></td>
                    <td><c:out value="${item.type}"/></td>
                    <td><c:out value="${item.amountOfItems}"/></td>
                    <td><c:out value="${item.pricePerItem}"/></td>
                    <td><c:out value="${item.amountOfExecutedItems}"/></td>
                    <td><c:out value="${item.state}"/></td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>
</div>

</body>
</html>