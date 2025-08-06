package com.example.template.context;

import org.springframework.context.ApplicationContext;

/**
 * BeanConstructor
 * - ApplcationContext 를 활용해 런타임 중 Bean 을 이름으로 조회
 *
 * @author myungki you
 * @created 2025/08/06
 */
public class BeanConstructor {
	
	private final String beanName;
	
	public BeanConstructor(String beanName) {
		this.beanName = beanName;
	}
	
    public Object getBean() {
        ApplicationContext applicationContext = ApplicationContextProvider.getApplicationContext();
        return applicationContext.getBean(beanName);
    }
    
}
 