package ftb.utils.api.guide;

import net.minecraftforge.fml.common.eventhandler.Event;

public class EventFTBUClientGuide extends Event
{
	public final ClientGuideFile file;
	
	public EventFTBUClientGuide(ClientGuideFile f)
	{ file = f; }
}