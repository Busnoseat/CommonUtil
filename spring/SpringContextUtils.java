package com.houbank.goutong.core.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * 全局工具类<br/>
 * getBean的方式
 * @author kuisheng
 * @typeName com.houbank.common.context.SpringContextUtil
 */
@Component
public class SpringContextUtils implements ApplicationContextAware,
		DisposableBean {
	private static Logger logger = LoggerFactory
			.getLogger(SpringContextUtils.class);
	private static ApplicationContext applicationContext;

	public static ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	/**
	 * 实现ApplicationContextAware接口, 注入Context到静态变量中.
	 */
	@Override
	public void setApplicationContext(
			final ApplicationContext applicationContext) throws BeansException {
		logger.debug("注入ApplicationContext到SpringContextHolder:{}",
				applicationContext);
		if (SpringContextUtils.applicationContext != null) {
			logger.warn("SpringContextHolder中的ApplicationContext被覆盖, 原有ApplicationContext为:"
					+ SpringContextUtils.applicationContext);
		}
		SpringContextUtils.applicationContext = applicationContext;

	}

	/**
	 * @param name
	 *            getBean的id
	 * @return T
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getBean(final String name) {
		return (T) applicationContext.getBean(name);
	}

	/**
	 * 从静态变量applicationContext中取得Bean, 自动转型为所赋值对象的类型.
	 *
	 * @param requiredType
	 * @return
	 */
	public static <T> T getBean(final Class<T> requiredType) {
		T t = null;
		try {
			final String name = requiredType.getSimpleName();
			t = getBean(getStr(name));
		} catch (final Exception e) {
			t = applicationContext.getBean(requiredType);
		}
		return t;
	}

	private static String getStr(final String str) {
		final char begin = str.charAt(0);
		return String.valueOf(begin) + str.substring(1);

	}


	/**
	 * 实现DisposableBean接口, 在Context关闭时清理静态变量.
	 */
	@Override
	public void destroy() throws Exception {
		clear();
	}

	/**
	 * 清除SpringContextUtil中的ApplicationContext为Null.
	 */
	public static void clear() {
		logger.debug("clear SpringContextUtil's  ApplicationContext:"
				+ applicationContext);
		applicationContext = null;
	}

}
