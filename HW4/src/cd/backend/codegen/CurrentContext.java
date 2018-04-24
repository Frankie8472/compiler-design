package cd.backend.codegen;

import cd.Config;
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

    private Integer currentLocalOffset;

    private Integer currentParameterOffset;

    /**
     * This HashMap maps the locals and parameters to the offset of the EBP of the current stack AFTER initializing the new method.
     * Meaning after pushing ebp and mov esp ebp.
     * Negative number is for locals and positive for
     */
    public Map<String, Integer> parameters_and_locals;


    public CurrentContext(CurrentContext context, MethodSymbol methodSymbol) {
        this(context.getClassSymbol(), methodSymbol);
    }

    public CurrentContext(ClassSymbol classSymbol) {
        this(classSymbol, null);
    }

    public CurrentContext(ClassSymbol classSymbol, MethodSymbol methodSymbol) {
        this.classSymbol = classSymbol;
        this.methodSymbol = methodSymbol;
        this.parameters_and_locals = new HashMap<>();
        this.currentLocalOffset = -Config.SIZEOF_PTR;
        // Skip return address and pushed base pointer
        this.currentParameterOffset = Config.SIZEOF_PTR * 2;
    }

    /**
     * Fetches the current MethodSymbol
     *
     * @return the current MethodSymbol
     */
    public MethodSymbol getMethodSymbol() {
        return this.methodSymbol;
    }

    /**
     * Fetches the current ClassSymbol
     *
     * @return the current ClassSymbol
     */
    public ClassSymbol getClassSymbol() {
        return this.classSymbol;
    }

    /**
     * Add a parameter to the hashmap
     * with the offset: current - 4 of the %EBP
     * and updates current: current -= 4
     *
     * @param name of the parameter as string
     * @return
     */
    public void addParameter(String name) {
        this.parameters_and_locals.put(name, this.currentParameterOffset);
        this.currentParameterOffset += Config.SIZEOF_PTR;
    }

    /**
     * Add a local to the hashmap
     * with the offset: current + 4 of the %EBP
     * and updates current: current += 4
     *
     * @param name of the local as string
     * @return nothing
     */
    public void addLocal(String name) {
        this.parameters_and_locals.put(name, this.currentLocalOffset);
        this.currentLocalOffset -= Config.SIZEOF_PTR;
    }

    /**
     * Returns the offset mapped to the given name
     *
     * @param name of the local or parameter as a string
     * @return offset in bytes from %EBP, use AssemblyEmitter.RegisterOffset()
     */
    public Integer getOffset(String name) {
        return this.parameters_and_locals.get(name);
    }
}