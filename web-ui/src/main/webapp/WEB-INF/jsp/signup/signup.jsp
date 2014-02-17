<%@ page session="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<link href="${ctx}/style/sign.css" rel="stylesheet">

<%--<h3>Sign Up</h3>--%>

<c:if test="${not empty message}">
    <div class="${message.type.cssClass}">${message.text}</div>
</c:if>

<c:url value="/signup" var="signupUrl" />

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
        <%--<div class="input-group">--%>
            <input type="text" name="username"  type="text" class="form-control" placeholder="Username"
                   data-placement="right" title="At least 6 characters" required autofocus/>
            <form:errors path="username" cssClass="alert-danger" />
        <%--</div>--%>
        <%--<div class="input-group">--%>
            <input type="password" name="password" class="form-control" placeholder="Password"
                   data-placement="right" title="At least 6 characters" required/>
            <form:errors path="password" cssClass="alert-danger" />
        <%--</div>--%>
        <%--<div class="input-group">--%>
            <input type="password" name="confirmedPassword" class="form-control" placeholder="Confirm Password"
                   data-placement="right" title="At least 6 characters" required/>
            <form:errors path="confirmedPassword" cssClass="alert-danger" />
        <%--</div>--%>

        <%--<div class="input-group">--%>
            <form:input path="identifier" type="text" class="form-control" placeholder="ID Card Number" data-placement="right"
                        title="15 or 18 characters"/>
            <form:errors path="identifier" cssClass="alert-danger" />
        <%--</div>--%>
                <%--<div class="input-group">--%>
            <form:input path="email" type="email" class="form-control" placeholder="Email" data-placement="right"
                        title="15 or 18 characters"/>
                <form:errors path="email" cssClass="alert-danger" />
                <%--</div>--%>
        <%--<div class="input-group">--%>
            <form:input path="firstName" type="text" class="form-control" placeholder="First Name" data-placement="right"
                   title="At least 3 characters"/>
            <form:errors path="firstName" cssClass="alert-danger" />
        <%--</div>--%>
        <%--<div class="input-group">--%>
            <form:input path="lastName" type="text" class="form-control" placeholder="Last Name" data-placement="right" title="Your last name"/>
            <form:errors path="lastName" cssClass="alert-danger" />
        <%--</div>--%>

        <div id="signupTerms" class="modal fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content">

                    <div class="modal-header">
                        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                        <h4 class="modal-title" id="myModalLabel">Terms</h4>
                    </div>
                    <div class="modal-body">
                        <h4>Overflowing text to show scroll behavior</h4>
                        <p>Cras mattis consectetur purus sit amet fermentum. Cras justo odio, dapibus ac facilisis in, egestas eget quam. Morbi leo risus, porta ac consectetur ac, vestibulum at eros.</p>
                        <p>Praesent commodo cursus magna, vel scelerisque nisl consectetur et. Vivamus sagittis lacus vel augue laoreet rutrum faucibus dolor auctor.</p>
                        <p>Aenean lacinia bibendum nulla sed consectetur. Praesent commodo cursus magna, vel scelerisque nisl consectetur et. Donec sed odio dui. Donec ullamcorper nulla non metus auctor fringilla.</p>
                        <p>Cras mattis consectetur purus sit amet fermentum. Cras justo odio, dapibus ac facilisis in, egestas eget quam. Morbi leo risus, porta ac consectetur ac, vestibulum at eros.</p>
                        <p>Praesent commodo cursus magna, vel scelerisque nisl consectetur et. Vivamus sagittis lacus vel augue laoreet rutrum faucibus dolor auctor.</p>
                        <p>Aenean lacinia bibendum nulla sed consectetur. Praesent commodo cursus magna, vel scelerisque nisl consectetur et. Donec sed odio dui. Donec ullamcorper nulla non metus auctor fringilla.</p>
                        <p>Cras mattis consectetur purus sit amet fermentum. Cras justo odio, dapibus ac facilisis in, egestas eget quam. Morbi leo risus, porta ac consectetur ac, vestibulum at eros.</p>
                        <p>Praesent commodo cursus magna, vel scelerisque nisl consectetur et. Vivamus sagittis lacus vel augue laoreet rutrum faucibus dolor auctor.</p>
                        <p>Aenean lacinia bibendum nulla sed consectetur. Praesent commodo cursus magna, vel scelerisque nisl consectetur et. Donec sed odio dui. Donec ullamcorper nulla non metus auctor fringilla.</p>
                    </div>
                    <div class="modal-footer">
                        <%--<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>--%>
                        <button type="button" class="btn btn-primary" data-dismiss="modal">Confirm</button>
                    </div>

                </div><!-- /.modal-content -->
            </div><!-- /.modal-dialog -->
        </div><!-- /.modal -->

        <label class="checkbox">
            <input type="checkbox" name="agreed"> I agree with <a data-toggle="modal" data-target="#signupTerms">terms</a>
            <form:errors path="agreed" cssClass="alert-danger" />
        </label>
    </fieldset>
    <button class="btn btn-lg btn-primary btn-block" type="submit">Sign Up</button>
</form:form>


<hr />
<p>Or you can Login with one of the following</p>
<!-- TWITTER SIGNIN -->
<%--<form id="tw_signin" action="<c:url value="/signin/twitter"/>" method="POST">--%>
<%--<button type="submit"><img src="<c:url value="/resources/social/twitter/sign-in-with-twitter-d.png"/>" /></button>--%>
<%--</form>--%>

<!-- FACEBOOK SIGNIN -->
<%--<form name="fb_signin" id="fb_signin" action="<c:url value="/signin/facebook"/>" method="POST">--%>
<%--<input type="hidden" name="scope" value="publish_stream,user_photos,offline_access" />--%>
<%--<button type="submit"><img src="<c:url value="/image/w_logo.jpg"/>" /></button>--%>
<%--</form>--%>

<!-- weibo SIGNIN -->
<%--<form name="wb_signin" id="wb_signin" action="<c:url value="/signin/weibo"/>" method="POST">--%>
<c:url value="/signin/weibo" var="signinWeiboUrl" />
<form:form  class="form-signup" name="wb_signin" id="wb_signin" action="${signinWeiboUrl}" method="POST">
    <input type="hidden" name="scope" value="publish_stream,user_photos,offline_access" />
    <button type="submit"><img src="<c:url value="/image/w_logo.jpg"/>" /></button>
</form:form>
<content tag="additionalJs">
    <script src="${ctx}/js/tooltip.js"></script>
</content>