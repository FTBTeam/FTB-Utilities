package latmod.core.util;
import java.io.*;
import java.util.UUID;

/** Made by LatvianModder <br>
 * DataInputStream + DataOutputStream */
public class DataIOStream
{
	/** Input stream */
	private InputStream input;
	
	/** Output stream */
	private OutputStream output;
	
	private final byte[] temp = new byte[8];
	
	/** Instance for reading and writing and with optional compression level */
	public DataIOStream(InputStream is, OutputStream os) throws Exception
	{
		input = (is == null) ? null : new BufferedInputStream(is);
		output = (os == null) ? null : new BufferedOutputStream(os);
	}
	
	/** Flushes output stream */
	public void flush() throws IOException
	{ if(output != null) output.flush(); }
	
	/** Stops streams */
	public void close() throws IOException
	{
		if(input != null) input.close();
		if(output != null) output.close();
	}
	
	public boolean isClosed()
	{ return input == null && output == null; }
	
	/** @return Bytes available to read */
	public int available() throws Exception
	{ return input.available(); }
	
	public void clear() throws Exception
	{ while(available() > 0) readByte(); }
	
	// Write functions //
	
	public void writeUByte(int i) throws IOException
	{ output.write(i); }
	
	public void writeByte(byte i) throws IOException
	{ writeUByte(i); }
	
	public void writeRawBytes(byte[] b, int off, int len) throws Exception
	{ output.write(b, off, len); }
	
	public void writeRawBytes(byte[] b) throws Exception
	{ writeRawBytes(b, 0, b.length); }
	
	public void writeByteArray(byte[] b) throws Exception
	{
		if(b == null) { writeUShort(-1); return; }
		writeUShort(b.length);
		writeRawBytes(b);
	}
	
	private void writeTemp(int i) throws Exception
	{ writeRawBytes(temp, 0, i); }
	
	public void writeBoolean(boolean b) throws Exception
	{ writeUByte(b ? 1 : 0); }
	
	public void writeString(String s) throws Exception
	{ writeByteArray(LMStringUtils.toBytes(s, false)); }
	
	public void writeUTF(String s) throws Exception
	{ writeByteArray(LMStringUtils.toBytes(s, true)); }
	
	public void writeUShort(int s) throws Exception
	{ Bits.fromUShort(temp, s); writeTemp(2); }
	
	public void writeShort(short s) throws Exception
	{ writeUShort(s); }
	
	public void writeInt(int i) throws Exception
	{ Bits.fromInt(temp, i); writeTemp(4); }
	
	public void writeLong(long l) throws Exception
	{ Bits.fromLong(temp, l); writeTemp(8); }
	
	public void writeFloat(float f) throws Exception
	{ writeInt(Float.floatToIntBits(f)); }
	
	public void writeDouble(double d) throws Exception
	{ writeLong(Double.doubleToLongBits(d)); }
	
	public void writeUUID(UUID id) throws Exception
	{ writeLong(id.getMostSignificantBits()); writeLong(id.getLeastSignificantBits()); }
	
	// Read functions //
	
	public int readUByte() throws Exception
	{ return input.read(); }
	
	public byte readByte() throws Exception
	{ return (byte)readUByte(); }
	
	public void readRawBytes(byte[] b, int off, int len) throws Exception
	{ input.read(b, off, len); }
	
	public void readRawBytes(byte[] b) throws Exception
	{ readRawBytes(b, 0, b.length); }
	
	public byte[] readByteArray() throws Exception
	{
		int s = readUShort();
		if(s == -1) return null;
		byte[] b = new byte[s];
		readRawBytes(b);
		return b;
	}
	
	private byte[] readTemp(int i) throws Exception
	{ readRawBytes(temp, 0, i); return temp; }
	
	public boolean readBoolean() throws Exception
	{ return readUByte() == 1; }
	
	public String readString() throws Exception
	{ return LMStringUtils.fromBytes(readByteArray(), false); }
	
	public String readUTF()  throws Exception
	{ return LMStringUtils.fromBytes(readByteArray(), true); }
	
	public int readUShort() throws Exception
	{ return Bits.toUShort(readTemp(2)); }
	
	public short readShort() throws Exception
	{ return (short)readUShort(); }
	
	public int readInt() throws Exception
	{ return Bits.toInt(readTemp(4)); }
	
	public long readLong() throws Exception
	{ return Bits.toLong(readTemp(8)); }
	
	public float readFloat() throws Exception
	{ return Float.intBitsToFloat(readInt()); }
	
	public double readDouble() throws Exception
	{ return Double.longBitsToDouble(readLong()); }
	
	public UUID readUUID() throws Exception
	{ return new UUID(readLong(), readLong()); }
}