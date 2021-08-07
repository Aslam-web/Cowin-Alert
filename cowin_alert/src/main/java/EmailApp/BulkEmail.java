package EmailApp;

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

public class BulkEmail {

	private Properties props;
	private Session session;
	private List<Patient> patients;

	private int threadCount = 1;
	private int count;
	private int failedMessages;
	private String status;

	public BulkEmail() {
		this.props = new Properties();
	}

	// can explicitly set the thread count (default = 1)
	public void setThreadCount(int threadCount) {
		this.threadCount = (threadCount <= 0) ? 1 : (threadCount > 20) ? 20 : threadCount;
	}

	// setRecipients()
	public void sendMail(List<Patient> patients) throws IOException {
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

	private void startThreadOperation() {

		// initializing thread count and details displayed
		System.out.println("Total no.of recipients: " + this.patients.size());
		System.out.println("Thread Count : " + this.threadCount);
		System.out.println("Preparing the messages ...\n----------------------------------------------");

		// process starts
		long startTime = System.currentTimeMillis();

		Thread[] threads = new Thread[threadCount];

		for (int i = 0; i < threads.length; i++) {
			threads[i] = new Thread(new MyRunnable());
			threads[i].start();
		}

		for (Thread thread : threads) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
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

	// connect() is responsible for creating [i.e via createMessage()] and sending
	// message
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

	// creates the message body
	private Message createMessage(Patient patient) {

		Message message = new MimeMessage(session);
		try {
			message.setFrom(new InternetAddress(props.getProperty("EMAIL")));
			message.setRecipient(Message.RecipientType.TO, new InternetAddress(patient.getEmail()));
			message.setSubject("Recogniztion for getting the CoviShield vaccine");

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

	private class MyRunnable implements Runnable {

		@Override
		public void run() {
			for (; count < patients.size();) {

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