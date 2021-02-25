# RconCore
This is a library for the [Source RCON Protocol](https://developer.valvesoftware.com/wiki/Source_RCON_Protocol), it is intended for raw use; there are no presets or built-in commands.

Forked from [Kronos666/rkon-core](https://github.com/Kronos666/rkon-core)

## Usage
```java
// Connects to 127.0.0.1 on port 27015
Rcon rcon = new Rcon("127.0.0.1", 27015, "mypassword".getBytes());

// Example: On a minecraft server this will list the connected players
String result = rcon.command("list");

// Display the result in the console
System.out.println(result);
```
When connecting to the rcon server, an `AuthenticationException` will be thrown if the password is incorrect.

## Download
If you want to download a packed `.jar`, its available [here](https://github.com/Pequla/RconCore/releases/latest).

### Maven 
[![](https://jitpack.io/v/Pequla/RconCore.svg)](https://jitpack.io/#Pequla/RconCore)
```xml
<repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
</repository>

<dependency>
    <groupId>com.github.Pequla</groupId>
    <artifactId>RconCore</artifactId>
    <version>1.2</version>
</dependency>
```