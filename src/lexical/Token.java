package lexical;

import errorHandlling.ErrorHandlling;

import java.util.ArrayList;

public class Token {
    public static ArrayList<Token> tokens = new ArrayList<>();
    private String desc;
    private Type type;

    public Token(String desc, boolean fromIntermediateCode) throws Exception {
        this.desc = desc;
        if (in(desc, Basic.keywords))
            this.type = Type.Keyword;
        else if (in(desc, Basic.comparision))
            this.type = Type.Comparision;
        else if (Basic.delimiter.containsKey(desc))
            this.type = Basic.delimiter.get(desc);
        else if (in(desc, Basic.operator))
            this.type = Type.Operator;
        else if (isDigit(desc))
            this.type = Type.Number;
        else if (isIdentifier(desc, fromIntermediateCode))
            this.type = Type.Identifier;
        else
            ErrorHandlling.wrongIdentifier(this);
    }

    private static boolean in(String desc, String[] array) {
        for (int i = 0; i < array.length; i++)
            if (array[i].equals(desc))
                return true;
        return false;
    }

    private static boolean isDigit(String number) {
        try {
            Integer.parseInt(number);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    private boolean isIdentifier(String desc, boolean fromIntermediateCode) {
        if (fromIntermediateCode)
            return true;
        if (isDigit(Character.toString(desc.charAt(0))))
            return false;
        if (desc.contains("temp"))
            if (desc.split("temp").length >= 2 && desc.split("temp")[0].equals("") && isDigit(desc.split("temp")[1]))
                return false;
        for (int i = 1; i < desc.length(); i++)
            if (in(Character.toString(desc.charAt(i)), Basic.operator) || Basic.delimiter.containsKey(Character.toString(desc.charAt(i))) || in(Character.toString(desc.charAt(i)), Basic.error))
                return false;
        return true;
    }

    public static void createTokens (String sourceCode) throws Exception {
        sourceCode = deleteComments(sourceCode);
        String code = deleteSpaces(sourceCode);
        Basic.initDelimiters();
        String id = "";
        for (int i = 0; i < code.length(); i++) {
            if (in(Character.toString(code.charAt(i)), Basic.operator)
                    || Basic.delimiter.containsKey(Character.toString(code.charAt(i)))
                    || in(Character.toString(code.charAt(i)), Basic.comparision)) {
                if (!id.equals(""))
                    Token.tokens.add(new Token(id, false));
                if (in(Token.tokens.get(Token.tokens.size() - 1).getDesc() + code.charAt(i), Basic.comparision))
                    Token.tokens.get(Token.tokens.size() - 1).setDesc(Token.tokens.get(Token.tokens.size() - 1).getDesc()+code.charAt(i));
                else
                    Token.tokens.add(new Token(Character.toString(code.charAt(i)), false));
                id = "";
            }
            else
                id += code.charAt(i);
        }
    }

    private static int countHsh(String sourceCode) {
        int count = 0;
        for (int i = 0; i < sourceCode.length(); i++)
            if (sourceCode.charAt(i) == '#')
                count++;
        return count;
    }

    private static String deleteComments(String sourceCode) throws Exception {
        if (countHsh(sourceCode) % 2 != 0)
            ErrorHandlling.wrongComment();
        String code = "";
        boolean getHsh = false;
        for (int i = 0; i < sourceCode.length(); i++) {
            if (!getHsh && sourceCode.charAt(i) == '#')
                getHsh = true;
            else if (getHsh && sourceCode.charAt(i) == '#')
                getHsh = false;
            if (getHsh || sourceCode.charAt(i) == '#')
                continue;
            code += sourceCode.charAt(i);
        }
        return code;
    }

    private static String deleteSpaces(String sourceCode) throws Exception {
        String finalCode = "";
        boolean space = false;
        for (int i = 0; i < sourceCode.length(); i++) {
            if (sourceCode.charAt(i) == ' ') {
                space = true;
                continue;
            }
            else if (space && finalCode.toCharArray().length > 0 && isAlphabetic(finalCode.toCharArray()[finalCode.toCharArray().length - 1]) && isAlphabetic(sourceCode.charAt(i)))
                ErrorHandlling.cannotSeparateIdentifierUsingSpace();
            else
                finalCode += Character.toString(sourceCode.charAt(i));
            space = false;
        }
        return finalCode;
    }

    private static boolean isAlphabetic(char c) {
        if (c >= 'a' && c <= 'z')
            return true;
        if (c >= 'A' && c <= 'Z')
            return true;
        return false;
    }

    public void setDesc(String desc) {
        this.desc = desc;
        this.type = Type.Comparision;
    }

    public String getDesc() {
        return desc;
    }

    public Type getType() {
        return type;
    }

}