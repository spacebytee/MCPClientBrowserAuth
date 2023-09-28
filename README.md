Writen solely by spacebyte and upio. With help from Github Copilot.

# MCPClientBrowserAuth
Browser Authentication Services for Minecraft Clients and Mods.

## How To Use
The authentication works via a multi-step process

* SessionUtils is invoked, this will begin a web server, and prompt a login page in the users browser.
* When the login is finished, the webserver will pick it up via the redirect uri, and the web server will close.
* The SessionUtils will reach out to Authentication to hook into Microsoft servers to return the Minecraft access token
* SessionUtils will use that access token, and get the Minecraft profile info to create a session and login.

# Specifics
If you only need certain parts, or would like to replace our code, here are some vague documentation on how to use it

## SessionUtils.tryLoginBrowser()
This will complete the whole process, if possible. It will begin the webserver and prompt for login. If successful, it will fully switch the session to the logged in user.

## Authentication.retrieveAccessToken(String mscode, String recentPkce)
This will return the Minecraft access token, given you provide the MSA authentication code, and a pkce code to use for interacting with Microsoft APIs. This will complete the whole process of reaching out to Microsoft, Xbox, and Minecraft APIs.

## WebServer.initWebServer()
This will initialize the WebServer, if successful, it will return to SessionUtils.recieveResponse(code) to handle the MSA code.
