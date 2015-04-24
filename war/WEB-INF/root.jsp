<%@ page import="com.google.appengine.api.blobstore.BlobstoreServiceFactory" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
	<head>
		<meta charset="UTF-8">
		<title>Assignment02</title>
	</head>
	<body>
		<c:choose>
			<c:when test="${user != null}">
				<h1>Welcome to FileDrop</h1>
				<p>
					Signed in as ${user.email} <br/>
					<a href="${logout}">Log out</a>
				</p>
				
				<form action="/directory" method="post">
					Add new directory: <input type="text" name="directory"/>
					<input type="submit" value="Add" />
				</form>
				
				<form enctype="multipart/form-data" method="post" action="<%= BlobstoreServiceFactory.getBlobstoreService().createUploadUrl("/files") %>">
					Upload new file: <input type="file" name="file" size="30"/>
					<input type="submit" value="Upload" />
				</form>
				
				<h2>${result}</h2>
				<c:choose>
					<c:when test="${root == change}">
						<form method="get" action="/directory">
							<input type="submit" name="action" value="Change" />
							<input type="hidden" name="dir" value=".."/>
							../
						</form>
					</c:when>
				</c:choose>
				
				<c:forEach items="${subdirs}" var="key" begin="0" varStatus="loop">
					<form method="get" action="/directory">
						<input type="hidden" name="dir" value="${key}"/>
						<input type="submit" name="action" value="Change" />
						<input type="submit" name="action" value="Delete" />
						${key}
					</form>
				</c:forEach>
				
				<c:forEach items="${files}" var="key" begin="0" varStatus="loop">
					<form method="get" action="/files">
						<input type="submit" name="action" value="Download" />
						<input type="submit" name="action" value="Delete" />
						<input type="hidden" name="key_value" value="${loop.index}"/>
						${key}
					</form>
				</c:forEach>
				
			</c:when>
			<c:otherwise>
				<h1>Welcome to FileDrop</h1>
				<p>
					<a href="${login}"> Sign in</a> to get access
				</p>
			</c:otherwise>
		</c:choose>
		
	</body>
</html>