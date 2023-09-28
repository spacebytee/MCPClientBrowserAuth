package com.bytespacegames.mcpauth;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

public class Authentication {
    private static final String CLIENT_ID = "e5785f8b-29f1-477a-b758-d4c15b565c9e";
    private static final String CLIENT_SECRET = "HIDDEN FOR SECURITY PURPOSES";
    private static final String REDIRECT_URI = "http://localhost:6921/microsoft/complete";

    public Authentication() {
    }
    
    public String retrieveAccessToken(String mscode, String recentPkce) {
    	try {
    		// Create a HttpClient to send a POST request to the token endpoint
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("https://login.live.com/oauth20_token.srf"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(
                    "client_id=" + CLIENT_ID +
                    "&code=" + mscode +
                    "&scope=Xboxlive.signin+Xboxlive.offline_access" +
                    "&code_verifier=" + recentPkce +
                    "&redirect_uri=http://localhost:6921/microsoft/complete" +
                    "&grant_type=authorization_code" +
                    "&client_secret=" + CLIENT_SECRET))
                .build();

            // Send the request and get the response
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JsonReader jsonReader = Json.createReader(new StringReader(response.body()));
            JsonObject jsonObject = jsonReader.readObject();
            jsonReader.close();
            
            String accessTokenToLive = jsonObject.getString("access_token");
            
            // Create a HttpClient to send a POST request to the xbox live endpoint
            client = HttpClient.newHttpClient();
            request = HttpRequest.newBuilder()
                .uri(new URI("https://user.auth.xboxlive.com/user/authenticate"))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(
                    "{\"Properties\":{\"AuthMethod\":\"RPS\",\"SiteName\":\"user.auth.xboxlive.com\",\"RpsTicket\":\"d=" + accessTokenToLive + "\"},\"RelyingParty\":\"http://auth.xboxlive.com\",\"TokenType\":\"JWT\"}"))
                .build();

            // Send the request and get the response
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            jsonReader = Json.createReader(new StringReader(response.body()));
            jsonObject = jsonReader.readObject();
            jsonReader.close();

            String xblAuthToken = jsonObject.getString("Token");
            
            
            String userHashString = jsonObject
            		.getJsonObject("DisplayClaims")
            	    .getJsonArray("xui")
            	    .getJsonObject(0)
            	    .getString("uhs");

            // Create a HttpClient to send a POST request to the xbox live endpoint (https://xsts.auth.xboxlive.com/xsts/authorize) to get the xsts token
            client = HttpClient.newHttpClient();
            request = HttpRequest.newBuilder()
                .uri(new URI("https://xsts.auth.xboxlive.com/xsts/authorize"))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(
                    "{\"Properties\":{\"SandboxId\":\"RETAIL\",\"UserTokens\":[\"" + xblAuthToken + "\"]},\"RelyingParty\":\"rp://api.minecraftservices.com/\",\"TokenType\":\"JWT\"}"))
                .build();
            
            // Send the request and get the response
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            jsonReader = Json.createReader(new StringReader(response.body()));
            jsonObject = jsonReader.readObject();
            jsonReader.close();

            String xstsToken = jsonObject.getString("Token");
            System.out.println(xstsToken);

            // Create a HttpClient to send a POST request to the xbox live endpoint (https://api.minecraftservices.com/authentication/login_with_xbox) to get the minecraft access token
            client = HttpClient.newHttpClient();
            request = HttpRequest.newBuilder()
                .uri(new URI("https://api.minecraftservices.com/authentication/login_with_xbox"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(
                    "{\"identityToken\":\"XBL3.0 x=" + userHashString + ";" + xstsToken + "\",\"ensureLegacyEnabled\":\"true\"}"))
                .build();
            
            // Send the request and get the response
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.body());
            // Parse JSON response
            jsonReader = Json.createReader(new StringReader(response.body()));
            jsonObject = jsonReader.readObject();
            jsonReader.close();

            String minecraftAccessToken = jsonObject.getString("access_token");
            return minecraftAccessToken;
    	} catch (Exception e) {
    		return e.getMessage();
    	}
    }
    
    public static JsonObject getAccountInfo(String accessToken){

        // send GET request to the https://api.minecraftservices.com/minecraft/profile endpoint with this in headers: {"Authorization": "Bearer " + accessToken}
        // if the response code is 200, parse the response body as JSON and return the "name" and "id" fields
        // if the response code is not 200, return "balls"

        // Create a HttpClient to send a POST request to the token endpoint
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("https://api.minecraftservices.com/minecraft/profile"))
            .header("Authorization", "Bearer " + accessToken)
            .GET()
            .build();
        
        // Send the request and get the response
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.body());
            JsonReader jsonReader = Json.createReader(new StringReader(response.body()));
            JsonObject jsonObject = jsonReader.readObject();
            jsonReader.close();

            return jsonObject;
        } catch (Exception e) {
            return null;
        }
    }
}
