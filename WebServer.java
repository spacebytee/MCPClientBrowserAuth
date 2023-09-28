package com.bytespacegames.mcpauth;

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

import com.bytespacegames.teapotclient.UI.GuiLogin;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class WebServer {
	
	public static HttpServer server;
	public static void initWebServer() {
		try {
			server = HttpServer.create(new InetSocketAddress(6921), 0);
			server.createContext("/microsoft/complete", new MyHandler());
			System.out.println("server started at 6921 port");
			server.start();
		} catch (Exception e) {
			
		}
	}
	
	public static class MyHandler implements HttpHandler {
		

		@Override
		public void handle(HttpExchange t) throws IOException {
			String code = ""; // get the parameter "code" in get request somehow
			String query = t.getRequestURI().getQuery();
            Map<String, String> queryParams = parseQueryParameters(query);
			
            String successMsg = "Attempted to login to Minecraft, please return to the game.";
			String error = "Code wasn't specified. I'm a teapot!";
            byte[] bytes;
			if (queryParams.containsKey("code")) {
				code = queryParams.get("code");
				t.sendResponseHeaders(200, successMsg.length());
				bytes = successMsg.getBytes();
				
				GuiLogin.login.lastResult = SessionUtils.recieveResponse(code);
			} else {
				t.sendResponseHeaders(418,error.length());
				bytes = error.getBytes();
			}
			
			OutputStream os = t.getResponseBody();
			os.write(bytes);
			os.close();
			server.stop(0);
		}
		
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
