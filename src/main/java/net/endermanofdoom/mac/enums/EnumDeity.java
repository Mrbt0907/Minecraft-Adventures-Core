package net.endermanofdoom.mac.enums;

public enum EnumDeity
{
	UNDEFINED(0),
	GOD(1),
	TITAN(2),
	ABSTRACT(3);

    private final int id;
	
    private EnumDeity(int tierid)
    {
    	id = tierid;
    }

    public int getDeityType()
    {
        return this.id;
    }
}
