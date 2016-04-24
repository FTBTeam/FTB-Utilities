package ftb.utils.world;

import com.mojang.authlib.GameProfile;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ftb.lib.api.friends.ILMPlayer;
import latmod.lib.LMUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public abstract class LMPlayer implements ILMPlayer, Comparable<ILMPlayer> // LMPlayerServer // LMPlayerClient
{
	private GameProfile gameProfile;
	
	public final Collection<UUID> friendsList;
	public final ItemStack[] lastArmor;
	protected NBTTagCompound commonPublicData = null;
	protected NBTTagCompound commonPrivateData = null;
	public boolean renderBadge;
	
	public LMPlayer(GameProfile gp)
	{
		gameProfile = gp;
		
		friendsList = new ArrayList<>();
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
	public final GameProfile getProfile()
	{ return gameProfile; }
	
	@Override
	public boolean isFriendRaw(ILMPlayer p)
	{ return p != null && (equalsPlayer(p) || friendsList.contains(p.getProfile().getId())); }
	
	@Override
	public boolean isFriend(ILMPlayer p)
	{ return p != null && isFriendRaw(p) && p.isFriendRaw(this); }
	
	@Override
	public final int compareTo(ILMPlayer o)
	{ return getProfile().getName().compareToIgnoreCase(o.getProfile().getName()); }
	
	@Override
	public String toString()
	{ return gameProfile.getName(); }
	
	@Override
	public final int hashCode()
	{ return getProfile().getId().hashCode(); }
	
	@Override
	public boolean equals(Object o)
	{
		if(o == null) return false;
		else if(o == this) return true;
		else if(o instanceof LMPlayer)
		{
			return ((LMPlayer) o).getProfile().equals(getProfile());
		}
		else if(o instanceof UUID)
		{
			return o.equals(getProfile().getId());
		}
		return o != null && (o == this || equalsPlayer(getWorld().getPlayer(o)));
	}
	
	public boolean equalsPlayer(ILMPlayer p)
	{ return p != null && (p == this || p.getProfile().equals(getProfile())); }
	
	public List<LMPlayer> getFriends()
	{
		ArrayList<LMPlayer> list = new ArrayList<>();
		for(UUID id : friendsList)
		{
			LMPlayer p = getWorld().getPlayer(id);
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