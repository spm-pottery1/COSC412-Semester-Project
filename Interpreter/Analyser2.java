import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Analyser2 {


    public static void main(String[] args) {
        // Similar implementation as Analyser class
        String filePath = "yourFile.txt"; 

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                System.out.println("Line " + lineNumber + ": " + line);
                // Process the line further (e.g., character by character)
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
