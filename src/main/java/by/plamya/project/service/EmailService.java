package by.plamya.project.service;

import by.plamya.project.entity.EmailDetails;

public interface EmailService {
 

    // To send a simple email
    String sendSimpleMail(EmailDetails details);
 

    // To send an email with attachment
    String sendMailWithAttachment(EmailDetails details);
}
