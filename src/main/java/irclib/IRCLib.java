package irclib;

import java.io.*;
import java.net.Socket;
import java.util.*;
import java.util.regex.*;

import com.google.common.collect.*;

public class IRCLib extends Thread {
    public BufferedWriter out;
    public BufferedReader in;
    protected boolean bConnected;
    protected List<String> lChannel;
    protected String sServer;
    protected String sNick;
    protected String sName;
    protected String sUser;
    protected String sKey;
    protected String SASLUser;
    protected String SASLPass;
    protected Integer iPort;
    protected Socket socket;
    private Map<String, IRCCommand> commands = Maps.newHashMap();
    private ArrayList<Coloring> colorings = Lists.newArrayList();

    public IRCLib () {
        super();
        this.lChannel = Lists.newArrayList();
        this.sName = "sdIRC";
        this.sUser = "sdIRC";
        this.sKey = "";
        this.SASLUser = "";
        this.SASLPass = "";
    }

    public boolean connect (final String s, final Integer p) throws IOException {
        if (this.sNick == null) {
            return false;
        }
        if (this.bConnected) {
            return false;
        }
        this.socket = new Socket(s, p);
        this.bConnected = true;
        this.out = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream()));
        this.in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
        if (!this.isAlive()) {
            this.start();
        }
        if (!this.SASLUser.isEmpty() && !this.SASLPass.isEmpty()) {
            this.out.write("CAP REQ :sasl\n");
        }
        if (!this.sKey.isEmpty()) {
            this.out.write("PASS " + this.sKey + "\n");
        }
        this.out.write("USER " + this.sUser + " * * :" + this.sName + "\n");
        this.out.write("NICK " + this.sNick + "\n");
        this.out.flush();
        return true;
    }

    public boolean connect (final String s, final Integer p, final String n) throws IOException {
        this.sNick = n;
        return this.connect(s, p);
    }

    public boolean connect (final String s, final Integer p, final String n, final String k) throws IOException {
        this.sNick = n;
        this.sKey = k;
        return this.connect(s, p);
    }

    public void close () throws IOException {
        this.bConnected = false;
        this.socket.close();
        this.in = null;
    }

    public String getServer () {
        return this.bConnected ? this.sServer : null;
    }

    public String getNick () {
        return this.sNick;
    }

    public boolean setNick (final String n) {
        if (this.bConnected) {
            this.sNick = n;
            this.sendRaw("NICK :" + n);
            return true;
        }
        this.sNick = n;
        return true;
    }

    public String getUser () {
        return this.sUser;
    }

    public boolean setUser (final String u) {
        if (this.bConnected) {
            return false;
        }
        this.sUser = u;
        return true;
    }

    public boolean setInfo (final String i) {
        if (this.bConnected) {
            return false;
        }
        this.sName = i;
        return true;
    }

    public void setSASLUser (final String s) {
        this.SASLUser = s;
    }

    public void setSASLPass (final String s) {
        this.SASLPass = s;
    }

    public List<String> getChannels () {
        return this.lChannel;
    }

    public boolean sendRaw (final String s) {
        if (this.bConnected) {
            try {
                this.out.write(s + "\n");
                this.out.flush();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return this.bConnected = false;
            }
        }
        return false;
    }

    private static String validNick = "[A-}][0-9A-}-]*";
    private static String validUser = "(?:(?=[\u0001-\u00ff])[^\\s@])+";
    private static Pattern mask = Pattern.compile(":("+validNick+")!("+validUser+")@("+validUser+')');
    protected Matcher parseMask (final String m) {
        final Matcher iUser = mask.matcher(m);
        return iUser.matches() ? iUser : null;
    }

    protected void process (final String line) {
        if (line.toUpperCase().startsWith("PING ")) {
            this.sendRaw("PONG " + line.substring(5));
        } else if (line.toUpperCase().startsWith("AUTHENTICATE +")) {
            this.sendRaw("AUTHENTICATE " + this.getSASL(this.SASLUser, this.SASLPass));
        } else {
            final String[] aLine = line.split(" ", 3);
            this.processCommand(aLine[1], line);
        }
    }

    protected void processCommand (final String sCommand, final String sLine) {
        final String[] sParsed = sLine.split(" ");
        if (sCommand.toUpperCase().trim().equals("004")) {
            this.onConnected();
        } else if (sCommand.toUpperCase().trim().equals("433")) {
            this.setNick(this.sNick + "_");
        } else if (sCommand.toUpperCase().equals("CAP")) {
            if (sParsed[3].equals("ACK")) {
                this.sendRaw("AUTHENTICATE PLAIN");
            }
        } else if (sCommand.toUpperCase().trim().equals("903") || sCommand.toUpperCase().trim().equals("904") || sCommand.toUpperCase().trim().equals("905")
                || sCommand.toUpperCase().trim().equals("906") || sCommand.toUpperCase().trim().equals("907")) {
            this.sendRaw("CAP END");
        } else if (!sCommand.toUpperCase().trim().equals("432")) {
            if (sCommand.toUpperCase().trim().equals("NICK")) {
                Matcher iUser = parseMask(sParsed[0]);
                String snNick = sParsed[0].split("!")[0].replaceFirst(":", "");
                if (iUser != null && iUser.group(1).equals(this.sNick)) {
                    this.sNick = sParsed[2].replaceFirst(":", "");
                    onNick(snNick, this.sNick);
                }
                this.onNick(snNick, sParsed[2].replaceFirst(":", ""));
            } else if (sCommand.toUpperCase().trim().equals("JOIN")) {
                final Matcher iUser = this.parseMask(sParsed[0]);
                final String sOrigin = (iUser == null) ? sParsed[0].replaceFirst(":", "") : iUser.group(1);
                if (iUser == null) {
                    this.onJoin(sOrigin, sParsed[2]);
                } else {
                    if (iUser.group(1).equals(this.sNick)) {
                        this.lChannel.add(sParsed[2]);
                    }
                    this.onJoin(iUser.group(1), iUser.group(2), iUser.group(3), sParsed[2]);
                }
            } else if (sCommand.toUpperCase().trim().equals("PART")) {
                final Matcher iUser = this.parseMask(sParsed[0]);
                if (iUser.group(1).equals(this.sNick)) {
                    this.lChannel.remove(sParsed[2]);
                }
                final String sPart = (sLine.split(" ", 4).length > 3) ? sLine.split(" ", 4)[3].replaceFirst(":", "") : null;
                this.onPart(iUser.group(1), iUser.group(2), iUser.group(3), sParsed[2], sPart);
            } else if (sCommand.toUpperCase().trim().equals("KICK")) {
                final Matcher iUser = this.parseMask(sParsed[0]);
                final String sOrigin = (iUser == null) ? sParsed[0].replaceFirst(":", "") : iUser.group(1);
                if (iUser == null) {
                    this.onKick(sOrigin, sParsed[3], sParsed[2], sParsed[4]);
                } else {
                    if (iUser.group(1).equals(this.sNick)) {
                        this.lChannel.remove(sParsed[2]);
                    }
                    this.onKick(iUser.group(1), iUser.group(2), iUser.group(3), sParsed[3], sParsed[2], sParsed[4]);
                }
            } else if (sCommand.toUpperCase().trim().equals("QUIT")) {
                final Matcher iUser = this.parseMask(sParsed[0]);
                this.onQuit(iUser.group(1), iUser.group(2), iUser.group(3), sLine.split(" ", 3)[2].replaceFirst(":", ""));
            } else if (sCommand.toUpperCase().trim().equals("PRIVMSG")) {
                final Matcher iUser = this.parseMask(sParsed[0]);
                final String sOrigin = (iUser == null) ? sParsed[0].replaceFirst(":", "") : iUser.group(1);
                if (sLine.split(" ", 4)[3].replaceFirst(":", "").startsWith(Character.toString('\u0001'))) {
                    if (iUser == null) {
                        this.processCTCP(sOrigin, sParsed[2], sLine.split(" ", 4)[3].replaceFirst(":", "").substring(1, sLine.split(" ", 4)[3].replaceFirst(":", "").length() - 1));
                    } else {
                        this.processCTCP(iUser.group(1), iUser.group(2), iUser.group(3), sParsed[2],
                                sLine.split(" ", 4)[3].replaceFirst(":", "").substring(1, sLine.split(" ", 4)[3].replaceFirst(":", "").length() - 1));
                    }
                } else if (iUser == null) {
                    this.onMessage(sOrigin, sParsed[2], sLine.split(" ", 4)[3].replaceFirst(":", ""));
                } else {
                    String m = sLine.split(" ", 4)[3].replaceFirst(":", "");
                    String[] v = m.split(" ");
                    List<String> temp = null;
                    if (commands.containsKey(v[0])) {
                        temp = commands.get(v[0]).onCommand(m);
                    }
                    if (temp == null) {
                        this.onMessage(iUser.group(1), iUser.group(2), iUser.group(3), sParsed[2], m);
                    } else {
                        for (String s : temp) {
                            sendMessage(sParsed[2], s);
                        }
                    }
                }
            } else if (sCommand.toUpperCase().trim().equals("NOTICE")) {
                final Matcher iUser = this.parseMask(sParsed[0]);
                final String sOrigin = (iUser == null) ? sParsed[0].replaceFirst(":", "") : iUser.group(1);
                if (iUser == null) {
                    this.onNotice(sOrigin, sParsed[2], sLine.split(" ", 4)[3].replaceFirst(":", ""));
                } else {
                    this.onNotice(iUser.group(1), iUser.group(2), iUser.group(3), sParsed[2], sLine.split(" ", 4)[3].replaceFirst(":", ""));
                }
            }
        }
    }

    public void processCTCP (final String n, final String u, final String h, final String d, final String m) {
        final String[] mParts = m.split(" ", 2);
        if (mParts[0].equals("ACTION")) {
            this.onAction(n, u, h, d, mParts[1]);
        } else {
            this.onCTCP(n, u, h, d, m);
        }
    }

    public void processCTCP (final String s, final String d, final String m) {
        final String[] mParts = m.split(" ", 2);
        if (mParts[0].equals("ACTION")) {
            this.onAction(s, d, mParts[1]);
        } else {
            this.onCTCP(s, d, m);
        }
    }

    public String getSASL (final String u, final String p) {
        return Base64.encode(u + Character.toString('\0') + u + Character.toString('\0') + p);
    }

    public void run () {
        try {
            String line = null;
            while (this.in != null) {
                if ((line = this.in.readLine()) != null) {
                    process(line);
                } else if (!bConnected || !socket.isConnected()) {
                    socket.close();
                    connect(socket.getInetAddress().getHostAddress(), socket.getPort());
                }
            }
        } catch (IOException ex) {
            System.out.println("[IRCLIB]" + ex.getMessage() == null ? "null" : ex.getMessage());
        }
    }

    public void joinChannel (final String c) {
        this.sendRaw("JOIN " + c);
    }

    public void joinChannel (final String c, final String k) {
        this.sendRaw("JOIN " + c + " " + k);
    }

    public void partChannel (final String c) {
        this.sendRaw("PART " + c);
    }

    public void sendMessage (final String d, final String m) {
        this.sendRaw("PRIVMSG " + d + " :" + m);
    }

    public void sendAction (final String d, final String m) {
        this.sendCTCP(d, "ACTION " + m);
    }

    public void sendCTCP (final String d, final String m) {
        this.sendRaw("PRIVMSG " + d + " :" + Character.toString('\u0001') + m + Character.toString('\u0001'));
    }

    public void sendCTCPReply (final String d, final String m) {
        this.sendRaw("NOTICE " + d + " :" + Character.toString('\u0001') + m + Character.toString('\u0001'));
    }

    public void onConnected () {
    }

    public void onNick (final String oldNick, final String newNick) {
    }

    public void onJoin (final String n, final String u, final String h, final String c) {
    }

    public void onJoin (final String s, final String c) {
    }

    public void onPart (final String n, final String u, final String h, final String c, final String r) {
    }

    public void onPart (final String s, final String c, final String r) {
    }

    public void onKick (final String n, final String kn, final String u, final String h, final String c, final String r) {
    }

    public void onKick (final String s, final String kn, final String c, final String r) {
    }

    public void onQuit (final String n, final String u, final String h, final String r) {
    }

    public void onCTCP (final String n, final String u, final String h, final String d, final String m) {
    }

    public void onCTCP (final String s, final String d, final String m) {
    }

    public void onAction (final String s, final String d, final String m) {
    }

    public void onAction (final String n, final String u, final String h, final String d, final String m) {
    }

    public void onMessage (final String n, final String u, final String h, final String d, final String m) {
    }

    public void onMessage (final String s, final String d, final String m) {
    }

    public void onNotice (final String n, final String u, final String h, final String d, final String m) {
    }

    public void onNotice (final String s, final String d, final String m) {
    }

    /**
     *
     * @param command the command to register ex: !players or !tps
     * @param callback the IRCCommand to run when this command is entered
     * @return true if the command is successfully registered, false if it is not registered
     */
    public boolean registerCommand (String command, IRCCommand callback) {
        if (commands.containsKey(command)) {
            return false;
        }
        commands.put(command, callback);
        return true;
    }

    /**
     *
     * @param command the command to check
     * @return true if the command is registered, false otherwise
     */
    public boolean isCommandRegistered (String command) {
        return commands.containsKey(command);
    }

    /**
     * removes a command if it exists
     * @param command command to remove
     */
    public void removeCommand (String command) {
        commands.remove(command);
    }

    /**
     *
     * @return Immutable Map of commands and their callbacks
     */
    @SuppressWarnings("rawtypes")
    public ImmutableMap getCommands () {
        return ImmutableMap.builder().putAll(commands).build();
    }

    /**
     *
     * @param c coloring to process during chat events
     */
    public void addColoring (Coloring c) {
        colorings.add(c);
    }

    /**
     * WARNING: DO NOT MODIFY THIS LIST!!!!
     * @return list of colorings
     */
    public List<Coloring> getColorings () {
        return colorings;
    }
}
