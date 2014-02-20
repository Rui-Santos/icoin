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
                    <li><a href="#trade_history">Trade History</a></li>
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
    <div class="page-header">
        <h1 id="portfolio">Portfolio</h1>
    </div>

    <div class="panel panel-primary">
        <div class="panel-heading">
            <h3 class="panel-title" id="overview">Overview</h3>
        </div>

        <div class="panel-body">
            <div class="admin-information">
                <!--<ul class="pi-nav">-->
                <!--<li class="edit-information">-->
                <!--<a href="https://designmodo.com/my-account/edit-address/?address=billing"-->
                <!--class="icn_pen"><span class="glyphicon glyphicon-edit"></span></a></li>-->
                <!--</ul>-->

                <dl class="myacc-col1">
                    <dt>Available Money</dt>
                    <dd><c:out value="${portfolio.amountOfMoney}"/></dd>
                </dl>
                <dl class="myacc-col2">
                    <dt>Reserved Money</dt>
                    <dd><c:out value="${portfolio.reservedAmountOfMoney}"/></dd>
                </dl>
                <dl class="myacc-col3">
                    <dt>Money Action</dt>
                    <dd>
                        <!--<form id="add_money" method="post" action="/admin/portfolio/coin/item/">-->
                        <!--<div class="input-group input-group-sm">-->
                        <!--<span class="input-group-addon">CNY</span>-->
                        <!--<input type="text" class="form-control" name="cny_money" required id="cny_money">-->
                        <!--<span class="input-group-btn">-->
                        <!--<input type="submit" class="btn btn-primary" name="add"   value="Add"/>-->
                        <!--</span>-->
                        <!--<span class="input-group-btn">-->
                        <!--<input type="submit" class="btn btn-danger" name="withdraw" value="Withdraw"/>-->
                        <!--</span>-->
                        <!--</div>-->
                        <!--</form>-->
                        <!--<div class="btn-group-sm">-->
                        <!--<div class="col-md-6">-->
                        <!--<a href="#" class="btn btn-primary btn-sm btn-block">Add</a>-->
                        <!--</div>-->
                        <!--<div class="col-md-6">-->
                        <!--<a href="#" class="btn btn-primary btn-sm btn-block">Withdraw</a>-->
                        <!--</div>-->
                        <!--</div>-->

                        <div class="btn-group btn-group-sm">
                            <a href="#" class="btn btn-primary btn-sm">Add</a>
                            <a href="#" class="btn btn-danger btn-sm">Withdraw</a>
                        </div>

                    </dd>
                </dl>

                <c:forEach var="item" items="${portfolio.items}">
                    <dl class="myacc-col1">
                        <dt>Available <c:out value="${item.value.coinName}"/></dt>
                        <dd><c:out value="${item.value.amountInPossession}"/></dd>
                    </dl>
                    <dl class="myacc-col2">
                        <dt>Reserved <c:out value="${item.value.coinName}"/></dt>
                        <dd><c:out value="${item.value.reservedAmount}"/></dd>
                    </dl>
                    <dl class="myacc-col3">
                        <dt><c:out value="${item.value.coinName}"/> Action</dt>
                        <dd>
                            <div class="btn-group btn-group-sm">
                                <a href="#" class="btn btn-primary btn-sm">Add</a>
                                <a href="#" class="btn btn-danger btn-sm">Withdraw</a>
                            </div>
                        </dd>
                    </dl>
                </c:forEach>
            </div>

        </div>
        <div class="panel-heading">
            <h3 class="panel-title" id="transactions">Transactions</h3>
        </div>

        <div class="panel-body">
            <table class="table table-condensed table-hover">
                <thead>
                <tr>
                    <th class="text-center">Coin</th>
                    <th class="text-center">Type</th>
                    <th class="text-center">Items</th>
                    <th class="text-center">Price</th>
                    <th class="text-center">Executed</th>
                    <th class="text-center">State</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="item" items="${transactions}">
                    <tr>
                        <td><c:out value="${item.coinName}"/></td>
                        <td><c:out value="${item.type}"/></td>
                        <td><c:out value="${item.amountOfItem}"/></td>
                        <td><c:out value="${item.pricePerItem}"/></td>
                        <td><c:out value="${item.amountOfExecutedItem}"/></td>
                        <td><c:out value="${item.state}"/></td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
            <ul class="pagination pull-right">
                <li class="disabled"><a href="#">&laquo;</a></li>
                <li class="active"><a href="#">1</a></li>
                <li><a href="#">2</a></li>
                <li><a href="#">3</a></li>
                <li><a href="#">4</a></li>
                <li><a href="#">5</a></li>
                <li><a href="#">&raquo;</a></li>
            </ul>
        </div>
    </div>

    <%--<div class="panel panel-primary">--%>
    <%--<div class="panel-heading">--%>
    <%--<h3 class="panel-title" id="trade_history">Trade History</h3>--%>
    <%--</div>--%>

    <%--<div class="panel-body">--%>
    <%--<table class="table table-condensed table-hover">--%>
    <%--<thead>--%>
    <%--<tr>--%>
    <%--<th class="text-center">Buy/Sell</th>--%>
    <%--<th class="text-center">Price</th>--%>
    <%--<th class="text-center">Amount</th>--%>
    <%--<th class="text-center">CNY</th>--%>
    <%--<th class="text-center">Date</th>--%>
    <%--</tr>--%>
    <%--</thead>--%>
    <%--<tbody>--%>
    <%--<tr>--%>
    <%--<td class="text-center">Buy</td>--%>
    <%--<td class="text-center">593.998</td>--%>
    <%--<td class="text-center">0.01</td>--%>
    <%--<td class="text-center">5.93998</td>--%>
    <%--<td class="text-center">21.11.13 15:35</td>--%>
    <%--</tr>--%>
    <%--<tr>--%>
    <%--<td class="text-center">Buy</td>--%>
    <%--<td class="text-center">593.998</td>--%>
    <%--<td class="text-center">0.01</td>--%>
    <%--<td class="text-center">5.93998</td>--%>
    <%--<td class="text-center">21.11.13 15:35</td>--%>
    <%--</tr>--%>
    <%--</tbody>--%>
    <%--</table>--%>
    <%--<ul class="pagination pull-right">--%>
    <%--<li class="disabled"><a href="#">&laquo;</a></li>--%>
    <%--<li class="active"><a href="#">1</a></li>--%>
    <%--<li><a href="#">2</a></li>--%>
    <%--<li><a href="#">3</a></li>--%>
    <%--<li><a href="#">4</a></li>--%>
    <%--<li><a href="#">5</a></li>--%>
    <%--<li><a href="#">&raquo;</a></li>--%>
    <%--</ul>--%>
    <%--</div>--%>
    <%--</div>--%>
</div>


<!-- Grid system
================================================== -->
<div class="bs-docs-section">
    <div class="page-header">
        <h1 id="profile">Profile</h1>
    </div>
    <div class="panel panel-primary">
        <div class="panel-heading">
            <h3 class="panel-title" id="profile_details">Details</h3>
        </div>

        <div class="panel-body">
            <div class="row pi-nav">

                <div class="col-xs-2 col-md-1">
                    <a href="${ctx}/user/changePassword"
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
                    <dd><c:out value="${userInfo.identifier.displayDesc}"/></dd>
                </dl>
                <dl class="myacc-col1">
                    <dt>Cell Phone Number</dt>
                    <dd><c:out value="${userInfo.cellPhoneNumber}"/></dd>
                </dl>
                <dl class="myacc-col2">
                    <dt>Real Name</dt>
                    <dd><c:out value="${userInfo.realName}"/></dd>
                </dl>
                <%--<dl class="myacc-col3">--%>
                <%--<dt>Add BTC</dt>--%>
                <%--<dd>--%>

                <%--</dd>--%>
                <%--</dl>--%>
            </div>

        </div>
    </div>

    <div class="panel panel-primary">
        <div class="panel-heading">
            <h3 class="panel-title" id="privacy">Privacy</h3>
        </div>

        <div class="panel-body">
            <div class="privacy-information">
                <dl class="dl-horizontal">
                    <dt>Logon notification</dt>
                    <dd>
                        <input type="checkbox" checked="" data-toggle="switch"/>
                    </dd>
                    <dt>Change password notification</dt>
                    <dd>
                        <input type="checkbox" checked="" data-toggle="switch"/>
                    </dd>
                    <dt>Change withdraw password notification</dt>
                    <dd>
                        <input type="checkbox" checked="" data-toggle="switch"/>
                    </dd>
                    <dt>Withdraw Money Notification</dt>
                    <dd>
                        <input type="checkbox" checked="" data-toggle="switch"/>
                    </dd>
                </dl>
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
</content>
</body>
</html>