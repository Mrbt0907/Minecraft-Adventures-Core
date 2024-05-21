package net.endermanofdoom.mac.registry;

import java.util.UUID;

import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.RangedAttribute;

public class MACAttributes
{
	public static final IAttribute ATTACK_RANGE = (new RangedAttribute((IAttribute)null, "generic.attackRange", 0.0D, 0.0D, Double.MAX_VALUE)).setShouldWatch(true);
	public static final UUID ATTACK_RANGE_UUID = UUID.fromString("ea86d582-7fbc-4157-beb2-e092e01f475c");
}
