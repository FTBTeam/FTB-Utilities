package latmod.ftbu.world;

import com.mojang.authlib.GameProfile;

import ftb.lib.LMNBTUtils;
import latmod.ftbu.world.ranks.RankConfig;
import latmod.lib.ByteIOStream;
import latmod.lib.config.ConfigGroup;

public class LMPlayerClientSelf extends LMPlayerClient
{
	private final PersonalSettings settings;
	public final RankConfig rankConfig;
	public int claimedChunks;
	public int maxClaimPower;
	
	public LMPlayerClientSelf(LMWorldClient w, int i, GameProfile gp)
	{
		super(w, i, gp);
		
		settings = new PersonalSettings(this);
		rankConfig = new RankConfig();
	}
	
	public PersonalSettings getSettings()
	{ return settings; }
	
	public void readFromNet(ByteIOStream io, boolean self) // LMPlayerServer
	{
		super.readFromNet(io, self);
		
		if(self)
		{
			settings.readFromNet(io);
			
			commonPrivateData = LMNBTUtils.readTag(io);
			claimedChunks = io.readInt();
			maxClaimPower = io.readInt();
			
			ConfigGroup group = new ConfigGroup("rank");
			group.addAll(RankConfig.class, rankConfig);
			group.read(io);
		}
	}
}