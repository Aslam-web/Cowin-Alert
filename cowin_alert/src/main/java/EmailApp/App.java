package EmailApp;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import EmailApp.reader.SqlReader;
import EmailApp.reader.TextFileReader;
import EmailApp.sender.BulkEmailSender;
import EmailApp.sender.ExcelWriter;

public class App {

	public static void main(String... args) throws Exception{
		
		// 1. Read from a file/database
//		Set<Patient> patientDetails = new SqlReader().read("SmartTouch");					// from SQL database
		Set<Patient> patientDetails = new TextFileReader().read("data/Patient Details.txt");// from text file
		
		// 2. Create Excel for all the patients
		List<Patient> patientList = ExcelWriter.createExcelForAll(patientDetails);
		
		// 3. Send an email to each patient with the corresponding details
		BulkEmailSender sender = new BulkEmailSender();
		sender.setThreadCount(4);
		sender.sendMail(patientList);

		
		// for printing the data that has been sent
		ExcelWriter.print(patientDetails);
		
	}
}
