package com.feed_the_beast.ftbutilities.events.chunks;

import com.feed_the_beast.ftbutilities.events.FTBUtilitiesEvent;
import com.feed_the_beast.ftbutilities.net.MessageClaimedChunksUpdate;

/**
 * @author LatvianModder
 */
public class UpdateClientDataEvent extends FTBUtilitiesEvent
{
	private final MessageClaimedChunksUpdate message;

	public UpdateClientDataEvent(MessageClaimedChunksUpdate m)
	{
		message = m;
	}

	public MessageClaimedChunksUpdate getMessage()
	{
		return message;
	}
}