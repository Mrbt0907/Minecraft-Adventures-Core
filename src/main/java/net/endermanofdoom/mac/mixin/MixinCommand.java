package net.endermanofdoom.mac.mixin;

import org.spongepowered.asm.mixin.Mixin;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mixin(EntityCow.class)
public abstract class MixinCommand extends EntityAnimal
{
	public MixinCommand(World worldIn) {super(worldIn);}

	public float getBrightness()
	{
		return 1.0F;
	}
	
	@SideOnly(Side.CLIENT)
    public int getBrightnessForRender()
    {
		return 15728880;
    }
}
