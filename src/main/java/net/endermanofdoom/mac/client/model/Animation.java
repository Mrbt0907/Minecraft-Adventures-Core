package net.endermanofdoom.mac.client.model;

import java.util.ArrayList;
import java.util.List;

public class Animation
{
	public final boolean loop;
	public final List<Object> timeline = new ArrayList<Object>();
	
	
	public Animation(boolean shouldLoop)
	{
		loop = shouldLoop;
	}
	
	
}