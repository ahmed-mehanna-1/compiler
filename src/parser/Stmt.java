package parser;

import errorHandlling.ErrorHandlling;
import lexical.Token;
import lexical.Type;

public class Stmt extends Node {
    private int startToken;
    private int endToken;

    public Stmt(int startToken, int endToken) throws Exception {
        this.startToken = startToken;
        this.endToken = endToken;
        whichStmt();
    }

    private void whichStmt() throws Exception {
        if (Token.tokens.get(startToken).getType() == Type.Keyword)
            middle = new IfStmt(startToken, endToken);
        else if (Token.tokens.get(startToken).getType() == Type.Identifier && (endToken - startToken + 1) == 4)
            middle = new AssStmt(startToken, endToken);
        else if (Token.tokens.get(startToken).getType() == Type.Identifier)
            middle = new ExpStmt(startToken, endToken);
        else
            ErrorHandlling.cannotDetectStmt();
    }

}
