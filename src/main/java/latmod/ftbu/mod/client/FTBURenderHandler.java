package latmod.ftbu.mod.client;
import java.util.UUID;

import latmod.ftbu.core.client.badges.Badge;
import latmod.ftbu.core.util.*;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderPlayerEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public class FTBURenderHandler
{
	public static final FTBURenderHandler instance = new FTBURenderHandler();
	public static final FastList<GuiNotification> messages = new FastList<GuiNotification>();
	public static final FastMap<UUID, Badge> playerBadges = new FastMap<UUID, Badge>();
	
	@SubscribeEvent
	public void onPlayerRender(RenderPlayerEvent.Specials.Post e)
	{
		if(!Badge.reloading && FTBUClient.enablePlayerDecorators.getB() && !e.entityPlayer.isInvisible())
		{
			Badge b = playerBadges.get(e.entityPlayer.getUniqueID());
			if(b != null) b.onPlayerRender(e.entityPlayer);
		}
	}
	
	@SubscribeEvent
    public void renderTick(TickEvent.RenderTickEvent event)
    {
        Minecraft mc = Minecraft.getMinecraft();
        
        if(mc.theWorld != null && event.phase == TickEvent.Phase.END && !messages.isEmpty())
        {
        	GuiNotification m = messages.get(0);
        	m.render(mc); if(m.isDead()) messages.remove(0);
        }
    }
}