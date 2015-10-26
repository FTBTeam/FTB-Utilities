package latmod.ftbu.mod.client.gui.guide;

import latmod.ftbu.api.guide.*;

public class TextLine
{
	public final String text;
	public final GuideLink special;
	
	public TextLine(GuiGuide g, String s)
	{
		text = s;
		GuideFile f = g.category.getFile();
		special = (f == null) ? null : f.getGuideLink(s);
	}
	
	public String toString()
	{ return special == null ? text : special.text; }
}