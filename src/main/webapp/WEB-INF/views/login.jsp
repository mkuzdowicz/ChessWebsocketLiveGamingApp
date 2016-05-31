<jsp:include page="includes/header.jsp" />
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<div class="container-fluid">
	<jsp:include page="includes/menu.jsp"></jsp:include>
	<div class="main-wrapper">
		<div class="sign-login-form">

			<h3 class="text-center">
				log in &nbsp;<span class="glyphicon glyphicon-tower"></span>
			</h3>
			<hr />
			<jsp:include page="includes/forms/logInForm.jsp" />

			<hr />
		</div>
	</div>
</div>
<jsp:include page="includes/footer.jsp" />