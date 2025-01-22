package net.mrbt0907.util.interfaces;

import net.minecraft.util.ResourceLocation;

public interface IDialogue
{
	public String getContent();
	public String getDisplayName();
	public ResourceLocation getSprite();
	public int getNameColor();
	public int getBackgroundColor();
}
