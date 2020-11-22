package errorHandlling;

import lexical.Token;

public class ErrorHandlling {

    public static void identifierNotDeclared(Token id) throws Exception {
        System.err.println("Identifier \"" + id.getDesc() +"\" Not Declared");
        throw new Exception("Identifier \"" + id.getDesc() +"\" Not Declared");
    }

    public static void cannotSeparateIdentifierUsingSpace() throws Exception{
        System.err.println("Cannot Separate Identifier Using Space");
        throw new Exception("Cannot Separate Identifier Using Space");
    }

    public static void wrongComment() throws Exception {
        System.err.println("Wrong Comment .. Check Number Of \"#\"");
        throw new Exception("Wrong Comment .. Check Number Of \"#\"");
    }

    public static void missedSemicolon() throws Exception {
        System.err.println("Missed Semicolon");
        throw new Exception("Missed Semicolon");
    }

    public static void wrongExpression() throws Exception {
        System.err.println("Wrong Expression");
        throw new Exception("Wrong Expression");
    }

    public static void wrongIdentifier(Token id) throws Exception {
        System.err.println("Wrong \"" + id.getDesc() +"\" Identifier");
        throw new Exception("Wrong \"" + id.getDesc() +"\" Identifier");
    }

    public static void cannotDetectStmt() throws Exception {
        System.err.println("Parser Cannot Resolve This Stmt");
        throw new Exception("Parser Cannot Resolve This Stmt");
    }

    public static void wrongIfStmt() throws Exception {
        System.err.println("Wrong If Statement");
        throw new Exception("Wrong If Statement");
    }

}