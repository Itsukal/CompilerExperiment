package main;

import java.io.IOException;

import parser.Parser;
import lexer.Lexer;

public class Main {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		//词法分析器
		Lexer lex = new Lexer();
		//语法分析器
		Parser parser = new Parser(lex);
		parser.program();
		System.out.print("\n");
	}

}
