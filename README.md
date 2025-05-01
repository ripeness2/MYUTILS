# Hello...
### my discord: _ripeness

<br></br>
##this is MYUTILS maven =>
<sup>
```
	<repositories>
		<repository>
		    <id>jitpack.io</id>
		    <url>https://jitpack.io</url>
		</repository>
	</repositories>

  <dependency>
	    <groupId>com.github.ripeness2</groupId>
	    <artifactId>MYUTILS</artifactId>
	    <version>u-1.2</version>
	</dependency>
```
</sup>

## how to use ListenerRegistration =>

<sup>
	
  ```
	
    public final class yourplugin extends JavaPlugin
      ListenerRegistration listeners = null;

	    @Override
	    public void onEnable() {
	        // Plugin startup logic
	        listeners = new ListenerRegistration(this, "org.ripeness.yourplugin.listeners");
	        listeners.registerListeners();
	    }
	
	    @Override
	    public void onDisable() {
	        // Plugin shutdown logic
	        try {
	            listeners.unregisterListeners();
	        } catch (Exception e) {
	            getLogger().warning("Failed to unregister listeners: " + e.getMessage());
	        }
	    }
	}
  ```
</sup>
