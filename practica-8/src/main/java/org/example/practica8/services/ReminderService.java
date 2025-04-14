package org.example.practica8.services;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ReminderService {

    private final EventService eventService;
    private final EmailService emailService;

    public ReminderService(EventService eventService, EmailService emailService) {
        this.eventService = eventService;
        this.emailService = emailService;
    }

    @Scheduled(cron = "0 0 8 * * ?")
    public void sendDailyEventReminder(){
        eventService.getEventsWithinRange(LocalDateTime.now(), LocalDateTime.now().plusDays(1))
                .forEach(event -> emailService.sendReminderFormattedEmail(
                        event.getOwner().getEmail(),
                        event
                ));
    }

    // This method was developed to test the email service
    @Scheduled(cron = "0 */5 * * * *")
    public void sendEventReminderEveryFiveMinutes(){
        eventService.getEventsWithinRange(LocalDateTime.now(), LocalDateTime.now().plusMinutes(5))
                .forEach(event -> emailService.sendReminderFormattedEmail(
                        event.getOwner().getEmail(),
                        event
                ));
    }
}
