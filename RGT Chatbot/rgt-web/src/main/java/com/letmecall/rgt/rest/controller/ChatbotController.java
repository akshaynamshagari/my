package com.letmecall.rgt.rest.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.letmecall.rgt.aspect.ApiMetrics;
import com.letmecall.rgt.aspect.CalLog;
import com.letmecall.rgt.chatbot.model.EntityRequest;
import com.letmecall.rgt.chatbot.model.Intent;
import com.letmecall.rgt.chatbot.model.IntentRequest;
import com.letmecall.rgt.chatbot.model.Request;
import com.letmecall.rgt.chatbot.services.IntentService;
import com.letmecall.rgt.model.Response;
import com.letmecall.rgt.model.StatusType;

@RestController
@RequestMapping(value = RgtResource.CHATBOT_BASE_URL)
public class ChatbotController {

	private static final Logger logger = LoggerFactory.getLogger(ChatbotController.class);

	@Autowired
	private IntentService intentService;

	@PostMapping(RgtResource.CHAT)
	@ApiMetrics(methodName = "chatting")
	@CalLog
	public ResponseEntity<Response<String>> chatting(@RequestBody @Validated Request request) throws Exception {
		logger.debug("chatting:INVOKED");
		String intentRecognize = null;
		Response<String> response = null;
		try {
			intentRecognize = intentService.intentRecognize(request);
			if (intentRecognize != null) {
				response = Response.buildResponse(intentRecognize, StatusType.SUCCESS, HttpStatus.OK.value(),
						"chat is successfully.");
			} else {
				response = Response.buildResponse(intentRecognize, StatusType.ERROR, HttpStatus.INTERNAL_SERVER_ERROR.value(),
						"chat has failure.");
			}
		} catch (Exception e) {
			logger.error("Exception Occured while executing chat", e);
			response = Response.buildResponse(null, StatusType.ERROR, HttpStatus.INTERNAL_SERVER_ERROR.value(),
					e.getMessage());

		}
		return new ResponseEntity<>(response, HttpStatus.valueOf(Integer.parseInt(response.getCode())));
	}

	@PostMapping(RgtResource.ADD_INTENT)
	@ApiMetrics(methodName = "addIntent")
	@CalLog
	public ResponseEntity<Response<String>> addIntent(@RequestBody @Validated IntentRequest intentRequest) {
		logger.debug("addIntent:INVOKED");
		Response<String> response = null;
		String intent = null;
		try {
			intent = intentService.addIntent(intentRequest);
			if (intent != null) {
				response = Response.buildResponse(intent, StatusType.SUCCESS, HttpStatus.OK.value(),
						"Intent are added successfully.");
			} else {
				response = Response.buildResponse(intent, StatusType.ERROR, HttpStatus.INTERNAL_SERVER_ERROR.value(),
						"Intent are not added..");
			}
		} catch (Exception e) {
			logger.error("Exception Occured while executing intent", e);
			response = Response.buildResponse(null, StatusType.ERROR, HttpStatus.INTERNAL_SERVER_ERROR.value(),
					e.getMessage());

		}
		return new ResponseEntity<>(response, HttpStatus.valueOf(Integer.parseInt(response.getCode())));
	}

	@PutMapping(RgtResource.UPDATE_INTENT)
	@ApiMetrics(methodName = "updateIntent")
	@CalLog
	public ResponseEntity<Response<String>> updateIntent(@RequestBody @Validated IntentRequest intentRequest) {
		logger.debug("updateIntent:INVOKED");
		Response<String> response = null;
		String intent = null;
		try {
			intent = intentService.updateIntent(intentRequest);
			if (intent != null) {
				response = Response.buildResponse(intent, StatusType.SUCCESS, HttpStatus.OK.value(),
						"Intent are updated successfully.");
			} else {
				response = Response.buildResponse(intent, StatusType.ERROR, HttpStatus.INTERNAL_SERVER_ERROR.value(),
						"Intent are not updated..");
			}
		} catch (Exception e) {
			logger.error("Exception Occured while executing intent", e);
			response = Response.buildResponse(null, StatusType.ERROR, HttpStatus.INTERNAL_SERVER_ERROR.value(),
					e.getMessage());

		}
		return new ResponseEntity<>(response, HttpStatus.valueOf(Integer.parseInt(response.getCode())));
	}

	@DeleteMapping(RgtResource.DELETE_INTENT)
	@ApiMetrics(methodName = "deleteIntent")
	@CalLog
	public ResponseEntity<Response<String>> deleteIntent(@RequestBody IntentRequest intentRequest) {
		logger.debug("deleteIntent:INVOKED");
		Response<String> response = null;
		String intent = null;
		try {
			intent = intentService.deleteIntent(intentRequest.getIntentName());
			if (intent != null) {
				response = Response.buildResponse(intent, StatusType.SUCCESS, HttpStatus.OK.value(),
						"Intent are deleted successfully.");
			} else {
				response = Response.buildResponse(intent, StatusType.ERROR, HttpStatus.INTERNAL_SERVER_ERROR.value(),
						"Intent are not deleted..");
			}
		} catch (Exception e) {
			logger.error("Exception Occured while executing intent", e);
			response = Response.buildResponse(null, StatusType.ERROR, HttpStatus.INTERNAL_SERVER_ERROR.value(),
					e.getMessage());

		}
		return new ResponseEntity<>(response, HttpStatus.valueOf(Integer.parseInt(response.getCode())));
	}

	@GetMapping(RgtResource.FETCH_INTENT)
	@ApiMetrics(methodName = "getIntent")
	@CalLog
	public ResponseEntity<Response<List<Intent>>> getIntent() {
		logger.debug("getIntent:INVOKED");
		Response<List<Intent>> response = null;
		List<Intent> intent = null;
		try {
			intent =  intentService.getAllIntents();
			if (intent != null) {
				response = Response.buildResponse(intent, StatusType.SUCCESS, HttpStatus.OK.value(),
						"Intent are fetched successfully.");
			} else {
				response = Response.buildResponse(intent, StatusType.ERROR, HttpStatus.INTERNAL_SERVER_ERROR.value(),
						"Intent are not fetched..");
			}
		} catch (Exception e) {
			logger.error("Exception Occured while executing intent", e);
			response = Response.buildResponse(null, StatusType.ERROR, HttpStatus.INTERNAL_SERVER_ERROR.value(),
					e.getMessage());

		}
		return new ResponseEntity<>(response, HttpStatus.valueOf(Integer.parseInt(response.getCode())));
	}

	@PostMapping(RgtResource.ADD_ENTITIES)
	@ApiMetrics(methodName = "addEntites")
	@CalLog
	public ResponseEntity<Response<String>> addEntites(@RequestBody @Validated EntityRequest entityRequest) {
		logger.debug("addEntites:INVOKED");
		Response<String> response = null;
		String entity = null;
		try {
			entity =  intentService.addEntites(entityRequest);
			if (entity != null) {
				response = Response.buildResponse(entity, StatusType.SUCCESS, HttpStatus.OK.value(),
						"Entities are added successfully.");
			} else {
				response = Response.buildResponse(entity, StatusType.ERROR, HttpStatus.INTERNAL_SERVER_ERROR.value(),
						"Entities are not added..");
			}
		} catch (Exception e) {
			logger.error("Exception Occured while executing Entities", e);
			response = Response.buildResponse(null, StatusType.ERROR, HttpStatus.INTERNAL_SERVER_ERROR.value(),
					e.getMessage());

		}
		return new ResponseEntity<>(response, HttpStatus.valueOf(Integer.parseInt(response.getCode())));
	}

	@GetMapping(RgtResource.GET_ENTITIES)
	@ApiMetrics(methodName = "getEntites")
	@CalLog
	public ResponseEntity<Response<EntityRequest>> getEntites() {
		logger.debug("getEntites:INVOKED");
		Response<EntityRequest> response = null;
		EntityRequest entity = null;
		try {
			entity =  intentService.getEntites();
			if (entity != null) {
				response = Response.buildResponse(entity, StatusType.SUCCESS, HttpStatus.OK.value(),
						"Entities are fetched successfully.");
			} else {
				response = Response.buildResponse(entity, StatusType.ERROR, HttpStatus.INTERNAL_SERVER_ERROR.value(),
						"Entities are not fetched..");
			}
		} catch (Exception e) {
			logger.error("Exception Occured while executing Entities", e);
			response = Response.buildResponse(null, StatusType.ERROR, HttpStatus.INTERNAL_SERVER_ERROR.value(),
					e.getMessage());

		}
		return new ResponseEntity<>(response, HttpStatus.valueOf(Integer.parseInt(response.getCode())));
	}

}
