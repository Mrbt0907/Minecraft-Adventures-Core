package net.endermanofdoom.mac.registry;

import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.RangedAttribute;

public class MACAttributes
{
	public static final IAttribute ATTACK_RANGE = (new RangedAttribute((IAttribute)null, "generic.attackRange", 3.0D, 3.0D, Double.MAX_VALUE)).setShouldWatch(true);
}
