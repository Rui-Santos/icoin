<form:errors path="userName" cssClass="error" />
It will renders and enclose the error messages with a default “span” element, which contains a CSS class of “error“.

<span id="username.errors" class="error">username is required!</span>
Note
path=”*” – display all error messages associated with any fields.
path=”username” – display error messages associated with the “username” field only.



<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
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

<content tag="breadcrumb">
    <ol class="breadcrumb">
        <li><a href="${ctx}/">Home</a></li>
        <li class="active"><a href="${ctx}/dashboard">Dashboard</a></li>
    </ol>
</content>

<form:form id="changePassword" action="/user/changePassword" class="form-signin" role="form" modelAttribute="changePasswordForm">
    <spring:hasBindErrors name="changePasswordForm">
        <div class="alert alert-warning alert-dismissable">
            <button type="button" class="close" data-dismiss="alert" aria-hidden="true">&times;</button>
            <form:errors path="*" element="div"/>
        </div>
    </spring:hasBindErrors>

    <div class="input-group input-group-lg">
        <span class="input-group-addon"><i class="glyphicon glyphicon-lock"></i></span>
        <input type="password" id="previousPassword" name='previousPassword' class="form-control"
               placeholder="Previous password"
               data-placement="right" title="Your Previous password"
               required>
    </div>
    <div class="input-group input-group-lg">
        <span class="input-group-addon"><i class="glyphicon glyphicon-lock"></i></span>
        <input type="password" id="newPassword" name='newPassword' class="form-control"
               placeholder="New password"
               data-placement="right" title="Your new password"
               required>
    </div>
    <div class="input-group input-group-lg">
        <span class="input-group-addon"><i class="glyphicon glyphicon-lock"></i></span>
        <input type="password" id="confirmedNewPassword" name='confirmedNewPassword' class="form-control"
               placeholder="Confirmed Password"
               data-placement="right" title="Your Confirmed new password"
               required>
    </div>
    <button class="btn btn-lg btn-primary btn-block" type="submit">Change</button>

</form:form>
<p>Or Change Your Withdraw Password.</p>
<form:form id="changeWithdrawPassword" action="/user/changeWithdrawPassword" class="form-signin" role="form" modelAttribute="changeWithdrawPasswordForm">
    <spring:hasBindErrors name="changeWithdrawPasswordForm">
        <div class="alert alert-warning alert-dismissable">
            <button type="button" class="close" data-dismiss="alert" aria-hidden="true">&times;</button>
            <form:errors path="*" element="div"/>
        </div>
    </spring:hasBindErrors>

    <div class="input-group input-group-lg">
        <span class="input-group-addon"><i class="glyphicon glyphicon-lock"></i></span>
        <input type="password" id="previousWithdrawPassword" name='previousWithdrawPassword' class="form-control"
               placeholder="Previous password"
               data-placement="right" title="Your Previous password"
               required>
    </div>
    <div class="input-group input-group-lg">
        <span class="input-group-addon"><i class="glyphicon glyphicon-lock"></i></span>
        <input type="password" id="withdrawPassword" name='withdrawPassword' class="form-control"
               placeholder="New withdraw password"
               data-placement="right" title="Your new withdraw password"
               required>
    </div>
    <div class="input-group input-group-lg">
        <span class="input-group-addon"><i class="glyphicon glyphicon-lock"></i></span>
        <input type="password" id="confirmedWithdrawPassword" name='confirmedWithdrawPassword' class="form-control"
               placeholder="Confirmed withdraw assword"
               data-placement="right" title="Your Confirmed withdraw password"
               required>
    </div>
    <button class="btn btn-lg btn-primary btn-block" type="submit">Change</button>
</form:form>