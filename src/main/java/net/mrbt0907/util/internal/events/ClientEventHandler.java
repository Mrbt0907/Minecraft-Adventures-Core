package net.mrbt0907.util.internal.events;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.EntitySelectors;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.mrbt0907.util.ClientProxy;
import net.mrbt0907.util.events.WorldEvent;
import net.mrbt0907.util.world.WorldDataManager;

public class ClientEventHandler
{
	private static final Predicate<Entity> CAN_BE_HIT = Predicates.and(EntitySelectors.NOT_SPECTATING, new Predicate<Entity>() {public boolean apply(@Nullable Entity target){return target != null && (target.canBeCollidedWith() || target.getParts() != null);}});
	private static final Minecraft MC = Minecraft.getMinecraft();
	/*private static int part = -1;
	
	public static void getLook()
	{
		Entity player = MC.getRenderViewEntity();
		Entity pointedEntity;
		float partialTicks = MC.getRenderPartialTicks();
		
		if ((MC.objectMouseOver == null || MC.objectMouseOver.typeOfHit.equals(RayTraceResult.Type.MISS)) && player != null)
		{
			if (MC.world != null)
			{
				MC.pointedEntity = null;
				IAttributeInstance attribute = MC.player.getAttributeMap().getAttributeInstance(MACAttributes.ATTACK_RANGE);
				double reach = attribute == null ? 3.0D : attribute.getAttributeValue() + 3.0D;
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
					MC.objectMouseOver = new RayTraceResult(pointedEntity, entityVector);
					if (pointedEntity instanceof EntityLivingBase || pointedEntity instanceof EntityItemFrame)
						MC.pointedEntity = pointedEntity;
				}
			}
		}
	}*/
	
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
		
		WorldDataManager.tick();
	}
}