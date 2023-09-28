package com.bytespacegames.mcpauth;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;

import com.mojang.authlib.GameProfile;

import fr.litarvan.openauth.microsoft.MicrosoftAuthResult;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticationException;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticator;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

public class SessionUtils {
	public static void setSession(Session session) {
		Minecraft.getMinecraft().session = session;
	}

	public static String generatePKCE() {
		SecureRandom secureRandom = new SecureRandom();
		byte[] codeVerifierBytes = new byte[32];
		secureRandom.nextBytes(codeVerifierBytes);
		String codeVerifier = Base64.encodeBase64URLSafeString(codeVerifierBytes);

		return codeVerifier;
	}

	static String recentPkce;

	public static void tryLoginBrowser() throws IOException, URISyntaxException {
		recentPkce = generatePKCE();
		WebServer.initWebServer();
		if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
			Desktop.getDesktop()
					.browse(new URI("https://login.live.com/oauth20_authorize.srf?"
							+ "client_id=e5785f8b-29f1-477a-b758-d4c15b565c9e" + "&prompt=select_account"
							+ "&scope=Xboxlive.signin+Xboxlive.offline_access" + "&code_challenge_method=S256"
							+ "&code_challenge=" + Base64.encodeBase64URLSafeString(DigestUtils.sha256(recentPkce))
							+ "&response_type=code" + "&redirect_uri=http://localhost:6921/microsoft/complete"));
		} else {
			WebServer.server.stop(0);
		}
	}

	public static String recieveResponse(String code) {
		try {
			String accessToken = new SkiddedAuth().retrieveRefreshToken(code,recentPkce);
			System.out.println("ref token "+accessToken);
			
			JsonObject loginProfileInfo = SkiddedAuth.getAccountInfo(accessToken);
			String name = loginProfileInfo.getString("name");
            String id = loginProfileInfo.getString("id");
			System.out.println("login profile info "+loginProfileInfo + "name "+name+" id "+id);
			
			setSession(new Session(name, id, accessToken,
					"legacy"));
			return "Logged in successfully as " + name +"!";
		} catch (Exception e) {
			e.printStackTrace();
			return "Could not log-in. idk what to tell you bruh";
		}
	}
}
