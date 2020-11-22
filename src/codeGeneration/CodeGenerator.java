package codeGeneration;

import codeOptemization.CodeOptimizer;

import java.io.*;
import java.util.ArrayList;

public class CodeGenerator {
    private static ArrayList<String> reservedWorrd = new ArrayList<>();
    private static ArrayList<String> word = new ArrayList<>();
    private static String machineCode = "PROG\tSTART\t0000\n";
    private static int lineNumber = 1;
    private static String storedInA = "";

    public static void createMachineCode() {
        String[] optimizedCode = CodeOptimizer.getOptimizedCode().split("\n");
        for (int i = 0; i < optimizedCode.length; i++) {
            if (optimizedCode[i].split(" ")[0].equals("if")) {
                writeMachineCode(optimizedCode[i]);
                machineCode += "LINE" + (lineNumber++);
            }
            else {
                if (!reservedWorrd.add("VAR_" + optimizedCode[i].split(" ")[0]))
                    reservedWorrd.add("VAR_" + optimizedCode[i].split(" ")[0]);
                writeMachineCode(optimizedCode[i].split(" ")[0], optimizedCode[i].split(" ")[2]);
            }
        }
        if (machineCode.split("\n")[machineCode.split("\n").length - 1].equals("LINE" + (lineNumber - 1)))
            machineCode += "\tRSUB\n";
        machineCode += "\tEND\tPROG\n";
        writeWord();
        writeReservedWord();
    }

    private static void writeReservedWord() {
        for (int i = 0; i < reservedWorrd.size(); i++)
            machineCode += reservedWorrd.get(i) + "\tRESW\t" + "1\n";
    }

    private static void writeWord() {
        for (int i = 0; i < word.size(); i++)
            machineCode += word.get(i) + "\tWORD\t" +  word.get(i).split("_")[1] + "\n";
    }

    private static void writeMachineCode(String ifStmt) {
        String condition = ifStmt.split(" ")[1];
        String[] sides = conditionSides(condition);
        String thenStmt = "";
        for (int i = 3; i < ifStmt.split(" ").length; i++)
            if (i == 3)
                thenStmt += ifStmt.split(" ")[i];
            else
                thenStmt += " " + ifStmt.split(" ")[i];

        if (isDigit(sides[0]) && !storedInA.equals("NUM_" + sides[0]))
            machineCode += "\tLDA\tNUM_" + sides[0] + "\n";
        else if (!storedInA.equals("VAR_" + sides[0]))
            machineCode += "\tLDA\tVAR_" + sides[0] + "\n";
        if (isDigit(sides[2]))
            machineCode += "\tCOMP\tNUM_" + sides[2] + "\n";
        else
            machineCode += "\tCOM\tVAR_" + sides[2] + "\n";
        machineCode += writeComparisionMachineCode(sides[1], sides[2]);


        String stmts[] = thenStmt.split(",");
        for (int i = 0; i < stmts.length; i++) {
            if (stmts[i].split(" ")[0].equals("if"))
                writeMachineCode(stmts[i]);
            else {
                if (!reservedWorrd.contains("VAR_" + stmts[i].split(" ")[0]))
                    reservedWorrd.add("VAR_" + stmts[i].split(" ")[0]);
                writeMachineCode(stmts[i].split(" ")[0], stmts[i].split(" ")[2]);
            }
        }

    }

    private static String[] conditionSides(String condition) {
        String[] conditionSides = new String[3];
        String compOp = null;
        if (condition.contains(">="))
            compOp = ">=";
        else if (condition.contains("<="))
            compOp = "<=";
        else if (condition.contains(">"))
            compOp = ">";
        else if (condition.contains("<"))
            compOp = "<";
        else if (condition.contains("=="))
            compOp = "==";
        conditionSides[0] = getSide(condition, compOp, 0);
        conditionSides[1] = compOp;
        conditionSides[2] = getSide(condition, compOp, 1);
        return conditionSides;
    }

    private static String getSide(String condition, String compOp, int sideNumber) {
        String side = condition.split(compOp)[sideNumber];
        if (isDigit(side) && !word.contains("NUM_" + side))
                word.add("NUM_" + side);
        return side;
    }

    private static String writeComparisionMachineCode(String compOp, String val) {
        String conditionMachineCode = "";
        switch (compOp) {
            case "==":
                conditionMachineCode += "\tJLT\tLINE" + lineNumber + "\n";
                conditionMachineCode += "\tJGT\tLINE" + lineNumber + "\n";
                break;
            case ">":
                conditionMachineCode += "\tJEQ\tLINE" + lineNumber + "\n";
                conditionMachineCode += "\tJLT\tLINE" + lineNumber + "\n";
                break;
            case "<":
                conditionMachineCode += "\tJEQ\tLINE" + lineNumber + "\n";
                conditionMachineCode += "\tJGT\tLINE" + lineNumber + "\n";
                break;
            case ">=":
                conditionMachineCode += "\tJLT\tLINE" + lineNumber + "\n";
                break;
            case "<=":
                conditionMachineCode += "\tJLT\tLINE" + lineNumber + "\n";
                break;
        }
        return conditionMachineCode;
    }

    private static void writeMachineCode(String var, String val) {
        if (!isExpression(val))
            assStmt(var, val);
        else if (val.length() > 1)
            expStmt(var, val);
    }

    private static boolean isExpression(String exp) {
        if (exp.contains("+") || exp.contains("-") || exp.contains("*") || exp.contains("/"))
            return true;
        return false;
    }

    private static void assStmt(String var, String val) {
        if (isDigit(val) && !word.contains("NUM_" + val)) {
            word.add("NUM_" + val);
            machineCode += "\tLDA\tNUM_" + val + "\n";
        }
        else if (!storedInA.equals("VAR_" + val))
            machineCode += "\tLDA\tVAR_" + val + "\n";
        machineCode += "\tSTA\tVAR_" + var + "\n";
        storedInA = "VAR_" + var;
    }

    private static void expStmt(String var, String exp) {
        String[] separatedExp = separateExp(exp);
        if (isDigit(separatedExp[0]))
            machineCode += "\tLDA\tNUM_" + separatedExp[0] + "\n";
        else if (!storedInA.equals("VAR_" + separatedExp[0]))
            machineCode += "\tLDA\tVAR_" + separatedExp[0] + "\n";
        operatorInstruction(separatedExp[1]);
        if (isDigit(separatedExp[2]))
            machineCode += "NUM_" + separatedExp[2] + "\n";
        else
            machineCode += "VAR_" + separatedExp[2] + "\n";

        machineCode += "\tSTA\t"  + "VAR_"+var + "\n";
        storedInA = "VAR_" + var;

        if (isDigit(separatedExp[0]) && !word.contains("NUM_" + separatedExp[0]))
            word.add("NUM_" + separatedExp[0]);
        if (isDigit(separatedExp[2]) && !word.contains("NUM_" + separatedExp[2]))
            word.add("NUM_" + separatedExp[2]);
    }

    private static String[] separateExp(String exp) {
        String[] separatedExp = null;
        if (exp.contains("+"))
            separatedExp = separateExp(exp, "+");
        else if (exp.contains("-"))
            separatedExp = separateExp(exp, "-");
        else if (exp.contains("*"))
            separatedExp = separateExp(exp, "*");
        else if (exp.contains("/"))
            separatedExp = separateExp(exp, "/");
        return separatedExp;
    }

    private static String[] separateExp(String exp, String op) {
        String[] separatedExp = new String[3];
        for (int i = 0; i < 3; i++)
            separatedExp[i] = "";
        int index = 0;
        for (int i = 0; i < exp.length(); i++) {
            if (exp.charAt(i) != op.charAt(0))
                separatedExp[index] += exp.charAt(i);
            else {
                separatedExp[++index] += exp.charAt(i);
                index++;
            }
        }
        return separatedExp;
    }

    private static void operatorInstruction(String op) {
        switch (op) {
            case "+":
                machineCode += "\tADD\t";
                break;
            case "-":
                machineCode += "\tSUB\t";
                break;
            case "*":
                machineCode += "\tMUL\t";
                break;
            case "/":
                machineCode += "\tDIV\t";
                break;
        }
    }

    private static boolean isDigit(String number) {
        try {
            Integer.parseInt(number);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    public static String getMachineCode() {
        return machineCode;
    }

    public static void writeMachineCodeToFile() throws IOException {
        FileWriter file = new FileWriter("/media/mehanna-cw/DATA/College/TERM 4/Programming Language And Translators/PROJECTS/Compiler Phases/res/machine code.txt");
        BufferedWriter bw = new BufferedWriter(file);
        bw.write(machineCode);
        bw.close();
        System.out.println("The Object  was successfully written to a file");
    }
}