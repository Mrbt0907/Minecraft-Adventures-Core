package net.endermanofdoom.mac.internal;

import java.util.UUID;

import net.endermanofdoom.mac.registry.MACAttributes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class ExtendedReachHandler
{
	public static void onAttack(EntityPlayerMP player, UUID entityUUID)
	{
		MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
		Entity entity = server.getEntityFromUuid(entityUUID);
		if (entity == null)
			return;
		if (entity instanceof EntityItem || entity instanceof EntityXPOrb || entity instanceof EntityArrow || entity == player)
        {
            player.connection.disconnect(new TextComponentTranslation("multiplayer.disconnect.invalid_entity_attacked", new Object[0]));
            server.logWarning("Player " + player.getName() + " tried to attack an invalid entity");
            return;
        }
		IAttributeInstance attribute = player.getAttributeMap().getAttributeInstance(MACAttributes.ATTACK_RANGE);
		double reach = attribute == null ? 3.0D : attribute.getAttributeValue();
		
		if (player.canEntityBeSeen(entity) && player.getDistance(entity) < reach)
			player.attackTargetEntityWithCurrentItem(entity);
	}
}
