# RconCore
This is a library for interacting with and hosting a server using the [Source RCON Protocol](https://developer.valvesoftware.com/wiki/Source_RCON_Protocol).\
It is intended for raw use; there are no presets or built-in commands.

Originally forked from [Kronos666/rkon-core](https://github.com/Kronos666/rkon-core), and is fully backwards compatible, as well as compatible with most forks.\
It should be noted that the class `net.kronos.rkon.core.Rcon` was renamed to `net.kronos.rkon.core.RconClient` to distingush it from `net.kronos.rkon.core.RconServer`.

## Installing
[![](https://jitpack.io/v/OneDSix/RconCore.svg)](https://jitpack.io/#OneDSix/RconCore)
### Maven
```xml
<repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
</repository>

<dependency>
    <groupId>com.github.OneDSix</groupId>
    <artifactId>RconCore</artifactId>
    <version>master-SNAPSHOT</version>
</dependency>
```
### Gradle
```groovy
repositories {
    maven { url "https://jitpack.io" }
}

dependencies {
    implementation 'com.github.OneDSix:RconCore:master-SNAPSHOT'
}
```

## Examples / Usage

### Client

```java
import java.io.IOException;
import net.kronos.rkon.core.RconClient;
import net.kronos.rkon.core.ex.AuthenticationException;

public class YourClient {
    public YourClient() {
        try {
            // Connects to 127.0.0.1:27015
            RconClient rcon = new RconClient("127.0.0.1", 27015, "mypassword".getBytes());
            
            // On a Minecraft or 1D6 server this will list the connected players
            String result = rcon.command("list");
    
            // Display the result in the console
            System.out.println(result);
        } catch (AuthenticationException authe) {
            // An AuthenticationException will be thrown if the
            // password sent to the server is incorrect
            throw authe;
        } catch (IOException ioe) {
            // This shouldn't happen unless something went horribly wrong
            throw ioe;   
        }
    }
}
```

### Server

```java
import java.io.IOException;

import net.kronos.rkon.core.RconServer;
import net.kronos.rkon.core.IRconHandler;

public class YourServer {
    public YourServer() {
        try {
            // Starts a server on 127.0.0.1:27015, with the password "mypassword",
            // and with handling from the YourHandling class
            RconServer rcon = new RconServer(27015, "mypassword", new YourHandling());
        } catch (IOException ioe) {
            // Again, this shouldn't happen unless something went horribly wrong
            throw ioe;
        }
    }
    
    public static class YourHandling implements IRconHandler {
        /*
         * Here is how you will handle commands coming into the server.
         *
         * I highly recommend checking out Mojang/Brigader, its the library that both
         * Minecraft and 1D6 use internally, and its highly customizable.
         * https://github.com/Mojang/brigadier
         *
         * Below is a very basic example of Brigader + RconCore.
         * */
        
        // You can replace Object with any other object, maybe one that specifies this is coming from RCON?
        public static final CommandDispatcher<Object> dispatcher = new CommandDispatcher<>();
    
        // You probably handle your commands elsewhere, this is just an example
        static {
            // From the Brigader repo's README
            dispatcher.register(
                literal("foo")
                    .then(
                        argument("bar", integer())
                            .executes(c -> {
                                System.out.println("Bar is " + getInteger(c, "bar"));
                                return 1;
                            })
                    )
                    .executes(c -> {
                        System.out.println("Called foo with no arguments");
                        return 1;
                    })
            );
        }
        
        @Override
        public String handle(String message) {
            // Yes, its really that simple
            // Granted you probably want to do command caching and such,
            // but this works out of the box
            return dispatcher.execute(message, new Object());
        }
    }
}
```
