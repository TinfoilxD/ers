package com.revature.gs.ers.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revature.gs.ers.beans.User;
import com.revature.gs.ers.data_service.DataService;
import com.revature.gs.ers.exceptions.NoRecordFoundException;
import com.revature.gs.ers.json.JSONHelper;

public class AuthenticationController {

	private static final Logger logger = Logger.getLogger(AuthenticationController.class);

	public void login(HttpServletRequest req, HttpServletResponse resp) {

		try {
			
			//get back JSON params
			Map<String,Object> data = JSONHelper.getMapFromRequest(req);
			logger.debug(data);

			String username = (String) data.get("username");
			String password = (String) data.get("password");
			logger.debug(username);
			logger.debug(password);
			User user = DataService.retrieveUserForLogin(username, password);
			
			//return user information except password
			new ObjectMapper().writeValue(resp.getWriter(), user);
			
			//set response to okay and store user in session
			req.getSession().setAttribute("loggedInUser", user);
			resp.setStatus(200); //acts as return statement for XHR request

		} catch (SQLException e) {
			logger.debug("message", e);
			resp.setStatus(500);
		} catch (NoRecordFoundException e) {
			logger.debug("message", e);
			resp.setStatus(401);
		} catch (IOException e) {
			logger.debug("message", e);
			resp.setStatus(500);
		}
	}

}
