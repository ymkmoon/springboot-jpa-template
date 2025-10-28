package com.example.template.security;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public class SecurityConstants {

	private SecurityConstants() {} // 인스턴스화 방지

	// Request Header 에 토큰이 존재하지 않아도 PASS
	// + 권한에 대한 보안 체크 X
	protected static final String[] SECURITY_WHITELIST = {
		    "/auth/sign-in",
		    "/auth/sign-up"
		};
    
	// Request Header 에 토큰이 존재하지 않아도 PASS
	public static final List<String> FILTER_WHITELIST =
            Collections.unmodifiableList(
                    Stream.concat(
                            Arrays.stream(SECURITY_WHITELIST), // SECURITY_WHITELIST 값을 포함
                            Arrays.stream(new String[]{
                                    "/h2-console",
                                    "/actuator",
                                    "/actuator/health",
                                    "/actuator/prometheus"
                            })
                    ).distinct().toList()
            );
    
}
