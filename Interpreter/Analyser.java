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
        //print(tokens);  
        parse(tokens);
    }

    public static void parse(List<MyToken> tokens) {
        MyToken currentToken = getNextToken(tokens);
        
        currentToken = matchToken(tokens, currentToken, "program");
        currentToken = matchToken(tokens, currentToken, "ID");
        currentToken = matchToken(tokens, currentToken, ":");
        currentToken = body(tokens, currentToken);
        currentToken = matchToken(tokens, currentToken, ".");
        if(currentToken.getLexeme().getKind().equals("END-OF-FILE")) {
            System.out.println("File Successfully Parsed");
        }
    }

    public static MyToken body(List <MyToken> tokens, MyToken currentToken) {
        if(currentToken.getLexeme().getKind().equals("bool") || currentToken.getLexeme().getKind().equals("int")){
            currentToken = declarations(tokens, currentToken);
        }
        currentToken = statements(tokens, currentToken);
        return currentToken;
    }

    public static MyToken statements(List <MyToken> tokens, MyToken currentToken) {
        currentToken = statement(tokens, currentToken);
        while(currentToken.getLexeme().getKind().equals(";")) {

            currentToken = getNextToken(tokens);
            currentToken = statement(tokens, currentToken);

        }
        return currentToken;
    }

    public static MyToken statement(List <MyToken> tokens, MyToken currentToken) { 
        if(currentToken.getLexeme().getKind().equals("ID")) {
            currentToken = assignment(tokens,currentToken);
        } else if(currentToken.getLexeme().getKind().equals("if")) {
            currentToken = conditional(tokens, currentToken);
        } else if(currentToken.getLexeme().getKind().equals("while")) {
            currentToken = iterrative(tokens, currentToken);
        } else if (currentToken.getLexeme().getKind().equals("print")) {
            currentToken = printStatement(tokens, currentToken);
        } else {
            System.out.println("Error: Expected ID, if, while, or print but got: " + currentToken.getLexeme().getKind() + ", at pos:" +currentToken.getLexeme().get_sPos());
            System.exit(-1);
        }
        return currentToken;

    }

    public static MyToken printStatement(List <MyToken> tokens, MyToken currentToken) {
        currentToken = matchToken(tokens, currentToken, "print");
        currentToken = expression(tokens, currentToken);
        return currentToken;
    }

    public static MyToken iterrative(List <MyToken> tokens, MyToken currentToken){
         currentToken = matchToken(tokens, currentToken, "while");
         currentToken = expression(tokens, currentToken);
         currentToken = matchToken(tokens, currentToken, "do");
         currentToken = body(tokens, currentToken);
         if(currentToken.getLexeme().getKind().equals("end")){
            currentToken = matchToken(tokens, currentToken, "end");
         } else {
            System.out.println("Error: Expected one of ID, if, while and print but got: " + currentToken.getLexeme().getKind() + ", at pos:" +currentToken.getLexeme().get_sPos());
            System.exit(-1);
         }

        return currentToken;
    }

    public static MyToken conditional(List<MyToken> tokens, MyToken currentToken) {
        currentToken = matchToken(tokens, currentToken, "if");
        currentToken = expression(tokens, currentToken);
        currentToken = matchToken(tokens, currentToken, "then");
        currentToken = body(tokens, currentToken);

        if(currentToken.getLexeme().getKind().equals("else")) {
            currentToken = getNextToken(tokens);
            currentToken = body(tokens, currentToken);
        }
        currentToken = matchToken(tokens, currentToken, "end");
        return currentToken;
    }

    public static MyToken assignment(List <MyToken> tokens, MyToken currentToken) {
        currentToken = matchToken(tokens, currentToken, "ID");
        currentToken = matchToken(tokens, currentToken, ":=");
        currentToken = expression(tokens, currentToken);
        return currentToken;
    }

    public static MyToken expression(List <MyToken> tokens, MyToken currentToken) {
        currentToken = simpleExpression(tokens, currentToken);
        if(currentToken.getLexeme().getKind().equals("<")|| currentToken.getLexeme().getKind().equals("<=")|| currentToken.getLexeme().getKind().equals("=")|| currentToken.getLexeme().getKind().equals("!")|| currentToken.getLexeme().getKind().equals("!=")|| currentToken.getLexeme().getKind().equals(">")|| currentToken.getLexeme().getKind().equals(">=")) {
            currentToken = getNextToken(tokens);
            currentToken = simpleExpression(tokens, currentToken);
        }

        return currentToken;
    }

    public static MyToken simpleExpression(List <MyToken> tokens, MyToken currentToken) {
    currentToken = term(tokens, currentToken);
    while(currentToken.getLexeme().getKind().equals("+") || currentToken.getLexeme().getKind().equals("-") || currentToken.getLexeme().getKind().equals("or")) {
        currentToken = getNextToken(tokens); // Consume the operator
        currentToken = term(tokens, currentToken); // Match the next term
    }
    return currentToken;
}

    public static MyToken term(List <MyToken> tokens, MyToken currentToken) {
    currentToken = factor(tokens, currentToken); // Match the first Factor
    
    // Handle zero or more operators followed by Factors
    while(currentToken.getLexeme().getKind().equals("*") || currentToken.getLexeme().getKind().equals("/") || currentToken.getLexeme().getKind().equals("and") || currentToken.getLexeme().getKind().equals("mod")) {
        currentToken = getNextToken(tokens); // Consume the operator (*, /, or and)
        currentToken = factor(tokens, currentToken); // Match the next Factor
    }
    return currentToken; // Returns the token *after* the term (e.g., '+', ';', etc.)
}

    public static MyToken factor(List <MyToken> tokens, MyToken currentToken) {
    String kind = currentToken.getLexeme().getKind();
    if(kind.equals("-") || kind.equals("not")) {
        currentToken = getNextToken(tokens);
        kind = currentToken.getLexeme().getKind();
    }
    if (kind.equals("NUM") || kind.equals("true") || kind.equals("false")) {
        currentToken = literal(tokens, currentToken);
    } else if (kind.equals("ID")) {
        currentToken = getNextToken(tokens);
    } else if (kind.equals("(")) {
        currentToken = getNextToken(tokens);
        currentToken = expression(tokens, currentToken);
        currentToken = getNextToken(tokens);
        currentToken = matchToken(tokens, currentToken, ")");
    } else {
        System.out.println("Error: Expected NUM, true, false, but got: " + currentToken.getLexeme().getKind());
        System.exit(-1);
    }
    return currentToken;
}

    public static MyToken literal(List <MyToken> tokens, MyToken currentToken) {
    String kind = currentToken.getLexeme().getKind();
    switch (kind) {
        case "NUM":
            currentToken = getNextToken(tokens); // Just consume the NUM token
            break;
        case "true":
        case "false":
            currentToken = booleanLiteral(tokens, currentToken);
            break;
        default:
            System.out.println("Error: Expected NUM, true, false, but got: " + currentToken.getLexeme().getKind());
            System.exit(-1);
    }
    return currentToken;
}

    public static MyToken booleanLiteral(List <MyToken> tokens, MyToken currentToken) {
    if(currentToken.getLexeme().getKind().equals("true")) {
        currentToken = matchToken(tokens, currentToken, "true"); 
    } else if(currentToken.getLexeme().getKind().equals("false")){
        currentToken = matchToken(tokens, currentToken, "false");
    }
    return currentToken;
}

    public static void expectedKind(MyToken currentToken) {
        System.out.println("Error at " + currentIndex + ".");
    }

    public static MyToken declarations(List <MyToken> tokens, MyToken currentToken) {
        while(currentToken.getLexeme().getKind().equals("bool") || currentToken.getLexeme().getKind().equals("int")) {
            currentToken=declaration(tokens,currentToken);
        }
        return currentToken;
    }

    public static MyToken declaration(List <MyToken> tokens, MyToken currentToken) {
        currentToken = getNextToken(tokens);
        currentToken = matchToken(tokens, currentToken, "ID");
        while(currentToken.getLexeme().getKind().equals(",")) {
            currentToken = matchToken(tokens, currentToken, ",");
            currentToken = matchToken(tokens, currentToken, "ID");
        }
        currentToken = matchToken(tokens, currentToken, ";");
        return currentToken;
    }

    public static MyToken matchToken(List <MyToken> tokens, MyToken currentToken, String expectedKind) {
        if(currentToken.getLexeme().getKind().equals(expectedKind)) {
            currentToken=getNextToken(tokens);
            //System.out.println("DEBUG CURRENT RETURN TOKEN matchToken" + currentToken);
            return currentToken;
        } else {
            System.out.println("Error: Expected " + expectedKind + ", but got: " + currentToken.getLexeme().getKind() + ", at pos:" +currentToken.getLexeme().get_sPos());
            System.exit(-1);
        }
        return currentToken;
    }
    public static MyToken getNextToken(List<MyToken> tokens) {
        if (currentIndex < tokens.size()) {
            //System.out.println("DEBUG CURRENT RETURN TOKEN getNextToken" + tokens.get(currentIndex));
            return tokens.get(currentIndex++); 
        } else {
            System.exit(-1);
            return null;
        }
    }








// Last Project


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
        hashSet.add("mod");
        hashSet.add("and");
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