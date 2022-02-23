<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<link rel="stylesheet" href="https://pro.fontawesome.com/releases/v5.10.0/css/all.css" integrity="sha384-AYmEC3Yw5cVb3ZcuHtOA93w35dYTsvhLPVnYs9eStHfGJvOvKxVfELGroGkvsg+p" crossorigin="anonymous"/>
<!-- ICon만들때 적어줘야함! -->
<link rel="stylesheet" href="resources/style.css"/>
<link rel="stylesheet" href="resources/LJWstyle.css"/>
<form name="f_bookDetail" method="post" action="nonUser_bookCancel_ok">
	<input type="hidden" name="book_num" value="${bdto.book_num}">
	<div class="column review border-bottom">
					<div class="row">
						<div class="flex column">
							<span class="test_left">
										<!-- 경로수정필요 -->
							<label><img src="resources/images/hotel/${hdto.h_image1}" width="390"></label><br>
							<label><font><b>호텔명: </b></font> ${hdto.h_name}<br></label>
							<label><font><b>성급: </b></font>${hdto.h_grade}<br></label>
							<label><font><b>객실타입: </b></font>${bdto.book_roomtype}<br></label>
							
							</span>
							<span>
							<font><b>호텔주소: </b></font>${fn:replace(hdto.h_address, '@', ' ')}
							</span>
						</div>
					</div>
				</div>
		<div>
	<div style="width: 400px; margin: 0 auto;">
		<div class="row justify-center align-center border-bottom" style="height: 80px;">
			결제 상세 내역
		</div>
		<div class="payment-info">
			<div>
				<label>예약번호</label>
				<label>${bdto.book_num}</label>
			</div>
			<div>
				<label>결제일시(예약날짜)</label>
				<label>${bdto.book_joindate}</label>
			</div>
			<div>
				<label>총 결제금액</label>
				<label>${bdto.book_totalprice}</label>
			</div>
			
			<div class="border-bottom">
				<label>최종환불금액</label>
				<label><font color="pink">${bdto.book_totalprice}</font></label>
			</div>
		</div>
		<div class="row justify-center button-actions">
			<button type="submit" style="background:#F5D19C;">예약취소 확인</button>
		</div>
	</div>
</form>