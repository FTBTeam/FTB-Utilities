package com.feed_the_beast.ftbu.ranks;

import com.feed_the_beast.ftbl.api.IRankConfig;
import com.feed_the_beast.ftbl.api.config.IConfigValue;
import com.feed_the_beast.ftbl.lib.config.PropertyNull;
import com.feed_the_beast.ftbu.FTBLibIntegration;
import com.feed_the_beast.ftbu.api.IRank;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by LatvianModder on 02.12.2016.
 */
abstract class AbstractDefaultRank implements IRank
{
    final Map<String, IConfigValue> configTree = new HashMap<>();

    @Override
    public IConfigValue getConfig(String id)
    {
        IConfigValue value = configTree.get(id);

        if(value == null || value.isNull())
        {
            IRankConfig config = FTBLibIntegration.API.getRankConfigRegistry().get(id);

            if(config != null)
            {
                value = createValue(config);
            }
        }

        return (value == null || value.isNull()) ? PropertyNull.INSTANCE : value;
    }

    abstract IConfigValue createValue(IRankConfig config);

    @Override
    public void fromJson(JsonElement json)
    {
        configTree.clear();

        if(json.isJsonObject())
        {
            for(Map.Entry<String, JsonElement> entry : json.getAsJsonObject().entrySet())
            {
                IRankConfig config = FTBLibIntegration.API.getRankConfigRegistry().get(entry.getKey());

                if(config != null)
                {
                    IConfigValue value = createValue(config);
                    value.fromJson(entry.getValue());
                    configTree.put(config.getID(), value);
                }
            }
        }
    }

    @Override
    public JsonElement getSerializableElement()
    {
        JsonObject o = new JsonObject();
        configTree.forEach((key, value) -> o.add(key, value.getSerializableElement()));
        return o;
    }
}
