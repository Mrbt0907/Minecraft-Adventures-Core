package net.endermanofdoom.mac.internal.events;

import java.util.List;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

import net.endermanofdoom.mac.ClientProxy;
import net.endermanofdoom.mac.MACCore;
import net.endermanofdoom.mac.events.WorldEvent;
import net.endermanofdoom.mac.internal.music.MusicManager;
import net.endermanofdoom.mac.registry.MACAttributes;
import net.endermanofdoom.mac.world.WorldDataManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;

public class ClientEventHandler
{
	private static final Predicate<Entity> THING = Predicates.and(EntitySelectors.NOT_SPECTATING, new Predicate<Entity>()
	{
		public boolean apply(@Nullable Entity p_apply_1_)
		{
			return p_apply_1_ != null && p_apply_1_.canBeCollidedWith();
		}
	});
	
	private static final Minecraft MC = Minecraft.getMinecraft();
	private static boolean extendedAttack;
	
	@SubscribeEvent
	public static void onPlayerAttack(AttackEntityEvent event)
	{
		if (extendedAttack)
		{
			extendedAttack = false;
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setUniqueId("entityUUID", MC.objectMouseOver.entityHit.getUniqueID());
			MACCore.NETWORK.sendToServer(4, nbt);
		}
	}
	
	@SubscribeEvent
	public static void onRenderWorld(MouseEvent event)
	{
		Entity entity = MC.getRenderViewEntity();
		Entity pointedEntity;
		float partialTicks = MC.getRenderPartialTicks();
		
		if ((MC.objectMouseOver == null || MC.objectMouseOver.typeOfHit.equals(RayTraceResult.Type.MISS)) && entity != null)
		{
			if (MC.world != null)
			{
				MC.pointedEntity = null;
				IAttributeInstance attribute = MC.player.getAttributeMap().getAttributeInstance(MACAttributes.ATTACK_RANGE);
				double reach = attribute == null ? 3.0D : attribute.getAttributeValue();
				Vec3d eyeVector = entity.getPositionEyes(partialTicks);

				if (MC.playerController.extendedReach())
					reach += 3.0D;

				Vec3d rotation = entity.getLook(1.0F);
				Vec3d reachVector = eyeVector.addVector(rotation.x * reach, rotation.y * reach, rotation.z * reach);
				pointedEntity = null;
				Vec3d entityVector = null;
				List<Entity> list = MC.world.getEntitiesInAABBexcluding(entity, entity.getEntityBoundingBox().expand(rotation.x * reach, rotation.y * reach, rotation.z * reach).grow(1.0D, 1.0D, 1.0D), THING);
				double entityDistance = reach;

				for (int j = 0; j < list.size(); ++j)
				{
					Entity entity1 = list.get(j);
					AxisAlignedBB axisalignedbb = entity1.getEntityBoundingBox().grow((double)entity1.getCollisionBorderSize());
					RayTraceResult raytraceresult = axisalignedbb.calculateIntercept(eyeVector, reachVector);
						
					if (axisalignedbb.contains(eyeVector))
					{
						if (entityDistance >= 0.0D)
						{
							pointedEntity = entity1;
							entityVector = raytraceresult == null ? eyeVector : raytraceresult.hitVec;
							entityDistance = 0.0D;
						}
					}
					else if (raytraceresult != null)
					{
						double d3 = eyeVector.distanceTo(raytraceresult.hitVec);

						if (d3 < entityDistance || entityDistance == 0.0D)
							if (entity1.getLowestRidingEntity() == entity.getLowestRidingEntity() && !entity1.canRiderInteract())
							{
								if (entityDistance == 0.0D)
								{
									pointedEntity = entity1;
									entityVector = raytraceresult.hitVec;
								}
							}
							else
							{
								pointedEntity = entity1;
								entityVector = raytraceresult.hitVec;
								entityDistance = d3;
							}
					}
				}

				if (pointedEntity != null && eyeVector.distanceTo(entityVector) > reach)
				{
					pointedEntity = null;
					MC.objectMouseOver = new RayTraceResult(RayTraceResult.Type.MISS, entityVector, (EnumFacing)null, new BlockPos(entityVector));
				}

				if (pointedEntity != null && (entityDistance < reach || MC.objectMouseOver == null))
				{
					extendedAttack = true;
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