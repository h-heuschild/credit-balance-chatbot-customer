package de.viind;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

public class App {
    public static void main(String[] args) {

        HttpClient httpClient = HttpClient.newHttpClient();
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("https://sdp-sandbox-billing.cluster01.viind.io/graphql/"))
                    .header("key1", "value1")
                    .header("key2", "value2")
                    .GET()
                    .build();

            HttpResponse<String> httpResponse = httpClient.send(request, BodyHandlers.ofString());
            System.out.println(httpResponse.body());
        } catch (URISyntaxException | IOException | InterruptedException e) {
            System.out.println("Es ist ein Problem aufgetreten: " + e.getMessage());
        }
    }
}
