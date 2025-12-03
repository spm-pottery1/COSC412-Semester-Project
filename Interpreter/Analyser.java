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
        
        // The parse method now returns the root of the AST
        ASTNode root = parse(tokens); 
        
        System.out.println("\n--- Abstract Syntax Tree ---");
        System.out.println(root.toTreeString(""));
    }

    public static ASTNode parse(List<MyToken> tokens) {
        MyToken currentToken = getNextToken(tokens);
        
        currentToken = matchToken(tokens, currentToken, "program");
        String programId = currentToken.getValue();
        currentToken = getNextToken(tokens); // Consume ID
        currentToken = matchToken(tokens, currentToken, ":");
        
        ASTNode bodyNode = body(tokens, currentToken);
        
        currentToken = matchToken(tokens, currentToken, ".");
        
        if(currentToken.getLexeme().getKind().equals("END-OF-FILE")) {
            System.out.println("File Successfully Parsed");
        }

        return new ProgramNode(programId, bodyNode);
    }

    public static ASTNode body(List <MyToken> tokens, MyToken currentToken) {
        ASTNode declarationsNode = new NoOpNode(); // Default to NOOP
        if(currentToken.getLexeme().getKind().equals("bool") || currentToken.getLexeme().getKind().equals("int")){
            MyToken startToken = currentToken;
            currentToken = declarations(tokens, currentToken);
            // In a full implementation, declarationsNode would be built here.
            // For now, it remains NOOP as per the previous simple structure.
            declarationsNode = new NoOpNode(); 
        }
        
        ASTNode statementsNode = statements(tokens, currentToken);
        
        return new BodyNode(declarationsNode, statementsNode);
    }

    public static ASTNode statements(List <MyToken> tokens, MyToken currentToken) {
        ASTNode firstStatement = statement(tokens, currentToken);
        StatementsNode statementsNode = new StatementsNode(firstStatement);

        while(currentToken.getLexeme().getKind().equals(";")) {
            currentToken = getNextToken(tokens); // Consume ';'
            ASTNode nextStatement = statement(tokens, currentToken);
            statementsNode.addStatement(nextStatement);
        }
        return statementsNode;
    }

    public static ASTNode statement(List <MyToken> tokens, MyToken currentToken) { 
        ASTNode node = null;
        if(currentToken.getLexeme().getKind().equals("ID")) {
            node = assignment(tokens,currentToken);
        } else if(currentToken.getLexeme().getKind().equals("if")) {
            node = conditional(tokens, currentToken);
        } else if(currentToken.getLexeme().getKind().equals("while")) {
            node = iterrative(tokens, currentToken);
        } else if (currentToken.getLexeme().getKind().equals("print")) {
            node = printStatement(tokens, currentToken);
        } else {
            System.out.println("Error: Expected ID, if, while, or print but got: " + currentToken.getLexeme().getKind() + ", at pos:" +currentToken.getLexeme().get_sPos());
            System.exit(-1);
        }
        return node;
    }

    /** Implements AST Generation for printStatement **/
    public static ASTNode printStatement(List <MyToken> tokens, MyToken currentToken) {
        currentToken = matchToken(tokens, currentToken, "print"); // Consume 'print'
        
        ASTNode expressionNode = expression(tokens, currentToken);
        
        // Assuming the expression results in a BOOL type
        return new PrintNode(expressionNode, "BOOL"); 
    }

    public static ASTNode iterrative(List <MyToken> tokens, MyToken currentToken){
         currentToken = matchToken(tokens, currentToken, "while");
         ASTNode conditionNode = expression(tokens, currentToken);
         currentToken = matchToken(tokens, currentToken, "do");
         ASTNode bodyNode = body(tokens, currentToken);
         if(currentToken.getLexeme().getKind().equals("end")){
            currentToken = matchToken(tokens, currentToken, "end");
         } else {
            System.out.println("Error: Expected 'end' but got: " + currentToken.getLexeme().getKind() + ", at pos:" +currentToken.getLexeme().get_sPos());
            System.exit(-1);
         }

        return new NoOpNode(); 
    }

    /** Implements AST Generation for conditional (if-then-else) **/
    public static ASTNode conditional(List<MyToken> tokens, MyToken currentToken) {
        currentToken = matchToken(tokens, currentToken, "if"); // Consume 'if'
        ASTNode conditionNode = expression(tokens, currentToken);
        
        currentToken = matchToken(tokens, currentToken, "then"); // Consume 'then'
        ASTNode thenBranchNode = body(tokens, currentToken);
        
        ASTNode elseBranchNode = new NoOpNode(); // Default to NOOP
        
        if(currentToken.getLexeme().getKind().equals("else")) {
            currentToken = getNextToken(tokens); // Consume 'else'
            elseBranchNode = body(tokens, currentToken);
        }
        
        currentToken = matchToken(tokens, currentToken, "end"); // Consume 'end'
        
        return new IfNode(conditionNode, thenBranchNode, elseBranchNode);
    }

    public static ASTNode assignment(List <MyToken> tokens, MyToken currentToken) {
        String id = currentToken.getValue();
        currentToken = matchToken(tokens, currentToken, "ID"); // Consume ID
        currentToken = matchToken(tokens, currentToken, ":="); // Consume ':='
        ASTNode expressionNode = expression(tokens, currentToken);
        return new AssignNode(id, expressionNode);
    }

    public static ASTNode expression(List <MyToken> tokens, MyToken currentToken) {
        ASTNode left = simpleExpression(tokens, currentToken);
        String op = currentToken.getLexeme().getKind();
        
        if(op.equals("<")|| op.equals("<=")|| op.equals("=")|| op.equals("!")|| op.equals("!=")|| op.equals(">")|| op.equals(">=")) {
            currentToken = getNextToken(tokens); // Consume operator
            ASTNode right = simpleExpression(tokens, currentToken);
            return new BinaryOpNode(op.toUpperCase(), left, right); // Example: LT, GT, EQ
        }

        return left;
    }

    public static ASTNode simpleExpression(List <MyToken> tokens, MyToken currentToken) {
    ASTNode left = term(tokens, currentToken);
    String op = currentToken.getLexeme().getKind();
    
    while(op.equals("+") || op.equals("-") || op.equals("or")) {
        currentToken = getNextToken(tokens); // Consume the operator
        ASTNode right = term(tokens, currentToken); // Match the next term
        left = new BinaryOpNode(op.toUpperCase(), left, right); // Example: ADD, SUB, OR
        op = currentToken.getLexeme().getKind();
    }
    return left;
}

    public static ASTNode term(List <MyToken> tokens, MyToken currentToken) {
    ASTNode left = factor(tokens, currentToken); // Match the first Factor
    String op = currentToken.getLexeme().getKind();
    
    // Handle zero or more operators followed by Factors
    while(op.equals("*") || op.equals("/") || op.equals("and") || op.equals("mod")) {
        currentToken = getNextToken(tokens); // Consume the operator (*, /, or and)
        ASTNode right = factor(tokens, currentToken); // Match the next Factor
        left = new BinaryOpNode(op.toUpperCase(), left, right); // Example: MUL, DIV, AND, MOD
        op = currentToken.getLexeme().getKind();
    }
    return left; // Returns the AST node for the term
}

    public static ASTNode factor(List <MyToken> tokens, MyToken currentToken) {
    String kind = currentToken.getLexeme().getKind();
    String op = null;
    if(kind.equals("-") || kind.equals("not")) {
        op = kind.toUpperCase();
        currentToken = getNextToken(tokens);
        kind = currentToken.getLexeme().getKind();
    }
    
    ASTNode node;
    if (kind.equals("NUM") || kind.equals("true") || kind.equals("false")) {
        node = literal(tokens, currentToken);
    } else if (kind.equals("ID")) {
        node = new IdNode(currentToken.getValue());
        currentToken = getNextToken(tokens); // Consume ID
    } else if (kind.equals("(")) {
        currentToken = getNextToken(tokens); // Consume '('
        node = expression(tokens, currentToken);
        currentToken = getNextToken(tokens);
        currentToken = matchToken(tokens, currentToken, ")"); // Consume ')'
    } else {
        System.out.println("Error: Expected NUM, true, false, ID, or ( but got: " + currentToken.getLexeme().getKind());
        System.exit(-1);
        node = null; 
    }

    if (op != null) {
        return new UnaryOpNode(op, node);
    }
    return node;
}

    public static ASTNode literal(List <MyToken> tokens, MyToken currentToken) {
    ASTNode node = null;
    String kind = currentToken.getLexeme().getKind();
    switch (kind) {
        case "NUM":
            node = new IntLiteralNode(currentToken.getValue());
            currentToken = getNextToken(tokens); // Consume the NUM token
            break;
        case "true":
        case "false":
            node = booleanLiteral(tokens, currentToken);
            break;
        default:
            System.out.println("Error: Expected NUM, true, false, but got: " + currentToken.getLexeme().getKind());
            System.exit(-1);
    }
    return node;
}

    public static ASTNode booleanLiteral(List <MyToken> tokens, MyToken currentToken) {
        String value = currentToken.getValue();
        if(currentToken.getLexeme().getKind().equals("true")) {
            currentToken = matchToken(tokens, currentToken, "true"); 
        } else if(currentToken.getLexeme().getKind().equals("false")){
            currentToken = matchToken(tokens, currentToken, "false");
        }
        return new BoolLiteralNode(value);
    }

    // --- Utility Methods (Token & Lexing) ---

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
            // Token matches, but don't consume it here. 
            return currentToken;
        } else {
            System.out.println("Error: Expected " + expectedKind + ", but got: " + currentToken.getLexeme().getKind() + ", at pos:" +currentToken.getLexeme().get_sPos());
            System.exit(-1);
        }
        return currentToken; // Should be unreachable due to exit
    }
    
    public static MyToken getNextToken(List<MyToken> tokens) {
        if (currentIndex < tokens.size()) {
            return tokens.get(currentIndex++); 
        } else {
            if (currentIndex == tokens.size()) {
                 return tokens.get(currentIndex - 1);
            }
            System.exit(-1);
            return null;
        }
    }

    public static List<MyToken> createTokens(List<String> tokenStrings, List<String> cTokenStrings, Tuple[] positions, HashSet<String> keywordSet, HashSet<String> symbolSet) {
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
                        if (tokenValue.length() > 0) {
                        tokenStrings.add(tokenValue.toString());
                        tokenValue.setLength(0);
                        }
                    break; 
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