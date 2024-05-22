package net.endermanofdoom.mac.item;

import java.util.Set;
import java.util.function.Predicate;

import net.endermanofdoom.mac.MACCore;
import net.endermanofdoom.mac.util.EnchantmentUtil;
import net.endermanofdoom.mac.util.math.Vec;
import net.endermanofdoom.mac.util.math.Vec3;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public abstract class ItemCrossbow extends ItemBowEX
{
	protected int magazine;
	protected int reloadSpeed;
	protected SoundEvent reloadSound;
	protected SoundEvent reloadedSound;
	
	public ItemCrossbow(Predicate<ItemStack> arrowType, int enchantability)
	{
		super(arrowType, enchantability);
		magazine = 1;
		reloadSpeed = 40;
	}
	
	public ItemCrossbow setMagazineSize(int arrows)
	{
		magazine = arrows;
		return this;
	}
	
	public ItemCrossbow setReloadSpeed(int ticks)
	{
		reloadSpeed = Math.max(ticks, 1);
		return this;
	}
	public ItemCrossbow setReloadSound(SoundEvent sound)
	{
		reloadSound = sound;
		return this;
	}
	public ItemCrossbow setReloadedSound(SoundEvent sound)
	{
		reloadedSound = sound;
		return this;
	}
	
	public boolean isReloaded(NBTTagCompound nbt)
	{
		return nbt.getBoolean("reloaded");
	}
	
	public int getArrowCount(NBTTagCompound nbt)
	{
		return nbt.getInteger("arrows");
	}
	
	public void setArrowCount(NBTTagCompound nbt, int arrows)
	{
		nbt.setInteger("arrows", arrows);
	}
	
	public void addArrows(NBTTagCompound nbt, int arrows)
	{
		setArrowCount(nbt, getArrowCount(nbt) + arrows);
	}
	
	@Override
	protected void shoot(ItemStack stack, World world, EntityPlayer shooter, int timeLeft)
	{
		onShootPre(stack, world, shooter, timeLeft);
		NBTTagCompound nbt = ItemUtils.loadNBT(stack);
		boolean hasInfinity = shooter.capabilities.isCreativeMode || EnchantmentUtil.getEnchantmentLevel(Enchantments.INFINITY, stack) > 0;
		ItemStack itemstack = unloadMagazine(shooter, nbt);
		ItemArrow arrow = (ItemArrow)(itemstack.getItem() instanceof ItemArrow ? itemstack.getItem() : Items.ARROW);

		if (!itemstack.isEmpty() || hasInfinity)
		{
			int arrows = arrowAmount + EnchantmentUtil.getEnchantmentLevel("multishot", stack, true);
			int j = EnchantmentUtil.getEnchantmentLevel(Enchantments.POWER, stack);
			int k = EnchantmentUtil.getEnchantmentLevel(Enchantments.PUNCH, stack);
			boolean l = EnchantmentUtil.getEnchantmentLevel(Enchantments.FLAME, stack) > 0;
			if (arrowAmount > 0)
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
						onShoot(stack, world, shooter, entityarrow, timeLeft, ii);
					}
					else
						onShoot(stack, world, shooter, null, timeLeft, ii);
					
					if (ii == 0)
						world.playSound((EntityPlayer)null, shooter.posX, shooter.posY, shooter.posZ, sound, SoundCategory.PLAYERS, 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + 0.5F);

					itemstack.shrink(1);
					if (itemstack.isEmpty())
					{
						shooter.inventory.deleteStack(itemstack);
						itemstack = unloadMagazine(shooter, nbt);
						arrow = (ItemArrow)(itemstack.getItem() instanceof ItemArrow ? itemstack.getItem() : Items.ARROW);
						if (itemstack.isEmpty())
							break;
					}

					shooter.addStat(StatList.getObjectUseStats(this));
				}

				if (!itemstack.isEmpty())
					addToMagazine(shooter, nbt, itemstack);
				onShootPost(stack, world, shooter, timeLeft);
			}
			else
				onShootFail(stack, world, shooter, timeLeft);
		}
		else
			onShootFail(stack, world, shooter, timeLeft);
		ItemUtils.saveNBT(stack, nbt);
	}
	
	@Override
	public void onPlayerStoppedUsing(ItemStack stack, World world, EntityLivingBase shooter, int timeLeft)
	{
		if (!(shooter instanceof EntityPlayer)) return;
		NBTTagCompound nbt = ItemUtils.loadNBT(stack);
		if (isReloaded(nbt) && getArrowCount(nbt) <= 0)
		{
			nbt.setBoolean("reloaded", false);
			nbt.setInteger("reloadTime", 0);
		}
		else
		{
			int maxUseTime = stack.getMaxItemUseDuration();
			if (maxUseTime - timeLeft > reloadSpeed)
			{
				if (reloadedSound != null)
					world.playSound((EntityPlayer)null, shooter.posX, shooter.posY, shooter.posZ, reloadedSound, SoundCategory.PLAYERS, 1.0F, 1.0F);
				loadMagazine((EntityPlayer) shooter, nbt);
				nbt.setBoolean("reloaded", true);
			}
			else
				nbt.setInteger("reloadTime", 0);
			MACCore.info("Reloaded Load [" + (maxUseTime - timeLeft) + "]: " + isReloaded(nbt));
		}
		nbt.setInteger("shotTime", 0);
		ItemUtils.saveNBT(stack, nbt);
		onStopUse(stack, world, (EntityPlayer) shooter, timeLeft);
	}
	
	@Override
	public void onUsingTick(ItemStack stack, EntityLivingBase shooter, int timeLeft)
	{
		if (!(shooter instanceof EntityPlayer)) return;
		EntityPlayer player = (EntityPlayer) shooter;
		NBTTagCompound nbt = ItemUtils.loadNBT(stack);
		onTickUse(stack, player.world, (EntityPlayer) shooter, timeLeft);
		
		if (isReloaded(nbt))
		{
			int shotTime = nbt.getInteger("shotTime");
			if (shotTime > chargeTime)
			{
				shotTime = -1;
				if (getArrowCount(nbt) > 0)
				{
					shoot(stack, player.world, player, shotTime);
					nbt = ItemUtils.loadNBT(stack);
				}
				else
				{
					nbt.setBoolean("reloaded", false);
					nbt.setInteger("reloadTime", 0);
				}
			}
			nbt.setInteger("shotTime", shotTime + 1);
			ItemUtils.saveNBT(stack, nbt);
		}
		else
		{
			int reloadTime = nbt.getInteger("reloadTime");
			if (reloadTime < reloadSpeed)
			{
				nbt.setInteger("reloadTime", reloadTime + 1);
				ItemUtils.saveNBT(stack, nbt);
			}
		}
	}
	
	public void loadMagazine(EntityPlayer shooter, NBTTagCompound nbt)
	{
		NBTTagCompound magazine = nbt.getCompoundTag("magazine");
		ItemStack stack;
		
		while (getArrowCount(nbt) < this.magazine && (!(stack = findAmmo(shooter)).isEmpty() || shooter.capabilities.isCreativeMode))
			addToMagazine(shooter, nbt, shooter.capabilities.isCreativeMode && stack.isEmpty() ? new ItemStack(Items.ARROW, 64) : stack);
	}
	
	public void addToMagazine(EntityPlayer shooter, NBTTagCompound nbt, ItemStack stack)
	{
		if (!(stack.getItem() instanceof ItemArrow)) return;
		NBTTagCompound magazine = nbt.getCompoundTag("magazine");
		int arrows = getArrowCount(nbt), stackCount = Math.min(stack.getCount(), this.magazine - arrows);
		String arrowType = stack.getItem().getRegistryName().toString();
		arrows += stackCount;
		magazine.setInteger(arrowType, magazine.getInteger(arrowType) + stackCount);
		magazine.setInteger(arrowType + ":meta", stack.getMetadata());
		if (!shooter.capabilities.isCreativeMode)
			stack.shrink(stackCount);
		setArrowCount(nbt, arrows);
		MACCore.info("ADDING AMMO: " + arrows);
	}
	
	public ItemStack unloadMagazine(EntityPlayer shooter, NBTTagCompound nbt)
	{
		ItemStack arrow = ItemStack.EMPTY;
		NBTTagCompound magazine = nbt.getCompoundTag("magazine");
		Set<String> keys = magazine.getKeySet();
		Item item;
		int amount;
		
		for (String arrowType : keys)
		{
			if (arrowType.matches("\\\\:meta$")) continue;
			item = Item.getByNameOrId(arrowType);
			if (item instanceof ItemArrow)
			{
				amount = Math.min(magazine.getInteger(arrowType), 64);
				arrow = new ItemStack(item, amount, magazine.getInteger(arrowType + ":meta"));
				magazine.setInteger(arrowType, magazine.getInteger(arrowType) - amount);
				addArrows(nbt, -amount);
				if (getArrowCount(nbt) <= 0)
					nbt.setTag("magazine", new NBTTagCompound());
				break;
			}
		}
		MACCore.info("Unloading AMMO: " + getArrowCount(nbt));
		return arrow;
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer shooter, EnumHand hand)
    {
		ActionResult<ItemStack> result = super.onItemRightClick(world, shooter, hand);
		ItemStack stack = result.getResult();
		NBTTagCompound nbt = ItemUtils.loadNBT(stack);
		int arrows = getArrowCount(nbt);
		if (arrows <= 0 && !result.getType().equals(EnumActionResult.FAIL))
			if (reloadSound != null)
				world.playSound((EntityPlayer)null, shooter.posX, shooter.posY, shooter.posZ, reloadSound, SoundCategory.PLAYERS, 1.0F, 1.0F);
		return result;
    }
	
	@Override
	protected float getPullPercent(EntityLivingBase entity, ItemStack stack)
	{
		return (float)ItemUtils.loadNBT(stack).getInteger("reloadTime") / reloadSpeed;
	}

	@Override
	protected boolean isPulling(EntityLivingBase entity, ItemStack stack)
	{
		return ItemUtils.loadNBT(stack).getInteger("reloadTime") > 0;
	}
}
