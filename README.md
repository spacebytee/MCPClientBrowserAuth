# MCPClientBrowserAuth
Browser Authentication Services for Minecraft Clients and Mods.
This supports Java 8! (unlike openauth)


Writen solely by [spacebyte](https://github.com/bytespacegames) and [upio](https://github.com/notpoiu).<br>
written with the help of github copilot & microsoft auth docs.

## How To Use
Use the code below to login using browser:
```java
import com.bytespacegames.mcpauth

try {
  SessionUtils.tryLoginBrowser();
} catch (IOException e) {
  e.printStackTrace();
} catch (URISyntaxException e) {
  e.printStackTrace();
}
```

## NOTE
Please note that you may need to go in WebServer.java and modify these lines of code to change status text on your GUI or print the status text:<br>
![image](https://github.com/notpoiu/MCPClientBrowserAuth/assets/75510171/ab5b5661-2488-4c2d-bb47-4f7e121127b5)

Also, you may need these jars as dependencies (you can download them using these download links or search them up yourself):
https://cdn.upio.dev/assets/http-20070405.jar
https://cdn.upio.dev/assets/javax.json-1.0.jar

## Contributing
Make a pull request

## Credits
Credits would be appreciated but are **NOT REQUIRED**
