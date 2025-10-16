import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Analyser2 {
    public static void main(String[] args) {
        String filePath = "C:\\Users\\spm51\\OneDrive\\Desktop\\Projects\\Interpreter\\input.txt"; 
        
        Tuple cPos = new Tuple(0, 0); 
        int lineNumber = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                
                lineNumber++;
                
                cPos.setX(lineNumber);
                
                cPos.setY(0); 

                tokenize(cPos, line);
            }
            
            Tuple endTuple = new Tuple(lineNumber + 1, 1);
            MyToken eofToken = new MyToken(new MyLexeme("EOF", endTuple), "$");
            System.out.println("Token: " + eofToken + " at Position: " + endTuple);
        
        } catch (IOException e) {
            System.err.println("An error occurred while reading the file: " + filePath);
            e.printStackTrace();
        }
    }

    public static void tokenize(Tuple pos, String line) {
        while(pos.getY() < line.length()) {
            MyToken token = nextToken(pos, line);
            if (token != null) { 
                System.out.println("Token: " + token + " at Position: " + pos);
            }
        }
    }

    public static MyToken nextToken(Tuple pos, String line) {
        
        while (pos.getY() < line.length() && Character.isWhitespace(line.charAt(pos.getY()))) {
            pos.setY(pos.getY() + 1);
        } 
        
        if (pos.getY() >= line.length()) {
            return null; 
        }

        char ch = line.charAt(pos.getY()); 
        pos.setY(pos.getY() + 1); 
        
        return new MyToken(new MyLexeme("CHAR", new Tuple(pos.getX(), pos.getY())), String.valueOf(ch));
    }
}