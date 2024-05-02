package net.endermanofdoom.mac.util.math;

import net.minecraft.util.math.BlockPos;

/**Used to create 2D vectors for positioning calculations*/
public class Vec
{
	/**Position X of this 2D vector*/
	public double posX = 0.0D;
	/**Position Z of this 2D vector*/
	public double posZ = 0.0D;
	
	/**Used to create 2D vectors for positioning calculations*/
	public Vec() {}
	/**Used to create 2D vectors for positioning calculations*/
	public Vec(BlockPos pos)
	{
		this.posX = pos.getX();
		this.posZ = pos.getZ();
	}
	/**Used to create 2D vectors for positioning calculations*/
	public Vec(int posX, int posZ)
	{
		this.posX = posX;
		this.posZ = posZ;
	}
	/**Used to create 2D vectors for positioning calculations*/
	public Vec(float posX, float posZ)
	{
		this.posX = posX;
		this.posZ = posZ;
	}
	/**Used to create 2D vectors for positioning calculations*/
	public Vec(double posX, double posZ)
	{
		this.posX = posX;
		this.posZ = posZ;
	}
	
	public Vec copy()
	{
		return new Vec(posX, posZ);
	}
	
	public BlockPos toBlockPos(double posY)
	{
		return new BlockPos(posX, posY, posZ);
	}
	
	public net.minecraft.util.math.Vec3d toVec3MC()
	{
		return new net.minecraft.util.math.Vec3d(posX, 0.0D, posZ);
	}
	
	public Vec addVector(double x, double z)
	{
		posX += x;
		posZ += z;
		return this;
	}
	
	/**Calculates the distance between this 2D vector and another set of positions*/
	public double distance(double posX, double posZ)
	{
		return Math.sqrt((this.posX - posX) * (this.posX - posX) + (this.posZ - posZ) * (this.posZ - posZ));
	}
	
	/**Calculates the distance between this 2D vector and another 2D vector*/
	public double distance(Vec vector)
	{
		return distance(vector.posX, vector.posZ);
	}
	
	/**Calculates the distance between this 2D vector and another 3D vector. posY of the 3D vector is not used in the formula*/
	public double distance(Vec3 vector)
	{
		return distance(vector.posX, vector.posZ);
	}
	
	public double speed()
	{
		return Math.sqrt(posX * posX + posZ * posZ);
	}
}