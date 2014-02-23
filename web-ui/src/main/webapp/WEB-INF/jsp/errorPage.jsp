<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div class="container-min-height">
    <c:if test="${not empty errorTitle}">
    <h3 class="text-danger text-center">
        System Error: ${errorTitle}
    </h3>
    </c:if>
    <c:if test="${not empty errorTitle}">
        <h5 class="text-danger text-center">
            ${errorMessage}
        </h5>
    </c:if>

</div>