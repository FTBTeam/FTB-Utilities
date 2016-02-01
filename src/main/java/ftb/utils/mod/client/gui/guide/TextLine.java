package ftb.utils.mod.client.gui.guide;

import ftb.lib.TextureCoords;
import ftb.utils.api.guide.GuideLink;

/**
 * Created by LatvianModder on 01.02.2016.
 */
public class TextLine
{
	public final TextLine parent;
	public String text = null;
	public GuideLink special = null;
	public TextureCoords texture = null;
	
	public TextLine(TextLine p)
	{ parent = p; }
	
	public String toString()
	{ return text; }
}
