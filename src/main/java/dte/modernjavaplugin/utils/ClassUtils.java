package dte.modernjavaplugin.utils;

import java.lang.reflect.Constructor;
import java.util.Optional;

public class ClassUtils 
{
	public static <T> Optional<Constructor<T>> getNoArgumentConstructor(Class<T> classz)
	{
		try
		{
			return Optional.of(classz.getConstructor());
		}
		catch(NoSuchMethodException exception)
		{
			return Optional.empty();
		}
	}
}
