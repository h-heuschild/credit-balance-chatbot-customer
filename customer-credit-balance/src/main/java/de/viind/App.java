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
    private static String GRAPHQL_URI = "https://sdp-sandbox-billing.cluster01.viind.io/graphql";

    public static void main(String[] args) {

        if (args.length == 0) {
            System.out.println(
                    "Bitte geben sie einen der beiden Modus ein: 'Creditabfrage' oder 'Crediterhoehung_Monat' mit der passenden Kundennummer dazu.");
        } else if (args[0].equals("Creditabfrage")) {
            processCreditabfrage(Integer.parseInt(args[1]));
        } else if (args[0].equals("Crediterhoehung_Monat")) {
            processCrediterhoehungMonat(Integer.parseInt(args[1]), Integer.parseInt(args[2]));
        } else {
            System.out.println(
                    "Bitte geben sie einen der beiden Modus ein: 'Creditabfrage' oder 'Crediterhoehung_Monat' mit der passenden Kundennummer dazu.");
        }
    }

    private static void processCrediterhoehungMonat(Integer customerId, Integer newMonthlyCredit) {
        HttpClient httpClient = HttpClient.newHttpClient();
        String mutationString = String.format(
                "{\"query\":\"mutation{ setBillingPlan(setBillingInput:{ " +
                        "customerId:\\\"%d\\\"" +
                        "monthlyCredits: %d" +
                        "}){" +
                        "monthlyCredits " +
                        "remainingCredits " +
                        "additionalCredits }}\"}",
                customerId,
                newMonthlyCredit);
        try {
            URI uri = new URIBuilder(GRAPHQL_URI)
                    .build();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .header(AUTH_HEADER, BEARER_TOKEN)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(mutationString))
                    .build();

            HttpResponse<String> httpResponse = httpClient.send(request, BodyHandlers.ofString());
            String ausgabeText = String.format(
                    "Dem Kunden %d stehen jetzt monatlich %d Credits zur Verfügung.",
                    customerId,
                    getValueOfIntegerVariable(httpResponse.body(), "monthlyCredits"));
            System.out.println(ausgabeText);
        } catch (URISyntaxException | IOException | InterruptedException e) {
            System.out.println("Es ist ein Problem aufgetreten: " + e.getMessage());
        }
    }

    private static void processCreditabfrage(Integer customerId) {
        HttpClient httpClient = HttpClient.newHttpClient();

        try {
            URI uri = new URIBuilder(GRAPHQL_URI)
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
                    getValueOfIntegerVariable(httpResponse.body(), "remainingCredits"),
                    getValueOfIntegerVariable(httpResponse.body(), "monthlyCredits"));
            System.out.println(ausgabeText);
        } catch (URISyntaxException | IOException | InterruptedException e) {
            System.out.println("Es ist ein Problem aufgetreten: " + e.getMessage());
        }
    }

    private static int getValueOfIntegerVariable(String queryString, String valueToFind) {
        int startIndexOfVariableKeyToFind = queryString.indexOf(valueToFind);
        // Hier wird +1 addiert um den Index ei Zeichen nach dem Doppelpunkt zu bekommen
        int startIndexOfVariableValueToFind = queryString.indexOf(":", startIndexOfVariableKeyToFind) + 1;
        int endIndexOfVariableValueToFind = getEndIndexOfVariableValueToFind(queryString,
                startIndexOfVariableValueToFind);
        int valueOfIntegerVariable = Integer
                .parseInt(queryString.substring(startIndexOfVariableValueToFind, endIndexOfVariableValueToFind));
        return valueOfIntegerVariable;
    }

    private static int getEndIndexOfVariableValueToFind(String queryString, int startIndexOfVariableValueToFind) {
        return queryString.indexOf(",", startIndexOfVariableValueToFind) != -1
                ? queryString.indexOf(",", startIndexOfVariableValueToFind)
                : queryString.indexOf("}", startIndexOfVariableValueToFind);
    }
}
