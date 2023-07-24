package de.viind;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

import org.apache.http.client.utils.URIBuilder;

public class App {

    private static String AUTH_HEADER = "Authorization";
    private static String BEARER_TOKEN = "Bearer pLdY3ulLws0XboEe5Z7BNyyYWSNrGt3a";

    public static void main(String[] args) {

        if (args.length == 0) {
            System.out.println(
                    "Bitte geben sie einen der beiden Modus ein: 'Creditabfrage' oder 'Crediterhoehung' mit der passenden Kundennummer dazu.");
        } else if (args[0].equals("Creditabfrage")) {
            processCreditabfrage(Integer.parseInt(args[1]));
        } else if (args[0].equals("Crediterhoehung")) {
            processCrediterhoehung(Integer.parseInt(args[1]), Integer.parseInt(args[2]));
        } else {
            System.out.println(
                    "Bitte geben sie einen der beiden Modus ein: 'Creditabfrage' oder 'Crediterhoehung' mit der passenden Kundennummer dazu.");
        }
    }

    private static void processCrediterhoehung(Integer customerId, Integer valueToRaiseCredit) {
    }

    private static void processCreditabfrage(Integer customerId) {
        HttpClient httpClient = HttpClient.newHttpClient();

        try {
            URI uri = new URIBuilder("https://sdp-sandbox-billing.cluster01.viind.io/graphql")
                    .addParameter("query", "{billing(customerId:\"1234\"){monthlyCredits remainingCredits}}")
                    .build();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .header(AUTH_HEADER, BEARER_TOKEN)
                    .GET()
                    .build();

            HttpResponse<String> httpResponse = httpClient.send(request, BodyHandlers.ofString());
            String ausgabeText = String.format(
                    "Der Kunde %d hat noch %d verfuegbare Credits von insgesamt %d monatlichen Credits",
                    customerId,
                    getRemainingBillingCredits(httpResponse.body()),
                    getMonthlyBillingCredits(httpResponse.body()));
            System.out.println(ausgabeText);
        } catch (URISyntaxException | IOException | InterruptedException e) {
            System.out.println("Es ist ein Problem aufgetreten: " + e.getMessage());
        }
    }

    private static int getMonthlyBillingCredits(String queryString) {
        String variableString = "monthlyCredits";
        int startIndexOfMonthlyCredits = queryString.indexOf(variableString) + variableString.length() + 2;
        int endIndexOfMonthlyCredits = queryString.indexOf(",");
        int monthlyBillingCredits = Integer
                .parseInt(queryString.substring(startIndexOfMonthlyCredits, endIndexOfMonthlyCredits));
        return monthlyBillingCredits;
    }

    private static int getRemainingBillingCredits(String queryString) {
        String variableString = "remainingCredits";
        int startIndexOfMonthlyCredits = queryString.indexOf(variableString) + variableString.length() + 2;
        int endIndexOfMonthlyCredits = queryString.indexOf("}");
        int monthlyBillingCredits = Integer
                .parseInt(queryString.substring(startIndexOfMonthlyCredits, endIndexOfMonthlyCredits));
        return monthlyBillingCredits;
    }
}
