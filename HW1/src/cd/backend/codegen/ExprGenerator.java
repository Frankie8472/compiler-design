package cd.backend.codegen;

import cd.Config;
import cd.backend.codegen.RegisterManager.Register;
import cd.ir.Ast;
import cd.ir.Ast.BinaryOp;
import cd.ir.Ast.BooleanConst;
import cd.ir.Ast.BuiltInRead;
import cd.ir.Ast.Cast;
import cd.ir.Ast.Expr;
import cd.ir.Ast.Field;
import cd.ir.Ast.Index;
import cd.ir.Ast.IntConst;
import cd.ir.Ast.NewArray;
import cd.ir.Ast.NewObject;
import cd.ir.Ast.NullConst;
import cd.ir.Ast.ThisRef;
import cd.ir.Ast.UnaryOp;
import cd.ir.Ast.Var;
import cd.ir.ExprVisitor;
import cd.util.debug.AstOneLine;

/**
 * Generates code to evaluate expressions. After emitting the code, returns a
 * String which indicates the register where the result can be found.
 */
class ExprGenerator extends ExprVisitor<Register, Void> {

    protected final AstCodeGenerator cg;

    ExprGenerator(AstCodeGenerator astCodeGenerator) {
        cg = astCodeGenerator;
    }

    public Register gen(Expr ast) {
        return visit(ast, null);
    }

    @Override
    public Register visit(Expr ast, Void arg) {
        try {
            cg.emit.increaseIndent("Emitting " + AstOneLine.toString(ast));
            return super.visit(ast, null);
        } finally {
            cg.emit.decreaseIndent();
        }
    }

    private int cntReg(Expr ast) {
        int num = 0;
        if (ast instanceof Ast.LeftRightExpr) {
            int left = cntReg(((Ast.LeftRightExpr) ast).left());
            int right = cntReg(((Ast.LeftRightExpr) ast).right());
            if (left == right) {
                num = right + 1;
            } else {
                num = Math.max(left, right);
            }
        } else if (ast instanceof Ast.ArgExpr) {
            num = cntReg(((Ast.ArgExpr) ast).arg());
        } else if (ast instanceof Ast.MethodCallExpr) {
            // dummy case
        } else { // Ast.LeafExpr (protected)
            num = 1;
        }
        return num;
    }

    @Override
    public Register binaryOp(BinaryOp ast, Void arg) {

        Register src;
        Register dest;

        if (cntReg(ast.left()) > cntReg(ast.right())) {
            dest = cg.eg.visit(ast.left(), arg);
            src = cg.eg.visit(ast.right(), arg);
        } else {
            src = cg.eg.visit(ast.right(), arg);
            dest = cg.eg.visit(ast.left(), arg);
        }

        String op;
        switch (ast.operator) {
            case B_MINUS:
                op = "subl";
                cg.emit.emit(op, src, dest);
                break;

            case B_PLUS:
                op = "addl";
                cg.emit.emit(op, src, dest);
                break;

            case B_TIMES:
                op = "imull";
                cg.emit.emit(op, src, dest);
                break;

            case B_DIV: // dest / src

                cg.emit.emit("pushl", Register.EAX);
                cg.emit.emit("pushl", Register.EDX);
                cg.emit.emit("pushl", src);
                cg.emit.emitMove(dest, Register.EAX);
                cg.emit.emitRaw("cltd");
                cg.emit.emit("idivl", AssemblyEmitter.registerOffset(0, Register.ESP));
                cg.emit.emit("addl", 4, Register.ESP);
                cg.emit.emitMove(Register.EAX, dest);

                if (dest.equals(Register.EDX)) {
                    cg.emit.emit("addl", 4, Register.ESP);
                } else {
                    cg.emit.emit("popl", Register.EDX);
                }
                if (dest.equals(Register.EAX)) {
                    cg.emit.emit("addl", 4, Register.ESP);
                } else {
                    cg.emit.emit("popl", Register.EAX);
                }

                break;

            case B_OR:
                break;
            case B_AND:
                break;
            case B_EQUAL:
                break;
            case B_NOT_EQUAL:
                break;
            case B_MOD:
                break;
            case B_LESS_THAN:
                break;
            case B_GREATER_THAN:
                break;
            case B_LESS_OR_EQUAL:
                break;
            case B_GREATER_OR_EQUAL:
                break;
            default:
                break;
        }

        // CleanUp
        cg.rm.releaseRegister(src);
        return dest;
    }

    @Override
    public Register booleanConst(BooleanConst ast, Void arg) {
        {
            throw new RuntimeException("Not required");
        }
    }

    @Override
    public Register builtInRead(BuiltInRead ast, Void arg) {
        Register result = cg.rm.getRegister();
        cg.emit.emit("leal", AssemblyEmitter.registerOffset(-4, Register.ESP), result);
        cg.emit.emit("pushl", result);
        cg.emit.emit("pushl", AssemblyEmitter.labelAddress("label_print"));
        cg.emit.emit("call", Config.SCANF);
        cg.emit.emit("popl", result);
        cg.emit.emit("popl", result);

        return result;
    }

    @Override
    public Register cast(Cast ast, Void arg) {
        {
            throw new RuntimeException("Not required");
        }
    }

    @Override
    public Register index(Index ast, Void arg) {
        {
            throw new RuntimeException("Not required");
        }
    }

    @Override
    public Register intConst(IntConst ast, Void arg) {
        Register dest = cg.rm.getRegister();
        cg.emit.emit("movl", ast.value, dest);
        return dest;
    }

    @Override
    public Register field(Field ast, Void arg) {
        {
            throw new RuntimeException("Not required");
        }
    }

    @Override
    public Register newArray(NewArray ast, Void arg) {
        {
            throw new RuntimeException("Not required");
        }
    }

    @Override
    public Register newObject(NewObject ast, Void arg) {
        {
            throw new RuntimeException("Not required");
        }
    }

    @Override
    public Register nullConst(NullConst ast, Void arg) {
        {
            throw new RuntimeException("Not required");
        }
    }

    @Override
    public Register thisRef(ThisRef ast, Void arg) {
        {
            throw new RuntimeException("Not required");
        }
    }

    @Override
    public Register unaryOp(UnaryOp ast, Void arg) {
        String op = "nop";
        Register dest = visit(ast.arg(), arg);

        switch (ast.operator) {
            case U_MINUS:
                op = "negl";
                break;
            case U_BOOL_NOT:
                op = "notl"; // this is not correct and not neccesary
                break;
            case U_PLUS:
                // Is in use later
                break;
            default:
                break;
        }
        cg.emit.emit(op, dest);
        return dest;

    }

    @Override
    public Register var(Var ast, Void arg) {
        Register dest = cg.rm.getRegister();
        String src = "var_" + ast.name;
        cg.emit.emitMove(src, dest);

        return dest;

    }
}
