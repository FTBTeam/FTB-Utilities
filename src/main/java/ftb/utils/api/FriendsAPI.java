package ftb.utils.api;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ftb.utils.world.LMPlayer;
import ftb.utils.world.LMWorldClient;
import ftb.utils.world.LMWorldServer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by LatvianModder on 23.03.2016.<br>
 * Designed for soft dependencies
 */
public class FriendsAPI
{
	/**
	 * Only server side! By default this value should be true, if the API is not present
	 */
	public static boolean areFriends(UUID player, UUID otherPlayer)
	{
		if(LMWorldServer.inst != null)
		{
			LMPlayer p0 = LMWorldServer.inst.getPlayer(player);
			if(p0 == null) return false;
			return p0.isFriend(LMWorldServer.inst.getPlayer(otherPlayer));
		}
		
		return false;
	}
	
	/**
	 * Only server side! By default this value should be an empty list
	 */
	public static List<UUID> getFriends(UUID player)
	{
		ArrayList<UUID> list = new ArrayList<>();
		
		if(LMWorldServer.inst != null)
		{
			LMPlayer p0 = LMWorldServer.inst.getPlayer(player);
			
			if(p0 == null) return list;
			
			for(LMPlayer p : p0.getFriends())
			{
				list.add(p.getProfile().getId());
			}
		}
		
		return list;
	}
	
	@SideOnly(Side.CLIENT)
	public static boolean isClientFriend(UUID otherPlayer)
	{
		if(LMWorldClient.inst != null && LMWorldClient.inst.clientPlayer != null)
		{
			return LMWorldClient.inst.clientPlayer.isFriend(LMWorldClient.inst.getPlayer(otherPlayer));
		}
		
		return false;
	}
	
	@SideOnly(Side.CLIENT)
	public static List<UUID> getClientFriends()
	{
		ArrayList<UUID> list = new ArrayList<>();
		
		if(LMWorldClient.inst != null && LMWorldClient.inst.clientPlayer != null)
		{
			for(LMPlayer p : LMWorldClient.inst.clientPlayer.getFriends())
			{
				list.add(p.getProfile().getId());
			}
		}
		
		return list;
	}
}