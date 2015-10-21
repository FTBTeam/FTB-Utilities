package latmod.ftbu.net;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.*;
import cpw.mods.fml.relauncher.*;
import ftb.lib.client.FTBLibClient;
import io.netty.buffer.ByteBuf;
import latmod.ftbu.api.EventLMPlayerClient;
import latmod.ftbu.util.client.LatCoreMCClient;
import latmod.ftbu.world.*;
import net.minecraft.nbt.NBTTagCompound;

public class MessageLMPlayerUpdate extends MessageLM<MessageLMPlayerUpdate>
{
	public int playerID;
	public NBTTagCompound data;
	
	public MessageLMPlayerUpdate() { }
	
	public MessageLMPlayerUpdate(LMPlayerServer p, boolean self)
	{
		playerID = p.playerID;
		
		data = new NBTTagCompound();
		p.writeToNet(data, self);
	}
	
	public void fromBytes(ByteBuf io)
	{
		playerID = io.readInt();
		data = ByteBufUtils.readTag(io);
	}
	
	public void toBytes(ByteBuf io)
	{
		io.writeInt(playerID);
		ByteBufUtils.writeTag(io, data);
	}
	
	@SideOnly(Side.CLIENT)
	public IMessage onMessage(MessageLMPlayerUpdate m, MessageContext ctx)
	{
		LMPlayerClient p = LMWorldClient.inst.getPlayer(m.playerID);
		p.readFromNet(m.data, p.getUUID().equals(FTBLibClient.getUUID()));
		new EventLMPlayerClient.DataChanged(p).post();
		LatCoreMCClient.onGuiClientAction();
		return null;
	}
}