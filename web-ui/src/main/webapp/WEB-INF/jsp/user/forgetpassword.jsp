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

<form:form id="forgetPassword" action="/user/forgetPassword" class="form-signin" role="form" modelAttribute="forgetPassword">
    <h2 class="form-signin-heading">Lost password</h2>

    <div class="input-group input-group-lg">
        <span class="input-group-addon"><i class="glyphicon glyphicon-user"></i></span>
        <input type="text" class="form-control"
               id="email" name='email'
               placeholder="Email"
               data-placement="right"
               required autofocus>
        <form:errors path="email" cssClass="alert-danger"/>
    </div>
    <button class="btn btn-lg btn-primary btn-block" type="submit">Reset Password</button>
</form:form>
<c:set var="lang" value="<%= LocaleContextHolder.getLocale().getLanguage()%>"/>

<content tag="additionalJs">
    <script type="text/javascript" src="${ctx}/js/localization/messages_${lang}.js"></script>
    <script src="${ctx}/js/tooltip.js"></script>
    <script src="${ctx}/js/forgetpassword.js"></script>
</content>