package cd.frontend.semantic;

import cd.ir.Symbol;

class SymbolWrapper{
    public final Symbol symbol;
    public final SymbolWrapper parentSymbol;

    public SymbolWrapper(Symbol symbol, SymbolWrapper parentSymbol){
        this.symbol = symbol;
        this.parentSymbol = parentSymbol;
    }
}