package vttp2023.batch4.paf.assessment.services;

import java.io.StringReader;

import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.json.Json;
import jakarta.json.JsonNumber;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

@Service
public class ForexService {

	// TODO: Task 5
	public float convert(String from, String to, float amount) {
		String url = UriComponentsBuilder.fromUriString("https://api.frankfurter.dev/v1/latest")
                .queryParam("base", from.toUpperCase())
                .queryParam("symbols", to.toUpperCase())
                .toUriString();
    System.out.printf("URL: %s\n", url);  // Debugging the URL
    RequestEntity<Void> req = RequestEntity.get(url)
                .accept(MediaType.APPLICATION_JSON)
                .build();

    try {
        RestTemplate template = new RestTemplate();
        ResponseEntity<String> resp = template.exchange(req, String.class);
        System.out.printf("API Response: %s\n", resp.getBody());  // Debugging the response body

        // Check if response body is not empty
        if (resp.getBody() == null || resp.getBody().isEmpty()) {
            System.err.println("Error: Empty response from API");
            return -1000f;
        }

        // Process the response and extract conversion rate
        try (JsonReader reader = Json.createReader(new StringReader(resp.getBody()))) {
            JsonObject jsonResponse = reader.readObject();

            // Check if rates object is present
            JsonObject rates = jsonResponse.getJsonObject("rates");
            if (rates == null) {
                System.err.println("Error: 'rates' object not found in the response");
                return -1000f;
            }

            // Fetch the conversion rate dynamically
            JsonNumber rate = rates.getJsonNumber(to.toUpperCase());
            if (rate == null) {
                System.err.println("Error: Conversion rate for " + to.toUpperCase() + " not found");
                return -1000f;  // If conversion rate is missing, return -1000
            }

            // Calculate and return the converted amount as float
            return amount * (float) rate.doubleValue();
        }
    } catch (Exception ex) {
        System.err.println("Error during API call or conversion: " + ex.getMessage());
        return -1000f;  // Return -1000 in case of any error
    }
	}
}
