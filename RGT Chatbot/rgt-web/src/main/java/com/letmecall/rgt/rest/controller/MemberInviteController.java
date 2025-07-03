package com.letmecall.rgt.rest.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.letmecall.rgt.aspect.ApiMetrics;
import com.letmecall.rgt.aspect.CalLog;
import com.letmecall.rgt.domain.MemberInvite;
import com.letmecall.rgt.domain.UserAuthDetails;
import com.letmecall.rgt.framework.notification.NotificationTypeEnum;
import com.letmecall.rgt.model.Response;
import com.letmecall.rgt.model.StatusType;
import com.letmecall.rgt.rest.helper.BaseRestUtill;
import com.letmecall.rgt.rest.helper.UserAuthDetailsHelper;
import com.letmecall.service.MemberInviteService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping(value = RgtResource.MEMBER_INVITE_BASE_URL)
public class MemberInviteController {

	private static final Logger logger = LoggerFactory.getLogger(MemberInviteController.class);

	@Autowired
	MemberInviteService communitiesMemberInviteService;

	@Autowired
	UserAuthDetailsHelper userAuthDetailsHelper;

	@PostMapping(consumes = RgtResource.CONSUMES, produces = RgtResource.PRODUCES)
	@ApiMetrics(methodName = "AddMemberInvite")
	@CalLog
	public ResponseEntity<Response<MemberInvite>> addMemberInvite(@RequestBody @Valid MemberInvite memberInvite,
			HttpServletRequest request) {
		Response<MemberInvite> response = null;
		try {
			Boolean isCustomerExist = userAuthDetailsHelper.isCustomerExist(memberInvite.getEmail());
			if (isCustomerExist != null && isCustomerExist) {
				logger.info("LoginActionController :: ExistingUser={} ", memberInvite.getEmail());
				response = Response.buildResponse(null, StatusType.ERROR, HttpStatus.INTERNAL_SERVER_ERROR.value(),
						"Your details are existing with us. Please login to search.");
			} else {
				Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
				String userName = authentication.getPrincipal().toString();
				UserAuthDetails userAuthDetails = new UserAuthDetails();
				userAuthDetails.setEmailAddress(userName);
				sendEmailNotification(request, userAuthDetails, memberInvite);
				response = Response.buildResponse(null, StatusType.SUCCESS, 200,
						"Invite member added and mail sent successfully");
			}
		} catch (Exception ex) {
			logger.error("Exception Occured while executing service", ex);
			response = Response.buildResponse(null, StatusType.ERROR, 500, ex.getMessage());
		}
		return new ResponseEntity<>(response, HttpStatus.valueOf(Integer.parseInt(response.getCode())));
	}

	private void sendEmailNotification(HttpServletRequest request, UserAuthDetails userAuthDetails,
			MemberInvite communitiesMemberInvite) {
		communitiesMemberInvite.setUserAuthDetails(userAuthDetails);
		communitiesMemberInvite = communitiesMemberInviteService.createMemberInvite(communitiesMemberInvite);
		String clientIPAddress = BaseRestUtill.getClientIpAddress(request);
		String serverPath = BaseRestUtill.getServerPath(request);
		communitiesMemberInvite.setIpAddress(clientIPAddress);
		communitiesMemberInvite.setServerPath(serverPath);
		communitiesMemberInvite.setType(NotificationTypeEnum.INVITE_MEMBER.name());
		communitiesMemberInviteService.inviteMemberThroughEmail(communitiesMemberInvite);
	}

	@GetMapping()
	@ApiMetrics(methodName = "FetchMemberInvite")
	@CalLog
	public ResponseEntity<Response<List<MemberInvite>>> fetchMemberInvite(
			@RequestParam(name = "pageNumber", defaultValue = "1") Integer pageNumber,
			@RequestParam(name = "pageSize", defaultValue = "10") Integer limit) {
		MemberInvite memberInvite = new MemberInvite();
		memberInvite.setPageNumber(pageNumber);
		memberInvite.setPageSize(limit);
		Response<List<MemberInvite>> response = Response.buildResponse(
				communitiesMemberInviteService.fetchMemberInvite(memberInvite), StatusType.SUCCESS,
				HttpStatus.OK.value(), "MemberInvite are fetched successfully.");
		return new ResponseEntity<>(response, HttpStatus.valueOf(Integer.parseInt(response.getCode())));
	}

}
