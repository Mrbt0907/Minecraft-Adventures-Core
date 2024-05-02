package net.endermanofdoom.mac.client.gui;

import org.lwjgl.opengl.GL11;
import net.endermanofdoom.mac.interfaces.IBossBar;
import net.endermanofdoom.mac.util.StringUtil;
import net.endermanofdoom.mac.util.math.Maths;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiBossBar extends AbstractGui
{
	public void renderNewBar(int offset, GuiBossBarEntry entry)
	{
		ScaledResolution res = new ScaledResolution(MC);
		
		GL11.glPushMatrix();
		GL11.glEnable(2977);
		GL11.glEnable(3042);
		GL11.glBlendFunc(770, 771);
		IBossBar ent = entry.getEntry();
		String output;
		int shakeX = (int)(!entry.canShowDamage() || !entry.damaged() ? 0 : Maths.random(-5, 5) * MathHelper.clamp((float)(entry.getLastDamage() / (entry.getMax(0) / 100.0F)), 0.1F, 1.0F));
		int shakeY = (int)(!entry.canShowDamage() || !entry.damaged() ? 0 : Maths.random(-5, 5) * MathHelper.clamp((float)(entry.getLastDamage() / (entry.getMax(0) / 100.0F)), 0.1F, 1.0F));
		int width = (res.getScaledWidth() / 2) - 104 + shakeX;
		int height = (offset * 30) + shakeY + 5;
		MC.getTextureManager().bindTexture(entry.getTexture());
		drawTexturedModalRect(width, height, 0, 0, 256, 51);
		if (entry.getIndicator(0) > entry.getCurrent(0))
		{
			color();
			drawBar(width, height, ent.getHealthBarStart(), 102, ent.getHealthBarLength(), 51, (float) entry.getIndicatorPerc(0));
		}

		if (entry.getCurrent(0) > 0.0D)
		{
			if (ent.canColorHealth()) color(entry.rgb[0], entry.rgb[1], entry.rgb[2]);
			drawBar(width, height, ent.getHealthBarStart(), 51, ent.getHealthBarLength(), 51, (float) entry.getCurrentPerc(0));
			color();
		}
		
		if (entry.canRenderStamina() && entry.getIndicator(1) > entry.getCurrent(2))
		{
			color();
			drawMirroredBar(width, height, ent.getStaminaBarStart(), 204, ent.getStaminaBarLength(), 51, (float)entry.getIndicatorPerc(2));
		}

		if (entry.canRenderStamina() && entry.getCurrent(2) > 0.0F)
		{
			if (ent.canColorStamina()) color(entry.rgb[3], entry.rgb[4], entry.rgb[5]);
			drawMirroredBar(width, height, ent.getStaminaBarStart(), 153, ent.getStaminaBarLength(), 51, (float)entry.getCurrentPerc(2));
			color();
		}

		output = entry.getName();
		MC.fontRenderer.drawStringWithShadow(output, (width + ent.getHealthBarLength() * 0.5F) - (MC.fontRenderer.getStringWidth(output) * 0.5F), height + ent.getNameBarStart(), 0xFFC0C0C0);
		output = StringUtil.parseDouble((float)entry.getCurrent(0));
		MC.fontRenderer.drawStringWithShadow(output, (width + ent.getHealthBarLength() * 0.5F) - (MC.fontRenderer.getStringWidth(output) * 0.5F), height + ent.getHealthNameStart(), 0xFFC0C0C0);
		if (ent.canShowDamage() && entry.getLastTotalDamage() != 0.0D)
		{
			output = StringUtil.parseDouble((float)entry.getLastTotalDamage());
			MC.fontRenderer.drawStringWithShadow(entry.getLastTotalDamage() >= entry.getMax(0) ? entry.getLastTotalDamage() >= entry.getMax(0) + -(entry.getMax(0) * 0.5D) ? "Overkill" : "Killed" : output, width + 200 - (MC.fontRenderer.getStringWidth(output) / 2) + (entry.getHurtTime() / 4), height + 4, 0xFFC02000);
		}

		color();
		GL11.glDisable(3042);
		GL11.glPopMatrix();
	}
}
