package com.revature.gs.ers.json;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JSONHelper {

	private static final Logger logger = Logger.getLogger(JSONHelper.class);

	
	/**
	 * Returns a list of values in a one-level JSON object as a map.
	 * @param HttpServletRequest object from XMLHttpRequest
	 * @return
	 * @throws IOException
	 */
	public static Map<String,Object> getMapFromRequest(HttpServletRequest req) throws IOException {

		InputStream httpRequestBody = req.getInputStream();
		logger.debug(httpRequestBody);
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> map = new HashMap<String, Object>();

		// convert JSON string to Map
		map = mapper.readValue(httpRequestBody, new TypeReference<Map<String, Object>>(){});
		return map;
		
	}
	

}


