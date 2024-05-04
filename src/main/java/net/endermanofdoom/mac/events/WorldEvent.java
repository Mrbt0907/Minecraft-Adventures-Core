package net.endermanofdoom.mac.events;

import net.minecraftforge.fml.common.eventhandler.Event;

public class WorldEvent extends Event
{
	public static class Start extends WorldEvent {}
	public static class Stop extends WorldEvent {}
}
