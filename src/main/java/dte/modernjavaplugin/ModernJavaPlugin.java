package dte.modernjavaplugin;

import java.util.Arrays;
import java.util.Objects;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.reflections.Reflections;

public class ModernJavaPlugin extends JavaPlugin
{
	private final String pluginName = getDescription().getName();
	
	private boolean isDisabled;
	private Runnable safeDisableListener;
	
	/**
	 * Registers the provided {@code listeners} for this plugin.
	 * 
	 * @param listeners The listeners to register.
	 */
	public void registerListeners(Listener... listeners) 
	{
		for(Listener listener : listeners)
			Bukkit.getPluginManager().registerEvents(listener, this);
	}
	
	public void registerSimpleListeners(String listenersPackageName) 
	{
		String listenersPackage = String.format("%s.%s", getClass().getPackage().getName(), listenersPackageName);
		
		Listener[] listeners = new Reflections(listenersPackage).getSubTypesOf(Listener.class).stream()
				.map(classz -> 
				{
					try
					{
						return classz.getConstructor().newInstance();
					} 
					catch(Exception exception) 
					{
						getLogger().severe("Could not auto-register listener: " + classz.getName());
						return null;
					}
				})
				.filter(Objects::nonNull)
				.toArray(Listener[]::new);
		
		registerListeners(listeners);
	}
	
	/**
	 * Sends a colorable message to the console, without omitting the plugin's prefix.
	 * 
	 * @param message The message to send.
	 */
	public void logToConsole(String message) 
	{
		String withPrefix = String.format("[%s] %s", this.pluginName, message);

		Bukkit.getConsoleSender().sendMessage(withPrefix);
	}
	
	
	
	/*
	 * Safe Disable Methods - The only workaround the following example scenario:
	 * 1. During onEnabe() You save the instance of Essentials.
	 * 2. During onDisable() you try to send a message to all the AFK players.
	 * 3. NPE is suddenly thrown during onDisable() because Essentials wasn't on the server.
	 * 
	 * The status of the plugin must be saved, because onDisable() is called *after* the plugin is disabled - so isEnabled() will always be false.
	 */
	
	@Override
	public void onDisable()
	{
		if(this.isDisabled) 
			return;
		
		this.safeDisableListener.run();
	}
	
	public void disable() 
	{
		this.isDisabled = true;
		setEnabled(false);
	}
	
	public void disableWithError(String... messages) 
	{
		Arrays.stream(messages).forEach(this::logToConsole);
		disable();
	}
	
	protected void setDisableListener(Runnable code) 
	{
		this.safeDisableListener = code;
	}
}