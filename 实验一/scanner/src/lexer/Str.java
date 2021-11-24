package lexer;

public class Str extends Token {
	public String lexeme = "";
    
    public Str(String v) {
		super(Tag.STR);
		lexeme = v;
	}
	
    public String toString() {
		return lexeme;
	}
}
