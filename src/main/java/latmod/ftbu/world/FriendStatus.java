package latmod.ftbu.world;

public enum FriendStatus
{
	NONE,
	FRIEND,
	INVITING,
	INVITED;
	
	public boolean isFriend()
	{ return this == FRIEND; }
	
	public boolean isFriendRaw()
	{ return this == FRIEND || this == INVITING; }
	
	public static FriendStatus get(LMPlayer p1, LMPlayer p2)
	{
		if(p1 == null || p2 == null) return NONE;
		
		boolean b1 = p1.isFriendRaw(p2);
		boolean b2 = p2.isFriendRaw(p1);
		
		if(b1 && b2) return FRIEND;
		if(b1 && !b2) return INVITING;
		if(!b1 && b2) return INVITED;
		return NONE;
	}

	public static int compare(LMPlayerClient o, LMPlayerClient p1, LMPlayerClient p2)
	{
		FriendStatus s0 = get(o, p1);
		FriendStatus s1 = get(o, p2);
		
		if(s0 == NONE && s1 != NONE) return 1;
		if(s0 != NONE && s1 == NONE) return -1;
		
		//if(s0 == s1)
		return Integer.compare(s0.ordinal(), s1.ordinal());
	}
}