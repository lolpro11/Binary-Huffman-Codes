import java.io.*;

public class BitOutputStream {

    private FileOutputStream outputStream;
    private int currentByte;
    private int numBits;

    /**
     * Constructs a BitOutputStream to write bits to the specified file output stream.
     * 
     * @param outputStream the output stream to write bits to
     */
    public BitOutputStream(FileOutputStream outputStream) {
        this.outputStream = outputStream;
        this.currentByte = 0;
        this.numBits = 0;
    }

    /**
     * Writes a single bit ('0' or '1') to the output stream.
     * 
     * @param bit the bit to write ('0' or '1')
     * @throws IOException if an I/O error occurs
     */
    public void writeBit(char bit) throws IOException {
        if (bit != '0' && bit != '1') {
            throw new IllegalArgumentException("Invalid bit: " + bit);
        }
        currentByte = (currentByte << 1) | (bit - '0');
        numBits++;
        if (numBits == 8) {
            outputStream.write(currentByte);
            currentByte = 0;
            numBits = 0;
        }
    }

    /**
     * Writes a string of bits to the output stream.
     * 
     * @param bitString the string of bits to write
     * @throws IOException if an I/O error occurs
     */
    public void writeBits(String bitString) throws IOException {
        for (char bit : bitString.toCharArray()) {
            writeBit(bit);
        }
    }

    /**
     * Closes the output stream, writing any remaining bits.
     * 
     * @throws IOException if an I/O error occurs
     */
    public void close() throws IOException {
        while (numBits != 0 && numBits != 8) {
            writeBit('0'); // Fill remaining bits with zeros
        }
        outputStream.close();
    }
}