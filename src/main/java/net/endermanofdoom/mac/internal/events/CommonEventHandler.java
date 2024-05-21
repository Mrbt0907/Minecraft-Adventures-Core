package net.endermanofdoom.mac.internal.events;

import net.endermanofdoom.mac.MACCore;
import net.endermanofdoom.mac.events.WorldEvent;
import net.endermanofdoom.mac.interfaces.ISetBonus;
import net.endermanofdoom.mac.item.ItemUtils;
import net.endermanofdoom.mac.registry.MACAttributes;
import net.endermanofdoom.mac.world.WorldDataManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.world.WorldEvent.Load;
import net.minecraftforge.event.world.WorldEvent.Save;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;

public class CommonEventHandler
{
	@SubscribeEvent
	public static void onServerTick(ServerTickEvent event)
	{
		if (WorldDataManager.isAvailable())
			WorldDataManager.tick();
	}
	
	@SubscribeEvent
	public static void onWorldLoad(Load event)
	{
		if (!WorldDataManager.isAvailable())
		{
			WorldDataManager.setAvailable();
			MinecraftForge.EVENT_BUS.post(new WorldEvent.Start());
		}
	}
	
	@SubscribeEvent
	public static void onWorldSave(Save event)
	{
		WorldDataManager.save();
	}
	
	@Mod.EventHandler
	public static void onWorldUnload(FMLServerStoppingEvent event)
	{
		MinecraftForge.EVENT_BUS.post(new WorldEvent.Stop());
		WorldDataManager.reset();
	}

	@SubscribeEvent
	public static void onProjectileImpact(ProjectileImpactEvent event)
	{
		Entity victim = event.getRayTraceResult().entityHit;
		if (victim == null) return;
		
		NBTTagCompound nbt = event.getEntity().getEntityData();
		if (nbt.hasKey("multiShot"))
		{
			victim.hurtResistantTime = 0;
			if (victim instanceof EntityLivingBase)
				((EntityLivingBase)victim).recentlyHit = 100;
		}
	}
	
	@SubscribeEvent
	public static void onMobSpawnEvent(EntityJoinWorldEvent event)
	{
		Entity entity = event.getEntity();
		if (entity instanceof EntityItem)
		{
			EntityItem item = (EntityItem)entity;
			
			if (
					item.getItem().getItem() == Item.getItemFromBlock(Blocks.COMMAND_BLOCK) ||
					item.getItem().getItem() == Item.getItemFromBlock(Blocks.CHAIN_COMMAND_BLOCK) ||
					item.getItem().getItem() == Item.getItemFromBlock(Blocks.REPEATING_COMMAND_BLOCK) ||
					item.getItem().getItem() == Item.getItemFromBlock(Blocks.BARRIER) ||
					item.getItem().getItem() == Item.getItemFromBlock(Blocks.BEDROCK) ||
					item.getItem().getItem() == Item.getItemFromBlock(Blocks.END_PORTAL_FRAME) ||
					item.getItem().getItem() == Item.getItemFromBlock(Blocks.END_PORTAL) ||
					item.getItem().getItem() == Item.getItemFromBlock(Blocks.END_GATEWAY) ||
					item.getItem().getItem() == Item.getItemFromBlock(Blocks.PORTAL) ||
					item.getItem().getItem() == Item.getItemFromBlock(Blocks.STRUCTURE_BLOCK) ||
					item.getItem().getItem() == Item.getItemFromBlock(Blocks.STRUCTURE_VOID) ||
					item.getItem().getItem() == Item.getItemFromBlock(Blocks.DRAGON_EGG) ||
					item.getItem().getItem() == Items.NETHER_STAR
				)
			{
			item.setEntityInvulnerable(true);
			item.setNoDespawn();
			}
		}
		else if(entity instanceof EntityPlayer)
		{
			AbstractAttributeMap attributes = ((EntityPlayer)entity).getAttributeMap();
			if (attributes.getAttributeInstance(MACAttributes.ATTACK_RANGE) == null)
				attributes.registerAttribute(MACAttributes.ATTACK_RANGE);
		}
	}
	
	@SubscribeEvent
	public static void onLivingTick(LivingUpdateEvent event)
	{
		EntityLivingBase entity = event.getEntityLiving();
		if (entity == null) return;
		
	}
	
	private static int getEquipmentIndex(int index)
	{
		switch (index)
		{
			case 0: return 4;
			case 1: return 3;
			case 2: return 2;
			case 3: return 1;
			case 4: return 0;
			default: return 5;
		}
	}
	
	private static ItemStack[] getEquipment(EntityLivingBase owner)
	{
		ItemStack[] equipment = new ItemStack[6];
		equipment[0] = owner.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
		equipment[1] = owner.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
		equipment[2] = owner.getItemStackFromSlot(EntityEquipmentSlot.LEGS);
		equipment[3] = owner.getItemStackFromSlot(EntityEquipmentSlot.FEET);
		equipment[4] = owner.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND);
		equipment[5] = owner.getItemStackFromSlot(EntityEquipmentSlot.OFFHAND);
		for (int i = 0; i < 6; i++) if (equipment[i] == null) equipment[i] = ItemStack.EMPTY;
		return equipment;
	}
	
	@SubscribeEvent
	public static void onEquipmentChange(LivingEquipmentChangeEvent event)
	{
		EntityLivingBase owner = event.getEntityLiving();
		EntityEquipmentSlot[] slots = EntityEquipmentSlot.values();
		EntityEquipmentSlot slotChanged = event.getSlot();
		ItemStack[] equipment = getEquipment(owner);
		ItemStack stackOld = event.getFrom();
		ItemStack stack;
		ISetBonus item;
		boolean isSetOld, isSetNow;
		int i;
		
		for (EntityEquipmentSlot slot : slots)
		{
			i = slot.getIndex();
			if (i > 5) continue;
			stack = equipment[getEquipmentIndex(i)];
			
			if (slotChanged.equals(slot) && stackOld.getItem() instanceof ISetBonus)
			{
				ISetBonus itemOld = (ISetBonus) stackOld.getItem();
				
			}
			
			if (stack.getItem() instanceof ISetBonus)
			{
				item = (ISetBonus) stack.getItem();
				switch(slotChanged)
				{
					case HEAD:
						isSetOld = item.isFullSet(owner, slot, equipment[0], equipment[1], equipment[2], equipment[3], equipment[4], equipment[5]);
						break;
					case CHEST:
						isSetOld = item.isFullSet(owner, slot, equipment[0], equipment[1], equipment[2], equipment[3], equipment[4], equipment[5]);
						break;
					case LEGS:
						isSetOld = item.isFullSet(owner, slot, equipment[0], equipment[1], equipment[2], equipment[3], equipment[4], equipment[5]);
						break;
					case FEET:
						isSetOld = item.isFullSet(owner, slot, equipment[0], equipment[1], equipment[2], equipment[3], equipment[4], equipment[5]);
						break;
					case MAINHAND:
						isSetOld = item.isFullSet(owner, slot, equipment[0], equipment[1], equipment[2], equipment[3], equipment[4], equipment[5]);
						break;
					case OFFHAND:
						isSetOld = item.isFullSet(owner, slot, equipment[0], equipment[1], equipment[2], equipment[3], equipment[4], equipment[5]);
						break;
				}
			}
		}
		/*
		//Parsing
		if (oldStack.getItem() instanceof ISetBonus)
		{
			ItemStack[] stacksOld = new ItemStack[6];
			ISetBonus stack = (ISetBonus) oldStack.getItem();
			stacksOld[5] = slot.equals(EntityEquipmentSlot.HEAD) ? oldStack : owner.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
			stacksOld[4] = slot.equals(EntityEquipmentSlot.CHEST) ? oldStack : owner.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
			stacksOld[3] = slot.equals(EntityEquipmentSlot.LEGS) ? oldStack : owner.getItemStackFromSlot(EntityEquipmentSlot.LEGS);
			stacksOld[2] = slot.equals(EntityEquipmentSlot.FEET) ? oldStack : owner.getItemStackFromSlot(EntityEquipmentSlot.FEET);
			stacksOld[0] = slot.equals(EntityEquipmentSlot.MAINHAND) ? oldStack : owner.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND);
			stacksOld[1] = slot.equals(EntityEquipmentSlot.OFFHAND) ? oldStack : owner.getItemStackFromSlot(EntityEquipmentSlot.OFFHAND);
			for (int i = 0; i < 6; i++)
				if (stacksOld[i] == null)
					stacksOld[i] = ItemStack.EMPTY;
			if (isFullSet(owner, oldStack, stacksOld, slot))
				stack.onUnequipFull(owner, oldStack, slot);
			stack.onUnequip(owner, oldStack, slot);
		}
		EntityEquipmentSlot[] slots = EntityEquipmentSlot.values();
		ItemStack stack;
		ISetBonus item;
		boolean isSet, isSetOld;
		int index;
		for (EntityEquipmentSlot slotB : slots)
		{
			index = slotB.ordinal();
			if (index > 5) continue;
			stack = stacks[index];
			if (!(stack.getItem() instanceof ISetBonus)) continue;
			item = (ISetBonus) stack.getItem();
			isSetOld = isFullSet(owner, item, oldStack, stacks, slotB);
			isSet = isFullSet(owner, item, newStack, stacks, slotB);
			if (slot.equals(slotB))
				item.onEquip(owner, stack, slotB);
			if (isSetOld && !isSet)
				item.onUnequipFull(owner, stack, slotB);
			else if ((!isSetOld || slot.equals(slotB)) && isSet)
				item.onEquipFull(owner, stack, slotB);
		}*/
	}
}
