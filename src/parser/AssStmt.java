package parser;

import errorHandlling.ErrorHandlling;
import lexical.Token;
import lexical.Type;

public class AssStmt extends Node {
    private int startToken;
    private int endToken;

    public AssStmt(int startToken, int endToken) throws Exception {
        this.startToken = startToken;
        this.endToken = endToken;
        if (isCorrectSyntax())
            createStmt();
        else
            ErrorHandlling.wrongExpression();
    }

    private boolean isCorrectSyntax() {
        if (Token.tokens.get(startToken + 1).getDesc().equals("=")
            && (Token.tokens.get(startToken + 2).getType() == Type.Identifier || Token.tokens.get(startToken + 2).getType() == Type.Number ))
            return true;
        return false;
    }

    private void createStmt() {
        left = new Node();
        left.lexeme = Token.tokens.get(startToken);
        middle = new Node();
        middle.lexeme = Token.tokens.get(startToken + 1);
        right = new Node();
        right.lexeme = Token.tokens.get(startToken + 2);
    }
}