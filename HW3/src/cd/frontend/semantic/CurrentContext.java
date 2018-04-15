package cd.frontend.semantic;

import cd.ir.Symbol.MethodSymbol;
import cd.ir.Symbol.ClassSymbol;

import java.util.ArrayList;
import java.util.List;

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

    /**
     * True, if the current MethodSymbol has a return statement in all paths if not void
     * If void, must be false
     */
    private Boolean correctReturn;

    private List<Boolean> insideIfStmt = new ArrayList<>();

    private Boolean isThereAnIfStmt;

    public CurrentContext(CurrentContext context, MethodSymbol methodSymbol) {
        this.classSymbol = context.getClassSymbol();
        this.methodSymbol = methodSymbol;
        this.correctReturn = false;
        this.isThereAnIfStmt = false;
    }

    public CurrentContext(ClassSymbol classSymbol) {
        this.classSymbol = classSymbol;
        this.methodSymbol = null;
        this.correctReturn = false;
        this.isThereAnIfStmt = false;

    }

    public CurrentContext(ClassSymbol classSymbol, MethodSymbol methodSymbol) {
        this.classSymbol = classSymbol;
        this.methodSymbol = methodSymbol;
        this.correctReturn = false;
        this.isThereAnIfStmt = false;

    }

    public MethodSymbol getMethodSymbol() {
        return methodSymbol;
    }

    public ClassSymbol getClassSymbol() {
        return classSymbol;
    }

    public boolean getCorrectReturn(){
        return correctReturn;
    }

    public Boolean getInsideIfStmt(){
        return insideIfStmt.get(insideIfStmt.size()-1);
    }

    public boolean getIsThereAnIfStmt(){
        return isThereAnIfStmt;
    }

    public void setCorrectReturn(Boolean correctReturn) {
        this.correctReturn = correctReturn;
    }

    public void setInsideIfStmt(Boolean insideIfStmt) {
        this.insideIfStmt.add(insideIfStmt);
    }

    public void setInsideIfStmt() {
        this.insideIfStmt.remove(insideIfStmt.size()-1);
    }

    public void setIsThereAnIfStmt(Boolean isThereAnIfStmt) {
        this.isThereAnIfStmt = isThereAnIfStmt;
    }
}