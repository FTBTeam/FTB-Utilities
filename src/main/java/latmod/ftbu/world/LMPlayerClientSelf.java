package latmod.ftbu.world;

import com.mojang.authlib.GameProfile;

import ftb.lib.LMNBTUtils;
import latmod.ftbu.world.ranks.RankConfig;
import latmod.lib.ByteIOStream;

public class LMPlayerClientSelf extends LMPlayerClient
{
	public final RankConfig rankConfig;
	public int claimedChunks;
	public int maxClaimPower;
	
	public LMPlayerClientSelf(LMWorldClient w, int i, GameProfile gp)
	{
		super(w, i, gp);
		
		rankConfig = new RankConfig();
	}
	
	public void readFromNet(ByteIOStream io, boolean self)
	{
		super.readFromNet(io, self);
		
		if(self)
		{
			commonPrivateData = LMNBTUtils.readTag(io);
			claimedChunks = io.readInt();
			maxClaimPower = io.readInt();
		}
	}
}