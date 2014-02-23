<%@ page import="org.springframework.context.i18n.LocaleContextHolder" %>
<%@ page session="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<link href="${ctx}/style/admin.css" rel="stylesheet">

<div class="row">
    <div class="col-lg-10 col-centered">
        <div class="bs-docs-section  col-centered">
            <h3 id="privacy">Privacy</h3>
            <c:if test="${not empty message}">
                <div class="${message.type.cssClass} text-center">${message.text}</div>
            </c:if>

            <div class="well well-lg">
                <form:form role="form" id="notificationSettings" action="/user/changeNotificationSettings" method="post" modelAttribute="notificationForm">
                    <fieldset>
                        <div class="row">
                            <div class="col-md-5 col-md-offset-2 text-right"><strong>Logon Alert</strong>
                            </div>
                            <div class="col-md-2">
                                <form:checkbox path="logonAlert" id="logonAlert" data-toggle="switch"/>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-5 col-md-offset-2 text-right"><strong>Withdraw Money Alert
                                notification</strong></div>
                            <div class="col-md-2">
                                <form:checkbox path="withdrawMoneyAlert" id="withdrawMoneyAlert" data-toggle="switch"/>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-5 col-md-offset-2 text-right"><strong>Withdraw Coin Alert
                                notification</strong>
                            </div>
                            <div class="col-md-2">
                                <form:checkbox path="withdrawItemAlert" id="withdrawItemAlert" data-toggle="switch"/>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-5 col-md-offset-2 text-right"><strong>Execution Alert
                                Notification</strong></div>
                            <div class="col-md-2">
                                <form:checkbox path="executedAlert" id="executedAlert" data-toggle="switch"/>
                            </div>
                        </div>
                        <button type="submit" class="btn btn-primary pull-right">Save</button>
                    </fieldset>
                </form:form>
            </div>
        </div>
    </div>
</div>

<!-- /container -->
<content tag="additionalJs">
    <script src="${ctx}/js/localization/messages_${lang}.js"></script>
    <script src="${ctx}/js/bootstrap-switch.js"></script>
    <script src="${ctx}/js/notificationsettings.js"></script>
</content>