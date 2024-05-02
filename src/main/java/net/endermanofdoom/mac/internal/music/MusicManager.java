package net.endermanofdoom.mac.internal.music;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Nonnull;

import net.endermanofdoom.mac.MACCore;
import net.endermanofdoom.mac.music.IMusicInteractable;
import net.endermanofdoom.mac.music.MusicEntity;
import net.endermanofdoom.mac.music.MusicObject;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;

public class MusicManager
{
	private static final Minecraft MC = Minecraft.getMinecraft();
	private static final List<IMusicInteractable> objects = new ArrayList<IMusicInteractable>();
	private static MovingSoundEX currentMusic, lastMusic;
	
	public static void update()
	{
		if (MC.isGamePaused())
			return;
		
		if (MC.world == null)
		{
			if (currentMusic != null || lastMusic != null)
				stop();
			
			objects.clear();
			
			if (!MusicObject.isEmpty())
				MusicObject.destroyAll();
			if (!MusicEntity.isEmpty())
				MusicEntity.destroyAll();
			
			return;
		}
		else if (MC.gameSettings.getSoundLevel(SoundCategory.MUSIC) <= 0.0F)
		{
			if (currentMusic != null || lastMusic != null)
				stop();
			
			return;
		}
		
		if (currentMusic != null && (currentMusic.isDonePlaying() || !MC.getSoundHandler().isSoundPlaying(currentMusic)))
			currentMusic = null;
			
		if (lastMusic != null && (lastMusic.isDonePlaying() || !MC.getSoundHandler().isSoundPlaying(lastMusic)))
			lastMusic = null;
		
		if (!objects.isEmpty())
		{
			Iterator<IMusicInteractable> iterator = objects.iterator();
			IMusicInteractable object;
			SoundEvent sound = null, music;
			int priority = -1, musicPriority;
			
			while (iterator.hasNext())
			{
				object = iterator.next();
				musicPriority = object.getMusicPriority();
				music = object.getMusic();
				
				if (object.isMusicDead())
				{
					if (object instanceof MusicEntity)
						MusicEntity.destroy(((MusicEntity)object).getEntity());
					iterator.remove();
				}
				else if (music != null && musicPriority > priority)
				{
					priority = musicPriority;
					sound = music;
				}
			}
			
			if (currentMusic == null && sound != null || currentMusic != null && (sound == null || !currentMusic.getSoundLocation().equals(sound.getSoundName())))
				play(sound);
		}
	}
	
	public static boolean isMusicPlaying()
	{
		return currentMusic != null || lastMusic != null;
	}
	
	public static boolean isMusicPlaying(@Nonnull SoundEvent sound)
	{
		return isMusicPlaying(sound.getSoundName());
	}
	
	public static boolean isMusicPlaying(@Nonnull ResourceLocation soundLocation)
	{
		return currentMusic == null ? lastMusic != null && lastMusic.getSoundLocation().equals(soundLocation) : currentMusic.getSoundLocation().equals(soundLocation);
	}
	
	public static boolean addMusicInteractable(IMusicInteractable object)
	{
		if (objects.contains(object))
			return false;
		else
			return objects.add(object);
	}
	
	public static boolean playMusic(@Nonnull SoundEvent sound)
	{
		return playMusic(sound, 0);
	}
	
	public static boolean playMusic(@Nonnull SoundEvent sound, int priority)
	{
		MusicObject object = MusicObject.create(sound, priority);
		
		if (object != null)
		{
			MACCore.debug("Queuing music for sound event: " + sound.getSoundName());
			addMusicInteractable(object);
			return true;
		}
		else
			return false;
	}
	
	public static MusicEntity playMusic(@Nonnull Entity entity, @Nonnull SoundEvent sound)
	{
		return playMusic(entity, sound, 0);
	}
	
	public static MusicEntity playMusic(@Nonnull Entity entity, @Nonnull SoundEvent sound, int priority)
	{
		MusicEntity object = MusicEntity.create(entity, sound, priority);
		
		if (object != null)
		{
			MACCore.debug("Queuing music for entity: " + entity.getName());
			addMusicInteractable(object);
			return object;
		}
		else
			return null;
	}
	
	public static void stopMusic(@Nonnull SoundEvent sound)
	{
		MACCore.debug("Stopping non-object music: " + sound.getSoundName());
		MusicObject.destroy(sound);
	}
	
	public static void stopMusic(@Nonnull Entity entity)
	{
		MACCore.debug("Stopping entity music: " + entity.getName());
		MusicEntity.destroy(entity);
	}
	
	private static void play(SoundEvent sound)
	{
		if (lastMusic == null && currentMusic == null)
			MC.getSoundHandler().stop("", SoundCategory.MUSIC);
		
		lastMusic = currentMusic;
		
		if (lastMusic != null)
			lastMusic.transition = true;
			
		currentMusic = sound == null ? null : new MovingSoundEX(MC.player, sound, SoundCategory.MUSIC, 1.0F, 1.0F, -1, false);
		
		if (currentMusic == null)
			MACCore.debug("Stopping Music: " + lastMusic.getSoundLocation());
		else
		{
			currentMusic.setRepeat(true);
			MC.getSoundHandler().playSound(currentMusic);
			MACCore.debug("Playing Music: " + currentMusic.getSoundLocation());
		}
		
	}
	
	private static void stop()
	{
		if (currentMusic != null)
		{
			MACCore.debug("Stopping Music: " + currentMusic.getSoundLocation());
			MC.getSoundHandler().stopSound(currentMusic);
			currentMusic = null;
		}
		
		if (lastMusic != null)
		{
			MACCore.debug("Stopping Last Music: " + lastMusic.getSoundLocation());
			MC.getSoundHandler().stopSound(lastMusic);
			lastMusic = null;
		}
	}
}
