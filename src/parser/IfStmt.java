package parser;

import errorHandlling.ErrorHandlling;
import lexical.Token;
import lexical.Type;

public class IfStmt extends Node {
    private int startToken;
    private int endToken;

    public IfStmt(int startToken, int endToken) throws Exception {
        this.startToken = startToken;
        this.endToken = endToken;
        if (isCorrectSyntax())
            createStmt();
        else
            ErrorHandlling.wrongIfStmt();
    }

    private boolean isCorrectSyntax() {
        if (Token.tokens.get(startToken + 1).getType() == Type.OpenParentheses
            && (Token.tokens.get(startToken + 2).getType()== Type.Identifier || Token.tokens.get(startToken + 2).getType() == Type.Number)
            && Token.tokens.get(startToken + 3).getType() == Type.Comparision
            && (Token.tokens.get(startToken + 4).getType()== Type.Identifier || Token.tokens.get(startToken + 4).getType() == Type.Number)
            && Token.tokens.get(startToken + 5).getType() == Type.CloseParentheses)
            return true;
        return false;
    }

    private void createStmt() throws Exception {
        left = new Node();
        left.lexeme = Token.tokens.get(startToken);
        middle = new Node();
        middle.exp = new Token[] {Token.tokens.get(startToken + 2), Token.tokens.get(startToken + 3), Token.tokens.get(startToken + 4)};
        right = new Stmt(startToken + 6, endToken);
    }

}
