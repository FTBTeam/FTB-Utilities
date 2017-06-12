package com.feed_the_beast.ftbu.api;

import com.feed_the_beast.ftbu.api.chunks.IChunkUpgrade;

/**
 * @author LatvianModder
 */
public interface IFTBUtilitiesRegistry
{
	void addCustomPermPrefix(NodeEntry entry);

	void addChunkUpgrade(IChunkUpgrade upgrade);
}