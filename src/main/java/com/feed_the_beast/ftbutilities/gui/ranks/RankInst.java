package com.feed_the_beast.ftbutilities.gui.ranks;

import com.feed_the_beast.ftblib.lib.io.DataIn;
import com.feed_the_beast.ftblib.lib.io.DataOut;
import com.feed_the_beast.ftblib.lib.util.FinalIDObject;
import com.feed_the_beast.ftblib.lib.util.misc.Node;
import com.feed_the_beast.ftbutilities.ranks.Rank;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

/**
 * @author LatvianModder
 */
public class RankInst extends FinalIDObject
{
	public static final DataOut.Serializer<RankInst> SERIALIZER = (data, object) -> {
		data.writeString(object.toString());
		data.writeString(object.parent);
		data.writeCollection(object.tags, DataOut.STRING);
		data.writeCollection(object.permissions, RankInst.ENTRY_SERIALIZER);
	};

	public static final DataIn.Deserializer<RankInst> DESERIALIZER = data -> {
		RankInst inst = new RankInst(data.readString());
		inst.parent = data.readString();
		inst.tags = data.readCollection(DataIn.STRING);
		inst.permissions = data.readCollection(RankInst.ENTRY_DESERIALIZER);
		return inst;
	};

	public static final DataOut.Serializer<Rank.Entry> ENTRY_SERIALIZER = (data, object) -> {
		data.writeString(object.node.toString());
		data.writeJson(object.json);
	};

	public static final DataIn.Deserializer<Rank.Entry> ENTRY_DESERIALIZER = data -> {
		Rank.Entry entry = new Rank.Entry(Node.get(data.readString()));
		entry.json = data.readJson();
		return entry;
	};

	public String parent;
	public Collection<String> tags;
	public Collection<Rank.Entry> permissions;

	public RankInst(String id)
	{
		super(id);
		parent = "";
		tags = new HashSet<>();
		permissions = new ArrayList<>();
	}
}