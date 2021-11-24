package exception;

public class SyntaxError extends Exception{
    public SyntaxError(String message,int line)
    {
        super(message+"(line:"+line+")");
    }
}