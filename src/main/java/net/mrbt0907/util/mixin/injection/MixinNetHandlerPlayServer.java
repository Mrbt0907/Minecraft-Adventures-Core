package net.mrbt0907.util.mixin.injection;

import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.NetHandlerPlayServer;

@Mixin(NetHandlerPlayServer.class)
public class MixinNetHandlerPlayServer
{
	@ModifyVariable(method = "processUseEntity(Lnet/minecraft/network/play/client/CPacketUseEntity;)V", at = @At("STORE"), ordinal = 0, require = 0)
	private double reachDistance(double reach)
	{
		return Math.pow(((RangedAttribute)EntityPlayer.REACH_DISTANCE).maximumValue, 2.0D);
	}
}
