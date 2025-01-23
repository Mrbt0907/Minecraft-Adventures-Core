package net.mrbt0907.util.mixin;

import net.minecraft.client.renderer.GlStateManager;
import net.mrbt0907.util.util.math.Maths;

public class CameraHandler
{
	private static float cameraShake;
	public static float cameraShakeRate = 0.01F;
	
	public static void shakeCamera(float magnitude)
	{
		shakeCamera(magnitude, false);
	}
	
	public static void shakeCamera(float magnitude, boolean setValue)
	{
		if (setValue)
			cameraShake = magnitude;
		else
			cameraShake = Math.max(cameraShake, magnitude);
		
		if (cameraShake < 0.0F)
			cameraShake = 0.0F;
	}
	
	public static void applyTransform(float partialTicks)
	{
		if (cameraShake > 0.0F)
		{
			GlStateManager.translate(Maths.random(-cameraShake, cameraShake), Maths.random(-cameraShake, cameraShake), Maths.random(-cameraShake, cameraShake));
			cameraShake = Maths.adjust(cameraShake, 0.0F, cameraShakeRate);
		}
	}
}
