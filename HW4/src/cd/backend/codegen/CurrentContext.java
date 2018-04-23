package cd.backend.codegen;

import cd.ToDoException;
import cd.ir.Symbol.MethodSymbol;
import cd.ir.Symbol.ClassSymbol;

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
    private final ClassSymbol classSymbol;
    /**
     * The Method this context is in. If this is null, the scope is the entire class and not in a method.
     */
    private final MethodSymbol methodSymbol;

    /**
     * This HashMap maps the locals and parameters to the offset of the EBP of the current stack AFTER initializing the new method.
     * Meaning after pushing ebp and mov esp ebp.
     * Negative number is for locals and positive for
     */
    private Map<String, Integer> parameters_and_locals = new HashMap<>();

    public CurrentContext(CurrentContext context, MethodSymbol methodSymbol) {
        this.classSymbol = context.getClassSymbol();
        this.methodSymbol = methodSymbol;
    }

    public CurrentContext(ClassSymbol classSymbol) {
        this.classSymbol = classSymbol;
        this.methodSymbol = null;
    }

    public CurrentContext(ClassSymbol classSymbol, MethodSymbol methodSymbol) {
        this.classSymbol = classSymbol;
        this.methodSymbol = methodSymbol;
    }

    /**
     * Fetches the current MethodSymbol
     * @return the current MethodSymbol
     */
    public MethodSymbol getMethodSymbol() {
        return methodSymbol;
    }

    /**
     * Fetches the current ClassSymbol
     * @return the current ClassSymbol
     */
    public ClassSymbol getClassSymbol() {
        return classSymbol;
    }

    /**
     * Add a parameter to the hashmap
     * @param name of the parameter as string
     * @param offset in bytes from the %EBP
     * @return
     * @throws Error if parameter is mapped to the current stack
     */
    public Void addParameter(String name, Integer offset) {
        if (offset > -8) {
            throw new ToDoException(); // todo: choose right exception
        }

        parameters_and_locals.put(name, offset);
        return null;
    }

    /**
     * Add a local to the hashmap
     * @param name of the local as string
     * @param offset in bytes from the %EBP
     * @return
     * @throws Error if parameter is not mapped to the current stack
     */
    public Void addLocal(String name, Integer offset){
        if(offset < 4) { // 4 -> 1 if boolean is handled as 1 byte not as 4
            throw new ToDoException(); // todo: choose right exception
        }

        parameters_and_locals.put(name, offset);
        return null;
    }

    /**
     * Returns the offset mapped to the given name
     * @param name of the local or parameter as a string
     * @return offset in bytes from %EBP, use AssemblyEmitter.RegisterOffset()
     */
    public Integer getOffset(String name){
        return parameters_and_locals.get(name);
    }
}