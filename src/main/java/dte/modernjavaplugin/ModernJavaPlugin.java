package dte.modernjavaplugin;

import static java.util.stream.Collectors.toList;
import static org.bukkit.ChatColor.RED;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.reflections.Reflections;

import dte.modernjavaplugin.serviceprovider.ServiceProvider;
import dte.modernjavaplugin.serviceprovider.VaultHook;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;


public class ModernJavaPlugin extends JavaPlugin
{
	private final String pluginName = getDescription().getName();

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
	
	/**
	 * Auto-detects and registers all subclasses of {@code Listener} in the package identified by the provided {@code package name}.
	 * 
	 * @param listenersPackage The name of the listeners' package.
	 */
	public void registerListeners(String listenersPackage) 
	{
		List<Listener> foundListeners = new Reflections(listenersPackage).getSubTypesOf(Listener.class).stream()
				.map(classz -> 
				{
					try
					{
						return (Listener) classz.getConstructor().newInstance();
					}
					catch(Exception exception)
					{
						logToConsole(RED + String.format("Could not auto-register listener %s due to: %s", classz.getName(), ExceptionUtils.getCause(exception)));
						return null;
					}
				})
				.filter(Objects::nonNull)
				.collect(toList());
		
		if(!foundListeners.isEmpty()) 
		{
			logToConsole(String.format("Auto-registering %d listeners...", foundListeners.size()));
			registerListeners(foundListeners.toArray(new Listener[0]));
		}
	}

	/**
	 * Sends a colored message to the console, without omitting the plugin's prefix.
	 * 
	 * @param message The message to send.
	 */
	public void logToConsole(String message) 
	{
		Bukkit.getConsoleSender().sendMessage(String.format("[%s] %s", this.pluginName, message));
	}

	/**
	 * Logs to console the provided {@code error messages} and then shuts the plugin down.
	 * 
	 * @param messages The error messages to send.
	 */
	public void disableWithError(String... messages) 
	{
		Arrays.stream(messages).forEach(this::logToConsole);
		Bukkit.getPluginManager().disablePlugin(this);
	}
	
	/**
	 * Shortcut method that imitates a natural way of retrieving the Economy Manager of the server.
	 * 
	 * @return What {@link VaultHook#loadEconomy()} returns.
	 */
	public ServiceProvider<Economy> loadEconomy()
	{
		return VaultHook.loadEconomy();
	}
	
	/**
	 * Shortcut method that imitates a natural way of retrieving the Permissions Manager of the server.
	 * 
	 * @return What {@link VaultHook#loadPermission()} returns.
	 */
	public ServiceProvider<Permission> loadPermissions()
	{
		return VaultHook.loadPermission();
	}
	
	/**
	 * Shortcut method that imitates a natural way of retrieving the Chat Manager of the server.
	 * 
	 * @return What {@link VaultHook#loadChat()} returns.
	 */
	public ServiceProvider<Chat> loadChat()
	{
		return VaultHook.loadChat();
	}
}