package latmod.ftbu.mod.client.gui.field;

import cpw.mods.fml.relauncher.*;
import latmod.ftbu.core.client.FTBULang;
import latmod.ftbu.core.gui.*;

@SideOnly(Side.CLIENT)
public class GuiSelectField extends GuiLM
{
	public final Object ID;
	public final FieldType type;
	public final String def;
	public final IFieldCallback callback;
	
	public final ButtonSimpleLM buttonCancel, buttonAccept;
	public final TextBoxLM textBox;
	
	public GuiSelectField(Object id, FieldType typ, String d, IFieldCallback c)
	{
		super(null, null);
		hideNEI = true;
		ID = id;
		type = typ;
		def = d;
		callback = c;
		
		xSize = 100;
		ySize = 40;
		
		int bsize = xSize / 2 - 4;
		
		buttonCancel = new ButtonSimpleLM(this, 2, ySize - 18, bsize, 16)
		{
			public void onButtonPressed(int b)
			{
				callback.onFieldSelected(new FieldSelected(ID, false, def, true));
			}
		};
		
		buttonCancel.title = FTBULang.button_cancel();
		
		buttonAccept = new ButtonSimpleLM(this, xSize - bsize - 2, ySize - 18, bsize, 16)
		{
			public void onButtonPressed(int b)
			{
				callback.onFieldSelected(new FieldSelected(ID, false, textBox.text, true));
			}
		};
		
		buttonAccept.title = FTBULang.button_accept();
		
		textBox = new TextBoxLM(this, 2, 2, xSize - 4, 18)
		{
			public boolean canAddChar(char c)
			{ return super.canAddChar(c) && type.isCharValid(c); }
		};
		
		textBox.text = def;
	}
	
	public GuiSelectField setCharLimit(int i)
	{ textBox.charLimit = i; return this; }
	
	public void addWidgets()
	{
		mainPanel.add(buttonCancel);
		mainPanel.add(buttonAccept);
		mainPanel.add(textBox);
	}
	
	public void drawBackground()
	{
		getFontRenderer();
		
		int size = 8 + fontRendererObj.getStringWidth(textBox.text);
		if(size > xSize)
		{
			xSize = size;
			int bsize = xSize / 2 - 4;
			buttonAccept.width = buttonCancel.width = bsize;
			buttonAccept.posX = xSize - bsize - 2;
			textBox.width = xSize - 4;
			initGui();
		}
		drawBlankRect(guiLeft, guiTop, zLevel, xSize, ySize, 0xAA666666);
		drawBlankRect(textBox.getAX(), textBox.getAY(), zLevel, textBox.width, textBox.height, 0xFF333333);
		buttonAccept.renderWidget();
		buttonCancel.renderWidget();
		textBox.renderCentred(textBox.width / 2, 6, 0xFFEEEEEE);
	}
}