package cd.backend.codegen;

import cd.Config;
import cd.ir.Ast;

import java.util.HashMap;
import java.util.Map;

import static cd.backend.codegen.AstCodeGenerator.MIN_NUM_PARAMS;

public class MethodContext {

    private ParamsNeededVisitor pnv = new ParamsNeededVisitor();

    private ClassContext classContext;
    private Ast.MethodDecl ast;

    Map<String, Integer> offsets = new HashMap<>();

    public final int stackSize;

    public String returnLabel;


    public MethodContext(ClassContext classContext, Ast.MethodDecl ast) {
        this.classContext = classContext;
        this.ast = ast;

        // Stack offset for params
        int stackSize = Config.SIZEOF_PTR * 3;


        for (String arg : ast.argumentNames) {
            this.offsets.put(arg, stackSize);
            stackSize += Config.SIZEOF_PTR;
        }

        // Start at %ebp - 4 and make space for callee save registers
        stackSize = Config.SIZEOF_PTR * 4;

        for (Ast decl : ast.decls().rwChildren) {
            if (decl instanceof Ast.VarDecl) {
                Ast.VarDecl varDecl = (Ast.VarDecl)decl;
                this.offsets.put(varDecl.name, -stackSize);
                stackSize += Config.SIZEOF_PTR;
            }
        }

        // Account for caller saves
        stackSize += Config.SIZEOF_PTR * 3;

        stackSize += Math.max(MIN_NUM_PARAMS, this.pnv.visitChildren(ast, null)) * Config.SIZEOF_PTR;

        // Align stack size to nearest 0x8
        stackSize += (((8 - (stackSize % 16)) + 16) % 16);

        this.stackSize = stackSize;

    }

    public Integer getOffset(String name) {
        return this.offsets.get(name);
    }
}
