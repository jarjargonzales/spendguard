package com.spendguard.application.service;

import com.spendguard.domain.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationService {

    public void notifyApprover(User approver, String message) {
        // Simulación de envío de notificación (en producción podría ser email, push, etc.)
        log.info("Notificación para {}: {}", approver.getEmail(), message);
    }
}