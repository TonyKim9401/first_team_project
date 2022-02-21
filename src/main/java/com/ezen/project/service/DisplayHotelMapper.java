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
import com.ezen.project.model.NUserBookingDTO;
import com.ezen.project.model.ReviewDTO;
import com.ezen.project.model.RoomDTO;

@Service
public class DisplayHotelMapper {

	@Autowired
	private SqlSession sqlSession;
	
	public void checkToday(String today) {
		sqlSession.update("checkToday", today);
	}
	
	// �ڵ��ϼ�
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
	
	// ���� ����Ʈ ����Ʈ
	public List<HotelDTO> listHotelByLocation(String condition){
		return sqlSession.selectList("listHotelByLocation", "%"+condition+"%");
	}
	
	// ���� ���ݼ� �����ؼ� ��������
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
	
	// ���� ���ݼ� �����ؼ� ��������
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
	
	// ���ø���Ʈ üũ���� Ȯ��
	public int isWishCheck(int h_num, int u_num) {
		Map<String, Integer> map = new Hashtable<String, Integer>();
		map.put("h_num", h_num);
		map.put("u_num", u_num);
		return sqlSession.selectOne("isWishCheck", map);
	}
	
	// ȣ�� �ϳ��� �ı� ���� ��ȯ
	public int getReviewCountByHotel(int h_num) {
		return sqlSession.selectOne("getReviewCountByHotel", h_num);
	}
	
	// ȣ�� �ϳ��� �������� List�� ��ȯ
	public List<Integer> listReviewStar(int h_num) {
		List<Integer> star = sqlSession.selectList("listReviewStar", h_num);
		return star;
	}
	
	//	���� Ÿ���� �������� �ش� ȣ���� ���� �׷� ����Ʈ�� ������
	//TWIN, DOUBLE, DELUXE
	public List<RoomDTO> listRoomByType(int h_num, String type) {
		Map<String, String> map = new Hashtable<String, String>();
		map.put("h_num",String.valueOf(h_num));
		map.put("type", type);
		
		return sqlSession.selectList("listRoomByType", map);
	}
	
//	�ش� ȣ���� �ı⸦ ����Ʈ�� ��ȯ
	public List<ReviewDTO> listReviewByHotel(int h_num) {
		return sqlSession.selectList("listReviewByHotel", h_num);
	}
	
//	���ø���Ʈ ����
	public void wishRelease(Map<String, String> params) {
		sqlSession.delete("wishRelease", params);
	}

//	���ø���Ʈ ����
	public void wishCheck(Map<String, String> params) {
		sqlSession.insert("wishCheck", params);
	}
	
//	���ø���Ʈ�޴����� ���ϱ� ����
	public void wishReleaseWL(int w_num) {
		sqlSession.delete("wishReleaseWL", w_num);
	}
	
	// ���� �ߺ� �ִ��� Ȯ��
	public boolean isDuplBook(Map<String, String> params) {
		int duplCount = sqlSession.selectOne("isDuplBook", params);
		boolean isDupl = duplCount > 0 ? true : false;
		
		return isDupl;
	}
	
//	��������&���� ����Ʈ ����
	public int insertBook(Map<String,String> params) {
		return sqlSession.insert("insertBook", params);
	}
	
//	������ �������� ��ȯ
	public BookingDTO getBook(int book_num) {
		BookingDTO bdto = sqlSession.selectOne("getBook", book_num);
		return bdto;
	}
	public int updatePoint(int updatePoint, int u_num) {
		Map<String, Integer>map = new Hashtable<String, Integer>();
		map.put("updatePoint", updatePoint);
		map.put("u_num", u_num);
		return sqlSession.update("updatePoint", map);
	}
	
//	�������
	public int deleteBook(int book_num, int u_num) {
		return sqlSession.update("deleteBook", book_num);
	}
	public void cancelPoint(Map<String,String> params) {
		 sqlSession.insert("cancelPoint", params);
	}
	
//	���ǰ��ݹ�ȯ
	public int getRoomPrice(int book_num) {
		int room_num = sqlSession.selectOne("getRoomNum",book_num);
		return sqlSession.selectOne("getRoomPrice", room_num);
	}
	
//	����Ʈdb���� ���
	public void savePoint(Map<String,String> params) {
		
		sqlSession.update("savePointUpdate", params);
		
		Map<String, String> map = new Hashtable<String, String>();
		String h_name = sqlSession.selectOne("getHotelName",params);
		int book_num = sqlSession.selectOne("getBookNum", params);
		int u_point = sqlSession.selectOne("getUserPoint", params.get("u_num"));
		
		map.put("u_num", params.get("u_num"));
		map.put("p_status","����");
		map.put("p_contents",h_name+" ���� Ȯ��");
		map.put("p_point", params.get("book_savepoint"));
		map.put("book_num", String.valueOf(book_num));
		map.put("p_remaining",String.valueOf(u_point));
		sqlSession.insert("savePointDB", map);
		sqlSession.update("updatePointEnd",u_point);
	}
	
//	����� ����� ����Ʈdb���
	public void usedPoint(Map<String,String> params) {
		
		sqlSession.update("usedPointUpdate", params);
		
		Map<String, String> map = new Hashtable<String, String>();
		String h_name = sqlSession.selectOne("getHotelName",params);
		int book_num = sqlSession.selectOne("getBookNum", params);
		int u_point = sqlSession.selectOne("getUserPoint", params.get("u_num"));
		map.put("u_num", params.get("u_num"));
		map.put("p_status","���");
		map.put("p_contents",h_name+" ���� ����");
		map.put("p_point", params.get("inputPoint"));
		map.put("book_num", String.valueOf(book_num));
		map.put("p_remaining",String.valueOf(u_point));
		sqlSession.insert("savePointDB", map);
	}
	
//	ȣ����ü���
	public Map<String, Integer> hotelList(){
		List<HotelDTO> hotelList = sqlSession.selectList("hotelList");
		
//		���� �� �������� �߰�
		String[] location = new String[] {"����","�λ�","����","����","����","����","����","��õ","����","��õ"};
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

	// Ư�� ���Ǳ׷쿡�� ����� ���� ������ �������� �޼ҵ�
	public int countBookedRoom(Map<String,String> map) {
		String sql = "SELECT count(*) FROM project_booking WHERE room_code='"+map.get("room_code")+"' AND "
				+ "(book_indate <= '"+map.get("book_outdate")+
				"' AND book_outdate >= '"+map.get("book_indate")+"') AND book_status <> 'deny'";
		
		Map<String, String> map2 = new Hashtable<>();
		map2.put("sql", sql);
		
		return sqlSession.selectOne("countBookedRoom", map2);
	}
	
	public int countBookedRoomNonUser(Map<String, String> map) {
		String sql = "SELECT count(*) FROM project_nonUserBooking WHERE room_code='"+map.get("room_code")+"' AND "
				+ "(book_indate <= '"+map.get("book_outdate")+
				"' AND book_outdate >= '"+map.get("book_indate")+"') AND book_status <> 'deny'";
		
		Map<String, String> map2 = new Hashtable<>();
		map2.put("sql", sql);
		
		return sqlSession.selectOne("countBookedRoom", map2);
	}
	
	// Ư�� ������ ���������� �ƴ��� �����ϴ� �޼ҵ�
	public boolean isBookedRoom(Map<String,String> map) {
		String sql = "SELECT count(*) FROM project_booking WHERE room_num="+map.get("room_num")+" AND "
				+ "(book_indate <= '"+map.get("book_outdate")+
				"' AND book_outdate >= '"+map.get("book_indate")+"') AND book_status <> 'deny'";
		
		Map<String, String> map2 = new Hashtable<>();
		map2.put("sql", sql);
		
		int res = sqlSession.selectOne("isBookedRoom", map2);
		
		return res > 0 ? true : false;
	}
	
	public boolean isBookedRoomNonUser(Map<String,String> map) {
		String sql = "SELECT count(*) FROM project_nonUserBooking WHERE room_num="+map.get("room_num")+" AND "
				+ "(book_indate <= '"+map.get("book_outdate")+
				"' AND book_outdate >= '"+map.get("book_indate")+"') AND book_status <> 'deny'";
		
		Map<String, String> map2 = new Hashtable<>();
		map2.put("sql", sql);
		
		int res = sqlSession.selectOne("isBookedRoom", map2);
		
		return res > 0 ? true : false;
	}

	public int insertBookNonUser(Map<String, String> params) {
		return sqlSession.insert("insertBookNonUser", params);
	}

	public int getNonUserBookNum() {
		return sqlSession.selectOne("getNonUserBookNum");
	}
	
	public NUserBookingDTO getNonUserBooking(Map<String, String> params) {
		return sqlSession.selectOne("getNonUserBooking", params);
	}

	public int deleteNonUserBook(String book_num) {
		return sqlSession.delete("deleteNonUserBook", Integer.parseInt(book_num));
	}
	
	// ���� �ߺ� �ִ��� Ȯ��
	public boolean isDuplBookNonUser(Map<String, String> params) {
		int duplCount = sqlSession.selectOne("isDuplBookNonUser", params);
		boolean isDuplNUser = duplCount > 0 ? true : false;
		
		return isDuplNUser;
	}
	
}
