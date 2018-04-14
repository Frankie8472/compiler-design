package cd.frontend.semantic;

import cd.ir.Symbol;

/**
 * This class is a container for the current context. The Current context is the current scope in which variables and
 * methods and fields are available.
 */
public final class CurrentContext {
    /**
     * The Class this context is in.
     */
    private final Symbol.ClassSymbol classSymbol;
    /**
     * The Method this context is in. If this is null, the scope is the entire class and not in a method.
     */
    private final Symbol.MethodSymbol methodSymbol;

    public CurrentContext(CurrentContext context, Symbol.MethodSymbol methodSymbol){
        this.classSymbol = context.classSymbol;
        this.methodSymbol = methodSymbol;
    }

    public CurrentContext(Symbol.ClassSymbol classSymbol){
        this.classSymbol = classSymbol;
        this.methodSymbol = null;
    }

    public CurrentContext(Symbol.ClassSymbol classSymbol, Symbol.MethodSymbol methodSymbol){
        this.classSymbol = classSymbol;
        this.methodSymbol = methodSymbol;
    }

    public Symbol.MethodSymbol getMethodSymbol() {
        return methodSymbol;
    }

    public Symbol.ClassSymbol getClassSymbol() {
        return classSymbol;
    }
}