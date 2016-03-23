package ftb.utils.net;

import cpw.mods.fml.common.network.simpleimpl.*;
import cpw.mods.fml.relauncher.*;
import ftb.lib.api.client.FTBLibClient;
import ftb.lib.api.net.LMNetworkWrapper;
import ftb.utils.api.guide.GuidePage;
import ftb.utils.mod.client.gui.guide.GuiGuide;
import latmod.lib.ByteCount;
import latmod.lib.json.JsonElementIO;

public class MessageDisplayGuide extends MessageFTBU
{
	public MessageDisplayGuide() { super(ByteCount.INT); }
	
	public MessageDisplayGuide(GuidePage file)
	{
		this();
		file.cleanup();
		io.writeUTF(file.getID());
		JsonElementIO.write(io, file.getJson());
	}
	
	public LMNetworkWrapper getWrapper()
	{ return FTBUNetHandler.NET_INFO; }
	
	@SideOnly(Side.CLIENT)
	public IMessage onMessage(MessageContext ctx)
	{
		GuidePage file = new GuidePage(io.readUTF());
		file.setJson(JsonElementIO.read(io));
		FTBLibClient.openGui(new GuiGuide(null, file));
		return null;
	}
}