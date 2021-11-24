package lexer;

import exception.SyntaxError;

import java.io.IOException;
import java.util.Hashtable;

import javax.sql.rowset.spi.SyncFactoryException;

public class Lexer {

	public static int line = 1;
	char peek = ' ';
	Hashtable words = new Hashtable();

	void reserve(Word w) {
		words.put(w.lexeme, w);
	}

	public Lexer() {
		// 推入hash表中
		reserve(new Word("if", Tag.IF));
		reserve(new Word("else", Tag.ELSE));
		reserve(new Word("while", Tag.WHILE));
		reserve(new Word("do", Tag.DO));
		reserve(new Word("break", Tag.BREAK));
		reserve(new Word("int", Tag.INT));
		reserve(new Word("void", Tag.VOID));
		reserve(new Word("double", Tag.DOUBLE));
		reserve(new Word("bool", Tag.BOOL));
		reserve(new Word("class", Tag.CLASS));
		reserve(new Word("for", Tag.FOR));
		reserve(new Word("static",Tag.STATIC));
		reserve(new Word("new",Tag.NEW));
		reserve(new Word("return",Tag.RETURN));
		reserve(Word.True);
		reserve(Word.False);
	}

	public void readch() throws IOException {
		peek = (char) System.in.read();

	}

	// 判断是否是
	boolean readch(char c) throws IOException {
		readch();
		if (peek != c) {
			return false;
		}
		peek = ' ';
		return true;
	}

	public Token scan() throws IOException, SyntaxError {
		// 一个一个字符读进来
		for (;; readch()) {
			if (peek == ' ' || peek == '\t')
				continue;
			else if (peek == '\n') {
				line += 1;
			} else {
				break;
			}
		}

		// 清除注释
		if (peek == '/') {
			readch();
			Boolean tempTag = true;// 判断是单行注释还是多行注释

			if (peek == '/')
				tempTag = true;
			else if (peek == '*')
				tempTag = false;

			// 清除单行注释
			if (tempTag) {
				readch();
				for (;; readch()) {
					if (peek == ' ' || peek == '\t')
						continue;
					else if (peek == '\n') {
						line += 1;
						tempTag = false;
						break;
					} else {
						continue;
					}
				}
			}

			// 清除多行注释
			if (!tempTag) {
				readch();
				for (;; readch()) {
					if (peek == '*') {
						readch();
						if (peek == '/') {
							readch();
							break;
						}
					}
					if (peek == ' ' || peek == '\t')
						continue;
					else if (peek == '\n') {
						line += 1;
						continue;
					} else {	
						continue;
					}
				}
			}
		}

		switch (peek) {
		case '&':
			if (readch('&'))
				return Word.and;
			else
				return new Token('&');
		case '|':
			if (readch('|'))
				return Word.or;
			else
				return new Token('|');
		case '=':
			if (readch('='))
				return Word.eq;
			else
				return new Token('=');
		case '!':
			if (readch('='))
				return Word.ne;
			else
				return new Token('!');
		case '<':
			if (readch('='))
				return Word.le;
			else
				return new Token('<');
		case '>':
			if (readch('='))
				return Word.ge;
			else
				return new Token('>');
		case '[':
			readch();
			return new Token('[');
		case ']':
			readch();
			return new Token(']');
		case '(':
			readch();
			return new Token('(');
		case ')':
			readch();
			return new Token(')');
		case '{':
			readch();
			return new Token('{');
		case '}':
			readch();
			return new Token('}');
		}

		// 识别整数小数
		if (Character.isDigit(peek)) {
			int v = 0;
			if (peek == '0') {
				readch();
				// 是十六进制整数
				if (peek == 'x' || peek == 'X') {
					readch();// 读'x'后的一个
					if (!(Character.isDigit(peek)||peek>='A'&&peek<='F'||peek>='a'&&peek<='f'))
						throw new SyntaxError("缺少十六进制数", line);
					String tempString = "";
					// 识别十六进制整数
					do {
						tempString += peek;
						readch();
					} while ((Character.isDigit(peek)||peek>='A'&&peek<='F'||peek>='a'&&peek<='f'));
					int ans = (int) Long.parseLong(tempString, 16);// 转成十进制
					return new Num(ans);
				}
			}
			// 现在处理十进制整数以及小数这个集合
			// 该集合有三种可能，一个为0
			// 一个为1.xxx这样的首位为非0
			// 一个为0.xxx

			// 为0且0已被消耗
			if (!Character.isDigit(peek) && peek != '.') {
				return new Num(0);
			}

			// 1.xxx
			if (Character.isDigit(peek)) {
				do {
					v = 10 * v + Character.digit(peek, 10);
					readch();
				} while (Character.isDigit(peek));
				// 确认是整数
				if (peek != '.')
					return new Num(v);
			}

			// 小数点后的小数识别
			float x = v;
			float d = 10;
			// 确认是小数
			for (;;) {
				readch();
				if (!Character.isDigit(peek))
					break;
				x = x + Character.digit(peek, 10) / d;
				d = d * 10;
			}

			// 科学计数法识别
			if (peek == 'e' || peek == 'E') {
				readch();
				if (peek != '+' && peek != '-') {
					throw new SyntaxError("指数部分需要指定正负号", line);
				}
				int sign = peek == '-' ? -1 : 1;
				readch();
				if (!Character.isDigit(peek)) {
					throw new SyntaxError("缺少指数数值", 32);
				}
				int exp = 0;
				do {
					exp = 10 * exp + Character.digit(peek, 10);
					readch();
				} while (Character.isDigit(peek));
				x = x * (float) Math.pow(10, sign * exp);
			}

			return new Real(x);
		}

		// 判断是否为字符串常量
		if (peek == '"') {
			readch();
			StringBuffer b = new StringBuffer();
			do {
				if (peek == '"') {
					readch();
					String s = b.toString();
					Str string = new Str(s);
					return string;
				}
				if (peek == '\n') {
					throw new SyntaxError("字符串不能有换行符", line);
				}
				b.append(peek);
				readch();
			} while (true);
		}

		// 判断标识符
		if (Character.isLetter(peek)) {
			StringBuffer b = new StringBuffer();
			do {
				b.append(peek);
				readch();
			} while (Character.isLetterOrDigit(peek) || peek == '_');
			// 转成字符串
			String s = b.toString();

			// 判断是否为hash表中的关键字
			Word w = (Word) words.get(s);

			// 如果是hash表中的关键字
			if (w != null)
				return w;

			// 为一般标识符
			w = new Word(s, Tag.ID);
			words.put(s, w);
			return w;
		}

		// peek中的任意字符都被作为词法单元返回
		Token tok = new Token(peek);
		peek = ' ';
		return tok;
	}

	public void out() {
		System.out.println(words.size());

	}

	public char getPeek() {
		return peek;
	}

	public void setPeek(char peek) {
		this.peek = peek;
	}

}
