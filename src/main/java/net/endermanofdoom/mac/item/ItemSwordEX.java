package net.endermanofdoom.mac.item;

import net.endermanofdoom.mac.interfaces.ISetBonus;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;

public abstract class ItemSwordEX extends ItemSword implements ISetBonus
{
	public final double attackReach;
	public ItemSwordEX(ToolMaterial material, double extendedAttackReach)
	{
		super(material);
		attackReach = extendedAttackReach;
	}

	@Override
	public boolean onEntitySwing(EntityLivingBase player, ItemStack stack)
    {
		
		return super.onEntitySwing(player, stack);
    }
}
