package latmod.ftbu.core.net;
import cpw.mods.fml.common.network.simpleimpl.*;
import cpw.mods.fml.relauncher.*;
import io.netty.buffer.ByteBuf;
import latmod.ftbu.core.api.LMPlayerClientEvent;
import latmod.ftbu.core.client.LatCoreMCClient;
import latmod.ftbu.core.gui.IClientActionGui;
import latmod.ftbu.core.world.*;
import latmod.ftbu.mod.FTBU;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.nbt.NBTTagCompound;

public class MessageLMPlayerUpdate extends MessageLM<MessageLMPlayerUpdate> implements IClientMessageLM<MessageLMPlayerUpdate>
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
	
	public void fromBytes(ByteBuf bb)
	{
		playerID = bb.readInt();
		data = LMNetHelper.readTagCompound(bb);
	}
	
	public void toBytes(ByteBuf bb)
	{
		bb.writeInt(playerID);
		LMNetHelper.writeTagCompound(bb, data);
	}
	
	public IMessage onMessage(MessageLMPlayerUpdate m, MessageContext ctx)
	{ FTBU.proxy.handleClientMessage(m, ctx); return null; }
	
	@SideOnly(Side.CLIENT)
	public void onMessageClient(MessageLMPlayerUpdate m, MessageContext ctx)
	{
		LMPlayerClient p = LMWorldClient.inst.getPlayer(m.playerID);
		p.readFromNet(m.data, p.getUUID().equals(LatCoreMCClient.getUUID()));
		new LMPlayerClientEvent.DataChanged(p).post();
		
		GuiScreen g = LatCoreMCClient.getMinecraft().currentScreen;
		if(g != null && g instanceof IClientActionGui)
			((IClientActionGui)g).onClientDataChanged();
	}
}