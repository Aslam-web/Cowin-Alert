package EmailApp.reader;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import EmailApp.Patient;

public class SqlReader extends MyDataReader<String> {

	Connection con;
	String database;
	ResultSet rs;

	@Override
	public Set<Patient> read(String dataSource) {
		this.database = dataSource;

		Set<Patient> patientsDetail = new HashSet<>();

		con = sqlconnection();
		rs = fetch(con);

		try {
			Patient p;
			while (rs.next()) {
				p = new Patient();
				p.setId(rs.getString("ID"));
				p.setEmail(rs.getString("email"));
				p.setName(rs.getString("person's_Name"));
				p.setAddress(rs.getString("person's_Address"));
				p.setVacCenter(rs.getString("Vaccination_Center"));
				p.setTime(rs.getString("Vaccination_Date"));
				p.setVacName(rs.getString("Vaccine_name"));
				patientsDetail.add(p);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return patientsDetail;
	}

	private Connection sqlconnection() {

		try {

			Properties props = new Properties();
			props.load(new FileInputStream("database.properties"));

			String url = props.getProperty("LOCAL_DB") + "databaseName=" + this.database;
			String username = props.getProperty("DATABASE_USER");
			String password = props.getProperty("DATABASE_PASS");

			System.out.print("CONECTING TO DATABASE...");
			con = DriverManager.getConnection(url, username, password);
			System.out.println("\tCONNECTED SUCCESSFULLY");
			return con;
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Connection Failed");
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Unable to read database properties");
		}

		return con;

	}

	private ResultSet fetch(Connection connection) {

		con = connection;
		String Procedure = "Registered_Cowin_Data";
		try {
			CallableStatement cs = con.prepareCall(Procedure);
			rs = cs.executeQuery();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return rs;
	}
}