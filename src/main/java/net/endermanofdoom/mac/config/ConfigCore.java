package net.endermanofdoom.mac.config;

import net.endermanofdoom.mac.MACCore;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.Name;

@Config(modid = MACCore.MODID)
public class ConfigCore
{
	@Name("Enable Debug Mode")
	@Comment("When enabled, shows extra debugging information to the console")
	public static boolean debug_mode = false;
	
	@Name("Enable Warning Messages")
	@Comment("When enabled, shows warnings in the console")
	public static boolean warn_mode = true;
	
	@Name("Enable Error Messages")
	@Comment("When enabled, shows errors in the console")
	public static boolean error_mode = true;
}
