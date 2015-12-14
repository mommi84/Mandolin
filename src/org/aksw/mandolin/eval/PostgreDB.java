package org.aksw.mandolin.eval;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 *
 */
public class PostgreDB {

	private static Logger lgr = Logger.getLogger(PostgreDB.class.getName());
	private Connection con = null;
	private Statement st = null;

	public PostgreDB() {
		super();
	}

	public void connect() {

		String url = "jdbc:postgresql://localhost/probkb";
		String user = "tom";
		String password = "";

		try {
			con = DriverManager.getConnection(url, user, password);
			st = con.createStatement();

		} catch (SQLException ex) {
			lgr.log(Level.SEVERE, ex.getMessage(), ex);
		}
	}

	/**
	 * A factor graph is composed by factors connected with one, two, or three
	 * clauses (i.e., relationships).
	 * 
	 * @param n size of the restriction, i.e. number of clauses (1, 2, 3).
	 * @return
	 */
	public ResultSet factors(int n) {

		ResultSet factors = null;

		try {

			switch (n) {
			case 1:
				// one...
				factors = st
						.executeQuery("select rs1.rel as r1, rs1.ent1 as a1, rs1.ent2 as b1, "
								+ "f.weight from probkb.relationships as rs1, probkb.factors as f "
								+ "where f.id1 = rs1.id and f.id2 is null and f.id3 is null;");
			case 2:
				// two...
				factors = st
						.executeQuery("select rs1.rel as r1, rs1.ent1 as a1, rs1.ent2 as b1, "
								+ "rs2.rel as r2, rs2.ent1 as a2, rs2.ent2 as b2, "
								+ "f.weight from probkb.relationships as rs1, "
								+ "probkb.relationships as rs2, probkb.factors as f "
								+ "where f.id1 = rs1.id and f.id2 = rs2.id and f.id3 is null;");
			case 3:
				// three...
				factors = st
						.executeQuery("select rs1.rel as r1, rs1.ent1 as a1, rs1.ent2 as b1, "
								+ "rs2.rel as r2, rs2.ent1 as a2, rs2.ent2 as b2, "
								+ "rs3.rel as r3, rs3.ent1 as a3, rs3.ent2 as b3, "
								+ "f.weight from probkb.relationships as rs1, "
								+ "probkb.relationships as rs2, probkb.relationships as rs3, "
								+ "probkb.factors as f "
								+ "where f.id1 = rs1.id and f.id2 = rs2.id and f.id3 = rs3.id;");
			}
		} catch (SQLException ex) {
			lgr.log(Level.WARNING, ex.getMessage(), ex);
		}

		return factors;

	}

	public void close() {
		try {

			if (st != null) {
				st.close();
			}
			if (con != null) {
				con.close();
			}

		} catch (SQLException ex) {
			lgr.log(Level.WARNING, ex.getMessage(), ex);
		}
	}

	public ResultSet evidence() {
		// TODO Auto-generated method stub
		return null;
	}

}
