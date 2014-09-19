package com.flansmod.common.trains;

public enum EnumSwitchMode 
{
	MANUAL;
	
	public static EnumSwitchMode get(String s)
	{
		for(EnumSwitchMode e : values())
		{
			if(e.name().toLowerCase().equals(s.toLowerCase()))
				return e;
		}
		return MANUAL;
	}
}
