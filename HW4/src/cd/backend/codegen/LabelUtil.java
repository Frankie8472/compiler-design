package cd.backend.codegen;

public class LabelUtil {

    public static String generateMethodTableLabelName(String className){
        return className + "_method_table";
    }

    public static String generateMethodLabelName(String className, String methodName){
        return className + "_" + methodName;
    }

    public static String generateLocalLabelName(String varName, CurrentContext arg){
        return arg.getClassSymbol().name + "_" + arg.getMethodSymbol().name + "_" + varName;
    }

    public static String generateArrayLabelName(String className) {
        return className + "_array_table";
    }
}
