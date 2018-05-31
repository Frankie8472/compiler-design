package cd.backend.codegen;

import cd.ir.Ast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
     * Known Arraybounds for the local variables that are Arrays.
     */
    private final Map<String, List<String>> knownLocalArrayVarAccess;

    /**
     * Known Arraybounds for the fields of the current class that are Arrays.
     */
    private final Map<String, Integer> knownFieldArrayBounds;

    /**
     * Known Objects not null.
     */
    private final List<String> knownObjects = new ArrayList<>();

    /**
     * Copies the class and other properties from the given Context and sets the new methodDecl.
     *
     * @param context    Context to copy from
     * @param methodDecl The new MethodDecl of the new context.
     */
    public CurrentContext(CurrentContext context, Ast.MethodDecl methodDecl) {
        this(context.getClassDecl(), methodDecl);
        this.knownFieldArrayBounds.putAll(context.knownFieldArrayBounds);
        this.knownObjects.clear();
    }

    /**
     * Creates a new CurrentContext for a class. This context is not inside a method.
     *
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
        this.knownLocalArrayVarAccess = new HashMap<>();
        this.knownObjects.clear();
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

    public boolean isKnownObjectAccess(String varName) {
        return this.knownObjects.contains(varName);
    }

    public void removeObjectAccess(String varName) {
        this.knownObjects.remove(varName);
    }

    public void addObjectAccess(String varName) {
        this.knownObjects.add(varName);
    }

    public boolean isKnownArrayAccess(String array, int offset) {
        if (knownLocalArrayBounds.get(array) != null) {
            if (offset >= 0 && offset <= knownLocalArrayBounds.get(array)) {
                return true;
            } else {
                knownLocalArrayBounds.put(array, offset);
                return false;
            }

        }
        return false;
    }

    public void removeAccessesToArray(String array) {
        knownLocalArrayBounds.remove(array);
        knownLocalArrayVarAccess.remove(array);
    }

    public void removeAccessFromArray(String array, String var) {
        if (knownLocalArrayVarAccess.get(array) != null) {
            knownLocalArrayVarAccess.get(array).remove(var);
        } else {
            knownLocalArrayVarAccess.put(array, new ArrayList<>());
        }
    }

    public boolean isKnownArrayAccess(String array, String var) {
        if (knownLocalArrayVarAccess.get(array) != null) {
            if (knownLocalArrayVarAccess.get(array).contains(var)) {
                return true;
            }
        } else {
            knownLocalArrayVarAccess.put(array, new ArrayList<>());
        }
        knownLocalArrayVarAccess.get(array).add(var);
        return false;
    }
}