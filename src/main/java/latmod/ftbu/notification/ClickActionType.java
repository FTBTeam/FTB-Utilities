package latmod.ftbu.notification;

import latmod.core.util.PrimitiveType;

public class ClickActionType
{
	public final String ID;
	public final PrimitiveType type;
	
	public ClickActionType(String s, PrimitiveType t)
	{ ID = s; type = t; }
	
	public String toString()
	{ return ID; }
	
	public boolean equals(Object o)
	{ return o != null && (this == o || ID.equals(o.toString())); }
	
	public int hashCode()
	{ return ID.hashCode(); }
}