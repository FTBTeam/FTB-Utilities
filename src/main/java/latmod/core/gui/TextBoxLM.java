package latmod.core.gui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ChatAllowedCharacters;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public class TextBoxLM extends WidgetLM
{
	public boolean isSelected = false;
	public String text = "";
	public int charLimit = -1;
	
	public TextBoxLM(GuiLM g, int x, int y, int w, int h)
	{
		super(g, x, y, w, h);
	}
	
	public boolean mousePressed(int mx, int my, int b)
	{
		if(mouseOver(mx, my))
		{
			isSelected = true;
			Keyboard.enableRepeatEvents(true);
			
			if(b == 1 && text.length() > 0)
			{
				text = "";
				textChanged();
			}
			
			return true;
		}
		
		return false;
	}
	
	public boolean canAddChar(char c)
	{ return charLimit == -1 || text.length() + 1 <= charLimit; }
	
	public void voidMousePressed(int mx, int my, int b)
	{
		Keyboard.enableRepeatEvents(false);
		isSelected = false;
	}
	
	public boolean keyPressed(int key, char keyChar)
	{
		if(isSelected)
		{
			if(key == Keyboard.KEY_BACK)
			{
				if(text.length() > 0)
				{
					if(GuiScreen.isCtrlKeyDown()) text = ""; else
						text = text.substring(0, text.length() - 1);
					
					textChanged();
				}
			}
			else if(key == Keyboard.KEY_ESCAPE)
			{
				isSelected = false;
			}
			else if(key == Keyboard.KEY_TAB)
			{
				tabPressed();
				isSelected = false;
			}
			else if(key == Keyboard.KEY_RETURN)
			{
				returnPressed();
				isSelected = false;
			}
			else
			{
				if(ChatAllowedCharacters.isAllowedCharacter(keyChar))
				{
					if(canAddChar(keyChar))
					{
						text += keyChar;
						textChanged();
					}
				}
			}
			
			return true;
		}
		
		return false;
	}
	
	public void textChanged()
	{
	}
	
	public void tabPressed()
	{
	}
	
	public void returnPressed()
	{
	}

	public void clear()
	{
		text = "";
	}
	
	public String getText()
	{ return text; }
	
	public void render(int x, int y, int col)
	{
		String s = getText() + "";
		
		if(isSelected && Minecraft.getSystemTime() % 1000L > 500L)
			s += '_';
		
		if(s.length() > 0)
			gui.getFontRenderer().drawString(s, gui.getPosX() + x, gui.getPosY() + y, col);
	}
}