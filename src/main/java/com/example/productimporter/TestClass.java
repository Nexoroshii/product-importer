package com.example.productimporter;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class TestClass {
    public static void main(String[] args) throws Exception {

        var client = HttpClient.newHttpClient();

        var request = HttpRequest.newBuilder()
            .uri(URI.create(
                "https://flowerforce.nl/pictures/X11039242_H_1.jpg"
            ))
            .GET()
            .build();

        var response =
            client.send(
                request,
                HttpResponse.BodyHandlers.ofByteArray()
            );

        System.out.println(
            response.statusCode()
        );
    }
}
