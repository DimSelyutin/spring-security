package by.plamya.project.config;

import org.eclipse.angus.mail.util.MailSSLSocketFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.security.GeneralSecurityException;
import java.util.Properties;

@Configuration
public class MailConfiguration {

    @Value("${spring.mail.username}")
    private String emailUser;

    @Value("${spring.mail.password}")
    private String password;

    @Bean
    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.mail.ru");
        mailSender.setPort(465);

        mailSender.setUsername(emailUser);
        mailSender.setPassword(password);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.debug", "true");
        props.put("mail.smtp.starttls.enable", "false");
        props.put("mail.smtp.ssl.enable", "false");

        return mailSender;
    }
}