package com.feed_the_beast.ftbu.world;

import com.feed_the_beast.ftbl.api.ForgePlayer;
import com.feed_the_beast.ftbl.api.ForgePlayerData;
import com.feed_the_beast.ftbl.util.PrivacyLevel;
import latmod.lib.Bits;

/**
 * Created by LatvianModder on 11.02.2016.
 */
public abstract class FTBUPlayerData extends ForgePlayerData
{
	public static final byte RENDER_BADGE = 1;
	public static final byte CHAT_LINKS = 2;
	public static final byte EXPLOSIONS = 3;
	public static final byte FAKE_PLAYERS = 4;
	
	protected byte flags = 0;
	public PrivacyLevel blocks;
	
	FTBUPlayerData(String id, ForgePlayer p)
	{
		super(id, p);
		blocks = PrivacyLevel.FRIENDS;
	}
	
	public boolean getFlag(byte f)
	{ return Bits.getBit(flags, f); }
	
	public void setFlag(byte f, boolean b)
	{ flags = Bits.setBit(flags, f, b); }
}