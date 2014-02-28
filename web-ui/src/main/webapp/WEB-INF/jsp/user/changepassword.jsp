<%@ page import="org.springframework.context.i18n.LocaleContextHolder" %>
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

<form:form id="changePassword" action="/user/changePassword" class="form-edit" role="form" modelAttribute="changePasswordForm">
    <spring:hasBindErrors name="changePasswordForm">
        <div class="alert alert-danger alert-dismissable">
            <button type="button" class="close" data-dismiss="alert" aria-hidden="true">&times;</button>
            <form:errors path="*" element="div"/>
        </div>
    </spring:hasBindErrors>
    <h5 class="form-signin-heading text-primary text-center">Change your password</h5>

    <input type="password" id="previousPassword" name='previousPassword' class="form-control"
           placeholder="Previous password"
           data-placement="right" title="Your Previous password"
           required/>
    <form:errors path="previousPassword" cssClass="alert-danger" />

    <input type="password" id="newPassword" name='newPassword' class="form-control"
           placeholder="New password"
           data-placement="right" title="Your new password"
           required/>
    <form:errors path="newPassword" cssClass="alert-danger" />

    <input type="password" id="confirmedNewPassword" name='confirmedNewPassword' class="form-control"
           placeholder="Confirmed Password"
           data-placement="right" title="Confirm new password"
           required>
    <form:errors path="confirmedNewPassword" cssClass="alert-danger" />
    <button class="btn btn-lg btn-primary btn-block" type="submit">Change</button>

</form:form>

<c:if test="${not empty changeWithdrawPasswordForm}">
    <form:form id="changeWithdrawPassword" action="/user/changeWithdrawPassword" class="form-signin" role="form" modelAttribute="changeWithdrawPasswordForm">
        <spring:hasBindErrors name="changeWithdrawPasswordForm">
            <div class="alert alert-danger alert-dismissable">
                <button type="button" class="close" data-dismiss="alert" aria-hidden="true">&times;</button>
                <form:errors path="*" element="div"/>
            </div>
        </spring:hasBindErrors>
        <h5 class="form-signin-heading text-primary text-center">Or Change your Trading password</h5>

        <input type="password" id="previousWithdrawPassword" name='previousWithdrawPassword' class="form-control"
               placeholder="Previous withdraw password"
               data-placement="right" title="Your Previous password"
               required>
        <form:errors path="previousWithdrawPassword" cssClass="alert-danger" />

        <input type="password" id="withdrawPassword" name='withdrawPassword' class="form-control"
               placeholder="New withdraw password"
               data-placement="right" title="New withdraw password"
               required>
        <form:errors path="withdrawPassword" cssClass="alert-danger" />

        <input type="password" id="confirmedWithdrawPassword" name='confirmedWithdrawPassword' class="form-control"
               placeholder="Confirmed withdraw assword"
               data-placement="right" title="Confirm withdraw password"
               required>
        <form:errors path="confirmedWithdrawPassword" cssClass="alert-danger" />
        <button class="btn btn-lg btn-primary btn-block" type="submit">Change</button>
    </form:form>
</c:if>

<c:if test="${not empty createWithdrawPasswordForm}">
    <form:form id="createWithdrawPassword" action="/user/createWithdrawPassword" class="form-signin" role="form" modelAttribute="createWithdrawPasswordForm">
        <spring:hasBindErrors name="changeWithdrawPasswordForm">
            <div class="alert alert-danger alert-dismissable">
                <button type="button" class="close" data-dismiss="alert" aria-hidden="true">&times;</button>
                <form:errors path="*" element="div"/>
            </div>
        </spring:hasBindErrors>
        <h5 class="form-signin-heading text-primary text-center">Or Create your Trading password</h5>

        <input type="password" id="withdrawPassword" name='withdrawPassword' class="form-control"
               placeholder="New withdraw password"
               data-placement="right" title="New withdraw password"
               required>
        <form:errors path="withdrawPassword" cssClass="alert-danger" />

        <input type="password" id="confirmedWithdrawPassword" name='confirmedWithdrawPassword' class="form-control"
               placeholder="Confirmed withdraw assword"
               data-placement="right" title="Confirm withdraw password"
               required>
        <form:errors path="confirmedWithdrawPassword" cssClass="alert-danger" />
        <button class="btn btn-lg btn-primary btn-block" type="submit">Create</button>
    </form:form>
</c:if>

<c:set var="lang" value="<%= LocaleContextHolder.getLocale().getLanguage()%>"/>
<content tag="additionalJs">
    <script src="${ctx}/js/localization/messages_${lang}.js"></script>
    <script src="${ctx}/js/tooltip.js"></script>
</content>