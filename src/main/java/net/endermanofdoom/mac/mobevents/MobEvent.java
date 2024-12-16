package net.endermanofdoom.mac.mobevents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import net.endermanofdoom.mac.interfaces.IBossBar;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

public abstract class MobEvent extends net.minecraftforge.registries.IForgeRegistryEntry.Impl<MobEvent> implements IBossBar
{
	
	protected int maxEntities;
	
	public World world;
	public ITextComponent displayName;
	public boolean isDead;
	public long ticksExisted;
	private final Map<String, List<UUID>> entities = new HashMap<String, List<UUID>>();
	
	public MobEvent(World world)
	{
		this.world = world;
	}
	
	public void writeNBT(NBTTagCompound nbt)
	{
		nbt.setBoolean("isDead", isDead);
		
		NBTTagList nbtEntities = new NBTTagList();
		NBTTagCompound nbtEntity; List<UUID> uuids; int size;
		for (Entry<String, List<UUID>> entry : entities.entrySet())
		{
			nbtEntity = new NBTTagCompound();
			nbtEntity.setString("class", entry.getKey());
			uuids = entry.getValue(); size = uuids.size();
			for (int i = 0; i < size; i++)
				nbtEntity.setUniqueId("uuid_" + String.valueOf(i), uuids.get(i));
			nbtEntities.appendTag(nbtEntity);
		}
		nbt.setTag("entities", nbtEntities);
	}
	
	public void readNBT(NBTTagCompound nbt)
	{
		isDead = nbt.getBoolean("isDead");
		
		NBTTagList nbtEntities = nbt.getTagList(getBarName(), 10);
		NBTTagCompound nbtEntity;
		int keys; String key;
		entities.clear();
		
		for (int i = 0; i < nbtEntities.tagCount(); i++)
		{
			nbtEntity = nbtEntities.getCompoundTagAt(i);
			keys = nbtEntity.getKeySet().size() - 1; key = nbtEntity.getString("class");
			entities.put(key, new ArrayList<UUID>());
			for (int ii = 0; ii < keys; i++)
				entities.get(key).add(nbtEntity.getUniqueId("uuid_" + ii));
		}
	}
	
	public void tick()
	{
		onTick();
		ticksExisted++;
	}
	
	protected abstract void onTick();
	public abstract void onEntitySpawned(Entity entity);
	public abstract void onEntityHurt(Entity entity);
	public abstract void onEntityDead(Entity entity);
	
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
