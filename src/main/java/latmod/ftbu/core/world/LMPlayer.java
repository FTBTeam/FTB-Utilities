package latmod.ftbu.core.world;

import java.util.UUID;

import com.mojang.authlib.GameProfile;

import cpw.mods.fml.relauncher.*;
import latmod.ftbu.core.LatCoreMC;
import latmod.ftbu.core.cmd.NameType;
import latmod.ftbu.core.util.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public abstract class LMPlayer implements Comparable<LMPlayer> //LMPlayerServer // LMPlayerClient
{
	public final LMWorld<?> world;
	public final int playerID;
	public final GameProfile gameProfile;
	public final boolean isServer;
	public final Side side;
	
	public final String uuidString;
	public final IntList friends;
	public final ItemStack[] lastArmor;
	public int deaths;
	public NBTTagCompound commonPublicData;
	public NBTTagCompound commonPrivateData;
	public final FastList<Mail> mail;
	
	public long lastSeen;
	public long firstJoined;
	public boolean chatLinks;
	public int chunkMessages;
	public boolean renderBadge;
	
	public LMPlayer(LMWorld<?> w, int i, GameProfile gp)
	{
		world = w;
		playerID = i;
		gameProfile = gp;
		isServer = (this instanceof LMPlayerServer);
		side = isServer ? Side.SERVER : Side.CLIENT;
		
		uuidString = LatCoreMC.toShortUUID(getUUID());
		friends = new IntList();
		lastArmor = new ItemStack[5];
		
		commonPublicData = new NBTTagCompound();
		commonPrivateData = new NBTTagCompound();
		mail = new FastList<Mail>();
	}
	
	public abstract boolean isOnline();
	
	public abstract LMPlayerServer toPlayerMP();
	
	@SideOnly(Side.CLIENT)
	public abstract LMPlayerClient toPlayerSP();
	
	public abstract EntityPlayer getPlayer();
	
	public String getName()
	{ return gameProfile.getName(); }
	
	public UUID getUUID()
	{ return gameProfile.getId(); }
	
	public boolean isFriendRaw(LMPlayer p)
	{ return p != null && (playerID == p.playerID || friends.contains(p.playerID)); }
	
	public boolean isFriend(LMPlayer p)
	{ return isFriendRaw(p) && p.isFriendRaw(this); }
	
	public int compareTo(LMPlayer o)
	{ return Integer.compare(playerID, o.playerID); }
	
	public String toString()
	{ return getName(); }
	
	public int hashCode()
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
	
	public NameType getNameType()
	{ return isOnline() ? NameType.ON : NameType.OFF; }
	
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
}