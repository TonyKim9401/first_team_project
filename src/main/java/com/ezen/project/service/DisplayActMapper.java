package com.ezen.project.service;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ezen.project.model.ActivityDTO;
import com.ezen.project.model.BookingActDTO;
import com.ezen.project.model.ReviewActDTO;
import com.ezen.project.model.WishListActDTO;

@Service
public class DisplayActMapper {
	
	@Autowired
	private SqlSession sqlSession;

	public int countSport() {
		return sqlSession.selectOne("sport");
	}
	public int countStudy() {
		return sqlSession.selectOne("study");
	}
	public int countEnt() {
		return sqlSession.selectOne("ent");
	}
	public int countMusic() {
		return sqlSession.selectOne("music");
	}
	public int countCulture() {
		return sqlSession.selectOne("culture");
	}
	public int countCooking() {
		return sqlSession.selectOne("cooking");
	}

	// �˻��� �Է½� ��� �������� �޼ҵ� (�Է¾��ϸ� ���� ������)
	public List<ActivityDTO> listActBySearch(String search){
		Map<String, String> map = new Hashtable<String, String>();
		map.put("search",search);
		return sqlSession.selectList("listActBySearch", map);
	}
	
	// �˻��� �ڵ��ϼ��� ���� �ּҿ� ��Ƽ��Ƽ���� ���� �ԷµǴ� ��쿡 ����� �������� �޼ҵ�
	public List<ActivityDTO> listActByNameAndAddr(String aname, String addr) {
		Map<String, String> map = new Hashtable<>();
		map.put("addr", "%"+addr+"%");
		map.put("aname", "%"+aname+"%");
		return sqlSession.selectList("listActByNameAndAddr", map);
	}
	
	// �˻��� �Է� ���� ī�װ��� ������ �� ����� �������� �޼ҵ�
	public List<ActivityDTO> listActByCode(String code) {
		Map<String, String> map = new Hashtable<String, String>();
		map.put("a_code", code);
		return sqlSession.selectList("listActByCode", map);
	}

	// �˻��� �Է��ؼ� ���� ������� ���͸� ������ �� ����� �������� �޼ҵ�
	public List<ActivityDTO> listActBySearchAndCode(String search, String code) {
		Map<String, String> map = new Hashtable<String, String>();
		map.put("search", search);
		map.put("a_code", code);
		return sqlSession.selectList("listActBySearchAndCode", map);
	}

	public ActivityDTO activityContent(String a_num) {
		return sqlSession.selectOne("activityContent", a_num);
	}
	
	// ���ø���Ʈ üũ���� Ȯ��
	public int isWishActCheck(int a_num, int u_num) {
		Map<String, Integer> map = new Hashtable<String, Integer>();
		map.put("a_num", a_num);
		map.put("u_num", u_num);
		return sqlSession.selectOne("isWishActCheck", map);
	}
	
	public List<WishListActDTO> getWishListAct(int u_num){
		return sqlSession.selectList("getWishListAct", u_num);
	}
	
	// ���ø���Ʈ ���������� ����
	public void wishActReleaseWL(int w_num) {
		sqlSession.delete("wishActReleaseWL", w_num);
	}
	
	// ��Ƽ��Ƽ �˻���� ���������� ���ø���Ʈ ����
	public void wishActReleaseOK(Map<String,String> params) {
		sqlSession.delete("wishActRelease", params);
	}
	
	// ��Ƽ��Ƽ �˻���� ���������� ���ø���Ʈ ���
	public void wishActCheckOK(Map<String, String> params) {
		sqlSession.insert("wishActCheck", params);
	}
	
//	�ڵ��ϼ�
	public List<String> allActOptions() {
		List<String> totalList = new ArrayList<String>();
		List<String> allOptions = sqlSession.selectList("getActNames");
		List<String> actAddresses = sqlSession.selectList("getActAddresses");
		for(int i=0; i<allOptions.size(); i++) {
			String allOption = allOptions.get(i);
			String[] address = actAddresses.get(i).split("@");
			totalList.add(allOption + ", "+ address[0]);
		}
		return totalList;
	}
	
	public boolean isDuplBookAct(BookingActDTO badto) {
		int duplCount = sqlSession.selectOne("isDuplBookAct", badto);
		boolean isDupl = duplCount > 0 ? true : false;
		
		return isDupl;
	}
	
	public int insertBookAct(BookingActDTO badto) {
		return sqlSession.insert("insertBookAct", badto);
	}
	
	public int getMaxBooker(int p_num) {
		return sqlSession.selectOne("getMaxBooker", p_num);
	}
	
	public List<Integer> listCurrentBooker(int p_num, String book_date) {
		Map<String, String> map = new Hashtable<>();
		map.put("p_num", String.valueOf(p_num));
		map.put("ba_bookdate", book_date);
		
		return sqlSession.selectList("listCurrentBooker", map);
	}

	public BookingActDTO getBookAct(int ba_num) {
		return sqlSession.selectOne("getBookAct", ba_num);
	}
	
	public int deleteActBook(int ba_num) {
		return sqlSession.update("deleteActBook", ba_num);
	}
	
	public int countReview(int a_num) {
		return sqlSession.selectOne("countReview", a_num);
	}
		
	public List<Integer> allReviewPointList(int a_num){
		return sqlSession.selectList("allReviewPointList", a_num);
	}
	
//	��Ƽ��Ƽ�� �ı� ���� ��ȯ
	public int getReviewCountByAct(int a_num) {
		return sqlSession.selectOne("getReviewCountByAct", a_num);
	}
	
	public List<ReviewActDTO> listReviewByAct(int a_num) {
		return  sqlSession.selectList("listReviewByAct", a_num);
		
	}
	public ActivityDTO getActivityContent(int a_num) {
		return sqlSession.selectOne("getActivityContent", a_num);
	}
}
