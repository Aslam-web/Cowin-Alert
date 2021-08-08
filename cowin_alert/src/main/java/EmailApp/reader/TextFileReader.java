package EmailApp.reader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import EmailApp.Patient;

public class TextFileReader extends MyDataReader<String> {

	@Override
	public Set<Patient> read(String filename) {
		Set<Patient> patientSet = new HashSet<>();
		
		System.out.print("Reading data from Text file ...");

		try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
			
			Patient patient;
			for (String line; (line = br.readLine()) != null;) {
				String[] det = line.split("\t\t");
				
				patient = new Patient();
				patient.setEmail(det[0]);
				patient.setName(det[1]);
				patient.setAddress(det[2]);
				patient.setVacCenter((det[3]));
				patient.setTime(det[4]);
				patient.setVacName((det[5]));
				patientSet.add(patient);
				
			}

			try {Thread.sleep(1000);} catch (Exception e1) {}		// dummy 
			System.out.println("\tFILE READ SUCCESSFULLY !!!");

		} catch (IOException e) {
			System.out.println("\tERROR READING DATABASE !!!");
			e.printStackTrace();
		}

		return patientSet;
	}
}
