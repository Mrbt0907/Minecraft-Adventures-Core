package net.mrbt0907.util.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.Entity;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.mrbt0907.util.ClientProxy;
import net.mrbt0907.util.client.gui.GuiBossBarEntry;
import net.mrbt0907.util.interfaces.IBossBar;

@SideOnly(Side.CLIENT)
public class BossBarManager
{
	private static final Minecraft MC = Minecraft.getMinecraft();
	private static final Map<IBossBar, GuiBossBarEntry> ENTRIES = new HashMap<IBossBar, GuiBossBarEntry>();
	private static boolean inWorld;
	private static long ticks;
	
	@SubscribeEvent
	public static void tick(TickEvent.ClientTickEvent event)
	{
		if (inWorld)
		{
			if (MC.world == null)
			{
				inWorld = false;
				reset();
			}
			else
			{
				if (ticks % 10L == 0L)
					tickQueue();
				
				ENTRIES.values().forEach(entry -> {
					entry.onUpdate();
				});
			}
		}
		else if (MC.world != null)
			inWorld = true;
		
		ticks++;
	}
	
	private static void tickQueue()
	{
		List<Entity> entities = new ArrayList<Entity>(MC.world.loadedEntityList);
		List<GuiBossBarEntry> entries = new ArrayList<GuiBossBarEntry>(ENTRIES.values());
		
		for (GuiBossBarEntry entry : entries)
			if (entry.isDead())
				ENTRIES.remove(entry.getEntry());
		
		for (Entity entity : entities)
		{
			if (entity instanceof IBossBar)
			{
				IBossBar candidate = (IBossBar) entity;
				
				if (!ENTRIES.containsKey(candidate))
					ENTRIES.put(candidate, new GuiBossBarEntry(candidate));
			}
		}
	}
	
	@SubscribeEvent
	public static void onRenderOverlay(RenderGameOverlayEvent.Pre event)
	{
		if (!event.getType().equals(ElementType.HOTBAR) || MC.world == null || MC.player == null)
			return;
		ScaledResolution res = new ScaledResolution(MC);
		List<GuiBossBarEntry> entries = new ArrayList<GuiBossBarEntry>(ENTRIES.values());
		GuiBossBarEntry entry;
		int size = entries.size();
		int finalSize = res.getScaledHeight() / 65;
		for (int i = 0, ii = 0; i < size && ii < finalSize; i++)
		{
			entry = entries.get(i);
			if (entry.canRender(MC.player))
			{
				ClientProxy.BOSSBARS.renderNewBar(ii, entry);
				ii++;
			}
		};
	}
	
	public static void reset()
	{
		ENTRIES.clear();
	}
}
