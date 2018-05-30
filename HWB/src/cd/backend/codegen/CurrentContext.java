package cd.backend.codegen;

import cd.ir.Ast;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is a container for the current context. The Current context is the current scope in which variables and
 * methods and fields are available.
 */
public final class CurrentContext {
    /**
     * The Class this context is in.
     */
    private final Ast.ClassDecl classDecl;
    /**
     * The Method this context is in. If this is null, the scope is the entire class and not in a method.
     */
    private final Ast.MethodDecl methodDecl;


    /**
     * Known Arraybounds for the local variables that are Arrays.
     */
    private final Map<String, Integer> knownLocalArrayBounds;

    /**
     * Known Arraybounds for the fields of the current class that are Arrays.
     */
    private final Map<String, Integer> knownFieldArrayBounds;


    /**
     * Copies the class and other properties from the given Context and sets the new methodDecl.
     * @param context Context to copy from
     * @param methodDecl The new MethodDecl of the new context.
     */
    public CurrentContext(CurrentContext context, Ast.MethodDecl methodDecl) {
        this(context.getClassDecl(), methodDecl);
        this.knownFieldArrayBounds.putAll(context.knownFieldArrayBounds);
    }

    /**
     * Creates a new CurrentContext for a class. This context is not inside a method.
     * @param classDecl Class for the context
     */
    public CurrentContext(Ast.ClassDecl classDecl) {
        this(classDecl, null);
    }

    private CurrentContext(Ast.ClassDecl classDecl, Ast.MethodDecl methodDecl) {
        this.classDecl = classDecl;
        this.methodDecl = methodDecl;
        this.knownLocalArrayBounds = new HashMap<>();
        this.knownFieldArrayBounds = new HashMap<>();
    }

    /**
     * Fetches the current MethodSymbol
     *
     * @return the current MethodSymbol
     */
    public Ast.MethodDecl getMethodDecl() {
        return this.methodDecl;
    }

    /**
     * Fetches the current ClassSymbol
     *
     * @return the current ClassSymbol
     */
    public Ast.ClassDecl getClassDecl() {
        return this.classDecl;
    }



}