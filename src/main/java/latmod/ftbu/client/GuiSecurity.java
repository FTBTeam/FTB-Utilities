package latmod.ftbu.client;

import static net.minecraft.util.EnumChatFormatting.GREEN;
import latmod.ftbu.FTBU;
import latmod.ftbu.core.LMPlayer;
import latmod.ftbu.core.gui.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import cpw.mods.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public class GuiSecurity extends GuiLM
{
	public static final ResourceLocation tex = FTBU.mod.getLocation("textures/gui/security.png");
	
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
		
		buttonSave.title = GREEN + FTBU.mod.translate("button.save");
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