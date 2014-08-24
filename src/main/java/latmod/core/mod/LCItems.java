package latmod.core.mod;

import latmod.core.mod.block.BlockPaintable;
import latmod.core.mod.item.*;

public class LCItems
{
	public static BlockPaintable b_paintable;
	
	public static ItemLinkCard i_link_card;
	public static ItemBlockPainter i_painter;
	
	public static void init(LMMod mod)
	{
		mod.addBlock(LCItems.b_paintable = new BlockPaintable("paintable"));
		
		mod.addItem(LCItems.i_link_card = new ItemLinkCard("linkCard"));
		mod.addItem(LCItems.i_painter = new ItemBlockPainter("blockPainter"));
	}
}