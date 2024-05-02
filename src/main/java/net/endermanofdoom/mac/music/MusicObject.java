package net.endermanofdoom.mac.music;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import net.endermanofdoom.mac.MACCore;
import net.minecraft.util.SoundEvent;

public class MusicObject implements IMusicInteractable
{
	private static final List<MusicObject> sounds = new ArrayList<MusicObject>();
	private final SoundEvent sound;
	public int priority;
	public boolean isDead;
	
	public static final MusicObject create(@Nonnull SoundEvent sound, int priority)
	{
		if (contains(sound))
			return null;

		MACCore.debug("Creating music object for sound effect: " + sound);
		MusicObject object = new MusicObject(sound, priority);
		sounds.add(object);
		return object;
	}
	
	public static final void destroy(@Nonnull SoundEvent sound)
	{
		MusicObject object = get(sound); 
		if (object != null)
		{
			MACCore.debug("Killing music object for sound effect: " + sound);
			object.isDead = true;
			sounds.remove(object);
		}
	}
	
	public static final void destroyAll()
	{
		MACCore.debug("Killing all music objects");
		sounds.forEach(object -> object.isDead = true);
		sounds.clear();
	}
	
	public static final MusicObject get(@Nonnull SoundEvent sound)
	{
		for(MusicObject object : sounds)
		{
			if (object.sound.equals(sound))
				return object;
		}
		
		return null;
	}
	
	public static final boolean isEmpty()
	{
		return sounds.isEmpty();
	}
	
	public static final boolean contains(@Nonnull SoundEvent sound)
	{
		return get(sound) != null;
	}
	
	private MusicObject(@Nonnull SoundEvent sound, int priority)
	{
		this.sound = sound;
		this.priority = priority;
	}
	
	@Override
	public boolean isMusicDead()
	{
		return isDead;
	}

	@Override
	public int getMusicPriority()
	{
		return priority;
	}

	@Override
	public SoundEvent getMusic()
	{
		return sound;
	}
}