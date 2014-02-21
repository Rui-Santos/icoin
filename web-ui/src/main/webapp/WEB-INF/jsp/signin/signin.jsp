<%@ page import="org.springframework.context.i18n.LocaleContextHolder" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
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

<link href="${ctx}/style/sign.css" rel="stylesheet">

<%--<p>You need to login to access this part of the site. Please provided your username and password</p>--%>

<c:if test="${not empty param.login_error}">
    <div class="alert alert-danger text-center">
        <p>
            <strong>Your login attempt was not successful, please try again.</strong>
        </p>

        <c:if test="${not empty SPRING_SECURITY_LAST_EXCEPTION}">
                <c:out value="${SPRING_SECURITY_LAST_EXCEPTION.message}"/>.
        </c:if>

    </div>
</c:if>

<form:form id="signin" action="/signin/authenticate" class="form-signin" role="form">
    <h2 class="form-signin-heading">Please sign in</h2>

    <div class="input-group input-group-lg">
        <span class="input-group-addon"><i class="glyphicon glyphicon-user"></i></span>
        <input type="text" class="form-control"
               id="j_username" name='j_username'
               value='<c:if test="${not empty param.login_error}"><c:out value="${SPRING_SECURITY_LAST_USERNAME}"/></c:if>'
               placeholder="Username"
               data-placement="right"
               required autofocus>
    </div>

    <div class="input-group input-group-lg">
        <span class="input-group-addon"><i class="glyphicon glyphicon-lock"></i></span>
        <input type="password" id="j_password" name='j_password' class="form-control"
               placeholder="Password"
               data-placement="right"
               required>
    </div>
    <button class="btn btn-lg btn-primary btn-block" type="submit">Sign in</button>
    <%--<p><a href="/user/forgetPassword">Lost Password</a> Or <a href="/signup">Sign up</a> with a new account</p>--%>
    <p class="alert"><a href="/user/forgetPassword" class="alert-link">Lost Password</a> Or <a href="/signup" class="alert-link">Sign up</a></p>
    <%--<a class="login-link" href="#">Lost your password?</a>--%>
</form:form>
<c:set var="lang" value="<%= LocaleContextHolder.getLocale().getLanguage()%>"/>

<content tag="additionalJs">
    <script type="text/javascript" src="${ctx}/js/localization/messages_${lang}.js"></script>
    <script src="${ctx}/js/tooltip.js"></script>
    <script src="${ctx}/js/signin.js"></script>
</content>