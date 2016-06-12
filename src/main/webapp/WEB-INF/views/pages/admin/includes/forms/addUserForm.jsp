<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<form:form method="POST" commandName="signUpForm"
	class="form-horizontal" name="addUserForm">

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
		<label class="col-sm-8 col-sm-offset-2"> <form:checkbox
				path="grantAdminAuthorities" /> grant admin authorities
		</label>
	</div>

	<c:if test="${ formActionMsg ne null }">
		<div class="form-group col-sm-12">
			<c:if test="${ formActionMsg.type eq errorMsg }">
				<div class="alert alert-danger text-center">${formActionMsg.body}</div>
			</c:if>
			<c:if test="${ formActionMsg.type eq successMsg }">
				<div class="alert alert-success text-center">${formActionMsg.body}</div>
			</c:if>
		</div>
	</c:if>

	<input type="hidden" name="${_csrf.parameterName}"
		value="${_csrf.token}" />

	<div class="form-group">
		<div class="col-sm-offset-2 col-sm-8">
			<input class="btn btn-success btn-block" type="submit"
				value="add account" />
		</div>
	</div>
</form:form>