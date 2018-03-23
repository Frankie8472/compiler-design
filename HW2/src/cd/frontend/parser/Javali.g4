grammar Javali; // parser grammar, parses streams of tokens

@header {
	// Java header
	//package cd.frontend.parser;
}



// PARSER RULES

//* // TODO: declare appropriate parser rules
//* // NOTE: Remove //* from the beginning of each line.
//* 
unit
    :	classDecl+  EOF
    ;

classDecl
    : 'class' Identifier ( 'extends' Identifier )? '{' memberList '}'
    ;

memberList
    : ( varDecl | methodDecl )*
    ;
varDecl
    : type Identifier ( ',' Identifier )* ';'
    ;
methodDecl
    : ( type | 'void' ) Identifier '(' ( formalParamList )? ')' '{' varDecl*  stmt* '}'
    ;
formalParamList
    : type Identifier ( ',' type Identifier )*
    ;

stmt
    : assignmentStmt
    | methodCallStmt
    | ifStmt
    | whileStmt
    | returnStmt
    | writeStmt
    ;

stmtBlock
    : '{' stmt* '}'
    ;

methodCallStmt
    : methodCallExpr ';'
    ;
assignmentStmt
    : identifierAccess '=' ( expr | newExpr | readExpr ) ';'
    ;
writeStmt
    : ( 'write' '(' expr ')' | 'writeln' '(' ')' ) ';'
    ;
ifStmt
    : 'if' '(' expr ')' stmtBlock ( 'else' stmtBlock )?
    ;
whileStmt
    : 'while' '(' expr ')' stmtBlock
    ;
returnStmt
    : 'return' ( expr )? ';'
    ;



// expressions
newExpr
    : 'new' ( Identifier '(' ')' | Identifier '[' expr ']'| PrimitiveType '[' expr ']' )
    ;
readExpr
    : 'read' '(' ')'
    ;
methodCallExpr
    : Identifier '(' ( actualParamList )? ')'
    | identifierAccess '.' Identifier '(' ( actualParamList )? ')'
    ;
actualParamList
    : expr ( ',' expr )*
    ;
identifierAccess
    : Identifier
    | 'this'
    | identifierAccess '.' Identifier
    | identifierAccess '[' expr ']'
    | Identifier '(' ( actualParamList )? ')'
    | identifierAccess '.' Identifier '(' ( actualParamList )? ')'
    ;

expr
    : literal
    | identifierAccess
    | '(' expr ')'
    //| ( '+' | '-' | '!') expr // TODO: Fix unary ops
    | '(' referenceType ')' expr
    | expr BinaryOp expr
    ;

//Operators

BinaryOp
    : (MultOp | AddOp | CompOp | EqOp | AndOp | OrOp)
    ;

fragment
MultOp
    : ('*' | '/' | '%')
    ;
fragment
AddOp
    : ('+' | '-')
    ;
fragment
CompOp
    : ('<' | '<=' | '>' | '>=')
    ;
fragment
EqOp
    : ('==' | '!=')
    ;
fragment
AndOp
    : '&&'
    ;
fragment
OrOp
    : '||'
    ;

// LEXER RULES
// TODO: provide appropriate lexer rules for numbers and boolean literals



//Literals

// types
type
    : PrimitiveType | referenceType
    ;
PrimitiveType
    : 'boolean' | 'int'
    ;
referenceType
    : Identifier | arrayType
    ;
arrayType
    : Identifier '[' ']'  |  PrimitiveType '[' ']'
    ;




Integer
    : Decimal
    | Hex
    ;
fragment
Decimal
    : '0'
    | '1'..'9' Digit*
    ;
fragment
Hex
    : ('0x' | '0X') HexDigit+
    ;


Boolean
    : 'false'
    | 'true'
    ;

Identifier
    : Letter ( Letter | Digit )*
    ;

literal
    : 'null'
    | Boolean
    | Integer
    ;


// Java(li) identifiers:
//Identifier 
//	:	Letter (Letter|Digit)*
//	;

fragment
Letter
	:	'A'..'Z'
	|	'a'..'z'
	;

fragment
Digit
    : '0'..'9'
    ;

fragment
HexDigit
    : Digit
    | 'a'..'f'
    | 'A'..'F'
    ;

// comments and white space does not produce tokens:
COMMENT
	:	'/*' .*? '*/' -> skip
	;

LINE_COMMENT
	:	'//' ~('\n'|'\r')* -> skip
	;

WS
	:	(' '|'\r'|'\t'|'\n')+ -> skip
	;


// handle characters which failed to match any other token
ErrorCharacter : . ;
