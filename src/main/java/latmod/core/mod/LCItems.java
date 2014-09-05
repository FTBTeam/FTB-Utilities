package latmod.core.mod;

import latmod.core.mod.block.*;
import latmod.core.mod.item.*;

public class LCItems
{
	public static BlockPaintable b_paintable;
	public static BlockScript b_script;
	public static BlockFacade b_facade;
	
	public static ItemLinkCard i_link_card;
	public static ItemBlockPainter i_painter;
	
	public static void init()
	{
		LC.mod.addBlock(LCItems.b_paintable = new BlockPaintable("paintable"));
		LC.mod.addBlock(LCItems.b_script = new BlockScript("scriptBlock"));
		LC.mod.addBlock(LCItems.b_facade = new BlockFacade("facade"));
		
		LC.mod.addItem(LCItems.i_link_card = new ItemLinkCard("linkCard"));
		LC.mod.addItem(LCItems.i_painter = new ItemBlockPainter("blockPainter"));
	}
}