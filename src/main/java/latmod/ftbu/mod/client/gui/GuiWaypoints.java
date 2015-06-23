package latmod.ftbu.mod.client.gui;

import latmod.ftbu.core.gui.*;
import latmod.ftbu.core.util.FastList;
import latmod.ftbu.mod.FTBU;
import net.minecraft.client.gui.*;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import cpw.mods.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public class GuiWaypoints extends GuiLM implements GuiYesNoCallback // GuiSelectWorld
{
	public static final ResourceLocation tex = FTBU.mod.getLocation("textures/gui/waypoints.png");
	
	public GuiWaypoints(EntityPlayer ep)
	{
		super(new ContainerEmpty(ep, null), tex);
		
        mc.displayGuiScreen(new GuiYesNo(this, "", "", "Share", I18n.format("gui.cancel"), 0));
	}
	
	public static GuiYesNo func_152129_a(GuiYesNoCallback p_152129_0_, String p_152129_1_, int p_152129_2_)
    {
        String s1 = I18n.format("selectWorld.deleteQuestion", new Object[0]);
        String s2 = "\'" + p_152129_1_ + "\' " + I18n.format("selectWorld.deleteWarning", new Object[0]);
        String s3 = I18n.format("selectWorld.deleteButton", new Object[0]);
        String s4 = I18n.format("gui.cancel", new Object[0]);
        GuiYesNo guiyesno = new GuiYesNo(p_152129_0_, s1, s2, s3, s4, p_152129_2_);
        return guiyesno;
    }
	
	public void addWidgets(FastList<WidgetLM> l)
	{
	}
	
	public void confirmClicked(boolean b, int i)
	{
		mc.displayGuiScreen(this);
	}
}