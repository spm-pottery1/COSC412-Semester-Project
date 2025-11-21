
public class MyLexeme {

    /* requirements:
     * - type needs to be able to differentiate between
     * - NUM,ID,KEYWORD,OPERATOR,DELIMITER
     */

    public String type;
    public Tuple sPos;
 

    public MyLexeme(String type, int lineNum, int charNum){
        this.type = type;
        this.sPos = new Tuple(lineNum, charNum);
    }
    public MyLexeme(String type, Tuple tuple){
        this.type = type;
        this.sPos = tuple;
    }
    public MyLexeme(){
        this.type = null;
        this.sPos = null;
    }
    public String getKind() {
        return this.type;
    }
    public Tuple get_sPos() {
        return this.sPos;
    }
    public String toString() {
        return "Type: " + this.type + ", Start Position: " + this.sPos.toString();
    }        
}
