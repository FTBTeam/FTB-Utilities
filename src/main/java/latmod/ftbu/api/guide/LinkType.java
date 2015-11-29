package latmod.ftbu.api.guide;

public enum LinkType
{
	URL,
	IMAGE,
	IMAGE_URL,
	RECIPE;
	
	public boolean isText()
	{ return this == URL; }
	
	public boolean isImage()
	{ return this == IMAGE || this == IMAGE_URL; }
}