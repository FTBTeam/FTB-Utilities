package com.feed_the_beast.ftbu.gui;

import com.feed_the_beast.ftbu.api.FTBUtilitiesEvent;
import com.feed_the_beast.ftbu.net.MessageClaimedChunksUpdate;

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