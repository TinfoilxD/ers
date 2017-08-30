package com.revature.gs.ers.controller;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.revature.gs.ers.beans.Reimbursement;
import com.revature.gs.ers.beans.ReimbursementStatus;
import com.revature.gs.ers.beans.ReimbursementType;
import com.revature.gs.ers.beans.User;
import com.revature.gs.ers.data_service.DataService;
import com.revature.gs.ers.exceptions.NoRecordFoundException;
import com.revature.gs.ers.json.JSONHelper;

public class ReimbursementController {
	private static final Logger logger = Logger.getLogger(ReimbursementController.class);

	// acts as the get-all reimbursements function and filter by status function
	// depending
	// on the presence of the reimbursement status variable in the request
	// no longer used by main application but still works
	@Deprecated
	public void getReimbursementsByStatus(HttpServletRequest req, HttpServletResponse resp) {
		User currentUser = (User) req.getSession().getAttribute("loggedInUser");

		// checks if user is logged in or user has insufficient permissions
		// is hard-coded to finance manager because we don't have a permission
		// level
		if (currentUser == null || !currentUser.getUserRole().getUserRole().equals("Finance Manager")) {
			resp.setStatus(403);
		} else {
			try {
				Map<String, Object> data = JSONHelper.getMapFromRequest(req);
				logger.debug(data);

				// status recreated from params to avoid database call
				// its also small enough that doesn't need an objectmapper
				// instantiation

				int statusId = (Integer) data.get("id");
				String statusString = (String) data.get("status");
				ReimbursementStatus status = new ReimbursementStatus(statusId, statusString);

				Set<Reimbursement> reims = DataService.getReimbursementsByStatus(status);
				new ObjectMapper().writeValue(resp.getWriter(), reims);
			} catch (IOException e) {
				// IOException is probably thrown if status is not selected.
				// In this case, return all reimbursements
				logger.debug(e);
				getAllReimbursements(req, resp);
			} catch (SQLException e) {
				logger.debug(e);
				resp.setStatus(500);
			} catch (NoRecordFoundException e) {
				logger.debug(e);
				;
				resp.setStatus(500);
			}
		}
	}

	/**
	 * Private method for getReimbursementByStatus that returns all
	 * reimbursements when status is null used for initial unfiltered page
	 * request
	 * 
	 * @param req
	 * @param resp
	 */
	public void getAllReimbursements(HttpServletRequest req, HttpServletResponse resp) {
		try {
			Set<Reimbursement> reims = DataService.getAllReimbursements();
			new ObjectMapper().writeValue(resp.getWriter(), reims);
		} catch (NoRecordFoundException e) {
			logger.debug(e);
			resp.setStatus(500);
		} catch (JsonGenerationException e) {
			logger.debug(e);
			resp.setStatus(500);
		} catch (JsonMappingException e) {
			logger.debug(e);
			resp.setStatus(500);
		} catch (IOException e) {
			logger.debug(e);
			resp.setStatus(500);
		} catch (SQLException e) {
			logger.debug(e);
			resp.setStatus(500);
		}

	}

	public void getReimbursementsForEmployee(HttpServletRequest req, HttpServletResponse resp) {

		User currentUser = (User) req.getSession().getAttribute("loggedInUser");
		if (currentUser == null) {
			resp.setStatus(401);
			return;
		}

		try {
			Set<Reimbursement> reims = DataService.getReimbursementsForEmployee(currentUser);
			new ObjectMapper().writeValue(resp.getWriter(), reims);
			resp.setStatus(200);
		} catch (NoRecordFoundException e) {
			// do nothing return nothing
			// just means the user has no records yet
		} catch (SQLException e) {
			logger.debug(e);
		} catch (JsonGenerationException e) {
			logger.debug(e);
		} catch (JsonMappingException e) {
			logger.debug(e);
		} catch (IOException e) {
			logger.debug(e);
		}

	}

	public void getAllReimbursementTypes(HttpServletRequest req, HttpServletResponse resp) {

		try {
			Set<ReimbursementType> reimTypes = DataService.getAllReimbursementTypes();
			new ObjectMapper().writeValue(resp.getWriter(), reimTypes);
			resp.setStatus(200);
		} catch (SQLException e) {
			logger.debug(e);
			;
			resp.setStatus(500);
		} catch (JsonGenerationException e) {
			logger.debug(e);
			resp.setStatus(500);
		} catch (JsonMappingException e) {
			logger.debug(e);
			resp.setStatus(500);
		} catch (IOException e) {
			logger.debug(e);
			resp.setStatus(500);
		}

	}

	public void createReimbursement(HttpServletRequest req, HttpServletResponse resp) {

		User user = (User) req.getSession().getAttribute("loggedInUser");
		logger.debug(user);
		if (user == null || !user.getUserRole().getUserRole().equals("Employee")) {
			// if user isn't logged in or not employee, return 403 forbidden
			resp.setStatus(403);
			logger.debug("not logged in");
		} else {

			try {
				Map<String, Object> data = JSONHelper.getMapFromRequest(req);
				Reimbursement reim = createReimbursementObject(data, req, resp, user);
				DataService.insertReimbursement(reim);
				logger.debug(data);
				resp.setStatus(200);
			} catch (IOException e) {
				logger.debug(e);
				resp.setStatus(500);
			} catch (SQLException e) {
				logger.debug("message", e);
				resp.setStatus(500);
			}
		}
	}

	/**
	 * private method that returns a Reimbursement object for an insert request
	 * takes info from multiple sources has dangerous and unsafe casting
	 * 
	 * @param data
	 * @param req
	 * @param resp
	 * @param user
	 */
	private Reimbursement createReimbursementObject(Map<String, Object> data, HttpServletRequest req,
			HttpServletResponse resp, User user) {

		Reimbursement reim = new Reimbursement();
		reim.setAmount(Double.valueOf((String) data.get("amount")));
		// user should be currently logged in user
		reim.setAuthor(user);
		reim.setDescription((String) data.get("description"));
		reim.setSubmitted(LocalDateTime.now());

		// mapping 2nd-level json object to proper type
		// type is reinstantiated from params to avoid another database call
		Map typeObj = (Map) data.get("type"); // can possibly be null
		ReimbursementType type = new ReimbursementType();

		Integer typeIdInt = (Integer) typeObj.get("id");
		// can return null pointer exception
		// but doesn't need to be handled

		int typeId = Integer.valueOf(typeIdInt);
		type.setId(typeId);
		type.setType((String) typeObj.get("type"));
		reim.setType(type);

		// reim is still missing status at this point
		// to avoid extra database calls, it will be defaulted in the database.
		logger.debug(reim);
		return reim;
	}

	public void getAllReimbursementStauses(HttpServletRequest req, HttpServletResponse resp) {
		try {
			Set<ReimbursementStatus> reimStatuses = DataService.getAllReimbursementStatuses();
			new ObjectMapper().writeValue(resp.getWriter(), reimStatuses);
			resp.setStatus(200);
		} catch (SQLException e) {
			logger.debug(e);
			;
			resp.setStatus(500);
		} catch (JsonGenerationException e) {
			logger.debug(e);
			resp.setStatus(500);
		} catch (JsonMappingException e) {
			logger.debug(e);
			resp.setStatus(500);
		} catch (IOException e) {
			logger.debug(e);
			resp.setStatus(500);
		}

	}

	public void processReimbursement(HttpServletRequest req, HttpServletResponse resp) {

		User user = (User) req.getSession().getAttribute("loggedInUser");
		logger.debug(user);
		if (user == null || !user.getUserRole().getUserRole().equals("Finance Manager")) {
			// if user isn't logged in or not employee, return 403 forbidden
			resp.setStatus(403);
			logger.debug("not logged in as finance manager");
		} else {
			try {
				InputStream requestBody = req.getInputStream();
				logger.debug(requestBody);
				ObjectMapper mapper = new ObjectMapper();
				mapper.registerModule(new JavaTimeModule());
				Reimbursement reim = mapper.readValue(requestBody, Reimbursement.class);
				logger.debug("processed reim: " + reim);
				DataService.processReimbursement(reim, reim.getStatus(), user);
				resp.setStatus(200);
			} catch (SQLException e) {
				logger.debug(e);
				resp.setStatus(500);
			} catch (JsonParseException e) {
				logger.debug(e);
				resp.setStatus(500);
			} catch (JsonMappingException e) {
				logger.debug("message", e);
				resp.setStatus(500);
			} catch (IOException e) {
				logger.debug(e);
				resp.setStatus(500);
			} catch (NoRecordFoundException e) {
				logger.debug(e);
				resp.setStatus(500);
			}
		}

	}

}
