package EmailApp;

public class MessageBody {

	public static String getMessageBody(Patient patient) {
		String message = "Dear Mr. " + patient.getName() + ",\n"
				+ "\tGreetings to you. we hope you are at the best of your health. "
				+ "\nCongratulations for getting your first dose of vaccine at the " + patient.getVacCenter()
				+ " at " + patient.getTime()+" and the type of vaccine is "+patient.getVacName()
				+"\n\tThank you for everything you do to keep our residents safe and healthy. Getting a COVID-19 "
				+ "vaccine is an important step to prevent getting sick with COVID-19 disease. We care about your health"
				
				+ "\n\n\nThanks & Regards"
				+ "\nHelth and Family Welfare Department,"
				+ "\nGovernment of India,"
				+ "\nWebsite : https://www.india.gov.in/"
				+ "\nEmail : indiaportal@gov.in,"
				+ "\nPhone: 011-23063024, 011-23063513, 011-23061661";
		
		return message;
	}
}
