package net.endermanofdoom.mac.internal.events;

import net.endermanofdoom.mac.ClientProxy;
import net.endermanofdoom.mac.MACCore;
import net.endermanofdoom.mac.internal.music.MusicManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;

public class ClientEventHandler
{
	private static final Minecraft MC = Minecraft.getMinecraft();
	private static boolean inWorld;
	
	public static void onWorldJoin()
	{
		inWorld = true;
	}
	
	public static void onWorldLeave()
	{
		inWorld = false;
		ClientProxy.DIALOGUE.reset();
	}
	
	@SubscribeEvent
	public static void onClientTick(ClientTickEvent event)
	{
		if (!inWorld && MC.world != null)
			onWorldJoin();
		else if (inWorld && MC.world == null)
			onWorldLeave();
			
		MusicManager.update();
	}
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onSoundPlayed(PlaySoundEvent event)
	{
		ISound sound = event.getResultSound();
		if (sound != null && sound.getCategory().equals(SoundCategory.MUSIC) && MusicManager.isMusicPlaying() && !MusicManager.isMusicPlaying(sound.getSoundLocation()))
		{
			MACCore.debug("Stopping music from playing: " + sound.getSoundLocation());
			event.setResultSound(null);
		}
	}
}