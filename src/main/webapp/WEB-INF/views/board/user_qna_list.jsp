<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:if test="${loginOkBean.a_level eq 3}">
	<%@ include file="../admin/admin_user_top.jsp" %>
</c:if>
<c:if test="${loginOkBean.a_level ne 3}">
	<%@ include file="../user/user_myPage.jsp" %>
</c:if>
<!-- user_qna_list.jsp -->
<div align="center">
<table width="600">
	<c:if test="${loginOkBean.a_level eq 3}">
	<caption>회원 Q&A 목록 (관리자)</caption>
	</c:if>
	<c:if test="${loginOkBean.a_level ne 3}">
	<caption>회원 Q&A 목록</caption>
	<tr>
		<td colspan="6" align="right">
			<input type="button" value="글쓰기" onclick="window.location='write_userQnA'">
		</td>
	</tr>
	</c:if>
	<tr bgcolor="lightgrey">
		<th width="10%">번호</th>
		<th width="60%">제목</th>
		<th width="15%">회원명</th>
		<th width="15%">작성일</th>
	</tr>

	<c:if test="${empty listUserQnA}">
	<tr>
		<td colspan="6">등록된 게시글이 없습니다.</td>
	</tr>		
	</c:if>
	<c:set var="orderNumber" value="${orderNumber}"/>
	<c:forEach var="uqadto" items="${listUserQnA}">
	<tr>
		<%-- <td align="center">${uqadto.uqa_num}</td> --%>
		<td align="center">${orderNumber}</td>
		<c:set var="orderNumber" value="${orderNumber - 1}"/>
		<td>
			<c:if test="${uqadto.uqa_re_level>0}">
			<img src="resources/images/level.gif" width="${uqadto.uqa_re_level*10}">
			<img src="resources/images/re.gif">
			</c:if>
		<a href="get_userQnA?uqa_num=${uqadto.uqa_num}">
			${uqadto.uqa_subject}
		</a>
		</td>
		<td align="center">${uqadto.u_nickname}</td>
		<td align="center">${uqadto.uqa_joindate}</td>
	</tr>	
	</c:forEach>
</table>
<c:if test="${loginOkBean.a_level eq 3 }">
	<c:if test="${rowCount>0}">
		<c:if test="${startPage>linkSize}">
			<a href="list_userQnA?pageNum=${startPage-linkSize}">[이전]</a>
		</c:if>
		<c:forEach var="i" begin="${startPage}" end="${endPage}">
			<a href="list_userQnA?pageNum=${i}">[${i}]</a>
		</c:forEach>
		<c:if test="${endPage<pageCount}">
			<a href="list_userQnA?pageNum=${startPage+linkSize}">[다음]</a>			
		</c:if>
	</c:if>
</c:if>
</div>
<%@ include file="../bottom.jsp" %>