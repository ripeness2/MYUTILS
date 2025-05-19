# Hello...

### my discord: _ripeness

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
                <version>${project.version}</version>
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