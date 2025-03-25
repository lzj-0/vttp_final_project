package com.recipes.FlavourFoundry.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;

import com.recipes.FlavourFoundry.service.UserService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;

@RestController
@RequestMapping("/api/payment")
public class StripeController {

    @Autowired
    UserService userService;

    @Value("${stripe.webhook.secret}")
    private String webhookSecret;

    @Value("${endpoint.url}")
    private String baseUrl;

    @PostMapping("/checkout")
    public ResponseEntity<Map<String, Object>> checkout(@RequestParam String userId) throws StripeException {
        SessionCreateParams params = SessionCreateParams.builder()
                        .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                        .setMode(SessionCreateParams.Mode.PAYMENT)
                        .setSuccessUrl(baseUrl + "/subscription/success")
                        .setCancelUrl(baseUrl + "/subscription/failure")
                        .putMetadata("userId", userId)
                        .addLineItem(
                            SessionCreateParams.LineItem.builder()
                                .setPriceData(
                                    SessionCreateParams.LineItem.PriceData.builder()
                                        .setCurrency("SGD")
                                        .setUnitAmount(999l)
                                        .setProductData(
                                            SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                .setName("Flavour Foundary Premium Subscription")
                                                .build()
                                        )
                                        .build()
                                )
                                .setQuantity(1L)
                                .build()
                        )
                        .build();
        
        Session session = Session.create(params);
        System.out.println("at checkout");
        Map<String, Object> result = new HashMap<>();
        result.put("sessionId", session.getId());
        System.out.println(session.getId());

        return ResponseEntity.ok(result);

    }

    // test with stripe CLI : stripe listen --forward-to http://localhost:8080/api/payment/webhook
    @PostMapping("/webhook")
    public ResponseEntity<String> handleStripeWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {
        
        Event event = null;
        System.out.println("detect webhook");

        try {
            // Verify the webhook signature
            event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
        } catch (SignatureVerificationException e) {
            return ResponseEntity.badRequest().body("Invalid signature");
        }

        if ("checkout.session.completed".equals(event.getType())) {
            Session session = (Session) event.getDataObjectDeserializer().getObject().orElse(null);

            if (session != null) {
                String userId = session.getMetadata().get("userId");

                if (userId != null) {
                    System.out.println("Changing premium status");
                    userService.updateUserPremiumStatus(userId, true);
                }
            }
        }

        return ResponseEntity.ok().build();
    }

}
