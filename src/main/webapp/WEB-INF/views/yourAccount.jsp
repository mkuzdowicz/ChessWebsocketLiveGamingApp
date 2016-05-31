<jsp:include page="includes/header.jsp"></jsp:include>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<div class="container-fluid">
	<jsp:include page="includes/menu.jsp"></jsp:include>
	<div class="main-wrapper">
		<div class="user-profile-form">
			<h3 class="text-center">
				<span class="glyphicon glyphicon-user text-primary"></span> Your
				profile data
			</h3>
			<jsp:include page="includes/forms/editYourAccountForm.jsp" />
		</div>
	</div>
</div>
<jsp:include page="includes/footer.jsp"></jsp:include>