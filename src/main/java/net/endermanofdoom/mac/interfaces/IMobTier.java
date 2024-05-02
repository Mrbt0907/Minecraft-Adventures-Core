package net.endermanofdoom.mac.interfaces;

import net.endermanofdoom.mac.enums.EnumLevel;

public interface IMobTier
{
	public EnumLevel getTier();
	
	public default float getMultiplier()
	{
		return getTier().getMultiplier();
	}
}
