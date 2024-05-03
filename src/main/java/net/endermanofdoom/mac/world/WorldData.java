package net.endermanofdoom.mac.world;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import net.endermanofdoom.mac.util.FileUtil;
import net.minecraft.nbt.NBTTagCompound;

public class WorldData
{
	protected final String fileName;
	protected final NBTTagCompound nbt;
	private final List<String> knownUUIDS = new ArrayList<String>();
	
	public WorldData(String fileName)
	{
		this.fileName = fileName;
		nbt = FileUtil.loadCompactNBT(FileUtil.getWorldFolderPath() + "data", fileName, true);
	}
	
	public void save()
	{
		FileUtil.saveCompactNBT(FileUtil.getWorldFolderPath() + "data", fileName, nbt);
	}
	
	public boolean hasKey(String key)
	{
		return knownUUIDS.contains(key) ? nbt.hasUniqueId(key) : nbt.hasKey(key);
	}
	
	public void setBoolean(String key, boolean value)
	{
		nbt.setBoolean(key, value);
	}
	
	public boolean getBoolean(String key)
	{
		return nbt.getBoolean(key);
	}
	
	public void setByte(String key, byte value)
	{
		nbt.setByte(key, value);
	}
	
	public byte getByte(String key)
	{
		return nbt.getByte(key);
	}
	
	public void setInteger(String key, int value)
	{
		nbt.setInteger(key, value);
	}
	
	public int getInteger(String key)
	{
		return nbt.getInteger(key);
	}
	
	public void setShort(String key, short value)
	{
		nbt.setShort(key, value);
	}
	
	public short getShort(String key)
	{
		return nbt.getShort(key);
	}
	
	public void setLong(String key, long value)
	{
		nbt.setLong(key, value);
	}
	
	public long getLong(String key)
	{
		return nbt.getLong(key);
	}
	
	public void setFloat(String key, float value)
	{
		nbt.setFloat(key, value);
	}
	
	public float getFloat(String key)
	{
		return nbt.getFloat(key);
	}
	
	public void setDouble(String key, double value)
	{
		nbt.setDouble(key, value);
	}
	
	public double getDouble(String key)
	{
		return nbt.getDouble(key);
	}
	
	public void setUUID(String key, UUID value)
	{
		nbt.setUniqueId(key, value);
		knownUUIDS.add(key);
	}
	
	public UUID getUUID(String key)
	{
		return nbt.getUniqueId(key);
	}
	
	public <T> void setList(String key, List<T> value)
	{
		NBTTagCompound nbt = new NBTTagCompound();
		int type = getObjectType(value);
		
		switch (type)
		{
			case 0:
				for (T entry : value)
					nbt.setBoolean(key, (boolean) entry);
				break;
			case 1:
				for (T entry : value)
					nbt.setByte(key, (byte) entry);
				break;
			case 2:
				for (T entry : value)
					nbt.setInteger(key, (int) entry);
				break;
			case 3:
				for (T entry : value)
					nbt.setShort(key, (short) entry);
				break;
			case 4:
				for (T entry : value)
					nbt.setLong(key, (long) entry);
				break;
			case 5:
				for (T entry : value)
					nbt.setFloat(key, (float) entry);
				break;
			case 6:
				for (T entry : value)
					nbt.setDouble(key, (double) entry);
				break;
			case 7:
				for (T entry : value)
					nbt.setUniqueId(key, (UUID) entry);
				break;
			case 8:
				for (T entry : value)
					nbt.setString(key, (String) entry);
				break;
			default:
				return;
		}
		this.nbt.setTag(key, nbt);
	}
	
	public List<?> getList(String key, EnumType listType)
	{
		NBTTagCompound nbt = this.nbt.getCompoundTag(key);
		Set<String> keys = nbt.getKeySet();
		List<Object> list = new ArrayList<Object>();
		int type = listType.ordinal();
		
		switch (type)
		{
			case 0:
				for (String index : keys)
					list.add(nbt.getBoolean(index));
				break;
			case 1:
				for (String index : keys)
					list.add(nbt.getByte(index));
				break;
			case 2:
				for (String index : keys)
					list.add(nbt.getInteger(index));
				break;
			case 3:
				for (String index : keys)
					list.add(nbt.getShort(index));
				break;
			case 4:
				for (String index : keys)
					list.add(nbt.getLong(index));
				break;
			case 5:
				for (String index : keys)
					list.add(nbt.getFloat(index));
				break;
			case 6:
				for (String index : keys)
					list.add(nbt.getDouble(index));
				break;
			case 7:
				for (String index : keys)
					list.add(nbt.getUniqueId(index));
				break;
			case 8:
				for (String index : keys)
					list.add(nbt.getString(index));
				break;
			default:
				return null;
		}
		
		return list;
	}
	
	private int getObjectType(Object obj)
	{
		if (obj instanceof Boolean)
			return 0;
		else if (obj instanceof Byte)
			return 1;
		else if (obj instanceof Integer)
			return 2;
		else if (obj instanceof Short)
			return 3;
		else if (obj instanceof Long)
			return 4;
		else if (obj instanceof Float)
			return 5;
		else if (obj instanceof Double)
			return 6;
		else if (obj instanceof UUID)
			return 7;
		else if (obj instanceof String)
			return 8;
		else
			return -1;
	}
	
	public static enum EnumType
	{
		BOOLEAN, BYTE, INTEGER, SHORT, LONG, FLOAT, DOUBLE, UUID;
	}
}
