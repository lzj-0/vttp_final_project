package com.recipes.FlavourFoundry.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/api/crypto")
public class CryptoRestController {

    @Value("${gecko.api.key}")
    private String apiKey;

    private String baseUrl = "https://api.coingecko.com/api/v3";

    @GetMapping("/price")
    public ResponseEntity<String> getPriceOfCoin(@RequestParam String coin, @RequestParam String currency) {

        RestTemplate template = new RestTemplate();

        String url = UriComponentsBuilder.fromUriString(baseUrl + "/simple/price")
                                            .queryParam("ids", coin)
                                            .queryParam("vs_currencies", currency)
                                            .queryParam("x_cg_demo_api_key", apiKey)
                                            .toUriString();

        ResponseEntity<String> resp = template.getForEntity(url, String.class);

        if (resp.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.ok(resp.getBody());
        } else {
            return ResponseEntity.status(resp.getStatusCode()).body("Fail to fetch price");
        }

    }
}
