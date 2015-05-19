package latmod.core.net;
import io.netty.buffer.ByteBuf;
import latmod.core.LMPlayer;
import latmod.core.mod.LC;
import net.minecraft.nbt.NBTTagCompound;
import cpw.mods.fml.common.network.simpleimpl.*;

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
		LC.proxy.playerLMDataChanged(p, m.action);
		return null;
	}
}