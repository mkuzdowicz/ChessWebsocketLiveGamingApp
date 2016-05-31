<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<div class="main-wrapper">
	<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

	<a id="add-new-user-btn" class="btn btn-primary btn-block"
		href="/admin/users/addUser">add new user</a>
	<div id="usersTable" class="tab-pane fade in active">

		<h3 class="text-center">
			<span class="glyphicon glyphicon-user"></span> USERS <span
				class="glyphicon glyphicon-user"></span>
		</h3>


		<div class="table-responsive">
			<table id="usersTableForDataTableJS"
				class="table table-condensed table-striped table-bordered table-accounts">
				<thead>
					<tr>
						<td style="width: 10px !important">registration date</td>
						<td>login</td>
						<td>role</td>
						<td style="width: 5px !important">is confirmed</td>
						<td>name</td>
						<td>lastname</td>
						<td style="width: 10px !important">email</td>
						<td>actions</td>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="user" items="${users}">
						<tr>
							<td class="info text-center"><fmt:formatDate
									pattern="dd-MM-yyyy" value="${user.registrationDate}" /></td>
							<td class="text-center"><c:out value="${user.username}" /></td>
							<c:choose>
								<c:when test="${user.role eq 1}">
									<td class="danger"><p class="text-center">admin</p></td>
								</c:when>
								<c:otherwise>

									<td class="warning"><p class="text-center">user</p></td>
								</c:otherwise>

							</c:choose>
							<c:choose>
								<c:when test="${user.isRegistrationConfirmed}">
									<td class="success"><p class="text-center">yes</p></td>
								</c:when>
								<c:otherwise>
									<td class="danger"><p class="text-center">no</p></td>
								</c:otherwise>
							</c:choose>
							<td class="text-center"><c:out value="${user.name}" /></td>
							<td class="text-center"><c:out value="${user.lastname}" /></td>
							<td class="text-center"><c:out value="${user.email}" /></td>
							<td class="text-center"><a
								class="btn btn-success btn-block btn-sm"
								href="/admin/users/editUser/?login=<c:out value="${user.username}"/>">edit</a>
								<a data-toggle="modal" data-target="#removeUser"
								class="btn btn-danger btn-block btn-sm"
								data-id="<c:out value="${user.username}" />">remove</a></td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>

	</div>
</div>
<jsp:include page="includes/modal_boxes/removeUserModal.jsp"></jsp:include>

