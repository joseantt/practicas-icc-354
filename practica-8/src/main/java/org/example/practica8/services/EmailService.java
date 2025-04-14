package org.example.practica8.services;

import com.resend.services.emails.model.CreateEmailOptions;
import lombok.extern.slf4j.Slf4j;
import org.example.practica8.entities.Event;
import org.springframework.stereotype.Service;
import com.resend.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@Slf4j
public class EmailService {
    private final Resend resend;

    public EmailService() {
        this.resend = new Resend("re_WhFuApMN_KSxtFw3gNiU9zsDxrmHA2uKC");
    }

    public void sendReminderFormattedEmail(String to, Event event) {
        String html = String.format("""
            <!DOCTYPE html>
            <html lang="en">
            <head>
              <meta charset="UTF-8">
              <title>Event Reminder - TimeGrid</title>
              <style>
                body {
                  background-color: #f4f4f7;
                  margin: 0;
                  padding: 0;
                  font-family: Arial, sans-serif;
                }
                .email-container {
                  max-width: 600px;
                  margin: 40px auto;
                  background: #ffffff;
                  border-radius: 8px;
                  overflow: hidden;
                  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
                }
                .header {
                  background: #053961;
                  padding: 20px;
                  text-align: center;
                  color: white;
                }
                .header svg {
                  width: 50px;
                  height: 50px;
                  margin-bottom: 10px;
                }
                .content {
                  padding: 30px 20px;
                  text-align: center;
                }
                .content h1 {
                  font-size: 22px;
                  color: #333333;
                }
                .content p {
                  color: #555555;
                  line-height: 1.6;
                }
                .event-details {
                  background: #f0f0fa;
                  padding: 15px;
                  margin: 20px auto;
                  border-radius: 6px;
                  display: inline-block;
                  text-align: left;
                }
                .event-details p {
                  margin: 5px 0;
                }
                .footer {
                  font-size: 12px;
                  color: #999999;
                  text-align: center;
                  padding: 20px;
                }
              </style>
            </head>
            <body>
              <div class="email-container">
                <div class="header">
                  <svg xmlns="http://www.w3.org/2000/svg" fill="white" viewBox="0 0 24 24">
                    <rect width="100%%" height="100%%" rx="4" ry="4"></rect>
                  </svg>
                  <h2>Time Grid</h2>
                </div>
                <div class="content">
                  <h1>Reminder: Upcoming Event</h1>
                  <p>Dear %s, this is a friendly reminder that you have an upcoming event scheduled.</p>
                  <div class="event-details">
                    <p><strong>Event:</strong> %s</p>
                    <p><strong>Description:</strong> %s</p>
                    <p><strong>Start:</strong> %s</p>
                    <p><strong>End:</strong> %s</p>
                  </div>
                  <p>Looking forward to seeing you there!</p>
                </div>
                <div class="footer">
                  &copy; %d TimeGrid — All rights reserved.
                </div>
              </div>
            </body>
            </html>""",
                !event.getOwner().getName().isBlank() ? event.getOwner().getName() : "User",
                event.getTitle(),
                !event.getDescription().isBlank() ? event.getDescription() : "No description provided",
                formatDateTime(event.getStartDate()),
                formatDateTime(event.getEndDate()),
                LocalDateTime.now().getYear()
        );

        CreateEmailOptions params = CreateEmailOptions.builder()
                .from("noreply@joseantstudio.tech")
                .to(to)
                .subject("⏰ Time Grid Reminder — Upcoming Event Scheduled")
                .html(html)
                .build();

        try { resend.emails().send(params); }
        catch (Exception e) { log.error("Error sending email: {}", e.getMessage()); }
    }

    private String formatDateTime(LocalDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ofPattern("MMMM dd, yyyy HH:mm"));
    }
}
