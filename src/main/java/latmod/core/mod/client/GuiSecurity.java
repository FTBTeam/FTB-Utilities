package latmod.core.mod.client;

import static net.minecraft.util.EnumChatFormatting.GREEN;
import latmod.core.LMPlayer;
import latmod.core.gui.*;
import latmod.core.mod.LC;
import latmod.core.net.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import cpw.mods.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public class GuiSecurity extends GuiLM
{
	public static final ResourceLocation tex = LC.mod.getLocation("textures/gui/security.png");
	
	public final LMPlayer owner;
	
	public ButtonLM buttonSave;
	
	public GuiSecurity(EntityPlayer ep)
	{
		super(new ContainerEmpty(ep, null), tex);
		owner = LMPlayer.getPlayer(ep);
		
		xSize = 161;
		ySize = 184;
		
		widgets.add(buttonSave = new ButtonLM(this, 139, 6, 16, 16)
		{
			public void onButtonPressed(int b)
			{
				playClickSound();
				mc.thePlayer.closeScreen();
			}
		});
		
		buttonSave.title = GREEN + LC.mod.translate("button.save");
	}
	
	public void sendUpdate(int c, int u)
	{
		MessageLM.NET.sendToServer(new MessageManageGroups(owner, c, u, 0, null));
	}
	
	public void drawGuiContainerBackgroundLayer(float f, int mx, int my)
	{
		super.drawGuiContainerBackgroundLayer(f, mx, my);
	}
	
	public void drawText(int mx, int my)
	{
		super.drawText(mx, my);
	}	
}