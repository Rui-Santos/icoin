<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
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

<%--<content tag="title">Dashboard</content>--%>
<%--<content tag="tagline">--%>
<%--Your overview of everything you have and want to know--%>
<%--</content>--%>
<%--<content tag="breadcrumb">--%>
<%--<ol class="breadcrumb">--%>
<%--<li><a href="${ctx}/">Home</a></li>--%>
<%--<li class="active"><a href="${ctx}/dashboard">Dashboard</a></li>--%>
<%--</ol>--%>
<%--</content>--%>

<div class="container bs-docs-container">
    <div class="row">
        <div class="col-md-3">
            <div class="bs-sidebar hidden-print" role="complementary">
                <ul class="nav bs-sidenav">

                    <li>
                        <a href="#portfolio">Portfolio</a>
                        <ul class="nav">
                            <li><a href="#overview">Overview</a></li>
                            <li><a href="#transactions">Transactions</a></li>
                            <%--<li><a href="#trade_history">Trade History</a></li>--%>
                        </ul>
                    </li>
                    <li>
                        <a href="#profile">Profile</a>
                        <ul class="nav">
                            <li><a href="#profile_details">Details</a></li>
                            <li><a href="#privacy">Privacy</a></li>
                        </ul>
                    </li>
                </ul>
            </div>
        </div>
        <div class="col-md-9" role="main">
            <!-- Global Bootstrap settings
            ================================================== -->
            <div class="bs-docs-section">
                <h1 id="portfolio" class="page-header">Portfolio</h1>

                <h3 id="overview">Overview</h3>

                <div class="well well-lg">
                    <div class="admin-information">
                        <dl class="myacc-col1">
                            <dt>Available Money</dt>
                            <dd>
                                <fmt:formatNumber value="${portfolio.amountOfMoney.amount}" type="number" pattern="#.##" currencyCode="CNY"/>
                            </dd>
                        </dl>
                        <dl class="myacc-col2">
                            <dt>Reserved Money</dt>
                            <dd>
                                <fmt:formatNumber value="${portfolio.reservedAmountOfMoney.amount}" type="number" pattern="#.##" currencyCode="CNY"/>
                            </dd>
                        </dl>
                        <dl class="myacc-col3">
                            <dt>Money Action</dt>
                            <dd>
                                <div class="btn-group btn-group-sm">
                                    <a href="#" class="btn btn-primary btn-sm">&nbsp;&nbsp;&nbsp;&nbsp;Add&nbsp;&nbsp;&nbsp;&nbsp;</a>
                                    <a href="#" class="btn btn-danger btn-sm">Withdraw</a>
                                </div>
                            </dd>
                        </dl>
                        <c:forEach var="item" items="${portfolio.items}">
                            <dl class="myacc-col1">
                                <dt>Available <c:out value="${item.value.coinIdentifier}"/></dt>
                                <dd>
                                    <fmt:formatNumber value="${item.value.amountInPossession.amount}" type="number" pattern="#.####"/>
                                </dd>
                            </dl>
                            <dl class="myacc-col2">
                                <dt>Reserved <c:out value="${item.value.coinIdentifier}"/></dt>
                                <dd>
                                    <fmt:formatNumber value="${item.value.reservedAmount.amount}" type="number" pattern="#.####"/>
                                </dd>
                            </dl>
                            <dl class="myacc-col3">
                                <dt><c:out value="${item.value.coinIdentifier}"/> Action</dt>
                                <dd>
                                    <div class="btn-group btn-group-sm">
                                        <a href="#" class="btn btn-primary btn-sm">&nbsp;&nbsp;&nbsp;&nbsp;Add&nbsp;&nbsp;&nbsp;&nbsp;</a>
                                        <a href="#" class="btn btn-danger btn-sm">Withdraw</a>
                                    </div>
                                </dd>
                            </dl>
                        </c:forEach>
                    </div>
                </div>


                <h3 id="transactions">Transactions</h3>

                <div class="well well-lg">
                    <table class="table table-condensed table-hover">
                        <thead>
                        <tr>
                            <th class="text-center">Coin</th>
                            <th class="text-center">Type</th>
                            <th class="text-center">Amount</th>
                            <th class="text-center">Money</th>
                            <th class="text-center">Executed Amount</th>
                            <th class="text-center">Executed Money</th>
                            <th class="text-center">State</th>
                            <th class="text-center">Created</th>
                            <th class="text-center">Action</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach var="item" items="${transactions}">
                            <c:choose>
                                <c:when test="${item.type == 'SELL'}">
                                    <tr class="danger">
                                </c:when>
                                <c:otherwise>
                                    <tr class="success">
                                </c:otherwise>
                            </c:choose>
                                <td><c:out value="${item.coinId}"/></td>
                                <td><c:out value="${item.type}"/></td>
                                <td>
                                    <fmt:formatNumber value="${item.amountOfItem.amount}" type="number" pattern="#.####"/>
                                </td>
                                <td>
                                    <fmt:formatNumber value="${item.totalMoney.amount}" type="number" pattern="#.##" currencyCode="CNY"/>
                                </td>
                                <td>
                                    <fmt:formatNumber value="${item.amountOfExecutedItem.amount}" type="number" pattern="#.####"/>
                                </td>
                                <td>
                                    <fmt:formatNumber value="${item.executedMoney.amount}" type="number" pattern="#.##" currencyCode="CNY"/>
                                </td>
                                <td>
                                    <c:choose>
                                        <c:when test="${item.state == 'EXECUTED'}">
                                            Done
                                        </c:when>
                                        <c:when test="${item.state == 'PARTIALLY_EXECUTED'}">
                                            Partially
                                        </c:when>
                                        <c:when test="${item.state == 'CANCELLED'}">
                                            Cancelled
                                        </c:when>
                                        <c:when test="${item.state == 'CONFIRMED'}">
                                            Confirmed
                                        </c:when><c:when test="${item.state == 'STARTED'}">
                                            Started
                                        </c:when>
                                        <c:otherwise>
                                            &nbsp;
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <fmt:formatDate pattern="MM-dd HH:mm:ss" value="${item.created}" />
                                </td>
                                <td class="text-center">
                                    <c:choose>
                                        <c:when test="${item.state == 'EXECUTED'}">
                                            &nbsp;
                                        </c:when>
                                        <c:when test="${item.state == 'CANCELLED'}">
                                            &nbsp;
                                        </c:when>
                                        <c:otherwise>
                                            <a href="#">Undo</a>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                    <ul class="pager">
                        <li class="next"><a href="#">Newer &rarr;</a></li>
                        <li class="previous disabled"><a href="#">&larr; Older</a></li>
                    </ul>
                </div>
            </div>


            <!-- Grid system
            ================================================== -->
            <div class="bs-docs-section">
                <h1 id="profile" class="page-header">Profile</h1>

                <h3 id="profile_details">Details</h3>

                <div class="well well-lg">

                    <div class="row pi-nav">

                        <div class="col-xs-2 col-md-1">
                            <a href="${ctx}/user/changeDetails"
                               class="icn_pen"><span class="glyphicon glyphicon-edit pull-right"></span></a>
                        </div>
                        <div class="col-xs-2 col-md-1">
                            <a href="${ctx}/user/changePassword"
                               class="icn_pen"><span class="glyphicon glyphicon-lock pull-right"></span></a>
                        </div>
                    </div>
                    <div class="row">
                    </div>
                    <div class="admin-information">
                        <dl class="myacc-col1">
                            <dt>Username</dt>
                            <dd><c:out value="${userInfo.username}"/></dd>
                        </dl>
                        <dl class="myacc-col2">
                            <dt>Email</dt>
                            <dd><c:out value="${userInfo.email}"/></dd>
                        </dl>
                        <dl class="myacc-col3">
                            <dt>ID Card</dt>
                            <dd id="identifier"><c:out value="${userInfo.identifier.number}"/></dd>
                        </dl>
                        <dl class="myacc-col1">
                            <dt>Cell Phone Number</dt>
                            <dd id="cellPhone"><c:out value="${userInfo.cellPhoneNumber}"/></dd>
                        </dl>
                        <dl class="myacc-col2">
                            <dt>First Name</dt>
                            <dd><c:out value="${userInfo.firstName}"/></dd>
                        </dl>
                        <dl class="myacc-col3">
                            <dt>Last Name</dt>
                            <dd><c:out value="${userInfo.lastName}"/></dd>
                        </dl>
                        <%--<dl class="myacc-col3">--%>
                        <%--<dt>Add BTC</dt>--%>
                        <%--<dd>--%>

                        <%--</dd>--%>
                        <%--</dl>--%>
                    </div>
                </div>

                <h3 id="privacy">Privacy</h3>

                <div class="well well-lg">
                    <div class="row pi-nav">
                        <div class="col-xs-2 col-md-1">
                            <a href="${ctx}/user/changeNotificationSettings"
                               class="icn_pen"><span class="glyphicon glyphicon-edit pull-right"></span></a>
                        </div>
                    </div>
                    <div class="row">
                    </div>

                    <div class="row">
                        <div class="col-md-5 col-md-offset-2 text-right"><strong>Logon Alert</strong></div>
                        <div class="col-md-2">
                            <input type="checkbox"
                                    <c:if test="${userInfo.logonAlert}"> checked </c:if>
                                   disabled data-toggle="switch"/>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-5 col-md-offset-2 text-right"><strong>Withdraw Money Alert</strong>
                        </div>
                        <div class="col-md-2">
                            <input type="checkbox"
                                    <c:if test="${userInfo.withdrawMoneyAlert}"> checked </c:if>
                                   disabled data-toggle="switch"/>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-5 col-md-offset-2 text-right"><strong>Withdraw Coin Alert
                            notification</strong></div>
                        <div class="col-md-2">
                            <input type="checkbox"
                                    <c:if test="${userInfo.withdrawItemAlert}"> checked </c:if>
                                   disabled data-toggle="switch"/>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-5 col-md-offset-2 text-right"><strong>Execution Alert</strong>
                        </div>
                        <div class="col-md-2">
                            <input type="checkbox"
                                    <c:if test="${userInfo.executedAlert}"> checked </c:if>
                                   disabled data-toggle="switch"/>
                        </div>
                    </div>
                </div>

            </div>

        </div>
    </div>
</div>
<content tag="additionalJs">
    <script src="${ctx}/js/bootstrap-switch.js"></script>
    <script src="${ctx}/js/icoin.js"></script>
    <script src="${ctx}/js/jquery.maskedinput.js"></script>
    <script src="${ctx}/js/adminedit.js"></script>
</content>
</body>
</html>