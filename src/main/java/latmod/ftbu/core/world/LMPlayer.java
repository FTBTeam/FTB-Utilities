package latmod.ftbu.core.world;

import java.util.UUID;

import latmod.ftbu.core.LatCoreMC;
import latmod.ftbu.core.cmd.NameType;
import latmod.ftbu.core.event.LMPlayerEvent;
import latmod.ftbu.core.util.*;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import com.mojang.authlib.GameProfile;

import cpw.mods.fml.relauncher.*;

public abstract class LMPlayer implements Comparable<LMPlayer> //LMPlayerServer // LMPlayerClient
{
	public static final String ACTION_GENERAL = "-";
	public static final String ACTION_GROUPS_CHANGED = "ftbu.groups";
	
	public final LMWorld<?> world;
	public final int playerID;
	public final GameProfile gameProfile;
	
	public final String uuidString;
	public final IntList friends;
	public final ItemStack[] lastArmor;
	public int deaths;
	public NBTTagCompound commonData;
	
	public LMPlayer(LMWorld<?> w, int i, GameProfile gp)
	{
		world = w;
		playerID = i;
		gameProfile = gp;
		
		uuidString = LatCoreMC.toShortUUID(getUUID());
		friends = new IntList();
		lastArmor = new ItemStack[5];
		
		commonData = new NBTTagCompound();
	}
	
	public LMPlayerServer toPlayerMP()
	{ return (LMPlayerServer)this; }
	
	@SideOnly(Side.CLIENT)
	public LMPlayerClient toPlayerSP()
	{ return (LMPlayerClient)this; }
	
	public String getName()
	{ return gameProfile.getName(); }
	
	public UUID getUUID()
	{ return gameProfile.getId(); }
	
	public void onPostLoaded()
	{ new LMPlayerEvent.DataLoaded(this).post(); }
	
	public boolean isOnline()
	{ return false; }
	
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
	{ return o != null && (o == this || equalsPlayer(world.getPlayer(o))); }
	
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
}