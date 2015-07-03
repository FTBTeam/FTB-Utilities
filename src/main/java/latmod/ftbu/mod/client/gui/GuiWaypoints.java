package latmod.ftbu.mod.client.gui;

import java.util.Arrays;

import latmod.ftbu.core.FTBULang;
import latmod.ftbu.core.gui.*;
import latmod.ftbu.core.util.*;
import latmod.ftbu.mod.FTBU;
import latmod.ftbu.mod.client.minimap.*;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public class GuiWaypoints extends GuiLM
{
	public static final ResourceLocation tex = FTBU.mod.getLocation("textures/gui/waypoints.png");
	public static final TextureCoords wpbutton = new TextureCoords(tex, 124, 0, 92, 10);
	public static final TextureCoords wpbutton_disabled = new TextureCoords(tex, 124, 10, 92, 10);
	public static final TextureCoords wpbutton_color = new TextureCoords(tex, 124, 20, 6, 6);
	public static final int LIST_SIZE = 8;
	
	public final WaypointButton[] waypoints;
	public final ButtonLM buttonUp, buttonAdd, buttonDown;
	private int scroll = 0;
	private static int currentDim;
	
	public GuiWaypoints()
	{
		super(new ContainerEmpty.ClientGui(), tex);
		xSize = 124;
		ySize = 114;
		currentDim = mc.thePlayer.dimension;
		
		waypoints = new WaypointButton[LIST_SIZE];
		
		buttonUp = new ButtonLM(this, 102, 6, 16, 16)
		{
			public void onButtonPressed(int b)
			{ scroll(-1); }
		};
		
		buttonUp.title = FTBULang.button_up;
		
		buttonAdd = new ButtonLM(this, 102, 48, 16, 16)
		{
			public void onButtonPressed(int b)
			{ mc.displayGuiScreen(new GuiEditWaypoint(GuiWaypoints.this, null)); }
		};
		
		buttonAdd.title = FTBULang.button_add;
		
		buttonDown = new ButtonLM(this, 102, 91, 16, 16)
		{
			public void onButtonPressed(int b)
			{ scroll(1); }
		};
		
		buttonDown.title = FTBULang.button_down;
	}
	
	public void scroll(int s)
	{
		s = scroll + s;
		int max = Waypoints.getAll().size() - LIST_SIZE;
		if(s > max) s = max;
		if(s < 0) s = 0;
		if(scroll != s) { scroll = s; refreshWidgets(); }
	}
	
	public void drawForeground()
	{
		super.drawForeground();
		int dw = Mouse.getDWheel();
		if(dw != 0) scroll((dw > 0) ? -1 : 1);
	}
	
	public void addWidgets(FastList<WidgetLM> l)
	{
		scroll(0);
		
		l.add(buttonUp);
		l.add(buttonAdd);
		l.add(buttonDown);
		
		Arrays.fill(waypoints, null);
		
		FastList<Waypoint> l1 = Waypoints.getAll();
		
		int added = 0;
		
		for(int i = scroll; i < l1.size(); i++)
		{
			Waypoint w = l1.get(i);
			
			if(w.dim == currentDim)
			{
				WaypointButton b = new WaypointButton(this, added, w);
				waypoints[added] = b;
				l.add(b);
				added++;
			}
			
			if(added == LIST_SIZE) break;
		}
	}
	
	public void drawBackground()
	{
		super.drawBackground();
		
		buttonUp.render(Icons.up);
		buttonAdd.render(Icons.add);
		buttonDown.render(Icons.down);
		
		for(int i = 0; i < LIST_SIZE; i++) if(waypoints[i] != null)
		{
			GL11.glColor4f(1F, 1F, 1F, 1F);
			waypoints[i].render(waypoints[i].getIcon());
			GL11.glColor4f(waypoints[i].waypoint.colR / 255F, waypoints[i].waypoint.colG / 255F, waypoints[i].waypoint.colB / 255F, 1F);
			wpbutton_color.render(this, waypoints[i].posX + 2, waypoints[i].posY + 2);
		}
	}
	
	public void drawText(FastList<String> l)
	{
		for(int i = 0; i < LIST_SIZE; i++)
			if(waypoints[i] != null)
				fontRendererObj.drawString(waypoints[i].waypoint.name, guiLeft + waypoints[i].posX + 11, guiTop + waypoints[i].posY + 1, 0xFFFFFFFF);
		super.drawText(l);
	}
	
	public void onGuiClosed()
	{
		Waypoints.save();
	}
	
	public static class WaypointButton extends ButtonLM
	{
		public final Waypoint waypoint;
		
		public WaypointButton(GuiLM g, int i, Waypoint w)
		{
			super(g, 7, 6 + i * 13, wpbutton.width, wpbutton.height);
			waypoint = w;
		}
		
		public TextureCoords getIcon()
		{ return waypoint.enabled ? wpbutton : wpbutton_disabled; }

		public void onButtonPressed(int b)
		{
			gui.playClickSound();
			
			if(b == 1) waypoint.enabled = !waypoint.enabled;
			else gui.mc.displayGuiScreen(new GuiEditWaypoint((GuiWaypoints)gui, waypoint));
		}
	}
	
	public static class GuiEditWaypoint extends GuiLM implements GuiSelectColor.ColorSelectorCallback, GuiYesNoCallback
	{
		public static final ResourceLocation tex_edit = FTBU.mod.getLocation("textures/gui/editwp.png");
		public static final TextureCoords col_button_tex = new TextureCoords(tex_edit, 91, 0, 28, 10);
		public static final TextureCoords marker_tex = new TextureCoords(tex_edit, 91, 10, 32, 32);
		public static final TextureCoords beacon_tex = new TextureCoords(tex_edit, 123, 10, 32, 32);
		
		public final GuiWaypoints parent;
		public final boolean newWaypoint;
		public final Waypoint waypoint;
		
		public final ButtonLM buttonSave, buttonType, buttonRemove, buttonSetColor;
		public final TextBoxLM textBoxName, textBoxX, textBoxY, textBoxZ;
		
		public GuiEditWaypoint(GuiWaypoints g, Waypoint w)
		{
			super(new ContainerEmpty.ClientGui(), tex_edit);
			parent = g;
			newWaypoint = w == null;
			waypoint = newWaypoint ? new Waypoint() : w;
			xSize = 91;
			ySize = 61;
			
			if(newWaypoint)
			{
				waypoint.name = "New";
				waypoint.dim = mc.thePlayer.dimension;
				waypoint.setPos(RenderManager.instance.viewerPosX, RenderManager.instance.viewerPosY, RenderManager.instance.viewerPosZ);
				waypoint.setColor(mc.theWorld.rand.nextInt());
			}
			
			buttonSave = new ButtonLM(this, 70, 4, 16, 16)
			{
				public void onButtonPressed(int b)
				{
					if(newWaypoint) Waypoints.add(waypoint);
					else Waypoints.save();
					closeGui();
				}
			};
			
			buttonSave.title = newWaypoint ? FTBULang.button_add : FTBULang.button_save;
			
			buttonType = new ButtonLM(this, 70, 22, 16, 16)
			{
				public void onButtonPressed(int b)
				{
					waypoint.isMarker = !waypoint.isMarker;
					buttonType.title = Waypoints.waypointType.getValueS(waypoint.isMarker ? 0 : 1);
				}
			};
			
			buttonType.title = Waypoints.waypointType.getValueS(waypoint.isMarker ? 0 : 1);
			
			buttonRemove = new ButtonLM(this, 70, 40, 16, 16)
			{
				public void onButtonPressed(int b)
				{
					//mc.thePlayer.closeScreen();
					if(newWaypoint) closeGui();
					else mc.displayGuiScreen(new GuiYesNo(GuiEditWaypoint.this, "", "", 0));
				}
			};
			
			buttonRemove.title = newWaypoint ? FTBULang.button_cancel : FTBULang.button_remove;
			
			buttonSetColor = new ButtonLM(this, 38, 32, col_button_tex.width, col_button_tex.height)
			{
				public void onButtonPressed(int b)
				{ mc.displayGuiScreen(new GuiSelectColor((GuiEditWaypoint)gui, waypoint.getColorRGB(), 0)); }
			};
			
			buttonSetColor.title = LatCore.Colors.getHex(waypoint.getColorRGB());
			
			//
			textBoxName = new TextBoxLM(this, 6, 5, 61, 12)
			{
				public boolean canAddChar(char c)
				{ return (c == ' ' || c == '.' || c == '-' || c == '_' || LatCore.isTextChar(c, true)) && super.canAddChar(c); }
				
				public void textChanged()
				{ waypoint.name = text.trim(); }
			};
			textBoxName.charLimit = 20;
			textBoxName.text = waypoint.name;
			//
			textBoxX = new TextBoxLM(this, 6, 18, 61, 12)
			{
				public boolean canAddChar(char c)
				{ return c >= '0' && c <= '9' && super.canAddChar(c); }
				
				public void textChanged()
				{ waypoint.posX = Integer.parseInt(text); }
			};
			textBoxX.charLimit = 8;
			textBoxX.text = waypoint.posX + "";
			//
			textBoxY = new TextBoxLM(this, 6, 31, 30, 12)
			{
				public boolean canAddChar(char c)
				{ return c >= '0' && c <= '9' && super.canAddChar(c); }
				
				public void textChanged()
				{ waypoint.posY = Integer.parseInt(text); }
			};
			textBoxY.charLimit = 3;
			textBoxY.text = waypoint.posY + "";
			//
			textBoxZ = new TextBoxLM(this, 6, 44, 61, 12)
			{
				public boolean canAddChar(char c)
				{ return c >= '0' && c <= '9' && super.canAddChar(c); }
				
				public void textChanged()
				{ waypoint.posZ = Integer.parseInt(text); }
			};
			textBoxZ.charLimit = 8;
			textBoxZ.text = waypoint.posZ + "";
		}
		
		public void addWidgets(FastList<WidgetLM> l)
		{
			l.add(buttonSave);
			l.add(buttonType);
			l.add(buttonRemove);
			l.add(buttonSetColor);
			
			l.add(textBoxName);
			l.add(textBoxX);
			l.add(textBoxY);
			l.add(textBoxZ);
		}
		
		public void drawBackground()
		{
			super.drawBackground();
			
			buttonSave.render(newWaypoint ? Icons.add : Icons.accept);
			buttonType.render(waypoint.isMarker ? marker_tex : beacon_tex);
			buttonRemove.render(newWaypoint ? Icons.cancel : Icons.remove);
			
			GL11.glColor4f(waypoint.colR / 255F, waypoint.colG / 255F, waypoint.colB / 255F, 1F);
			buttonSetColor.render(col_button_tex);
			GL11.glColor4f(1F, 1F, 1F, 1F);
		}
		
		public void drawText(FastList<String> l)
		{
			textBoxName.render(8, 7, 0xFFFFFFFF);
			textBoxX.render(8, 20, 0xFFFFFFFF);
			textBoxY.render(8, 33, 0xFFFFFFFF);
			textBoxZ.render(8, 46, 0xFFFFFFFF);
			
			super.drawText(l);
		}
		
		public void closeGui()
		{
			parent.refreshWidgets();
			mc.displayGuiScreen(parent);
		}
		
		public void onGuiClosed()
		{
			Waypoints.save();
		}
		
		public void confirmClicked(boolean set, int ID)
		{
			if(set && !newWaypoint)
				Waypoints.remove(waypoint.listID);
			closeGui();
		}
		
		public void onColorSelected(boolean set, int color, int ID)
		{
			waypoint.setColor(color);
			mc.displayGuiScreen(this);
		}
	}
}