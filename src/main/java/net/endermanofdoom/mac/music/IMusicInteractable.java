package net.endermanofdoom.mac.music;

import net.minecraft.util.SoundEvent;

public interface IMusicInteractable
{
	/**Indicates when this instance should be deleted from the music manager. Set to true when this object needs to be deleted*/
	public boolean isMusicDead();
	
	/**Indicates when this music can play. Higher values makes this music take more priority over others*/
	public int getMusicPriority();
	
	/**Gets the music that will play*/
	public SoundEvent getMusic();
}
