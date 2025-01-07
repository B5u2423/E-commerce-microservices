package com.vubl.notification.entity;

import com.vubl.notification.model.OrderConfirmation;
import com.vubl.notification.model.PaymentConfirmation;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Document
public class Notification {
    @Id
    private String id;
    private NotificationType type;
    private LocalDateTime timestamp;
    private OrderConfirmation orderConfirmation;
    private PaymentConfirmation paymentConfirmation;
}
