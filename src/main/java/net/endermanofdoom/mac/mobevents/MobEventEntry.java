package net.endermanofdoom.mac.mobevents;

import net.minecraft.entity.Entity;

public class MobEventEntry
{
	public final Class<? extends Entity> clazz;
	public final MobEventType type;
	public final float eventPercentage;
	public final float chance;
	public final int maxActiveCount;
	public final int maxTotalCount;
	
	public MobEventEntry(Class<? extends Entity> clazz, MobEventType type, float eventPercentage, float chance, int maxActiveCount, int maxTotalCount)
	{
		this.clazz = clazz;
		this.type = type;
		this.eventPercentage = eventPercentage;
		this.chance = chance;
		this.maxActiveCount = maxActiveCount;
		this.maxTotalCount = maxTotalCount;
	}
}
