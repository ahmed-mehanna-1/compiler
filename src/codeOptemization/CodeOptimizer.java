package codeOptemization;

import intermediateCodeGeneration.IntermediateCodeGenerator;

import java.util.ArrayList;
import java.util.Hashtable;

public class CodeOptimizer {
    private static String optimizedCode = "";
    private static Hashtable<String, Double> variables = new Hashtable<>();

    public static void optimizeIntermediateCode() {
        String[] stmts = IntermediateCodeGenerator.getIntermediateCode().split("\n");
        ArrayList<int[]> linesNumber = new ArrayList<>();
        for (int i = 0; i < stmts.length; i++) {
            if (stmts[i].split(" ")[0].equals("if"))
                optimizeIfCondition(stmts, linesNumber, i);
            else if (!isIdentifier((stmts[i].split(" := ")[0])))
                continue;
            else {
                String newRightSide = initOptimize(stmts, i);
                stmts[i] = stmts[i].split(" := ")[0] + " := " + newRightSide;
                linesNumber.add(new int[]{i-1, i-1});
                if (!variables.containsKey(stmts[i].split(" := ")[0])) {
                    String x = stmts[i];
                    while (stmts[i].split(" := ")[1].contains("temp") || containsVariable(stmts[i].split(" := ")[1]))
                        stmts[i] = stmts[i].split(" := ")[0] + " := " + getVal(stmts, i);
                    stmts[i] = stmts[i].split(" := ")[1];

                    variables.put(x.split(" := ")[0], EvaluateString.eval(stmts[i]));
                    stmts[i] = x;
                }
                else
                    optemizeExpression(stmts, linesNumber, i);
            }
        }
        for (int i = 0; i < linesNumber.size(); i++)
            for (int j = linesNumber.get(i)[0]; j <= linesNumber.get(i)[1]; j++)
                stmts[j] = "?";
        for (int i = 0; i < stmts.length; i++)
            if (!stmts[i].equals("?"))
                optimizedCode += stmts[i] + "\n";
    }

    private static String[] deleteLines(String[] stmts, int line) {
        String[] newStmts = new String[stmts.length - 1];
        int index = 0;
        stmts[line] = "?";
        for (int i = 0; i < stmts.length; i++)
            if (!stmts[i].equals("?"))
                newStmts[index++] = stmts[i];
        return newStmts;
    }

    private static boolean isIdentifier(String id) {
        if (!id.contains("temp"))
            return true;
        String[] sides = id.split("temp");
        if (sides.length >= 2 && sides[0].equals("") && isDigit(sides[1]))
            return false;

        return true;
    }

    private static String thenStmt(String[] stmts, int i) {
        String thenStmt = "";
        for (int j = 3; j < stmts[i].split(" ").length; j++)
            if (j == 3)
                thenStmt += stmts[i].split(" ")[j];
            else
                thenStmt += " " + stmts[i].split(" ")[j];
        return thenStmt;
    }

    private static String optimizeIfCondition(String[] stmts, ArrayList<int[]> linesNumber, int i) {
        String condition = stmts[i].split(" ")[1];
        String[] sides = conditionSides(condition);

        String thenStmt = thenStmt(stmts, i);

        if (!isDigit(sides[0]) && !isDigit(sides[2])) {
            sides[0] = variables.get(sides[0]).toString();
            sides[2] = variables.get(sides[2]).toString();
        }
        else if (!isDigit(sides[0]) && isDigit(sides[2]))
            sides[0] = variables.get(sides[0]).toString();
        else if (isDigit(sides[0]) && !isDigit(sides[2]))
            sides[2] = variables.get(sides[2]).toString();

        boolean enterIf = true;
        enterIf = enterIf(sides);

        String stmt = stmts[i].split("then")[0] + "then ";
        stmts[i] = thenStmt;

        if (enterIf && stmts[i].split(" ")[0].equals("if"))
            stmt += optimizeIfCondition(stmts, linesNumber, i);

        else if (enterIf) {
            int j = thenStmt.split(",").length - 1;
            String[] x = thenStmt.split(",");
            String newRightSide = initOptimize(x, j);
            x[j] = x[j].split(" := ")[0] + " := " + newRightSide;
            x = deleteLines(x, j - 1);
            j--;
            for (int k = 0; k < x.length; k++)
                if (k < x.length - 1)
                    stmt += x[k] + ",";
                else
                    stmt += x[k];

            while (x[j].split(" := ")[1].contains("temp") || containsVariable(x[j].split(" := ")[1]))
                x[j] = x[j].split(" := ")[0] + " := " + getVal(x, j);

            String exp = x[j].split(" := ")[1];
            double val = EvaluateString.eval(exp);

            if (variables.get(x[j].split(" := ")[0]) == val)
                enterIf = false;
            else
                variables.put(x[j].split(" := ")[0], EvaluateString.eval(exp));
        }
        stmts[i] = stmt;

        if (!enterIf)
            linesNumber.add(new int[]{i, i});
        return stmt;
    }

    private static boolean enterIf(String[] condition) {
        switch (condition[1]) {
            case ">=":
                if (Double.parseDouble(condition[0]) >= Double.parseDouble(condition[2]))
                    return true;
                break;
            case "<=":
                if (Double.parseDouble(condition[0]) <= Double.parseDouble(condition[2]))
                    return true;
                break;
            case ">":
                if (Double.parseDouble(condition[0]) > Double.parseDouble(condition[2]))
                    return true;
                break;
            case "<":
                if (Double.parseDouble(condition[0]) < Double.parseDouble(condition[2]))
                    return true;
                break;
            case "==":
                if (Double.parseDouble(condition[0]) == Double.parseDouble(condition[2]))
                    return true;
                break;
        }
        return false;
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
        return condition.split(compOp)[sideNumber];
    }

    private static void optemizeExpression(String[] stmts, ArrayList<int[]> linesNumber, int i) {
        String x = stmts[i];
        while (stmts[i].split(" := ")[1].contains("temp") || containsVariable(stmts[i].split(" := ")[1]))
            stmts[i] = stmts[i].split(" := ")[0] + " := " + getVal(stmts, i);
        String exp = stmts[i].split(" := ")[1];
        stmts[i] = x;
        if (EvaluateString.eval(exp) == variables.get(stmts[i].split(" := ")[0]))
            linesNumber.add(new int[]{getStartIndex(stmts, i), i});
        else
            variables.replace(x.split(" := ")[0], EvaluateString.eval(exp));
    }

    private static String getVariable(String exp) {
        String var = "";
        for (int i = 0; i < exp.length(); i++)
            if (!isDigit(Character.toString(exp.charAt(i))) && !isOperator(exp.charAt(i)) && exp.charAt(i) != '.') {
                var += exp.charAt(i);
                for (int j = i + 1; j < exp.length(); j++)
                    if (isOperator(exp.charAt(j)))
                        break;
                    else
                        var += exp.charAt(j);
                break;
            }
        return var;
    }

    private static boolean containsVariable(String exp) {
        for (int i = 0; i < exp.length(); i++)
            if (!isDigit(Character.toString(exp.charAt(i))) && !isOperator(exp.charAt(i)) && exp.charAt(i) != '.')
                return true;
        return false;
    }

    private static boolean isOperator(char op) {
        switch (op) {
            case '+':
            case '-':
            case '*':
            case '/':
                return true;
            default:
                return false;
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

    private static boolean isEqual(String exp1, String exp2) {
        if (EvaluateString.eval(exp1) == EvaluateString.eval(exp2))
            return true;
        return false;
    }

    private static int getStartIndex(String[] stmts, int line) {
        if (stmts[line].split(" := ")[1].contains("temp")) {
            String stmt = stmts[line].split(" := ")[1];
            String temp = getTemp(stmt);
            int i;
            for (i = line - 1; i >= 0; i--)
                if (stmts[i].split(" := ")[0].equals(temp))
                    break;
            return getStartIndex(stmts, i);
        }
        else
            return line;
    }

    private static String initOptimize(String[] stmts, int line) {
        String stmt = stmts[line].split(" := ")[1];
        String temp = getTemp(stmt);
        int i;
        for (i = line - 1; i >= 0; i--)
            if (stmts[i].split(" := ")[0].equals(temp))
                break;
        return stmt.replace(temp, stmts[i].split(" := ")[1]);
    }

    private static String getVal(String[] stmts, int line) {
        if (!isIdentifier(stmts[line].split(" := ")[1])) {
            String stmt = stmts[line].split(" := ")[1];
            String temp = getTemp(stmt);
            int i;
            for (i = line - 1; i >= 0; i--)
                if (stmts[i].split(" := ")[0].equals(temp))
                    break;
            String val = getVal(stmts, i);
            return stmt.replace(temp, val);
        }
        if (containsVariable(stmts[line].split(" := ")[1])) {
            String stmt = stmts[line].split(" := ")[1];
            String var = getVariable(stmt);
            int i;
            for (i = line - 1; i >= 0; i--)
                if (stmts[i].split(" := ")[0].equals(var))
                    break;
            if (i < 0)
                return stmt.replace(var, variables.get(var).toString());
            String val = getVal(stmts, i);
            return stmt.replace(var, val);
        }
        else
            return stmts[line].split(" := ")[1];
    }

    private static String getTemp(String stmt) {
        int i;
        for (i = stmt.indexOf("temp") + 1; i < stmt.length(); i++)
            if (stmt.charAt(i) == '+' || stmt.charAt(i) == '-' || stmt.charAt(i) == '*' || stmt.charAt(i) == '/')
                break;
        return stmt.substring(stmt.indexOf("temp"), i);
    }

    public static String getOptimizedCode() {
        return optimizedCode;
    }

}