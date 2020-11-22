package parser;

import lexical.Token;
import lexical.Type;

import java.util.Stack;

public class Postfix {
    private static int Prec(Token ch) {
        switch (ch.getDesc()) {
            case "+":
            case "-":
                return 1;

            case "*":
            case "/":
                return 2;
        }
        return -1;
    }

    public static Token[] infixToPostfix(int startToken, int endToken) {
        Stack<Token> stack = new Stack<>();
        Token[] result = new Token[endToken - startToken + 1];
        int j = 0;
        for (int i = startToken; i <= endToken; ++i) {
            Token token = Token.tokens.get(i);
            if (token.getType() == Type.Number || token.getType() == Type.Identifier)
                result[j++] = token;
            else {
                while (!stack.isEmpty() && Prec(token) <= Prec(stack.peek()))
                    result[j++] = stack.pop();
                stack.push(token);
            }
        }

        while (!stack.isEmpty())
            result[j++] = stack.pop();

        return result;
    }
}
