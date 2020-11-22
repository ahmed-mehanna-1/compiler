package lexical;

import java.util.Hashtable;

public class Basic {
    public static String[] keywords = {"if"};
    public static String[] comparision = {"==", ">", "<", "<=", ">="};
    public static String[] operator = {"+", "-", "*", "/", "="};
    public static String[] error = {"!", "@", "$", "&", "~", "`"};
    public static Hashtable<String, Type> delimiter;

    public static void initDelimiters() {
        delimiter = new Hashtable<>();
        delimiter.put("(", Type.OpenParentheses);
        delimiter.put(")", Type.CloseParentheses);
        delimiter.put(";", Type.End);
    }

}
