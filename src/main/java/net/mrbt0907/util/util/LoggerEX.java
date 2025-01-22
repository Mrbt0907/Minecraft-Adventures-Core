package net.mrbt0907.util.util;

import org.apache.logging.log4j.Logger;

public class LoggerEX 
{
	private Logger log;
	
	public boolean debug;
	public boolean warn;
	public boolean error;
	
	public LoggerEX(Logger log)
	{
		if (log == null)
			fatal("Logger was null");
		
		this.log = log;
		
		debug = false;
		warn = true;
		error = true;
	}
	
	private String getMessage(Object... messages)
	{
		String output = "";
		String input;
		
		for (Object message : messages)
		{
			input = String.valueOf(message);
			
			if (!input.isEmpty())
				if (output.isEmpty())
					output = input;
				else
					output += "\n" + input;
		}
		
		return output;
	}
	
	public void info(Object... messages)
	{
		String output = getMessage(messages);
			
		if (!output.isEmpty())
			log.info(output);
	}
	
	public void debug(Object... messages)
	{
		String output = getMessage(messages);
		
		if (!output.isEmpty())
		{
			log.debug(output);
			
			if (debug)
				log.info(output);
		}
	}
	
	public void warn(Object... messages)
	{
		if (warn)
		{
			String output = getMessage(messages);
			
			if (!output.isEmpty())
				log.warn(output);
		}
	}
	
	public void error(Object message)
	{
		if (error)
		{
			Throwable throwable;
			
			if (message instanceof Throwable)
				throwable = (Throwable) message;
			else
				throwable = new Throwable(String.valueOf(message));
			
			throwable.printStackTrace();
		}
	}
	
	public void fatal(Object message)
	{
		Error error;
		
		if (message instanceof Error)
			error = (Error) message;
		else if (message instanceof Exception)
			error = new Error(((Exception)message).getMessage());
		else
			error = new Error(String.valueOf(message));
		
		throw error;
	}
}
