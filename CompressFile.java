import java.io.*;
import java.util.*;

/**
 * Represents a node in the Huffman tree.
 */
class HuffmanNode implements Comparable<HuffmanNode> {
    char data; // The character stored in this node
    int frequency; // The frequency of the character
    HuffmanNode left, right; // References to the left and right children

    /**
     * Constructs a HuffmanNode with the given character and frequency.
     *
     * @param data      The character stored in the node
     * @param frequency The frequency of the character
     */
    public HuffmanNode(char data, int frequency) {
        this.data = data;
        this.frequency = frequency;
        left = right = null;
    }


    /**
     * Compares this HuffmanNode with another based on their frequencies.
     *
     * @param node The other HuffmanNode to compare
     * @return A negative integer, zero, or a positive integer if this node's frequency
     *         is less than, equal to, or greater than the frequency of the specified node.
     */
    @Override
    public int compareTo(HuffmanNode node) {
        return this.frequency - node.frequency;
    }
}

/**
 * CompressFile class provides methods for compressing a file using Huffman coding.
 */
public class CompressFile {
    static Map<Character, String> huffmanCodes = new HashMap<>();

    /**
     * Main method to compress a file using Huffman coding.
     *
     * @param args Command line arguments: source file path and destination file path
     */
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java CompressFile <source file> <destination file>");
            return;
        }

        String sourceFile = args[0];
        String destinationFile = args[1];
        FileInputStream inputStream = null;
        ObjectOutputStream objectOutputStream = null;
        BitOutputStream bitOutputStream = null;

        try {
            inputStream = new FileInputStream(sourceFile);
            FileOutputStream fileOutputStream = new FileOutputStream(destinationFile);
            objectOutputStream = new ObjectOutputStream(fileOutputStream);
            bitOutputStream = new BitOutputStream(fileOutputStream); 
            
            // Step 1: Calculate frequency of characters
            Map<Character, Integer> frequencyMap = new HashMap<>();
            int character;
            while ((character = inputStream.read()) != -1) {
                char c = (char) character;
                frequencyMap.put(c, frequencyMap.getOrDefault(c, 0) + 1);
            }

            // Step 2: Build Huffman tree
            HuffmanNode root = buildHuffmanTree(frequencyMap);

            // Step 3: Generate Huffman codes
            generateHuffmanCodes(root, new StringBuilder());

            // Step 4: Write Huffman tree to file using ObjectOutputStream
            objectOutputStream.writeObject(huffmanCodes);

            // Step 5: Compress source file using Huffman codes and BitOutputStream
            inputStream.close();
            inputStream = new FileInputStream(sourceFile);
            encodeFile(inputStream, bitOutputStream);

        } catch (IOException e) {
            System.out.println("Error occurred: " + e.getMessage());
        } finally {
            try {
                if (inputStream != null) inputStream.close();
                if (objectOutputStream != null) objectOutputStream.close();
                if (bitOutputStream != null) bitOutputStream.close();
            } catch (IOException e) {
                System.out.println("Error occurred while closing streams: " + e.getMessage());
            }
        }
    }


    /**
     * Builds the Huffman tree based on the given frequency map.
     *
     * @param frequencyMap A map containing characters and their frequencies
     * @return The root of the Huffman tree
     */
    private static HuffmanNode buildHuffmanTree(Map<Character, Integer> frequencyMap) {
        PriorityQueue<HuffmanNode> priorityQueue = new PriorityQueue<>();
        for (Map.Entry<Character, Integer> entry : frequencyMap.entrySet()) {
            priorityQueue.offer(new HuffmanNode(entry.getKey(), entry.getValue()));
        }

        while (priorityQueue.size() > 1) {
            HuffmanNode left = priorityQueue.poll();
            HuffmanNode right = priorityQueue.poll();
            HuffmanNode parent = new HuffmanNode('\0', left.frequency + right.frequency);
            parent.left = left;
            parent.right = right;
            priorityQueue.offer(parent);
        }

        return priorityQueue.poll();
    }


    /**
     * Recursively generates Huffman codes for each character in the tree.
     *
     * @param root The root of the Huffman tree
     * @param code A StringBuilder to store the generated code
     */
    private static void generateHuffmanCodes(HuffmanNode root, StringBuilder code) {
        if (root == null) return;
        if (root.left == null && root.right == null) {
            huffmanCodes.put(root.data, code.toString());
            return;
        }
        generateHuffmanCodes(root.left, code.append('0'));
        code.deleteCharAt(code.length() - 1);
        generateHuffmanCodes(root.right, code.append('1'));
        code.deleteCharAt(code.length() - 1);
    }

    /**
     * Encodes the source file using the generated Huffman codes and writes to the output stream.
     *
     * @param inputStream    The input stream of the source file
     * @param bitOutputStream The BitOutputStream to write the encoded data
     * @throws IOException If an I/O error occurs
     */
    private static void encodeFile(FileInputStream inputStream, BitOutputStream bitOutputStream) throws IOException {
        int character;
        while ((character = inputStream.read()) != -1) {
            char c = (char) character;
            String code = huffmanCodes.get(c);
            bitOutputStream.writeBits(code);
        }
        bitOutputStream.close();
    }
}
