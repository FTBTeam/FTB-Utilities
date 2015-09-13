package latmod.ftbu.mod.client.gui.field;

import cpw.mods.fml.relauncher.*;
import latmod.ftbu.core.client.FTBULang;
import latmod.ftbu.core.gui.*;

@SideOnly(Side.CLIENT)
public class GuiSelectField extends GuiLM
{
	public static final int TYPE_TEXT = 0;
	public static final int TYPE_INT = 1;
	public static final int TYPE_FLOAT = 2;
	
	public final Object ID;
	public final int type;
	public final String def;
	public final IFieldCallback callback;
	
	public final ButtonLM buttonCancel, buttonAccept;
	public final TextBoxLM textBox;
	
	public GuiSelectField(Object id, int typ, String d, IFieldCallback c)
	{
		super(null, null);
		ID = id;
		type = typ;
		def = d;
		callback = c;
		
		xSize = 100;
		ySize = 40;
		
		int bsize = xSize / 2 - 4;
		
		buttonCancel = new ButtonLM(this, 2, ySize - 18, bsize, 16)
		{
			public void onButtonPressed(int b)
			{
				c.onFieldSelected(new FieldSelected(ID, false, def, true));
			}
		};
		
		buttonAccept = new ButtonLM(this, xSize - bsize - 2, ySize - 18, bsize, 16)
		{
			public void onButtonPressed(int b)
			{
				c.onFieldSelected(new FieldSelected(ID, false, textBox.text, true));
			}
		};
		
		textBox = new TextBoxLM(this, 2, 2, xSize - 4, 18)
		{
			public boolean canAddChar(char c)
			{
				if(!super.canAddChar(c)) return false;
				else if(type == TYPE_TEXT) return true;
				else if(c == '.') return type == TYPE_FLOAT;
				return c == '-' || (c < '0' || c > '9');
			}
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
		renderButton(buttonAccept, FTBULang.button_accept());
		renderButton(buttonCancel, FTBULang.button_cancel());
		textBox.renderCentred(textBox.width / 2, 6, 0xFFEEEEEE);
	}
	
	private void renderButton(ButtonLM b, String s)
	{
		int x = b.getAX();
		int y = b.getAY();
		drawBlankRect(x, y, zLevel, b.width, b.height, b.mouseOver() ? 0xFF999999 : 0xFF888888);
		drawCenteredString(fontRendererObj, s, x + b.width / 2, y + (b.height - fontRendererObj.FONT_HEIGHT) / 2, 0xFFFFFFFF);
	}
}