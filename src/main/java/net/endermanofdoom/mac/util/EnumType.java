package net.endermanofdoom.mac.util;

import java.util.List;
import java.util.UUID;

public enum EnumType
{
	BOOLEAN, BYTE, INTEGER, SHORT, LONG, FLOAT, DOUBLE, UUID, STRING, LIST, OTHER;
	
	public static EnumType getType(Object obj)
	{
		if (obj instanceof Boolean)
			return EnumType.BOOLEAN;
		else if (obj instanceof Byte)
			return EnumType.BYTE;
		else if (obj instanceof Integer)
			return EnumType.INTEGER;
		else if (obj instanceof Short)
			return EnumType.SHORT;
		else if (obj instanceof Long)
			return EnumType.LONG;
		else if (obj instanceof Float)
			return EnumType.FLOAT;
		else if (obj instanceof Double)
			return EnumType.DOUBLE;
		else if (obj instanceof UUID)
			return EnumType.UUID;
		else if (obj instanceof String)
			return EnumType.STRING;
		else if (obj instanceof List<?>)
			return EnumType.LIST;
		else
			return EnumType.OTHER;
	}
	
	public static EnumType valueOf(int type)
	{
		switch(type)
		{
			case 0: return BOOLEAN;
			case 1: return BYTE;
			case 2: return INTEGER;
			case 3: return SHORT;
			case 4: return LONG;
			case 5: return FLOAT;
			case 6: return DOUBLE;
			case 7: return UUID;
			case 8: return STRING;
			case 9: return LIST;
			default: return OTHER;
		}
	}
}