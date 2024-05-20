package net.endermanofdoom.mac.registry;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.endermanofdoom.mac.MACCore;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.IForgeRegistry;

public abstract class AbstractItemRegistry
{
	private IForgeRegistry<Item> registry;
	private final List<Block> blocks = new LinkedList<Block>();
	private final Map<Item, Integer> items = new LinkedHashMap<Item, Integer>();
	private final Map<Item, String> oreDicts = new LinkedHashMap<Item, String>();
	private final Map<Block, String> oreDictBlocks = new LinkedHashMap<Block, String>();
	private final String MODID;
	
	protected AbstractItemRegistry(String modid)
	{
		MODID = modid;
		MinecraftForge.EVENT_BUS.register(this);
	}

	public abstract void init();
	public abstract void register();
	
	@SubscribeEvent
	public void register(RegistryEvent.Register<Item> event)
	{
		MACCore.debug("Registering items for mod " + MODID + "..." );
		registry = event.getRegistry();
		blocks.forEach(block -> addItem(block.getRegistryName().getResourcePath(), new ItemBlock(block)));
		items.forEach((item, meta) -> 
		{
			registry.register(item);
			if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
				for (int i = 0;i <= meta;i++)
					net.minecraftforge.client.model.ModelLoader.setCustomModelResourceLocation(item, i, new net.minecraft.client.renderer.block.model.ModelResourceLocation(MODID + ":" + item.getUnlocalizedName().substring(5), "inventory"));
		});
		oreDicts.forEach((item, oreDict) -> OreDictionary.registerOre(oreDict, item));
		oreDictBlocks.forEach((block, oreDict) -> OreDictionary.registerOre(oreDict, block));
		register();
		blocks.clear();
		items.clear();
		oreDicts.clear();
		registry = null;
	}
	
	public void addOreDict(String oreDictName, Item item)
	{
		if (oreDictName != null)
			if (registry == null)
				oreDicts.put(item, oreDictName);
			else
				OreDictionary.registerOre(oreDictName, item);
	}
	
	public void addOreDict(String oreDictName, Block block)
	{
		if (oreDictName != null)
			oreDictBlocks.put(block, oreDictName);
	}
	
	public void addBlock(Block block)
	{
		blocks.add(block);
	}
	
	public void addItem(String registryName, Item item)
	{
		addItem(registryName, null, item, null, 0);
	}
	
	public void addItem(String registryName, Item item, CreativeTabs creativeTab)
	{
		addItem(registryName, null, item, creativeTab, 0);
	}
	
	public void addItem(String registryName, String oreDictName, Item item)
	{
		addItem(registryName, oreDictName, item, null, 0);
	}

	public void addItem(String registryName, String oreDictName, Item item, CreativeTabs creativeTab)
	{
		addItem(registryName, oreDictName, item, null, 0);
	}
	
	public void addItem(String registryName, Item item, int meta)
	{
		addItem(registryName, null, item, null, meta);
	}
	
	
	public void addItem(String registryName, Item item, CreativeTabs creativeTab, int meta)
	{
		addItem(registryName, null, item, creativeTab, meta);
	}
	
	public void addItem(String registryName, String oreDictName, Item item, int meta)
	{
		addItem(registryName, oreDictName, item, null, meta);
	}
	
	public void addItem(String registryName, String oreDictName, Item item, CreativeTabs creativeTab, int meta)
	{
		item.setRegistryName(new ResourceLocation(MODID, registryName));
		item.setUnlocalizedName(registryName);
			
		
		if (creativeTab != null)
			item.setCreativeTab(creativeTab);
		if (registry != null)
			registry.register(item);
		else
			items.put(item, meta);
		addOreDict(oreDictName, item);
		
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT && registry != null)
			for (int i = 0;i <= meta;i++)
				net.minecraftforge.client.model.ModelLoader.setCustomModelResourceLocation(item, i, new net.minecraft.client.renderer.block.model.ModelResourceLocation(MODID + ":" + item.getUnlocalizedName().substring(5), "inventory"));
			
		MACCore.debug("Registered item " + item.getRegistryName());
	}
}