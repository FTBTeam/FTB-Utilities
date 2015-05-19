package latmod.core.net;
import io.netty.buffer.ByteBuf;
import latmod.core.LatCoreMC;
import latmod.core.mod.LC;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import cpw.mods.fml.common.network.simpleimpl.*;

public class MessageOpenGui extends MessageLM<MessageOpenGui>
{
	public String guiID;
	public NBTTagCompound data;
	public int windowID;
	
	public MessageOpenGui() { }
	
	public MessageOpenGui(String id, NBTTagCompound tag, int wid)
	{
		guiID = id;
		data = tag;
		windowID = wid;
	}
	
	public void fromBytes(ByteBuf bb)
	{
		guiID = readString(bb);
		data = readTagCompound(bb);
		windowID = bb.readInt();
	}
	
	public void toBytes(ByteBuf bb)
	{
		writeString(bb, guiID);
		writeTagCompound(bb, data);
		bb.writeInt(windowID);
	}
	
	public IMessage onMessage(MessageOpenGui m, MessageContext ctx)
	{
		EntityPlayer player = LC.proxy.getClientPlayer();
		LatCoreMC.openGui(player, m.guiID, m.data);
        player.openContainer.windowId = m.windowID;
        return null;
	}
}