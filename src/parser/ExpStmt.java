package parser;

import errorHandlling.ErrorHandlling;
import lexical.Token;
import lexical.Type;

public class ExpStmt extends Node {
    private int startToken;
    private int endToken;

    public ExpStmt(int startToken, int endToken) throws Exception {
        this.startToken = startToken;
        this.endToken = endToken;
        if (isCorrectSyntax())
            createStmt();
        else
            ErrorHandlling.wrongExpression();
    }

    private boolean isCorrectSyntax() {
        if (!Token.tokens.get(startToken + 1).getDesc().equals("=") || ((endToken - startToken + 1) < 6))
            return false;

        for (int i = startToken + 2; i < endToken - 1; i+=2) {
            if ((Token.tokens.get(i).getType() == Type.Identifier || Token.tokens.get(i).getType() == Type.Number)
                && Token.tokens.get(i + 1).getType() == Type.Operator && !Token.tokens.get(i + 1).getDesc().equals("=")
                && (Token.tokens.get(i + 2).getType() == Type.Identifier || Token.tokens.get(i + 2).getType() == Type.Number));
            else
                return false;
        }

        return true;
    }

    private void createStmt() {
        left = new Node();
        left.lexeme = Token.tokens.get(startToken);
        right = new Node();
        // startToken + 2   ->  first token in expression
        // endToken - 1 ->  last token in expression
        right.exp = Postfix.infixToPostfix(startToken + 2, endToken - 1);
    }

}