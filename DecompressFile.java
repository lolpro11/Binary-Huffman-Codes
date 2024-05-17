/**
 * Decompresses a file using Binary Huffman Codes
 * @author Joshua Wong, Samriddhi Matharu, Robert Arias, Matthew Ngai
 */
import java.io.*;
import java.util.*;

/**
 * DecompressFile class provides methods for decompressing a file using Huffman coding.
 */
public class DecompressFile {
    /**
     * Main method to decompress a file using Huffman coding.
     *
     * @param args Command line arguments: source file path and destination file path
     */
    public static void main(String[] args) {
        // Ensure there are exactly two arguments
        if (args.length != 2) {
            System.out.println("Usage: java DecompressFile <source file> <destination file>");
            return;
        }

        // Read the source and destination file paths from command line arguments
        String sourceFile = args[0];
        String destinationFile = args[1];

        // Use try-with-resources to ensure streams are closed properly
        try (FileInputStream inputStream = new FileInputStream(sourceFile);
             ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
             FileOutputStream outputStream = new FileOutputStream(destinationFile)) {

            // Step 1: Read Huffman codes from the compressed file
            @SuppressWarnings("unchecked")
            Map<Character, String> huffmanCodes = (Map<Character, String>) objectInputStream.readObject();

            // Step 2: Reconstruct Huffman tree
            HuffmanNode root = reconstructHuffmanTree(huffmanCodes);

            // Step 3: Decode the binary data using Huffman tree
            decodeFile(inputStream, outputStream, root);

        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error occurred: " + e.getMessage());
        }
    }

    /**
     * Reconstructs the Huffman tree using the provided Huffman codes.
     *
     * @param huffmanCodes The Huffman codes extracted from the compressed file
     * @return The root node of the reconstructed Huffman tree
     */
    private static HuffmanNode reconstructHuffmanTree(Map<Character, String> huffmanCodes) {
        // Create an empty root node
        HuffmanNode root = new HuffmanNode('\0', 0);

        // Iterate over each character and its corresponding Huffman code
        for (Map.Entry<Character, String> entry : huffmanCodes.entrySet()) {
            char[] codeArray = entry.getValue().toCharArray(); // Convert the code to a char array

            HuffmanNode current = root; // Start from the root node

            // Traverse the Huffman tree based on the code
            for (char bit : codeArray) {
                if (bit == '0') {
                    if (current.left == null) {
                        current.left = new HuffmanNode('\0', 0); // Create a new left child if it doesn't exist
                    }
                    current = current.left; // Move to the left child
                } else if (bit == '1') {
                    if (current.right == null) {
                        current.right = new HuffmanNode('\0', 0); // Create a new right child if it doesn't exist
                    }
                    current = current.right; // Move to the right child
                }
            } 
            current.data = entry.getKey(); // Assign the character to the leaf node
        }

        return root; // Return the root of the reconstructed Huffman tree
    }

    /**
     * Decodes the binary data from the input stream using the provided Huffman tree
     * and writes the decompressed data to the output stream.
     *
     * @param inputStream  The input stream of the compressed file
     * @param outputStream The output stream to write the decompressed data
     * @param root         The root node of the Huffman tree
     * @throws IOException If an I/O error occurs
     */
    private static void decodeFile(FileInputStream inputStream, FileOutputStream outputStream, HuffmanNode root) throws IOException {
        HuffmanNode current = root; // Start from the root of the Huffman tree
        int bits;
        // Read each byte from the input stream
        while ((bits = inputStream.read()) != -1) {
            // Process each bit in the byte
            for (int i = 7; i >= 0; i--) {
                int bit = (bits >> i) & 1; // Extract the bit at position i
                current = (bit == 0) ? current.left : current.right; // Traverse left or right based on the bit
                if (current.left == null && current.right == null) {
                    // If a leaf node is reached, write the character to the output stream
                    outputStream.write(current.data);
                    current = root; // Reset to the root for the next character
                }
            }
        }
    }
}
