package cd.backend.codegen;

import cd.ir.Ast;
import cd.ir.Symbol;

public class ClassContext {

    Ast.ClassDecl ast;
    Symbol.ClassSymbol symbol;
    VTable vTable;
    MemLayout memLayout;

    public ClassContext(Ast.ClassDecl ast, VTable vTable, MemLayout memLayout) {
        this.ast = ast;
        this.symbol = ast.sym;
        this.vTable = vTable;
        this.memLayout = memLayout;
    }

}
