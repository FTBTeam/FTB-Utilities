package latmod.ftbu.net;
import cpw.mods.fml.common.network.simpleimpl.*;
import cpw.mods.fml.relauncher.*;
import latmod.core.util.ByteIOStream;
import latmod.ftbu.mod.FTBU;
import latmod.ftbu.util.LMGuiHandler;
import latmod.ftbu.util.client.LatCoreMCClient;
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
	
	public void readData(ByteIOStream io) throws Exception
	{
		modID = io.readString();
		guiID = io.readInt();
		data = LMNetHelper.readTagCompound(io);
		windowID = io.readUByte();
	}
	
	public void writeData(ByteIOStream io) throws Exception
	{
		io.writeString(modID);
		io.writeInt(guiID);
		LMNetHelper.writeTagCompound(io, data);
		io.writeUByte(windowID);
	}
	
	@SideOnly(Side.CLIENT)
	public IMessage onMessage(MessageOpenGui m, MessageContext ctx)
	{
		LMGuiHandler h = LMGuiHandler.Registry.getLMGuiHandler(m.modID);
		if(h != null && FTBU.proxy.openClientGui(LatCoreMCClient.mc.thePlayer, m.modID, m.guiID, data))
			LatCoreMCClient.mc.thePlayer.openContainer.windowId = m.windowID;
		return null;
	}
}