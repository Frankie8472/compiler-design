package cd.backend.codegen;

import cd.Config;
import cd.ir.Symbol;

import java.util.*;

public class VTable {

    private List<String> methods = new ArrayList<>();

    private List<String> fields = new ArrayList<>();
    private int fieldOffset = Config.SIZEOF_PTR;

    private String className;
    private String superClassName;

    public VTable(Symbol.ClassSymbol classSymbol){
        className = classSymbol.name;
        superClassName = classSymbol.superClass.name;
        Symbol.ClassSymbol currentSymbol = classSymbol;

        while (currentSymbol.superClass != null) {
            for (Symbol.VariableSymbol field : currentSymbol.fields.values()) {
                fields.add(field.name);
            }
            currentSymbol = currentSymbol.superClass;
        }
        Collections.reverse(fields);

        for (Symbol.MethodSymbol method : classSymbol.methods.values()) {
            methods.add(method.name);
        }
    }

    public Integer getMethodOffset(String methodName){
        if(!methods.contains(methodName))
            return null;
        return (methods.indexOf(methodName) + 1) * Config.SIZEOF_PTR;
    }

    public Integer getFieldOffset(String fieldName){
        if(!fields.contains(fieldName))
            return null;
        return (fields.lastIndexOf(fieldName) + 1) * Config.SIZEOF_PTR;
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
        emitter.emitRaw(Config.TEXT_SECTION);
        emitter.emitLabel(LabelUtil.generateMethodTableLabelName(this.className));

        if(!superClassName.equals("Object"))
            emitter.emitConstantData(LabelUtil.generateMethodTableLabelName(superClassName));
        else
            emitter.emitConstantData("0");


        for(String methodName : methods){
            emitter.emitConstantData(LabelUtil.generateMethodLabelName(this.className, methodName));
        }
    }



}
