package latmod.ftbu.api.paint;

public interface IPaintable
{
	public boolean setPaint(PaintData p);
	
	public boolean isPaintValid(int side, Paint p);
}