package com.ezen.project;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.ezen.project.model.CompanyQnADTO;
import com.ezen.project.model.ReviewActDTO;
import com.ezen.project.model.ReviewDTO;
import com.ezen.project.model.UserQnADTO;
import com.ezen.project.service.HotelMapper;
import com.ezen.project.service.QnABoardMapper;
import com.ezen.project.service.UserMyPageMapper;

@Controller
public class BoardController {
	
	@Autowired
	private QnABoardMapper qnaBoardMapper;
	
	@Autowired
	private UserMyPageMapper userMyPageMapper;
	
	@Autowired
	private HotelMapper hotelMapper;
	
	@Resource(name = "uploadPath")
	private String upPath;
	
	@RequestMapping("/list_companyQnA")
	public String listCompanyQnA(HttpServletRequest req, @RequestParam(required=false) String pageNum) {
		HttpSession session = req.getSession();

		LoginOkBeanCompany companyLoginOkBean = (LoginOkBeanCompany)session.getAttribute("companyLoginOkBean");
		
		if(pageNum == null) pageNum = "1";
		// 따로 누른 페이지가 없이 초기화면일때 pageNum을 1로 설정
		
		int pageSize = 7;
		// 한페이지에 표시되는 글목록의 수

		int currentPage = Integer.parseInt(pageNum);
		// pageNum을 int형태로 변환해서 currentPage로 저장
		
		int startRow = (currentPage - 1) * pageSize + 1;
		// 1P:1, 2P:6, 3P:11, 4P:16....
		
		int endRow = startRow + (pageSize - 1);
		// 1P:5, 2P:10, 3P:15, 4P:20....
		
		int rowCount = qnaBoardMapper.getAdminCompanyQnACount();
		/*
		int rowCount = 0;
		
		if(companyLoginOkBean.getA_level().equals("3")) {
			rowCount = qnaBoardMapper.getAdminCompanyQnACount();
		}else {
			rowCount = qnaBoardMapper.getCompanyQnACount(companyLoginOkBean.getU_num());
		}
		*/
		if(endRow>rowCount) endRow = rowCount;
		// 5개씩 끊어지면 마지막에 5의배수가 아니면 개수가 안맞으므로 끝번호에 맞게 endRow를 조정
		
		List<CompanyQnADTO> list = null;
		
		if(companyLoginOkBean.getA_level().equals("3")) {
			list = qnaBoardMapper.listAdminCompanyQnA(startRow, endRow);
		}else {
			list = qnaBoardMapper.listCompanyQnA(companyLoginOkBean.getC_num());
		}
		
		for(CompanyQnADTO cqadto : list) {
			if(qnaBoardMapper.getCnameByCnum(cqadto.getC_num()) == null) {
				cqadto.setC_name("탈퇴기업");
			}else {
				cqadto.setC_name(qnaBoardMapper.getCnameByCnum(cqadto.getC_num()));
			}
		}
		
		int linkSize = 0;
		int pageCount = 0;
		int startPage = 0;
		int endPage = 0;
		
		if(rowCount>0) {
			linkSize = 5;
			// 화면 하단에 나타낼 페이지링크 수

			pageCount = rowCount / pageSize + (rowCount%pageSize == 0 ? 0 : 1);
			
			startPage = ((currentPage-1) / linkSize) * linkSize + 1;
			// 화면에 나타낼 페이지링크의 처음
			
			endPage = startPage+linkSize-1;
			// 화면에 나타낼 페이지링크의 마지막 (현재페이지+페이지링크수-1)
			
			if(endPage>pageCount) endPage=pageCount;
		}
		
		req.setAttribute("listCompanyQnA", list);
		req.setAttribute("rowCount", rowCount);
		req.setAttribute("linkSize", linkSize);
		req.setAttribute("pageCount", pageCount);
		req.setAttribute("startPage", startPage);
		req.setAttribute("endPage", endPage);
		
		return "board/company_qna_list";
	}
	
	@RequestMapping("/get_companyQnA")
	public String getCompanyQnA(HttpServletRequest req, @RequestParam int cqa_num) {
		CompanyQnADTO cqadto = qnaBoardMapper.getCompanyQnA(cqa_num);
		cqadto.setC_name(qnaBoardMapper.getCnameByCnum(cqadto.getC_num()));
		req.setAttribute("cqadto", cqadto);

		return "board/company_qna_content";
	}
	
	@RequestMapping("/write_companyQnA")
	public String writeCompanyQnA() {
		return "board/company_qna_writeform";
	}
	
	@RequestMapping("/write_companyQnA_ok")
	public String writeCompanyQnAOk(HttpServletRequest req, @ModelAttribute CompanyQnADTO cqadto, BindingResult result) {
		if(result.hasErrors()) {
			cqadto.setCqa_num(0);
			cqadto.setCqa_re_step(0);
			cqadto.setCqa_re_level(0);
		}
		
		if(cqadto.getC_num_parent()==0) cqadto.setC_num_parent(cqadto.getC_num());

		int res = qnaBoardMapper.insertCompanyQnA(cqadto);
		
		if(res>0) {
			req.setAttribute("msg", "게시판에 글이 등록 되었습니다.");
			req.setAttribute("url", "list_companyQnA");
		}else {
			req.setAttribute("msg", "등록 오류 발생. 관리자에게 문의해 주세요.");
			req.setAttribute("url", "write_companyQnA");
		}
		
		return "message";
	}
	
	@RequestMapping("/delete_companyQnA_ok")
	public String deleteCompanyQnAOk(HttpServletRequest req, int cqa_num) {
		int res = qnaBoardMapper.deleteCompanyQnA(cqa_num);
		
		if(res>0) {
			req.setAttribute("msg", "글을 삭제하였습니다. 글 목록 페이지로 이동합니다.");
			req.setAttribute("url", "list_companyQnA");
		}else {
			req.setAttribute("msg", "삭제 오류 발생. 관리자에게 문의해 주세요.");
			req.setAttribute("url", "get_companyQnA?cqa_num="+cqa_num);
		}			
		
		return "message";
	}
	
	@RequestMapping("/update_companyQnA")
	public String updateCompanyQnA(HttpServletRequest req, int cqa_num) {
		CompanyQnADTO cqadto = qnaBoardMapper.getCompanyQnA(cqa_num);
		cqadto.setC_name(qnaBoardMapper.getCnameByCnum(cqadto.getC_num()));
		req.setAttribute("cqadto", cqadto);
		
		return "board/company_qna_updateform";
	}
	
	@RequestMapping("/update_companyQnA_ok")
	public String updateProBoard(HttpServletRequest req, CompanyQnADTO cqadto) {
		int res = qnaBoardMapper.updateCompanyQnA(cqadto);
		
		if(res>0) {
			req.setAttribute("msg", "글이 수정되었습니다. 글 목록 페이지로 이동합니다.");
			req.setAttribute("url", "list_companyQnA");
		}else {
			req.setAttribute("msg", "수정 오류 발생. 관리자에게 문의해 주세요.");
			req.setAttribute("url", "get_companyQnA?cqa_num="+cqadto.getCqa_num());
		}
		
		return "message";
	}
	
	@RequestMapping("/list_userQnA")
	public String listUserQnA(HttpServletRequest req, @RequestParam(required=false) String pageNum) {
		HttpSession session = req.getSession();
		
		LoginOkBeanUser loginOkBean = (LoginOkBeanUser)session.getAttribute("loginOkBean");
		
		if(pageNum == null) pageNum = "1";
		// 따로 누른 페이지가 없이 초기화면일때 pageNum을 1로 설정
		
		int pageSize = 7;
		// 한페이지에 표시되는 글목록의 수

		int currentPage = Integer.parseInt(pageNum);
		// pageNum을 int형태로 변환해서 currentPage로 저장
		
		int startRow = (currentPage - 1) * pageSize + 1;
		// 1P:1, 2P:6, 3P:11, 4P:16....
		
		int endRow = startRow + (pageSize - 1);
		// 1P:5, 2P:10, 3P:15, 4P:20....
		
		int rowCount = qnaBoardMapper.getAdminUserQnACount();
		/*
		int rowCount = 0;
		
		if(loginOkBean.getA_level().equals("3")) {
			rowCount = qnaBoardMapper.getAdminUserQnACount();
		}else {
			rowCount = qnaBoardMapper.getUserQnACount(loginOkBean.getU_num());
		}
		*/
		if(endRow>rowCount) endRow = rowCount;
		// 5개씩 끊어지면 마지막에 5의배수가 아니면 개수가 안맞으므로 끝번호에 맞게 endRow를 조정
		
		List<UserQnADTO> list = null;
		
		
		if(loginOkBean.getA_level().equals("3")) {
			list = qnaBoardMapper.listAdminUserQnA(startRow, endRow);
		}else {
			list = qnaBoardMapper.listUserQnA(loginOkBean.getU_num());
		}
		
		//글 번호 순서
		int orderNumber = 0;
		if(loginOkBean.getA_level().equals("3")) {
			orderNumber = rowCount;
		}else {
			orderNumber = qnaBoardMapper.getUserQnACount(loginOkBean.getU_num());
		}
		
		for(UserQnADTO uqadto : list) {
			if(qnaBoardMapper.getUnicknameByUnum(uqadto.getU_num()) == null) {
				uqadto.setU_nickname("탈퇴회원");
			}else {
				uqadto.setU_nickname(qnaBoardMapper.getUnicknameByUnum(uqadto.getU_num()));
			}
		}
		
		int linkSize = 0;
		int pageCount = 0;
		int startPage = 0;
		int endPage = 0;
		
		if(rowCount>0) {
			linkSize = 5;
			// 화면 하단에 나타낼 페이지링크 수

			pageCount = rowCount / pageSize + (rowCount%pageSize == 0 ? 0 : 1);
			
			startPage = ((currentPage-1) / linkSize) * linkSize + 1;
			// 화면에 나타낼 페이지링크의 처음
			
			endPage = startPage+linkSize-1;
			// 화면에 나타낼 페이지링크의 마지막 (현재페이지+페이지링크수-1)
			
			if(endPage>pageCount) endPage=pageCount;
		}
		
		req.setAttribute("listUserQnA", list);
		req.setAttribute("rowCount", rowCount);
		req.setAttribute("orderNumber", orderNumber);
		req.setAttribute("linkSize", linkSize);
		req.setAttribute("pageCount", pageCount);
		req.setAttribute("startPage", startPage);
		req.setAttribute("endPage", endPage);
		
		return "board/user_qna_list";
	}
	
	@RequestMapping("/get_userQnA")
	public String getUserQnA(HttpServletRequest req, @RequestParam int uqa_num) {
		UserQnADTO uqadto = qnaBoardMapper.getUserQnA(uqa_num);
		uqadto.setU_nickname(qnaBoardMapper.getUnicknameByUnum(uqadto.getU_num()));
		req.setAttribute("uqadto", uqadto);

		return "board/user_qna_content";
	}
	
	@RequestMapping("/write_userQnA")
	public String writeUserQnA() {
		return "board/user_qna_writeform";
	}
	
	@RequestMapping("/write_userQnA_ok")
	public String writeUserQnAOk(HttpServletRequest req, @ModelAttribute UserQnADTO uqadto, BindingResult result) {
		if(result.hasErrors()) {
			uqadto.setUqa_num(0);
			uqadto.setUqa_re_step(0);
			uqadto.setUqa_re_level(0);
		}
		
		if(uqadto.getU_num_parent()==0) uqadto.setU_num_parent(uqadto.getU_num());
		
		int res = qnaBoardMapper.insertUserQnA(uqadto);
		
		if(res>0) {
			req.setAttribute("msg", "게시판에 글이 등록 되었습니다.");
			req.setAttribute("url", "list_userQnA");
		}else {
			req.setAttribute("msg", "등록 오류 발생. 관리자에게 문의해 주세요.");
			req.setAttribute("url", "write_userQnA");
		}
		
		return "message";
	}
	
	@RequestMapping("/delete_userQnA_ok")
	public String deleteUserQnAOk(HttpServletRequest req, int uqa_num) {
		int res = qnaBoardMapper.deleteUserQnA(uqa_num);
		
		if(res>0) {
			req.setAttribute("msg", "글을 삭제하였습니다. 글 목록 페이지로 이동합니다.");
			req.setAttribute("url", "list_userQnA");
		}else {
			req.setAttribute("msg", "삭제 오류 발생. 관리자에게 문의해 주세요.");
			req.setAttribute("url", "get_userQnA?uqa_num="+uqa_num);
		}			
		
		return "message";
	}
	
	@RequestMapping("/update_userQnA")
	public String updateUserQnA(HttpServletRequest req, int uqa_num) {
		UserQnADTO uqadto = qnaBoardMapper.getUserQnA(uqa_num);
		uqadto.setU_nickname(qnaBoardMapper.getUnicknameByUnum(uqadto.getU_num()));
		req.setAttribute("uqadto", uqadto);
		
		return "board/user_qna_updateform";
	}
	
	@RequestMapping("/update_userQnA_ok")
	public String updateProBoard(HttpServletRequest req, UserQnADTO uqadto) {
		int res = qnaBoardMapper.updateUserQnA(uqadto);
		
		if(res>0) {
			req.setAttribute("msg", "글이 수정되었습니다. 글 목록 페이지로 이동합니다.");
			req.setAttribute("url", "list_userQnA");
		}else {
			req.setAttribute("msg", "수정 오류 발생. 관리자에게 문의해 주세요.");
			req.setAttribute("url", "get_userQnA?uqa_num="+uqadto.getUqa_num());
		}
		
		return "message";
	}
	
	//리뷰쓰는 화면으로
	@RequestMapping("/user_reviewform")
	public String userReviewform(HttpServletRequest req, @RequestParam int h_num, int room_num, int book_num) {
		HttpSession session = req.getSession();
		LoginOkBeanUser loginOkBean = (LoginOkBeanUser)session.getAttribute("loginOkBean");

		req.setAttribute("book_num", book_num);
		req.setAttribute("room_type", hotelMapper.getRoomType(room_num));
		req.setAttribute("u_num", loginOkBean.getU_num());
		req.setAttribute("h_num", h_num);
		req.setAttribute("review_nickname", loginOkBean.getU_nickname());
		
		return "user/user_reviewform";
	}
	
	//리뷰 실행될 때
	@RequestMapping("/user_reviewOk")
	public String user_reviewOk(HttpServletRequest req) throws IllegalStateException, IOException {
		
		MultipartHttpServletRequest mr = (MultipartHttpServletRequest)req;
		MultipartFile mf = mr.getFile("review_image");
		
//		같은 날에 해당 유저가 쓴 리뷰가 있는지 확인하고 있으면 바로 리스트로 보냄
		SimpleDateFormat format = new SimpleDateFormat ("yyyy.MM.dd");
		Date time = new Date();
		String today = format.format(time);
		
		/*
		List<ReviewDTO> reviewList = userMyPageMapper.listReview(Integer.parseInt(mr.getParameter("u_num")), Integer.parseInt(mr.getParameter("h_num")));
		for(ReviewDTO rdto : reviewList) {
			if(rdto.getReview_joindate().contains(today)) {
				req.setAttribute("msg", "해당 호텔에 대한 리뷰를 이미 작성하셨습니다.");
				req.setAttribute("url", "user_reviewList");
				return "message";
			}
		}
		*/	
		
		String review_image = mf.getOriginalFilename();
		
		if(review_image != null && !review_image.trim().equals("")) {
			review_image = UUID.randomUUID().toString()+"_"+review_image;
		}
		
		int res = userMyPageMapper.insertReview(mr, review_image);
		
		if(res>0) {
			if(!review_image.equals("")) {
				File file = new File(upPath+"\\review", review_image);
				mf.transferTo(file);
			}
			
			req.setAttribute("msg", "리뷰 등록 성공!! 리뷰 리스트 페이지로 이동합니다.");
            req.setAttribute("url", "user_reviewList");
		}
		
		return "message";
	}
	
	//	리뷰수정페이지
	@RequestMapping("/editReview")
	public String editReview(HttpServletRequest req, @RequestParam int review_num) {
		ReviewDTO rdto = userMyPageMapper.getReview(review_num);
		req.setAttribute("rdto", rdto);
		return "user/user_reviewEdit";
	}
	
//		리뷰수정확인페이지
	@RequestMapping("/user_reviewEditOk")
	public String editReviewOk(HttpServletRequest req, @RequestParam String pastImage) throws Exception {
		MultipartHttpServletRequest mr = (MultipartHttpServletRequest)req;
		MultipartFile mf = mr.getFile("newImage");
		
		String newImage = mf.getOriginalFilename();
		
		if(newImage != null && !newImage.trim().equals("")) {
			newImage = UUID.randomUUID().toString()+"_"+newImage;
		}else {
			newImage = pastImage;
		}
		
		int res = userMyPageMapper.editReview(mr, newImage);
		
		if(res > 0) {
			if(!newImage.equals(pastImage)) {
				File file = new File(upPath+"\\review", newImage);
				mf.transferTo(file);
				
				File pastFile = new File(upPath+"\\review", pastImage);
				if(pastFile.exists()) {
					pastFile.delete();
				}
			}
			
			req.setAttribute("msg", "리뷰 수정 성공!! 리뷰 리스트 페이지로 이동합니다.");
            req.setAttribute("url", "user_reviewList");
		}
		
		return "message";
	}

	//리뷰 삭제 버튼 눌렀을 때
	@RequestMapping("/deleteReview")
	   public String deleteReview(HttpServletRequest req, @RequestParam int review_num) {
	      
	      String msg = null;
	      String image = userMyPageMapper.getReview_image(review_num);
	      int res = userMyPageMapper.deleteReview(review_num);
	      
	      if(res > 0) {
	         try {
	        	 File file = new File(upPath+"\\review", image);
	        	 
	        	 if(file.exists()) {
	        		 file.delete();
	        		 msg = "리뷰삭제(이미지삭제)";
	        	 }else {
	        		 msg = "리뷰삭제(이미지남음)";
	        	 }
	    	  }catch(Exception e) {
	    		  msg = "리뷰삭제";
	    	  }
	      }else {
	    	  msg = "리뷰삭제 실패";
	      }
	      
	      req.setAttribute("msg", msg);
	      req.setAttribute("url", "user_reviewList");
	      return "message";
	   }

	//내가쓴 리뷰 페이지로 이동
	@RequestMapping("/user_reviewList")
	public ModelAndView user_reviewList(HttpServletResponse resp, HttpServletRequest req, @RequestParam(required = false) String pageNum) {
		HttpSession session = req.getSession();
		LoginOkBeanUser loginOkBean = (LoginOkBeanUser)session.getAttribute("loginOkBean");
		int u_num = loginOkBean.getU_num();
		
		ModelAndView mav = new ModelAndView();
		if(pageNum == null) {
			pageNum = "1";
		}
		int pageSize = 5;
		int currentPage = Integer.parseInt(pageNum);
		int startRow = (currentPage-1) * pageSize + 1;
		int endRow = startRow + pageSize - 1;
		int number = 0;
		int rowCount = 0;
		
		rowCount = userMyPageMapper.getReviewCount(u_num);
		
		if (endRow>rowCount) endRow = rowCount;
		number = rowCount - startRow + 1;
		
		List<ReviewDTO> listReview = userMyPageMapper.listReviewByUser(startRow, endRow, u_num);
		
		mav.addObject("listReview", listReview);
		mav.addObject("number", number);
		mav.addObject("rowCount", rowCount);
		
		if (rowCount>0){
//				[1] [2] [3]
			int pageBlock = 5;
//				31(게시글수) / 5  =  몫 : 6, 나머지 = 1
			int pageCount = rowCount / pageSize;
//				나머지가 0이 아니면, 나머지 게시글 보여주기 위해 몫++ 해줌
			if (rowCount%pageSize != 0){
				pageCount++;
			}
//										(1-1)	/	3		*	3		+ 1   = 1
//										(2-1)	/	3		*	3		+ 1   = 1
//										(4-1)	/	3		*	3		+ 1	  = 4
			int startPage = ((currentPage-1)/pageBlock) * pageBlock + 1;
//									1	+	3	-	1	= 3..
//									4	+	3	-	1	= 6..
//									7	+	3	-	1	= 9
			int endPage = startPage + pageBlock - 1;
//						3	>	7	거짓
//						9	>	7	참		endPage = 7(마지막페이지 넘버는 7이 됨)
			if (endPage > pageCount) endPage = pageCount;
			
			mav.addObject("startPage", startPage);
			mav.addObject("endPage", endPage);
			mav.addObject("pageBlock", pageBlock);
			mav.addObject("pageCount", pageCount);
		}
		
		mav.setViewName("user/user_reviewList");
		return mav;
	}

	//엑티비티리뷰쓰는 화면으로
		@RequestMapping("/user_reviewActform")
		public String userActReviewform(HttpServletRequest req, @RequestParam int a_num, int p_num, int ba_num) {
			HttpSession session = req.getSession();
			LoginOkBeanUser loginOkBean = (LoginOkBeanUser)session.getAttribute("loginOkBean");
			int u_num = loginOkBean.getU_num();
			
			String review_nickname = loginOkBean.getU_nickname();
			String program_type = userMyPageMapper.getProgramType(p_num);
			
			req.setAttribute("ba_num", ba_num);
			req.setAttribute("program_type", program_type);
			req.setAttribute("u_num", u_num);
			req.setAttribute("a_num", a_num);
			req.setAttribute("review_nickname", review_nickname);
			
			return "user/user_reviewActForm";
		}
		
		//엑티비티 리뷰 입력 될 때
		@RequestMapping("/user_reviewActOk")
		public String user_reviewActOk(HttpServletRequest req) throws IllegalStateException, IOException {
			 
			MultipartHttpServletRequest mr = (MultipartHttpServletRequest)req;
			MultipartFile mf = mr.getFile("review_image");
			
			String review_image = mf.getOriginalFilename();
			
			if(review_image != null && !review_image.trim().equals("")) {
				review_image = UUID.randomUUID().toString()+"_"+review_image;
			}
			
			int res = userMyPageMapper.insertActReview(mr, review_image);
			
			if(res > 0) {
				if(!review_image.equals("")) {
					File file = new File(upPath+"\\review", review_image);
					mf.transferTo(file);
				}
				
				req.setAttribute("msg", "리뷰 등록 완료");
	            req.setAttribute("url", "user_reviewListAct");
			}
			
			return "message";
		}
		
		//내가쓴 리뷰 페이지로 이동
		@RequestMapping("/user_reviewListAct")
		public ModelAndView user_reviewListAct(HttpServletRequest req, @RequestParam(required = false) String pageNum) {
			HttpSession session = req.getSession();
			LoginOkBeanUser loginokbean = (LoginOkBeanUser)session.getAttribute("loginOkBean");
			int u_num = loginokbean.getU_num();
			
			ModelAndView mav = new ModelAndView();
			if(pageNum == null) {
				pageNum = "1";
			}
			int pageSize = 5;
			int currentPage = Integer.parseInt(pageNum);
			int startRow = (currentPage-1) * pageSize + 1;
			int endRow = startRow + pageSize - 1;
			int number = 0;
			int rowCount = 0;
			
			rowCount = userMyPageMapper.getReviewActCount(u_num);
			
			if (endRow>rowCount) endRow = rowCount;
			number = rowCount - startRow + 1;
			
			List<ReviewDTO> listReview = userMyPageMapper.listReviewActByUser(startRow, endRow, u_num);
			
			mav.addObject("listReview", listReview);
			mav.addObject("number", number);
			mav.addObject("rowCount", rowCount);
			
			if (rowCount>0){
//					[1] [2] [3]
				int pageBlock = 5;
//					31(게시글수) / 5  =  몫 : 6, 나머지 = 1
				int pageCount = rowCount / pageSize;
//					나머지가 0이 아니면, 나머지 게시글 보여주기 위해 몫++ 해줌
				if (rowCount%pageSize != 0){
					pageCount++;
				}
//											(1-1)	/	3		*	3		+ 1   = 1
//											(2-1)	/	3		*	3		+ 1   = 1
//											(4-1)	/	3		*	3		+ 1	  = 4
				int startPage = ((currentPage-1)/pageBlock) * pageBlock + 1;
//										1	+	3	-	1	= 3..
//										4	+	3	-	1	= 6..
//										7	+	3	-	1	= 9
				int endPage = startPage + pageBlock - 1;
//							3	>	7	거짓
//							9	>	7	참		endPage = 7(마지막페이지 넘버는 7이 됨)
				if (endPage > pageCount) endPage = pageCount;
				
				mav.addObject("startPage", startPage);
				mav.addObject("endPage", endPage);
				mav.addObject("pageBlock", pageBlock);
				mav.addObject("pageCount", pageCount);
			}
			
			mav.addObject("upPath", upPath);
			mav.setViewName("user/user_reviewActList");
			return mav;
		}
		
		//리뷰 삭제 버튼 눌렀을 때
		@RequestMapping("/deleteActReview")
			public String reviewActdelete(HttpServletRequest req, @RequestParam int review_num) {
				
				String msg = null;
				String image = userMyPageMapper.getReview_imageAct(review_num);
				int res = userMyPageMapper.deleteActReview(review_num);
				
				if(res > 0) {
					try {
						File file = new File(upPath+"\\review", image);
						
						if(file.exists()) {
							file.delete();
							msg = "리뷰삭제(이미지삭제)";
						}else {
							msg = "리뷰삭제(이미지남음)";
						}
					}catch(Exception e) {
						msg = "리뷰삭제";
					}
				}else {
					msg = "리뷰삭제 실패";
				}
				
				req.setAttribute("msg", msg);
				req.setAttribute("url", "user_reviewListAct");
				return "message";
			}
		
//		리뷰수정페이지
		@RequestMapping("/editActReview")
		public String editActReview(HttpServletRequest req, @RequestParam int review_num) {
			ReviewActDTO rdto = userMyPageMapper.getActReview(review_num);
			req.setAttribute("rdto", rdto);
			return "user/user_reviewActEdit";
		}
		
//		리뷰수정확인페이지
		@RequestMapping("/user_reviewActEditOk")
		public String editActReviewOk(HttpServletRequest req, @RequestParam String pastImage) throws Exception {
			MultipartHttpServletRequest mr = (MultipartHttpServletRequest)req;
			MultipartFile mf = mr.getFile("newImage");
			
			String newImage = mf.getOriginalFilename();
			
			if(newImage != null && !newImage.trim().equals("")) {
				newImage = UUID.randomUUID().toString()+"_"+newImage;
			}else {
				newImage = pastImage;
			}
			
			int res = userMyPageMapper.editActReview(mr, newImage);
			
			if(res > 0) {
				if(!newImage.equals(pastImage)) {
					File file = new File(upPath+"\\review", newImage);
					mf.transferTo(file);
					
					File pastFile = new File(upPath+"\\review", pastImage);
					if(pastFile.exists()) {
						pastFile.delete();
					}
				}
				
				req.setAttribute("msg", "리뷰 수정 성공!! 리뷰 리스트 페이지로 이동합니다.");
				req.setAttribute("url", "user_reviewListAct");
			}
			
			return "message";
		}
}
