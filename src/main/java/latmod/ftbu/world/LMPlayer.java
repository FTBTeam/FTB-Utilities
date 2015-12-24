package latmod.ftbu.world;

import com.mojang.authlib.GameProfile;
import cpw.mods.fml.relauncher.*;
import latmod.ftbu.world.ranks.Rank;
import latmod.lib.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.UUID;

public abstract class LMPlayer implements Comparable<LMPlayer> // LMPlayerServer // LMPlayerClient
{
	public final LMWorld world;
	public final int playerID;
	public GameProfile gameProfile;
	
	public final IntList friends;
	public final ItemStack[] lastArmor;
	protected NBTTagCompound commonPublicData = null;
	protected NBTTagCompound commonPrivateData = null;
	public boolean renderBadge;
	
	public LMPlayer(LMWorld w, int i, GameProfile gp)
	{
		world = w;
		playerID = i;
		gameProfile = gp;
		
		friends = new IntList();
		lastArmor = new ItemStack[5];
	}
	
	public abstract Side getSide();
	public abstract boolean isOnline();
	
	public final String getStringUUID()
	{ return LMStringUtils.fromUUID(getUUID()); }
	
	public abstract LMPlayerServer toPlayerMP();
	
	@SideOnly(Side.CLIENT)
	public abstract LMPlayerClient toPlayerSP();
	
	public abstract EntityPlayer getPlayer();
	
	public final String getName()
	{ return gameProfile.getName(); }
	
	public final UUID getUUID()
	{ return gameProfile.getId(); }
	
	public boolean isFriendRaw(LMPlayer p)
	{ return p != null && (playerID == p.playerID || friends.contains(p.playerID)); }
	
	public boolean isFriend(LMPlayer p)
	{ return isFriendRaw(p) && p.isFriendRaw(this); }
	
	public final int compareTo(LMPlayer o)
	{ return Integer.compare(playerID, o.playerID); }
	
	public String toString()
	{ return getName(); }
	
	public final int hashCode()
	{ return playerID; }
	
	public boolean equals(Object o)
	{
		if(o == null) return false;
		else if(o == this) return true;
		else if(o instanceof Integer || o instanceof LMPlayer)
		{ int h = o.hashCode(); return (h <= 0) ? false : h == playerID; }
		return o != null && (o == this || equalsPlayer(world.getPlayer(o)));
	}
	
	public boolean equalsPlayer(LMPlayer p)
	{ return p != null && (p == this || p.playerID == playerID); }
	
	public FastList<LMPlayer> getFriends()
	{
		FastList<LMPlayer> list = new FastList<LMPlayer>();
		for(int i = 0; i < friends.size(); i++)
		{
			LMPlayer p = world.getPlayer(friends.get(i));
			if(p != null) list.add(p);
		}
		return list;
	}
	
	public FriendStatus getStatus(LMPlayer p)
	{ return FriendStatus.get(this, p); }
	
	public final NBTTagCompound getPublicData()
	{
		if(commonPublicData == null)
			commonPublicData = new NBTTagCompound();
		return commonPublicData;
	}
	
	public final NBTTagCompound getPrivateData()
	{
		if(commonPrivateData == null)
			commonPrivateData = new NBTTagCompound();
		return commonPrivateData;
	}
	
	public PersonalSettings getSettings()
	{ return null; }
	
	public Rank getRank()
	{ return null; }
}