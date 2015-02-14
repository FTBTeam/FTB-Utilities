package latmod.core.net;
import java.util.UUID;

import latmod.core.LMPlayer;
import latmod.core.event.LMPlayerEvent;
import latmod.core.mod.LC;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import cpw.mods.fml.common.network.simpleimpl.*;
import cpw.mods.fml.relauncher.Side;

public class MessageUpdateLMPlayer extends MessageLM implements IMessageHandler<MessageUpdateLMPlayer, IMessage>
{
	public MessageUpdateLMPlayer() { }
	
	public MessageUpdateLMPlayer(LMPlayer p, byte action)
	{
		data = new NBTTagCompound();
		data.setInteger("ID", p.playerID);
		data.setLong("M", p.uuid.getMostSignificantBits());
		data.setLong("L", p.uuid.getLeastSignificantBits());
		NBTTagCompound data1 = new NBTTagCompound();
		p.writeToNBT(data1);
		data.setTag("D", data1);
		if(action > 0) data.setByte("A", action);
	}
	
	public IMessage onMessage(MessageUpdateLMPlayer m, MessageContext ctx)
	{
		if(LC.proxy.getClientWorld() == null) return null;
		
		int playerID = m.data.getInteger("ID");
		
		UUID id = new UUID(m.data.getLong("M"), m.data.getLong("L"));
		EntityPlayer ep = LC.proxy.getClientWorld().func_152378_a(id);
		if(ep == null) return null;
		
		LMPlayer p = LMPlayer.getPlayer(id);
		if(p == null)
		{
			p = new LMPlayer(playerID, id, ep.getCommandSenderName());
			LMPlayer.map.put(playerID, p);
		}
		
		p.readFromNBT(m.data.getCompoundTag("D"));
		byte c = m.data.getByte("A");
		new LMPlayerEvent.DataChanged(p, Side.CLIENT, c).post();
		
		if(c == LMPlayer.ACTION_LOGGED_IN)
			new LMPlayerEvent.LoggedIn(p, Side.CLIENT, ep, !p.isOld).post();
		
		if(c == LMPlayer.ACTION_LOGGED_OUT)
			new LMPlayerEvent.LoggedOut(p, Side.CLIENT, ep).post();
		
		return null;
	}
}