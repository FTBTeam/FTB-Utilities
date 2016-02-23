package ftb.utils.api.guide;

import ftb.lib.api.players.LMPlayerMP;
import net.minecraftforge.fml.common.eventhandler.Event;

public class EventFTBUServerGuide extends Event
{
	public final ServerGuideFile file;
	public final LMPlayerMP player;
	public final boolean isOP;
	
	public EventFTBUServerGuide(ServerGuideFile f, LMPlayerMP p, boolean o)
	{
		file = f;
		player = p;
		isOP = o;
	}
}