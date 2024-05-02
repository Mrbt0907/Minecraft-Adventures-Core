package net.endermanofdoom.mac.enums;

public enum EnumTier
{
	UNDEFINED(0),
	TIER1(1),
	TIER2(2),
	TIER3(3),
	TIER4(4),
	TIER5(5),
	TIER6(6),
	TIER7(7),
	TIER8(8),
	TIER9(9),
	TIER10(10);

    private final int id;
	
    private EnumTier(int tierid)
    {
    	id = tierid;
    }

    public int getTierID()
    {
        return this.id;
    }
}
