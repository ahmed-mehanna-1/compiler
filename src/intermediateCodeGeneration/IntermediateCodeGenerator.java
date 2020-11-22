package intermediateCodeGeneration;

import errorHandlling.ErrorHandlling;
import lexical.Token;
import lexical.Type;
import parser.*;

import java.util.ArrayList;
import java.util.Stack;

public class IntermediateCodeGenerator {
    private static String intermediateCode = "";
    private static int count = 1;
    private static boolean newLine = true;
    private static ArrayList<String> declaredIds = new ArrayList<>();

    private static void createIntermediateCode(ExpStmt expStmt) throws Exception {
        Node right = expStmt.getRight();
        Node left = expStmt.getLeft();
        Stack<Token> stack = new Stack<>();
        Stack<Token> tempStack = new Stack<>();
        for (int i = right.getExp().length - 1; i >= 0; i--)
            stack.push(right.getExp()[i]);
        for (int i = 0; i < right.getExp().length; i++) {
            if (stack.peek().getType() == Type.Operator) {
                Token b = tempStack.pop();
                Token a = tempStack.pop();
                if (a.getType() == Type.Identifier && !declaredIds.contains(a.getDesc()) && isIdentifier(a.getDesc()))
                    ErrorHandlling.identifierNotDeclared(a);
                if (b.getType() == Type.Identifier && !declaredIds.contains(b.getDesc()) && isIdentifier(b.getDesc()))
                    ErrorHandlling.identifierNotDeclared(b);
                intermediateCode += "temp"+ count + " := " + a.getDesc() + stack.pop().getDesc() + b.getDesc();
                newLine();
                stack.push(new Token("temp" + (count++), true));
                if (stack.size() > 1)
                    i--;
            }
            else
                tempStack.push(stack.pop());
        }
        intermediateCode += left.getLexeme().getDesc() + " := " + stack.pop().getDesc();
        declaredIds.add(left.getLexeme().getDesc());
        newLine = true;
        newLine();
    }

    private static boolean isIdentifier(String id) {
        if (!id.contains("temp"))
            return true;
        if (id.contains("temp"))
            if (id.split("temp").length >= 2 && id.split("temp")[0].equals("") && isDigit(id.split("temp")[1]))
                return false;

        return true;
    }

    private static boolean isDigit(String number) {
        try {
            Integer.parseInt(number);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    private static void newLine() {
        if (newLine)
            intermediateCode += "\n";
        else
            intermediateCode += ",";
    }

    private static void createIntermediateCode(AssStmt assStmt) throws Exception {
        if (assStmt.getRight().getLexeme().getType() == Type.Identifier && !declaredIds.contains(assStmt.getRight().getLexeme().getDesc()))
            ErrorHandlling.identifierNotDeclared(assStmt.getRight().getLexeme());
        if (!newLine && assStmt.getLeft().getLexeme().getType() == Type.Identifier && !declaredIds.contains(assStmt.getLeft().getLexeme().getDesc()))
            ErrorHandlling.identifierNotDeclared(assStmt.getLeft().getLexeme());
        intermediateCode += "temp" + count + " := " + assStmt.getRight().getLexeme().getDesc();
        newLine();
        intermediateCode += assStmt.getLeft().getLexeme().getDesc() + " := " + "temp" + (count++);
        declaredIds.add(assStmt.getLeft().getLexeme().getDesc());
        newLine = true;
        newLine();
    }

    private static void createIntermediateCode(IfStmt ifStmt) throws Exception {
        Node left = ifStmt.getLeft();
        Node middle = ifStmt.getMiddle();
        Node right = ifStmt.getRight();
        intermediateCode += left.getLexeme().getDesc() + " ";
        for (int i = 0; i < middle.getExp().length; i++) {
            if (middle.getExp()[i].getType() == Type.Identifier && !declaredIds.contains(middle.getExp()[i].getDesc()))
                ErrorHandlling.identifierNotDeclared(middle.getExp()[i]);
            intermediateCode += middle.getExp()[i].getDesc();
        }
        intermediateCode += " then ";
        newLine = false;
        traverse(right);
    }

    private static void traverse (Node root) throws Exception {
        if(root instanceof CmpStmt) {
            traverse(root.getLeft());
            if (root.getRight() != null)
                traverse(root.getRight());
        }
        else if(root instanceof Stmt)
            traverse(root.getMiddle());
        else if(root instanceof AssStmt)
            createIntermediateCode((AssStmt) root);
        else if (root instanceof ExpStmt)
            createIntermediateCode((ExpStmt) root);
        else if(root instanceof IfStmt)
            createIntermediateCode((IfStmt) root);
    }

    public static void createIntermediateCode(Node root) throws Exception {
        traverse(root);
    }

    public static String getIntermediateCode() {
        return intermediateCode;
    }

}