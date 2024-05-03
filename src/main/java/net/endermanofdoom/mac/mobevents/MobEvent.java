package net.endermanofdoom.mac.mobevents;

import net.endermanofdoom.mac.interfaces.IBossBar;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.ITextComponent;

public class MobEvent implements IBossBar
{
	protected final int MAX_ENTITIES;
	public ITextComponent displayName;
	public boolean isDead;
	
	public MobEvent(int maxEntities)
	{
		MAX_ENTITIES = maxEntities;
	}
	
	public void writeNBT(NBTTagCompound nbt)
	{
		
	}
	
	public void readNBT(NBTTagCompound nbt)
	{
		
	}
	
	public void tick()
	{
		
	}
	
	public int getMaxEntities()
	{
		return MAX_ENTITIES;
	}
	
	public int getEntityCount()
	{
		return 0;
	}
	
	@Override
	public boolean isDead()
	{
		return isDead;
	}

	@Override
	public double getBarHealth()
	{
		return 0;
	}

	@Override
	public double getBarMaxHealth()
	{
		return 0;
	}

	@Override
	public String getBarName()
	{
		return displayName.getFormattedText();
	}

}
