package cd.backend.codegen;

import cd.Config;
import cd.ir.Symbol;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static cd.backend.codegen.AstCodeGenerator.METHOD_PREFIX;
import static cd.backend.codegen.AstCodeGenerator.VTABLE_PREFIX;

public class VTable {

    private final String name;
    private final int size;
    private final VTable parent;
    private Map<Symbol.MethodSymbol, Integer> offsets = new HashMap<>();


    public VTable(VTable parent, Symbol.ClassSymbol symbol) {
        this.parent = parent;
        this.name = symbol.name;

        AtomicInteger offset = new AtomicInteger(((parent != null) ? parent.size : 0) + Config.SIZEOF_PTR);
        symbol.methods.values().forEach(method -> {
            Integer parentOffset;

            if (parent != null && (parentOffset = parent.getOffset(method.name)) != null) {
                this.offsets.put(method, parentOffset);
            } else {
                this.offsets.put(method, offset.getAndAdd(Config.SIZEOF_PTR));
            }
        });
        this.size = offset.get();
    }

    public Integer getOffset(String name) {
        Optional<Integer> offset = this.offsets.entrySet().stream()
                .filter(entry -> entry.getKey().name.equals(name))
                .map(Map.Entry::getValue)
                .findFirst();

        if (offset.isPresent()) {
            return offset.get();
        } else if (this.parent != null) {
            return this.parent.getOffset(name);
        } else {
            return null;
        }
    }

    public String getLabel() {
        return VTABLE_PREFIX + this.name;
    }

    public void emit(AssemblyEmitter emit) {
        emit.emitLabel(this.getLabel());
        emit.increaseIndent("Emitting VTable " + this.name);



        this.getOffsets().entrySet().stream()
                .sorted(Comparator.comparingInt(Map.Entry::getKey))
                .map(Map.Entry::getValue)
                .forEach(emit::emitConstantData);

        emit.decreaseIndent();
    }

    private Map<Integer, String> getOffsets() {
        Map<Integer, String> map;

        if (this.parent != null) {
            map = this.parent.getOffsets();
            map.put(parent.size, VTABLE_PREFIX + this.name);
        } else {
            map = new HashMap<>();
            map.put(0, VTABLE_PREFIX + this.name);
        }

        this.offsets.entrySet().forEach(entry -> map.put(entry.getValue(), METHOD_PREFIX + this.name + "_" + entry.getKey().name));

        return map;

    }

    public int getParentSize() {
        return (this.parent != null) ? this.parent.size : 0;
    }

}
