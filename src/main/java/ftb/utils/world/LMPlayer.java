package ftb.utils.world;

import com.mojang.authlib.GameProfile;
import cpw.mods.fml.relauncher.*;
import ftb.lib.api.friends.ILMPlayer;
import ftb.utils.mod.FTBUPermissions;
import latmod.lib.IntList;
import latmod.lib.json.UUIDTypeAdapterLM;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.*;

public abstract class LMPlayer implements ILMPlayer, Comparable<ILMPlayer> // LMPlayerServer // LMPlayerClient
{
	private final int playerID;
	private GameProfile gameProfile;
	
	public final IntList friends;
	public final ItemStack[] lastArmor;
	protected NBTTagCompound commonPublicData = null;
	protected NBTTagCompound commonPrivateData = null;
	public boolean renderBadge;
	
	public LMPlayer(int i, GameProfile gp)
	{
		playerID = i;
		gameProfile = new GameProfile(gp.getId(), gp.getName());
		
		friends = new IntList();
		lastArmor = new ItemStack[5];
		renderBadge = true;
	}
	
	public abstract LMWorld getWorld();
	
	public final String getStringUUID()
	{ return UUIDTypeAdapterLM.getString(gameProfile.getId()); }
	
	public abstract LMPlayerServer toPlayerMP();
	
	@SideOnly(Side.CLIENT)
	public abstract LMPlayerClient toPlayerSP();
	
	public void setProfile(GameProfile p)
	{ if(p != null) gameProfile = p; }
	
	public final int getPlayerID()
	{ return playerID; }
	
	public final GameProfile getProfile()
	{ return gameProfile; }
	
	public boolean isFriendRaw(ILMPlayer p)
	{ return p != null && (playerID == p.getPlayerID() || friends.contains(p.getPlayerID())); }
	
	public boolean isFriend(ILMPlayer p)
	{ return p != null && isFriendRaw(p) && p.isFriendRaw(this); }
	
	public final int compareTo(ILMPlayer o)
	{ return Integer.compare(playerID, o.getPlayerID()); }
	
	public String toString()
	{ return gameProfile.getName(); }
	
	public final int hashCode()
	{ return playerID; }
	
	public boolean equals(Object o)
	{
		if(o == null) return false;
		else if(o == this) return true;
		else if(o instanceof Integer || o instanceof LMPlayer)
		{
			int h = o.hashCode();
			return (h <= 0) ? false : h == playerID;
		}
		return o != null && (o == this || equalsPlayer(getWorld().getPlayer(o)));
	}
	
	public boolean equalsPlayer(LMPlayer p)
	{ return p != null && (p == this || p.playerID == playerID); }
	
	public List<LMPlayer> getFriends()
	{
		ArrayList<LMPlayer> list = new ArrayList<>();
		for(int i = 0; i < friends.size(); i++)
		{
			LMPlayer p = getWorld().getPlayer(friends.get(i));
			if(p != null) list.add(p);
		}
		return list;
	}
	
	public final NBTTagCompound getPublicData()
	{
		if(commonPublicData == null) commonPublicData = new NBTTagCompound();
		return commonPublicData;
	}
	
	public final NBTTagCompound getPrivateData()
	{
		if(commonPrivateData == null) commonPrivateData = new NBTTagCompound();
		return commonPrivateData;
	}
	
	public PersonalSettings getSettings()
	{ return null; }
	
	public boolean allowInteractSecure()
	{ return FTBUPermissions.allow_interact_secure.getBoolean(getProfile()); }
}