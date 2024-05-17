package net.endermanofdoom.mac.util.math;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class Vec3
{
	/**Position X of this 3D vector*/
	public double posX = 0.0D;
	/**Position Y of this 3D vector*/
	public double posY = 0.0D;
	/**Position Z of this 3D vector*/
	public double posZ = 0.0D;

	/**Used to create 3D vectors for positioning calculations*/
	public Vec3() {}
	/**Used to create 3D vectors for positioning calculations*/
	public Vec3(Vec3d pos)
	{
		posX = pos.x;
		posY = pos.y;
		posZ = pos.z;
	}
	/**Used to create 3D vectors for positioning calculations*/
	public Vec3(Entity entity)
	{
		posX = entity.posX;
		posY = entity.posY;
		posZ = entity.posZ;
	}
	/**Used to create 3D vectors for positioning calculations*/
	public Vec3(BlockPos pos)
	{
		posX = pos.getX();
		posY = pos.getY();
		posZ = pos.getZ();
	}
	/**Used to create 3D vectors for positioning calculations*/
	public Vec3(int posX, int posY, int posZ)
	{
		this.posX = posX;
		this.posY = posY;
		this.posZ = posZ;
	}
	/**Used to create 3D vectors for positioning calculations*/
	public Vec3(float posX, float posY, float posZ)
	{
		this.posX = posX;
		this.posY = posY;
		this.posZ = posZ;
	}
	/**Used to create 3D vectors for positioning calculations*/
	public Vec3(double posX, double posY, double posZ)
	{
		this.posX = posX;
		this.posY = posY;
		this.posZ = posZ;
	}
	
	public BlockPos toBlockPos()
	{
		return new BlockPos(posX, posY, posZ);
	}
	
	
	public net.minecraft.util.math.Vec3d toVec3MC()
	{
    	return new net.minecraft.util.math.Vec3d(posX, posY, posZ);
    }
	
	public Vec3 copy()
	{
		return new Vec3(posX, posY, posZ);
	}
	
	public Vec3 addVector(double x, double y, double z)
	{
		posX += x;
		posY += y;
		posZ += z;
		return this;
	}
	
	/**Calculates the distance between this 3D vector and another set of positions*/
	public double distance(double posX, double posY, double posZ)
	{
		return Maths.distance(this.posX, this.posY, this.posZ, posX, posY, posZ);
	}
	
	/**Calculates the distance between this 3D vector and another 2D vector. The posY value for the 2D vector is equal to posY of this vector*/
	public double distance(Vec vector)
	{
		return distance(vector.posX, posY, vector.posZ);
	}
	
	/**Calculates the distance between this 3D vector and another 3D vector*/
	public double distance(Vec3 vector)
	{
		return distance(vector.posX, vector.posY, vector.posZ);
	}
	
	/**Calculates the square rooted distance between this 3D vector and another set of positions*/
	public double distanceSq(double posX, double posY, double posZ)
	{
		return Maths.distanceSq(this.posX, this.posY, this.posZ, posX, posY, posZ);
	}
	
	/**Calculates the square rooted distance between this 3D vector and another 2D vector. The posY value for the 2D vector is equal to posY of this vector*/
	public double distanceSq(Vec vector)
	{
		return distanceSq(vector.posX, posY, vector.posZ);
	}
	
	/**Calculates the square rooted distance between this 3D vector and another 3D vector*/
	public double distanceSq(Vec3 vector)
	{
		return distanceSq(vector.posX, vector.posY, vector.posZ);
	}
	
	/**Calculates the square rooted speed of this vector*/
	public double speed()
	{
		return Maths.speed(posX, posY, posZ);
	}
	
	/**Calculates the square rooted speed of this vector*/
	public double speedSq()
	{
		return Maths.speedSq(posX, posY, posZ);
	}
}
