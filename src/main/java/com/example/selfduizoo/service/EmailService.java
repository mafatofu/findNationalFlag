package com.example.selfduizoo.service;

import com.example.selfduizoo.dto.ReqSendEmailAuthenticationApiV1DTO;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.UnsupportedEncodingException;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmailService {
    private final TemplateEngine templateEngine;
    private final JavaMailSender javaMailSender;
    public Boolean sendEmailAuthentication(ReqSendEmailAuthenticationApiV1DTO reqEmailAuthenticationApiV1DTO, String authenticationCode) {
        // 메시지 객체를 생성하고
        MimeMessage message = javaMailSender.createMimeMessage();

        try {
            // 이메일 제목 설정
            message.setSubject("사이트 회원가입 인증번호 입니다.");

            // 이메일 수신자 설정
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(reqEmailAuthenticationApiV1DTO.getEmail(), "", "UTF-8"));

            // 이메일 내용 설정
            message.setText(setContext(authenticationCode), "UTF-8", "html");

            // 송신
            //TODO 여기서 오류남
            /*
            * Failed messages: org.eclipse.angus.mail.smtp.SMTPSendFailedException: 554 5.7.1 The sender address is unauthorized I3Qs3It4SjGwWlfZVe7EyA - nsmtp
            * */
            javaMailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return false;
        }
        // 다 성공했다면
        return true;
    }

    // 생성해놓은 html에 인증 코드를 넣어서 반환
    private String setContext(String authenticationCode) {
        Context context = new Context();
        context.setVariable("authenticationCode", authenticationCode);
        return templateEngine.process("email-authentication", context);
    }
}