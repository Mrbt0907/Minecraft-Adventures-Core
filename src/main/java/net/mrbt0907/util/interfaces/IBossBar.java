package net.mrbt0907.util.interfaces;

import net.minecraft.util.ResourceLocation;
import net.mrbt0907.util.MrbtAPI;

public interface IBossBar
{
	public static final ResourceLocation DEFAULT_BOSS_BAR = new ResourceLocation(MrbtAPI.MODID, "textures/bossbar/default.png");
	
	public boolean isDead();
	
	public default boolean canRenderBar() {return true;}
	
	public default boolean hasStamina() {return false;}
	
	public default boolean canColorHealth() {return true;}
	
	public default boolean canColorStamina() {return true;}
	
	public default boolean canShowDamage() {return true;}
	
	public default int getHealthBarStart() {return 0;}
	
	public default int getHealthBarLength() {return 208;}
	
	public default int getStaminaBarStart() {return 0;}
	
	public default int getStaminaBarLength() {return 105;}
	
	public default int getNameBarStart() {return -2;}
	
	public default int getHealthNameStart() {return 6;}
	
	public double getBarHealth();
	
	public double getBarMaxHealth();
	
	public default double getBarStamina() {return 0.0D;}
	
	public default double getBarMaxStamina() {return 0.0D;}
	
	public default ResourceLocation getBarTexture() {return DEFAULT_BOSS_BAR;}
	
	public default int[] getBarColor() {return new int[] {255, 0, 255, 255, 135, 0};}
	public String getBarName();
}
