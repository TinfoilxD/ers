package com.revature.gs.ers.data_access;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import com.revature.gs.ers.beans.Reimbursement;
import com.revature.gs.ers.beans.ReimbursementStatus;
import com.revature.gs.ers.beans.ReimbursementType;
import com.revature.gs.ers.beans.User;
import com.revature.gs.ers.connection.ConnectionManager;
import com.revature.gs.ers.exceptions.NoRecordFoundException;

public class ReimbursementDAO implements ReimbursementDAInt{
	
	private static final Logger logger = Logger.getLogger(ReimbursementDAO.class);
	private Connection conn;
	
	public ReimbursementDAO(Connection conn){
		super();
		this.conn = conn;
	}
	public void insertReimbursement(Reimbursement reim) throws SQLException{
		
		String sql = "INSERT INTO ers_reimbursement"
				+ " (reimb_id, reimb_amount, reimb_submitted,"
				+ " reimb_resolved, reimb_description, reimb_receipt,"
				+ " reimb_author, reimb_resolver, reimb_status_id, reimb_type_id)"
				+ "VALUES (?,?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement stmt = conn.prepareStatement(sql);
		int reimId = reim.getId();
		if(reimId  > 0){
			stmt.setInt(1,reim.getId());
		} else{
			stmt.setNull(1, java.sql.Types.INTEGER);;
		}
		if(reim.getAmount() > 0)
			stmt.setDouble(2, reim.getAmount());
		else{
			throw new IllegalArgumentException("Amount is less than 0");
		}
		stmt.setTimestamp(3, Timestamp.valueOf(reim.getSubmitted()));
		if(reim.getResolved() != null){
			stmt.setTimestamp(4, Timestamp.valueOf(reim.getResolved()));
		} else{
			stmt.setNull(4, java.sql.Types.TIMESTAMP);
		}
		if(reim.getDescription() != null){
			stmt.setString(5, reim.getDescription());
		} else{
			stmt.setNull(5, java.sql.Types.VARCHAR);
		}
		if(reim.getReceipt() != null){
			stmt.setBlob(6, reim.getReceipt());
		} else{
			stmt.setNull(6, java.sql.Types.BLOB);
		}
		stmt.setInt(7, reim.getAuthor().getId());
		if(reim.getResolver() != null){
			stmt.setInt(8, reim.getResolver().getId());
		} else{
			stmt.setNull(8, java.sql.Types.INTEGER);
		}
		if(reim.getStatus() != null){
			stmt.setInt(9, reim.getStatus().getId());
		} else{
			stmt.setNull(9, java.sql.Types.INTEGER);
		}
		stmt.setInt(10,  reim.getType().getId());
		stmt.executeUpdate();

	}
	/**
	 * 
	 * @param employee
	 * @return Set of Reimbursement objects related to given employee
	 * @throws SQLException
	 * @throws NoRecordFoundException 
	 */
	public Set<Reimbursement> getPastTickets(User employee) throws SQLException, NoRecordFoundException{
		
		//check input parameter to prevent misuse
		if(employee == null || employee.getId() <= 0)
			throw new IllegalArgumentException();
		
		String sql = "SELECT * FROM ers_reimbursement WHERE reimb_author = ?";
		PreparedStatement stmt = conn.prepareStatement(sql);
		stmt.setInt(1, employee.getId());
		ResultSet rs = stmt.executeQuery();
		return resultToHashSet(rs);
		
	}
	/**
	 * 
	 * @return Set of Reimbursement objects. All reimbursements with no filter.
	 * @throws SQLException
	 * @throws NoRecordFoundException 
	 */
	public Set<Reimbursement> getAllTickets() throws SQLException, NoRecordFoundException{
				
		String sql = "SELECT * FROM ers_reimbursement";
		PreparedStatement stmt = conn.prepareStatement(sql);
		ResultSet rs = stmt.executeQuery();
		return resultToHashSet(rs);
	}
	/**
	 * 
	 * @param object of type ReimbursementStatus
	 * @return Set of all Reimbursements that contain that status
	 * @throws SQLException
	 * @throws NoRecordFoundException 
	 */
	public Set<Reimbursement> getTicketsByStatus(ReimbursementStatus status) throws SQLException, NoRecordFoundException{
		
		if(status == null || status.getId() <= 0)
			throw new IllegalArgumentException();
		
		String sql = "SELECT * FROM ers_reimbursement WHERE reimb_status_id = ?";
		PreparedStatement stmt = conn.prepareStatement(sql);
		stmt.setInt(1, status.getId());
		ResultSet rs = stmt.executeQuery();
		return resultToHashSet(rs);
	}
	
	/**
	 * Sets the Reimbursement request status to boolean.
	 * Also sets resolved and resolver. 
	 * @param reim
	 * @throws SQLException 
	 */
	public void processRequest(Reimbursement reim, ReimbursementStatus status, User resolver) throws SQLException{
		if(reim == null || resolver == null)
			throw new IllegalArgumentException();
		reim.setResolver(resolver);
		reim.setResolved(LocalDateTime.now());
		
		// send update request now that its been resolved
		String sql = "UPDATE ers_reimbursement SET reimb_resolver = ?, reimb_resolved = ?, "
				+ "reimb_status_id = ? "
				+ "WHERE reimb_id = ?";
		PreparedStatement stmt = conn.prepareStatement(sql);
		stmt.setInt(1, resolver.getId());
		stmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
		stmt.setInt(3, status.getId());
		stmt.setInt(4, reim.getId());
		stmt.executeUpdate();
		

	}

	/**
	 * 
	 * @param ResultSet of Reimbursements
	 * @return HashSet of Reimbursements
	 * @throws SQLException
	 * @throws NoRecordFoundException 
	 */
	private Set<Reimbursement> resultToHashSet(ResultSet rs) throws SQLException, NoRecordFoundException{
		
		Set<Reimbursement> tickets = new HashSet<>();
		
		while(rs.next()){
			Reimbursement reim = new Reimbursement();
			reim.setId(rs.getInt("reimb_id"));
			reim.setAmount(rs.getDouble("reimb_amount"));
			reim.setSubmitted(rs.getTimestamp("reimb_submitted").toLocalDateTime());
			
			Timestamp resolved = rs.getTimestamp("reimb_resolved");
			if(resolved != null)
				reim.setResolved(resolved.toLocalDateTime());
			String description = rs.getString("reimb_description");
			if(description != null || !description.equals(""))
				reim.setDescription(description);
			//do not take in receipt blobs for now
			/*
			 * space reserved for taking receipt blobs
			 * 
			 */
			UserDAO userDAO = new UserDAO(conn);
			int authorId = rs.getInt("reimb_author");
			reim.setAuthor(userDAO.getUserFromId(authorId));
			int resolverId = rs.getInt("reimb_resolver");
			if(resolverId >= 1){ //adds a user object for resolver if an id is given
				reim.setResolver(userDAO.getUserFromId(resolverId));
			}
			ReimbursementStatusDAO statusDAO = new ReimbursementStatusDAO(conn);
			int statusId = rs.getInt("reimb_status_id");
			reim.setStatus(statusDAO.getStatusFromId(statusId));
			int typeId = rs.getInt("reimb_type_id");
			ReimbursementTypeDAO typeDAO = new ReimbursementTypeDAO(conn);
			reim.setType(typeDAO.getTypeFromId(typeId));
			
			tickets.add(reim); //all 10 members of the reimbursement class should be set
			
		}
		//throws an exception if this returns an empty hashMap
		if(tickets.size() <= 0)
			throw new NoRecordFoundException("No records were found for this query");
		return tickets;
	}
	@Override
	public Reimbursement getReimbursementById(int id) throws SQLException, NoRecordFoundException {
		
		if(id <= 0)
			throw new IllegalArgumentException();
		
		String sql = "SELECT * FROM ers_reimbursement WHERE reimb_id = ?";
		PreparedStatement stmt = conn.prepareStatement(sql);
		stmt.setInt(1, id);
		ResultSet rs = stmt.executeQuery();
		Set<Reimbursement> reims = resultToHashSet(rs);
		
		//checks for results are empty.
		if(reims.size() == 0)
			throw new NoRecordFoundException(String.format("No Reimbursement for id %d was found.",id));
		//checks to make sure set size is only 1
		if(reims.size() > 1)
			throw new SQLException("Query returned multiple records for 1 id");
		//returns first and only element of the set.
		return reims.iterator().next();
	}

}
