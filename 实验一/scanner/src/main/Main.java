package main;

import java.io.IOException;
import java.util.Hashtable;
import exception.SyntaxError;

/*f
h*/
import lexer.Lexer;
import lexer.Token;

public class Main {
	public static void main(String[] args) throws IOException{
		// String str="FFFF";
		// long ans=Long.parseLong(str,16);
		// System.out.println(ans);
		Lexer lexer = new Lexer();
		char c;
		do {
			Token token;
			try{
				token=lexer.scan();
			}catch(SyntaxError e)
			{
				while(lexer.getPeek()!='\n')
				{
					lexer.readch();
				}
				lexer.setPeek(' ');
				//输出错误信息
				System.out.println(e.getMessage());
				continue;
			}
			switch (token.tag) {
			case 270:
			case 272:
				System.out.println("(NUM , "+token.toString()+")");
				break;
			case 264:
				System.out.println("(ID , "+token.toString()+")");
				break;
			case 256:
			case 257:
			case 258:
			case 259:

			case 260:
			case 265:
			case 274:
			case 276:
			case 278:
			case 279:
			case 280:
			case 281:
			case 282:
			case 283:
			case 284:
			case 285:
			case 275:
				System.out.println("(KEY , "+token.toString()+")");
				break;
			case 10:
				break;
			case 13:
				break;
			case 277:
				System.out.println("(STR, "+token.toString()+")");
				break;
			default:
				System.out.println("(SYM , "+token.toString()+")");
				break;
			}

		} while (lexer.getPeek()!='\n');
	}
}





