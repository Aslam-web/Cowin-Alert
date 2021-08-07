package EmailApp;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import EmailApp.reader.SqlReader;
import EmailApp.reader.TextFileReader;

public class App {

	public static void main(String... args) throws IOException{
		
		Set<Patient> patientDetails = new SqlReader().read("SmartTouch");
//		Set<Patient> patientDetails = new TextFileReader().read("data/Patient Details.txt");
		List<Patient> patientList = ExcelWriter.createExcelForAll(patientDetails);
		
//		BulkEmail bulkEmail = new BulkEmail();
//		bulkEmail.setThreadCount(5);
//		bulkEmail.sendMail(patientList);

		
		
		System.out.println("\n-----------------Printing Data-----------------------\n");
		Iterator<Patient> iter = patientDetails.iterator();
		while(iter.hasNext()) {
			Patient p = iter.next();
			System.out.printf("%s\t\t%s\t\t\t%s\t\t\t%s\t\t%s\t\t\t%s\n",
					p.getEmail(),p.getName(),p.getAddress(),p.getVacCenter(),p.getTime(),p.getVacName(),p.getExcelFile());
		}
	}
}
