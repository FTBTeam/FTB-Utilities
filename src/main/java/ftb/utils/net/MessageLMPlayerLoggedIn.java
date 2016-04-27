package ftb.utils.net;

import com.mojang.authlib.GameProfile;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ftb.lib.api.net.LMNetworkWrapper;
import ftb.lib.api.net.MessageLM;
import ftb.utils.api.EventLMPlayerClient;
import ftb.utils.world.LMPlayerClient;
import ftb.utils.world.LMPlayerServer;
import ftb.utils.world.LMWorldClient;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;

import java.util.UUID;

public class MessageLMPlayerLoggedIn extends MessageLM<MessageLMPlayerLoggedIn>
{
	public UUID playerID;
	public String name;
	public boolean first;
	public NBTTagCompound tag;
	
	public MessageLMPlayerLoggedIn() { }
	
	public MessageLMPlayerLoggedIn(LMPlayerServer p, boolean f, boolean s)
	{
		playerID = p.getProfile().getId();
		name = p.getProfile().getName();
		first = f;
		tag = new NBTTagCompound();
		p.writeToNet(tag, s);
	}
	
	@Override
	public LMNetworkWrapper getWrapper()
	{ return FTBUNetHandler.NET; }
	
	@Override
	public void fromBytes(ByteBuf io)
	{
		playerID = readUUID(io);
		name = readString(io);
		first = io.readBoolean();
		tag = readTag(io);
	}
	
	@Override
	public void toBytes(ByteBuf io)
	{
		writeUUID(io, playerID);
		writeString(io, name);
		io.writeBoolean(first);
		writeTag(io, tag);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IMessage onMessage(MessageLMPlayerLoggedIn m, MessageContext ctx)
	{
		if(LMWorldClient.inst == null) return null;
		
		boolean isSelf = m.playerID.equals(LMWorldClient.inst.clientPlayer.getProfile().getId());
		
		LMPlayerClient p = LMWorldClient.inst.getPlayer(m.playerID);
		boolean add = p == null;
		if(add) p = new LMPlayerClient(new GameProfile(m.playerID, m.name));
		p.readFromNet(m.tag, isSelf);
		LMWorldClient.inst.playerMap.put(m.playerID, p);
		
		//TODO: if(isSelf)?
		new EventLMPlayerClient.LoggedIn(p, m.first).post();
		
		new EventLMPlayerClient.DataLoaded(p).post();
		return null;
	}
}