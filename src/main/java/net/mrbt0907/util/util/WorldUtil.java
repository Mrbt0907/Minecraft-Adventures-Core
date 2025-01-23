package net.mrbt0907.util.util;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import net.minecraft.entity.Entity;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.math.AxisAlignedBB;

public class WorldUtil
{
	public static final Predicate<Entity> CAN_BE_HIT = Predicates.and(EntitySelectors.NOT_SPECTATING, new Predicate<Entity>()
	{
		public boolean apply(@Nullable Entity target)
		{
			return target != null && (target.canBeCollidedWith() || target.getParts() != null);
		}
	});
	
	public static List<Entity> getEntities(Entity entity, AxisAlignedBB searchBoundingBox, Predicate<Entity> predicate)
	{
		List<Entity> entities = new ArrayList<Entity>();
		Entity target;
		Entity[] parts;
		AxisAlignedBB boundingBox;
		
		if (searchBoundingBox == null) return entities;
		for (int i = 0; i < entity.world.loadedEntityList.size(); i++)
		{
			target = entity.world.loadedEntityList.get(i);
			parts = target.getParts();
			
			if (!target.equals(entity))
			{
				boundingBox = target.getEntityBoundingBox();
				if (boundingBox != null && searchBoundingBox.intersects(boundingBox))
					if (predicate == null || predicate.test(target))
						entities.add(target);
				
				if (parts != null)
					for (Entity part : parts)
					{
						boundingBox = part.getEntityBoundingBox();
						if (boundingBox != null && searchBoundingBox.intersects(boundingBox))
							if (predicate == null || predicate.test(target))
								entities.add(part);
					}
			}
		}
		
		return entities;
	}
	
	public static List<Entity> getEntities(Entity entity, double radius, Predicate<Entity> predicate)
	{
		List<Entity> entities = new ArrayList<Entity>();
		Entity target;
		Entity[] parts;
		double distance;
		
		for (int i = 0; i < entity.world.loadedEntityList.size(); i++)
		{
			target = entity.world.loadedEntityList.get(i);
			parts = target.getParts();
			
			if (!target.equals(entity))
			{
				distance = target.getDistance(entity);
				if (distance <= radius)
				{
					if (predicate == null || predicate.test(target))
						entities.add(target);
				}
				
				if (parts != null)
					for (Entity part : parts)
					{
						distance = part.getDistance(entity);
						if (distance <= radius && (predicate == null || predicate.test(target)))
							entities.add(part);
					}
			}
		}
		
		return entities;
	}
}