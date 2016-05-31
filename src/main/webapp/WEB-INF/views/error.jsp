<jsp:include page="includes/header.jsp" />
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<div class="container-fluid">
	<jsp:include page="includes/menu.jsp"></jsp:include>
	<br />
	<div class="main-wrapper">
		<div class="message-box">
			<br /> <br />

			<h2 class="alert alert-danger text-center">
				<span class="glyphicon glyphicon-fire text-danger"></span>
				${errorMessage}
			</h2>
			<br /> <br />
		</div>
	</div>
</div>
<jsp:include page="includes/footer.jsp" />