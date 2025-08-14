package com.example.template.admin;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.template.model.entity.AdminEntity;

@Repository
public interface AdminRepository extends JpaRepository<AdminEntity, String> {
	AdminEntity findAccountByName(String name);
	AdminEntity findAccountByLoginId(String loginId);
	boolean existsByPhoneNumber(String phoneNumber);
	boolean existsByEmail(String email);
	boolean existsByLoginId(String loginId);
	
	
	/**
     * V1: 네이티브 SQL을 사용한 회원 목록 조회
     * @Query 어노테이션에 nativeQuery = true 속성을 사용하여 SQL을 직접 작성합니다.
     */
    @Query(value = "SELECT * FROM admin " +
                   "WHERE (:loginId IS NULL OR login_id = :loginId) " +
                   "AND (:name IS NULL OR name = :name) " +
                   "AND (:email IS NULL OR email = :email) " +
                   "AND (:phoneNumber IS NULL OR phone_number = :phoneNumber) " +
                   "ORDER BY created_at DESC,id DESC " +
                   "LIMIT :limit OFFSET :offset",
                   nativeQuery = true)
    List<AdminEntity> findAdminListV1(@Param("loginId") String loginId,
                                      @Param("name") String name,
                                      @Param("email") String email,
                                      @Param("phoneNumber") String phoneNumber,
                                      @Param("offset") long offset,
                                      @Param("limit") int limit);
    
    @Query(value = "SELECT COUNT(*) FROM admin " +
            "WHERE (:loginId IS NULL OR login_id = :loginId) " +
            "AND (:name IS NULL OR name = :name) " +
            "AND (:email IS NULL OR email = :email) " +
            "AND (:phoneNumber IS NULL OR phone_number = :phoneNumber)",
            nativeQuery = true)
    long countAdminListV1(@Param("loginId") String loginId,
                          @Param("name") String name,
                          @Param("email") String email,
                          @Param("phoneNumber") String phoneNumber);
    
    
    /**
     * V2: Spring Data JPA의 Pageable을 활용한 회원 목록 조회
     * 메서드 이름 쿼리를 사용하며, Page 객체를 반환하여 페이징 정보를 함께 제공합니다.
     * DTO로 바로 매핑하는 기능은 제공하지 않으므로 엔티티를 반환합니다.
     */
    Page<AdminEntity> findAllByOrderByCreatedAtDescIdDesc(Pageable pageable);
}
