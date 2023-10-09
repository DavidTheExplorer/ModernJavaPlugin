package dte.modernjavaplugin.utils;

public class ClassUtils 
{
	public static Class<?> load(String className)
	{
		try 
		{
			return Class.forName(className);
		}
		catch(Exception exception) 
		{
			return null;
		}
	}
}
