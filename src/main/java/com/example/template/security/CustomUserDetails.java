package com.example.template.security;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.template.constants.ApprovalStatus;

public class CustomUserDetails implements UserDetails {

    private static final long serialVersionUID = 1L;

    private final String username;             // AdminEntity PK
    private final String password;
    private final ApprovalStatus approvalStatus;
    private final boolean isActive;
    private final Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(String username, String password, ApprovalStatus approvalStatus, 
    		boolean isActive, String role) {
        this.username = username;
        this.password = password;
        this.approvalStatus = approvalStatus;
        this.isActive = isActive;
        this.authorities = Collections.singletonList(new SimpleGrantedAuthority(role));
    }

	public String getUsername() {
        return username;
    }


    public ApprovalStatus getApprovalStatus() {
        return approvalStatus;
    }
    
    public boolean isActive() {
	  	return isActive;
	  }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // 필요에 따라 approvalStatus로 판단 가능
    }

    @Override
    public boolean isAccountNonLocked() {
//        return approvalStatus != ApprovalStatus.SUSPENDED; // 일시정지면 잠긴 계정
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
//        return approvalStatus == ApprovalStatus.ACTIVE;
    	return true;
    }
    
    

}

