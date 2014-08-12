package latmod.core.mod.gui;
import org.lwjgl.input.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.util.*;

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
			return true;
		}
		
		return false;
	}
	
	public boolean canAddChar()
	{ return charLimit == -1 || text.length() + 1 <= charLimit; }
	
	public void voidMousePressed(int mx, int my, int b)
	{
		isSelected = false;
	}
	
	public boolean keyPressed(int key, char keyChar)
	{
		if(isSelected)
		{
			if(key == Keyboard.KEY_BACK)
			{
				if(text.length() > 0)
					text = text.substring(0, text.length() - 1);
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
					if(canAddChar()) text += keyChar;
				}
			}
			
			return true;
		}
		
		return false;
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
}