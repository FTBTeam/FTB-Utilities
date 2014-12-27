package latmod.core.net;
import java.util.UUID;

import latmod.core.LMPlayer;
import latmod.core.event.LMPlayerEvent;
import net.minecraft.nbt.NBTTagCompound;
import cpw.mods.fml.common.network.simpleimpl.*;
import cpw.mods.fml.relauncher.Side;

public class MessageUpdateLMPlayer extends MessageLM implements IMessageHandler<MessageUpdateLMPlayer, IMessage>
{
	public MessageUpdateLMPlayer() { }
	
	public MessageUpdateLMPlayer(LMPlayer p, String channel)
	{
		data = new NBTTagCompound();
		data.setLong("M", p.uuid.getMostSignificantBits());
		data.setLong("L", p.uuid.getLeastSignificantBits());
		NBTTagCompound data1 = new NBTTagCompound();
		p.writeToNBT(data1);
		data.setTag("D", data1);
		if(channel != null) data.setString("C", channel);
	}
	
	public IMessage onMessage(MessageUpdateLMPlayer m, MessageContext ctx)
	{
		UUID id = new UUID(m.data.getLong("M"), m.data.getLong("L"));
		LMPlayer p = LMPlayer.getPlayer(id);
		p.readFromNBT(m.data.getCompoundTag("D"));
		new LMPlayerEvent.DataChanged(p, Side.CLIENT, m.data.getString("C")).post();
		return null;
	}
}