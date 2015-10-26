package latmod.ftbu.util.gui;

import latmod.ftbu.mod.FTBU;
import net.minecraft.util.ResourceLocation;

public class GuiIcons
{
	public static final ResourceLocation tex = FTBU.mod.getLocation("textures/gui/icons.png");
	
	private static final TextureCoords getIcon(int index)
	{ return new TextureCoords(tex, (index % 16) * 16, (index / 16) * 16, 16, 16, 256, 256); }
	
	public static final TextureCoords left = getIcon(0);
	public static final TextureCoords right = getIcon(1);
	public static final TextureCoords accept = getIcon(2);
	public static final TextureCoords add = getIcon(3);
	public static final TextureCoords remove = getIcon(4);
	public static final TextureCoords info = getIcon(5);
	public static final TextureCoords sort = getIcon(6);
	public static final TextureCoords friends = getIcon(7);
	public static final TextureCoords bug = getIcon(8);
	public static final TextureCoords jacket = getIcon(9);
	public static final TextureCoords up = getIcon(10);
	public static final TextureCoords down = getIcon(11);
	public static final TextureCoords button = getIcon(12);
	public static final TextureCoords pressed = getIcon(13);
	public static final TextureCoords player = getIcon(14);
	public static final TextureCoords online = getIcon(15);
	
	public static final TextureCoords settings = getIcon(16);
	public static final TextureCoords bed = getIcon(17);
	public static final TextureCoords bell = getIcon(18);
	public static final TextureCoords compass = getIcon(19);
	public static final TextureCoords map = getIcon(20);
	public static final TextureCoords shield = getIcon(21);
	public static final TextureCoords picture = getIcon(22);
	public static final TextureCoords moneybag = getIcon(23);
	public static final TextureCoords game = getIcon(24);
	public static final TextureCoords feather = getIcon(25);
	public static final TextureCoords camera = getIcon(26);
	public static final TextureCoords cancel = getIcon(27);
	public static final TextureCoords accept_gray = getIcon(28);
	public static final TextureCoords add_gray = getIcon(29);
	public static final TextureCoords remove_gray = getIcon(30);
	public static final TextureCoords info_gray = getIcon(31);
	
	public static final TextureCoords[] inv =
	{
		getIcon(32),
		getIcon(33),
		getIcon(34),
		getIcon(35),
	};
	
	public static final TextureCoords[] redstone =
	{
		getIcon(36),
		getIcon(37),
		getIcon(38),
		getIcon(39),
	};
	
	public static final TextureCoords[] security =
	{
		getIcon(40),
		getIcon(41),
		getIcon(42),
		getIcon(43),
	};
	
	public static final TextureCoords back = getIcon(44);
	public static final TextureCoords close = getIcon(45);
	public static final TextureCoords player_gray = getIcon(46);
	public static final TextureCoords online_red = getIcon(47);
	public static final TextureCoords notes = getIcon(48);
	public static final TextureCoords hsb = getIcon(49);
	public static final TextureCoords rgb = getIcon(50);
	public static final TextureCoords comment = getIcon(51);
	public static final TextureCoords bin = getIcon(52);
	public static final TextureCoords marker = getIcon(53);
	public static final TextureCoords beacon = getIcon(54);
	public static final TextureCoords color_blank = getIcon(55);
	public static final TextureCoords refresh = getIcon(56);
	public static final TextureCoords dice = getIcon(57);
	public static final TextureCoords diamond = getIcon(58);
	public static final TextureCoords timer = getIcon(59);
	public static final TextureCoords globe = getIcon(60);
	public static final TextureCoords money = getIcon(61);
	public static final TextureCoords tick = getIcon(62);
	public static final TextureCoords star = getIcon(63);
}