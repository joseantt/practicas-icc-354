package org.example.practica8.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
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
    @Scheduled(cron = "0 */2 * * * *")
    public void sendEventReminderEveryFiveMinutes(){
        log.info("Sending reminder every 2 minutes");
        eventService.getEventsWithinRange(LocalDateTime.now(), LocalDateTime.now().plusMinutes(2))
                .forEach(event -> emailService.sendReminderFormattedEmail(
                        event.getOwner().getEmail(),
                        event
                ));
    }
}
