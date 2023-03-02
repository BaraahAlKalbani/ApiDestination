package org.ApiDestination;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

/**
 * the Main Application
 */
public class Main {
    /**
     * Main class for accessing the Google Maps API for distance matrix
     * @param args not used
     */
    public static void main(String[] args) {
        System.out.print("Enter origin point in term (place name,country name): ");
        // Read the origin input from the application arguments
        String origin = args[0];
        System.out.print("Enter destination point in term (place name,country name): ");
        // Read the destination input from the application arguments
        String destination = args[1];
        try{
            // Build the URL for the Google Maps Distance Matrix API
            HttpUrl.Builder urlBuilder = HttpUrl.parse("https://maps.googleapis.com/maps/api/distancematrix/json").newBuilder();
            urlBuilder.addQueryParameter("origins", origin);
            urlBuilder.addQueryParameter("destinations", destination);
            urlBuilder.addQueryParameter("units", "imperial");
            urlBuilder.addQueryParameter("key", "API_KEY");

            // Create an OkHttpClient object to handle the API request
            OkHttpClient client = new OkHttpClient().newBuilder().build();

            // Create a request object
            Request request = new Request.Builder()
                    .url(urlBuilder.build())
                    .get()
                    .build();

            // Call the `saveResponse` method to handle the API response
            DistanceMatrix distanceMatrix = saveResponse(request,client);

            // Print the duration of the trip
            System.out.println("Duration: "+distanceMatrix.rows[0].elements[0].duration.text);
        } catch (Exception e) {
            if (e instanceof JsonSyntaxException) {
                System.out.println("The API response is not in the expected format. Please try again later.");
            } else {
                System.out.println("An error occurred. Error message: " + e.getMessage());
            }
        }
    }
    /**
     * Method for saving the response of the API call to a JSON file
     * @param request the request object for the API call
     * @param client the OkHttpClient object for executing the API call
     * @return the DistanceMatrix object obtained from the API call
     */
    public static DistanceMatrix saveResponse(Request request,OkHttpClient client)
    {
        // The name of the file to store the API response
        String fileName = "Data/distance_matrix.json";
        try (Response response = client.newCall(request).execute()) {
            // Get the JSON response from the API
            String json = response.body().string();
            // Create a Gson object to parse the JSON
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            // Convert the JSON to a `DistanceMatrix` object
            DistanceMatrix distanceMatrix = gson.fromJson(json, DistanceMatrix.class);

            // Write the `DistanceMatrix` object to a file
            try (FileWriter writer = new FileWriter(fileName)) {
                gson.toJson(distanceMatrix, writer);
            }
            // Return the `DistanceMatrix` object
            return distanceMatrix;

        } catch (IOException e) {
            // Throw a runtime exception in case of an Error
            throw new RuntimeException(e);
        }

    }
}