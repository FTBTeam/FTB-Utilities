package latmod.ftbu.core.net;
import io.netty.buffer.ByteBuf;

import java.util.UUID;

import latmod.ftbu.core.world.*;
import latmod.ftbu.mod.FTBU;
import net.minecraft.nbt.NBTTagCompound;
import cpw.mods.fml.common.network.simpleimpl.*;
import cpw.mods.fml.relauncher.*;

public class MessageLMWorldUpdate extends MessageLM<MessageLMWorldUpdate> implements IClientMessageLM<MessageLMWorldUpdate>
{
	public UUID worldID;
	public NBTTagCompound players;
	
	public MessageLMWorldUpdate() { }
	
	public MessageLMWorldUpdate(UUID id, LMPlayerServer self)
	{
		worldID = id;
		
		players = new NBTTagCompound();
		LMWorldServer.inst.writePlayersToNet(players, self);
	}
	
	public void fromBytes(ByteBuf bb)
	{
		worldID = new UUID(bb.readLong(), bb.readLong());
		players = LMNetHelper.readTagCompound(bb);
	}
	
	public void toBytes(ByteBuf bb)
	{
		bb.writeLong(worldID.getMostSignificantBits());
		bb.writeLong(worldID.getLeastSignificantBits());
		LMNetHelper.writeTagCompound(bb, players);
	}
	
	public IMessage onMessage(MessageLMWorldUpdate m, MessageContext ctx)
	{ FTBU.proxy.handleClientMessage(m, ctx); return null; }
	
	@SideOnly(Side.CLIENT)
	public void onMessageClient(MessageLMWorldUpdate m, MessageContext ctx)
	{
		LMWorldClient.inst = new LMWorldClient(m.worldID, true);
		LMWorldClient.inst.readPlayersFromNet(m.players);
	}
}