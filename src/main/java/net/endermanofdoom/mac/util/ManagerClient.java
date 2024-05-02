package net.endermanofdoom.mac.util;

import net.endermanofdoom.mac.gui.GuiBossBarHud;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

public class ManagerClient extends Manager
{
	private final Minecraft mc;
	public final GuiBossBarHud inGameGui;
	
	public ManagerClient()
	{
		mc = Minecraft.getMinecraft();
		inGameGui = new GuiBossBarHud(mc);
	}
	
	public void tickGui(Phase phase, float delta)
	{
		if (phase.equals(Phase.START))
			inGameGui.tickPre(delta);
		else
			inGameGui.tickPost(delta);
	}
}
