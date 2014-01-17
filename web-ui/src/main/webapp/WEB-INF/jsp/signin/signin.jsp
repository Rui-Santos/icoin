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

<p>You need to login to access this part of the site. Please provided your username and password</p>

<c:if test="${not empty param.login_error}">
    <div class="alert-message error">
        <p>
            <strong>Your login attempt was not successful, try again.</strong>
        </p>

        <p>
            Reason: <c:out value="${SPRING_SECURITY_LAST_EXCEPTION.message}"/>.
        </p>
    </div>
</c:if>

<form name="f" action="<c:url value='${ctx}/signin/authenticate'/>" method="POST" class="form-stacked">
    <fieldset>
        <legend>Login to get access</legend>
        <div class="clearfix">
            <label for="j_username">Username</label>

            <div class="input">
                <input type='text' id="j_username" name='j_username'
                       value='<c:if test="${not empty param.login_error}"><c:out value="${SPRING_SECURITY_LAST_USERNAME}"/></c:if>'/>
            </div>
        </div>
        <div class="clearfix">
            <label for="j_password">Password</label>

            <div class="input">
                <input type='password' id="j_password" name='j_password'/>
            </div>
        </div>
        <div class="clearfix">
            <label>
                <input type="checkbox" name="_spring_security_remember_me">
                <span>Don't ask for my password for two weeks</span>
            </label>
        </div>
        <div class="actions">
            <input name="submit" type="submit" class="btn primary">
            <input name="reset" type="reset" class="btn">
        </div>
    </fieldset>

    <tr>
        <td colspan='2'></td>
    </tr>
    </table>
    <p>Or you can <a href="<c:url value="/signup"/>">signup</a> with a new account.</p>

</form>


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
<form name="wb_signin" id="wb_signin" action="<c:url value="/signin/weibo"/>" method="POST">
    <input type="hidden" name="scope" value="publish_stream,user_photos,offline_access" />
    <button type="submit"><img src="<c:url value="/image/w_logo.jpg"/>" /></button>
</form>