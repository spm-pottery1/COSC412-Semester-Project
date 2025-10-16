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
		return value;
	}

	@Override
	public String toString() {
		if (lexeme == null) {
			return "Lexeme: [NULL], Value: " + String.valueOf(value);
		}
		return "Lexeme: [" + lexeme.toString() + "], Value: " + String.valueOf(value);
	}

	public void setValue(Object v) {
		this.value = v;
	}

	public void setLexeme(MyLexeme lex) {
		this.lexeme = lex;
	}
}