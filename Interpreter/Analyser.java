import java.util.*;
import java.io.File;
import java.io.FileNotFoundException;
public class Analyser {
     public static void main(String[] args) {
         Scanner scanner = new Scanner(System.in);
         HashSet<String> keywordSet = new HashSet<>();
         HashSet<String> symbolSet = new HashSet<>();
         HashSet<Character> charSet = new HashSet<>();
         initLists(keywordSet,symbolSet,charSet);
         File inputFile = new File("C:\\Users\\spm51\\OneDrive\\Desktop\\Projects\\Interpreter\\input.txt");
         
         HashMap<Integer, String> fileContent = nextFromFile(inputFile);
         
         List<String> tokenStrings = getTokenStrings(fileContent, symbolSet);
         
         List<String> cTokenStrings = cTokenStrings(tokenStrings, keywordSet, symbolSet);
         
         Tuple[] positions = getTokenPositions(fileContent, tokenStrings);
         
         createAndPrintTokens(tokenStrings, cTokenStrings, positions, keywordSet, symbolSet);
     }
    
     private static void createAndPrintTokens(List<String> tokenStrings, List<String> cTokenStrings, Tuple[] positions, HashSet<String> keywordSet, HashSet<String> symbolSet) {
         List<MyToken> tokenSet = new ArrayList<>(tokenStrings.size());
         
         for (int i = 0; i < tokenStrings.size(); i++) {
             String token = tokenStrings.get(i);
             String cToken = cTokenStrings.get(i);
             if (i >= positions.length) {
                 System.out.println("Error: Mismatch between token count and position count.");
                 break; 
             }
             Tuple position = positions[i];
             tokenSet.add(new MyToken(new MyLexeme(cToken, position), token));
         }
         tokenSet.add(new MyToken(new MyLexeme("END-OF-TEXT", new Tuple(positions[positions.length - 1].getX(), positions[positions.length - 1].getY() + 1)), null));
         print(tokenSet);
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

    /*  private static void testStringLists(List<String> tokenStrings, List<String> cTokenStrings) {
         for(String ts: tokenStrings) {
             System.out.println("Token String: " + ts);
         }
         for(String cts: cTokenStrings) {
             System.out.println("Checked Type Token String: " + cts);
         }
     } */

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

         System.out.println("DEBUG: KEYOWORD SET CREATED");
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
         hashSet.add("++");
         hashSet.add("--");
         hashSet.add("'");
         hashSet.add("(");
         hashSet.add(")");
         hashSet.add("{");
         hashSet.add("}");


         System.out.println("DEBUG: SYMBOL SET CREATED");
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
             System.out.println("Symbol Checked");
             return true;

         } else {
             return false;
         }

     }

     public static boolean checkEndOfText(String tokenValue) {
         if(tokenValue == null || tokenValue.isEmpty()) {
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

}