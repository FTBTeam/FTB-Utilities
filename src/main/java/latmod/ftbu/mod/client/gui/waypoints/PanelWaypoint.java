package latmod.ftbu.mod.client.gui.waypoints;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.*;
import latmod.ftbu.core.client.FTBULang;
import latmod.ftbu.core.gui.*;
import latmod.ftbu.core.util.LMColorUtils;
import latmod.ftbu.mod.client.gui.field.color.GuiSelectColorRGB;
import latmod.ftbu.mod.client.minimap.Waypoint;
import net.minecraft.client.gui.GuiYesNo;

@SideOnly(Side.CLIENT)
public class PanelWaypoint extends PanelLM
{
	public final Waypoint waypoint;
	public final ButtonLM edit, color, type, delete;
	
	public PanelWaypoint(PanelWaypoints p, Waypoint w)
	{
		super(p.gui, 0, p.height, p.width, 18);
		waypoint = w;
		
		edit = new ButtonLM(p.gui, 1, 1, p.gui.getFontRenderer().getStringWidth(waypoint.name) + 6, 16)
		{
			public void onButtonPressed(int b)
			{
				gui.playClickSound();
				
				if(b == 0)
					gui.mc.displayGuiScreen(new GuiEditWaypoint((GuiWaypoints)gui, waypoint));
				else
				{
					waypoint.enabled = !waypoint.enabled;
					gui.refreshWidgets();
				}
			}
		};
		
		edit.title = FTBULang.button_settings();
		
		color = new ButtonLM(p.gui, width - 54, 1, 16, 16)
		{
			public void onButtonPressed(int b)
			{
				gui.playClickSound();
				GuiSelectColorRGB.displayGui((GuiWaypoints)gui, waypoint.color, waypoint.listID, true);
			}
		};
		
		color.title = LMColorUtils.getHex(waypoint.color);
		
		type = new ButtonLM(p.gui, width - 36, 1, 16, 16)
		{
			public void onButtonPressed(int b)
			{
				gui.playClickSound();
				waypoint.type = waypoint.type.next();
				gui.refreshWidgets();
			}
		};
		
		type.title = waypoint.type.getIDS();
		
		delete = new ButtonLM(p.gui, width - 18, 1, 16, 16)
		{
			public void onButtonPressed(int b)
			{
				gui.playClickSound();
				gui.mc.displayGuiScreen(new GuiYesNo((GuiWaypoints)gui, FTBULang.deleteItem(waypoint.name), null, waypoint.listID));
			}
		};
		
		delete.title = FTBULang.button_remove();
	}
	
	public void addWidgets()
	{
		add(edit);
		add(color);
		add(type);
		add(delete);
	}
	
	public void renderWidget()
	{
		int ay = getAY();
		//if(ay >= gui.height || ay < -height) return;
		boolean mouseOver = mouseOver();
		GuiLM.drawBlankRect(0, ay, 0F, width, height, mouseOver ? 0x33FFFFFF : 0x33333333);
		gui.drawString(gui.getFontRenderer(), waypoint.name, 4, ay + 5, waypoint.enabled ? 0xFFFFFFFF : 0xFF777777);
		LMColorUtils.setGLColor(waypoint.color, 255);
		color.render(GuiIcons.color_blank);
		GL11.glColor4f(1F, 1F, 1F, 1F);
		type.render(waypoint.type.icon);
		if(delete.mouseOver()) delete.render(GuiIcons.remove);
		else
		{
			GL11.glColor4f(1F, 1F, 1F, 0.2F);
			delete.render(GuiIcons.remove_gray);
			GL11.glColor4f(1F, 1F, 1F, 1F);
		}
		
		if(edit.mouseOver())
			GuiLM.drawBlankRect(edit.posX, ay + 1, 0F, edit.width, edit.height, 0x33FFFFFF);
	}
}