package com.feed_the_beast.ftbu.api.guide;

import com.feed_the_beast.ftbl.api.FTBLibEvent;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nullable;
import java.util.function.Consumer;

/**
 * @author LatvianModder
 */
public class ServerInfoEvent extends FTBLibEvent
{
	private final EntityPlayerMP player;
	private final long now;
	private final Consumer<ITextComponent> callback;

	public ServerInfoEvent(EntityPlayerMP p, long t, Consumer<ITextComponent> c)
	{
		player = p;
		now = t;
		callback = c;
	}

	public EntityPlayerMP getPlayer()
	{
		return player;
	}

	public long getTime()
	{
		return now;
	}

	public void println(@Nullable ITextComponent component)
	{
		callback.accept(component);
	}
}