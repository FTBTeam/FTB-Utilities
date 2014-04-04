package latmod.core;

public interface LMEventListener
{
	public void onEvent(String eventID, Object... args);
}