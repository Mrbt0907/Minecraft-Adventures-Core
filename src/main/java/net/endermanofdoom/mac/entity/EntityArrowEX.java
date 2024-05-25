package net.endermanofdoom.mac.entity;

import net.endermanofdoom.mac.interfaces.IArrowBehavior;
import net.endermanofdoom.mac.item.ItemArrowEX;
import net.endermanofdoom.mac.util.math.Maths;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketChangeGameState;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
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
			item.onArrowHit(world, shootingEntity instanceof EntityLivingBase ? (EntityLivingBase) shootingEntity : null, victim, this);
		if (customBehavior != null)
			customBehavior.onArrowHit(world, shootingEntity instanceof EntityLivingBase ? (EntityLivingBase) shootingEntity : null, victim, this);
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
						item.onArrowHitBlock(world, shooter, result, this);
						if (customBehavior != null)
							customBehavior.onArrowHitBlock(world, shooter, result, this);
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
		onHitPost(result);
		if (item != null)
			item.onArrowStop(world, shooter, result, this);
		if (customBehavior != null)
			customBehavior.onArrowStop(world, shooter, result, this);
	}
	
	protected void onHitPost(RayTraceResult raytrace)
	{
		Entity victim = raytrace.entityHit;
		EntityLivingBase shooter = shootingEntity instanceof EntityLivingBase ? (EntityLivingBase) shootingEntity : null;
		
		if (victim != null)
		{
			double speed = Maths.speedSq(motionX, motionY, motionZ);
			double damage = Math.ceil(speed * this.damage);
			if (getIsCritical())
				speed += Maths.random(speed) + 2.0F;
			
			boolean attacked = customBehavior != null ? customBehavior.onDamageEntity(world, shooter, victim, raytrace, this, (float) damage, (float) speed) : true;
			attacked = attacked == true ? item != null && item.onDamageEntity(world, shooter, victim, raytrace, this, (float) damage, (float) speed) : false;
			
			if (attacked)
			{
				if (victim instanceof EntityLivingBase)
				{
					EntityLivingBase victimLiving = (EntityLivingBase)victim;

					if (!world.isRemote)
						victimLiving.setArrowCountInEntity(victimLiving.getArrowCountInEntity() + 1);

					if (knockbackStrength > 0)
					{
						double f1 = Maths.speedSq(motionX, motionZ);

						if (f1 > 0.0F)
							victimLiving.addVelocity(motionX * (double)knockbackStrength * 0.6000000238418579D / (double)f1, 0.1D, motionZ * (double)knockbackStrength * 0.6000000238418579D / (double)f1);
					}

					if (shooter != null)
					{
						EnchantmentHelper.applyThornEnchantments(victimLiving, shooter);
						EnchantmentHelper.applyArthropodEnchantments(shooter, victimLiving);
					}

					arrowHit(victimLiving);

					if (victimLiving instanceof EntityPlayer && shootingEntity instanceof EntityPlayerMP && victimLiving != shootingEntity)
						((EntityPlayerMP)shootingEntity).connection.sendPacket(new SPacketChangeGameState(6, 0.0F));
				}
				
				playSound(SoundEvents.ENTITY_ARROW_HIT, 1.0F, 1.2F / (rand.nextFloat() * 0.2F + 0.9F));
				boolean pierced = customBehavior != null ? customBehavior.onPierce(world, shooter, victim, raytrace, this) : true;
				pierced = pierced == true ? item != null && item.onPierce(world, shooter, victim, raytrace, this) : false;
				
				if (!pierced)
					setDead();
			}
			else
			{
				motionX *= -0.10000000149011612D;
				motionY *= -0.10000000149011612D;
				motionZ *= -0.10000000149011612D;
				rotationYaw += 180.0F;
				prevRotationYaw += 180.0F;
				ticksInAir = 0;

				if (!world.isRemote && motionX * motionX + motionY * motionY + motionZ * motionZ < 0.0010000000474974513D)
				{
					if (pickupStatus == EntityArrow.PickupStatus.ALLOWED)
						entityDropItem(getArrowStack(), 0.1F);
					setDead();
				}
			}
		}
		else
		{
			BlockPos blockpos = raytrace.getBlockPos();
			IBlockState iblockstate = world.getBlockState(blockpos);
			boolean pierced = customBehavior != null ? customBehavior.onPierceBlock(world, shooter, iblockstate, blockpos, raytrace, this) : true;
			pierced = pierced == true ? item != null && item.onPierceBlock(world, shooter, iblockstate, blockpos, raytrace, this) : false;
			if (!pierced)
			{
				xTile = blockpos.getX();
				yTile = blockpos.getY();
				zTile = blockpos.getZ();
				inTile = iblockstate.getBlock();
				inData = inTile.getMetaFromState(iblockstate);
				motionX = (double)(raytrace.hitVec.x - posX);
				motionY = (double)(raytrace.hitVec.y - posY);
				motionZ = (double)(raytrace.hitVec.z - posZ);
				double f2 = MathHelper.sqrt(motionX * motionX + motionY * motionY + motionZ * motionZ);
				posX -= motionX / f2 * 0.05000000074505806D;
				posY -= motionY / f2 * 0.05000000074505806D;
				posZ -= motionZ / f2 * 0.05000000074505806D;
				playSound(SoundEvents.ENTITY_ARROW_HIT, 1.0F, 1.2F / (rand.nextFloat() * 0.2F + 0.9F));
				inGround = true;
				arrowShake = 7;
				setIsCritical(false);

				if (iblockstate.getMaterial() != Material.AIR)
					inTile.onEntityCollidedWithBlock(world, blockpos, iblockstate, this);
			}
		}
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