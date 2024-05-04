package net.endermanofdoom.mac.world;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import net.endermanofdoom.mac.MACCore;
import net.endermanofdoom.mac.util.EnumType;
import net.endermanofdoom.mac.util.FileUtil;
import net.minecraft.nbt.NBTTagCompound;

public class WorldData
{
	public boolean networkReady;
	public long networkNextTicks;
	protected final String fileName;
	protected NBTTagCompound nbt;
	protected final List<String> networkQueue = new ArrayList<String>();
	private final Map<String, EnumType> keyTypes = new HashMap<String, EnumType>();
	private final Map<String, EnumType> listTypes = new HashMap<String, EnumType>();
	
	
	public WorldData(String fileName)
	{
		if (!WorldDataManager.isAvailable())
			MACCore.fatal("Cannot create world data when the world has not started");
		if (WorldDataManager.exists(fileName))
			MACCore.fatal("Created duplicate world data objects");
		this.fileName = fileName;
		WorldDataManager.addWorldData(this);
		if (WorldDataManager.isRemote())
		{
			nbt = new NBTTagCompound();
			networkReady = true;
		}
		else
			nbt = FileUtil.loadCompactNBT(FileUtil.getWorldFolderPath() + FileUtil.getWorldFolderName() + "data", fileName, true);
	}
	
	public void save()
	{
		if (!WorldDataManager.exists(fileName))
		{
			MACCore.error("World Data " + fileName + " is not initialized. Create a new world data object");
			return;
		}
		if (WorldDataManager.isRemote())
		{
			MACCore.error("Cannot save world data " + fileName + " on the client");
			return;
		}
		FileUtil.saveCompactNBT(FileUtil.getWorldFolderPath() + FileUtil.getWorldFolderName() + "data", fileName, nbt);
	}
	
	public boolean hasKey(String key)
	{
		return keyTypes.get(key) == EnumType.UUID ? nbt.hasUniqueId(key) : nbt.hasKey(key);
	}
	
	public Object get(String key)
	{
		EnumType type = keyTypes.get(key);
		if (type == null) type = EnumType.OTHER;
		switch(type)
		{
			case BOOLEAN: return getBoolean(key);
			case BYTE: return getByte(key);
			case INTEGER: return getInteger(key);
			case SHORT: return getShort(key);
			case LONG: return getLong(key);
			case FLOAT: return getFloat(key);
			case DOUBLE: return getDouble(key);
			case UUID: return getUUID(key);
			case STRING: return getString(key);
			case LIST: return getList(key, listTypes.get(key));
			default: return null;
		}
	}
	
	public void setBoolean(String key, boolean value)
	{
		if (WorldDataManager.isRemote())
		{
			MACCore.error("Cannot set world data " + fileName + " on the client");
			return;
		}
		nbt.setBoolean(key, value);
		keyTypes.put(key, EnumType.BOOLEAN);
		networkQueue.add(key);
		networkReady = true;
	}
	
	public boolean getBoolean(String key)
	{
		return nbt.getBoolean(key);
	}
	
	public void setByte(String key, byte value)
	{
		if (WorldDataManager.isRemote())
		{
			MACCore.error("Cannot set world data " + fileName + " on the client");
			return;
		}
		nbt.setByte(key, value);
		keyTypes.put(key, EnumType.BYTE);
		networkQueue.add(key);
		networkReady = true;
	}
	
	public byte getByte(String key)
	{
		return nbt.getByte(key);
	}
	
	public void setInteger(String key, int value)
	{
		if (WorldDataManager.isRemote())
		{
			MACCore.error("Cannot set world data " + fileName + " on the client");
			return;
		}
		nbt.setInteger(key, value);
		keyTypes.put(key, EnumType.INTEGER);
		networkQueue.add(key);
		networkReady = true;
	}
	
	public int getInteger(String key)
	{
		return nbt.getInteger(key);
	}
	
	public void setShort(String key, short value)
	{
		if (WorldDataManager.isRemote())
		{
			MACCore.error("Cannot set world data " + fileName + " on the client");
			return;
		}
		nbt.setShort(key, value);
		keyTypes.put(key, EnumType.SHORT);
		networkQueue.add(key);
		networkReady = true;
	}
	
	public short getShort(String key)
	{
		return nbt.getShort(key);
	}
	
	public void setLong(String key, long value)
	{
		if (WorldDataManager.isRemote())
		{
			MACCore.error("Cannot set world data " + fileName + " on the client");
			return;
		}
		nbt.setLong(key, value);
		keyTypes.put(key, EnumType.LONG);
		networkQueue.add(key);
		networkReady = true;
	}
	
	public long getLong(String key)
	{
		return nbt.getLong(key);
	}
	
	public void setFloat(String key, float value)
	{
		if (WorldDataManager.isRemote())
		{
			MACCore.error("Cannot set world data " + fileName + " on the client");
			return;
		}
		nbt.setFloat(key, value);
		keyTypes.put(key, EnumType.FLOAT);
		networkQueue.add(key);
		networkReady = true;
	}
	
	public float getFloat(String key)
	{
		return nbt.getFloat(key);
	}
	
	public void setDouble(String key, double value)
	{
		if (WorldDataManager.isRemote())
		{
			MACCore.error("Cannot set world data " + fileName + " on the client");
			return;
		}
		nbt.setDouble(key, value);
		keyTypes.put(key, EnumType.DOUBLE);
		networkQueue.add(key);
		networkReady = true;
	}
	
	public double getDouble(String key)
	{
		return nbt.getDouble(key);
	}
	
	public void setUUID(String key, UUID value)
	{
		if (WorldDataManager.isRemote())
		{
			MACCore.error("Cannot set world data " + fileName + " on the client");
			return;
		}
		nbt.setUniqueId(key, value);
		keyTypes.put(key, EnumType.UUID);
		networkQueue.add(key);
		networkReady = true;
	}
	
	public UUID getUUID(String key)
	{
		return nbt.getUniqueId(key);
	}
	
	public void setString(String key, String value)
	{
		if (WorldDataManager.isRemote())
		{
			MACCore.error("Cannot set world data " + fileName + " on the client");
			return;
		}
		nbt.setString(key, value);
		keyTypes.put(key, EnumType.STRING);
		networkQueue.add(key);
		networkReady = true;
	}
	
	public String getString(String key)
	{
		return nbt.getString(key);
	}
	
	public <T> void setList(String key, List<T> value, EnumType listType)
	{
		if (WorldDataManager.isRemote())
		{
			MACCore.error("Cannot set world data " + fileName + " on the client");
			return;
		}
		NBTTagCompound nbt = new NBTTagCompound();
		EnumType type = listTypes.containsKey(key) ? listTypes.get(key) : listType;
		int size = value.size();
		
		switch (listType)
		{
			case BOOLEAN:
				for (int i = 0; i < size; i++)
					nbt.setBoolean(String.valueOf(i), (boolean) value.get(i));
				break;
			case BYTE:
				for (int i = 0; i < size; i++)
					nbt.setByte(String.valueOf(i), (byte) value.get(i));
				break;
			case INTEGER:
				for (int i = 0; i < size; i++)
					nbt.setInteger(String.valueOf(i), (int) value.get(i));
				break;
			case SHORT:
				for (int i = 0; i < size; i++)
					nbt.setShort(String.valueOf(i), (short) value.get(i));
				break;
			case LONG:
				for (int i = 0; i < size; i++)
					nbt.setLong(String.valueOf(i), (long) value.get(i));
				break;
			case FLOAT:
				for (int i = 0; i < size; i++)
					nbt.setFloat(String.valueOf(i), (float) value.get(i));
				break;
			case DOUBLE:
				for (int i = 0; i < size; i++)
					nbt.setDouble(String.valueOf(i), (double) value.get(i));
				break;
			case UUID:
				for (int i = 0; i < size; i++)
					nbt.setUniqueId(String.valueOf(i), (UUID) value.get(i));
				break;
			case STRING:
				for (int i = 0; i < size; i++)
					nbt.setString(String.valueOf(i), (String) value.get(i));
				break;
			default:
				return;
		}
		
		this.nbt.setTag(key, nbt);
		keyTypes.put(key, EnumType.LIST);
		listTypes.put(key, type);
		networkQueue.add(key);
		networkReady = true;
	}
	
	@SuppressWarnings("unchecked")
	public <T> List<T> getList(String key, EnumType listType)
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
		return (List<T>) list;
	}
	
	public NBTTagCompound build()
	{
		NBTTagCompound nbt = new NBTTagCompound();
		for (String key : networkQueue)
			nbt.setTag(key, buildKey(key));
		networkQueue.clear();
		return nbt;
	}
	
	public NBTTagCompound buildAll()
	{
		NBTTagCompound nbt = new NBTTagCompound();
		Set<String> keys = this.nbt.getKeySet();
		for (String key : keys)
			nbt.setTag(key, buildKey(key));
		return nbt;
	}
	
	private NBTTagCompound buildKey(String key)
	{
		NBTTagCompound nbt = new NBTTagCompound();
		Object value = get(key);
		EnumType type = keyTypes.get(key);
		if (type == null) type = EnumType.OTHER;
		nbt.setString("key", key);
		nbt.setInteger("type", type.ordinal());
		
		switch (type)
		{
			case BOOLEAN:
				nbt.setBoolean("value", (boolean) value);
				break;
			case BYTE:
				nbt.setByte("value", (byte) value);
				break;
			case INTEGER:
				nbt.setInteger("value", (int) value);
				break;
			case SHORT:
				nbt.setShort("value", (short) value);
				break;
			case LONG:
				nbt.setLong("value", (long) value);
				break;
			case FLOAT:
				nbt.setFloat("value", (float) value);
				break;
			case DOUBLE:
				nbt.setDouble("value", (double) value);
				break;
			case UUID:
				nbt.setUniqueId("value", (UUID) value);
				break;
			case STRING:
				nbt.setString("value", (String) value);
				break;
			case LIST:
				nbt.setInteger("listType", listTypes.get(key).ordinal());
				nbt.setTag("value", (NBTTagCompound) value);
				break;
			default:
		}
		
		return nbt;
	}
	
	public void onNetworkRecieved(NBTTagCompound nbt)
	{
		NBTTagCompound valueNbt;
		Set<String> keys = nbt.getKeySet();
		EnumType type, listType = null;
		
		for(String key : keys)
		{
			valueNbt = nbt.getCompoundTag(key);
			String index = valueNbt.getString("key");
			type = EnumType.valueOf(valueNbt.getInteger("type"));
			if (valueNbt.hasKey("listType"))
				listType = EnumType.valueOf(valueNbt.getInteger("listType"));
			switch (type)
			{
				case BOOLEAN:
					keyTypes.put(index, type);
					this.nbt.setBoolean(index, valueNbt.getBoolean("value"));
					break;
				case BYTE:
					keyTypes.put(index, type);
					this.nbt.setByte(index, valueNbt.getByte("value"));
					break;
				case INTEGER:
					keyTypes.put(index, type);
					this.nbt.setInteger(index, valueNbt.getInteger("value"));
					break;
				case SHORT:
					keyTypes.put(index, type);
					this.nbt.setShort(index, valueNbt.getShort("value"));
					break;
				case LONG:
					keyTypes.put(index, type);
					this.nbt.setLong(index, valueNbt.getLong("value"));
					break;
				case FLOAT:
					keyTypes.put(index, type);
					this.nbt.setFloat(index, valueNbt.getFloat("value"));
					break;
				case DOUBLE:
					keyTypes.put(index, type);
					this.nbt.setDouble(index, valueNbt.getDouble("value"));
					break;
				case UUID:
					keyTypes.put(index, type);
					this.nbt.setUniqueId(index, valueNbt.getUniqueId("value"));
					break;
				case STRING:
					keyTypes.put(index, type);
					this.nbt.setString(index, valueNbt.getString("value"));
					break;
				case LIST:
					keyTypes.put(index, type);
					listTypes.put(index, listType);
					this.nbt.setTag(index, valueNbt.getCompoundTag("value"));
					break;
				default:
					
			}
		}
	}
}
