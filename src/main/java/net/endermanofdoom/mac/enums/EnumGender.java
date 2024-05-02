package net.endermanofdoom.mac.enums;

public enum EnumGender
{
	NONE("It", "It", "Its"),
	MALE("He", "Him", "His"),
	FEMALE("She", "Her", "Hers");
	
	private String singular1;
	private String singular2;
	private String second;
	
	EnumGender(String add1, String add2, String sec)
	{	
		this.singular1 = add1;
		this.singular2 = add2;
		this.second = sec;
	}
	
	public String getSingular1()
	{
		return singular1;
	}
	
	public String getSingular2()
	{
		return singular2;
	}
	
	public String get2nd()
	{
		return second;
	}
}
