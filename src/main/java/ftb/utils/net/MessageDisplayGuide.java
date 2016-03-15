package ftb.utils.net;

import cpw.mods.fml.common.network.simpleimpl.*;
import cpw.mods.fml.relauncher.*;
import ftb.lib.api.client.FTBLibClient;
import ftb.lib.api.net.LMNetworkWrapper;
import ftb.utils.api.guide.GuideFile;
import ftb.utils.mod.client.gui.guide.GuiGuide;
import latmod.lib.ByteCount;
import latmod.lib.json.JsonElementIO;

public class MessageDisplayGuide extends MessageFTBU
{
	public MessageDisplayGuide() { super(ByteCount.INT); }
	
	public MessageDisplayGuide(GuideFile file)
	{
		this();
		file.main.cleanup();
		JsonElementIO.write(io, file.getJson());
	}
	
	public LMNetworkWrapper getWrapper()
	{ return FTBUNetHandler.NET_INFO; }
	
	@SideOnly(Side.CLIENT)
	public IMessage onMessage(MessageContext ctx)
	{
		GuideFile file = new GuideFile("guide");
		file.setJson(JsonElementIO.read(io));
		FTBLibClient.openGui(new GuiGuide(null, file.main));
		return null;
	}
}