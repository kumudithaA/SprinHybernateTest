package student_enrollment.service;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import student_enrollment.dao.StudentEnrollmentDao;
import student_enrollment.model.Student;
import student_enrollment.model.StudentEnrollment;

@Service
public class StudentEnrollmentSeviceImpl implements StudentEnrollmentSevice {

	StudentEnrollmentDao studentEnrollmentDao;

	@Autowired
	public void setStudentEnrollmentDao(StudentEnrollmentDao studentEnrollmentDao) {
		this.studentEnrollmentDao = studentEnrollmentDao;
	}

	public String enrollStudent(StudentEnrollment studentEnrollment) {
		boolean availabile = studentEnrollmentDao.checkAavailability(studentEnrollment.getStuId(),
				studentEnrollment.getSubId());
		if (!availabile) {

			int enrollmentsPerDay = studentEnrollmentDao.enrollmentsPerDay(studentEnrollment.getStuId(),
					studentEnrollment.getEnrolledDate());
			if (enrollmentsPerDay <= 3) {
				studentEnrollmentDao.saveOrUpdate(studentEnrollment);
				return "You have successfully enrolled";
			}

			if (sendEmail(studentEnrollment.getStuId())) {
				return "Please check your email";
			} else {
				return "cannot send email";
			}

		} else {

			return "You have already enrolled to this Subject";
		}
	}

	protected boolean sendEmail(Student stuId) {
		if (studentEnrollmentDao.getStudentEmail(stuId) != null) {
			String to = "sonoojaiswal1988@gmail.com";// change accordingly
			String from = "sonoojaiswal1987@gmail.com";// change accordingly
			String host = "localhost";// or IP address

			// Get the session object
			Properties properties = System.getProperties();
			properties.setProperty("mail.smtp.host", host);
			Session session = Session.getDefaultInstance(properties);

			// compose the message
			try {
				MimeMessage message = new MimeMessage(session);
				message.setFrom(new InternetAddress(from));
				message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
				message.setSubject("Ping");
				message.setText("Hello, this is example of sending email  ");

				// Send message
				Transport.send(message);
				System.out.println("message sent successfully....");
				return true;

			} catch (MessagingException mex) {
				mex.printStackTrace();
				return false;
			}
		} else {
			return false;
		}
	}

}
