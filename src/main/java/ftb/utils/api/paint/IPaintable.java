package ftb.utils.api.paint;

import net.minecraft.util.EnumFacing;

public interface IPaintable
{
	boolean setPaint(PaintData p);
	boolean isPaintValid(EnumFacing side, Paint p);
}