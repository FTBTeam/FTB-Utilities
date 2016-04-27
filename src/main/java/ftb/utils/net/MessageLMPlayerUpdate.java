package ftb.utils.net;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ftb.lib.api.client.FTBLibClient;
import ftb.lib.api.net.LMNetworkWrapper;
import ftb.lib.api.net.MessageLM;
import ftb.utils.api.EventLMPlayerClient;
import ftb.utils.world.LMPlayerClient;
import ftb.utils.world.LMPlayerServer;
import ftb.utils.world.LMWorldClient;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;

import java.util.UUID;

public class MessageLMPlayerUpdate extends MessageLM<MessageLMPlayerUpdate>
{
	public UUID playerID;
	public boolean self;
	public NBTTagCompound tag;
	
	public MessageLMPlayerUpdate() { }
	
	public MessageLMPlayerUpdate(LMPlayerServer p, boolean s)
	{
		playerID = p.getProfile().getId();
		self = s;
		tag = new NBTTagCompound();
		p.writeToNet(tag, self);
	}
	
	@Override
	public LMNetworkWrapper getWrapper()
	{ return FTBUNetHandler.NET; }
	
	@Override
	public void fromBytes(ByteBuf io)
	{
		playerID = readUUID(io);
		self = io.readBoolean();
		tag = readTag(io);
	}
	
	@Override
	public void toBytes(ByteBuf io)
	{
		writeUUID(io, playerID);
		io.writeBoolean(self);
		writeTag(io, tag);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IMessage onMessage(MessageLMPlayerUpdate m, MessageContext ctx)
	{
		LMPlayerClient p = LMWorldClient.inst.getPlayer(m.playerID);
		p.readFromNet(m.tag, m.self);
		new EventLMPlayerClient.DataChanged(p).post();
		FTBLibClient.onGuiClientAction();
		return null;
	}
}