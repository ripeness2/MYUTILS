# Hello...

### my discord: _ripeness

### https://app.gitbook.com/o/asRZPDMyOj17Dy3Pjq6T/s/P6eAIqjW4JVquJPpbgNe/myutils-config-item-getter

## how to get MYUTILS maven

<sup>

                <repositories>
                    <repository>
                        <id>jitpack.io</id>
                        <url>https://jitpack.io</url>
                    </repository>
		    </repositories>

		<dependency>
                <groupId>com.github.ripeness2</groupId>
                <artifactId>MYUTILS</artifactId>
                <version>VERSION</version>
		</dependency>

</sup>
## how to use ListenerRegistration

<sup>

    public final class yourplugin extends JavaPlugin
      ListenerRegistration listeners = null;

	    @Override
	    public void onEnable() {
	        // Plugin startup logic

            try {
	        listeners = new ListenerRegistration(this, "org.ripeness.yourplugin.listeners");
	        listeners.registerListeners();
            } catch (Exception e) {
                getLogger().warning("Failed to register listeners: " + e.getMessage());
            }
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

</sup>
