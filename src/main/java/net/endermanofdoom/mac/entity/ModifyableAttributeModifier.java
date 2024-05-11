package net.endermanofdoom.mac.entity;

import java.util.UUID;

import net.endermanofdoom.mac.MACCore;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;

public class ModifyableAttributeModifier extends AttributeModifier {

	public double value;
    public int op;
	private final double defaultAmount;
	private final int defaultOperation;
    
	public ModifyableAttributeModifier(String nameIn, double amountIn, int operationIn)
	{
		super(nameIn, amountIn, operationIn);
		value = amountIn;
		defaultAmount = amountIn;
		op = operationIn;
		defaultOperation = operationIn;
	}

	public ModifyableAttributeModifier(UUID idIn, String nameIn, double amountIn, int operationIn)
    {
        super(idIn, nameIn, amountIn, operationIn);
		value = amountIn;
		defaultAmount = amountIn;
		op = operationIn;
		defaultOperation = operationIn;
    }
	
	public static boolean hasModifier(IAttributeInstance attribute, UUID attributeUUID)
	{
		return attribute.getModifier(attributeUUID) != null;
	}
	
	public static ModifyableAttributeModifier getModifier(IAttributeInstance attribute, UUID attributeUUID, String name)
	{
		return getModifier(attribute, attributeUUID, name, 0.0D, 0);
	}
	
	public static ModifyableAttributeModifier getModifier(IAttributeInstance attribute, UUID attributeUUID, String name, double amount, int operation)
	{
		if (attributeUUID == null)
		{
			MACCore.error(new NullPointerException("Attribute UUID was null"));
			return null;
		}
		AttributeModifier modifier = attribute.getModifier(attributeUUID);
		return modifier instanceof ModifyableAttributeModifier ? (ModifyableAttributeModifier) modifier : modifier == null ? new ModifyableAttributeModifier(attributeUUID, name, amount, operation) : new ModifyableAttributeModifier(attributeUUID, modifier.getName(), modifier.getAmount(), modifier.getOperation());
	}
	
	public static ModifyableAttributeModifier apply(IAttributeInstance attribute, UUID attributeUUID, String name)
	{
		return apply(attribute, attributeUUID, name, 0.0D, 0);
	}
	
	public static ModifyableAttributeModifier apply(IAttributeInstance attribute, UUID attributeUUID, String name, double amount, int operation)
	{
		if (attributeUUID == null)
		{
			MACCore.error(new NullPointerException("Attribute UUID was null"));
			return null;
		}

		final AttributeModifier modifierA = attribute.getModifier(attributeUUID);
		if (modifierA instanceof ModifyableAttributeModifier)
		{
			attribute.removeModifier(attributeUUID);
			attribute.applyModifier(modifierA);
			return (ModifyableAttributeModifier) modifierA;
		}
		
		final ModifyableAttributeModifier modifierB = modifierA == null ? new ModifyableAttributeModifier(attributeUUID, name, amount, operation) : new ModifyableAttributeModifier(attributeUUID, modifierA.getName(), modifierA.getAmount(), modifierA.getOperation());
		if (modifierA != null)
			attribute.removeModifier(attributeUUID);
		attribute.applyModifier(modifierB);
		MACCore.debug("Applied modifier " + name + " to attriubute " + attribute.toString());
		return modifierB;
	}
	
	public void setDefault()
	{
		value = defaultAmount;
		op = defaultOperation;
	}
	
	@Override
	public int getOperation()
    {
        return op;
    }

	@Override
    public double getAmount()
    {
        return value;
    }
}
