package net.mrbt0907.util.util.math;

import net.minecraft.entity.Entity;
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

	/**Used to create 2D vectors for positioning calculations*/
	public Vec(Entity entity)
	{
		this.posX = entity.posX;
		this.posZ = entity.posZ;
	}
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
		return Maths.distance(this.posX, this.posZ, posX, posZ);
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
	
	/**Calculates the square rooted distance between this 2D vector and another set of positions*/
	public double distanceSq(double posX, double posZ)
	{
		return Maths.distanceSq(this.posX, this.posZ, posX, posZ);
	}
	
	/**Calculates the square rooted distance between this 2D vector and another 2D vector*/
	public double distanceSq(Vec vector)
	{
		return distanceSq(vector.posX, vector.posZ);
	}
	
	/**Calculates the square rooted distance between this 2D vector and another 3D vector. posY of the 3D vector is not used in the formula*/
	public double distanceSq(Vec3 vector)
	{
		return distanceSq(vector.posX, vector.posZ);
	}
	
	/**Calculates the speed of this vector*/
	public double speed()
	{
		return Maths.speed(posX, posZ);
	}
	
	/**Calculates the square rooted speed of this vector*/
	public double speedSq()
	{
		return Maths.speedSq(posX, posZ);
	}
}