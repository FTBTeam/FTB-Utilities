package ftb.utils.api.guide;

import ftb.lib.api.players.ForgePlayerMP;
import net.minecraftforge.fml.common.eventhandler.Event;

public class EventFTBUServerGuide extends Event
{
	public final ServerGuideFile file;
	public final ForgePlayerMP player;
	public final boolean isOP;
	
	public EventFTBUServerGuide(ServerGuideFile f, ForgePlayerMP p, boolean o)
	{
		file = f;
		player = p;
		isOP = o;
	}
}