//Simon Murray
//Build #4
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Analyser {
    public static int currentIndex = 0;
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        HashSet<String> keywordSet = new HashSet<>();
        HashSet<String> symbolSet = new HashSet<>();
        HashSet<Character> charSet = new HashSet<>();
        initLists(keywordSet,symbolSet,charSet);
        File inputFile = new File(fileLoc(scanner));
        
        HashMap<Integer, String> fileContent = nextFromFile(inputFile);
        
        List<String> tokenStrings = getTokenStrings(fileContent, symbolSet);
        
        List<String> cTokenStrings = cTokenStrings(tokenStrings, keywordSet, symbolSet);
        
        Tuple[] positions = getTokenPositions(fileContent, tokenStrings);
        
        List<MyToken> tokens = createTokens(tokenStrings, cTokenStrings, positions, keywordSet, symbolSet);
        print(tokens);  
        parse(tokens);
    }

    public static void parse(List<MyToken> tokens) {
        // FIX: Removed local currentIndex and used global
        programHeaderParse(tokens); 
        System.out.println(tokens.get(currentIndex));
        System.out.println("Entering Body Parse");
        bodyParse(tokens);
    }

    // FIX: Removed currentIndex parameter
    public static void bodyParse(List<MyToken> tokens) {
        // FIX: Removed currentIndex parameter from getNextToken
        MyToken currentToken = getNextToken(tokens);
        //System.out.println("Current Token in Body Parse: " + currentToken);
        currentIndex--; // Backtrack, since getNextToken advanced it.
        currentToken = tokens.get(currentIndex); // Get the token without advancing index
        
        boolean isInt = currentToken.getLexeme().getKind().equals("int");
        boolean isBool = currentToken.getLexeme().getKind().equals("bool");

        
        declarationsParse(tokens, isBool, isInt);
        statementParse(tokens);

    }

    public static void declarationsParse(List<MyToken> tokens, boolean isBool, boolean isInt) {
        MyToken currentToken = getNextToken(tokens);
        System.out.println(currentToken);
        while(currentToken != null && currentToken.getLexeme().getKind().equals(",")) {
            System.out.println("Is it in you?");
            declaration(tokens);
            currentToken = getNextToken(tokens);
        }
        
    }
    
    public static void declaration(List<MyToken> tokens) {
        MyToken currentToken = getNextToken(tokens);
        matchToken(currentToken, "ID");
        while(currentToken.getLexeme().getKind().equals(",")){
            System.out.println(currentToken);
            currentToken=getNextToken(tokens);
            matchToken(currentToken, "ID");
        }
        matchToken(currentToken, ";");
    }

    public static void statementParse(List<MyToken> tokens) {
        // FIX: Removed currentIndex parameter from getNextToken
        MyToken currentToken = getNextToken(tokens);
        
    }

    // FIX: Removed currentIndex parameter and return value
    public static void programHeaderParse(List<MyToken> tokens) {
        
        // getNextToken advances the global currentIndex
        MyToken currentToken = getNextToken(tokens); 
        matchToken(currentToken, "program");
        
        currentToken = getNextToken(tokens);
        matchToken(currentToken, "ID");
        
        currentToken = getNextToken(tokens);
        matchToken(currentToken,":");

    }


    public static boolean matchToken(MyToken currentToken, String expectedKind) {
        if(currentToken.getLexeme().getKind().equals(expectedKind)) {
            System.out.println("Matched " + expectedKind + " at " + currentToken.getLexeme().get_sPos());
            return true;
        } else {
            System.out.println("Error: Expected " + expectedKind + " at " + currentToken.getLexeme().get_sPos());
            return false;
        }
    }

    // FIX: Removed currentIndex parameter. It now uses and increments the global one.
    public static MyToken getNextToken(List<MyToken> tokens) {
        if (currentIndex < tokens.size()) {
            return tokens.get(currentIndex++); // Uses global currentIndex
        } else {
            return null; // No more tokens
        }
    }

    
    public static List<MyToken> createTokens(List<String> tokenStrings, List<String> cTokenStrings, Tuple[] positions, HashSet<String> keywordSet, HashSet<String> symbolSet) {
        /*creates my tokens and stores them in a List before printing them. 
         * This uses a cheked token value for kind (ctoken) and a string for value (token)
         */
        List<MyToken> tokenSet = new ArrayList<>(tokenStrings.size());         
         int i = 0; 
         while (!tokenStrings.isEmpty()) { 
             String token = tokenStrings.remove(0); 
             String cToken = cTokenStrings.remove(0); 
           
            if (i >= positions.length) {
                break; 
            }
            Tuple position = positions[i];
            tokenSet.add(new MyToken(new MyLexeme(cToken, position), token));
            i++; 
        }
        
        if (positions.length > 0) {
            tokenSet.add(new MyToken(new MyLexeme("END-OF-FILE", new Tuple(positions[positions.length - 1].getX(), positions[positions.length - 1].getY() + 1)), "$"));
        } else {
            // Handle case with no tokens in file
             tokenSet.add(new MyToken(new MyLexeme("END-OF-FILE", new Tuple(1, 1)), "$"));
        }
        
        return(tokenSet);
    }


    private static Tuple[] getTokenPositions(HashMap<Integer, String> fileContent, List<String> tokenStrings) {
        Tuple[] positions = new Tuple[tokenStrings.size()];
        int tokenIndex = 0;
        for (int i = 0; i < fileContent.size(); i++) {
            String line = fileContent.get(i);
            if (line == null) {
                continue;
            }
            int j = 0;
            while (j < line.length() && tokenIndex < tokenStrings.size()) {
                
                if (j + 1 < line.length() && line.substring(j, j + 2).equals("//")) {
                // If comment is found, skip the rest of the line
                break; 
                }
                
                char c = line.charAt(j);
                if (Character.isWhitespace(c)) {
                    j++;
                    continue;
                }
                String currentToken = tokenStrings.get(tokenIndex);
                if (line.startsWith(currentToken, j)) {
                    positions[tokenIndex] = new Tuple(i + 1, j + 1);
                    tokenIndex++;
                    j += currentToken.length();
                } else {
                    j++;
                }
            }
        }
        return positions;
    }

    private static List<String> cTokenStrings(List<String> tokenStrings, HashSet<String> keywordSet, HashSet<String> symbolSet) {
        List<String> cTokenStrings = new ArrayList<>();
        for (String token : tokenStrings) {
            String cToken = check(token, keywordSet, symbolSet);
            cTokenStrings.add(cToken);
        }
        return cTokenStrings;
    }

    private static List<String> getTokenStrings(HashMap<Integer, String> fileContent, HashSet<String> symbolSet) {
    List<String> tokenStrings = new ArrayList<>();
    StringBuilder tokenValue = new StringBuilder();
    for (int i = 0; i < fileContent.size(); i++) {
        String line = fileContent.get(i);
        if (line == null) {
            continue;
        }
        int j = 0;
        while (j < line.length()) {
            char c = line.charAt(j);

            if (Character.isWhitespace(c)) {
                if (tokenValue.length() > 0) {
                    tokenStrings.add(tokenValue.toString());
                    tokenValue.setLength(0);
                }
                j++;
            } else {  
                String twoCharSymbol = (j + 1 < line.length()) ? line.substring(j, j + 2) : null;      
                
                if (twoCharSymbol != null && twoCharSymbol.equals("//")) {
                    // If a token was being built, add it first.
                        if (tokenValue.length() > 0) {
                        tokenStrings.add(tokenValue.toString());
                        tokenValue.setLength(0);
                        }
                    break; // Exit the inner while loop to move to the next line
                }
                
                String oneCharSymbol = String.valueOf(c);
                if (twoCharSymbol != null && symbolSet.contains(twoCharSymbol)) {
                    if (tokenValue.length() > 0) {
                        tokenStrings.add(tokenValue.toString());
                        tokenValue.setLength(0);
                    }
                    tokenStrings.add(twoCharSymbol);
                    j += 2;
                } else if (symbolSet.contains(oneCharSymbol)) {
                    if (tokenValue.length() > 0) {
                        tokenStrings.add(tokenValue.toString());
                        tokenValue.setLength(0);
                    }
                    tokenStrings.add(oneCharSymbol);
                    j += 1;
                } else {
                    if (!Character.isLetterOrDigit(c) && c != '_') {
                        System.out.println("Error: Unrecognized character '" + c + "' at line " + (i + 1) + ", char " + (j + 1) + ".");
                        System.exit(1);
                    }

                    tokenValue.append(c);
                    j++;
                }
            }
        }
        if (tokenValue.length() > 0) {
            tokenStrings.add(tokenValue.toString());
            tokenValue.setLength(0);
        }
    }
    return tokenStrings;
}

    private static HashMap<Integer, String> nextFromFile(File inputFile) {
        try {
            Scanner fileScanner = new Scanner(inputFile);
            HashMap<Integer, String> hashMap = new HashMap<>();
            int lineCount = 0;
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                hashMap.put(lineCount, line);
                lineCount++;
            }
            fileScanner.close();
            return hashMap;
        } catch (FileNotFoundException e) {
            System.out.println("Error: File not found.");
            System.exit(1);
        }
        return new HashMap<>();
    } 

    public static File getFile(Scanner scanner) {
        System.out.println("Enter file path: ");
        String filePath = scanner.nextLine();
        File inputFile = new File(filePath);
        if(!inputFile.exists() || !inputFile.isFile()) {
            System.out.println("Error: Invalid file path.");
            System.exit(1);
        }
        return inputFile;
    }

    public static void initLists(HashSet<String> keywordSet, HashSet<String> symbolSet, HashSet<Character> charSet) {
        initKeywordSet(keywordSet);
        initSymbolSet(symbolSet);
        initCharSet(charSet);
    }


    public static void print(List<MyToken> tokenSet){
        int tokenCount = 0;

        for(MyToken token: tokenSet) {
            System.out.println("TOKEN " + tokenCount + ": " + token);
            tokenCount++;
        }
    }

    private static void initCharSet(HashSet<Character> charSet) {
        for (char c = 'a'; c <= 'z'; c++) charSet.add(c);
        for (char c = 'A'; c <= 'Z'; c++) charSet.add(c);
        charSet.add('_');
    }
    public static void initKeywordSet(HashSet<String> hashSet) {
        hashSet.add("for");
        hashSet.add("while");
        hashSet.add("do");
        hashSet.add("end");
        hashSet.add("true");
        hashSet.add("false");
        hashSet.add("or");
        hashSet.add("mod");
        hashSet.add("add");
        hashSet.add("not");
        hashSet.add("int");
        hashSet.add("bool");
        hashSet.add("if");
        hashSet.add("then");
        hashSet.add("else");
        hashSet.add("print");
        hashSet.add("program");
    }
    public static void initSymbolSet(HashSet<String> hashSet) {
        hashSet.add("+");
        hashSet.add("-");
        hashSet.add("*");
        hashSet.add("/");
        hashSet.add("//");
        hashSet.add("<");
        hashSet.add("<=");
        hashSet.add(">");
        hashSet.add(">=");
        hashSet.add("!");
        hashSet.add("=");
        hashSet.add("!=");
        hashSet.add(":=");
        hashSet.add("++");
        hashSet.add("--");
        hashSet.add("'");
        hashSet.add("(");
        hashSet.add(")");
        hashSet.add("{");
        hashSet.add("}");
        hashSet.add(",");
        hashSet.add(";");
        hashSet.add(".");
        hashSet.add(":");
        hashSet.add("\\");
    }
    public static boolean checkInt(String tokenValue) {
        if(tokenValue == null || tokenValue.isEmpty()){
            return false;
        } else {
            for(int i = 0; i < tokenValue.length(); i++){
                if(!Character.isDigit(tokenValue.charAt(i))){
                    return false;
                }
            }
            return true;
        }
    }

    public static boolean checkKeyword(String tokenValue, HashSet<String> keywordSet) {
        if(tokenValue == null || tokenValue.isEmpty()){
            return false;
        } 
        if(keywordSet.contains(tokenValue)) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean checkSymbol(String tokenValue, HashSet<String> symbolSet) {

        if(tokenValue==null || tokenValue.isEmpty()) {
            return false;
        }
        if(symbolSet.contains(tokenValue)) {
            return true;

        } else {
            return false;
        }

    }

    public static boolean checkEndOfText(String tokenValue) {
        if(tokenValue.equals("$")) {
            return true;
        } else {
            return false;
        }

    }


    public static String check(String tokenValue, HashSet<String> keywordSet, HashSet<String> symbolSet) {
        boolean num = checkInt(tokenValue);
        boolean keyword = checkKeyword(tokenValue, keywordSet);
        boolean symbol = checkSymbol(tokenValue, symbolSet);
        if(num) {
            return "NUM";
        } else if (keyword) {
            return tokenValue;
        } else if (symbol) {
            return tokenValue;
        }  else { 
            return "ID";
        }

    }

    public static String fileLoc(Scanner scanner) {
        //Get user input and store it into file loc
        System.out.print("Enter the file location: ");
        String fileLoc = scanner.nextLine();
        return fileLoc;
    }


}