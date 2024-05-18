package net.endermanofdoom.mac.entity;

import net.endermanofdoom.mac.interfaces.IArrowBehavior;
import net.endermanofdoom.mac.item.ItemArrowEX;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityArrowEX extends EntityArrow
{
	protected IArrowBehavior customBehavior;
	protected ItemArrowEX item;
	protected float baseVelocity;
	protected float baseAccuracy;
	
	public EntityArrowEX(World worldIn)
	{
		super(worldIn);
	}

	public EntityArrowEX(World world, ItemArrowEX arrowItem, double x, double y, double z)
	{
		super(world, x, y ,z);
		item = arrowItem;
		customBehavior = item.getCustomBehavior();
	}
	
	public EntityArrowEX(World world, ItemArrowEX arrowItem, EntityLivingBase shooter)
	{
		super(world, shooter);
		item = arrowItem;
		customBehavior = item.getCustomBehavior();
	}
	
	@Override
	public void writeEntityToNBT(NBTTagCompound nbt)
	{
		super.writeEntityToNBT(nbt);
		if (item != null)
		{
			nbt.setString("arrowItem", item.getClass().getCanonicalName());
		}
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt)
	{
		super.readEntityFromNBT(nbt);
		if (item == null && nbt.hasKey("arrowItem"))
		{
			try
			{
				item = (ItemArrowEX) Class.forName(nbt.getString("arrowItem")).getConstructor().newInstance();
				if (customBehavior == null)
					customBehavior = item.getCustomBehavior();
			} catch (Exception e) {};
		}
	}
	
	public EntityArrowEX setBaseVelocity(float value)
	{
		baseVelocity = value;
		return this;
	}
	
	public EntityArrowEX setBaseAccuracy(float value)
	{
		baseAccuracy = value;
		return this;
	}
	
	@Override
	public void onUpdate()
	{
		super.onUpdate();
		if (item != null)
		{
			EntityLivingBase shooter = shootingEntity instanceof EntityLivingBase ? (EntityLivingBase) shootingEntity : null;
			item.onArrowTick(world, shooter, this);
			if (inGround)
				item.onArrowTickGround(world, shooter, this);
			else if (inWater)
				item.onArrowTickWater(world, shooter, this);
			else
				item.onArrowTickAir(world, shooter, this);
		}
		
		if (customBehavior != null)
		{
			EntityLivingBase shooter = shootingEntity instanceof EntityLivingBase ? (EntityLivingBase) shootingEntity : null;
			customBehavior.onArrowTick(world, shooter, this);
			if (inGround)
				customBehavior.onArrowTickGround(world, shooter, this);
			else if (inWater)
				customBehavior.onArrowTickWater(world, shooter, this);
			else
				customBehavior.onArrowTickAir(world, shooter, this);
		}
	}
	
	@Override
	public void shoot(Entity shooter, float pitch, float yaw, float p_184547_4_, float velocity, float inaccuracy)
    {
		super.shoot(shooter, pitch, yaw, p_184547_4_, velocity + baseVelocity, inaccuracy + baseAccuracy);
    }
	
	@Override
	public void shoot(double x, double y, double z, float velocity, float inaccuracy)
    {
		super.shoot(x, y, z, velocity + baseVelocity, inaccuracy + baseAccuracy);
    }
	
	@Override
	protected void arrowHit(EntityLivingBase victim)
    {
		super.arrowHit(victim);
		if (item != null)
			item.onArrowHit(world, shootingEntity instanceof EntityLivingBase ? (EntityLivingBase) shootingEntity : null, victim, null);
		if (customBehavior != null)
			customBehavior.onArrowHit(world, shootingEntity instanceof EntityLivingBase ? (EntityLivingBase) shootingEntity : null, victim, null);
    }
	
	@Override
	protected Entity findEntityOnPath(Vec3d start, Vec3d end)
    {
		Entity victim = super.findEntityOnPath(start, end);
		return item == null || item.canCollide(world, victim, this) ? victim : null;
    }
	
	@Override
	protected void onHit(RayTraceResult result)
    {
		EntityLivingBase shooter = shootingEntity instanceof EntityLivingBase ? (EntityLivingBase) shootingEntity : null;
		if (item != null)
			switch(result.typeOfHit)
			{	
				case BLOCK:
					if (item.canCollide(world, result.getBlockPos(), this))
					{
						item.onArrowHitBlock(world, shooter, result.getBlockPos(), this);
						if (customBehavior != null)
							customBehavior.onArrowHitBlock(world, shooter, result.getBlockPos(), this);
					}
					else
						return;
					break;
				case ENTITY:
					if (item.canCollide(world, result.entityHit, this))
					{
						item.onArrowHit(world, shooter, result.entityHit, this);
						if (customBehavior != null)
							customBehavior.onArrowHit(world, shooter, result.entityHit, this);
					}
					else
						return;
					break;
				default:
			}
		super.onHit(result);
		if (item != null)
			item.onArrowStop(world, shooter, this);
		if (customBehavior != null)
			customBehavior.onArrowStop(world, shooter, this);
    }
	
	@Override
	protected ItemStack getArrowStack()
	{
		return item != null ? item.getDefaultInstance() : Items.ARROW.getDefaultInstance();
	}

	@SideOnly(Side.CLIENT)
	public ResourceLocation getTexture()
	{
		return item == null ? net.minecraft.client.renderer.entity.RenderTippedArrow.RES_ARROW : item.getArrowTexture(this);
	}
}