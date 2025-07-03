package com.letmecall.rgt.rest.helper;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.letmecall.rgt.domain.ApiDetails;
import com.letmecall.service.ApiDetailsServcie;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ApiDetailsHelper {

	@Autowired
	private ApiDetailsServcie apiDetailsServcie;

	public ApiDetails createApiDetails(ApiDetails apiDetails) {
		return apiDetailsServcie.createApiDetails(apiDetails);
	}

	public ApiDetails updateApiDetails(ApiDetails apiDetails) {
		return apiDetailsServcie.updateApiDetails(apiDetails);
	}

	public ApiDetails findByApiDetails(Long apiDetailsId) {
		ApiDetails apiDetails = new ApiDetails();
		apiDetails.setApiDetailsId(apiDetailsId);
		return apiDetailsServcie.findByApiDetails(apiDetails);
	}

	public List<ApiDetails> findAllApiDetails(Integer pageNumber, Integer limit) {
		ApiDetails apiDetails=new ApiDetails();
		apiDetails.setPageNumber(pageNumber);
		apiDetails.setPageSize(limit);
		return apiDetailsServcie.findAllApiDetails(apiDetails);
	}

	public ApiDetails deleteApiDetails(ApiDetails apiDetails) {
		return apiDetailsServcie.deleteApiDetails(apiDetails);
	}

}
