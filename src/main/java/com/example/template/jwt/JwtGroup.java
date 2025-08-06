package com.example.template.jwt;

import java.time.Duration;
import java.util.Arrays;

import com.example.template.common.CommonConstants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum JwtGroup {

	ACCESS_TOKEN(CommonConstants.ACCESS_TOKEN.getTitle(), "acc_ess_tok_ens_ecr_et", Duration.ofMinutes(30).toMillis()),
	REFRESH_TOKEN(CommonConstants.REFRESH_TOKEN.getTitle(), "ref_res_hto_ken_sec_ret", Duration.ofDays(3).toMillis()),
	EMPTY(CommonConstants.EMPTY.getTitle(), CommonConstants.EMPTY.getTitle(), 0)
	;
	
	private final String type;
	private final String secretKey;
	private final long validity;
	
	public static JwtGroup tokenInformation(String tokenType) {
		return Arrays.stream(JwtGroup.values())
				.filter(token -> token.getType().equals(tokenType))
				.findAny()
				.orElse(EMPTY);
	}
	
}
