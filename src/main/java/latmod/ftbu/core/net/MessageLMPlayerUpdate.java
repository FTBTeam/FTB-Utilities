package latmod.ftbu.core.net;
import io.netty.buffer.ByteBuf;
import latmod.ftbu.core.LMPlayer;
import latmod.ftbu.core.event.LMPlayerEvent;
import net.minecraft.nbt.NBTTagCompound;
import cpw.mods.fml.common.network.simpleimpl.*;
import cpw.mods.fml.relauncher.Side;

public class MessageLMPlayerUpdate extends MessageLM<MessageLMPlayerUpdate>
{
	public int playerID;
	public String action;
	public NBTTagCompound dat;
	
	public MessageLMPlayerUpdate() { }
	
	public MessageLMPlayerUpdate(LMPlayer p, String a)
	{
		playerID = p.playerID;
		action = a;
		
		dat = new NBTTagCompound();
		p.writeToNBT(dat, false);
	}
	
	public void fromBytes(ByteBuf bb)
	{
		playerID = bb.readInt();
		action = readString(bb);
		dat = readTagCompound(bb);
	}
	
	public void toBytes(ByteBuf bb)
	{
		bb.writeInt(playerID);
		writeString(bb, action);
		writeTagCompound(bb, dat);
	}
	
	public IMessage onMessage(MessageLMPlayerUpdate m, MessageContext ctx)
	{
		LMPlayer p = LMPlayer.getPlayer(m.playerID);
		p.readFromNBT(m.dat, false);
		new LMPlayerEvent.DataChanged(p, Side.CLIENT, action).post();
		return null;
	}
}