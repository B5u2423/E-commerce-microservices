package com.vubl.notification.repository;

import com.vubl.notification.entity.Notification;
import com.vubl.notification.model.PaymentConfirmation;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface NotificationRepository extends MongoRepository<Notification, String> {
}
