package com.katros.autolinkbn.services;

import com.katros.autolinkbn.entities.DeviceToken;
import com.katros.autolinkbn.exceptions.NotFoundException;
import com.katros.autolinkbn.repositories.DeviceTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;

@Service
public class NotificationService {

    @Value("${firebase.server.key}")
    private String firebaseServerKey;

    private final DeviceTokenRepository tokenRepository;

    public NotificationService(DeviceTokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    public void registerDeviceToken(String userId, String token) {
        DeviceToken deviceToken = tokenRepository.findByUserId(userId)
                .orElse(new DeviceToken());

        deviceToken.setUserId(userId);
        deviceToken.setToken(token);
        deviceToken.setUpdatedAt(LocalDateTime.now());

        tokenRepository.save(deviceToken);
    }

    public void sendNotificationToUser(String userId, String title, String body) {
        DeviceToken deviceToken = tokenRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Device token not found"));

        sendFCM(deviceToken.getToken(), title, body);
    }

    private void sendFCM(String token, String title, String body) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://fcm.googleapis.com/fcm/send"))
                    .header("Authorization", "key=" + firebaseServerKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(buildPayload(token, title, body)))
                    .build();

            client.send(request, HttpResponse.BodyHandlers.ofString());

        } catch (Exception e) {
            throw new RuntimeException("Notification send failed", e);
        }
    }

    private String buildPayload(String token, String title, String body) {
        return """
            {
              "to": "%s",
              "notification": {
                "title": "%s",
                "body": "%s"
              }
            }
            """.formatted(token, title, body);
    }

    public void sendPushNotificationToCarOwner(String ownerId, String message) {
        sendNotificationToUser(ownerId, "Car Booking Request", message);
    }

    public void sendPushNotificationToRenter(String renterId, String message) {
        sendNotificationToUser(renterId, "Booking Approved", message);
    }
}

