package dev.upio.tidalwave.utils.auth;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

/**
 * This class represents a simple web server for handling authentication requests.
 */
public class WebServer {

    /**
     * The HTTP server instance.
     */
    public static HttpServer server;

    /**
     * Initializes the web server and starts listening on a specific port.
     */
    public static void initWebServer() {
        try {
            server = HttpServer.create(new InetSocketAddress(6921), 0);
            server.createContext("/microsoft/complete", new MyHandler());
            System.out.println("Server started at port 6921");
            server.start();
        } catch (Exception e) {
            System.out.println("An internal error occurred. Sorry!");
        }
    }

    /**
     * Handles HTTP requests and responds with appropriate messages.
     */
    public static class MyHandler implements HttpHandler {

        /**
         * Handles an HTTP request and sends a response based on the query parameters.
         *
         * @param t The HTTP exchange object.
         * @throws IOException If an I/O error occurs during request handling.
         */
        @Override
        public void handle(HttpExchange t) throws IOException {
            String code = ""; // get the parameter "code" in a GET request somehow
            String query = t.getRequestURI().getQuery();
            Map<String, String> queryParams = parseQueryParameters(query);

            String successMsg = "Attempted to log in to Minecraft. Please return to the game.";
            String error = "An error occurred during authentication.";
            byte[] bytes;
            if (queryParams.containsKey("code")) {
                code = queryParams.get("code");
                t.sendResponseHeaders(200, successMsg.length());
                bytes = successMsg.getBytes();

                // You can change this to change the status text inside your auth GUI
                // GuiLogin.login.lastResult = SessionUtils.recieveResponse(code);
                SessionUtils.recieveResponse(code);
            } else {
                t.sendResponseHeaders(418, error.length());
                bytes = error.getBytes();
            }

            OutputStream os = t.getResponseBody();
            os.write(bytes);
            os.close();
            server.stop(0);
        }

        /**
         * Parses query parameters from the URL-encoded query string.
         *
         * @param query The URL-encoded query string.
         * @return A map containing query parameters and their values.
         * @throws UnsupportedEncodingException If UTF-8 encoding is not supported.
         */
        private Map<String, String> parseQueryParameters(String query) throws UnsupportedEncodingException {
            Map<String, String> queryParams = new HashMap<>();

            if (query != null) {
                String[] pairs = query.split("&");
                for (String pair : pairs) {
                    String[] keyValue = pair.split("=");
                    if (keyValue.length == 2) {
                        String key = URLDecoder.decode(keyValue[0], "UTF-8");
                        String value = URLDecoder.decode(keyValue[1], "UTF-8");
                        queryParams.put(key, value);
                    }
                }
            }

            return queryParams;
        }
    }
}
