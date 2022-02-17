package com.ezen.project.service;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ezen.project.model.BookingDTO;
import com.ezen.project.model.HotelDTO;
import com.ezen.project.model.ReviewDTO;
import com.ezen.project.model.RoomDTO;

@Service
public class DisplayHotelMapper {

	@Autowired
	private SqlSession sqlSession;
	
	public void checkToday(String today) {
		sqlSession.update("checkToday", today);
	}
	
	//	자동완성
	public List<String> allOptions(){
		List<String> totalList = new ArrayList<String>();
		List<String> allOptions = sqlSession.selectList("getHotelNames");
		List<String> hotelAddresses = sqlSession.selectList("getHotelAddresses");
		
		int forSize = allOptions.size();
		
		if(allOptions.size() > 3) {
			forSize = 3;
		}
		
		for(int i=0; i<forSize; i++) {
			String allOption = allOptions.get(i);
			totalList.add(allOption);
		}
		for(int i=0; i<forSize; i++) {
			String[] address = hotelAddresses.get(i).split("@");
			totalList.add(address[0]);
		}
		return totalList;
	}
	
	// 최초 디폴트 리스트
	public List<HotelDTO> listHotelByLocation(String condition){
		return sqlSession.selectList("listHotelByLocation", "%"+condition+"%");
	}
	
	// 높은 가격순 정렬해서 가져오기
	public List<HotelDTO> listHotelByMaxPrice(String condition){
		Map<String,String> sql = new Hashtable<String, String>();
		
		String str = "SELECT h.* "
					+ "FROM project_hotel h INNER JOIN ("
					+ "SELECT h_num, MAX(to_number(room_price)) as topprice "
					+ "FROM project_room "
					+ "GROUP BY h_num"
					+ ")r "
					+ "ON h.h_num=r.h_num "
					+ "WHERE h_name like '%"+condition+"%' or h_address like '%"+condition+"%' "
					+ "ORDER BY r.topprice desc";
		
		sql.put("sql", str);
		
		return sqlSession.selectList("listHotelByFilter", sql);
	}
	
	// 낮은 가격순 정렬해서 가져오기
	public List<HotelDTO> listHotelByMinPrice(String condition) {
		Map<String,String> sql = new Hashtable<String, String>();
		String str = "SELECT h.* "
					+ "FROM project_hotel h INNER JOIN ("
					+ "SELECT h_num, MIN(to_number(room_price)) as lowprice "
					+ "FROM project_room "
					+ "GROUP BY h_num"
					+ ")r "
					+ "ON h.h_num=r.h_num "
					+ "WHERE h_name like '%"+condition+"%' or h_address like '%"+condition+"%' "
					+ "ORDER BY r.lowprice asc";
		
		sql.put("sql", str);
		
		return sqlSession.selectList("listHotelByFilter", sql);
	}
	
	// 위시리스트 체크여부 확인
	public int isWishCheck(int h_num, int u_num) {
		Map<String, Integer> map = new Hashtable<String, Integer>();
		map.put("h_num", h_num);
		map.put("u_num", u_num);
		return sqlSession.selectOne("isWishCheck", map);
	}
	
	// 호텔 하나의 후기 개수 반환
	public int getReviewCountByHotel(int h_num) {
		return sqlSession.selectOne("getReviewCountByHotel", h_num);
	}
	
	// 호텔 하나의 별점 평균 반환
	public double getReviewStarAverage(int h_num) {
		List<Integer> star = sqlSession.selectList("getReviewStarAverage", h_num);

		int totalStar = 0;
		for(int i=0; i<star.size(); i++) {
			totalStar += star.get(i);
		}
		double averageStar = (double)totalStar/star.size();
		return averageStar;
	}
	
	//	객실 타입을 기준으로 해당 호텔의 객실 그룹 리스트를 가져옴
	//TWIN, DOUBLE, DELUXE
	public List<RoomDTO> listRoomByType(int h_num, String type) {
		Map<String, String> map = new Hashtable<String, String>();
		map.put("h_num",String.valueOf(h_num));
		map.put("type", type);
		
		return sqlSession.selectList("listRoomByType", map);
	}
	
//	해당 호텔의 후기를 리스트로 반환
	public List<ReviewDTO> listReviewByHotel(int h_num) {
		return sqlSession.selectList("listReviewByHotel", h_num);
	}
	
//	위시리스트 해제
	public void wishRelease(Map<String, String> params) {
		sqlSession.delete("wishRelease", params);
	}

//	위시리스트 저장
	public void wishCheck(Map<String, String> params) {
		sqlSession.insert("wishCheck", params);
	}
	
//	위시리스트메뉴에서 찜하기 해제
	public void wishReleaseWL(int w_num) {
		sqlSession.delete("wishReleaseWL", w_num);
	}
	
//	예약저장&유저 포인트 수정
	public int insertBook(Map<String,String> params) {
		return sqlSession.insert("insertBook", params);
	}
	
//	유저의 예약정보 반환
	public BookingDTO getBook(int book_num) {
		BookingDTO bdto = sqlSession.selectOne("getBooking", book_num);
		return bdto;
	}
	
//	예약취소
	public int deleteBook(int book_num, int u_num) {
		int usePoint = sqlSession.selectOne("usePoint", book_num);
		int savePoint = sqlSession.selectOne("savePoint", book_num);
		int update = usePoint - savePoint;
		Map<String, Integer> map = new Hashtable<String, Integer>();
		map.put("update", update);
		map.put("u_num", u_num);
		sqlSession.update("updatePoint", map);
		
		//취소 포인트 파라미터값 만들어주기
		Map<String, String> params = new Hashtable<String, String>();
		BookingDTO bdto = sqlSession.selectOne("getBooking", book_num);
		String p_status = "취소";
		String h_name = sqlSession.selectOne("getHotelName",bdto.getH_num());
		String p_contents = h_name + " 예약 취소";
		int p_remaining = sqlSession.selectOne("getUserPoint", u_num);
		params.put("book_num", String.valueOf(book_num));
		params.put("u_num", String.valueOf(u_num));
		params.put("p_status", p_status);
		params.put("p_contents", p_contents);
		params.put("p_point", String.valueOf(update));
		params.put("p_remaining", String.valueOf(p_remaining));
		sqlSession.insert("cancelPoint", params);
		
		return sqlSession.update("deleteBook", book_num);
	}
	
//	객실가격반환
	public int getRoomPrice(int book_num) {
		int room_num = sqlSession.selectOne("getRoomNum",book_num);
		return sqlSession.selectOne("getRoomPrice", room_num);
	}
	
//	포인트db적립 기록
	public void savePoint(Map<String,String> params) {
		
		sqlSession.update("savePointUpdate", params);
		
		Map<String, String> map = new Hashtable<String, String>();
		String h_name = sqlSession.selectOne("getHotelName",params);
		int book_num = sqlSession.selectOne("getBookNum", params);
		int u_point = sqlSession.selectOne("getUserPoint", params.get("u_num"));
		
		map.put("u_num", params.get("u_num"));
		map.put("p_status","적립");
		map.put("p_contents",h_name+" 예약 확정");
		map.put("p_point", params.get("book_savepoint"));
		map.put("book_num", String.valueOf(book_num));
		map.put("p_remaining",String.valueOf(u_point));
		sqlSession.insert("savePointDB", map);
		sqlSession.update("updatePointEnd",u_point);
	}
	
//	예약시 사용한 포인트db기록
	public void usedPoint(Map<String,String> params) {
		
		sqlSession.update("usedPointUpdate", params);
		
		Map<String, String> map = new Hashtable<String, String>();
		String h_name = sqlSession.selectOne("getHotelName",params);
		int book_num = sqlSession.selectOne("getBookNum", params);
		int u_point = sqlSession.selectOne("getUserPoint", params.get("u_num"));
		map.put("u_num", params.get("u_num"));
		map.put("p_status","사용");
		map.put("p_contents",h_name+" 예약 할인");
		map.put("p_point", params.get("inputPoint"));
		map.put("book_num", String.valueOf(book_num));
		map.put("p_remaining",String.valueOf(u_point));
		sqlSession.insert("savePointDB", map);
	}
	
//	호텔전체목록
	public Map<String, Integer> hotelList(){
		List<HotelDTO> hotelList = sqlSession.selectList("hotelList");
		
//		지역 다 정해지면 추가
		String[] location = new String[] {"서울","부산","제주","속초","강릉","경주","여수","인천","전주","춘천"};
		Map<String, Integer> map = new Hashtable<String, Integer>();
		
		for(int i=0; i<hotelList.size(); i++) {
			HotelDTO hdto = hotelList.get(i);
			String h_address = hdto.getH_address();
			
			for(int j=0; j<location.length; j++) {
				if(h_address.contains(location[j])) {
					if(map.containsKey(location[j])) {
						int count = map.get(location[j]);
						count++;
						map.put(location[j], count);
					}
					else {
						int count = 1;
						map.put(location[j], count);
					}
				}
			}
		}
		return map;
	}
	
	// 특정 객실이 예약중인지 아닌지 리턴하는 메소드
	public String isBookedRoom(Map<String,String> map) {
		String sql = "SELECT count(*) FROM project_booking WHERE room_num="+map.get("room_num")+" AND "
				+ "(book_indate <= '"+map.get("book_outdate")+
				"' AND book_outdate >= '"+map.get("book_indate")+"') AND book_status <> 'deny'";
		
		Map<String, String> map2 = new Hashtable<>();
		map2.put("sql", sql);
		
		int res = sqlSession.selectOne("isBookedRoom", map2);
		
		String isBooked = res > 0 ? "y" : "n";
		
		return isBooked;
	}
	
}
