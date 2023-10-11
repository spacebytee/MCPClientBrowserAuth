package dev.upio.mcpauth.utils.http;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    URI uri;
    String method;
    Map<String, String> headers = new HashMap<>();
    String body;

    public HttpRequest(URI uri, String method) {
        this.uri = uri;
        this.method = method;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {
        HttpRequest request;

        public Builder() {
            request = new HttpRequest(null, "GET");  // Default method is GET
        }

        public Builder uri(URI uri) {
            request.uri = uri;
            return this;
        }

        public Builder header(String name, String value) {
            request.headers.put(name, value);
            return this;
        }

        public Builder GET() {
            request.method = "GET";
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
    
    public static class BodyPublishers {
        public static BodyPublisher ofString(String body) {
            return new BodyPublisher(body);
        }
    }
}
