package com.example.template.aop;

import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.ContentCachingRequestWrapper;

import com.example.template.util.DataParsingUtil;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

/**
 * LogAspect
 * - 어플리케이션의 동작 로그 추적 및 기록
 *
 * @author myungki you
 * @created 2025/08/06
 */
@Aspect
@Component
@RequiredArgsConstructor
public class LogAspect {
	
    Logger logger = LoggerFactory.getLogger(this.getClass());
    
    // 특정 패키지에서 로그 출력
    @Around("execution(* com.example.template..*Service.*(..))")
    public Object logging(ProceedingJoinPoint pjp) throws Throwable {
    	return around(pjp);
    }

    @Pointcut("@annotation(org.springframework.web.bind.annotation.GetMapping)")
    public void getMapping() {
    	logger.info("Pointcut GetMapping(): begin");
    	logger.info("Pointcut GetMapping(): end");
    }
    @Pointcut("@annotation(org.springframework.web.bind.annotation.PostMapping)")
	public void postMapping() {
		logger.info("Pointcut PostMapping(): begin");
		logger.info("Pointcut PostMapping(): end");
	}
	@Pointcut("@annotation(org.springframework.web.bind.annotation.RequestMapping)")
	public void requestMapping() {
		logger.info("Pointcut RequestMapping(): begin");
		logger.info("Pointcut RequestMapping(): end");
	}
	@Pointcut("@annotation(org.springframework.web.bind.annotation.PatchMapping)")
	public void patchMapping() {
		logger.info("Pointcut PatchMapping(): begin");
		logger.info("Pointcut PatchMapping(): end");
	}
    
	
	@Before("getMapping()") 
	public void beforeGetMethod(JoinPoint pjp) { 
		before(pjp);
	}
	@Before("postMapping()") 
	public void beforePostMethod(JoinPoint pjp) { 
		before(pjp);
	}
	@Before("requestMapping()") 
	public void beforeRequestMethod(JoinPoint pjp) { 
		before(pjp);
	}
	@Before("patchMapping()") 
	public void beforePatchMethod(JoinPoint pjp) { 
		before(pjp);
	}
	

	@Around("getMapping()")
	public Object aroundGet(ProceedingJoinPoint pjp) throws Throwable {
		HttpServletRequest original = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
		ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(original);
		
		Map<String, String[]> paramMap = wrappedRequest.getParameterMap();
		if (logger.isInfoEnabled() && !paramMap.isEmpty()) {
			logger.info("GET Parameter : [ {} ]", DataParsingUtil.paramMapToString(paramMap));
		}
		return around(pjp);
	}
	
	@Around("postMapping()")
	public Object aroundPost(ProceedingJoinPoint pjp) throws Throwable {
		HttpServletRequest original = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
		ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(original);
		if (logger.isInfoEnabled()) {
			logger.info("POST RequestBody : [ {} ]", IOUtils.toString(wrappedRequest.getReader()));
		}
		return around(pjp);
	}
	
	@Around("requestMapping()")
	public Object aroundRequest(ProceedingJoinPoint pjp) throws Throwable {
		return around(pjp);
	}
	
	@Around("patchMapping()")
	public Object aroundPatch(ProceedingJoinPoint pjp) throws Throwable {
		HttpServletRequest original = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
		ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(original);
		if (logger.isInfoEnabled()) {
			logger.info("PATCH RequestBody : [ {} ]", IOUtils.toString(wrappedRequest.getReader()));
		}
		return around(pjp);
	}
	
	private final Object around(ProceedingJoinPoint pjp) throws Throwable {
		logger.info("start @Arround - {} / {}", pjp.getSignature().getDeclaringTypeName(), pjp.getSignature().getName());
        Object result = pjp.proceed();
        logger.info("finished @Arround - {} / {}", pjp.getSignature().getDeclaringTypeName(), pjp.getSignature().getName());
        return result;
	}
	
	private final void before(JoinPoint pjp) {
		logger.info("start @Before - {} / {}", pjp.getSignature().getDeclaringTypeName(), pjp.getSignature().getName());
        logger.info("finished @Before - {} / {}", pjp.getSignature().getDeclaringTypeName(), pjp.getSignature().getName());
	}
    
}
