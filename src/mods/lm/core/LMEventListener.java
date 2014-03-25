package mods.lm.core;

public interface LMEventListener
{
	public void onEvent(String eventID, Object... args);
}