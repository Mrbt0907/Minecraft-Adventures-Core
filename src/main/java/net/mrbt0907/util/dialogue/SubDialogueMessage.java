package net.mrbt0907.util.dialogue;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.mrbt0907.util.interfaces.IDialogue;

public class SubDialogueMessage implements IDialogue
{
	private final String content;
	private String displayedContent = "";
	private final String displayName;
	private final ResourceLocation sprite;
	private final int nameColor;
	private final int backgroundColor;
	public int alpha;
	public int index;
	
	public SubDialogueMessage(NBTTagCompound nbt)
	{
		this(nbt.getString("displayName"), nbt.getString("content"), nbt.hasKey("sprite") ? new ResourceLocation(nbt.getString("sprite")) : null, nbt.getInteger("nameColor"), nbt.getInteger("backgroundColor"));
	}
	
	public SubDialogueMessage(String displayName, String content, ResourceLocation sprite, int nameColor, int backgroundColor)
	{
		this.content = content;
		this.displayName = displayName;
		this.sprite = sprite;
		this.nameColor = nameColor;
		this.backgroundColor = backgroundColor;
	}

	public NBTTagCompound writeNBT(NBTTagCompound nbt)
	{
		nbt.setString("content", content);
		nbt.setString("displayName", displayName);
		if (sprite != null)
			nbt.setString("sprite", sprite.toString());
		nbt.setInteger("nameColor", nameColor);
		nbt.setInteger("backgroundColor", backgroundColor);
		return nbt;
	}
	
	public boolean isDone()
	{
		return index >= content.length();
	}
	
	public void nextCharacter()
	{
		if (index < content.length())
		{
			displayedContent += content.charAt(index);
			index++;
		}
	}
	
	@Override
	public String getContent()
	{
		return displayedContent;
	}

	@Override
	public String getDisplayName()
	{
		return displayName;
	}

	@Override
	public ResourceLocation getSprite()
	{
		return sprite;
	}

	@Override
	public int getNameColor()
	{
		return nameColor;
	}

	@Override
	public int getBackgroundColor()
	{
		return backgroundColor;
	}
}
