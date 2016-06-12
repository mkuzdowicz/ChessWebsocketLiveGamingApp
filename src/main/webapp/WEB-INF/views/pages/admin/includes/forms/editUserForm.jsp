<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/security/tags"
	prefix="security"%>



<form:form action="/admin/users/edit-user" method="POST"
	commandName="editForm" name="editUserForm" class="form-horizontal">
	<div class="form-group">
		<label class="control-label col-sm-2">login</label>
		<div class="col-sm-8">
			<form:input type="text" class="form-control" readonly="true"
				path="username" />
		</div>
	</div>

	<div class="form-group">
		<label class="control-label col-sm-2">name</label>
		<div class="col-sm-8">
			<form:input path="name" type="text" class="form-control" />
			<form:errors path="name" cssClass="alert-danger danger" />
		</div>
	</div>

	<div class="form-group">
		<label class="control-label col-sm-2">lastname</label>
		<div class="col-sm-8">
			<form:input path="lastname" type="text" class="form-control" />
			<form:errors path="lastname" cssClass="alert-danger danger" />
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
		<label class="col-sm-8 col-sm-offset-2"> <form:checkbox
				path="grantAdminAuthorities" /> grant admin authorities
		</label>
	</div>

	<div class="form-group">
		<label class="col-sm-8 col-sm-offset-2"> <form:checkbox
				path="accountConfirmed" /> accountConfirmed
		</label>
	</div>

	<c:if test="${changePasswordCheckBoxIsChecked}">
		<div class="form-group">
			<label class="control-label col-sm-2"></label>
			<div class="col-sm-8">
				<form:errors path="password" cssClass="alert-danger danger" />
			</div>
		</div>
	</c:if>

	<div id="passwordChangeInputs">
		<div class="form-group">
			<label class="col-sm-2 control-label"> userPassword: </label>
			<div class="col-sm-8">
				<form:input path="password" class="form-control" type="password" />
			</div>
		</div>

		<div class="form-group">
			<label class="col-sm-2 control-label">Confirm password: </label>
			<div class="col-sm-8">
				<form:input path="confirmPassword" class="form-control"
					type="password" />

			</div>
		</div>
	</div>

	<div class="form-group">
		<label class="col-sm-8 col-sm-offset-2"> <form:checkbox
				path="changePasswordFlag" id="changePasswordCheckBox" /> change
			password
		</label>
	</div>

	<input type="hidden" name="${_csrf.parameterName}"
		value="${_csrf.token}" />

	<form:input type="hidden" path="userId" />

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

	<div class="form-group">
		<div class="col-sm-offset-2 col-sm-8">
			<input class="btn btn-success btn-block" type="submit"
				value="change data" />
		</div>
	</div>

</form:form>
<script>
	$(document).ready(function() {

		$('#passwordChangeInputs').hide();
		$('#changePasswordCheckBox').attr('checked', false);

	});

	$('#changePasswordCheckBox').change(function() {
		if (this.checked) {
			$('#passwordChangeInputs').show();
		} else {

			$('#passwordChangeInputs').hide();
		}
	});
</script>