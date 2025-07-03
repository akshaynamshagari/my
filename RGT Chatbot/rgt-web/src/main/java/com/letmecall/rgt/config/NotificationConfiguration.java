
package com.letmecall.rgt.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executors;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.VelocityException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ConcurrentTaskExecutor;
import org.springframework.security.task.DelegatingSecurityContextTaskExecutor;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactoryBean;

import com.letmecall.rgt.framework.notification.NotificationTypeEnum;
import com.letmecall.rgt.framework.notification.task.NotificationTask;
import com.letmecall.rgt.servcie.notification.ChangePasswordEmailNotification;
import com.letmecall.rgt.servcie.notification.ContactUsEmaiNotification;
import com.letmecall.rgt.servcie.notification.ForGotPasswordEmailNotification;
import com.letmecall.rgt.servcie.notification.InviteMemberEmaiNotification;
import com.letmecall.rgt.servcie.notification.LoginPasswordEmailNotification;

@Configuration
@EnableAsync
public class NotificationConfiguration {

	@Value("${spring.velocity.resourceLoaderPath}")
	private String velocityPath;

	@Autowired
	private ForGotPasswordEmailNotification forGotPasswordEmailNotification;

	@Autowired
	private ChangePasswordEmailNotification changePasswordEmailNotification;

	@Autowired
	private LoginPasswordEmailNotification loginPasswordEmailNotification;

	@Autowired
	private InviteMemberEmaiNotification inviteMemberEmaiNotification;

	@Autowired
	private ContactUsEmaiNotification contactUsEmaiNotification;

	@Bean
	public Map<String, NotificationTask> emailNotificationMap() {
		Map<String, NotificationTask> map = new HashMap<>();
		map.put(NotificationTypeEnum.FORGOT_PASSWORD_OTP.name(), forGotPasswordEmailNotification);
		map.put(NotificationTypeEnum.CHANGE_PASSWORD_OTP.name(), changePasswordEmailNotification);
		map.put(NotificationTypeEnum.LOGIN_PASSWORD_OTP.name(), loginPasswordEmailNotification);
		map.put(NotificationTypeEnum.INVITE_MEMBER.name(), inviteMemberEmaiNotification);
		map.put(NotificationTypeEnum.CONTACTUS.name(), contactUsEmaiNotification);
		return map;
	}

	@Bean
	public Map<String, NotificationTask> faxNotificationMap() {
		return new HashMap<>();
	}

	@Bean
	public Map<String, NotificationTask> smsNotificationMap() {
		return new HashMap<>();
	}

	@Bean
	public Map<String, NotificationTask> voiceNotificationMap() {
		return new HashMap<>();
	}

	@Bean
	public Map<String, NotificationTask> pushNotificationMap() {
		return new HashMap<>();
	}

	@Bean
	public VelocityEngine getVelocityEngine() throws VelocityException {
		Properties props = new Properties();
		props.put("resource.loader", "file");
		props.put("file.resource.loader.class", "org.apache.velocity.runtime.resource.loader.FileResourceLoader");
		props.put("file.resource.loader.path", velocityPath);
		props.put("file.resource.loader.cache", "false");
		return new VelocityEngine(props);
	}

	@Bean
	public DelegatingSecurityContextTaskExecutor threadsPool() {
		return new DelegatingSecurityContextTaskExecutor(getConcurrentTaskExecutor());
	}

	/*
	 * FreeMarker configuration.
	 */
	@Bean(name = "freemarkerConfiguration")
	public FreeMarkerConfigurationFactoryBean getFreeMarkerConfiguration() {
		FreeMarkerConfigurationFactoryBean bean = new FreeMarkerConfigurationFactoryBean();
		bean.setTemplateLoaderPath(velocityPath);
		return bean;
	}

	@Lazy
	@Bean(name = "configuration")
	public freemarker.template.Configuration getConfiguration() {
		return getFreeMarkerConfiguration().getObject();
	}
	
	@Bean(name = "taskExecutor")
	public TaskExecutor getConcurrentTaskExecutor() {
		return new ConcurrentTaskExecutor(Executors.newFixedThreadPool(10));
	}
}
