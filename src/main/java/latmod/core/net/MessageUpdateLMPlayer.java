package latmod.core.net;
import java.util.UUID;

import latmod.core.*;
import latmod.core.mod.LC;
import net.minecraft.nbt.NBTTagCompound;
import cpw.mods.fml.common.network.simpleimpl.*;

public class MessageUpdateLMPlayer extends MessageLM implements IMessageHandler<MessageUpdateLMPlayer, IMessage>
{
	public MessageUpdateLMPlayer() { }
	
	public MessageUpdateLMPlayer(LMPlayer p, boolean first, String action)
	{
		data = new NBTTagCompound();
		data.setInteger("ID", p.playerID);
		
		NBTTagCompound data1 = new NBTTagCompound();
		p.writeToNBT(data1);
		data.setTag("D", data1);
		
		if(first)
		{
			NBTTagCompound data2 = new NBTTagCompound();
			data2.setLong("M", p.uuid.getMostSignificantBits());
			data2.setLong("L", p.uuid.getLeastSignificantBits());
			data2.setString("U", p.username);
			data.setTag("FD", data2);
		}
		
		if(action != null && !action.isEmpty()) data.setString("A", action);
	}
	
	public IMessage onMessage(MessageUpdateLMPlayer m, MessageContext ctx)
	{
		if(LC.proxy.getClientWorld() == null) return null;
		
		int playerID = m.data.getInteger("ID");
		
		LMPlayer p = LMPlayer.getPlayer(playerID);
		
		if(p == null && data.hasKey("FD"))
		{
			p = new LMPlayer(playerID, new UUID(data.getLong("M"), data.getLong("L")), data.getString("U"));
			LMPlayer.map.put(p.playerID, p);
		}
		
		if(p != null)
		{
			p.readFromNBT(m.data.getCompoundTag("D"));
			String a = m.data.getString("A");
			
			if(!a.isEmpty()) p.receiveUpdate(a);
		}
		else LatCoreMC.printChat(LC.proxy.getClientPlayer(), "LatCoreMC error! PlayerID: " + playerID);
		
		return null;
	}
}