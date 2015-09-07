package latmod.ftbu.core.paint;

public interface IPaintable
{
	public boolean setPaint(PaintData p);
	public boolean isPaintValid(int side, Paint p);
}