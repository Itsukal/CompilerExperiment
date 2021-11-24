package inter;

import symbols.Type;

public class For extends Stmt {

    Stmt before;
    Stmt behind;
    Expr expr;
    Stmt stmt;

    public For() {
        before = null;
        behind = null;
        expr = null;
        stmt = null;
    }

    public void init(Stmt before,Expr x,Stmt behind, Stmt s) {
        this.before=before;
        expr = x;
        this.behind=behind;
        stmt = s;
        if (expr.type != Type.Bool)
            expr.error("boolean required in while");
    }

    public void gen(int b, int a) {
    }

    public void display() {
        emit("stmt : for begin");
        stmt.display();
        emit("stmt : for end");
    }
}
