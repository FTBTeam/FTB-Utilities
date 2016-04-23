package ftb.utils.world;

import com.mojang.authlib.GameProfile;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ftb.lib.api.friends.ILMPlayer;
import latmod.lib.IntList;
import latmod.lib.LMUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.ArrayList;
import java.util.List;

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
		gameProfile = gp;
		
		friends = new IntList();
		lastArmor = new ItemStack[5];
		renderBadge = true;
	}
	
	public abstract LMWorld getWorld();
	
	public final String getStringUUID()
	{ return LMUtils.fromUUID(gameProfile.getId()); }
	
	public abstract LMPlayerServer toPlayerMP();
	
	@SideOnly(Side.CLIENT)
	public abstract LMPlayerClient toPlayerSP();
	
	public void setProfile(GameProfile p)
	{ if(p != null) gameProfile = p; }
	
	@Override
	public final int getPlayerID()
	{ return playerID; }
	
	@Override
	public final GameProfile getProfile()
	{ return gameProfile; }
	
	@Override
	public boolean isFriendRaw(ILMPlayer p)
	{ return p != null && (playerID == p.getPlayerID() || friends.contains(p.getPlayerID())); }
	
	@Override
	public boolean isFriend(ILMPlayer p)
	{ return p != null && isFriendRaw(p) && p.isFriendRaw(this); }
	
	@Override
	public final int compareTo(ILMPlayer o)
	{ return Integer.compare(playerID, o.getPlayerID()); }
	
	@Override
	public String toString()
	{ return gameProfile.getName(); }
	
	@Override
	public final int hashCode()
	{ return playerID; }
	
	@Override
	public boolean equals(Object o)
	{
		if(o == null) return false;
		else if(o == this) return true;
		else if(o instanceof Integer || o instanceof LMPlayer)
		{
			int h = o.hashCode();
			return h > 0 && h == playerID;
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
	
	@Override
	public boolean allowInteractSecure()
	{ return false; }
}