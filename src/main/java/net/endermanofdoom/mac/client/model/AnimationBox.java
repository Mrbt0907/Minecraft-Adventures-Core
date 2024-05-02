package net.endermanofdoom.mac.client.model;

import net.minecraft.client.model.ModelBox;

public class AnimationBox
{
	public final ModelBox box;
	protected float delay;
	private float currentDelay;
	
	public AnimationBox(ModelBox box)
	{
		this.box = box;
	}
	
	public void rotate(float x, float y, float z, float delay)
	{
		
	}
}
