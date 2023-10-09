package dte.modernjavaplugin.serviceprovider;

import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

public class ServiceProvider<S>
{
	private S service;
	private Runnable absenceHandler = () -> {};

	public ServiceProvider(S service) 
	{
		this.service = service;
	}
	
	/*
	 * Factories
	 */
	public static <S> ServiceProvider<S> empty()
	{
		return new ServiceProvider<>(null);
	}
	
	public static <S> ServiceProvider<S> fromBukkit(Class<S> serviceClass)
	{
		RegisteredServiceProvider<S> foundProvider = Bukkit.getServicesManager().getRegistration(serviceClass);
		
		if(foundProvider == null)
			return empty();
		
		return new ServiceProvider<>(foundProvider.getProvider());
	}
	
	/*
	 * Builder Pattern
	 */
	public ServiceProvider<S> ifMissing(Runnable absenceHandler)
	{
		this.absenceHandler = absenceHandler;
		return this;
	}
	
	public S get()
	{
		if(this.service == null) 
			this.absenceHandler.run();

		return this.service;
	}
}
