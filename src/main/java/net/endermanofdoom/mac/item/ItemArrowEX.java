package net.endermanofdoom.mac.item;

import net.endermanofdoom.mac.entity.EntityArrowEX;
import net.endermanofdoom.mac.interfaces.IArrowBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class ItemArrowEX extends ItemArrow
{
	public ItemArrowEX() {}
	public abstract EntityArrowEX onArrowCreate(World world, ItemStack stack, EntityLivingBase shooter, EntityArrowEX arrow);
	public abstract void onArrowTick(World world, EntityLivingBase shooter, EntityArrowEX arrow);
	public abstract void onArrowTickAir(World world, EntityLivingBase shooter, EntityArrowEX arrow);
	public abstract void onArrowTickGround(World world, EntityLivingBase shooter, EntityArrowEX arrow);
	public abstract void onArrowTickWater(World world, EntityLivingBase shooter, EntityArrowEX arrow);
	public abstract void onArrowHit(World world, EntityLivingBase shooter, Entity victim, EntityArrowEX arrow);
	public abstract void onArrowHitBlock(World world, EntityLivingBase shooter, RayTraceResult raytrace, EntityArrowEX arrow);
	public abstract void onArrowStop(World world, EntityLivingBase shooter, RayTraceResult raytrace, EntityArrowEX arrow);
	
	public double getBaseDamage()
	{
		return 2.0D;
	}
	public float getBaseVelocity()
	{
		return 0.0F;
	}
	public float getBaseAccuracy()
	{
		return 0.0F;
	}
	public IArrowBehavior getCustomBehavior()
	{
		return null;
	}
	
	public boolean canCollide(World world, Entity entity, EntityArrowEX arrow)
	{
		return true;
	}
	public boolean canCollide(World world, BlockPos position, EntityArrowEX arrow)
	{
		return true;
	}
	
	@SideOnly(Side.CLIENT)
	public ResourceLocation getArrowTexture(EntityArrowEX arrow)
	{
		return net.minecraft.client.renderer.entity.RenderTippedArrow.RES_ARROW;
	}
	
	@Override
	public EntityArrow createArrow(World world, ItemStack stack, EntityLivingBase shooter)
	{
		EntityArrowEX arrow = onArrowCreate(world, stack, shooter, new EntityArrowEX(world, this, shooter));
		arrow.setDamage(getBaseDamage());
		arrow.setBaseVelocity(getBaseVelocity());
		arrow.setBaseAccuracy(getBaseAccuracy());
		return arrow;
	}
}
