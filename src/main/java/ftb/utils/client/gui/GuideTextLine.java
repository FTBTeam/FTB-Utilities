package ftb.utils.client.gui;

import ftb.lib.TextureCoords;
import ftb.utils.api.guide.GuideLink;

public class GuideTextLine
{
	public final GuideTextLine parent;
	public String text = null;
	public GuideLink special = null;
	public TextureCoords texture = null;
	
	public GuideTextLine(GuideTextLine p)
	{ parent = p; }
	
	public String toString()
	{ return text; }
}