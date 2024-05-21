package net.endermanofdoom.mac.internal.client;

import net.endermanofdoom.mac.internal.events.ClientEventHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.entity.Entity;

public class EntityRendererEX extends EntityRenderer
{
	private static final Minecraft MC = Minecraft.getMinecraft();
	public EntityRendererEX(Minecraft mcIn, IResourceManager resourceManagerIn)
	{
		super(mcIn, resourceManagerIn);
	}
	
	@Override
	public void getMouseOver(float partialTicks)
    {
        Entity entity = MC.getRenderViewEntity();

        if (entity != null)
        {
            if (MC.world != null)
            {
                MC.mcProfiler.startSection("pick");
                double d0 = (double)MC.playerController.getBlockReachDistance();
                MC.objectMouseOver = entity.rayTrace(d0, partialTicks);
                ClientEventHandler.getLook();
                MC.mcProfiler.endSection();
            }
        }
    }
}
