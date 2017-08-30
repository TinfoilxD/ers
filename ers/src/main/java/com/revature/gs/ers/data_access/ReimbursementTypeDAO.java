package com.revature.gs.ers.data_access;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import com.revature.gs.ers.beans.ReimbursementType;
import com.revature.gs.ers.exceptions.NoRecordFoundException;

public class ReimbursementTypeDAO implements ReimbursementTypeDAInt {

	private static final int LODGING = 1;
	private static final int TRAVEL = 2;
	private static final int FOOD = 3;
	private static final int OTHER = 4;

	private Connection conn;

	public ReimbursementTypeDAO(Connection conn) {
		super();
		this.conn = conn;
	}

	public Set<ReimbursementType> getAllTypes() throws SQLException {
		Set<ReimbursementType> types = new HashSet<>();
		String sql = "SELECT * FROM ers_reimbursement_type";

		PreparedStatement stmt = conn.prepareStatement(sql);
		ResultSet rs = stmt.executeQuery();
		while (rs.next()) {
			ReimbursementType newType = new ReimbursementType();
			newType.setId(rs.getInt("reimb_type_id"));
			newType.setType(rs.getString("reimb_type"));
			types.add(newType);
		}

		return types;
	}

	public ReimbursementType getTypeFromId(int id) throws SQLException, NoRecordFoundException {
		
		if(id <= 0)
			throw new IllegalArgumentException();
		ReimbursementType type = new ReimbursementType();

		String sql = "SELECT reimb_type FROM ers_reimbursement_type " + "WHERE reimb_type_id = ?";

		PreparedStatement stmt = conn.prepareStatement(sql);
		stmt.setInt(1, id);

		ResultSet rs = stmt.executeQuery();

		if (rs.next()) {
			type.setId(id);
			type.setType(rs.getString("reimb_type"));
		} else {
			throw new NoRecordFoundException(
					String.format("Type for corresponding id %d does not exist", id));
		}

		return type;
	}

	@Override
	public ReimbursementType getLodging() throws SQLException {
		try {
			return getTypeFromId(ReimbursementTypeDAO.LODGING);
		} catch (NoRecordFoundException e) {
			e.printStackTrace();
			throw new SQLException("Lodging type cannot be found in database.");
		}
	}

	@Override
	public ReimbursementType getTravel() throws SQLException {
		try {
			return getTypeFromId(ReimbursementTypeDAO.TRAVEL);
		} catch (NoRecordFoundException e) {
			e.printStackTrace();
			throw new SQLException("Travel type cannot be found in database.");
		}
	}

	@Override
	public ReimbursementType getFood() throws SQLException {
		try {
			return getTypeFromId(ReimbursementTypeDAO.FOOD);
		} catch (NoRecordFoundException e) {
			e.printStackTrace();
			throw new SQLException("Food type cannot be found in database.");
		}
	}

	@Override
	public ReimbursementType getOther() throws SQLException {
		try {
			return getTypeFromId(ReimbursementTypeDAO.OTHER);
		} catch (NoRecordFoundException e) {
			e.printStackTrace();
			throw new SQLException("Other type cannot be found in database.");
		}
	}
}
