package com.letmecall.rgt.rest.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.letmecall.rgt.config.security.AuthenticationRequest;
import com.letmecall.rgt.domain.MemberInvite;
import com.letmecall.rgt.domain.UserAuthDetails;
import com.letmecall.rgt.domain.UserOtp;
import com.letmecall.rgt.framework.notification.NotificationTypeEnum;
import com.letmecall.rgt.utility.DateUtil;
import com.letmecall.service.MemberInviteService;
import com.letmecall.service.SocialUserService;
import com.letmecall.service.UserAuthDetailsServcie;
import com.letmecall.service.UserOtpService;

@Component
public class UserAuthDetailsHelper {

	private static final Logger logger = LoggerFactory.getLogger(UserAuthDetailsHelper.class);

	@Autowired
	private UserAuthDetailsServcie userDetailsService;

	@Autowired
	private BCryptPasswordEncoder bcryptEncoder;

	@Autowired
	private UserOtpService communityUserOtpService;

	@Autowired
	private MemberInviteService memberInviteService;

	@Autowired
	private SocialUserService socialUserService;

	public UserAuthDetails getUserDetailBean(Long userId, String userName) {
		logger.debug("getUserDetailBean: userName={}", userName);
		UserAuthDetails userAuthDetails = new UserAuthDetails();
		userAuthDetails.setUserId(userId);
		userAuthDetails.setEmailAddress(userName);
		return userDetailsService.fetchUserAuthDetails(userAuthDetails);
	}

	public UserAuthDetails fetchCompleteUserDetail(Long userId, String userName) {
		logger.debug("fetchCompleteUserDetail: userName={},userId={}", userName, userId);
		UserAuthDetails userAuthDetails = new UserAuthDetails();
		userAuthDetails.setEmailAddress(userName);
		userAuthDetails.setUserId(userId);
		return userDetailsService.fetchCompleteUserAuthDetails(userAuthDetails);
	}

	public UserAuthDetails updateLogOutTime(String userName) {
		logger.debug("updateLogOutTime: userName={}", userName);
		UserAuthDetails userAuthDetails = new UserAuthDetails();
		userAuthDetails.setEmailAddress(userName);
		return userDetailsService.updateLogOutTime(userAuthDetails);
	}

	public void updateLastAccessTime(Long userId, String userName) {
		logger.debug("updateLoginTime: userName={}", userName);
		UserAuthDetails userAuthDetails = new UserAuthDetails();
		userAuthDetails.setEmailAddress(userName);
		userAuthDetails.setUserId(userId);
		userDetailsService.updateLastAccessTime(userAuthDetails);
	}

	public void resetPassword(Long userId, String userName, String password) {
		logger.debug("resetPassword: userDetailBean={}", userName);
		UserAuthDetails userAuthDetails = new UserAuthDetails();
		userAuthDetails.setEmailAddress(userName);
		userAuthDetails.setUserId(userId);
		userAuthDetails.setPassword(bcryptEncoder.encode(password));
		userDetailsService.resetPassword(userAuthDetails);
	}

	public Boolean validateOldPasswordandNewPassword(String newPassword, String oldPassword) {
		logger.debug("validateOldPasswordandNewPassword");
		return bcryptEncoder.matches(newPassword, oldPassword);
	}

	public Boolean isCustomerExist(String userName) {
		logger.debug("isCustomerExist: userName={}", userName);
		UserAuthDetails userAuthDetails = new UserAuthDetails();
		userAuthDetails.setEmailAddress(userName);
		return userDetailsService.isCustomerExist(userAuthDetails);
	}

	public void createUserDetails(AuthenticationRequest authenticationRequest, MemberInvite memberInvite) {
		logger.debug("createUserDetails: authenticationRequest={}", authenticationRequest);
		UserAuthDetails userAuthDetails = new UserAuthDetails();
		userAuthDetails.setEmailAddress(authenticationRequest.getUserName());
		userAuthDetails.setPassword(bcryptEncoder.encode(authenticationRequest.getPassword()));
		userAuthDetails.setLastLoginTime(DateUtil.getCurrentTimeStamp());
		userAuthDetails.setIsActive(true);
		userAuthDetails.setIsRegistered(true);
		userAuthDetails.setLastLogOutTime(null);
		userAuthDetails.setFirstName(memberInvite.getFirstName());
		userAuthDetails.setLastName(memberInvite.getLastName());
		userDetailsService.createUserDetails(userAuthDetails);
		logger.debug("updateMemberInvite: memberInvite={}", memberInvite);
		memberInviteService.updateMemberInvite(memberInvite);
	}

	public void generateOTPForForGotPassword(UserOtp communityUserOtp, UserAuthDetails communityUserAuthDetails) {
		communityUserOtp.setUserId(communityUserAuthDetails.getUserId());
		communityUserOtp.setUserAuthDetails(communityUserAuthDetails);
		communityUserOtp.setType(NotificationTypeEnum.FORGOT_PASSWORD_OTP.name());
		communityUserOtpService.createUserOtp(communityUserOtp);
	}

	public Boolean verifyOTPForUser(Long userId, String otpNumber) {
		logger.debug("verifyOTPForUser: userId={},otpNumber={}", userId, otpNumber);
		return communityUserOtpService.verifyOTPForUser(userId, otpNumber);
	}

	public void generateOTPForUserLogin(UserOtp communityUserOtp, UserAuthDetails communityUserAuthDetails) {
		communityUserOtp.setUserId(communityUserAuthDetails.getUserId());
		communityUserOtp.setUserAuthDetails(communityUserAuthDetails);
		communityUserOtp.setType(NotificationTypeEnum.LOGIN_PASSWORD_OTP.name());
		communityUserOtpService.createUserOtp(communityUserOtp);
	}

	public MemberInvite validateMemberInviteKey(String userName, String resetKey) {
		logger.debug("isCustomerExist: userName={}", userName);
		return memberInviteService.validateMemberInviteKey(userName, resetKey);
	}

	public void saveLogOutTime(String userName, String provider) {
		logger.debug("updateLogOutTime: userName={}", userName);
		socialUserService.saveLogOutTime(userName, provider);
	}

}
