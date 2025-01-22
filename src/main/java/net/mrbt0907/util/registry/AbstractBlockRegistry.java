package net.mrbt0907.util.registry;

import java.util.LinkedList;
import java.util.List;
import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

public abstract class AbstractBlockRegistry
{
	private IForgeRegistry<Block> registry;
	private final List<Block> blocks = new LinkedList<Block>();
	private final String MODID;
	private final AbstractItemRegistry ITEM_REGISTRY;
	
	public AbstractBlockRegistry(String modid, @Nonnull AbstractItemRegistry itemRegistry)
	{
		MODID = modid;
		ITEM_REGISTRY = itemRegistry;
		MinecraftForge.EVENT_BUS.register(this);
	}

	public abstract void init();
	public abstract void register();
	
	@SubscribeEvent
	public void register(RegistryEvent.Register<Block> event)
	{
		registry = event.getRegistry();
		blocks.forEach(block -> registry.register(block));
		register();
		blocks.clear();
		registry = null;
	}
	
	public void addTileEntity(String registryName, Class<? extends TileEntity> tile)
	{
		if (tile != null)
			GameRegistry.registerTileEntity(tile, new ResourceLocation(MODID, registryName));
	}
	
	public void addOreDict(String oreDictName, Block block)
	{
		ITEM_REGISTRY.addOreDict(oreDictName, block);
	}
	
	public void addBlock(String registryName, Block block)
	{
		addBlock(registryName, null, block, null);
	}
	
	
	public void addBlock(String registryName, Block block, CreativeTabs creativeTab)
	{
		addBlock(registryName, null, block, creativeTab);
	}
	
	
	public void addBlock(String registryName, String oreDictName, Block block)
	{
		addBlock(registryName, oreDictName, block, null);
	}
	
	public void addBlock(String registryName, String oreDictName, Block block, CreativeTabs creativeTab)
	{
		block.setRegistryName(new ResourceLocation(MODID, registryName));
		block.setUnlocalizedName(registryName);
			
		if (creativeTab != null)
			block.setCreativeTab(creativeTab);
		if (registry != null)
			registry.register(block);
		else
			blocks.add(block);
		addOreDict(oreDictName, block);
		
		ITEM_REGISTRY.addBlock(block);
	}
}
