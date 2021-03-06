package cd.backend.codegen;

import java.io.Writer;
import java.util.*;

import cd.Main;
import cd.ir.Ast.ClassDecl;
import cd.ir.Ast.MethodDecl;
import cd.ir.Ast.Stmt;
import cd.ir.AstVisitor;
import cd.ir.BasicBlock;
import cd.ir.ControlFlowGraph;
import cd.ir.Symbol.PrimitiveTypeSymbol;
import cd.util.debug.AstDump;

public class CfgCodeGenerator {

    public final Main main;
    private final AstCodeGeneratorRef cg;

    public CfgCodeGenerator(Main main, Writer out) {
        this.main = main;
        if (main.deactivateAssemblyOptimize) {
            cg = new AstCodeGeneratorRef(main, out);
        } else {
            cg = new AstCodeGeneratorOpt(main, out);
        }
    }

    public void go(List<? extends ClassDecl> astRoots) {
        cg.emitPrefix(astRoots);
        for (ClassDecl cdecl : astRoots)
            new CfgStmtVisitor().visit(cdecl, null);
    }

    private class CfgStmtVisitor extends AstVisitor<Void, CurrentContext> {

        @Override
        public Void classDecl(ClassDecl ast, CurrentContext arg) {
            cg.emit.emitCommentSection("Class " + ast.name);
            cg.emit.increaseIndent("");
            CurrentContext context = new CurrentContext(ast);
            super.classDecl(ast, context);
            cg.emit.decreaseIndent();
            return null;
        }

        @Override
        public Void methodDecl(MethodDecl ast, CurrentContext arg) {
            cg.emitMethodPrefix(ast);
            CurrentContext context = new CurrentContext(arg, ast);

            ControlFlowGraph cfg = ast.cfg;
            assert cfg != null;

            Map<BasicBlock, String> labels = new HashMap<BasicBlock, String>();
            for (BasicBlock blk : cfg.allBlocks)
                labels.put(blk, cg.emit.uniqueLabel());
            String exitLabel = cg.emit.uniqueLabel();


            cfg.allBlocks.sort(Comparator.comparingInt(o -> o.index));
            if(cfg.start != cfg.allBlocks.get(0)) {
                cg.emit.emit("jmp", labels.get(cfg.start));
            }

            for (BasicBlock blk : cfg.allBlocks) {

                cg.emit.emitCommentSection("Basic block " + blk.index);
                cg.emit.emitLabel(labels.get(blk));

                for (Stmt stmt : blk.stmts) {
//                    System.out.println(AstDump.toString(stmt));
                    cg.sg.gen(stmt, context);
                }

                if (blk == cfg.end) {
                    cg.emit.emitComment(String.format("Return"));
                    assert blk.successors.size() == 0;
                    cg.emit.emit("jmp", exitLabel);
                } else if (blk.condition != null) {
                    assert blk.successors.size() == 2;
                    cg.emit.emitComment(String.format(
                            "Exit to block %d if true, block %d if false",
                            blk.trueSuccessor().index, blk.falseSuccessor().index));
                    cg.genJumpIfFalse(blk.condition, labels.get(blk.falseSuccessor()), arg);
                    if(blk.trueSuccessor().index != blk.index + 1) {
                        cg.emit.emit("jmp", labels.get(blk.trueSuccessor()));
                    }
                } else {
                    cg.emit.emitComment(String.format(
                            "Exit to block %d", blk.successors.get(0).index));
                    assert blk.successors.size() == 1;
                    if(blk.successors.get(0).index != blk.index + 1){
                        cg.emit.emit("jmp", labels.get(blk.successors.get(0)));
                    }
                }
                cg.rm.flushTags();
            }

            cg.emit.emitLabel(exitLabel);
            if (ast.sym.returnType.equals(PrimitiveTypeSymbol.voidType))
                cg.emitMethodSuffix(true);
            else
                cg.emitMethodSuffix(true);

            return null;
        }

    }
}

