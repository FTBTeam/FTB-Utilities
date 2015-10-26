package latmod.ftbu.api.guide;

public class GuideLink
{
	public static final int TYPE_URL = 1;
	public static final int TYPE_IMAGE = 2;
	public static final int TYPE_IMAGE_URL = 3;
	
	public final int type;
	public String link = null;
	public String text = null;
	public String hover = null;
	
	public GuideLink(int i)
	{ type = i; }
}