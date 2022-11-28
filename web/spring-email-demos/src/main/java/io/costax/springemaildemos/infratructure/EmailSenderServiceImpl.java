package io.costax.springemaildemos.infratructure;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import io.costax.springemaildemos.domain.notifications.EmailSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.UncheckedIOException;

@Service
public class EmailSenderServiceImpl implements EmailSenderService {

    @Autowired
    JavaMailSender mailSender;

    @Autowired
    Configuration freemarkerConfig;

    @Override
    public void send(final Message message) {
        try {

            final MimeMessage mimeMessage = mailSender.createMimeMessage();

            final MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);

            mimeMessageHelper.setSubject(message.getSubject());
            mimeMessageHelper.setText(buildBody(message), true);
            mimeMessageHelper.setTo(message.getTos().toArray(new String[0]));
            mimeMessageHelper.setFrom(message.getFrom());

            mailSender.send(mimeMessage);
        } catch (MessagingException | TemplateException e) {
            throw new EmailSenderException("Some Expected problem sending email", e);
        }

    }

    private String buildBody(final Message message) throws TemplateException {
        try {
            if (message.getTemplateName() == null) {
                return message.getBody();
            }

            final Template template = freemarkerConfig.getTemplate(message.getTemplateName());

            return FreeMarkerTemplateUtils
                    .processTemplateIntoString(template, message.getTemplateParams());

        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
