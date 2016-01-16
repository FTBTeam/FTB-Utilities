package latmod.ftbu.mod.client;

import latmod.ftbu.util.client.LMFrustrumUtils;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public class FTBURenderHandler
{
	public static final FTBURenderHandler instance = new FTBURenderHandler();
	
	@SubscribeEvent
	public void renderWorld(RenderWorldLastEvent e)
	{
		//TODO: Move me to FTBLib
		LMFrustrumUtils.update();
	}
}