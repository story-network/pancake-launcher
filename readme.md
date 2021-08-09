# Pancake Launcher
Portable custom Minecraft server launcher with Mixin and official obfuscation map applied.

## Usage
`java -Xmx2G -Xms2G -jar pancake-launcher.jar <server.jar> [args]`

## Implementing custom server
Custom server JAR must provide `target_version` resource for detecting capable Minecraft server version. Also register `IPancakeServer` service with proper implementation.

Sample implementation below
```java
public class SampleServer implements IPancakeServer {

    @Override
    public void start(String[] args, Runnable finishMixin) {
        // Finish mixin registration.
        finishMixin.run();

        System.out.println("Launching server...");

        // Start vanilla MC server
        net.minecraft.server.Main.main(args);
    }
    
}
```