package cd.frontend.semantic;

import cd.ir.Ast;
import cd.ir.Ast.ClassDecl;
import cd.ir.AstVisitor;
import cd.ir.Symbol;
import cd.ir.Symbol.TypeSymbol;
import cd.ir.Symbol.ClassSymbol;
import cd.ir.Symbol.PrimitiveTypeSymbol;
import cd.ir.Symbol.ArrayTypeSymbol;
import cd.ir.Symbol.MethodSymbol;

import java.util.List;

public class SemanticChecker extends AstVisitor<Void, CurrentContext> {

    private TypeManager typeManager; // FIELD!

    public SemanticChecker(TypeManager typeManager) {
        this.typeManager = typeManager;
    }

    public void check(List<Ast.ClassDecl> classDecls) throws SemanticFailure {
        // Check for correct initialization
        try {
            ClassSymbol mainClass = (ClassSymbol) typeManager.stringToTypeSymbol("Main");
            MethodSymbol mainMethod = typeManager.getMethod("main", mainClass);
            if (mainMethod.parameters.size() != 0 || mainMethod.returnType != PrimitiveTypeSymbol.voidType) {
                throw new SemanticFailure(SemanticFailure.Cause.INVALID_START_POINT);
            }
        } catch (SemanticFailure e) {
            if(e.cause == SemanticFailure.Cause.INVALID_START_POINT){
                throw e;
            }
            if(e.cause == SemanticFailure.Cause.NO_SUCH_TYPE || e.cause == SemanticFailure.Cause.NO_SUCH_METHOD){
                throw new SemanticFailure(SemanticFailure.Cause.INVALID_START_POINT);
            }
        }

        for (ClassDecl classDecl : classDecls) {
            visit(classDecl, null);
        }

    }

    @Override
    public Void classDecl(ClassDecl ast, CurrentContext arg) {
        visitChildren(ast, new CurrentContext(ast.sym));
        return null;
    }

    @Override
    public Void varDecl(Ast.VarDecl ast, CurrentContext arg) {
        // Ignore that one
        return null;
    }

    @Override
    public Void methodDecl(Ast.MethodDecl ast, CurrentContext arg) {
        CurrentContext current = new CurrentContext(arg, ast.sym);
        visitChildren(ast, current);
        if (!current.getCorrectReturn() && !current.getMethodSymbol().returnType.equals(PrimitiveTypeSymbol.voidType)) {
            throw new SemanticFailure(SemanticFailure.Cause.MISSING_RETURN);
        }
        return null;
    }

    @Override
    public Void methodCall(Ast.MethodCall ast, CurrentContext arg) {
        visitChildren(ast, arg);
        return null;
    }

    @Override
    public Void methodCall(Ast.MethodCallExpr ast, CurrentContext arg) {
        MethodSymbol methodSymbol;
        TypeSymbol receiverTypeSymbol;

        visitChildren(ast, arg);

        if (ast.receiver() != null) {
            if (!(ast.receiver().type instanceof ClassSymbol) || ast.receiver().type instanceof PrimitiveTypeSymbol || ast.receiver().type instanceof ArrayTypeSymbol) {
                throw new SemanticFailure(SemanticFailure.Cause.TYPE_ERROR);
            }
            receiverTypeSymbol = ast.receiver().type;
        } else {
            receiverTypeSymbol = arg.getClassSymbol();
        }

        methodSymbol = typeManager.getMethod(ast.methodName, receiverTypeSymbol);

        ast.type = methodSymbol.returnType;

        if (methodSymbol.parameters.size() != ast.argumentsWithoutReceiver().size()) {
            throw new SemanticFailure(SemanticFailure.Cause.WRONG_NUMBER_OF_ARGUMENTS);
        }

        for (int i = 0; i < methodSymbol.parameters.size(); i++) {
            TypeSymbol is_symbol = ast.argumentsWithoutReceiver().get(i).type;
            TypeSymbol should_be_symbol = methodSymbol.parameters.get(i).type;
            if (!typeManager.isAssignable(should_be_symbol, is_symbol)) {
                throw new SemanticFailure(SemanticFailure.Cause.TYPE_ERROR);
            }
        }

        return null;
    }

    @Override
    public Void assign(Ast.Assign ast, CurrentContext arg) {
        visitChildren(ast, arg);
        if (!(ast.left() instanceof Ast.Var || ast.left() instanceof Ast.Field || ast.left() instanceof Ast.Index)) {
            throw new SemanticFailure(SemanticFailure.Cause.NOT_ASSIGNABLE);
        }
        if (!typeManager.isAssignable(ast.left().type, ast.right().type)) {
            throw new SemanticFailure(SemanticFailure.Cause.TYPE_ERROR);
        }
        return null;
    }

    @Override
    public Void builtInWrite(Ast.BuiltInWrite ast, CurrentContext arg) {
        visitChildren(ast, arg);
        if (!typeManager.isAssignable(PrimitiveTypeSymbol.intType, ast.arg().type)) {
            throw new SemanticFailure(SemanticFailure.Cause.TYPE_ERROR);
        }
        return null;
    }

    @Override
    public Void builtInWriteln(Ast.BuiltInWriteln ast, CurrentContext arg) {
        if (ast.children().size() != 0) {
            throw new SemanticFailure(SemanticFailure.Cause.WRONG_NUMBER_OF_ARGUMENTS);
        }
        return null;
    }

    @Override
    public Void ifElse(Ast.IfElse ast, CurrentContext arg) {

        if (ast.condition() != null) {
            visit(ast.condition(), arg);
        }

        if (arg.getCorrectReturn()) {

            if (ast.then() != null) {
                visit(ast.then(), arg);
            }

            if (ast.otherwise() != null) {
                visit(ast.otherwise(), arg);
            }

        } else {

            if (ast.then() != null) {
                visit(ast.then(), arg);
            }

            if (arg.getCorrectReturn() && ast.otherwise() != null) {
                arg.setCorrectReturn(false);
                visit(ast.otherwise(), arg);
                arg.setCorrectReturn(arg.getCorrectReturn());
            } else if (ast.otherwise() != null) {
                visit(ast.otherwise(), arg);
                arg.setCorrectReturn(false);
            }
        }


        if (!typeManager.isAssignable(PrimitiveTypeSymbol.booleanType, ast.condition().type)) {
            throw new SemanticFailure(SemanticFailure.Cause.TYPE_ERROR);
        }
        return null;
    }

    @Override
    public Void returnStmt(Ast.ReturnStmt ast, CurrentContext arg) {

        arg.setCorrectReturn(true);

        visitChildren(ast, arg);
        if ((arg.getMethodSymbol().returnType.equals(PrimitiveTypeSymbol.voidType)) &&
                (ast.children().size() != 0)) {
            throw new SemanticFailure(SemanticFailure.Cause.TYPE_ERROR);
        }

        if ((ast.children().size() != 0) && !typeManager.isAssignable(arg.getMethodSymbol().returnType, ast.arg().type)) {
            throw new SemanticFailure(SemanticFailure.Cause.TYPE_ERROR);
        }
        return null;
    }

    @Override
    public Void whileLoop(Ast.WhileLoop ast, CurrentContext arg) {
        visitChildren(ast, arg);
        if (!typeManager.isAssignable(PrimitiveTypeSymbol.booleanType, ast.condition().type)) {
            throw new SemanticFailure(SemanticFailure.Cause.TYPE_ERROR);
        }
        return null;
    }

    @Override
    public Void binaryOp(Ast.BinaryOp ast, CurrentContext arg) {
        visit(ast.left(), arg);
        visit(ast.right(), arg);

        if ((!typeManager.isAssignable(ast.left().type, ast.right().type)) &&
                (!typeManager.isAssignable(ast.right().type, ast.left().type))) {
            throw new SemanticFailure(SemanticFailure.Cause.TYPE_ERROR);
        }

        if (typeManager.isAssignable(ast.left().type, ast.right().type)) {
            ast.type = ast.left().type;
        } else {
            ast.type = ast.right().type;
        }


        switch (ast.operator) {
            case B_PLUS:
            case B_MINUS:
            case B_MOD:
            case B_TIMES:
            case B_DIV:
                if (!typeManager.isAssignable(PrimitiveTypeSymbol.intType, ast.type)) {
                    throw new SemanticFailure(SemanticFailure.Cause.TYPE_ERROR);
                }
                break;
            case B_AND:
            case B_OR:
                if (!typeManager.isAssignable(PrimitiveTypeSymbol.booleanType, ast.type)) {
                    throw new SemanticFailure(SemanticFailure.Cause.TYPE_ERROR);
                }
                break;
            case B_GREATER_OR_EQUAL:
            case B_LESS_OR_EQUAL:
            case B_GREATER_THAN:
            case B_LESS_THAN:
                if (!typeManager.isAssignable(PrimitiveTypeSymbol.intType, ast.type)) {
                    throw new SemanticFailure(SemanticFailure.Cause.TYPE_ERROR);
                }
                ast.type = PrimitiveTypeSymbol.booleanType;
                break;
            case B_NOT_EQUAL:
            case B_EQUAL:
                ast.type = PrimitiveTypeSymbol.booleanType;
                break;
            default:
                break;
        }

        return null;
    }

    @Override
    public Void booleanConst(Ast.BooleanConst ast, CurrentContext arg) {
        ast.type = PrimitiveTypeSymbol.booleanType;
        return null;
    }

    @Override
    public Void intConst(Ast.IntConst ast, CurrentContext arg) {
        ast.type = PrimitiveTypeSymbol.intType;
        return null;
    }

    @Override
    public Void nullConst(Ast.NullConst ast, CurrentContext arg) {
        ast.type = ClassSymbol.nullType;
        return null;
    }

    @Override
    public Void builtInRead(Ast.BuiltInRead ast, CurrentContext arg) {
        ast.type = PrimitiveTypeSymbol.intType;
        if (ast.children().size() != 0) {
            throw new SemanticFailure(SemanticFailure.Cause.WRONG_NUMBER_OF_ARGUMENTS);
        }
        return null;
    }

    @Override
    public Void unaryOp(Ast.UnaryOp ast, CurrentContext arg) {
        visit(ast.arg(), arg);
        switch (ast.operator) {
            case U_PLUS:
            case U_MINUS:
                ast.type = PrimitiveTypeSymbol.intType;
                if (!typeManager.isAssignable(ast.type, ast.arg().type)) {
                    throw new SemanticFailure(SemanticFailure.Cause.TYPE_ERROR);
                }
                break;
            case U_BOOL_NOT:
                ast.type = PrimitiveTypeSymbol.booleanType;
                if (!typeManager.isAssignable(ast.type, ast.arg().type)) {
                    throw new SemanticFailure(SemanticFailure.Cause.TYPE_ERROR);
                }
                break;
            default:
                break;
        }

        return null;
    }

    @Override
    public Void var(Ast.Var ast, CurrentContext arg) {
        if (arg.getMethodSymbol().locals.containsKey(ast.name)) {
            ast.type = arg.getMethodSymbol().locals.get(ast.name).type;
            return null;
        } else {
            for (Symbol.VariableSymbol symbol : arg.getMethodSymbol().parameters) {
                if (symbol.name.equals(ast.name)) {
                    ast.type = symbol.type;
                    return null;
                }
            }

            ClassSymbol current = arg.getClassSymbol();
            while (current != ClassSymbol.objectType) {
                if (current.fields.containsKey(ast.name)) {
                    ast.type = current.fields.get(ast.name).type;
                    return null;
                }
                current = current.superClass;
            }
        }
        throw new SemanticFailure(SemanticFailure.Cause.NO_SUCH_VARIABLE);
    }

    @Override
    public Void cast(Ast.Cast ast, CurrentContext arg) {
        visit(ast.arg(), arg);
        ast.type = typeManager.stringToTypeSymbol(ast.typeName);
        if (!typeManager.isAssignable(ast.type, ast.arg().type)) {
            throw new SemanticFailure(SemanticFailure.Cause.TYPE_ERROR);
        }
        return null;
    }

    @Override
    public Void field(Ast.Field ast, CurrentContext arg) {
        if (ast.arg() == null) {
            ClassSymbol current = arg.getClassSymbol();
            while (current != ClassSymbol.objectType) {
                if (current.fields.containsKey(ast.fieldName)) {
                    ast.type = current.fields.get(ast.fieldName).type;
                    return null;
                }
                current = current.superClass;
            }
            throw new SemanticFailure(SemanticFailure.Cause.NO_SUCH_FIELD);
        } else {
            visit(ast.arg(), arg);
            if (!(ast.arg().type instanceof ClassSymbol) || ast.arg().type instanceof PrimitiveTypeSymbol || ast.arg().type instanceof ArrayTypeSymbol) {
                throw new SemanticFailure(SemanticFailure.Cause.TYPE_ERROR);
            }
            ClassSymbol current = (ClassSymbol) ast.arg().type;
            while (current != ClassSymbol.objectType) {
                if (current.fields.containsKey(ast.fieldName)) {
                    ast.type = current.fields.get(ast.fieldName).type;
                    return null;
                }
                current = current.superClass;
            }
            throw new SemanticFailure(SemanticFailure.Cause.NO_SUCH_FIELD);
        }


    }

    @Override
    public Void newObject(Ast.NewObject ast, CurrentContext arg) {
        if (!typeManager.isAvailableType(ast.typeName)) {
            throw new SemanticFailure(SemanticFailure.Cause.NO_SUCH_TYPE);
        }
        ast.type = typeManager.getClassSymbol(ast.typeName);
        return null;
    }

    @Override
    public Void newArray(Ast.NewArray ast, CurrentContext arg) {
        visit(ast.arg(), arg);
        if (!ast.arg().type.equals(PrimitiveTypeSymbol.intType)) {
            throw new SemanticFailure(SemanticFailure.Cause.TYPE_ERROR);
        }
        if (!typeManager.getTypes().contains(new ClassSymbol(ast.typeName))) {
            throw new SemanticFailure(SemanticFailure.Cause.NO_SUCH_TYPE);
        }
        return null;
    }

    @Override
    public Void index(Ast.Index ast, CurrentContext arg) {
        // left array, right index
        visitChildren(ast, arg);
        if (!(ast.left().type instanceof ArrayTypeSymbol)) {
            throw new SemanticFailure(SemanticFailure.Cause.TYPE_ERROR);
        }

        ast.type = ((ArrayTypeSymbol) ast.left().type).elementType;

        if (!typeManager.isAssignable(PrimitiveTypeSymbol.intType, ast.right().type)) {
            throw new SemanticFailure(SemanticFailure.Cause.TYPE_ERROR);
        }

        return null;
    }

    @Override
    public Void thisRef(Ast.ThisRef ast, CurrentContext arg) {
        ast.type = arg.getClassSymbol();
        return null;
    }
}
