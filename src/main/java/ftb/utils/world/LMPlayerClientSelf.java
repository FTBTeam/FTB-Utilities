package ftb.utils.world;

import com.mojang.authlib.GameProfile;
import ftb.lib.LMNBTUtils;
import latmod.lib.ByteIOStream;

public class LMPlayerClientSelf extends LMPlayerClient
{
	private final PersonalSettings settings;
	public int claimedChunks;
	public int loadedChunks;
	public int maxClaimedChunks;
	public int maxLoadedChunks;
	
	public LMPlayerClientSelf(LMWorldClient w, int i, GameProfile gp)
	{
		super(w, i, gp);
		
		settings = new PersonalSettings();
	}
	
	@Override
	public LMPlayerClientSelf toPlayerSPSelf()
	{ return this; }
	
	@Override
	public PersonalSettings getSettings()
	{ return settings; }
	
	@Override
	public void readFromNet(ByteIOStream io, boolean self) // LMPlayerServer
	{
		super.readFromNet(io, self);
		
		if(self)
		{
			settings.readFromNet(io);
			
			commonPrivateData = LMNBTUtils.readTag(io);
			claimedChunks = io.readUnsignedShort();
			loadedChunks = io.readUnsignedShort();
			maxClaimedChunks = io.readUnsignedShort();
			maxLoadedChunks = io.readUnsignedShort();
		}
	}
}