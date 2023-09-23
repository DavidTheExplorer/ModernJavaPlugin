package dte.modernjavaplugin.serviceprovider;

import org.bukkit.Bukkit;

import dte.modernjavaplugin.utils.ClassUtils;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

/**
 * Provides convenient loading of services that belong to the {@code Vault} plugin.
 */
public class VaultHook 
{
	/**
	 * Provides the installed implementation of the {@link Economy} interface on the server.
	 * 
	 * @return A provider for the server's economy, which could represent that it wasn't found.
	 */
	public static ServiceProvider<Economy> loadEconomy()
	{
		return createProviderFor("net.milkbowl.vault.economy.Economy");
	}
	
	/**
	 * Provides the installed implementation of the {@link Permission} interface on the server.
	 * 
	 * @return A provider for the server's permission manager, which could represent that it wasn't found.
	 */
	public static ServiceProvider<Permission> loadPermission()
	{
		return createProviderFor("net.milkbowl.vault.permission.Permission");
	}
	
	/**
	 * Provides the installed implementation of the {@link Chat} interface on the server.
	 * 
	 * @return A provider for the server's chat manager, which could represent that it wasn't found.
	 */
	public static ServiceProvider<Chat> loadChat()
	{
		return createProviderFor("net.milkbowl.vault.chat.Chat");
	}
	
	
	private static <S> ServiceProvider<S> createProviderFor(String serviceClassName) 
	{
		if(!Bukkit.getPluginManager().isPluginEnabled("Vault"))
			return ServiceProvider.empty();
		
		/*
		 * Impossible to get the class itself as a parameter because(for example) if an Economy plugin is not installed - 
		 * referencing Economy.class leads to NoClassDefFoundError.
		 */
		@SuppressWarnings("unchecked") //safe because this private method is used correctly
		Class<S> serviceClass = (Class<S>) ClassUtils.load(serviceClassName);
		
		return ServiceProvider.fromBukkit(serviceClass);
	}
}