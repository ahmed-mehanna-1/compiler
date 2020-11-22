package parser;

import errorHandlling.ErrorHandlling;
import lexical.Token;
import lexical.Type;

public class CmpStmt extends Node {

    public CmpStmt(int startToken) throws Exception {
        int i = startToken;
        while ( i < Token.tokens.size() && Token.tokens.get(i).getType() != Type.End)
            i++;
        if (i == Token.tokens.size())
            ErrorHandlling.missedSemicolon();
        else if (Token.tokens.size() - 1 == i)
            left = new Stmt(startToken, i);
        else if (i + 1 < Token.tokens.size()){
            left = new Stmt(startToken, i);
            right = new CmpStmt(++i);
        }
    }

}