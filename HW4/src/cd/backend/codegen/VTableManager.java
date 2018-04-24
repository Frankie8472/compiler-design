package cd.backend.codegen;

public class VTableManager { // TODO: Rename!!

    public static String generateMethodTableLabelName(String className){
        return className + "_method_table";
    }

    public static String generateMethodLabelName(String className, String methodName){
        return className + "_" + methodName;
    }

}
