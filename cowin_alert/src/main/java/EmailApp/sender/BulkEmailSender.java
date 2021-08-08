package EmailApp.sender;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import EmailApp.Patient;

public class BulkEmailSender {

	private Properties props;
	private Session session;
	private List<Patient> patients;

	private int threadCount = 1;
	private int count;
	private int failedMessages;
	private String status;

	public BulkEmailSender() {
		this.props = new Properties();
	}

	// can explicitly set the thread count (default = 1)
	public void setThreadCount(int threadCount) {
		this.threadCount = (threadCount <= 0) ? 1 : (threadCount > 20) ? 20 : threadCount;
	}

	// setRecipients()
	public void sendMail(List<Patient> patients) throws Exception {
		this.patients = patients;

		// loads the neccessary properties to connect to the smtp server from file
		props.load(new FileInputStream("config.properties"));

		session = Session.getInstance(props, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(props.getProperty("EMAIL"), props.getProperty("PASSWORD"));
			}
		});

		// starts the process using the given amount of threads
		startThreadOperation();

	}

	private void startThreadOperation() throws InterruptedException {

		// initializing thread count and details displayed
		System.out.print("Total no.of Patients: " + this.patients.size());
		System.out.println(",\tThread Count : " + this.threadCount);
		System.out.println("----------------------------------------------\nPreparing the messages ...\n");

		// process starts
		long startTime = System.currentTimeMillis();

		Thread[] threads = new Thread[threadCount];

		for (int i = 0; i < threads.length; i++) {
			threads[i] = new Thread(new MyRunnable());
			threads[i].start();
		}

		for (Thread thread : threads) {
			thread.join();
		}

		// prints the results of the progress
		printResults(startTime);
	}

	private void printResults(long startTime) {

		this.status = (count == 0) ? "NOT OK" : // no recipients to send - NOT OK
				(patients.size() == count) ? "SUCCESS" : // sent to all recipients - SUCCESS
						(this.status != null) ? this.status : // failed to send for some recipients - OK
								"ERROR"; // none of these - ERROR

		System.out.println("----------------------------------------------\n");
		System.out.printf(
				"Total recipients: %s\nSent : %s,\tFailed : %s,\tStatus : %s\nTime taken : %ds\nNo.Of Threads used : %d\n",
				patients.size(), count - failedMessages, failedMessages, status,
				(System.currentTimeMillis() - startTime) / 1000, threadCount);
	}

	// Creates [i.e via createMessage()] and sends message
	private void send(Patient patient) {

		try {

			Message message = createMessage(patient);
			Transport.send(message);
			System.out.printf("Message successfully sent to <%s>\n", patient.getEmail());
		} catch (Exception e) {
			System.out.printf("Failed to send message to : <%s>\t\t\t", patient.getEmail());
			System.out.println("Problem : " + e.getMessage());
			failedMessages++;
			this.status = "OK"; // sets the status to OK if atleast 1 failed msg
		}
	}

	// Constructs the message
	private Message createMessage(Patient patient) {

		Message message = new MimeMessage(session);
		try {
			message.setFrom(new InternetAddress(props.getProperty("EMAIL")));
			message.setRecipient(Message.RecipientType.TO, new InternetAddress(patient.getEmail()));
			message.setSubject("Recogniztion for getting the Covid-19 vaccine");

			Multipart multipart = new MimeMultipart();

			// text part
			MimeBodyPart text1 = new MimeBodyPart();
			text1.setText(MessageBody.getMessageBody(patient));

			// attachment
			MimeBodyPart file = new MimeBodyPart();
			file.attachFile(patient.getExcelFile());

			multipart.addBodyPart(text1);
			multipart.addBodyPart(file);
			message.setContent(multipart);

			return message;
		} catch (Exception e) {
			System.out.printf("SOME ERROR OCCURED IN CREATING EMAIL FOR %s!!!", patient.getEmail());
			e.printStackTrace();
		}

		return message;
	}

	// The job allocated for the threads
	private class MyRunnable implements Runnable {

		@Override
		public void run() {
			while (count < patients.size()) {

				int localCount = 0;
				synchronized (this) {
					localCount = count;
					++count;
				}
				send(patients.get(localCount));
			}
		}
	}
}