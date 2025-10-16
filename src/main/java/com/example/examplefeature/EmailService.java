package com.example.examplefeature;

import jakarta.mail.*;
import jakarta.mail.internet.*;

import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

public class EmailService {

    // Dados da tua conta Gmail de teste
    private static final String USERNAME = "exemploengsoftware@gmail.com";
    private static final String PASSWORD = "lqhn qdqp grci zcmh";
    public static void sendTaskListEmail(String recipient, List<Task> tarefas) {
        String subject = "Lista de Tarefas";
        String body = tarefas.stream()
                .map(t -> "- " + t.getDescription() +
                        (t.getDueDate() != null ? " (Due: " + t.getDueDate() + ")" : ""))
                .collect(Collectors.joining("\n"));

        sendEmail(recipient, subject, body);
    }

    public static void sendEmail(String recipient, String subject, String body) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true"); // TLS
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(USERNAME, PASSWORD);
            }
        });
        session.setDebug(true);

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(USERNAME));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
            message.setSubject(subject);
            message.setText(body);

            Transport.send(message);
            System.out.println("Email enviado com sucesso para: " + recipient);

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}


