package dte.modernjavaplugin;

import static org.bukkit.ChatColor.RED;

import java.util.Arrays;
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
	 * Registers all {@code Listener} classes that are located inside the provided {@code package}. 
	 * This method treats only classes that have an empty constructor.
	 * 
	 * @param listenersPackage The name of the listeners' package.
	 */
	public void registerListenersAt(String listenersPackage) 
	{
		Listener[] foundListeners = new Reflections(listenersPackage).getSubTypesOf(Listener.class).stream()
				.map(classz -> 
				{
					try
					{
						//create an instance from the empty constructor
						return classz.getConstructor().newInstance();
					}
					catch(NoSuchMethodException exception) 
					{
						//skip the class if it doesn't have an empty constructor
						return null;
					}
					catch(Exception exception)
					{
						//log any other exceptions
						logError(String.format("Couldn't register listener '%s' due to %s", classz.getSimpleName(), ExceptionUtils.getRootCauseMessage(exception)));
						return null;
					}
				})
				.filter(Objects::nonNull)
				.toArray(Listener[]::new);

		registerListeners(foundListeners);
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
	 * A variant of {@link #logToConsole(String)} that logs <b>red</b> messages to the console.
	 * 
	 * @param message The error message to log.
	 */
	public void logError(String message) 
	{
		logToConsole(RED + message);
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