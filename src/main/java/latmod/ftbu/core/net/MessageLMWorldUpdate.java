package latmod.ftbu.core.net;
import java.util.UUID;

import cpw.mods.fml.common.network.simpleimpl.*;
import cpw.mods.fml.relauncher.*;
import io.netty.buffer.ByteBuf;
import latmod.ftbu.core.LatCoreMC;
import latmod.ftbu.core.api.LMClientWorldJoinedEvent;
import latmod.ftbu.core.world.*;
import latmod.ftbu.mod.FTBU;
import latmod.ftbu.mod.client.FTBUClient;
import net.minecraft.nbt.NBTTagCompound;

public class MessageLMWorldUpdate extends MessageLM<MessageLMWorldUpdate> implements IClientMessageLM<MessageLMWorldUpdate>
{
	public UUID worldID;
	public NBTTagCompound players;
	public int clientPlayerID;
	
	public MessageLMWorldUpdate() { }
	
	public MessageLMWorldUpdate(UUID id, int p)
	{
		worldID = id;
		players = new NBTTagCompound();
		LMWorldServer.inst.writePlayersToNet(players, p);
		clientPlayerID = p;
	}
	
	public void fromBytes(ByteBuf bb)
	{
		worldID = LMNetHelper.readUUID(bb);
		players = LMNetHelper.readTagCompound(bb);
		clientPlayerID = bb.readInt();
	}
	
	public void toBytes(ByteBuf bb)
	{
		LMNetHelper.writeUUID(bb, worldID);
		LMNetHelper.writeTagCompound(bb, players);
		bb.writeInt(clientPlayerID);
	}
	
	public IMessage onMessage(MessageLMWorldUpdate m, MessageContext ctx)
	{ FTBU.proxy.handleClientMessage(m, ctx); return null; }
	
	@SideOnly(Side.CLIENT)
	public void onMessageClient(MessageLMWorldUpdate m, MessageContext ctx)
	{
		if(LMWorldClient.inst != null); // LatCoreMC.logger.error("Current client world instance is not null!");
		LMWorldClient.inst = new LMWorldClient(m.worldID, LatCoreMC.toShortUUID(m.worldID), m.clientPlayerID);
		LMWorldClient.inst.readPlayersFromNet(m.players);
		LatCoreMC.logger.info("Joined the server with PlayerID " + LMWorldClient.inst.clientPlayer.playerID + " on world " + LMWorldClient.inst.worldIDS);
		FTBUClient.onWorldJoined();
		new LMClientWorldJoinedEvent().post();
	}
}