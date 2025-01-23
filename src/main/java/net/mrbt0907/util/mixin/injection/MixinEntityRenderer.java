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
				double d0 = (double)mc.playerController.getBlockReachDistance();
				mc.objectMouseOver = entity.rayTrace(d0, partialTicks);
				Vec3d vec3d = entity.getPositionEyes(partialTicks);
				double d1 = d0;

				if (mc.playerController.extendedReach())
				{
					d1 += 3.0D;
					d0 = d1;
				}

				if (mc.objectMouseOver != null)
					d1 = mc.objectMouseOver.hitVec.distanceTo(vec3d);

				Vec3d vec3d1 = entity.getLook(1.0F);
				Vec3d vec3d2 = vec3d.addVector(vec3d1.x * d0, vec3d1.y * d0, vec3d1.z * d0);
				pointedEntity = null;
				Vec3d vec3d3 = null;
				List<Entity> list = mc.world.getEntitiesInAABBexcluding(entity, entity.getEntityBoundingBox().expand(vec3d1.x * d0, vec3d1.y * d0, vec3d1.z * d0).grow(1.0D, 1.0D, 1.0D), Predicates.and(EntitySelectors.NOT_SPECTATING, new Predicate<Entity>()
				{
					public boolean apply(@Nullable Entity p_apply_1_)
					{
						return p_apply_1_ != null && p_apply_1_.canBeCollidedWith();
					}
				}));
				double d2 = d1;

				for (int j = 0; j < list.size(); ++j)
				{
					Entity entity1 = list.get(j);
					AxisAlignedBB axisalignedbb = entity1.getEntityBoundingBox().grow((double)entity1.getCollisionBorderSize());
					RayTraceResult raytraceresult = axisalignedbb.calculateIntercept(vec3d, vec3d2);

					if (axisalignedbb.contains(vec3d))
					{
						if (d2 >= 0.0D)
						{
							pointedEntity = entity1;
							vec3d3 = raytraceresult == null ? vec3d : raytraceresult.hitVec;
							d2 = 0.0D;
						}
					}
					else if (raytraceresult != null)
					{
						double d3 = vec3d.distanceTo(raytraceresult.hitVec);

						if (d3 < d2 || d2 == 0.0D)
						{
							if (entity1.getLowestRidingEntity() == entity.getLowestRidingEntity() && !entity1.canRiderInteract())
							{
								if (d2 == 0.0D)
								{
									pointedEntity = entity1;
									vec3d3 = raytraceresult.hitVec;
								}
							}
							else
							{
								pointedEntity = entity1;
								vec3d3 = raytraceresult.hitVec;
								d2 = d3;
							}
						}
					}
				}

				if (pointedEntity != null && (d2 < d1 || mc.objectMouseOver == null))
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