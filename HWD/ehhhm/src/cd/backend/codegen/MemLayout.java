package cd.backend.codegen;

import cd.Config;
import cd.ir.Symbol;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class MemLayout {

    public final int size;
    private final VTable vTable;
    private final MemLayout parent;
    private Map<String, Integer> offsets = new HashMap<>();


    public MemLayout(MemLayout parent, VTable vTable, Symbol.ClassSymbol symbol) {
        this.parent = parent;
        this.vTable = vTable;

        AtomicInteger offset = new AtomicInteger((parent != null) ? parent.size : Config.SIZEOF_PTR);
        symbol.fields.values().forEach(var -> this.offsets.put(var.name, offset.getAndAdd(Config.SIZEOF_PTR)));
        this.size = offset.get();
    }

    public Integer getOffset(String name) {
        Optional<Integer> offset = this.offsets.entrySet().stream()
                .filter(entry -> entry.getKey().equals(name))
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
}
