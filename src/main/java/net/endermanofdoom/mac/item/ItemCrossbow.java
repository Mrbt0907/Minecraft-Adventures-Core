package net.endermanofdoom.mac.item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;
import javax.annotation.Nullable;

import com.google.common.collect.Multimap;

import net.endermanofdoom.mac.capabilities.CapabilityCrossbow;
import net.endermanofdoom.mac.capabilities.CapabilityCrossbow.Provider;
import net.endermanofdoom.mac.util.EnchantmentUtil;
import net.endermanofdoom.mac.util.math.Maths;
import net.endermanofdoom.mac.util.math.Vec;
import net.endermanofdoom.mac.util.math.Vec3;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
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
	protected SoundEvent soundEmpty = SoundEvents.BLOCK_TRIPWIRE_CLICK_OFF;
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
				
				if (entity == null)
					return 0.0F;
				else
				{
					CapabilityCrossbow capability = stack.getCapability(CapabilityCrossbow.Provider.INSTANCE, Provider.FACE);
					return capability.isLoaded ? 1F : ((float)capability.reloadTime) / reloadTime;
				}
			}
		});
		addPropertyOverride(new ResourceLocation("pulling"), new IItemPropertyGetter()
		{
			@SideOnly(Side.CLIENT)
			public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn)
			{
				CapabilityCrossbow capability = stack.getCapability(CapabilityCrossbow.Provider.INSTANCE, Provider.FACE);
				return entityIn != null && (capability.isLoaded && !capability.isMagazineEmpty() || entityIn.isHandActive() && !capability.isLoaded) ? 1.0F : 0.0F;
			}
		});
	}

	/**Runs when the player right clicks with this crossbow*/
	public abstract void onStartUse(ItemStack stack, World world, EntityPlayer shooter, CapabilityCrossbow capability);
	/**Runs while the player holds right click with this crossbow*/
	public abstract void onTickUse(ItemStack stack, World world, EntityPlayer shooter, CapabilityCrossbow capability, int timeLeft);
	/**Runs when the player releases right click with this crossbow in hand*/
	public abstract void onStopUse(ItemStack stack, World world, EntityPlayer shooter, CapabilityCrossbow capability, int timeLeft);
	/**Runs before all arrows are shot from this crossbow*/
	public abstract void onShootPre(ItemStack stack, World world, EntityPlayer shooter, CapabilityCrossbow capability, int timeLeft);
	/**Runs for each arrow shot from this crossbow*/
	public abstract void onShoot(ItemStack stack, World world, EntityPlayer shooter, CapabilityCrossbow capability, EntityArrow arrow, int timeLeft, int arrowIndex);
	/**Runs after all arrows are shot from this crossbow*/
	public abstract void onShootPost(ItemStack stack, World world, EntityPlayer shooter, CapabilityCrossbow capability, int timeLeft);
	/**Runs when any arrow fails to shoot from this crossbow*/
	public abstract void onShootFail(ItemStack stack, World world, EntityPlayer shooter, CapabilityCrossbow capability, int timeLeft);
	/**Returns the arrow that this crossbow will shoot. Return null or the arrow parameter to shoot the original arrow*/
	public EntityArrow onCreateArrow(ItemStack stack, World world, EntityPlayer shooter, CapabilityCrossbow capability, EntityArrow arrow, int timeLeft, int arrowIndex)
	{
		return null;
	}
	/**Returns where the created arrow of this crossbow will shoot from*/
	public Vec3 getShootPos(ItemStack stack, World world, EntityPlayer shooter, int arrowIndex)
	{
		Vec3 position = new Vec3(shooter);
		position.posY += shooter.eyeHeight;
		return position;
	}
	/**Returns the direction in which the created arrow of this crossbow will shoot towards*/
	public Vec getShootRot(ItemStack stack, World world, EntityPlayer shooter, int arrowIndex)
	{
		return new Vec(shooter.rotationPitch, shooter.rotationYaw);
	}
	/**Runs when attributes for this crossbow are being gathered. Add attribute modifiers in attributes. Existing values will be overwritten*/
	public void getAttributes(EntityEquipmentSlot slot, Map<String, AttributeModifier> attributes) {}
	
	
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
	
	protected void shoot(ItemStack stack, World world, EntityPlayer shooter, int timeLeft)
	{
		CapabilityCrossbow capability = stack.getCapability(CapabilityCrossbow.Provider.INSTANCE, Provider.FACE);
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
						if (entityarrow == null) entityarrow = ((ItemArrow)Items.ARROW).createArrow(world, itemstack, shooter);
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
				capability.toMagazine(0, itemstack, true);
			onShootPost(stack, world, shooter, capability, timeLeft);
		}
		else
			onShootFail(stack, world, shooter, capability, timeLeft);
	}
	
	@Override
	public void onPlayerStoppedUsing(ItemStack stack, World world, EntityLivingBase shooter, int timeLeft)
	{
		if (!(shooter instanceof EntityPlayer)) return;
		CapabilityCrossbow capability = stack.getCapability(CapabilityCrossbow.Provider.INSTANCE, Provider.FACE);
		
		if (!capability.isLoaded)
		{
			if (stack.getMaxItemUseDuration() - timeLeft > reloadTime)
			{
				if (((EntityPlayer)shooter).capabilities.isCreativeMode && findAmmo((EntityPlayer) shooter).isEmpty())
					capability.toMagazine(new ItemStack(Items.ARROW, 64), false);
				else
					capability.loadMagazine(((EntityPlayer)shooter).inventory, !((EntityPlayer)shooter).capabilities.isCreativeMode && EnchantmentUtil.getEnchantmentLevel(Enchantments.INFINITY, stack) < 1);
				if (!world.isRemote)
					capability.markDirty((EntityPlayer) shooter, "inventory", "field_71071_by", ((EntityPlayer)shooter).inventory.getSlotFor(stack));
				if (!capability.isMagazineEmpty())
					world.playSound((EntityPlayer)null, shooter.posX, shooter.posY, shooter.posZ, soundLoaded, SoundCategory.PLAYERS, 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + 0.5F);
			}
		}
		capability.hasFired = false;
		capability.isLoaded = !capability.isMagazineEmpty();
		capability.reloadTime = 0;
		onStopUse(stack, shooter.world, (EntityPlayer) shooter, capability, timeLeft);
	}
	
	@Override
	public void onUsingTick(ItemStack stack, EntityLivingBase shooter, int timeLeft)
	{
		if (!(shooter instanceof EntityPlayer)) return;
		
		CapabilityCrossbow capability = stack.getCapability(CapabilityCrossbow.Provider.INSTANCE, Provider.FACE);
		onTickUse(stack, shooter.world, (EntityPlayer) shooter, capability, timeLeft);

		if (shooter.ticksExisted < capability.nextShot - getChargeTime(stack))
			capability.nextShot = shooter.ticksExisted + getChargeTime(stack);
		
		if (capability.isLoaded)
		{
			int maxUseTime = stack.getMaxItemUseDuration();
			if (autoFire || (!capability.isMagazineEmpty() && !capability.hasFired))
			{
				if (((maxUseTime == timeLeft || maxUseTime - timeLeft > getChargeTime(stack))) && capability.nextShot < shooter.ticksExisted)
				{
					if ((autoFire || !capability.hasFired) && !capability.isMagazineEmpty())
					{
						shoot(stack, shooter.world, (EntityPlayer) shooter, 0);
						capability.nextShot = shooter.ticksExisted + getChargeTime(stack);
						capability.hasFired = true;
						shooter.activeItemStackUseCount = maxUseTime - 1;
					}
					else
					{
						shooter.world.playSound((EntityPlayer)null, shooter.posX, shooter.posY, shooter.posZ, soundEmpty, SoundCategory.PLAYERS, 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + 0.5F);
						if (!shooter.world.isRemote)
							capability.markDirty((EntityPlayer) shooter, "inventory", "field_71071_by", ((EntityPlayer)shooter).inventory.getSlotFor(stack));
						shooter.activeItemStackUseCount = maxUseTime - 1;
						onShootFail(stack, shooter.world, (EntityPlayer) shooter, capability, timeLeft);
					}
				}
			}
			else
			{
				if (!shooter.world.isRemote)
					capability.markDirty((EntityPlayer) shooter, "inventory", "field_71071_by", ((EntityPlayer)shooter).inventory.getSlotFor(stack));
				shooter.activeItemStackUseCount = maxUseTime - 1;
			}
		}
		else
			capability.reloadTime++;
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer shooter, EnumHand hand)
    {
		ActionResult<ItemStack> result = super.onItemRightClick(world, shooter, hand);
		ItemStack stack = result.getResult();
		CapabilityCrossbow capability = stack.getCapability(CapabilityCrossbow.Provider.INSTANCE, Provider.FACE);
		int maxAmmo = this.maxAmmo + EnchantmentUtil.getEnchantmentLevel("multishot", stack, true);
		if (capability.maxAmmo != maxAmmo)
			capability.maxAmmo = maxAmmo;
		if (result.getType().equals(EnumActionResult.FAIL) && !capability.isMagazineEmpty())
		{
			shooter.setActiveHand(hand);
			result = new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
		}
		
		if (!result.getType().equals(EnumActionResult.FAIL))
		{
			onStartUse(stack, world, shooter, capability);
			if (!capability.isLoaded)
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
	
	@Override
	public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack)
    {
		Multimap<String, AttributeModifier> modifiers = super.getAttributeModifiers(slot, stack);
		Map<String, AttributeModifier> userModifiers = new HashMap<String, AttributeModifier>();
		getAttributes(slot, userModifiers);
		
		for (Entry<String, AttributeModifier> modifier : userModifiers.entrySet())
		{
			if (modifiers.containsKey(modifier.getKey()))
				modifiers.removeAll(modifier.getKey());
			modifiers.put(modifier.getKey(), modifier.getValue());
		}
		return modifiers;
    }
}
