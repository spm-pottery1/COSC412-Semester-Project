/*
 * Each token should include the following information:

 1. The position of the lexeme. The position is a pair consisting of the line number of the lexeme
and the position of the first character of the lexeme in that line.



2. The kind of the lexeme. To keep things simple, use strings for representing the kind of
lexemes.

We have five different lexemes. The following is the kinds of the lexemes:
    (a) For identifiers the kind is “ID”. For example, for identifier ’speed’, the kind is “ID”.
    (b) For integers (i.e., numbers), the kind is “NUM”. For example, for the integer 3400, the kind is “NUM”.
    (c) For keywords, the keyword itself is the kind. For example, for the keyword ’false’, the kind is “false”.
    (d) For other symbols, the kind is a string corresponding to the symbol. For example, for the symbol ’:=’ the kind is “:=”.
    (e) There is a special kind “end-of-text”. Upon encountering the end of the input file, the scanner must generate a token whose kind is “end-of- text”. 
 */
public class MyToken {

     private MyLexeme lexeme;
     private Object value;

     public MyToken(MyLexeme lexeme, Object v) {
         this.lexeme = lexeme;
         this.value = v;
     }
      public MyToken(MyLexeme lexeme) {
         this.lexeme = lexeme;
         this.value = null;
     }
     public MyToken() {
         this.lexeme = null;
         this.value = null;
     }
     public MyLexeme getLexeme() {
         return lexeme;
     }
     public Object getValue() {
            if(lexeme.getKind().equals("NUM")) {
                return value;
            } else if(lexeme.getKind().equals("ID")) {
                return value;
            } else if (value != null) {
                return value;
            } else {
                return null;
            }
        }
    public String toString() {
            return "Lexeme: [" + lexeme.toString() + "], Value: " + getValue();
        }   

    public void setValue(Object v) {
        value = v;
    }
    public void setLexeme(MyLexeme lex) {
        lexeme = lex;
    }
}



