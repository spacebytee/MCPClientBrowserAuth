# MCPClientBrowserAuth
Browser Authentication Services for Minecraft Clients and Mods.
This supports Java 8! (unlike openauth)


Writen solely by spacebyte and upio.
upio wrote the current authentication class using github copilot & microsoft auth docs.

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

## Contributing
Make a pull request

## Credits
Credits would be appreciated but are **NOT REQUIRED**
