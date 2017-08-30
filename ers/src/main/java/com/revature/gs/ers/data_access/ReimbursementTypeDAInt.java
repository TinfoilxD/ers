package com.revature.gs.ers.data_access;

import java.sql.SQLException;
import java.util.Set;

import com.revature.gs.ers.beans.ReimbursementType;
import com.revature.gs.ers.exceptions.NoRecordFoundException;

public interface ReimbursementTypeDAInt {
	Set<ReimbursementType> getAllTypes() throws SQLException;
	ReimbursementType getTypeFromId(int id) throws SQLException, NoRecordFoundException;
	ReimbursementType getLodging() throws SQLException;
	ReimbursementType getTravel() throws SQLException;
	ReimbursementType getFood() throws SQLException;
	ReimbursementType getOther() throws SQLException;
}
