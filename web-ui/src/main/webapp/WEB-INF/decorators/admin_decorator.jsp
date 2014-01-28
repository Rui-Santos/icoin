<%@ taglib prefix="decorator" uri="http://www.opensymphony.com/sitemesh/decorator" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
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

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <link rel="shortcut icon" href="${ctx}/image/favicon.ico">
    <link rel="icon" href="${ctx}/image/favicon.ico">
    <title><decorator:title/></title>
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="Website for BTC & Alt-BTC Trading">
    <meta name="keywords" content="BTC, обмен BTC, биржа BTC, обменник BTC, Bitcoin, биткоин, обмен биткоин, биржа биткоин, обменник биткоин, покупка BTC, продажа BTC, покупка bitcoin, продажа bitcoin, биржа bitcoin, обменник bitcoin, echange trading">
    <meta name="author" content="cooder">
    <meta name="description" content="Website contaning the Axon sample using a trader application">
    <!-- Le HTML5 shim, for IE6-8 support of HTML elements -->
    <!--[if lt IE 9]>
    <script src="${ctx}/js/html5shiv.js" type="text/javascript"></script>
    <script src="${ctx}/js/ie8-responsive-file-warning.js"></script><![endif]-->
    <![endif]-->

    <!-- Bootstrap core CSS -->
    <link href="${ctx}/style/bootstrap.css" rel="stylesheet">

    <!-- Custom styles for this template -->
    <link href="${ctx}/style/jumbotron.css" rel="stylesheet">
    <link href="${ctx}/style/offcanvas.css" rel="stylesheet">
    <link href="${ctx}/style/justified-nav.css" rel="stylesheet">
    <link href="${ctx}/style/btn.css" rel="stylesheet">
    <link href="${ctx}/style/footer.css" rel="stylesheet">
    <link href="${ctx}/style/flat-ui.css" rel="stylesheet">

    <script src="js/html5shiv.js"></script>
    <script src="js/respond.min.js"></script>

    <decorator:head/>
</head>
<body>
<div class="navbar navbar-inverse navbar-fixed-top" role="navigation" id="header">
    <div class="container">
        <div class="navbar-header">
            <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
                <span class="sr-only">Toggle navigation</span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            <a class="navbar-brand" href="/">iCoin another</a>
        </div>

        <div class="navbar-collapse collapse">
            <ul class="nav navbar-nav">
                <li>
                    <a href="#" class="active">Last price:$639.00000</a>
                </li>
                <li>
                    <a href="#">High:$650.15316</a>
                </li>
                <li>
                    <a href="#">Low:$453.29000</a>
                </li>
                <li>
                    <a href="#">Volume:61645 BTC</a>
                </li>
            </ul>
            <sec:authorize access="isAuthenticated()">
                <ul class="nav navbar-nav navbar-right">
                    <li class="active">
                        <a href="/dashboard"><sec:authentication property="principal.fullName"/></a>
                    </li>
                    <li>
                        <a href="${ctx}/j_spring_security_logout">logout</a>
                    </li>
                </ul>
            </sec:authorize>
            <sec:authorize access="isAnonymous()">
                <form action="<c:url value='${ctx}/signin/authenticate'/>" class="navbar-form navbar-right"  method="POST">
                    <div class="form-group">
                        <input type="text" placeholder="Username" name='j_username' class="form-control"
                               value='<c:if test="${not empty param.login_error}"><c:out value="${SPRING_SECURITY_LAST_USERNAME}"/></c:if>'/>
                    </div>
                    <div class="form-group">
                        <input type="password" placeholder="Password" class="form-control" name='j_password'/>
                    </div>
                    <button type="submit" class="btn btn-success">Sign in</button>
                </form>
            </sec:authorize>
        </div>
        <!--/.navbar-collapse -->
    </div>
</div>
<div class="container">

    <%--<h4><decorator:getProperty property="page.herounit"/></h4>--%>
    <div class="page-header">
        <h1><decorator:getProperty property="page.title"/>
            <small><decorator:getProperty property="page.tagline"/></small>
        </h1>
    </div>
    <decorator:getProperty property="page.breadcrumb"/>
    <decorator:body/>

</div>



<footer>
    <div class="container">
        <div class="row">
            <div class="col-md-7">
                <h3 class="footer-title">Subscribe</h3>
                <p>Do you like this freebie? Want to get more stuff like this?<br/>
                    Subscribe to designmodo news and updates to stay tuned on great designs.<br/>
                    Go to: <a href="http://designmodo.com/flat-free" target="_blank">designmodo.com/flat-free</a>
                </p>

                <p class="pvl">
                    <a href="https://twitter.com/share" class="twitter-share-button" data-url="http://designmodo.com/flat-free/" data-text="Flat UI Free - PSD&amp;amp;HTML User Interface Kit" data-via="designmodo">Tweet</a>
                    <script>!function(d,s,id){var js,fjs=d.getElementsByTagName(s)[0],p=/^http:/.test(d.location)?'http':'https';if(!d.getElementById(id)){js=d.createElement(s);js.id=id;js.src=p+'://platform.twitter.com/widgets.js';fjs.parentNode.insertBefore(js,fjs);}}(document, 'script', 'twitter-wjs');</script>
                    <iframe src="http://ghbtns.com/github-btn.html?user=designmodo&repo=Flat-UI&type=watch&count=true" height="20" width="107" frameborder="0" scrolling="0" style="width:105px; height: 20px;" allowTransparency="true"></iframe>
                    <iframe src="http://ghbtns.com/github-btn.html?user=designmodo&repo=Flat-UI&type=fork&count=true" height="20" width="107" frameborder="0" scrolling="0" style="width:105px; height: 20px;" allowTransparency="true"></iframe>
                    <iframe src="http://ghbtns.com/github-btn.html?user=designmodo&type=follow&count=true" height="20" width="195" frameborder="0" scrolling="0" style="width:195px; height: 20px;" allowTransparency="true"></iframe>
                </p>
            </div> <!-- /col-md-7 -->

            <div class="col-md-5">
                <div class="footer-banner">
                    <h3 class="footer-title">Get Flat UI Pro</h3>
                    <ul>
                        <li>Tons of Basic and Custom UI Elements</li>
                        <li>A Lot of Useful Samples</li>
                    </ul>
                    Email : <a href="mailto:icoin@icoin.net" target="_blank">designmodo.com/flat</a>
                </div>
            </div>
        </div>
    </div>
</footer>
<!-- /container -->
<script src="${ctx}/js/jquery-1.10.2.min.js"></script>
<script src="${ctx}/js/bootstrap.min.js"></script>
</body>
</html>
