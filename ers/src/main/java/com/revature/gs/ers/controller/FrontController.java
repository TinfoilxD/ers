package com.revature.gs.ers.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

public class FrontController extends HttpServlet {

	private static final Logger logger = Logger.getLogger(FrontController.class);
	private AuthenticationController authenticationController;
	private ReimbursementController reimbursementController;

	@Override
	public void init() throws ServletException {
		authenticationController = new AuthenticationController();
		reimbursementController = new ReimbursementController();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doDispatch(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doDispatch(req, resp);
	}

	private void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		String requestURI = req.getRequestURI();
		logger.trace(requestURI);
		switch (requestURI) { // requires JAVA 7
		case "/ers/authentication/login": {
			authenticationController.login(req, resp);
			break;
		}
		case "/ers/content/getPastTickets": {
			reimbursementController.getReimbursementsForEmployee(req, resp);
			break;
		}
		case "/ers/content/getReimbursementTypes": {
			reimbursementController.getAllReimbursementTypes(req, resp);
			break;
		}
		case "/ers/content/createReimbursement": {
			reimbursementController.createReimbursement(req, resp);
			break;
		}
		case "/ers/content/getReimbursementStatus":{
			reimbursementController.getAllReimbursementStauses(req, resp);
			break;
		}
		case "/ers/content/getAllReimbursements":{
			reimbursementController.getAllReimbursements(req, resp);
			break;
		}
		case "/ers/content/processReimbursement":{
			reimbursementController.processReimbursement(req,resp);
			break;
		}
		default: {
			resp.setStatus(404);
		}
		}
	}

}
