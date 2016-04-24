package ftb.utils.world;

import com.mojang.authlib.GameProfile;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ftb.lib.LMNBTUtils;
import ftb.lib.api.client.FTBLibClient;
import ftb.utils.api.EventLMPlayerClient;
import latmod.lib.LMUtils;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@SideOnly(Side.CLIENT)
public class LMWorldClient extends LMWorld // LMWorldServer
{
	public static LMWorldClient inst = null;
	
	public final Map<UUID, LMPlayerClient> playerMap;
	public final LMPlayerClientSelf clientPlayer;
	
	public LMWorldClient()
	{
		super(Side.CLIENT);
		playerMap = new HashMap<>();
		clientPlayer = new LMPlayerClientSelf(FTBLibClient.mc.getSession().func_148256_e());
	}
	
	@Override
	public Map<UUID, ? extends LMPlayer> playerMap()
	{ return playerMap; }
	
	@Override
	public World getMCWorld()
	{ return FTBLibClient.mc.theWorld; }
	
	@Override
	public LMWorldClient getClientWorld()
	{ return this; }
	
	@Override
	public LMPlayerClient getPlayer(Object o)
	{
		LMPlayer p = super.getPlayer(o);
		return (p == null) ? null : p.toPlayerSP();
	}
	
	public void readDataFromNet(NBTTagCompound tag, boolean first)
	{
		if(first)
		{
			playerMap.clear();
			
			NBTTagCompound playerData = tag.getCompoundTag("PD");
			
			NBTTagCompound tag1 = playerData.getCompoundTag(clientPlayer.getStringUUID());
			clientPlayer.readFromNet(tag1, true);
			playerMap.put(clientPlayer.getProfile().getId(), clientPlayer);
			playerData.removeTag(clientPlayer.getStringUUID());
			LMPlayerClient p;
			
			for(Map.Entry<String, NBTBase> e : LMNBTUtils.entrySet(playerData))
			{
				UUID uuid = LMUtils.fromString(e.getKey());
				
				if(e.getValue() instanceof NBTTagString)
				{
					p = new LMPlayerClient(new GameProfile(uuid, ((NBTTagString) e.getValue()).func_150285_a_()));
				}
				else
				{
					NBTTagCompound tag2 = (NBTTagCompound) e.getValue();
					p = new LMPlayerClient(new GameProfile(uuid, tag2.getString("N")));
					p.readFromNet(tag2, false);
					new EventLMPlayerClient.DataLoaded(p).post();
				}
				
				playerMap.put(uuid, p);
			}
			
			new EventLMPlayerClient.DataLoaded(clientPlayer).post();
		}
		
		settings.readFromNet(tag);
	}
}