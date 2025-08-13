package com.example.template.admin;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.template.common.dto.AdminDto;
import com.example.template.common.dto.OffsetBasedPageRequest;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

/**
 * AdminController
 * - 회원 관련 컨트롤러
 *
 * @author myungki you
 * @created 2025/08/06
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("user")
public class AdminController {

    private final AdminService adminService;
    
    /**
     * @param AdminListRequest 회원 조회에 사용되는 요청 정보
     * @return AdminListResponse 회원 목록
     * 
     * SQL 을 이용한 회원 목록 조회 API
     */
    @GetMapping("/v1")
	public ResponseEntity<List<AdminDto.AdminListResponse>> getVocQuestionsVer1(
			AdminDto.AdminListRequest condition,
			@RequestParam(name="offset") @NotNull long offset,
    		@RequestParam(name="limit") @NotNull int limit) {
		Pageable pageable = new OffsetBasedPageRequest(offset, limit);
		return new ResponseEntity<>(adminService.getAdminListV1(pageable, condition), HttpStatus.OK);
	}
    
    /**
     * @param AdminListRequest 회원 조회에 사용되는 요청 정보
     * @return AdminListResponse 회원 목록
     * 
     * JPA 를 이용한 회원 목록 조회 API
     */
    @GetMapping("/v2")
	public ResponseEntity<List<AdminDto.AdminListResponse>> getVocQuestionsVer2(
			AdminDto.AdminListRequest condition,
			@RequestParam(name="offset") @NotNull long offset,
    		@RequestParam(name="limit") @NotNull int limit) {
		Pageable pageable = new OffsetBasedPageRequest(offset, limit);
		return new ResponseEntity<>(adminService.getAdminListV2(pageable, condition), HttpStatus.OK);
	}
    
    /**
     * @param AdminListRequest 회원 조회에 사용되는 요청 정보
     * @return AdminListResponse 회원 목록
     * 
     * QueryDSL 을 이용한 회원 목록 조회 API
     */
    @GetMapping("/v3")
	public ResponseEntity<List<AdminDto.AdminListResponse>> getVocQuestionsVer3(
			AdminDto.AdminListRequest condition,
			@RequestParam(name="offset") @NotNull long offset,
    		@RequestParam(name="limit") @NotNull int limit) {
		Pageable pageable = new OffsetBasedPageRequest(offset, limit);
		return new ResponseEntity<>(adminService.getAdminListV3(pageable, condition), HttpStatus.OK);
	}
}


