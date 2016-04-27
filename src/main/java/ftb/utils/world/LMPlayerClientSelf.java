package ftb.utils.world;

import com.mojang.authlib.GameProfile;
import ftb.lib.PrivacyLevel;
import latmod.lib.IntMap;
import net.minecraft.nbt.NBTTagCompound;

public class LMPlayerClientSelf extends LMPlayerClient
{
	private final PersonalSettings settings;
	public int claimedChunks;
	public int loadedChunks;
	public int maxClaimedChunks;
	public int maxLoadedChunks;
	
	public LMPlayerClientSelf(GameProfile gp)
	{
		super(gp);
		settings = new PersonalSettings();
	}
	
	@Override
	public LMPlayerClientSelf toPlayerSPSelf()
	{ return this; }
	
	@Override
	public PersonalSettings getSettings()
	{ return settings; }
	
	@Override
	public void readFromNet(NBTTagCompound tag, boolean self) // LMPlayerServer
	{
		super.readFromNet(tag, self);
		
		if(self)
		{
			IntMap map = new IntMap();
			map.list.addAll(tag.getIntArray("SP"));
			
			settings.flags = (byte) map.get(0);
			settings.blocks = PrivacyLevel.VALUES_3[map.get(1)];
			
			claimedChunks = map.get(2);
			loadedChunks = map.get(3);
			maxClaimedChunks = map.get(4);
			maxLoadedChunks = map.get(5);
			
			commonPrivateData = tag.hasKey("CPRD") ? tag.getCompoundTag("CPRD") : null;
		}
	}
}