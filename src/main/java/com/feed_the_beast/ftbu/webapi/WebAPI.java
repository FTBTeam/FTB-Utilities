package com.feed_the_beast.ftbu.webapi;

import com.feed_the_beast.ftbl.FTBLibStats;
import com.feed_the_beast.ftbl.api.IForgePlayer;
import com.feed_the_beast.ftbl.lib.util.LMJsonUtils;
import com.feed_the_beast.ftbu.FTBLibIntegration;
import com.feed_the_beast.ftbu.config.FTBUConfigWebAPI;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.stats.StatList;
import net.minecraft.stats.StatisticsManagerServer;

import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by LatvianModder on 18.06.2016.
 */
public class WebAPI extends Thread
{
    public static final WebAPI INST = new WebAPI();
    private ServerSocket serverSocket;

    private WebAPI()
    {
        super("FTBU_WebAPI");
    }

    @Override
    public void run()
    {
        try
        {
            serverSocket = new ServerSocket(FTBUConfigWebAPI.PORT.getInt());

            System.out.println(getName() + " started");

            while(isAPIRunning())
            {
                try
                {
                    Socket socket = serverSocket.accept();

                    PrintWriter out = new PrintWriter(socket.getOutputStream());
                    out.println("HTTP/1.0 200 OK");
                    out.println("Content-Type: application/json");
                    out.println("Server: Bot");
                    out.println("");

                    JsonTable table = new JsonTable();
                    table.setTitle("name", "Name");
                    table.setTitle("deaths", "Deaths");
                    table.setTitle("dph", "Deaths per hour");
                    table.setTitle("last_seen", "Last time seen");

                    for(IForgePlayer player : FTBLibIntegration.API.getUniverse().getPlayers())
                    {
                        StatisticsManagerServer stats = player.stats();

                        JsonTable.TableEntry tableEntry = new JsonTable.TableEntry();
                        tableEntry.set("name", new JsonPrimitive(player.getProfile().getName()));
                        tableEntry.set("deaths", new JsonPrimitive(stats.readStat(StatList.DEATHS)));
                        tableEntry.set("dph", new JsonPrimitive(FTBLibStats.getDeathsPerHour(stats)));
                        tableEntry.set("last_seen", new JsonPrimitive(player.isOnline() ? 0 : FTBLibStats.getLastSeen(stats, false)));
                        table.addEntry(tableEntry);
                    }

                    JsonObject json = new JsonObject();
                    json.add("time", new JsonPrimitive(System.currentTimeMillis()));
                    json.add("stats", table.toJson());

                    String outputData = LMJsonUtils.toJson(LMJsonUtils.GSON, json);
                    out.print(outputData);

                    out.flush();
                    socket.close();

                    System.out.println("Sent data: " + outputData);
                }
                catch(Exception ex)
                {
                    ex.printStackTrace();
                }
            }

            System.out.println(getName() + " closed");
        }
        catch(Exception ex0)
        {
            ex0.printStackTrace();
        }
    }

    public boolean isAPIRunning()
    {
        return FTBUConfigWebAPI.ENABLED.getBoolean() && FTBLibIntegration.API.getUniverse() != null && serverSocket != null && !serverSocket.isClosed();
    }

    public void startAPI()
    {
        if(FTBUConfigWebAPI.ENABLED.getBoolean() && !isAPIRunning())
        {
            start();
        }
    }

    public void stopAPI()
    {
        if(isAPIRunning())
        {
            try
            {
                serverSocket.close();
            }
            catch(Exception ex)
            {
                serverSocket = null;
            }
        }
    }
}