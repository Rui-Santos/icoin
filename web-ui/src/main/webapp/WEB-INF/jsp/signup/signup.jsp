<%@ page import="org.springframework.context.i18n.LocaleContextHolder" %>
<%@ page session="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<link href="${ctx}/style/sign.css" rel="stylesheet">

<%--<h3>Sign Up</h3>--%>

<c:if test="${not empty message}">
    <div class="${message.type.cssClass}">${message.text}</div>
</c:if>

<c:url value="/signup" var="signupUrl"/>

<form:form id="signup" action="${signupUrl}" method="post" class="form-edit" modelAttribute="signupForm">
    <h2 class="form-signip-heading">Please sign up</h2>

    <div class="formInfo">
        <s:bind path="*">
            <c:choose>
                <c:when test="${status.error}">
                    <div class="alert-danger">Please fix the errors below before resubmit.</div>
                </c:when>
            </c:choose>
        </s:bind>
    </div>

    <fieldset>
        <div class="from-group">
          <label class="sr-only" for="username">Username</label>
            <input type="text" id="username" name="username" type="text" class="form-control" placeholder="Username"
                   data-placement="right" title="Please input a username" required autofocus/>
            <form:errors path="username" cssClass="alert-danger"/>
        </div>
        <div class="from-group">
        <label class="sr-only" for="password">Password</label>
            <input type="password" id="password" name="password" class="form-control" placeholder="Password"
                   data-placement="right" title="Please input your password" required/>
            <form:errors path="password" cssClass="alert-danger"/>
        </div>
        <div class="from-group">
            <label class="sr-only" for="confirmedPassword">Confirm Password</label>
            <input type="password" id="confirmedPassword" name="confirmedPassword" class="form-control" placeholder="Confirm Password"
                   data-placement="right" title="Please input your password again" required/>
            <form:errors path="confirmedPassword" cssClass="alert-danger"/>
        </div>

        <div class="from-group">
            <label class="sr-only" for="identifier">Identifier</label>
            <form:input path="identifier" id="identifier" type="text" class="form-control" placeholder="ID Card Number"
                        data-placement="right"
                        title="Please input your ID number"/>
            <form:errors path="identifier" cssClass="alert-danger"/>
        </div>
        <div class="from-group">
        <label class="sr-only" for="email">Email</label>
            <form:input path="email" id="email" type="email" class="form-control" placeholder="Email" data-placement="right"
                        title="Please input your email address"/>
            <form:errors path="email" cssClass="alert-danger"/>
        </div>
        <div class="from-group">
            <label class="sr-only" for="firstName">First Name</label>
            <form:input path="firstName" id="firstName" type="text" class="form-control" placeholder="First Name"
                        data-placement="right"
                        title="Please input your firstname"/>
            <form:errors path="firstName" cssClass="alert-danger"/>
        </div>
        <div class="from-group">
        <label class="sr-only" for="lastName">Last Name</label>
            <form:input path="lastName" id="lastName" type="text" class="form-control" placeholder="Last Name" data-placement="right"
                        title="Please input your last name"/>
            <form:errors path="lastName" cssClass="alert-danger"/>
        </div>

         <img id="captchaImage" src="/simpleCaptcha.png" alt="captcha image" class="captchaImage"/>
          <%--<img src="reload.jpg" onclick="reloadCaptcha()" alt="reload"width="40" height="40"/>--%>
        <form:input path="captcha" id="captcha" type="text" class="form-control" placeholder="captcha" data-placement="right"
                    title="Please input captcha"/>

        <div id="signupTerms" class="modal fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel"
             aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content">

                    <div class="modal-header">
                        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                        <h4 class="modal-title" id="myModalLabel">Terms</h4>
                    </div>
                    <div class="modal-body">
                        <h4>Overflowing text to show scroll behavior</h4>

                        <p>Cras mattis consectetur purus sit amet fermentum. Cras justo odio, dapibus ac facilisis in,
                            egestas eget quam. Morbi leo risus, porta ac consectetur ac, vestibulum at eros.</p>

                        <p>Praesent commodo cursus magna, vel scelerisque nisl consectetur et. Vivamus sagittis lacus
                            vel augue laoreet rutrum faucibus dolor auctor.</p>

                        <p>Aenean lacinia bibendum nulla sed consectetur. Praesent commodo cursus magna, vel scelerisque
                            nisl consectetur et. Donec sed odio dui. Donec ullamcorper nulla non metus auctor
                            fringilla.</p>

                        <p>Cras mattis consectetur purus sit amet fermentum. Cras justo odio, dapibus ac facilisis in,
                            egestas eget quam. Morbi leo risus, porta ac consectetur ac, vestibulum at eros.</p>

                        <p>Praesent commodo cursus magna, vel scelerisque nisl consectetur et. Vivamus sagittis lacus
                            vel augue laoreet rutrum faucibus dolor auctor.</p>

                        <p>Aenean lacinia bibendum nulla sed consectetur. Praesent commodo cursus magna, vel scelerisque
                            nisl consectetur et. Donec sed odio dui. Donec ullamcorper nulla non metus auctor
                            fringilla.</p>

                        <p>Cras mattis consectetur purus sit amet fermentum. Cras justo odio, dapibus ac facilisis in,
                            egestas eget quam. Morbi leo risus, porta ac consectetur ac, vestibulum at eros.</p>

                        <p>Praesent commodo cursus magna, vel scelerisque nisl consectetur et. Vivamus sagittis lacus
                            vel augue laoreet rutrum faucibus dolor auctor.</p>

                        <p>Aenean lacinia bibendum nulla sed consectetur. Praesent commodo cursus magna, vel scelerisque
                            nisl consectetur et. Donec sed odio dui. Donec ullamcorper nulla non metus auctor
                            fringilla.</p>
                    </div>
                    <div class="modal-footer">
                            <%--<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>--%>
                        <button type="button" class="btn btn-primary" data-dismiss="modal">Confirm</button>
                    </div>

                </div>
                <!-- /.modal-content -->
            </div>
            <!-- /.modal-dialog -->
        </div>
        <!-- /.modal -->
        <div class="checkbox">
            <label class="checkbox">
                <input type="checkbox" name="agreed"> I agree with <a data-toggle="modal" data-target="#signupTerms">terms</a>
                <form:errors path="agreed" cssClass="alert-danger"/>
            </label>
        </div>


    </fieldset>
    <button class="btn btn-lg btn-primary btn-block" type="submit">Sign Up</button>
</form:form>


<hr/>
<p class="text-center">Or you can Login with one of the following</p>
<!-- TWITTER SIGNIN -->
<%--<form id="tw_signin" action="<c:url value="/signin/twitter"/>" method="POST">--%>
<%--<button type="submit"><img src="<c:url value="/resources/social/twitter/sign-in-with-twitter-d.png"/>" /></button>--%>
<%--</form>--%>

<!-- FACEBOOK SIGNIN -->
<%--<form name="fb_signin" id="fb_signin" action="<c:url value="/signin/facebook"/>" method="POST">--%>
<%--<input type="hidden" name="scope" value="publish_stream,user_photos,offline_access" />--%>
<%--<button type="submit"><img src="<c:url value="/image/w_logo.jpg"/>" /></button>--%>
<%--</form>--%>


<%----%>

<!-- weibo SIGNIN -->
<%--<form name="wb_signin" id="wb_signin" action="<c:url value="/signin/weibo"/>" method="POST">--%>
<c:url value="/signin/weibo" var="signinWeiboUrl"/>
<form:form class="form-edit" name="wb_signin" id="wb_signin" action="${signinWeiboUrl}" method="POST">
    <input type="hidden" name="scope" value="publish_stream,user_photos,offline_access"/>
    <button type="submit"><img src="<c:url value="/image/w_logo.jpg"/>"/></button>
</form:form>
<c:set var="lang" value="<%= LocaleContextHolder.getLocale().getLanguage()%>"/>


<content tag="additionalJs">
<%--<script src="${ctx}/js/jquery.validate.min.js"></script>--%>
    <script type="text/javascript" src="${ctx}/js/localization/messages_${lang}.js"></script>
    <script src="${ctx}/js/tooltip.js"></script>
    <script src="${ctx}/js/signup.js"></script>
</content>