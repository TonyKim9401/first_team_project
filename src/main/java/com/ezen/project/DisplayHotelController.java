package com.ezen.project;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.ezen.project.model.HotelDTO;
import com.ezen.project.model.ReviewDTO;
import com.ezen.project.model.RoomDTO;
import com.ezen.project.model.WishListDTO;
import com.ezen.project.service.DisplayHotelMapper;
import com.ezen.project.service.HotelMapper;
import com.ezen.project.service.UserMyPageMapper;

@Controller
public class DisplayHotelController {
	
	@Autowired
	private DisplayHotelMapper displayHotelMapper;
	
	@Autowired
	private HotelMapper hotelMapper;
	
	@Autowired
	private UserMyPageMapper userMyPageMapper;
	
	// ȣ�� �˻� ��� �������� �����ֱ� ���� ����
	@RequestMapping("/display_hotelSearchOk")
	public String hotelSearchOk(HttpServletRequest req, @RequestParam Map<String,String> params) {
		// params�� ��� �͵� : condition(���� �Է��� �˻�� ����Ű����), indate, outdate, inwon
		// filter�� ���ʹ������� ���
		
		// �˻��� ȣ���� ���� List �����ؼ� �˻����ǿ� �´� ��� DTO�� ������
		List<HotelDTO> hotelList = displayHotelMapper.listHotelByLocation(params.get("condition"));
		
		// ������ ȣ��DTO�鿡 �� ȣ�ڸ����� ���� ������ MAP���·� �����ϴ� �۾�
		Map<Integer, Integer> reviewMaxCountMap = displayHotelMapper.countReview(hotelList);
		
		// ������ ȣ��DTO�鿡 �� ȣ�ڸ����� ���� ��������� MAP���·� �����ϴ� �۾� 
		Map<Integer, Double> reviewStarAvgMap = displayHotelMapper.averageReview(hotelList);
		
		// ���͸� �������� ���ǹ��� Ÿ�Ե�
		if(params.get("filter") != null) {
			
			// ���䰳�� ���� ������ �����صα�
			List<HotelDTO> tmpHotelList = new ArrayList<HotelDTO>();
			List<Map.Entry<Integer, Integer>> orderedByReviewCount = new LinkedList<>(reviewMaxCountMap.entrySet());
			orderedByReviewCount.sort(Map.Entry.comparingByValue());
			
			for(Map.Entry<Integer, Integer> entry : orderedByReviewCount) {
				int order = entry.getKey();
				for(HotelDTO hdto : hotelList) {
					if(order == hdto.getH_num()) {
						tmpHotelList.add(hdto);
						break;
					}
				}
			}
			
			// ���亰�� ���� ������ �����صα�
			List<HotelDTO> tmpHotelList2 = new ArrayList<HotelDTO>();
			List<Map.Entry<Integer, Double>> orderedByStarCount = new LinkedList<>(reviewStarAvgMap.entrySet());
			orderedByStarCount.sort(Map.Entry.comparingByValue());
			
			for(Map.Entry<Integer, Double> entry : orderedByStarCount) {
				int order = entry.getKey();
				for(HotelDTO hdto : hotelList) {
					if(order == hdto.getH_num()) {
						tmpHotelList2.add(hdto);
						break;
					}
				}
			}
			
			// ���� ���Ϳ� ���� ����Ʈ ������
			switch(params.get("filter")) {
				case "maxPrice":
					// DB���� ���� ���� ������ �ٽ� �̾ƿ�
					hotelList = displayHotelMapper.listHotelByMaxPrice(params.get("condition"));
					break; 
				case "minPrice":
					// DB���� ���� ���� ������ �ٽ� �̾ƿ�
					hotelList = displayHotelMapper.listHotelByMinPrice(params.get("condition"));
					break;
				case "maxReview": 
					// DB���� ���� ���� ������ �ٽ� �̾ƿ� (�Ʒ����� ���� �ϳ��� ��ߵ�)
					// hotelList = displayHotelMapper.listHotelByMaxReviewCount(params.get("condition"));
					
					// ��� ���� ���� ������ ���ĵ� tmpHotelList�� �������� hotelList�� ����
					Collections.reverse(tmpHotelList);
					hotelList = tmpHotelList;
					break;
				case "minReview": 
					// DB���� ���� ���� ������ �ٽ� �̾ƿ� (�Ʒ����� ���� �ϳ��� ��ߵ�)
					// hotelList = displayHotelMapper.listHotelByMinReviewCount(params.get("condition"));
					
					// ��� ���� ���� ������ ���ĵ� tmpHotelList�� hotelList�� ����
					hotelList = tmpHotelList;
					break;
				case "maxStar": 
					// ��� ���� ���� ������ ���ĵ� tmpHotelList2�� �������� hotelList�� ����
					Collections.reverse(tmpHotelList2);
					hotelList = tmpHotelList2;
					break;
				case "minStar":
					// ��� ���� ���� ������ ���ĵ� tmpHotelList2�� hotelList�� ����
					hotelList = tmpHotelList2;
					break;
				default:
					break;
			}
		}
		
		HttpSession session = req.getSession();
		
		// �α����� �� ���, �ش� ������ ȣ���� ���ø���Ʈ�� ����ߴ��� �ƴ��� Ȯ���ؼ� üũ (��� 1, �̵�� 0)
		LoginOkBeanUser loginOkBean = (LoginOkBeanUser)session.getAttribute("loginOkBean");
		
		if(loginOkBean != null) {
			int u_num = loginOkBean.getU_num();
			
			for(HotelDTO hdto : hotelList) {
				hdto.setWishList(displayHotelMapper.isWishCheck(hdto.getH_num(), u_num));
			}
		}

		// ����API���� ���� ���� �ּ� �迭 �����
		String[] addrsForMap = new String[hotelList.size()];
		
		for(int i=0; i<hotelList.size(); i++) {
			HotelDTO hdto = hotelList.get(i);
			String addr = hdto.getH_address();
			String[] fullAddress = addr.split("\\(");
			addrsForMap[i] = fullAddress[0];
		}
		
		if(params.get("indate") != null && params.get("outdate") != null ) {
			// üũ��, üũ�ƿ� ��¥��  ������ ���
			session.setAttribute("indate", params.get("indate"));
			session.setAttribute("outdate", params.get("outdate"));
		}else {
			// �������� ������ ����, ���� ��¥�� ����
			if((String)session.getAttribute("indate") == null && (String)session.getAttribute("outdate") == null) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				Date time = new Date();
				String today = sdf.format(time);

		        Calendar cal = new GregorianCalendar();
		        cal.add(Calendar.DATE, 1);
		        Date date = cal.getTime();
		        String tmr = sdf.format(date);
						
				session.setAttribute("indate", today);
				session.setAttribute("outdate", tmr);
			}
		}
		
		session.setAttribute("inwon", params.get("inwon"));
		session.setAttribute("hotelList", hotelList);
		session.setAttribute("addrsForMap", addrsForMap);	// Ÿ���� Map�� �ƴ϶� īī���ʿ� ���� String�迭��
		session.setAttribute("reviewMaxCountMap", reviewMaxCountMap);
		session.setAttribute("reviewStarAvgMap", reviewStarAvgMap);
		session.setAttribute("condition", params.get("condition"));
		
		return "display/display_hotelSearchOk";
	}
	
//	h_num���� ȣ�� ������ ã��
	@RequestMapping("/display_hotelContent")
	public String hotelContent(HttpServletRequest req, int h_num) {
		HttpSession session = req.getSession();

		// ȣ�� ���� ���� ��������
		int reviewCount = displayHotelMapper.getReviewCountByHotel(h_num);
		
		// ȣ�� ���� ��� ��������
		double starAverage = displayHotelMapper.getReviewStarAverage(h_num);
		starAverage = Math.round(starAverage*10)/10.0; // �Ҽ� 1��° �ڸ����� ǥ��
		
		// ���� Ÿ�Ժ��� List �̾ƿ���
		List<RoomDTO> twinList = displayHotelMapper.listRoomByType(h_num, "TWIN");
		List<RoomDTO> doubleList = displayHotelMapper.listRoomByType(h_num, "DOUBLE");
		List<RoomDTO> deluxeList = displayHotelMapper.listRoomByType(h_num, "DELUXE");
		
		LoginOkBeanUser loginOkBean = (LoginOkBeanUser)session.getAttribute("loginOkBean");
		HotelDTO hdto = hotelMapper.getHotel(h_num);
		
		// �α����� ��츸 ���ø���Ʈ üũ
		if(loginOkBean != null) {
			hdto.setWishList(displayHotelMapper.isWishCheck(h_num, loginOkBean.getU_num()));
		}
		
		// ȣ�ڿ� ���� ���� ����Ʈ
		List<ReviewDTO> reviewList = displayHotelMapper.listReviewByHotel(h_num);
	      
		String addrForMap = "";
		String h_address = hdto.getH_address();

		for(int i=0; i<h_address.length(); i++) {
			String letter = String.valueOf(h_address.charAt(i));
			
			if(!letter.equals("@")) {
				addrForMap += letter;
			} else {
				break;
			}
		}
		
		session.setAttribute("hdto", hdto);
		session.setAttribute("map_addr", addrForMap);
		session.setAttribute("reviewCount", reviewCount);
		session.setAttribute("starAverage", starAverage);
		session.setAttribute("twinRoom", twinList);
		session.setAttribute("doubleRoom", doubleList);
		session.setAttribute("deluxeRoom", deluxeList);
		session.setAttribute("reviewList", reviewList);
		
		return "display/display_hotelContent";
	}
	
	
//	h_num�� room_num�� ��ġ�ϴ� ��� ã��
	@RequestMapping("/display_roomContent")
	public String roomContent(HttpServletRequest req, @RequestParam(required=false) String room_code, 
			int h_num) {
//		ȣ������
		HotelDTO hdto = hotelMapper.getHotel(h_num);
		List<RoomDTO> roomList = hotelMapper.listRoomInGroupByRoomCode(room_code);
		RoomDTO room = roomList.get(0);
		
		HttpSession session = req.getSession();
		
		Map<String, String> map = new Hashtable<>();
		map.put("book_indate", (String)session.getAttribute("indate"));
		map.put("book_outdate", (String)session.getAttribute("outdate"));
		
		for(RoomDTO rdto : roomList) {
			map.put("room_num", String.valueOf(rdto.getRoom_num()));
			rdto.setRoom_booked(displayHotelMapper.isBookedRoom(map));
		}
		
		List<RoomDTO> roomList2 = new ArrayList<RoomDTO>();
		for(RoomDTO rdto : roomList) {
			if(rdto.getRoom_booked().equals("n")) {
				roomList2.add(rdto);
			}
		}
		
		roomList = roomList2;
		
		req.setAttribute("hdto", hdto);
		req.setAttribute("Room", room);
		req.setAttribute("roomList", roomList);
		
		
		Map<String, String> map2 = new Hashtable<String, String>();
		
		map2.put("book_indate", (String)session.getAttribute("indate"));
		map2.put("book_outdate", (String)session.getAttribute("outdate"));
		map2.put("room_code", room.getRoom_code());
		
		int max_roomCount = hotelMapper.countRoomOnGroup(room.getRoom_code());
		int booked_roomCount = displayHotelMapper.countBookedRoom(map2);
		int bookable_roomCount = max_roomCount - booked_roomCount;
		
//		ȣ�ڱ⺻����
//		@�����ڷ� ���׵��� ������ �迭�� ����� -> jsp���� �迭 �ϳ��� ���+�ٰ���
		String[] hotelInfo = hdto.getH_info().split("@");
		String[] hotelNotice = hdto.getH_notice().split("@");
		req.setAttribute("hotelInfo", hotelInfo);
		req.setAttribute("hotelNotice", hotelNotice);
		req.setAttribute("bookable_roomCount", bookable_roomCount);
		
		return "display/display_roomContent";
	}
	
	@RequestMapping("/review")
	public String review(HttpServletRequest req, @RequestParam int h_num) {
		
//		ȣ�ڿ� ���� ���� ����Ʈ
		List<ReviewDTO> reviewList = displayHotelMapper.listReviewByHotel(h_num);
		
//		ȣ�� ���� ����
		int reviewCount = displayHotelMapper.getReviewCountByHotel(h_num);
		
//		ȣ�� ���� ���
		double starAverage = displayHotelMapper.getReviewStarAverage(h_num);
		starAverage = Math.round(starAverage*10)/10.0;//�Ҽ� 1��° �ڸ����� ǥ��
		
		req.setAttribute("reviewCount", reviewCount);
		req.setAttribute("reviewList", reviewList);
		req.setAttribute("starAverage", starAverage);
		return "board/review";
	}
	
	//����Ʈ�������� �̵��� ��
	@RequestMapping("/user_pointList")
	public ModelAndView userpointList(HttpServletRequest req, @RequestParam(required = false) String pageNum) {
		
		HttpSession session = req.getSession();
		LoginOkBeanUser loginokbean = (LoginOkBeanUser)session.getAttribute("loginOkBean");
		int u_num = loginokbean.getU_num();
		
		ModelAndView mav = new ModelAndView();
		if(pageNum == null) {
			pageNum = "1";
		}
		int pageSize = 10;
		int currentPage = Integer.parseInt(pageNum);
		int startRow = (currentPage-1) * pageSize + 1;
		int endRow = startRow + pageSize - 1;
		int number = 0;
		int rowCount = 0;
		
		rowCount = userMyPageMapper.getPointCount(u_num);
		
		if (endRow>rowCount) endRow = rowCount;
		number = rowCount - startRow + 1;
		
		List<ReviewDTO> pointList = userMyPageMapper.listPoint(startRow, endRow, u_num);
		
		mav.addObject("pointList", pointList);
		mav.addObject("number", number);
		mav.addObject("rowCount", rowCount);
		
		if (rowCount>0) {
//				[1] [2] [3]
			int pageBlock = 2;
//				31(�Խñۼ�) / 5  =  �� : 6, ������ = 1
			int pageCount = rowCount / pageSize;
//				�������� 0�� �ƴϸ�, ������ �Խñ� �����ֱ� ���� ��++ ����
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
//						3	>	7	����
//						9	>	7	��		endPage = 7(������������ �ѹ��� 7�� ��)
			if (endPage > pageCount) endPage = pageCount;
			
			mav.addObject("startPage", startPage);
			mav.addObject("endPage", endPage);
			mav.addObject("pageBlock", pageBlock);
			mav.addObject("pageCount", pageCount);
		}
		
		mav.setViewName("user/user_pointList");
		
		return mav;
	}

	//���ø���Ʈ�� �̵�
	@RequestMapping("/user_wishlist")
	public ModelAndView userWishlist(HttpServletRequest req) {
		HttpSession session = req.getSession();
		LoginOkBeanUser loginokbean = (LoginOkBeanUser)session.getAttribute("loginOkBean");
		int u_num = loginokbean.getU_num();
		List<WishListDTO> wdto = userMyPageMapper.listWishList(u_num);
		List<WishListDTO> wdtoAct = userMyPageMapper.listWishListAct(u_num);
		req.setAttribute("wishListAct", wdtoAct);
		req.setAttribute("wishList", wdto);
		return new ModelAndView("user/user_wishlist", "wishList", wdto);
	}
	
//	wishList ���������� ȣ�� ���ø���Ʈ ����
	@RequestMapping(value="/wishReleaseWL")
	public String wishReleaseWL(HttpServletRequest req, @RequestParam int w_num) {
		displayHotelMapper.wishReleaseWL(w_num);
		return "user/user_wishlist";
	}
	
//	hotelSeachOk ���������� ���ø���Ʈ üũ/����
	@RequestMapping(value="/wishReleaseOK")
	public String wishReleaseOK(HttpServletRequest req, @RequestParam Map<String, String> params) {
		displayHotelMapper.wishRelease(params);
		return "display/display_hotelSearchOk";
	}
	
	@RequestMapping(value="/wishCheckOK")
	public String wishCheckOK(HttpServletRequest req,@RequestParam Map<String, String> params) {
		displayHotelMapper.wishCheck(params);
		return "display/display_hotelSearchOk";
	}
	
	// hotelContent ���������� ���ø���Ʈ üũ/����
	@RequestMapping(value="/wishReleaseHC")
	public String wishReleaseHC(HttpServletRequest req, @RequestParam Map<String, String> params) {
		displayHotelMapper.wishRelease(params);
		req.setAttribute("h_num", params.get("h_num"));
		return "display/display_hotelContent";
	}
	@RequestMapping(value="/wishCheckHC")
	public String wishCheckHC(HttpServletRequest req,@RequestParam Map<String, String> params) {
		displayHotelMapper.wishCheck(params);
		req.setAttribute("h_num", params.get("h_num"));
		return "display/display_hotelContent";
	}
	
}
