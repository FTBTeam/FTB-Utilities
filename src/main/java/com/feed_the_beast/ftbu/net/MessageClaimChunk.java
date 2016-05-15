package com.feed_the_beast.ftbu.net;

import com.feed_the_beast.ftbl.api.ForgePlayerMP;
import com.feed_the_beast.ftbl.api.ForgeWorldMP;
import com.feed_the_beast.ftbl.api.net.LMNetworkWrapper;
import com.feed_the_beast.ftbl.api.net.MessageToServer;
import com.feed_the_beast.ftbl.util.ChunkDimPos;
import com.feed_the_beast.ftbl.util.LMAccessToken;
import com.feed_the_beast.ftbu.world.ClaimedChunk;
import com.feed_the_beast.ftbu.world.FTBUPlayerDataMP;
import com.feed_the_beast.ftbu.world.FTBUWorldDataMP;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.DimensionType;

public class MessageClaimChunk extends MessageToServer<MessageClaimChunk>
{
	public static final int ID_CLAIM = 0;
	public static final int ID_UNCLAIM = 1;
	public static final int ID_UNCLAIM_ALL = 2;
	public static final int ID_UNCLAIM_ALL_DIMS = 3;
	public static final int ID_LOAD = 4;
	public static final int ID_UNLOAD = 5;
	
	public int type;
	public ChunkDimPos pos;
	public long token;
	
	public MessageClaimChunk() { }
	
	@Override
	public LMNetworkWrapper getWrapper()
	{ return FTBUNetHandler.NET; }
	
	@Override
	public void fromBytes(ByteBuf io)
	{
		type = io.readUnsignedByte();
		token = io.readLong();
		pos = new ChunkDimPos(DimensionType.getById(io.readInt()), io.readInt(), io.readInt());
	}
	
	@Override
	public void toBytes(ByteBuf io)
	{
		io.writeByte(type);
		io.writeLong(token);
		io.writeInt(pos.dim.getId());
		io.writeInt(pos.chunkXPos);
		io.writeInt(pos.chunkZPos);
	}
	
	@Override
	public void onMessage(MessageClaimChunk m, EntityPlayerMP ep)
	{
		ForgePlayerMP p = ForgeWorldMP.inst.getPlayer(ep);
		FTBUPlayerDataMP d = FTBUPlayerDataMP.get(p);
		
		if(m.type == ID_CLAIM)
		{
			d.claimChunk(m.pos);
			new MessageAreaUpdate(p, m.pos.chunkXPos, m.pos.chunkZPos, m.pos.dim, 1, 1).sendTo(ep);
		}
		else if(m.type == ID_UNCLAIM)
		{
			if(m.token != 0L && LMAccessToken.equals(p.getPlayer(), m.token, false))
			{
				ClaimedChunk c = FTBUWorldDataMP.get().getChunk(m.pos);
				if(c != null)
				{
					ForgePlayerMP p1 = ForgeWorldMP.inst.getPlayer(c.ownerID);
					FTBUPlayerDataMP d1 = FTBUPlayerDataMP.get(p1);
					d1.unclaimChunk(m.pos);
				}
			}
			else { d.unclaimChunk(m.pos); }
			new MessageAreaUpdate(p, m.pos.chunkXPos, m.pos.chunkZPos, m.pos.dim, 1, 1).sendTo(ep);
		}
		else if(m.type == ID_UNCLAIM_ALL) { d.unclaimAllChunks(m.pos.dim); }
		else if(m.type == ID_UNCLAIM_ALL_DIMS) { d.unclaimAllChunks(null); }
		else if(m.type == ID_LOAD) { d.setLoaded(m.pos, true); }
		else if(m.type == ID_UNLOAD) { d.setLoaded(m.pos, false); }
	}
}