package latmod.core.net;
import java.util.UUID;

import latmod.core.LMPlayer;
import latmod.core.event.LMPlayerEvent;
import net.minecraft.nbt.NBTTagCompound;
import cpw.mods.fml.common.network.simpleimpl.*;
import cpw.mods.fml.relauncher.Side;

public class MessageUpdatePlayerData extends MessageLM implements IMessageHandler<MessageUpdatePlayerData, IMessage>
{
	public MessageUpdatePlayerData() { }
	
	public MessageUpdatePlayerData(LMPlayer p, String channel)
	{
		data = new NBTTagCompound();
		data.setLong("M", p.uuid.getMostSignificantBits());
		data.setLong("L", p.uuid.getLeastSignificantBits());
		data.setString("U", p.username);
		data.setString("C", channel);
		NBTTagCompound data1 = new NBTTagCompound();
		p.writeToNBT(data1);
		data.setTag("D", data1);
		
	}
	
	public IMessage onMessage(MessageUpdatePlayerData m, MessageContext ctx)
	{
		UUID id = new UUID(m.data.getLong("M"), m.data.getLong("L"));
		LMPlayer p = LMPlayer.getPlayer(id);
		
		if(p == null)
		{
			p = new LMPlayer(id, m.data.getString("U"));
			LMPlayer.list.add(p);
		}
		
		p.readFromNBT(m.data.getCompoundTag("D"));
		new LMPlayerEvent.DataChanged(p, Side.CLIENT, m.data.getString("C")).post();
		
		return null;
	}
}