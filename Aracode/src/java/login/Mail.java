package login;

import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 *
 * @author Vavra, Millington (viz. dokumentace)
 */
public class Mail {
    
    private static final String username = "aracodebot@seznam.cz";
    private static final String password = "AraCode2345*";
    private static final String smtpServer = "smtp.seznam.cz";

    /**
     * Sends e-mail with token to recepient
     * @param recepient email adress of recepient
     * @param token ResetToken string
     */
    public static void sendEmail(String recepient, String token) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", smtpServer);
        props.put("mail.smtp.port", "587");
//        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.ssl.trust", "*");
        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(recepient));
            message.setSubject("Password change");
            message.setText("Your password change token:\n http://localhost:8080/Login/changePassword.jsp?token=" + token + "\nEnter this to change password form");
            Transport.send(message);
        } catch (MessagingException e) {
           e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Mail.sendEmail("redmineteam@seznam.cz", "d");
    }
}
