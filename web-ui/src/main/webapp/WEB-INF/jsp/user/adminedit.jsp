<%@ page import="org.springframework.context.i18n.LocaleContextHolder" %>
<%@ page session="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<link href="${ctx}/style/admin.css" rel="stylesheet">

<div class="row">
    <div class="col-lg-10 col-centered">
        <div class="bs-docs-section  col-centered">

            <h3 id="details">Details</h3>
            <c:if test="${not empty message}">
                <div class="${message.type.cssClass} text-center">${message.text}</div>
            </c:if>

            <p>Your detail information</p>

            <div class="well well-lg">
                <form:form role="form" id="changeDetails" action="/user/changeDetails" method="post" modelAttribute="changeInfoForm">
                    <fieldset>
                        <div class="admin-information">

                            <dl class="myacc-col1">
                                <label for="username" class="control-label">Username</label>
                                <form:input type="text" class="form-control" path="username" id="username" disabled="true" placeholder="Enter username"/>
                                <form:errors path="username" cssClass="alert-danger"/>
                            </dl>
                            <dl class="myacc-col2">
                                <label for="email" class="control-label">Email</label>
                                <form:input type="text" class="form-control" path="email" id="email" placeholder="Enter email"/>
                                <form:errors path="email" cssClass="alert-danger"/>
                            </dl>
                            <dl class="myacc-col3">
                                <label for="identifier" class="control-label">ID Card</label>
                                <form:input type="text" class="form-control" path="identifier" id="identifier"
                                       placeholder="Enter ID number" disabled="true"/>
                                <form:errors path="identifier" cssClass="alert-danger"/>
                            </dl>


                            <dl class="myacc-col1">
                                <label for="mobile" class="control-label">Cell Phone Number</label>
                                <form:input  type="tel" class="form-control" path="mobile" id="mobile"
                                       placeholder="Enter mobile number"/>
                                <form:errors id="mobile" path="mobile" cssClass="alert-danger"/>

                            </dl>
                            <dl class="myacc-col2">
                                <label for="firstName" class="control-label">First Name</label>
                                <form:input  type="text" class="form-control" path="firstName" id="firstName"
                                       placeholder="Enter first name"/>
                                <form:errors path="firstName" cssClass="alert-danger"/>
                            </dl>
                            <dl class="myacc-col3">
                                <label for="lastName" class="control-label">Last Name</label>
                                <form:input  type="text" class="form-control" path="lastName" id="lastName"
                                             placeholder="Enter last name"/>
                                <form:errors path="lastName" cssClass="alert-danger"/>
                            </dl>
                            <button type="submit" class="btn btn-primary pull-right">Save</button>
                        </div>
                    </fieldset>
                </form:form>
            </div>
        </div>
    </div>
</div>

<c:set var="lang" value="<%= LocaleContextHolder.getLocale().getLanguage()%>"/>
<content tag="additionalJs">
    <script src="${ctx}/js/localization/messages_${lang}.js"></script>
    <script src="${ctx}/js/jquery.maskedinput.js"></script>
    <script src="${ctx}/js/adminedit.js"></script>
</content>