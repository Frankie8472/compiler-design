package cd.backend.codegen;

import cd.Config;
import cd.ir.Symbol;
import org.antlr.v4.misc.OrderedHashMap;

import java.util.*;

public class VTable {

    private Map<String, String> methods = new LinkedHashMap<>();

    private List<String> fields = new ArrayList<>();
    private int fieldOffset = Config.SIZEOF_PTR;

    private String className;
    private String superClassName;

    public VTable(Symbol.ClassSymbol classSymbol){
        className = classSymbol.name;
        if(classSymbol.superClass != null) {

            List<Symbol.ClassSymbol> classList = new ArrayList<>();

            superClassName = classSymbol.superClass.name;

            Symbol.ClassSymbol currentSymbol = classSymbol;

            while (currentSymbol.superClass != null) {
                classList.add(currentSymbol);
                currentSymbol = currentSymbol.superClass;
            }

            Collections.reverse(classList);

            for (Symbol.ClassSymbol currentClass : classList) {

                for (Symbol.VariableSymbol field : currentClass.fields.values()) {
                    fields.add(field.name);
                }

                for (Symbol.MethodSymbol method : currentClass.methods.values()) {
                    methods.put(method.name, currentClass.name);
                }
            }
        }
    }

    public Integer getMethodOffset(String methodName){
        if(!methods.containsKey(methodName))
            return null;
        return ((new ArrayList<>(methods.keySet())).indexOf(methodName) + 1) * Config.SIZEOF_PTR;
    }

    public Integer getFieldOffset(String fieldName){
        if(!fields.contains(fieldName))
            return null;
        return (fields.lastIndexOf(fieldName) + 1) * Config.SIZEOF_PTR;
    }

    public Integer getOffset(String varName) {

        if (getMethodOffset(varName) != null){
            return getMethodOffset(varName);
        }

        return getFieldOffset(varName);
    }

    public Integer getFullSize(){
        return (fields.size() + 1) * Config.SIZEOF_PTR;
    }

    public Integer getFieldCount(){
        return fields.size() + 1;
    }


    public void emitStaticMethodVTable(AssemblyEmitter emitter){
        /*
        An example how the Table may look like
          A_methodtable:
            .int $super_methodtable
            .int $A_my_Method
            .int $A_my_Method2
            .int $A_my_Method3
            .int $A_my_Method4
         */
        emitter.emitRaw(Config.DATA_INT_SECTION);
        emitter.emitRaw(".align 4");
        emitter.emitLabel(LabelUtil.generateMethodTableLabelName(this.className));

        if(superClassName != null)
            emitter.emitConstantData(LabelUtil.generateMethodTableLabelName(superClassName));
        else
            emitter.emitConstantData("0");


        for(String methodName : methods.keySet()){
            emitter.emitConstantData(LabelUtil.generateMethodLabelName(methods.get(methodName), methodName));
        }

        emitter.emitRaw(Config.DATA_INT_SECTION);
        emitter.emitRaw(".align 4");
        emitter.emitLabel(LabelUtil.generateArrayLabelName(this.className));
        emitter.emitConstantData(LabelUtil.generateMethodTableLabelName(Symbol.ClassSymbol.objectType.name));

    }



}
