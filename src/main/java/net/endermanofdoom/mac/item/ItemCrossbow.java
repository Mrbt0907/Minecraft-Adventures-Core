package net.endermanofdoom.mac.item;

import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;

import net.endermanofdoom.mac.MACCore;
import net.endermanofdoom.mac.capabilities.CapabilityCrossbow;
import net.endermanofdoom.mac.util.EnchantmentUtil;
import net.endermanofdoom.mac.util.math.Maths;
import net.endermanofdoom.mac.util.math.Vec;
import net.endermanofdoom.mac.util.math.Vec3;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
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

public abstract class ItemCrossbow extends ItemBow
{
	public final Predicate<ItemStack> arrowPredicate;
	private final int enchantability;
	protected int chargeTime = 20;
	protected int arrowAmount = 1;
	protected float arrowDamage = 0.0F;
	protected float accuracy = 1.0F;
	protected float maxVelocity = 3.5F;
	protected boolean autoFire = false;
	protected int maxAmmo = 1;
	protected int reloadTime = 20;
	protected SoundEvent sound = SoundEvents.ENTITY_ARROW_SHOOT;
	protected SoundEvent soundEmpty = SoundEvents.BLOCK_DISPENSER_FAIL;
	protected SoundEvent soundReload = SoundEvents.BLOCK_TRIPWIRE_ATTACH;
	protected SoundEvent soundLoaded = SoundEvents.BLOCK_TRIPWIRE_CLICK_ON;
	
	public ItemCrossbow(Predicate<ItemStack> arrowType, int enchantability)
	{
		setCreativeTab(null);
		arrowPredicate = arrowType;
		this.enchantability = enchantability;
		addPropertyOverride(new ResourceLocation("pull"), new IItemPropertyGetter()
		{
			@SideOnly(Side.CLIENT)
			public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entity)
			{
				
				if (entity == null || !stack.hasCapability(CapabilityCrossbow.INSTANCE, null))
					return 0.0F;
				else
				{
					CapabilityCrossbow capability = stack.getCapability(CapabilityCrossbow.INSTANCE, null);
					return ItemUtils.loadNBT(stack).getBoolean("isLoaded") ? 1.0F : (float)capability.reloadTime / reloadTime;
				}
			}
		});
		addPropertyOverride(new ResourceLocation("pulling"), new IItemPropertyGetter()
		{
			@SideOnly(Side.CLIENT)
			public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn)
			{
				return entityIn != null && entityIn.isHandActive() && entityIn.getActiveItemStack() == stack ? 1.0F : ItemUtils.loadNBT(stack).getBoolean("isLoaded") ? 1.0F : 0.0F;
			}
		});
	}
	
	public ItemCrossbow setChargeTime(int value)
	{
		chargeTime = value;
		return this;
	}
	
	public ItemCrossbow setArrowAmount(int value)
	{
		arrowAmount = value;
		return this;
	}
	
	public ItemCrossbow setArrowAccuracy(float value)
	{
		accuracy = value;
		return this;
	}
	
	public ItemCrossbow setArrowDamage(float value)
	{
		arrowDamage = value;
		return this;
	}
	
	public ItemCrossbow setArrowVelocity(float value)
	{
		maxVelocity = value;
		return this;
	}
	
	public ItemCrossbow setAutofire(boolean value)
	{
		autoFire = value;
		return this;
	}
	
	public ItemCrossbow setFireSound(SoundEvent value)
	{
		sound = value;
		return this;
	}
	
	public ItemCrossbow setFireEmptySound(SoundEvent value)
	{
		soundEmpty = value;
		return this;
	}
	
	public ItemCrossbow setReloadSound(SoundEvent value)
	{
		soundReload = value;
		return this;
	}
	
	public ItemCrossbow setLoadedSound(SoundEvent value)
	{
		soundLoaded = value;
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

	public abstract void onStartUse(ItemStack stack, World world, EntityPlayer shooter, CapabilityCrossbow capability);
	public abstract void onTickUse(ItemStack stack, World world, EntityPlayer shooter, CapabilityCrossbow capability, int timeLeft);
	public abstract void onStopUse(ItemStack stack, World world, EntityPlayer shooter, CapabilityCrossbow capability, int timeLeft);
	public abstract void onShootPre(ItemStack stack, World world, EntityPlayer shooter, CapabilityCrossbow capability, int timeLeft);
	public abstract void onShoot(ItemStack stack, World world, EntityPlayer shooter, CapabilityCrossbow capability, EntityArrow arrow, int timeLeft, int arrowIndex);
	public abstract void onShootPost(ItemStack stack, World world, EntityPlayer shooter, CapabilityCrossbow capability, int timeLeft);
	public abstract void onShootFail(ItemStack stack, World world, EntityPlayer shooter, CapabilityCrossbow capability, int timeLeft);
	public abstract EntityArrow onCreateArrow(ItemStack stack, World world, EntityPlayer shooter, CapabilityCrossbow capability, EntityArrow arrow, int timeLeft, int arrowIndex);
	
	protected void shoot(ItemStack stack, World world, EntityPlayer shooter, int timeLeft)
	{
		if (!stack.hasCapability(CapabilityCrossbow.INSTANCE, null)) return;
		CapabilityCrossbow capability = stack.getCapability(CapabilityCrossbow.INSTANCE, null);
		onShootPre(stack, world, shooter, capability, timeLeft);
		boolean hasInfinity = shooter.capabilities.isCreativeMode || EnchantmentUtil.getEnchantmentLevel(Enchantments.INFINITY, stack) > 0;
		
		if (!capability.isMagazineEmpty())
		{
			ItemStack itemstack = capability.fromMagazine();
			ItemArrow arrow = (ItemArrow)(itemstack.getItem() instanceof ItemArrow ? itemstack.getItem() : Items.ARROW);
			int arrows = arrowAmount + EnchantmentUtil.getEnchantmentLevel("multishot", stack, true);
			int j = EnchantmentUtil.getEnchantmentLevel(Enchantments.POWER, stack);
			int k = EnchantmentUtil.getEnchantmentLevel(Enchantments.PUNCH, stack);
			boolean l = EnchantmentUtil.getEnchantmentLevel(Enchantments.FLAME, stack) > 0;
			if (arrowAmount > 0)
			{
				if (!hasInfinity && !world.isRemote && stack.getItemDamage() + 1 > stack.getMaxDamage())
				{
					if (!itemstack.isEmpty())
						capability.toMagazine(itemstack, !hasInfinity);
					List<ItemStack> ammunition = capability.getAmmo();
					ammunition.forEach(entry -> world.spawnEntity(new EntityItem(world, shooter.posX + Maths.random(-1.0D, 1.0D), shooter.posY + shooter.getEyeHeight() + Maths.random(-1.0D, 1.0D), shooter.posZ + Maths.random(-1.0D, 1.0D), entry)));
					capability.unloadMagazine();
					stack.damageItem(1, shooter);
					return;
				}
				else
					stack.damageItem(1, shooter);
				for (int ii = 0; ii < arrows; ii++)
				{
					if (!world.isRemote)
					{
						Vec3 position = getShootPos(itemstack, world, shooter, ii);
						Vec rotation = getShootRot(itemstack, world, shooter, ii);
						EntityArrow entityarrow = onCreateArrow(itemstack, world, shooter, capability, arrow.createArrow(world, itemstack, shooter), timeLeft, ii);
						entityarrow.setPosition(position.posX, position.posY, position.posZ);
						entityarrow.setDamage(entityarrow.getDamage() + arrowDamage);
						entityarrow = customizeArrow(entityarrow);
						entityarrow.getEntityData().setBoolean("multiShot", true);
						entityarrow.shoot(shooter, (float) rotation.posX, (float) rotation.posZ, 0.0F, maxVelocity, accuracy * arrows);

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
						onShoot(stack, world, shooter, capability, entityarrow, timeLeft, ii);
					}
					else
						onShoot(stack, world, shooter, capability, null, timeLeft, ii);
					
					if (ii == 0)
						world.playSound((EntityPlayer)null, shooter.posX, shooter.posY, shooter.posZ, sound, SoundCategory.PLAYERS, 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + 0.5F);

					itemstack.shrink(1);
					if (itemstack.isEmpty())
					{
						itemstack = capability.fromMagazine();
						if (itemstack.isEmpty())
							break;
						arrow = (ItemArrow)(itemstack.getItem() instanceof ItemArrow ? itemstack.getItem() : Items.ARROW);
					}
				}
				
				shooter.addStat(StatList.getObjectUseStats(this));
			}
			if (!itemstack.isEmpty())
				capability.toMagazine(itemstack, true);
			capability.markDirty((EntityPlayer) shooter, "inventory", "field_71071_by", ((EntityPlayer)shooter).inventory.getSlotFor(stack));
			onShootPost(stack, world, shooter, capability, timeLeft);
		}
		else
			onShootFail(stack, world, shooter, capability, timeLeft);
	}
	
	@Override
	public void onPlayerStoppedUsing(ItemStack stack, World world, EntityLivingBase shooter, int timeLeft)
	{
		if (!(shooter instanceof EntityPlayer) || !stack.hasCapability(CapabilityCrossbow.INSTANCE, null)) return;
		CapabilityCrossbow capability = stack.getCapability(CapabilityCrossbow.INSTANCE, null);
		NBTTagCompound nbt = ItemUtils.loadNBT(stack);
		
		if (!nbt.getBoolean("isLoaded") && stack.getMaxItemUseDuration() - timeLeft > reloadTime)
		{
			if (((EntityPlayer)shooter).capabilities.isCreativeMode && findAmmo((EntityPlayer) shooter).isEmpty())
				capability.toMagazine(new ItemStack(Items.ARROW, 64), false);
			else
				capability.loadMagazine(((EntityPlayer)shooter).inventory, !((EntityPlayer)shooter).capabilities.isCreativeMode && EnchantmentUtil.getEnchantmentLevel(Enchantments.INFINITY, stack) < 1);
			capability.markDirty((EntityPlayer) shooter, "inventory", "field_71071_by", ((EntityPlayer)shooter).inventory.getSlotFor(stack));
			if (!capability.isMagazineEmpty())
				world.playSound((EntityPlayer)null, shooter.posX, shooter.posY, shooter.posZ, soundLoaded, SoundCategory.PLAYERS, 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + 0.5F);
		}
		if (!autoFire)
			nbt.setBoolean("fired", false);
		
		nbt.setBoolean("isLoaded", !capability.isMagazineEmpty());
		
		ItemUtils.saveNBT(stack, nbt);
		onStopUse(stack, shooter.world, (EntityPlayer) shooter, capability, timeLeft);
	}
	
	@Override
	public void onUsingTick(ItemStack stack, EntityLivingBase shooter, int timeLeft)
	{
		if (!(shooter instanceof EntityPlayer) || !stack.hasCapability(CapabilityCrossbow.INSTANCE, null)) return;
		CapabilityCrossbow capability = stack.getCapability(CapabilityCrossbow.INSTANCE, null);
		NBTTagCompound nbt = ItemUtils.loadNBT(stack);
		onTickUse(stack, shooter.world, (EntityPlayer) shooter, capability, timeLeft);

		if (shooter.ticksExisted < nbt.getInteger("ticksExisted") - getChargeTime(stack))
		{
			nbt.setInteger("ticksExisted", shooter.ticksExisted + getChargeTime(stack));
			ItemUtils.saveNBT(stack, nbt);
			nbt = ItemUtils.loadNBT(stack);
		}
		
		if (nbt.getBoolean("isLoaded"))
		{
			int maxUseTime = stack.getMaxItemUseDuration();
			if (autoFire || (!capability.isMagazineEmpty() && !nbt.getBoolean("fired")))
			{
				if (((maxUseTime == timeLeft || maxUseTime - timeLeft > getChargeTime(stack))) && nbt.getInteger("ticksExisted") < shooter.ticksExisted)
				{
					if (autoFire || !nbt.getBoolean("fired"))
					{
						shoot(stack, shooter.world, (EntityPlayer) shooter, 0);
						shooter.activeItemStackUseCount = maxUseTime - 1;
						nbt.setInteger("ticksExisted", shooter.ticksExisted + getChargeTime(stack));
						if (!autoFire)
						{
							nbt.setBoolean("fired", true);
						}
						ItemUtils.saveNBT(stack, nbt);
					}
					else
					{
						shooter.world.playSound((EntityPlayer)null, shooter.posX, shooter.posY, shooter.posZ, soundEmpty, SoundCategory.PLAYERS, 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + 0.5F);
						shooter.activeItemStackUseCount = maxUseTime - 1;
					}
				}
			}
			else
				shooter.activeItemStackUseCount = maxUseTime - 1;
		}
		else if (capability.isMagazineEmpty())
			capability.reloadTime++;
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer shooter, EnumHand hand)
    {
		ActionResult<ItemStack> result = super.onItemRightClick(world, shooter, hand);
		ItemStack stack = result.getResult();
		CapabilityCrossbow capability = stack.getCapability(CapabilityCrossbow.INSTANCE, null);
		NBTTagCompound nbt = ItemUtils.loadNBT(stack);
		int maxAmmo = this.maxAmmo + EnchantmentUtil.getEnchantmentLevel("multishot", stack, true);
		if (capability.maxAmmo != maxAmmo)
			capability.maxAmmo = maxAmmo;
		if (result.getType().equals(EnumActionResult.FAIL) && !capability.isMagazineEmpty())
		{
			shooter.setActiveHand(hand);
			result = new ActionResult<ItemStack>(EnumActionResult.PASS, stack);
		}
		
		if (!result.getType().equals(EnumActionResult.FAIL))
		{
			onStartUse(stack, world, shooter, capability);
			if (!nbt.getBoolean("isLoaded"))
				world.playSound((EntityPlayer)null, shooter.posX, shooter.posY, shooter.posZ, soundReload, SoundCategory.PLAYERS, 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + 0.5F);
		}
		return result;
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
