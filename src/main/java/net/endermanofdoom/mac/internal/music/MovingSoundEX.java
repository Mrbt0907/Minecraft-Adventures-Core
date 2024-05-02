package net.endermanofdoom.mac.internal.music;

import net.endermanofdoom.mac.MACCore;
import net.endermanofdoom.mac.util.math.Maths.Vec3;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.entity.Entity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;

public class MovingSoundEX extends MovingSound
{
	private static Minecraft mc = Minecraft.getMinecraft();
	
	public long ticksExisted;
	public boolean transition;
	private Object obj;
	private Vec3 pos;
	private float maxVolume, transitionVolume = 0.0F, range;
	private boolean useY;
	
	public MovingSoundEX(SoundEvent event, SoundCategory category, float volume, float pitch)
	{
		this(null, event, category, volume, pitch, -1, false);
	}
	
	public MovingSoundEX(Object obj, SoundEvent event, SoundCategory category, float volume, float range)
	{
		this(obj, event, category, volume, 1.0F, range, true);
	}
	
	public MovingSoundEX(Object obj, SoundEvent event, SoundCategory category, float volume, float pitch, float range)
	{
		this(obj, event, category, volume, pitch, range, true);
	}
	
	public MovingSoundEX(Object obj, SoundEvent event, SoundCategory category, float volume, float pitch, float range, boolean useY)
	{
		super(event, category);
		this.obj = obj;
		volume = MathHelper.clamp(volume, 0.0F, 1.0F);
		this.volume = volume;
		maxVolume = volume;
		this.pitch = pitch;
		this.range = range;
		this.useY = useY;
		repeatDelay = 0;
		attenuationType = useY == false && range < 0.0F ? AttenuationType.NONE : attenuationType;
		update();
	}

	public void update()
	{
		if (donePlaying)
			return;
		if (mc.player == null || mc.world == null)
		{
			donePlaying = true;
			MACCore.error("Unable to play sound " + getSoundLocation().toString() + " as the world is null");
			return;
		}
		
		if (transition)
		{
			if (transitionVolume <= 0.0F)
			{
				donePlaying = true;
				MACCore.debug("Stopped Music: " + getSoundLocation());
				return;
			}
			else
				transitionVolume -= 0.01F;
		}
		else if (transitionVolume < 1.0F)
			transitionVolume = Math.min(transitionVolume + 0.01F, 1.0F);
		
		xPosF = (float) mc.player.posX;
		yPosF = (float) mc.player.posY;
		zPosF = (float) mc.player.posZ;
		
		if (pos == null)
		{
			if (obj instanceof Vec3)
				pos = (Vec3)obj;
			else if (obj instanceof Entity)
			{
				Entity entity = ((Entity)obj);
				pos = new Vec3(entity.posX, entity.posY, entity.posZ);
			}
			else
			{
				donePlaying = true;
				MACCore.error("Unable to play sound " + getSoundLocation().toString() + " as the provided object is not supported");
				return;
			}
		}
		else if (range > 0.0F)
		{
			float multiplier = (float)MathHelper.clamp((range - pos.distance(mc.player.posX, useY ? mc.player.posY : 0.0D, mc.player.posZ)) / range, 0.0F, 1.0F);
			volume = maxVolume * transitionVolume * multiplier;
			xPosF = (float) MathHelper.clamp(pos.posX, mc.player.posX - 6.0D, mc.player.posX + 6.0D);
			yPosF = (float) MathHelper.clamp(pos.posY, mc.player.posY - 6.0D, mc.player.posY + 6.0D);
			zPosF = (float) MathHelper.clamp(pos.posZ, mc.player.posZ - 6.0D, mc.player.posZ + 6.0D);
		}
		else
			volume = maxVolume * transitionVolume;
		ticksExisted++;
	}
	
	public void setRepeat(boolean shouldRepeat)
	{
		repeat = shouldRepeat;
	}
	
	public void adjustVolume(float volume)
	{
		maxVolume = MathHelper.clamp(volume, 0.0F, 1.0F);
	}
	
	public void adjustPitch(float pitch)
	{
		this.pitch = MathHelper.clamp(pitch, 0.0F, 1.0F);
	}
	
	public void setDone()
	{
		donePlaying = true;
	}
}
