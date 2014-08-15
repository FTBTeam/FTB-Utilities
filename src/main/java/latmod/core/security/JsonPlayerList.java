package latmod.core.security;

import latmod.core.util.FastList;

import com.google.gson.annotations.Expose;

public class JsonPlayerList
{
	@Expose public FastList<JsonPlayer> players;
}