import java.io.*;
import java.util.*;

class HuffmanNode implements Comparable<HuffmanNode> {
    char data;
    int frequency;
    HuffmanNode left, right;

    public HuffmanNode(char data, int frequency) {
        this.data = data;
        this.frequency = frequency;
        left = right = null;
    }

    public int compareTo(HuffmanNode node) {
        return this.frequency - node.frequency;
    }
}

public class CompressFile {
    static Map<Character, String> huffmanCodes = new HashMap<>();

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