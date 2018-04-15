package cd.frontend.semantic;

import cd.ir.Symbol.MethodSymbol;
import cd.ir.Symbol.ClassSymbol;

/**
 * This class is a container for the current context. The Current context is the current scope in which variables and
 * methods and fields are available.
 */
public final class CurrentContext {
    /**
     * The Class this context is in.
     */
    private final ClassSymbol classSymbol;
    /**
     * The Method this context is in. If this is null, the scope is the entire class and not in a method.
     */
    private final MethodSymbol methodSymbol;

    private Boolean correctReturn;

    public CurrentContext(CurrentContext context, MethodSymbol methodSymbol) {
        this.classSymbol = context.getClassSymbol();
        this.methodSymbol = methodSymbol;
        this.correctReturn = false;

    }

    public CurrentContext(ClassSymbol classSymbol) {
        this.classSymbol = classSymbol;
        this.methodSymbol = null;
        this.correctReturn = false;

    }

    public CurrentContext(ClassSymbol classSymbol, MethodSymbol methodSymbol) {
        this.classSymbol = classSymbol;
        this.methodSymbol = methodSymbol;
        this.correctReturn = false;

    }

    public MethodSymbol getMethodSymbol() {
        return methodSymbol;
    }

    public ClassSymbol getClassSymbol() {
        return classSymbol;
    }

    public Boolean getCorrectReturn(){
        return correctReturn;
    }

    public void setCorrectReturn(Boolean correctReturn) {
        this.correctReturn = correctReturn;
    }
}