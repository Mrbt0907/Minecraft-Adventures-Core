package net.endermanofdoom.mac.item;

import java.util.function.Predicate;

import javax.annotation.Nullable;

import net.endermanofdoom.mac.util.EnchantmentUtil;
import net.endermanofdoom.mac.util.math.Vec;
import net.endermanofdoom.mac.util.math.Vec3;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class ItemBowEX extends ItemBow
{
	public final Predicate<ItemStack> arrowPredicate;
	private final int enchantability;
	protected int chargeTime = 20;
	protected int arrowAmount = 1;
	protected float arrowDamage = 0.0F;
	protected float accuracy = 1.0F;
	protected float maxVelocity = 3.0F;
	protected boolean autoFire = false;
	protected SoundEvent sound = SoundEvents.ENTITY_ARROW_SHOOT;
	
	public ItemBowEX(Predicate<ItemStack> arrowType, int enchantability)
	{
		setCreativeTab(null);
		arrowPredicate = arrowType;
		this.enchantability = enchantability;
		addPropertyOverride(new ResourceLocation("pull"), new IItemPropertyGetter()
		{
			@SideOnly(Side.CLIENT)
			public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entity)
			{
				if (entity == null)
					return 0.0F;
				else
					return !(entity.getActiveItemStack().getItem() instanceof ItemBowEX) ? 0.0F : (float)(stack.getMaxItemUseDuration() - entity.getItemInUseCount()) / getChargeTime(stack);
			}
		});
		addPropertyOverride(new ResourceLocation("pulling"), new IItemPropertyGetter()
		{
			@SideOnly(Side.CLIENT)
			public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn)
			{
				return entityIn != null && entityIn.isHandActive() && entityIn.getActiveItemStack() == stack ? 1.0F : 0.0F;
			}
		});
	}
	
	public ItemBowEX setChargeTime(int value)
	{
		chargeTime = value;
		return this;
	}
	
	public ItemBowEX setArrowAmount(int value)
	{
		arrowAmount = value;
		return this;
	}
	
	public ItemBowEX setArrowAccuracy(float value)
	{
		accuracy = value;
		return this;
	}
	
	public ItemBowEX setArrowDamage(float value)
	{
		arrowDamage = value;
		return this;
	}
	
	public ItemBowEX setArrowVelocity(float value)
	{
		maxVelocity = value;
		return this;
	}
	
	public ItemBowEX setAutofire(boolean value)
	{
		autoFire = value;
		return this;
	}
	
	public ItemBowEX setFireSound(SoundEvent value)
	{
		sound = value;
		return this;
	}

	public Vec3 getShootPos(ItemStack stack, World world, EntityPlayer shooter, int arrowIndex)
	{
		Vec3 position = new Vec3(shooter);
		position.posY += shooter.eyeHeight;
		return position;
	}
	
	public Vec getShootRot(ItemStack stack, World world, EntityPlayer shooter, int arrowIndex)
	{
		return new Vec(shooter.rotationPitch, shooter.rotationYaw);
	}

	public abstract void onStartUse(ItemStack stack, World world, EntityPlayer shooter);
	public abstract void onTickUse(ItemStack stack, World world, EntityPlayer shooter, int timeLeft);
	public abstract void onStopUse(ItemStack stack, World world, EntityPlayer shooter, int timeLeft);
	public abstract void onShootPre(ItemStack stack, World world, EntityPlayer shooter, int timeLeft);
	public abstract void onShoot(ItemStack stack, World world, EntityPlayer shooter, EntityArrow arrow, int timeLeft, int arrowIndex);
	public abstract void onShootPost(ItemStack stack, World world, EntityPlayer shooter, int timeLeft);
	public abstract void onShootFail(ItemStack stack, World world, EntityPlayer shooter, int timeLeft);
	public abstract EntityArrow onCreateArrow(ItemStack stack, World world, EntityPlayer shooter, EntityArrow arrow, int timeLeft, int arrowIndex);

	
	protected void shoot(ItemStack stack, World world, EntityPlayer shooter, int timeLeft)
	{
		onShootPre(stack, world, shooter, timeLeft);
		boolean hasInfinity = shooter.capabilities.isCreativeMode || EnchantmentUtil.getEnchantmentLevel(Enchantments.INFINITY, stack) > 0;
		ItemStack itemstack = findAmmo(shooter);
		ItemArrow arrow = (ItemArrow)(itemstack.getItem() instanceof ItemArrow ? itemstack.getItem() : Items.ARROW);
		int i = getMaxItemUseDuration(stack) - timeLeft;
		i = net.minecraftforge.event.ForgeEventFactory.onArrowLoose(stack, world, shooter, i, !itemstack.isEmpty() || hasInfinity);
		if (i < 0) return;

		if (!itemstack.isEmpty() || hasInfinity)
		{
			float f = getVelocity(stack, i);
			int arrows = arrowAmount + EnchantmentUtil.getEnchantmentLevel("multishot", stack, true);
			int j = EnchantmentUtil.getEnchantmentLevel(Enchantments.POWER, stack);
			int k = EnchantmentUtil.getEnchantmentLevel(Enchantments.PUNCH, stack);
			boolean l = EnchantmentUtil.getEnchantmentLevel(Enchantments.FLAME, stack) > 0;
			if (f >= 0.1F && arrowAmount > 0)
			{
				stack.damageItem(1, shooter);
				for (int ii = 0; ii < arrows; ii++)
				{
					if (!world.isRemote)
					{
						Vec3 position = getShootPos(itemstack, world, shooter, ii);
						Vec rotation = getShootRot(itemstack, world, shooter, ii);
						EntityArrow entityarrow = onCreateArrow(itemstack, world, shooter, arrow.createArrow(world, itemstack, shooter), timeLeft, ii);
						entityarrow.setPosition(position.posX, position.posY, position.posZ);
						entityarrow.setDamage(entityarrow.getDamage() + arrowDamage);
						entityarrow = customizeArrow(entityarrow);
						entityarrow.getEntityData().setBoolean("multiShot", true);
						entityarrow.shoot(shooter, (float) rotation.posX, (float) rotation.posZ, 0.0F, f * maxVelocity, accuracy * arrows);

						if (f == 1.0F)
							entityarrow.setIsCritical(true);
						if (j > 0)
							entityarrow.setDamage(entityarrow.getDamage() + (double)j * 0.5D + 0.5D);
						if (k > 0)
							entityarrow.setKnockbackStrength(k);
						if (l)
							entityarrow.setFire(100);
						if (hasInfinity || shooter.capabilities.isCreativeMode && (arrow == Items.SPECTRAL_ARROW || arrow == Items.TIPPED_ARROW))
							entityarrow.pickupStatus = EntityArrow.PickupStatus.CREATIVE_ONLY;

						world.spawnEntity(entityarrow);
						onShoot(stack, world, shooter, entityarrow, timeLeft, ii);
					}
					else
						onShoot(stack, world, shooter, null, timeLeft, ii);
					
					if (ii == 0)
						world.playSound((EntityPlayer)null, shooter.posX, shooter.posY, shooter.posZ, sound, SoundCategory.PLAYERS, 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + f * 0.5F);

					if (!hasInfinity)
					{
						itemstack.shrink(1);

						if (itemstack.isEmpty())
						{
							shooter.inventory.deleteStack(itemstack);
							itemstack = findAmmo(shooter);
							if (itemstack.isEmpty())
								break;
							arrow = (ItemArrow)(itemstack.getItem() instanceof ItemArrow ? itemstack.getItem() : Items.ARROW);
						}
					}

					shooter.addStat(StatList.getObjectUseStats(this));
				}

				onShootPost(stack, world, shooter, timeLeft);
			}
			else
				onShootFail(stack, world, shooter, timeLeft);
		}
		else
			onShootFail(stack, world, shooter, timeLeft);
	}
	
	@Override
	public void onPlayerStoppedUsing(ItemStack stack, World world, EntityLivingBase shooter, int timeLeft)
	{
		if (!(shooter instanceof EntityPlayer)) return;
		shoot(stack, world, (EntityPlayer) shooter, timeLeft);
		onStopUse(stack, shooter.world, (EntityPlayer) shooter, timeLeft);
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer shooter, EnumHand hand)
    {
		ActionResult<ItemStack> result = super.onItemRightClick(world, shooter, hand);
		if (!result.getType().equals(EnumActionResult.FAIL))
			onStartUse(result.getResult(), world, shooter);
		return result;
    }
	
	@Override
	public void onUsingTick(ItemStack stack, EntityLivingBase shooter, int timeLeft)
	{
		if (!(shooter instanceof EntityPlayer)) return;
		onTickUse(stack, shooter.world, (EntityPlayer) shooter, timeLeft);
		if (!autoFire) return;
		int maxUseTime = stack.getMaxItemUseDuration();
		if (maxUseTime - timeLeft > getChargeTime(stack))
		{
			shoot(stack, shooter.world, (EntityPlayer) shooter, 0);
			shooter.activeItemStackUseCount = maxUseTime;
		}
	}
	
	public float getVelocity(ItemStack stack, int charge)
	{
		float f = (float)charge / getChargeTime(stack);
		f = (f * f + f * 2.0F) / 3.0F;

		if (f > 1.0F)
			f = 1.0F;

		return f;
	}
	
	public int getChargeTime(ItemStack stack)
	{
		return chargeTime;
	}
	
	@Override
	public int getMaxItemUseDuration(ItemStack stack)
	{
		return 72000;
	}
	
	@Override
	protected boolean isArrow(ItemStack stack)
	{
		return stack.getItem() instanceof ItemArrow && (arrowPredicate == null ? true : arrowPredicate.test(stack));
	}

	@Override
	public int getItemEnchantability()
	{
		return enchantability;
	}
}
