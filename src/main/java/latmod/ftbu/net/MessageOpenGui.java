package latmod.ftbu.net;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.*;
import cpw.mods.fml.relauncher.*;
import ftb.lib.client.FTBLibClient;
import io.netty.buffer.ByteBuf;
import latmod.ftbu.mod.FTBU;
import latmod.ftbu.util.LMGuiHandler;
import net.minecraft.nbt.NBTTagCompound;

public class MessageOpenGui extends MessageLM<MessageOpenGui>
{
	public String modID;
	public int guiID;
	public NBTTagCompound data;
	public int windowID;
	
	public MessageOpenGui() { }
	
	public MessageOpenGui(String mod, int id, NBTTagCompound tag, int wid)
	{
		modID = mod;
		guiID = id;
		data = tag;
		windowID = wid;
	}
	
	public void fromBytes(ByteBuf io)
	{
		modID = ByteBufUtils.readUTF8String(io);
		guiID = io.readInt();
		data = ByteBufUtils.readTag(io);
		windowID = io.readUnsignedByte();
	}
	
	public void toBytes(ByteBuf io)
	{
		ByteBufUtils.writeUTF8String(io, modID);
		io.writeInt(guiID);
		ByteBufUtils.writeTag(io, data);
		io.writeByte(windowID);
	}
	
	@SideOnly(Side.CLIENT)
	public IMessage onMessage(MessageOpenGui m, MessageContext ctx)
	{
		LMGuiHandler h = LMGuiHandler.Registry.getLMGuiHandler(m.modID);
		if(h != null && FTBU.proxy.openClientGui(FTBLibClient.mc.thePlayer, m.modID, m.guiID, m.data))
			FTBLibClient.mc.thePlayer.openContainer.windowId = m.windowID;
		return null;
	}
}