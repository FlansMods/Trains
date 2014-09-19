package com.flansmod.common.trains;

/** The dihedral group of order 4. Simple as */
public enum D4 
{
	i, r, rr, rrr, s, rs, rrs, rrrs;
	
	private static final D4[][] compositionTable = new D4[][] 
	{
		{    i,    r,   rr,  rrr,    s,   rs,  rrs, rrrs },
		{    r,   rr,  rrr,    i,   rs,  rrs, rrrs,    s },
		{   rr,  rrr,    i,    r,  rrs, rrrs,    s,   rs },
		{  rrr,    i,    r,   rr, rrrs,    s,   rs,  rrs },
		{    s, rrrs,  rrs,   rs,    i,  rrr,   rr,    r },
		{   rs,    s, rrrs,  rrs,    r,    i,  rrr,   rr },
		{  rrs,   rs,    s, rrrs,   rr,    r,    i,  rrr },
		{ rrrs,  rrs,   rs,    s,  rrr,   rr,    r,    i }
	};
	
	public static D4 compose(D4 a, D4 b)
	{
		return compositionTable[a.ordinal()][b.ordinal()];
	}
	
	public static D4 get(String s)
	{
		for(D4 d4 : values())
			if(d4.toString().equals(s))
				return d4;
		return i;
	}
}
