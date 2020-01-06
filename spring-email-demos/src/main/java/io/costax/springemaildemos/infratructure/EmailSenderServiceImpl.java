package io.costax.springemaildemos.infratructure;

import io.costax.springemaildemos.domain.notifications.EmailSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
public class EmailSenderServiceImpl implements EmailSenderService {

    @Autowired
    JavaMailSender mailSender;

    @Override
    public void send(final Message message) {

        try {
            final MimeMessage mimeMessage = mailSender.createMimeMessage();

            final MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);

            mimeMessageHelper.setSubject(message.getSubject());
            mimeMessageHelper.setText(message.getBody(), true);
            mimeMessageHelper.setTo(message.getTos().toArray(new String[0]));
            mimeMessageHelper.setFrom(message.getFrom());

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new EmailSenderException("Some Expected problem sending email", e);
        }

    }
}
