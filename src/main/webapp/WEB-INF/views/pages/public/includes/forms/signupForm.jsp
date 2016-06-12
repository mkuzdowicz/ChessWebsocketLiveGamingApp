<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<c:choose>
	<c:when test="${ msg != null }">
		<div class="alert alert-danger">${msg}</div>
	</c:when>
</c:choose>
<form:form method="POST" commandName="signUpForm"
	class="form-horizontal" name="signUpForm">

	<div class="form-group">
		<label class="control-label col-sm-2">login</label>
		<div class="col-sm-8">
			<form:input path="username" type="text" class="form-control" />
			<form:errors path="username" cssClass="alert-danger danger" />
		</div>
	</div>

	<div class="form-group">
		<label class="control-label col-sm-2">email</label>
		<div class="col-sm-8">
			<form:input path="email" type="email" class="form-control" />
			<form:errors path="email" cssClass="alert-danger danger" />
		</div>
	</div>

	<div class="form-group">
		<label class="control-label col-sm-2">password</label>
		<div class="col-sm-8">
			<form:input path="password" type="password" class="form-control" />
			<form:errors path="password" cssClass="alert-danger danger" />
		</div>
	</div>

	<div class="form-group">
		<label class="control-label col-sm-2">confirm password</label>
		<div class="col-sm-8">
			<form:input path="confirmPassword" type="password"
				class="form-control" />
			<form:errors path="confirmPassword" cssClass="alert-danger danger" />
		</div>
	</div>

	<div class="form-group">
		<div class="col-sm-offset-2 col-sm-8">
			<input class="btn btn-success btn-block" type="submit"
				value="create account" />
		</div>
	</div>

	<c:if test="${errorMessage ne null}">
		<h2 class="alert alert-danger text-center">${errorMessage}</h2>
	</c:if>

	<input type="hidden" name="${_csrf.parameterName}"
		value="${_csrf.token}" />
</form:form>