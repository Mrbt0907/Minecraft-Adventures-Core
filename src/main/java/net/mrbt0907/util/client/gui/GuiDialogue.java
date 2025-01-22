package net.mrbt0907.util.client.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.mrbt0907.util.dialogue.SubDialogueMessage;
import net.mrbt0907.util.util.ColorUtils;
import net.mrbt0907.util.util.StringUtil;

public class GuiDialogue extends AbstractGui
{
	protected final List<SubDialogueMessage> SUBDIALOGUE = new ArrayList<SubDialogueMessage>();

	protected SubDialogueMessage currentSubDialogue;
	protected boolean displayingDialogue;
	protected boolean displayingSubDialogue;
	protected long tickSubDialogue;
	protected long tickSubDialogueDone;
	
	protected int xCenter;
	protected int yCenter;
	protected int xStart;
	protected int yStart;
	
	@SubscribeEvent
	public void onRenderOverlay(RenderGameOverlayEvent.Pre event)
	{
		if (!event.getType().equals(ElementType.HOTBAR) || MC.world == null || MC.player == null)
			return;
		
		if (displayingDialogue)
		{
			if (displayingSubDialogue)
				displayingSubDialogue = false;
		}
		else if (displayingSubDialogue)
		{
			if (currentSubDialogue == null)
				currentSubDialogue = SUBDIALOGUE.get(0);
			
			ScaledResolution s = new ScaledResolution(MC);
			int scaledWidth = s.getScaledWidth();
			int scaledHeight = s.getScaledHeight();
			xCenter = (int) (scaledWidth * 0.5F);
			yCenter = (int) (scaledHeight);
			xStart = (int) (xCenter - 150);
			yStart = (int) (yCenter - 90);
			int[] sentTextColor = ColorUtils.toRGB(currentSubDialogue.getNameColor());
			int[] sentTextMainColor = ColorUtils.toRGB(0xEEEEEE);
			int[] sentBackgroundColor = ColorUtils.toRGB(currentSubDialogue.getBackgroundColor());
			int textColor = ColorUtils.toHex(sentTextColor[0],sentTextColor[1],sentTextColor[2], currentSubDialogue.alpha);
			int textMainColor = ColorUtils.toHex(sentTextMainColor[0],sentTextMainColor[1],sentTextMainColor[2], currentSubDialogue.alpha);
			int backgroundColor = ColorUtils.toHex(50 ,50 , 50, (int) (currentSubDialogue.alpha * 0.5F));
			int borderColor = ColorUtils.toHex(sentBackgroundColor[0],sentBackgroundColor[1],sentBackgroundColor[2], (int) (currentSubDialogue.alpha));
			boolean isDone = currentSubDialogue.isDone();
			
			if (!MC.isGamePaused())
			{
				if (!isDone && currentSubDialogue.alpha < 150)
					currentSubDialogue.alpha += 5;
				else if (isDone && tickSubDialogueDone >= 100L && currentSubDialogue.alpha > 1)
					currentSubDialogue.alpha -= 5;
				if (tickSubDialogue % 2L == 0L && !isDone)
					currentSubDialogue.nextCharacter();
			}
			color(255,255,255,currentSubDialogue.alpha);
			drawRect(xStart, yStart, xStart + 300, yStart + 42, borderColor);
			drawRect(xStart + 2, yStart + 2, xStart + 298, yStart + 40, backgroundColor);
			if (tickSubDialogueDone < 100L)
			{
				List<String> content = StringUtil.wordwrap(MC.fontRenderer, currentSubDialogue.getContent(), 290);
				int size = content.size();
				int offset = Math.max(size - 4, 0);
				MC.fontRenderer.drawString(currentSubDialogue.getDisplayName(), xStart + 8, yStart - 10, textColor);
				for (int i = offset; i < size; i++)
					MC.fontRenderer.drawString(content.get(i), xStart + 4, yStart + 4 + (9 * (i - offset)), textMainColor);
			}
			if (tickSubDialogueDone >= 150L)
			{
				tickSubDialogue = tickSubDialogueDone = 0L;
				SUBDIALOGUE.remove(currentSubDialogue);
				currentSubDialogue = null;
				if (SUBDIALOGUE.isEmpty())
					displayingSubDialogue = false;
			}
			if (!MC.isGamePaused())
			{
				tickSubDialogue++;
				
				if (isDone)
					tickSubDialogueDone++;
			}
		}
	}
	
	public void addSubDialogue(SubDialogueMessage message)
	{
		SUBDIALOGUE.add(message);
		displayingSubDialogue = true;
	}
	
	public void forceAddSubDialogue(SubDialogueMessage message)
	{
		reset();
		addSubDialogue(message);
	}
	
	public void reset()
	{
		if (!SUBDIALOGUE.isEmpty())
			SUBDIALOGUE.clear();
		currentSubDialogue = null;
		displayingDialogue = displayingSubDialogue = false;
		tickSubDialogue = 0L;
	}
}
