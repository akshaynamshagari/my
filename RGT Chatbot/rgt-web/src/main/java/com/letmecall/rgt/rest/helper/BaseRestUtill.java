package com.letmecall.rgt.rest.helper;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;

public class BaseRestUtill {

	public static String getServerPath(HttpServletRequest request) {
		String path = null;
		String contextPath = request.getServletPath();
		String requestURL = request.getRequestURL().toString();
		int indexOf = StringUtils.indexOf(requestURL, contextPath);
		if (indexOf != -1) {
			path = StringUtils.substring(requestURL, 0, indexOf);
		}
		return path;
	}

	public static String getClientIpAddress(HttpServletRequest request) {
		String clientIPAddress = request.getHeader("X-FORWARDED-FOR");
		if (clientIPAddress == null) {
			clientIPAddress = request.getRemoteAddr();
		}
		return clientIPAddress;
	}

	public static String decode(String uts) {
		String decoded = null;
		try {
			decoded = URLDecoder.decode(uts, "UTF-8");
			decoded = decoded.replace(" ", "+");
			decoded = decoded.replace(" ", "+");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return decoded;
	}

	public static boolean checkFileType(MultipartFile excelfile) {
		boolean status = false;
		if (excelfile != null && excelfile.getOriginalFilename() != null) {
			status = fileTypeExcel(excelfile);
		}
		return status;

	}

	public static boolean fileTypeExcel(MultipartFile excelfile) {
		return (excelfile.getOriginalFilename() != null && (excelfile.getOriginalFilename().endsWith(".xlsx")
				|| excelfile.getOriginalFilename().endsWith(".xls")));
	}

}
