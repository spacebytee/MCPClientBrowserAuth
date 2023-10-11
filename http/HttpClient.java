package dev.upio.mcpauth.utils.http;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class HttpClient {

    public HttpResponse send(HttpRequest request) throws IOException {
        URL url = new URL(request.uri.toString());
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(request.method);
        for (Map.Entry<String, String> header : request.headers.entrySet()) {
            connection.setRequestProperty(header.getKey(), header.getValue());
        }
        if (request.body != null) {
            connection.setDoOutput(true);
            try (OutputStream os = connection.getOutputStream()) {
                os.write(request.body.getBytes());
            }
        }
        int responseCode = connection.getResponseCode();
        String responseBody;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            StringBuilder responseContent = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                responseContent.append(line);
                responseContent.append(System.lineSeparator());
            }
            responseBody = responseContent.toString();
        }
        return new HttpResponse(responseCode, responseBody);
    }

    public static HttpClient newHttpClient() {
        return new HttpClient();
    }
    
    public static class Builder {
        HttpRequest request;

        public Builder() {
            request = new HttpRequest(null, "GET");
        }

        public Builder uri(URI uri) {
            request.uri = uri;
            return this;
        }

        public Builder header(String name, String value) {
            request.headers.put(name, value);
            return this;
        }

        public Builder POST(BodyPublisher bodyPublisher) {
            request.method = "POST";
            request.body = bodyPublisher.getBody();
            return this;
        }

        public HttpRequest build() {
            return request;
        }
    }
    
    
}
