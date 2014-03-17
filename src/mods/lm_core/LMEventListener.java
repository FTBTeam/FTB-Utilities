package mods.lm_core;

public interface LMEventListener
{
	public void onEvent(String eventID, Object... args);
}