package net.endermanofdoom.mac.item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.Multimap;

import net.endermanofdoom.mac.registry.MACAttributes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public abstract class ItemSwordEX extends ItemSword
{
	public final double attackReach;
	private int targetIndex;
	public ItemSwordEX(ToolMaterial material, double extendedAttackReach)
	{
		super(material);
		attackReach = extendedAttackReach;
	}
	
	/**Runs before the vanilla damage triggers on the victim. Return true to cancel vanilla damage calculations. Return shouldCancel to use the vanilla result. Also cancels onAttack()*/
	public abstract boolean onAttackPre(ItemStack stack, World world, EntityPlayer attacker, Entity victim, int victimIndex, boolean shouldCancel);
	/**Runs after the vanilla damage triggers on the victim. Return true to continue further vanilla itemstack operations. Return shouldContinue to use the vanilla result*/
	public abstract boolean onAttack(ItemStack stack, World world, EntityPlayer attacker, Entity victim, int victimIndex);
	/**Runs when the player swings this sword at anytime*/
	public abstract void onSwing(ItemStack stack, World world, EntityPlayer attacker);
	/**Runs when the player right clicks with this sword*/
	public abstract void onStartUse(ItemStack stack, World world, EntityPlayer attacker);
	/**Runs while the player holds right click with this sword*/
	public abstract void onTickUse(ItemStack stack, World world, EntityPlayer attacker, int timeLeft);
	/**Runs when the player releases right click with this sword in hand*/
	public abstract void onStopUse(ItemStack stack, World world, EntityPlayer attacker, int timeLeft);
	/**Returns a list of additional entities this sword should attempt to attack. Return null or an empty list to just attack the original target if they exist*/
	public List<Entity> getTargets(ItemStack stack, World world, EntityPlayer attacker)
	{
		return null;
	}
	/**Runs when attributes for this sword are being gathered. Add attribute modifiers in attributes. Existing values will be overwritten*/
	public void getAttributes(EntityEquipmentSlot slot, Map<String, AttributeModifier> attributes) {}
	
	@Override
	public boolean hitEntity(ItemStack stack, EntityLivingBase victim, EntityLivingBase attacker)
    {
		if (!(attacker instanceof EntityPlayer)) return super.hitEntity(stack, victim, attacker);
		return onAttack(stack, attacker.world, (EntityPlayer) attacker, victim, targetIndex) ? super.hitEntity(stack, victim, attacker) : false;
    }
	
	@Override
	public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity victim)
    {
		boolean shouldStopAttack = super.onLeftClickEntity(stack, player, victim);
		if (targetIndex < 0)
		{
			onSwing(stack, player.world, player);
			List<Entity> targets = getTargets(stack, player.world, player);
			if (targets != null)
			{
				Entity target;
				int size = targets.size();
				if (targets != null)
					for (int i = 0; i < size; i++)
					{
						target = targets.get(i);
						targetIndex = i + 1;
						player.attackTargetEntityWithCurrentItem(target);
					}
			}
			targetIndex = -1;
			return onAttackPre(stack, player.world, player, victim, targetIndex + 1, shouldStopAttack) ? true : shouldStopAttack;
		}
		return onAttackPre(stack, player.world, player, victim, targetIndex, shouldStopAttack) ? true : shouldStopAttack;
    }
	
	@Override
	public boolean onEntitySwing(EntityLivingBase attacker, ItemStack stack)
    {
		if (attacker instanceof EntityPlayer)
		{
			EntityPlayer player = (EntityPlayer) attacker;
			onSwing(stack, player.world, player);
			List<Entity> targets = getTargets(stack, player.world, player);
			if (targets != null)
			{
				Entity target;
				int size = targets.size();
				if (targets != null)
					for (int i = 0; i < size; i++)
					{
						target = targets.get(i);
						targetIndex = i;
						player.attackTargetEntityWithCurrentItem(target);
					}
			}
			targetIndex = -1;
		}
		return super.onEntitySwing(attacker, stack);
    }
	
	@Override
	public void onPlayerStoppedUsing(ItemStack stack, World world, EntityLivingBase attacker, int timeLeft)
	{
		if (!(attacker instanceof EntityPlayer)) return;
		onStopUse(stack, world, (EntityPlayer) attacker, timeLeft);
	}
	
	@Override
	public void onUsingTick(ItemStack stack, EntityLivingBase attacker, int timeLeft)
	{
		if (!(attacker instanceof EntityPlayer)) return;
		onTickUse(stack, attacker.world, (EntityPlayer) attacker, timeLeft);
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
	public Multimap<String, AttributeModifier> getItemAttributeModifiers(EntityEquipmentSlot slot)
    {
		Multimap<String, AttributeModifier> modifiers = super.getItemAttributeModifiers(slot);
		Map<String, AttributeModifier> userModifiers = new HashMap<String, AttributeModifier>();
		if (slot == EntityEquipmentSlot.MAINHAND)
			modifiers.put(MACAttributes.ATTACK_RANGE.getName(), new AttributeModifier(MACAttributes.ATTACK_RANGE_UUID, "Weapon modifier", attackReach, 0));
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
