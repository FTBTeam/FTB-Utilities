package ftb.utils.net;

import com.google.gson.JsonElement;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ftb.lib.JsonHelper;
import ftb.lib.api.client.FTBLibClient;
import ftb.lib.api.item.LMInvUtils;
import ftb.lib.api.net.LMNetworkWrapper;
import ftb.lib.api.net.MessageLM;
import ftb.utils.world.LMPlayerClient;
import ftb.utils.world.LMPlayerServer;
import ftb.utils.world.LMWorldClient;
import ftb.utils.world.LMWorldServer;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IChatComponent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class MessageLMPlayerInfo extends MessageLM<MessageLMPlayerInfo>
{
	public UUID playerID;
	public List<JsonElement> info;
	public NBTTagCompound armor;
	public Collection<UUID> friends;
	
	public MessageLMPlayerInfo() { }
	
	public MessageLMPlayerInfo(LMPlayerServer owner, UUID id)
	{
		playerID = id;
		LMPlayerServer p = LMWorldServer.inst.getPlayer(playerID);
		
		List<IChatComponent> info0 = new ArrayList<>();
		p.getInfo(owner, info0);
		int s = Math.min(255, info0.size());
		
		info = new ArrayList<>();
		
		for(IChatComponent c : info0)
		{
			info.add(JsonHelper.serializeICC(c));
			if((--s) <= 0) break;
		}
		
		armor = new NBTTagCompound();
		LMInvUtils.writeItemsToNBT(p.lastArmor, armor, "A");
		
		friends = p.friendsList;
	}
	
	@Override
	public LMNetworkWrapper getWrapper()
	{ return FTBUNetHandler.NET_INFO; }
	
	@Override
	public void fromBytes(ByteBuf io)
	{
		playerID = readUUID(io);
		
		int s = io.readUnsignedShort();
		
		info = new ArrayList<>();
		
		for(int i = 0; i < s; i++)
		{
			info.add(readJsonElement(io));
		}
		
		s = io.readUnsignedShort();
		
		friends = new ArrayList<>();
		
		for(int i = 0; i < s; i++)
		{
			friends.add(readUUID(io));
		}
		
		armor = readTag(io);
	}
	
	@Override
	public void toBytes(ByteBuf io)
	{
		writeUUID(io, playerID);
		
		io.writeShort(info.size());
		
		for(JsonElement e : info)
		{
			writeJsonElement(io, e);
		}
		
		io.writeShort(friends.size());
		
		for(UUID id : friends)
		{
			writeUUID(io, id);
		}
		
		writeTag(io, armor);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IMessage onMessage(MessageLMPlayerInfo m, MessageContext ctx)
	{
		if(LMWorldClient.inst == null) return null;
		LMPlayerClient p = LMWorldClient.inst.getPlayer(m.playerID);
		if(p == null) return null;
		
		List<IChatComponent> info = new ArrayList<>();
		
		for(JsonElement e : m.info)
		{
			info.add(JsonHelper.deserializeICC(e));
		}
		
		p.receiveInfo(info);
		
		p.friendsList.clear();
		p.friendsList.addAll(m.friends);
		
		LMInvUtils.readItemsFromNBT(p.lastArmor, m.armor, "A");
		
		FTBLibClient.onGuiClientAction();
		return null;
	}
}