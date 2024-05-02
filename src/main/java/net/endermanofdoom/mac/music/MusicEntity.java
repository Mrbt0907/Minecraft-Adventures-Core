package net.endermanofdoom.mac.music;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import net.endermanofdoom.mac.MACCore;
import net.minecraft.entity.Entity;
import net.minecraft.util.SoundEvent;

public class MusicEntity implements IMusicInteractable
{
	private static final List<MusicEntity> sounds = new ArrayList<MusicEntity>();
	private final Entity entity;
	private final SoundEvent music;
	private final int priority;
	public boolean isDead;
	
	MusicEntity(@Nonnull Entity entity, @Nonnull SoundEvent music, int priority)
	{
		this.entity = entity;
		this.music = music;
		this.priority = priority;
	}
	
	public static final MusicEntity create(@Nonnull Entity entity, @Nonnull SoundEvent music, int priority)
	{
		if (contains(entity))
			return null;

		MACCore.debug("Creating music entity for entity: " + entity);
		MusicEntity object = new MusicEntity(entity, music, priority);
		sounds.add(object);
		return object;
	}
	
	public static final void destroy(@Nonnull Entity entity)
	{
		MusicEntity object = get(entity); 
		if (object != null)
		{
			MACCore.debug("Killing music entity for entity: " + entity.getName());
			object.isDead = true;
			sounds.remove(object);
		}
	}
	
	public static final void destroyAll()
	{
		MACCore.debug("Killing all music entities");
		sounds.forEach(object -> object.isDead = true);
		sounds.clear();
	}
	
	public static final MusicEntity get(@Nonnull Entity entity)
	{
		for(MusicEntity object : sounds)
		{
			if (object.entity.equals(entity))
				return object;
		}
		
		return null;
	}
	
	public static final boolean isEmpty()
	{
		return sounds.isEmpty();
	}
	
	public static final boolean contains(@Nonnull Entity entity)
	{
		return get(entity) != null;
	}
	
	public Entity getEntity()
	{
		return entity;
	}
	
	@Override
	public boolean isMusicDead()
	{
		return isDead || entity.isDead;
	}

	@Override
	public int getMusicPriority()
	{
		return priority;
	}

	@Override
	public SoundEvent getMusic()
	{
		return music;
	}
	
	
}
