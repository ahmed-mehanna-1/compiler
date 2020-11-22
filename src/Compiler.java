import codeGeneration.CodeGenerator;
import codeOptemization.CodeOptimizer;
import intermediateCodeGeneration.IntermediateCodeGenerator;
import lexical.Token;
import parser.Node;
import parser.CmpStmt;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Compiler {

    private static String sourceCode() throws IOException {
        FileReader fr = new FileReader("res/source code.txt");
        BufferedReader read = new BufferedReader(fr);
        String code = "";
        String line;
        while ((line = read.readLine()) != null)
            code += line;
        return code;
    }

    public static void main(String[] args) throws Exception {
        String sourceCode = sourceCode();
        System.out.println("Source Code: \n" + sourceCode);
        Token.createTokens(sourceCode);
        Node root = new CmpStmt(0);
        IntermediateCodeGenerator.createIntermediateCode(root);
        System.out.println("\nIntermediate Code: \n" + IntermediateCodeGenerator.getIntermediateCode());
        CodeOptimizer.optimizeIntermediateCode();
        System.out.println("Optimized Code: \n" + CodeOptimizer.getOptimizedCode());
        CodeGenerator.createMachineCode();
        System.out.println("Machine Code: \n" + CodeGenerator.getMachineCode());
        CodeGenerator.writeMachineCodeToFile();
    }
}