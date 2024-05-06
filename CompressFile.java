import java.io.*;

public class CompressFile {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java CompressFile <source file> <destination file>");
            return;
        }

        String sourceFile = args[0];
        String destinationFile = args[1];

        try (FileInputStream inputStream = new FileInputStream(sourceFile);
             FileOutputStream outputStream = new FileOutputStream(destinationFile)) {

            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

        } catch (IOException e) {
            System.out.println("Error occurred: " + e.getMessage());
        }
    }
}
