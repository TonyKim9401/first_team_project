package com.ezen.project.service;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ezen.project.model.CompanyQnADTO;
import com.ezen.project.model.UserQnADTO;

@Service
public class QnABoardMapper {
	
	@Autowired
	private SqlSession sqlSession;
	
	// QnA게시판 리스트를 전부 꺼내서 보여주기 위한 메소드 (관리자 페이지용)
	public List<CompanyQnADTO> listAdminCompanyQnA(int start, int end) {
		Map<String, Integer> map = new Hashtable<String, Integer>();
		map.put("start", start);
		map.put("end", end);
		
		return sqlSession.selectList("listAdminCompanyQnA", map);
	}
	
	// QnA게시판 리스트를 꺼내서 보여주기 위한 메소드 (기업 회원용)
	public List<CompanyQnADTO> listCompanyQnA(int c_num) {
		return sqlSession.selectList("listCompanyQnA", c_num);
	}
	
	// 게시글이 총 몇개인지 확인하는 메소드 (게시판 페이지 나눌때 사용)
	public int getAdminCompanyQnACount() {
		return sqlSession.selectOne("getAdminCompanyQnACount");
	}

	// 특정 기업의 게시글이 총 몇개인지 확인하는 메소드 (게시판 페이지 나눌때 사용)
	public int getCompanyQnACount(int c_num) {
		return sqlSession.selectOne("getCompanyQnACount");
	}
	
	// 게시글 등록할 때 사용하는 메소드
	public int insertCompanyQnA(CompanyQnADTO cqadto) {
		String sql = "";
		
		if(cqadto.getCqa_num()==0) {
			// 게시글이 답글이 아니라 처음 쓰는 글일 경우
			sql = "update project_companyQnA set cqa_re_step=cqa_re_step+1";
		}else {
			// 게시글이 답글일 경우
			sql = "update project_companyQnA set cqa_re_step=cqa_re_step+1 where cqa_re_step>"+cqadto.getCqa_re_step();	
			cqadto.setCqa_re_step(cqadto.getCqa_re_step() + 1);
			cqadto.setCqa_re_level(cqadto.getCqa_re_level() + 1);
		}
		
		Map<String, String> map = new Hashtable<String, String>();
		map.put("sql", sql);
		int res = sqlSession.update("plusCompanyQnARe", map);
		
		return sqlSession.insert("insertCompanyQnA", cqadto);
	}
	
	// 게시글 목록에서 게시글을 눌렀을 때 본문을 보여주기 위한 메소드
	public CompanyQnADTO getCompanyQnA(int cqa_num) {
		return sqlSession.selectOne("getCompanyQnA", cqa_num);
	}
	
	// 게시글을 삭제할 때 사용하는 메소드
	public int deleteCompanyQnA(int cqa_num) {
		return sqlSession.delete("deleteCompanyQnA", cqa_num);
	}
	
	// 게시글을 수정할 때 사용하는 메소드
	public int updateCompanyQnA(CompanyQnADTO cqadto) {
		return sqlSession.update("updateCompanyQnA", cqadto);
	}
	
	// QnA게시판 리스트를 전부 꺼내서 보여주기 위한 메소드 (관리자 페이지용)
	public List<UserQnADTO> listAdminUserQnA(int start, int end) {
		Map<String, Integer> map = new Hashtable<String, Integer>();
		map.put("start", start);
		map.put("end", end);
		
		return sqlSession.selectList("listAdminUserQnA", map);
	}
	
	// QnA게시판 리스트를 꺼내서 보여주기 위한 메소드 (회원 페이지용)
	public List<UserQnADTO> listUserQnA(int u_num) {
		return sqlSession.selectList("listUserQnA", u_num);
	}
	
	// 게시글이 총 몇개인지 확인하는 메소드 (게시판 페이지 나눌때 사용)
	public int getAdminUserQnACount() {
		return sqlSession.selectOne("getAdminUserQnACount");
	}
	
	// 특정 회원의 게시글이 총 몇개인지 확인하는 메소드 (게시판 페이지 나눌때 사용)
	public int getUserQnACount(int u_num) {
		return sqlSession.selectOne("getUserQnACount", u_num);
	}
	
	// 게시글 등록할 때 사용하는 메소드
	public int insertUserQnA(UserQnADTO uqadto) {
		String sql = "";
		
		if(uqadto.getUqa_num()==0) {
			// 게시글이 답글이 아니라 처음 쓰는 글일 경우
			sql = "update project_userQnA set uqa_re_step=uqa_re_step+1";
		}else {
			// 게시글이 답글일 경우
			sql = "update project_userQnA set uqa_re_step=uqa_re_step+1 where uqa_re_step>"+uqadto.getUqa_re_step();	
			uqadto.setUqa_re_step(uqadto.getUqa_re_step() + 1);
			uqadto.setUqa_re_level(uqadto.getUqa_re_level() + 1);
		}
		
		Map<String, String> map = new Hashtable<String, String>();
		map.put("sql", sql);
		int res = sqlSession.update("plusUserQnARe", map);
		
		return sqlSession.insert("insertUserQnA", uqadto);
	}
	
	// 게시글 목록에서 게시글을 눌렀을 때 본문을 보여주기 위한 메소드
	public UserQnADTO getUserQnA(int uqa_num) {
		return sqlSession.selectOne("getUserQnA", uqa_num);
	}
	
	// 게시글을 삭제할 때 사용하는 메소드
	public int deleteUserQnA(int uqa_num) {
		return sqlSession.delete("deleteUserQnA", uqa_num);
	}
	
	// 게시글을 수정할 때 사용하는 메소드
	public int updateUserQnA(UserQnADTO uqadto) {
		return sqlSession.update("updateUserQnA", uqadto);
	}
	
	public String getCnameByCnum(int c_num) {
		return sqlSession.selectOne("getCnameByCnum", c_num);
	}
	
	//UserQnA 게시글 리스트에서 작성자 닉네임을 불러올때 사용하는 메소드
	public String getUnicknameByUnum(int u_num) {
		return sqlSession.selectOne("getUnicknameByUnum", u_num);
	}

	
}
