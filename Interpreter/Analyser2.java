import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Analyser2 {
    public static void main(String[] args) {
        String filePath = "C:\\Users\\spm51\\OneDrive\\Desktop\\Projects\\Interpreter\\input.txt"; 
        // Initializing cPos with a Tuple object
        Tuple cPos = new Tuple(0, 0); 
        int lineNumber = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // The cPos should likely be reset/updated here for a new line,
                lineNumber++;
                // but we stick to the provided logic for now.
                tokenize(cPos, line);
            }
            Tuple endTuple = new Tuple(lineNumber + 1, 1);
            MyToken eofToken = new MyToken(new MyLexeme("EOF", endTuple), "$");
            System.out.println("Token: " + eofToken + " at Position: " + endTuple);
        } catch (IOException e) {
            // Handle IO errors here (e.g., file not found)
            System.err.println("An error occurred while reading the file: " + filePath);
            e.printStackTrace();
        }
    }

    // FIX 1: Make method static to be called from main
    public static void tokenize(Tuple pos, String line) {
        while(pos.getY() < line.length()) {
            MyToken token = nextToken(pos,line);
            if (token != null) { // Added null check for nextToken return
                System.out.println("Token: " + token + " at Position: " + pos);
            }
        }

    }

    // FIX 2: Make method static to be called from the static tokenize method
    public static MyToken nextToken(Tuple pos, String line) {
        // Dummy implementation for illustration
        if (pos.getX() >= line.length()) {
            return null; // No more tokens
        }
        char ch = line.charAt(pos.getX());
        pos.setX(pos.getY() + 1); // Move to the next character
        
        // FIX 4: Assuming a constructor MyToken(String) is used for simple tokens.
        return new MyToken(new MyLexeme("CHAR", new Tuple(pos.getX() - 1, pos.getY())), String.valueOf(ch));
    }
}