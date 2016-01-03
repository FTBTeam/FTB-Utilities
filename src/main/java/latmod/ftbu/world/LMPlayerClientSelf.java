package latmod.ftbu.world;

import com.mojang.authlib.GameProfile;
import ftb.lib.LMNBTUtils;
import latmod.ftbu.world.ranks.Rank;
import latmod.lib.ByteIOStream;

public class LMPlayerClientSelf extends LMPlayerClient
{
	private final PersonalSettings settings;
	private Rank rank;
	public int claimedChunks;
	public int loadedChunks;

	public LMPlayerClientSelf(LMWorldClient w, int i, GameProfile gp)
	{
		super(w, i, gp);
		
		settings = new PersonalSettings(this);
		rank = null;
	}

	public LMPlayerClientSelf toPlayerSPSelf()
	{ return this; }
	
	public PersonalSettings getSettings()
	{ return settings; }

	public Rank getRank()
	{
		if(rank == null)
			rank = new Rank("Client");
		return rank;
	}
	
	public void readFromNet(ByteIOStream io, boolean self) // LMPlayerServer
	{
		super.readFromNet(io, self);
		
		if(self)
		{
			settings.readFromNet(io);
			
			commonPrivateData = LMNBTUtils.readTag(io);
			claimedChunks = io.readUnsignedShort();
			loadedChunks = io.readUnsignedShort();

			rank = new Rank(io.readUTF());
			try { rank.readFromIO(io); }
			catch(Exception ex) { }
		}
	}
}