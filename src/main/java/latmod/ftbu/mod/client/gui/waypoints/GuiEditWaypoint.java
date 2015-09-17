package latmod.ftbu.mod.client.gui.waypoints;

import cpw.mods.fml.relauncher.*;
import latmod.ftbu.core.LatCoreMC;
import latmod.ftbu.core.client.FTBULang;
import latmod.ftbu.core.gui.*;
import latmod.ftbu.core.util.LMColorUtils;
import latmod.ftbu.mod.client.gui.field.*;
import latmod.ftbu.mod.client.gui.field.color.*;
import latmod.ftbu.mod.client.minimap.*;

@SideOnly(Side.CLIENT)
public class GuiEditWaypoint extends GuiLM implements IColorCallback, IFieldCallback
{
	public final GuiWaypoints parent; // PanelWaypoint
	public final ButtonSimpleLM buttonCancel, buttonAdd, buttonType, buttonColor;
	public final ButtonSimpleLM buttonTitle, buttonPosX, buttonPosY, buttonPosZ;
	public final Waypoint current;
	
	public GuiEditWaypoint(GuiWaypoints p, Waypoint w)
	{
		super(null, null);
		parent = p;
		hideNEI = true;
		xSize = 200;
		ySize = 2 + 18 * 4;
		
		current = (w == null) ? new Waypoint() : w.clone();
		
		if(w == null)
		{
			current.name = "Waypoint " + LMColorUtils.getHex(LatCoreMC.rand.nextInt());
			current.type = Waypoint.Type.BEACON;
			current.setPos(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
			current.dim = mc.thePlayer.dimension;
			current.color = LatCoreMC.rand.nextInt();
		}
		else current.listID = w.listID;
		
		int buttonSize, buttonY;
		
		buttonSize = xSize - 4;
		buttonY = 2;
		
		buttonTitle = new ButtonSimpleLM(this, 2, buttonY, buttonSize, 16)
		{
			public void onButtonPressed(int b)
			{
				gui.playClickSound();
				mc.displayGuiScreen(new GuiSelectField(0, GuiSelectField.TYPE_TEXT, current.name, GuiEditWaypoint.this));
			}
		};
		
		buttonSize = xSize / 3 - 2;
		buttonY = 2 + 18 * 1;
		
		buttonPosX = new ButtonSimpleLM(this, 2, buttonY, buttonSize, 16)
		{
			public void onButtonPressed(int b)
			{
				gui.playClickSound();
				mc.displayGuiScreen(new GuiSelectField(1, GuiSelectField.TYPE_INT, current.posX + "", GuiEditWaypoint.this));
			}
		};
		
		buttonPosY = new ButtonSimpleLM(this, xSize / 3 + 2, buttonY, buttonSize, 16)
		{
			public void onButtonPressed(int b)
			{
				gui.playClickSound();
				mc.displayGuiScreen(new GuiSelectField(2, GuiSelectField.TYPE_INT, current.posY + "", GuiEditWaypoint.this));
			}
		};
		
		buttonPosZ = new ButtonSimpleLM(this, xSize * 2 / 3 + 1, buttonY, buttonSize, 16)
		{
			public void onButtonPressed(int b)
			{
				gui.playClickSound();
				mc.displayGuiScreen(new GuiSelectField(3, GuiSelectField.TYPE_INT, current.posZ + "", GuiEditWaypoint.this));
			}
		};
		
		buttonSize = xSize / 2 - 3;
		buttonY = 2 + 18 * 2;
		
		buttonType = new ButtonSimpleLM(this, 2, buttonY, buttonSize, 16)
		{
			public void onButtonPressed(int b)
			{
				gui.playClickSound();
				current.type = current.type.next();
				gui.refreshWidgets();
			}
		};
		
		buttonColor = new ButtonSimpleLM(this, xSize - buttonSize - 2, buttonY, buttonSize, 16)
		{
			public void onButtonPressed(int b)
			{
				gui.playClickSound();
				GuiSelectColorRGB.displayGui(GuiEditWaypoint.this, current.color, 0, false);
			}
		};
		
		buttonY = 2 + 18 * 3;
		
		buttonCancel = new ButtonSimpleLM(this, 2, buttonY, buttonSize, 16)
		{
			public void onButtonPressed(int b)
			{
				gui.playClickSound();
				parent.refreshWidgets();
				gui.mc.displayGuiScreen(parent);
			}
		};
		
		buttonCancel.title = FTBULang.button_cancel();
		
		buttonAdd = new ButtonSimpleLM(this, xSize - buttonSize - 2, buttonY, buttonSize, 16)
		{
			public void onButtonPressed(int b)
			{
				gui.playClickSound();
				Waypoints.add(current);
				parent.refreshWidgets();
				gui.mc.displayGuiScreen(parent);
			}
		};
		
		buttonAdd.title = (w == null) ? FTBULang.button_add() : FTBULang.button_save();
	}
	
	public void initLMGui()
	{
	}
	
	public void addWidgets()
	{
		mainPanel.add(buttonCancel);
		mainPanel.add(buttonAdd);
		mainPanel.add(buttonType);
		mainPanel.add(buttonColor);
		mainPanel.add(buttonTitle);
		mainPanel.add(buttonPosX);
		mainPanel.add(buttonPosY);
		mainPanel.add(buttonPosZ);
		
		buttonTitle.title = current.name;
		buttonPosX.title = "X: " + current.posX;
		buttonPosY.title = "Y: " + current.posY;
		buttonPosZ.title = "Z: " + current.posZ;
		buttonType.title = current.type.getIDS();
		buttonColor.title = LMColorUtils.getHex(current.color);
	}
	
	public void drawBackground()
	{
		GuiLM.drawBlankRect(guiLeft, guiTop, zLevel, xSize, ySize, 0xFF333333);
		drawCenteredString(getFontRenderer(), Waypoints.clientConfig.getIDS(), width / 2, 6, 0xFFFFFFFF);
		
		//GL11.glDisable(GL11.GL_LIGHTING);
		//GL11.glEnable(GL11.GL_BLEND);
		//GL11.glColor4f(1F, 1F, 1F, 1F);
		
		for(WidgetLM w : mainPanel.getWidgets())
			w.renderWidget();
	}
	
	public void onColorSelected(ColorSelected c)
	{
		if(c.set || c.closeGui) current.color = c.color;
		if(c.closeGui) mc.displayGuiScreen(this);
		refreshWidgets();
	}
	
	public void onFieldSelected(FieldSelected c)
	{
		int i = c.ID.hashCode();
		
		if(i == 0) current.name = c.getS();
		else if(i == 1) current.posX = c.getI();
		else if(i == 2) current.posY = c.getI();
		else if(i == 3) current.posZ = c.getI();
		
		mc.displayGuiScreen(this);
		refreshWidgets();
	}
}