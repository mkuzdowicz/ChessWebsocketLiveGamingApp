<%@ page import="com.kuzdowicz.livegaming.chess.app.props.*"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%
	String contextURL = ChessAppProperties
			.getProperty("app.contextpath");
%>
<form:form method="POST" commandName="loginForm"
	cssClass="form-horizontal" name="loginForm">
	<div class="form-group">
		<label class="control-label col-sm-3">login</label>
		<div class="col-sm-6">
			<form:input path="username" type="text" cssClass="form-control"
				pattern=".{5,12}" required="true" title="5 to 12 characters minimum" />
		</div>
	</div>

	<div class="form-group">
		<label class="control-label col-sm-3">password</label>
		<div class="col-sm-6">
			<form:input path="password" type="password" cssClass="form-control"
				pattern="((?=.*\d)(?=.*[a-z])(?=.*[A-Z]).{8,30})" required="true"
				title="password should have at least one digit, at least one capital letter, be between 8 - 30 characters" />
		</div>
	</div>

	<form:errors path="*" cssStyle="color: #ff0000;" />

	<div class="form-group">
		<div class="col-sm-offset-3 col-sm-6">
			<input class="btn btn-info btn-block" type="submit" value="log in" />
			<a class="btn btn-success btn-block" href="<%=contextURL%>/signup">sign
				up</a>
		</div>
	</div>


</form:form>