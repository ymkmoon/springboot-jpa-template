package com.example.template.admin;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.template.model.entity.AdminEntity;

@Repository
public interface AdminRepository extends JpaRepository<AdminEntity, Long> {
	
	AdminEntity findAccountByName(String name);
	AdminEntity findAccountByLoginId(String loginId);

}
