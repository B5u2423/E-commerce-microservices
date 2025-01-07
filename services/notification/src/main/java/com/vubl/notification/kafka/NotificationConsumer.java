package com.vubl.notification.kafka;

import com.vubl.notification.entity.Notification;
import com.vubl.notification.entity.NotificationType;
import com.vubl.notification.model.OrderConfirmation;
import com.vubl.notification.model.PaymentConfirmation;
import com.vubl.notification.repository.NotificationRepository;
import com.vubl.notification.service.EmailService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationConsumer {
    private final NotificationRepository repository;
    private final EmailService service;
    private final EmailService emailService;

    @KafkaListener(topics = "payment-topic")
    public void consumePaymentSuccessNotification(PaymentConfirmation paymentConfirmation)
            throws MessagingException {
        log.info("Consuming message from topic 'payment-topic': {} ", paymentConfirmation);
        repository.save(
                Notification.builder()
                        .type(NotificationType.PAYMENT_CONFIRMATION)
                        .timestamp(LocalDateTime.now())
                        .paymentConfirmation(paymentConfirmation)
                        .build()
        );

        // Send email
        var customerName = paymentConfirmation.customerFirstName() + " "
                + paymentConfirmation.customerLastName();
        emailService.sendPaymentSuccessEmail(
                paymentConfirmation.customerEmail(),
                customerName,
                paymentConfirmation.amount(),
                paymentConfirmation.orderReference()
        );
    }

    @KafkaListener(topics = "order-topic")
    public void consumeOrderConfirmationNotification(OrderConfirmation orderConfirmation)
            throws MessagingException {
        log.info("Consuming message from topic 'order-topic': {} ", orderConfirmation);
        repository.save(
                Notification.builder()
                        .type(NotificationType.ORDER_CONFIRMATION)
                        .timestamp(LocalDateTime.now())
                        .orderConfirmation(orderConfirmation)
                        .build()
        );

        // todo send email
        var customerName = orderConfirmation.customer().firstName() + " "
                + orderConfirmation.customer().lastName();
        emailService.sendOrderConfirmationEmail(
                orderConfirmation.customer().email(),
                customerName,
                orderConfirmation.totalAmount(),
                orderConfirmation.orderReference(),
                orderConfirmation.products()
        );
    }
}
