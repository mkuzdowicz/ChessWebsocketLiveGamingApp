<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<jsp:include page="includes/header.jsp" />
<div class="container-fluid">
	<jsp:include page="includes/menu.jsp"></jsp:include>
	<div class="main-wrapper">
		<div class="sign-login-form">
			<h1 class="text-center">
				create your account &nbsp;<span
					class="glyphicon glyphicon-cloud-upload"></span>
			</h1>

			<hr />
			<jsp:include page="includes/forms/signupForm.jsp" />

			<hr />
		</div>
	</div>
</div>
<jsp:include page="includes/footer.jsp" />