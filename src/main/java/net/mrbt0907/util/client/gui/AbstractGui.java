package net.mrbt0907.util.client.gui;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.mrbt0907.util.util.math.Maths;

public abstract class AbstractGui extends Gui
{
	protected static final Minecraft MC = Minecraft.getMinecraft();

	protected void drawBar(int x, int y, int u, int v, int width, int height, int cu, int cv, float amount)
	{
		amount = Maths.clamp(amount, 0.0F, 1.0F);
		drawModalRectWithCustomSizedTexture(x, y, u, v, (int)(width * amount), height, cu, cv);
	}

	protected void drawMirroredBar(int x, int y, int u, int v, int width, int height, int cu, int cv, float amount)
	{
		amount = Maths.clamp(amount, 0.0F, 1.0F);
		drawModalRectWithCustomSizedTexture(x + (int)(width - (width * amount)), y, u + (int)(width - (width * amount)), v, (int)(width * amount), height, cu, cv);
		drawModalRectWithCustomSizedTexture(x + width, y, width, v, (int)(width * amount), height, cu, cv);
	}
	
	protected void drawBar(int x, int y, int u, int v, int width, int height, float amount)
	{
		amount = Maths.clamp(amount, 0.0F, 1.0F);
		drawTexturedModalRect(x, y, u, v, (int)(width * amount), height);
	}

	protected void drawMirroredBar(int x, int y, int u, int v, int width, int height, float amount)
	{
		amount = Maths.clamp(amount, 0.0F, 1.0F);
		drawTexturedModalRect(x + (int)(width - (width * amount)), y, u + (int)(width - (width * amount)), v, (int)(width * amount), height);
		drawTexturedModalRect(x + width, y, width, v, (int)(width * amount), height);
	}

	protected void color(float... values)
	{
		int index = 0;
		float[] rgba = {255.0F, 255.0F, 255.0F, 255.0F};
		for (int i = 0; i < values.length; i++)
		if (index < 4)
		{
			rgba[index] = values[i];
			index ++;
		}

		else
		break;
		GL11.glColor4f(Maths.clamp(rgba[0] / 255.0F, 0.0F, 1.0F), Maths.clamp(rgba[1] / 255.0F, 0.0F, 1.0F), Maths.clamp(rgba[2] / 255.0F, 0.0F, 1.0F), Maths.clamp(rgba[3] / 255.0F, 0.0F, 1.0F));
	}
}
