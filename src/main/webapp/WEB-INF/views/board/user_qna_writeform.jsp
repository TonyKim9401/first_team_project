<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:if test="${loginOkBean.a_level eq 3}">
	<%@ include file="../admin/admin_user_top.jsp" %>
</c:if>
<c:if test="${loginOkBean.a_level ne 3}">
	<%@ include file="../user/user_myPage.jsp" %>
</c:if>
<!-- user_qna_writeForm.jsp -->
<script type="text/javascript">
	function inputChk() {
		if(f_writeUserQnA.subject.value=="") {
			alert("제목을 입력해 주세요.")
			f_writeUserQnA.subject.focus()
			return false
		}
		if(f_writeUserQnA.content.value=="") {
			alert("내용을 입력해 주세요.")
			f_writeUserQnA.content.focus()
			return false
		}
		return true
	}
</script>
<div align="center">
<form name="f_writeUserQnA" action="write_userQnA_ok" method="post" onsubmit="return inputChk()">
<input type="hidden" name="uqa_num" value="${param.uqa_num}">
<input type="hidden" name="uqa_re_step" value="${param.uqa_re_step}">
<input type="hidden" name="uqa_re_level" value="${param.uqa_re_level}">
<input type="hidden" name="u_num" value="${loginOkBean.u_num}">
<input type="hidden" name="u_num_parent" value="${param.u_num_parent}">
<table border="1" width="600">
	<caption>회원 Q&A 글쓰기</caption>
	<tr>
		<td bgcolor="lightgrey" align="center" width="15%">회원명</td>
		<td width="85%">${loginOkBean.u_nickname}</td>
	</tr>
	<tr>
		<td bgcolor="lightgrey" align="center" width="15%" placeholder="제목을 입력 해 주세요">제 목</td>
		<td width="85%"><input type="text" name="uqa_subject" size="65"></td>
	</tr>
	<tr>
		<td bgcolor="lightgrey" align="center" width="15%">내 용</td>
		<td width="85%">
			<textarea name="uqa_contents" cols="65" rows="12" placeholder="내용을 입력 해 주세요"></textarea>
		</td>
	</tr>
	<tr>
		<td align="center" colspan="2">
			<input type="submit" value="글쓰기">
			<input type="button" value="목록보기" onclick="window.location='list_userQnA'">
		</td>
	</tr>
</table>
</form>
</div>
<%@ include file="../bottom.jsp" %>