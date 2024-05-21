package net.endermanofdoom.mac.internal.events;

import java.util.List;
import javax.annotation.Nullable;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import net.endermanofdoom.mac.ClientProxy;
import net.endermanofdoom.mac.MACCore;
import net.endermanofdoom.mac.events.WorldEvent;
import net.endermanofdoom.mac.internal.client.EntityRendererEX;
import net.endermanofdoom.mac.internal.music.MusicManager;
import net.endermanofdoom.mac.registry.MACAttributes;
import net.endermanofdoom.mac.world.WorldDataManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;

public class ClientEventHandler
{
	private static final Predicate<Entity> CAN_BE_HIT = Predicates.and(EntitySelectors.NOT_SPECTATING, new Predicate<Entity>() {public boolean apply(@Nullable Entity target){return target != null && (target.canBeCollidedWith() || target.getParts() != null);}});
	private static final Minecraft MC = Minecraft.getMinecraft();
	private static boolean extendedAttack;
	private static boolean leftClicked;
	private static int part = -1;
	
	@SubscribeEvent
	public static void onItemTooltip(ItemTooltipEvent event)
	{
		
	}
	
	public static void addTooltip(List<String> tooltips, ItemStack stack, String string)
	{
		String predicate;
		boolean found = false;
		
		if (stack.isItemDamaged())
        {
			predicate = I18n.format("item.durability", stack.getMaxDamage() - stack.getItemDamage(), stack.getMaxDamage());
			
        }
		else
		{
			predicate = TextFormatting.DARK_GRAY + ((ResourceLocation)Item.REGISTRY.getNameForObject(stack.getItem())).toString();
		}
		
		for (int i = tooltips.size() - 1; i > -1; i--)
			if (found)
			{
				tooltips.add(i, string);
				break;
			}
			else if (tooltips.get(i).equals(predicate))
				found = true;
	}
	
	@SubscribeEvent
	public static void onPlayerAttack(AttackEntityEvent event)
	{
		if (extendedAttack && leftClicked && MC.objectMouseOver != null && MC.objectMouseOver.entityHit != null)
		{
			leftClicked = false;
			extendedAttack = false;
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setUniqueId("entityUUID", MC.objectMouseOver.entityHit.getUniqueID());
			if (part > -1)
				nbt.setInteger("partIndex", part);
			MACCore.info("Hit");
			MACCore.NETWORK.sendToServer(4, nbt);
		}
	}
	
	@SubscribeEvent
	public static void onMouseEvent(MouseEvent event)
	{
		if (event.getButton() == 0 && event.isButtonstate())
			leftClicked = true;
		
		if (!MC.entityRenderer.getClass().equals(EntityRendererEX.class))
		{
			getLook();
		}
	}
	
	public static void getLook()
	{
		Entity player = MC.getRenderViewEntity();
		Entity pointedEntity;
		float partialTicks = MC.getRenderPartialTicks();
		
		if ((MC.entityRenderer.getClass().equals(EntityRendererEX.class) || MC.objectMouseOver == null || MC.objectMouseOver.typeOfHit.equals(RayTraceResult.Type.MISS)) && player != null)
		{
			if (MC.world != null)
			{
				MC.pointedEntity = null;
				IAttributeInstance attribute = MC.player.getAttributeMap().getAttributeInstance(MACAttributes.ATTACK_RANGE);
				double reach = attribute == null ? 3.0D : attribute.getAttributeValue();
				Vec3d eyeVector = player.getPositionEyes(partialTicks);

				if (MC.playerController.extendedReach())
					reach += 3.0D;

				Vec3d rotation = player.getLook(1.0F);
				Vec3d reachVector = eyeVector.addVector(rotation.x * reach, rotation.y * reach, rotation.z * reach);
				pointedEntity = null;
				Vec3d entityVector = null;
				
				List<Entity> targets = MC.world.loadedEntityList;
				double entityDistance = reach;
				for (int j = 0; j < targets.size(); ++j)
				{
					Entity entity1 = targets.get(j);
					if (entity1.equals(MC.player) || !CAN_BE_HIT.apply(entity1)) continue;
					AxisAlignedBB axisalignedbb = entity1.getEntityBoundingBox().grow((double)entity1.getCollisionBorderSize());
					RayTraceResult raytraceresult = axisalignedbb.calculateIntercept(eyeVector, reachVector);
					Entity[] parts = entity1.getParts();
					boolean flag = true;
					
					if (parts != null)
					{
						ClientEventHandler.part = -1;
						AxisAlignedBB axisalignedbbPart;
						RayTraceResult raytraceresultPart;
						Entity part;
						flag = entity1.canBeCollidedWith();
						for (int ii = 0; ii < parts.length; ii++)
						{
							part = parts[ii];
							axisalignedbbPart = part.getEntityBoundingBox().grow((double)part.getCollisionBorderSize());
							raytraceresultPart = axisalignedbbPart.calculateIntercept(eyeVector, reachVector);
							if (axisalignedbbPart.contains(eyeVector))
							{
								if (entityDistance >= 0.0D)
								{
									ClientEventHandler.part = ii;
									pointedEntity = entity1;
									entityVector = raytraceresult == null ? eyeVector : raytraceresult.hitVec;
									entityDistance = 0.0D;
								}
							}
							else if (raytraceresultPart != null)
							{
								double d3 = eyeVector.distanceTo(raytraceresultPart.hitVec);

								if (d3 < entityDistance || entityDistance == 0.0D)
									if (part.getLowestRidingEntity() == player.getLowestRidingEntity() && !part.canRiderInteract())
									{
										if (entityDistance == 0.0D)
										{
											ClientEventHandler.part = ii;
											pointedEntity = entity1;
											entityVector = raytraceresult == null ? eyeVector : raytraceresult.hitVec;
										}
									}
									else
									{
										ClientEventHandler.part = ii;
										pointedEntity = entity1;
										entityVector = raytraceresult == null ? eyeVector : raytraceresult.hitVec;
										entityDistance = d3;
									}
							}
						}
					}
					
					if (!flag || pointedEntity == entity1) continue;
					
					if (axisalignedbb.contains(eyeVector))
					{
						if (entityDistance >= 0.0D)
						{
							ClientEventHandler.part = parts == null ? -1 : ClientEventHandler.part;
							pointedEntity = entity1;
							entityVector = raytraceresult == null ? eyeVector : raytraceresult.hitVec;
							entityDistance = 0.0D;
						}
					}
					else if (raytraceresult != null)
					{
						double d3 = eyeVector.distanceTo(raytraceresult.hitVec);

						if (d3 < entityDistance || entityDistance == 0.0D)
							if (entity1.getLowestRidingEntity() == player.getLowestRidingEntity() && !entity1.canRiderInteract())
							{
								if (entityDistance == 0.0D)
								{
									ClientEventHandler.part = parts == null ? -1 : ClientEventHandler.part;
									pointedEntity = entity1;
									entityVector = raytraceresult == null ? eyeVector : raytraceresult.hitVec;
								}
							}
							else
							{
								ClientEventHandler.part = parts == null ? -1 : ClientEventHandler.part;
								pointedEntity = entity1;
								entityVector = raytraceresult == null ? eyeVector : raytraceresult.hitVec;
								entityDistance = d3;
							}
					}

					
				}

				if (pointedEntity != null && eyeVector.distanceTo(entityVector) > reach)
				{
					pointedEntity = null;
					MC.objectMouseOver = new RayTraceResult(RayTraceResult.Type.MISS, entityVector, (EnumFacing)null, new BlockPos(entityVector));
				}

				if (pointedEntity != null && (entityDistance < reach || MC.objectMouseOver == null || MC.objectMouseOver.typeOfHit.equals(RayTraceResult.Type.MISS)))
				{
					extendedAttack = part > -1 ? true : MC.playerController.extendedReach() ? reach > 6.0D : reach > 3.0D;
					MC.objectMouseOver = new RayTraceResult(pointedEntity, entityVector);
					if (pointedEntity instanceof EntityLivingBase || pointedEntity instanceof EntityItemFrame)
						MC.pointedEntity = pointedEntity;
				}
			}
		}
		else
			extendedAttack = false;
	}
	
	@SubscribeEvent
	public static void onWorldLeave(WorldEvent.Stop event)
	{
		ClientProxy.DIALOGUE.reset();
	}
	
	@SubscribeEvent
	public static void onClientTick(ClientTickEvent event)
	{
		if (MC.world == null && FMLClientHandler.instance().getServer() == null && WorldDataManager.isAvailable())
		{
			MinecraftForge.EVENT_BUS.post(new WorldEvent.Stop());
			WorldDataManager.reset();
		}
		
		MusicManager.update();
		WorldDataManager.tick();
	}
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onSoundPlayed(PlaySoundEvent event)
	{
		ISound sound = event.getResultSound();
		if (sound != null && sound.getCategory().equals(SoundCategory.MUSIC) && MusicManager.isMusicPlaying() && !MusicManager.isMusicPlaying(sound.getSoundLocation()))
		{
			MACCore.debug("Stopping music from playing: " + sound.getSoundLocation());
			event.setResultSound(null);
		}
	}
}