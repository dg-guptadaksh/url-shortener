package com.example.urlshortener.controller;

import com.example.urlshortener.model.UrlMapping;
import com.example.urlshortener.repository.UrlMappingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/urlshortner")
public class UrlShortenerController {

    @Autowired
    private UrlMappingRepository urlMappingRepository;

    // POST method to shorten the URL
    @PostMapping("/shorten")
    public ResponseEntity<String> shortenUrl(@RequestBody Map<String, String> requestBody) {
        String longUrl = requestBody.get("longUrl");




        // Validate URL
        if (longUrl == null || longUrl.isEmpty() || !isValidUrl(longUrl)) {
            return ResponseEntity.badRequest().body("Invalid URL provided");
        }


        // Generate short URL hash
        String shortUrl = generateShortUrl(longUrl);

        // Save the short and long URL to the database
        UrlMapping urlMapping = new UrlMapping();
        urlMapping.setLongUrl(longUrl);
        urlMapping.setShortUrl(shortUrl);
        urlMappingRepository.save(urlMapping);

        // Return the full short URL to the client
        return ResponseEntity.ok("http://localhost:8081/urlshortner/" + shortUrl);
    }

    private boolean isValidUrl(String longUrl) {
            try {
                new java.net.URL(longUrl);
                return true;
            } catch (Exception e) {
                return false;
            }
        }

    // Logic to generate a short URL (for now, using hashCode)
    private String generateShortUrl(String longUrl) {
        return String.valueOf(Math.abs(longUrl.hashCode())); // Only the hash value
    }

    // GET method to handle redirects for the short URLs
    @GetMapping("/{shortUrl}")
    public ResponseEntity<?> redirectToLongUrl(@PathVariable String shortUrl) {
        System.out.println("Received short URL: " + shortUrl);

        // Query the database for the short URL hash
        Optional<UrlMapping> urlMapping = urlMappingRepository.findByShortUrl(shortUrl);

        if (urlMapping.isPresent()) {
            String longUrl = urlMapping.get().getLongUrl();
            System.out.println("Redirecting to long URL: " + longUrl);

            // Ensure the long URL is well-formed
            if (!longUrl.startsWith("http://") && !longUrl.startsWith("https://")) {
                longUrl = "https://" + longUrl;
            }

            // Redirect to the long URL
            return ResponseEntity.status(302).header("Location", longUrl).build();
        } else {
            System.out.println("Short URL not found: " + shortUrl);
            return ResponseEntity.status(404).body("Invalid Response 404 : URL not found");
        }
    }
}
