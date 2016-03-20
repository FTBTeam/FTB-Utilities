package ftb.utils.net;

import ftb.lib.api.client.FTBLibClient;
import ftb.lib.api.net.*;
import ftb.utils.api.guide.GuidePage;
import ftb.utils.client.gui.GuiGuide;
import latmod.lib.ByteCount;
import latmod.lib.json.JsonElementIO;
import net.minecraftforge.fml.common.network.simpleimpl.*;
import net.minecraftforge.fml.relauncher.*;

public class MessageDisplayGuide extends MessageLM_IO
{
	public MessageDisplayGuide() { super(ByteCount.INT); }
	
	public MessageDisplayGuide(GuidePage file)
	{
		this();
		io.writeUTF(file.getID());
		JsonElementIO.write(io, file);
	}
	
	public LMNetworkWrapper getWrapper()
	{ return FTBUNetHandler.NET; }
	
	@SideOnly(Side.CLIENT)
	public IMessage onMessage(MessageContext ctx)
	{
		GuidePage file = new GuidePage(io.readUTF());
		JsonElementIO.read(io, file);
		FTBLibClient.openGui(new GuiGuide(null, file));
		return null;
	}
}