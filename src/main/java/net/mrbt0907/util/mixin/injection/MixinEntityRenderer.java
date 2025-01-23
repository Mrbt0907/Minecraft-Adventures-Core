package net.mrbt0907.util.mixin.injection;

import java.util.List;

import javax.annotation.Nullable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.mrbt0907.util.mixin.CameraHandler;
import net.mrbt0907.util.util.WorldUtil;

@Mixin(EntityRenderer.class)
public class MixinEntityRenderer
{
	@Shadow
	private Minecraft mc;
	@Shadow
	private Entity pointedEntity;
	
	/** Injecting into getMouseOver allows us to add extra range to weapons and tools */
	@Inject(method = "getMouseOver(F)V", at = @At("HEAD"), cancellable = true, require = -1)
	public void getMouseOver(float partialTicks, CallbackInfo callback)
	{
		Entity entity = mc.getRenderViewEntity();

		if (entity != null)
		{
			if (mc.world != null)
			{
				mc.mcProfiler.startSection("pick");
				mc.pointedEntity = null;
				double reachDistance = (double) mc.playerController.getBlockReachDistance();
				double d0 = reachDistance;
				
				mc.objectMouseOver = entity.rayTrace(reachDistance, partialTicks);
				Vec3d vec3d = entity.getPositionEyes(partialTicks);

				if (mc.playerController.extendedReach())
				{
					d0 += 3.0D;
					reachDistance = d0;
				}

				if (mc.objectMouseOver != null)
					d0 = mc.objectMouseOver.hitVec.distanceTo(vec3d);

				Vec3d vec3d1 = entity.getLook(1.0F);
				Vec3d vec3d2 = vec3d.addVector(vec3d1.x * reachDistance, vec3d1.y * reachDistance, vec3d1.z * reachDistance);
				pointedEntity = null;
				Vec3d vec3d3 = null;
				
				List<Entity> entities = WorldUtil.getEntities(entity, entity.getEntityBoundingBox().expand(vec3d1.x * reachDistance, vec3d1.y * reachDistance, vec3d1.z * reachDistance).grow(1.0D, 1.0D, 1.0D), WorldUtil.CAN_BE_HIT);
				AxisAlignedBB boundingBox;
				RayTraceResult result;
				double d1 = d0;
				
				for (Entity target : entities)
				{
					boundingBox = target.getEntityBoundingBox().grow((double)target.getCollisionBorderSize());
					result = boundingBox.calculateIntercept(vec3d, vec3d2);

					if (boundingBox.contains(vec3d))
					{
						if (d1 >= 0.0D)
						{
							pointedEntity = target;
							vec3d3 = result == null ? vec3d : result.hitVec;
							d1 = 0.0D;
						}
					}
					else if (result != null)
					{
						double d3 = vec3d.distanceTo(result.hitVec);

						if (d3 < d1 || d1 == 0.0D)
						{
							if (target.getLowestRidingEntity() == entity.getLowestRidingEntity() && !target.canRiderInteract())
							{
								if (d1 == 0.0D)
								{
									pointedEntity = target;
									vec3d3 = result.hitVec;
								}
							}
							else
							{
								pointedEntity = target;
								vec3d3 = result.hitVec;
								d1 = d3;
							}
						}
					}
				}

				if (pointedEntity != null && (d1 < d0 || mc.objectMouseOver == null))
				{
					mc.objectMouseOver = new RayTraceResult(pointedEntity, vec3d3);

					if (pointedEntity instanceof EntityLivingBase || pointedEntity instanceof EntityItemFrame)
						mc.pointedEntity = pointedEntity;
				}

				mc.mcProfiler.endSection();
			}
		}
		callback.cancel();
	}
	
	/** Injecting into orientCamera gives us the perfect spot to shake the perspective */
	@Inject(method = "orientCamera(F)V", at = @At("RETURN"))
	private void orientCamera(float partialTicks, CallbackInfo callback)
    {
		if (!mc.isGamePaused())
			CameraHandler.applyTransform(partialTicks);
    }
}