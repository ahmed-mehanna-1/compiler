package parser;

import lexical.Token;

import java.util.ArrayList;

public class Node {
    protected Token lexeme;
    protected Token[] exp;
    protected Node left;
    protected Node middle;
    protected Node right;

    public Node getRight() {
        return right;
    }

    public Node getMiddle() {
        return middle;
    }

    public Node getLeft() {
        return left;
    }

    public Token getLexeme() {
        return lexeme;
    }

    public Token[] getExp() {
        return exp;
    }
}
