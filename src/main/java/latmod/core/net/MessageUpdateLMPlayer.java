package latmod.core.net;
import latmod.core.*;
import latmod.core.mod.*;
import net.minecraft.nbt.*;
import cpw.mods.fml.common.network.simpleimpl.*;

public class MessageUpdateLMPlayer extends MessageLM implements IMessageHandler<MessageUpdateLMPlayer, IMessage>
{
	public MessageUpdateLMPlayer() { }
	
	public MessageUpdateLMPlayer(LMPlayer p, String action)
	{
		data = new NBTTagCompound();
		data.setInteger("ID", p.playerID);
		
		NBTTagCompound data1 = new NBTTagCompound();
		p.writeToNBT(data1);
		data.setTag("D", data1);
		
		if(action != null && !action.isEmpty()) data.setString("A", action);
	}
	
	public IMessage onMessage(MessageUpdateLMPlayer m, MessageContext ctx)
	{
		if(LC.proxy.getClientWorld() == null) return null;
		
		int playerID = m.data.getInteger("ID");
		
		LMPlayer p = LMPlayer.getPlayer(playerID);
		
		p.readFromNBT(m.data.getCompoundTag("D"));
		String a = m.data.getString("A");
		
		if(!a.isEmpty()) p.receiveUpdate(a);
		return null;
	}
}