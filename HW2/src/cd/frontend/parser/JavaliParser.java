// Generated from /run/media/Josua/Daten/Josua/Uni/6. Semester/Compiler Design/compiler-design/HW2/src/cd/frontend/parser/Javali.g4 by ANTLR 4.7
package cd.frontend.parser;

	// Java header
	 //package cd.frontend.parser; // TODO: Remove before commit!!

import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class JavaliParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.7", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		T__9=10, T__10=11, T__11=12, T__12=13, T__13=14, T__14=15, T__15=16, T__16=17, 
		T__17=18, T__18=19, T__19=20, T__20=21, T__21=22, T__22=23, T__23=24, 
		T__24=25, T__25=26, T__26=27, T__27=28, T__28=29, T__29=30, T__30=31, 
		T__31=32, T__32=33, T__33=34, T__34=35, T__35=36, T__36=37, PrimitiveType=38, 
		Integer=39, Boolean=40, Identifier=41, COMMENT=42, LINE_COMMENT=43, WS=44, 
		ErrorCharacter=45;
	public static final int
		RULE_unit = 0, RULE_classDecl = 1, RULE_memberList = 2, RULE_varDecl = 3, 
		RULE_methodDecl = 4, RULE_formalParamList = 5, RULE_stmt = 6, RULE_stmtBlock = 7, 
		RULE_methodCallStmt = 8, RULE_assignmentStmt = 9, RULE_writeStmt = 10, 
		RULE_ifStmt = 11, RULE_whileStmt = 12, RULE_returnStmt = 13, RULE_newExpr = 14, 
		RULE_readExpr = 15, RULE_methodCallExpr = 16, RULE_actualParamList = 17, 
		RULE_identifierAccess = 18, RULE_expr = 19, RULE_type = 20, RULE_referenceType = 21, 
		RULE_arrayType = 22, RULE_literal = 23;
	public static final String[] ruleNames = {
		"unit", "classDecl", "memberList", "varDecl", "methodDecl", "formalParamList", 
		"stmt", "stmtBlock", "methodCallStmt", "assignmentStmt", "writeStmt", 
		"ifStmt", "whileStmt", "returnStmt", "newExpr", "readExpr", "methodCallExpr", 
		"actualParamList", "identifierAccess", "expr", "type", "referenceType", 
		"arrayType", "literal"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'class'", "'extends'", "'{'", "'}'", "','", "';'", "'void'", "'('", 
		"')'", "'='", "'write'", "'writeln'", "'if'", "'else'", "'while'", "'return'", 
		"'new'", "'['", "']'", "'read'", "'.'", "'this'", "'+'", "'-'", "'!'", 
		"'*'", "'/'", "'%'", "'<'", "'<='", "'>'", "'>='", "'=='", "'!='", "'&&'", 
		"'||'", "'null'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, null, null, null, null, null, null, null, null, null, 
		null, null, null, null, null, null, null, null, null, null, null, null, 
		null, null, null, null, null, null, null, null, null, null, null, null, 
		null, null, "PrimitiveType", "Integer", "Boolean", "Identifier", "COMMENT", 
		"LINE_COMMENT", "WS", "ErrorCharacter"
	};
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "Javali.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public JavaliParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class UnitContext extends ParserRuleContext {
		public TerminalNode EOF() { return getToken(JavaliParser.EOF, 0); }
		public List<ClassDeclContext> classDecl() {
			return getRuleContexts(ClassDeclContext.class);
		}
		public ClassDeclContext classDecl(int i) {
			return getRuleContext(ClassDeclContext.class,i);
		}
		public UnitContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_unit; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JavaliVisitor ) return ((JavaliVisitor<? extends T>)visitor).visitUnit(this);
			else return visitor.visitChildren(this);
		}
	}

	public final UnitContext unit() throws RecognitionException {
		UnitContext _localctx = new UnitContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_unit);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(49); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(48);
				classDecl();
				}
				}
				setState(51); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==T__0 );
			setState(53);
			match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ClassDeclContext extends ParserRuleContext {
		public List<TerminalNode> Identifier() { return getTokens(JavaliParser.Identifier); }
		public TerminalNode Identifier(int i) {
			return getToken(JavaliParser.Identifier, i);
		}
		public MemberListContext memberList() {
			return getRuleContext(MemberListContext.class,0);
		}
		public ClassDeclContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_classDecl; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JavaliVisitor ) return ((JavaliVisitor<? extends T>)visitor).visitClassDecl(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ClassDeclContext classDecl() throws RecognitionException {
		ClassDeclContext _localctx = new ClassDeclContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_classDecl);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(55);
			match(T__0);
			setState(56);
			match(Identifier);
			setState(59);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__1) {
				{
				setState(57);
				match(T__1);
				setState(58);
				match(Identifier);
				}
			}

			setState(61);
			match(T__2);
			setState(62);
			memberList();
			setState(63);
			match(T__3);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class MemberListContext extends ParserRuleContext {
		public List<VarDeclContext> varDecl() {
			return getRuleContexts(VarDeclContext.class);
		}
		public VarDeclContext varDecl(int i) {
			return getRuleContext(VarDeclContext.class,i);
		}
		public List<MethodDeclContext> methodDecl() {
			return getRuleContexts(MethodDeclContext.class);
		}
		public MethodDeclContext methodDecl(int i) {
			return getRuleContext(MethodDeclContext.class,i);
		}
		public MemberListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_memberList; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JavaliVisitor ) return ((JavaliVisitor<? extends T>)visitor).visitMemberList(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MemberListContext memberList() throws RecognitionException {
		MemberListContext _localctx = new MemberListContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_memberList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(69);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__6) | (1L << PrimitiveType) | (1L << Identifier))) != 0)) {
				{
				setState(67);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,2,_ctx) ) {
				case 1:
					{
					setState(65);
					varDecl();
					}
					break;
				case 2:
					{
					setState(66);
					methodDecl();
					}
					break;
				}
				}
				setState(71);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class VarDeclContext extends ParserRuleContext {
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public List<TerminalNode> Identifier() { return getTokens(JavaliParser.Identifier); }
		public TerminalNode Identifier(int i) {
			return getToken(JavaliParser.Identifier, i);
		}
		public VarDeclContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_varDecl; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JavaliVisitor ) return ((JavaliVisitor<? extends T>)visitor).visitVarDecl(this);
			else return visitor.visitChildren(this);
		}
	}

	public final VarDeclContext varDecl() throws RecognitionException {
		VarDeclContext _localctx = new VarDeclContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_varDecl);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(72);
			type();
			setState(73);
			match(Identifier);
			setState(78);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__4) {
				{
				{
				setState(74);
				match(T__4);
				setState(75);
				match(Identifier);
				}
				}
				setState(80);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(81);
			match(T__5);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class MethodDeclContext extends ParserRuleContext {
		public TerminalNode Identifier() { return getToken(JavaliParser.Identifier, 0); }
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public FormalParamListContext formalParamList() {
			return getRuleContext(FormalParamListContext.class,0);
		}
		public List<VarDeclContext> varDecl() {
			return getRuleContexts(VarDeclContext.class);
		}
		public VarDeclContext varDecl(int i) {
			return getRuleContext(VarDeclContext.class,i);
		}
		public List<StmtContext> stmt() {
			return getRuleContexts(StmtContext.class);
		}
		public StmtContext stmt(int i) {
			return getRuleContext(StmtContext.class,i);
		}
		public MethodDeclContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_methodDecl; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JavaliVisitor ) return ((JavaliVisitor<? extends T>)visitor).visitMethodDecl(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MethodDeclContext methodDecl() throws RecognitionException {
		MethodDeclContext _localctx = new MethodDeclContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_methodDecl);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(85);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case PrimitiveType:
			case Identifier:
				{
				setState(83);
				type();
				}
				break;
			case T__6:
				{
				setState(84);
				match(T__6);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(87);
			match(Identifier);
			setState(88);
			match(T__7);
			setState(90);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==PrimitiveType || _la==Identifier) {
				{
				setState(89);
				formalParamList();
				}
			}

			setState(92);
			match(T__8);
			setState(93);
			match(T__2);
			setState(97);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,7,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(94);
					varDecl();
					}
					} 
				}
				setState(99);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,7,_ctx);
			}
			setState(103);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__10) | (1L << T__11) | (1L << T__12) | (1L << T__14) | (1L << T__15) | (1L << T__21) | (1L << Identifier))) != 0)) {
				{
				{
				setState(100);
				stmt();
				}
				}
				setState(105);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(106);
			match(T__3);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class FormalParamListContext extends ParserRuleContext {
		public List<TypeContext> type() {
			return getRuleContexts(TypeContext.class);
		}
		public TypeContext type(int i) {
			return getRuleContext(TypeContext.class,i);
		}
		public List<TerminalNode> Identifier() { return getTokens(JavaliParser.Identifier); }
		public TerminalNode Identifier(int i) {
			return getToken(JavaliParser.Identifier, i);
		}
		public FormalParamListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_formalParamList; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JavaliVisitor ) return ((JavaliVisitor<? extends T>)visitor).visitFormalParamList(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FormalParamListContext formalParamList() throws RecognitionException {
		FormalParamListContext _localctx = new FormalParamListContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_formalParamList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(108);
			type();
			setState(109);
			match(Identifier);
			setState(116);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__4) {
				{
				{
				setState(110);
				match(T__4);
				setState(111);
				type();
				setState(112);
				match(Identifier);
				}
				}
				setState(118);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class StmtContext extends ParserRuleContext {
		public AssignmentStmtContext assignmentStmt() {
			return getRuleContext(AssignmentStmtContext.class,0);
		}
		public MethodCallStmtContext methodCallStmt() {
			return getRuleContext(MethodCallStmtContext.class,0);
		}
		public IfStmtContext ifStmt() {
			return getRuleContext(IfStmtContext.class,0);
		}
		public WhileStmtContext whileStmt() {
			return getRuleContext(WhileStmtContext.class,0);
		}
		public ReturnStmtContext returnStmt() {
			return getRuleContext(ReturnStmtContext.class,0);
		}
		public WriteStmtContext writeStmt() {
			return getRuleContext(WriteStmtContext.class,0);
		}
		public StmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_stmt; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JavaliVisitor ) return ((JavaliVisitor<? extends T>)visitor).visitStmt(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StmtContext stmt() throws RecognitionException {
		StmtContext _localctx = new StmtContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_stmt);
		try {
			setState(125);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,10,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(119);
				assignmentStmt();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(120);
				methodCallStmt();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(121);
				ifStmt();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(122);
				whileStmt();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(123);
				returnStmt();
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(124);
				writeStmt();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class StmtBlockContext extends ParserRuleContext {
		public List<StmtContext> stmt() {
			return getRuleContexts(StmtContext.class);
		}
		public StmtContext stmt(int i) {
			return getRuleContext(StmtContext.class,i);
		}
		public StmtBlockContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_stmtBlock; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JavaliVisitor ) return ((JavaliVisitor<? extends T>)visitor).visitStmtBlock(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StmtBlockContext stmtBlock() throws RecognitionException {
		StmtBlockContext _localctx = new StmtBlockContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_stmtBlock);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(127);
			match(T__2);
			setState(131);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__10) | (1L << T__11) | (1L << T__12) | (1L << T__14) | (1L << T__15) | (1L << T__21) | (1L << Identifier))) != 0)) {
				{
				{
				setState(128);
				stmt();
				}
				}
				setState(133);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(134);
			match(T__3);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class MethodCallStmtContext extends ParserRuleContext {
		public MethodCallExprContext methodCallExpr() {
			return getRuleContext(MethodCallExprContext.class,0);
		}
		public MethodCallStmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_methodCallStmt; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JavaliVisitor ) return ((JavaliVisitor<? extends T>)visitor).visitMethodCallStmt(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MethodCallStmtContext methodCallStmt() throws RecognitionException {
		MethodCallStmtContext _localctx = new MethodCallStmtContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_methodCallStmt);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(136);
			methodCallExpr();
			setState(137);
			match(T__5);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class AssignmentStmtContext extends ParserRuleContext {
		public IdentifierAccessContext identifierAccess() {
			return getRuleContext(IdentifierAccessContext.class,0);
		}
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public NewExprContext newExpr() {
			return getRuleContext(NewExprContext.class,0);
		}
		public ReadExprContext readExpr() {
			return getRuleContext(ReadExprContext.class,0);
		}
		public AssignmentStmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_assignmentStmt; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JavaliVisitor ) return ((JavaliVisitor<? extends T>)visitor).visitAssignmentStmt(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AssignmentStmtContext assignmentStmt() throws RecognitionException {
		AssignmentStmtContext _localctx = new AssignmentStmtContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_assignmentStmt);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(139);
			identifierAccess(0);
			setState(140);
			match(T__9);
			setState(144);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__7:
			case T__21:
			case T__22:
			case T__23:
			case T__24:
			case T__36:
			case Integer:
			case Boolean:
			case Identifier:
				{
				setState(141);
				expr(0);
				}
				break;
			case T__16:
				{
				setState(142);
				newExpr();
				}
				break;
			case T__19:
				{
				setState(143);
				readExpr();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(146);
			match(T__5);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class WriteStmtContext extends ParserRuleContext {
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public WriteStmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_writeStmt; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JavaliVisitor ) return ((JavaliVisitor<? extends T>)visitor).visitWriteStmt(this);
			else return visitor.visitChildren(this);
		}
	}

	public final WriteStmtContext writeStmt() throws RecognitionException {
		WriteStmtContext _localctx = new WriteStmtContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_writeStmt);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(156);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__10:
				{
				setState(148);
				match(T__10);
				setState(149);
				match(T__7);
				setState(150);
				expr(0);
				setState(151);
				match(T__8);
				}
				break;
			case T__11:
				{
				setState(153);
				match(T__11);
				setState(154);
				match(T__7);
				setState(155);
				match(T__8);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(158);
			match(T__5);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class IfStmtContext extends ParserRuleContext {
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public List<StmtBlockContext> stmtBlock() {
			return getRuleContexts(StmtBlockContext.class);
		}
		public StmtBlockContext stmtBlock(int i) {
			return getRuleContext(StmtBlockContext.class,i);
		}
		public IfStmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ifStmt; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JavaliVisitor ) return ((JavaliVisitor<? extends T>)visitor).visitIfStmt(this);
			else return visitor.visitChildren(this);
		}
	}

	public final IfStmtContext ifStmt() throws RecognitionException {
		IfStmtContext _localctx = new IfStmtContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_ifStmt);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(160);
			match(T__12);
			setState(161);
			match(T__7);
			setState(162);
			expr(0);
			setState(163);
			match(T__8);
			setState(164);
			stmtBlock();
			setState(167);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__13) {
				{
				setState(165);
				match(T__13);
				setState(166);
				stmtBlock();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class WhileStmtContext extends ParserRuleContext {
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public StmtBlockContext stmtBlock() {
			return getRuleContext(StmtBlockContext.class,0);
		}
		public WhileStmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_whileStmt; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JavaliVisitor ) return ((JavaliVisitor<? extends T>)visitor).visitWhileStmt(this);
			else return visitor.visitChildren(this);
		}
	}

	public final WhileStmtContext whileStmt() throws RecognitionException {
		WhileStmtContext _localctx = new WhileStmtContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_whileStmt);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(169);
			match(T__14);
			setState(170);
			match(T__7);
			setState(171);
			expr(0);
			setState(172);
			match(T__8);
			setState(173);
			stmtBlock();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ReturnStmtContext extends ParserRuleContext {
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public ReturnStmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_returnStmt; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JavaliVisitor ) return ((JavaliVisitor<? extends T>)visitor).visitReturnStmt(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ReturnStmtContext returnStmt() throws RecognitionException {
		ReturnStmtContext _localctx = new ReturnStmtContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_returnStmt);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(175);
			match(T__15);
			setState(177);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__7) | (1L << T__21) | (1L << T__22) | (1L << T__23) | (1L << T__24) | (1L << T__36) | (1L << Integer) | (1L << Boolean) | (1L << Identifier))) != 0)) {
				{
				setState(176);
				expr(0);
				}
			}

			setState(179);
			match(T__5);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class NewExprContext extends ParserRuleContext {
		public TerminalNode Identifier() { return getToken(JavaliParser.Identifier, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public TerminalNode PrimitiveType() { return getToken(JavaliParser.PrimitiveType, 0); }
		public NewExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_newExpr; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JavaliVisitor ) return ((JavaliVisitor<? extends T>)visitor).visitNewExpr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final NewExprContext newExpr() throws RecognitionException {
		NewExprContext _localctx = new NewExprContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_newExpr);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(181);
			match(T__16);
			setState(195);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,16,_ctx) ) {
			case 1:
				{
				setState(182);
				match(Identifier);
				setState(183);
				match(T__7);
				setState(184);
				match(T__8);
				}
				break;
			case 2:
				{
				setState(185);
				match(Identifier);
				setState(186);
				match(T__17);
				setState(187);
				expr(0);
				setState(188);
				match(T__18);
				}
				break;
			case 3:
				{
				setState(190);
				match(PrimitiveType);
				setState(191);
				match(T__17);
				setState(192);
				expr(0);
				setState(193);
				match(T__18);
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ReadExprContext extends ParserRuleContext {
		public ReadExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_readExpr; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JavaliVisitor ) return ((JavaliVisitor<? extends T>)visitor).visitReadExpr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ReadExprContext readExpr() throws RecognitionException {
		ReadExprContext _localctx = new ReadExprContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_readExpr);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(197);
			match(T__19);
			setState(198);
			match(T__7);
			setState(199);
			match(T__8);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class MethodCallExprContext extends ParserRuleContext {
		public TerminalNode Identifier() { return getToken(JavaliParser.Identifier, 0); }
		public ActualParamListContext actualParamList() {
			return getRuleContext(ActualParamListContext.class,0);
		}
		public IdentifierAccessContext identifierAccess() {
			return getRuleContext(IdentifierAccessContext.class,0);
		}
		public MethodCallExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_methodCallExpr; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JavaliVisitor ) return ((JavaliVisitor<? extends T>)visitor).visitMethodCallExpr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MethodCallExprContext methodCallExpr() throws RecognitionException {
		MethodCallExprContext _localctx = new MethodCallExprContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_methodCallExpr);
		int _la;
		try {
			setState(216);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,19,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(201);
				match(Identifier);
				setState(202);
				match(T__7);
				setState(204);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__7) | (1L << T__21) | (1L << T__22) | (1L << T__23) | (1L << T__24) | (1L << T__36) | (1L << Integer) | (1L << Boolean) | (1L << Identifier))) != 0)) {
					{
					setState(203);
					actualParamList();
					}
				}

				setState(206);
				match(T__8);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(207);
				identifierAccess(0);
				setState(208);
				match(T__20);
				setState(209);
				match(Identifier);
				setState(210);
				match(T__7);
				setState(212);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__7) | (1L << T__21) | (1L << T__22) | (1L << T__23) | (1L << T__24) | (1L << T__36) | (1L << Integer) | (1L << Boolean) | (1L << Identifier))) != 0)) {
					{
					setState(211);
					actualParamList();
					}
				}

				setState(214);
				match(T__8);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ActualParamListContext extends ParserRuleContext {
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public ActualParamListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_actualParamList; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JavaliVisitor ) return ((JavaliVisitor<? extends T>)visitor).visitActualParamList(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ActualParamListContext actualParamList() throws RecognitionException {
		ActualParamListContext _localctx = new ActualParamListContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_actualParamList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(218);
			expr(0);
			setState(223);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__4) {
				{
				{
				setState(219);
				match(T__4);
				setState(220);
				expr(0);
				}
				}
				setState(225);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class IdentifierAccessContext extends ParserRuleContext {
		public IdentifierAccessContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_identifierAccess; }
	 
		public IdentifierAccessContext() { }
		public void copyFrom(IdentifierAccessContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class RemoteMethodCallContext extends IdentifierAccessContext {
		public IdentifierAccessContext identifierAccess() {
			return getRuleContext(IdentifierAccessContext.class,0);
		}
		public TerminalNode Identifier() { return getToken(JavaliParser.Identifier, 0); }
		public ActualParamListContext actualParamList() {
			return getRuleContext(ActualParamListContext.class,0);
		}
		public RemoteMethodCallContext(IdentifierAccessContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JavaliVisitor ) return ((JavaliVisitor<? extends T>)visitor).visitRemoteMethodCall(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class FieldAccessContext extends IdentifierAccessContext {
		public IdentifierAccessContext identifierAccess() {
			return getRuleContext(IdentifierAccessContext.class,0);
		}
		public TerminalNode Identifier() { return getToken(JavaliParser.Identifier, 0); }
		public FieldAccessContext(IdentifierAccessContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JavaliVisitor ) return ((JavaliVisitor<? extends T>)visitor).visitFieldAccess(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class VarAccessContext extends IdentifierAccessContext {
		public TerminalNode Identifier() { return getToken(JavaliParser.Identifier, 0); }
		public VarAccessContext(IdentifierAccessContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JavaliVisitor ) return ((JavaliVisitor<? extends T>)visitor).visitVarAccess(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ThisAccessContext extends IdentifierAccessContext {
		public ThisAccessContext(IdentifierAccessContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JavaliVisitor ) return ((JavaliVisitor<? extends T>)visitor).visitThisAccess(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ArrayAccessContext extends IdentifierAccessContext {
		public IdentifierAccessContext identifierAccess() {
			return getRuleContext(IdentifierAccessContext.class,0);
		}
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public ArrayAccessContext(IdentifierAccessContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JavaliVisitor ) return ((JavaliVisitor<? extends T>)visitor).visitArrayAccess(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class LocalMethodCallContext extends IdentifierAccessContext {
		public TerminalNode Identifier() { return getToken(JavaliParser.Identifier, 0); }
		public ActualParamListContext actualParamList() {
			return getRuleContext(ActualParamListContext.class,0);
		}
		public LocalMethodCallContext(IdentifierAccessContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JavaliVisitor ) return ((JavaliVisitor<? extends T>)visitor).visitLocalMethodCall(this);
			else return visitor.visitChildren(this);
		}
	}

	public final IdentifierAccessContext identifierAccess() throws RecognitionException {
		return identifierAccess(0);
	}

	private IdentifierAccessContext identifierAccess(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		IdentifierAccessContext _localctx = new IdentifierAccessContext(_ctx, _parentState);
		IdentifierAccessContext _prevctx = _localctx;
		int _startState = 36;
		enterRecursionRule(_localctx, 36, RULE_identifierAccess, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(235);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,22,_ctx) ) {
			case 1:
				{
				_localctx = new VarAccessContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(227);
				match(Identifier);
				}
				break;
			case 2:
				{
				_localctx = new ThisAccessContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(228);
				match(T__21);
				}
				break;
			case 3:
				{
				_localctx = new LocalMethodCallContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(229);
				match(Identifier);
				setState(230);
				match(T__7);
				setState(232);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__7) | (1L << T__21) | (1L << T__22) | (1L << T__23) | (1L << T__24) | (1L << T__36) | (1L << Integer) | (1L << Boolean) | (1L << Identifier))) != 0)) {
					{
					setState(231);
					actualParamList();
					}
				}

				setState(234);
				match(T__8);
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(255);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,25,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(253);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,24,_ctx) ) {
					case 1:
						{
						_localctx = new FieldAccessContext(new IdentifierAccessContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_identifierAccess);
						setState(237);
						if (!(precpred(_ctx, 4))) throw new FailedPredicateException(this, "precpred(_ctx, 4)");
						setState(238);
						match(T__20);
						setState(239);
						match(Identifier);
						}
						break;
					case 2:
						{
						_localctx = new ArrayAccessContext(new IdentifierAccessContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_identifierAccess);
						setState(240);
						if (!(precpred(_ctx, 3))) throw new FailedPredicateException(this, "precpred(_ctx, 3)");
						setState(241);
						match(T__17);
						setState(242);
						expr(0);
						setState(243);
						match(T__18);
						}
						break;
					case 3:
						{
						_localctx = new RemoteMethodCallContext(new IdentifierAccessContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_identifierAccess);
						setState(245);
						if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
						setState(246);
						match(T__20);
						setState(247);
						match(Identifier);
						setState(248);
						match(T__7);
						setState(250);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__7) | (1L << T__21) | (1L << T__22) | (1L << T__23) | (1L << T__24) | (1L << T__36) | (1L << Integer) | (1L << Boolean) | (1L << Identifier))) != 0)) {
							{
							setState(249);
							actualParamList();
							}
						}

						setState(252);
						match(T__8);
						}
						break;
					}
					} 
				}
				setState(257);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,25,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class ExprContext extends ParserRuleContext {
		public ExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expr; }
	 
		public ExprContext() { }
		public void copyFrom(ExprContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class ExprUnaryOpContext extends ExprContext {
		public Token op;
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public ExprUnaryOpContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JavaliVisitor ) return ((JavaliVisitor<? extends T>)visitor).visitExprUnaryOp(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ExprBOpCompContext extends ExprContext {
		public Token op;
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public ExprBOpCompContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JavaliVisitor ) return ((JavaliVisitor<? extends T>)visitor).visitExprBOpComp(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ExprBOpEqContext extends ExprContext {
		public Token op;
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public ExprBOpEqContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JavaliVisitor ) return ((JavaliVisitor<? extends T>)visitor).visitExprBOpEq(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ExprBOpAddContext extends ExprContext {
		public Token op;
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public ExprBOpAddContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JavaliVisitor ) return ((JavaliVisitor<? extends T>)visitor).visitExprBOpAdd(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ExprCastContext extends ExprContext {
		public ReferenceTypeContext referenceType() {
			return getRuleContext(ReferenceTypeContext.class,0);
		}
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public ExprCastContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JavaliVisitor ) return ((JavaliVisitor<? extends T>)visitor).visitExprCast(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ExprIdentifierAccessContext extends ExprContext {
		public IdentifierAccessContext identifierAccess() {
			return getRuleContext(IdentifierAccessContext.class,0);
		}
		public ExprIdentifierAccessContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JavaliVisitor ) return ((JavaliVisitor<? extends T>)visitor).visitExprIdentifierAccess(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ExprBOpOrContext extends ExprContext {
		public Token op;
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public ExprBOpOrContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JavaliVisitor ) return ((JavaliVisitor<? extends T>)visitor).visitExprBOpOr(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ExprLiteralContext extends ExprContext {
		public LiteralContext literal() {
			return getRuleContext(LiteralContext.class,0);
		}
		public ExprLiteralContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JavaliVisitor ) return ((JavaliVisitor<? extends T>)visitor).visitExprLiteral(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ExprBOpMultContext extends ExprContext {
		public Token op;
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public ExprBOpMultContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JavaliVisitor ) return ((JavaliVisitor<? extends T>)visitor).visitExprBOpMult(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ExprInBracketsContext extends ExprContext {
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public ExprInBracketsContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JavaliVisitor ) return ((JavaliVisitor<? extends T>)visitor).visitExprInBrackets(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ExprBOpAndContext extends ExprContext {
		public Token op;
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public ExprBOpAndContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JavaliVisitor ) return ((JavaliVisitor<? extends T>)visitor).visitExprBOpAnd(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExprContext expr() throws RecognitionException {
		return expr(0);
	}

	private ExprContext expr(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		ExprContext _localctx = new ExprContext(_ctx, _parentState);
		ExprContext _prevctx = _localctx;
		int _startState = 38;
		enterRecursionRule(_localctx, 38, RULE_expr, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(272);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,26,_ctx) ) {
			case 1:
				{
				_localctx = new ExprLiteralContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(259);
				literal();
				}
				break;
			case 2:
				{
				_localctx = new ExprIdentifierAccessContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(260);
				identifierAccess(0);
				}
				break;
			case 3:
				{
				_localctx = new ExprInBracketsContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(261);
				match(T__7);
				setState(262);
				expr(0);
				setState(263);
				match(T__8);
				}
				break;
			case 4:
				{
				_localctx = new ExprUnaryOpContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(265);
				((ExprUnaryOpContext)_localctx).op = _input.LT(1);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__22) | (1L << T__23) | (1L << T__24))) != 0)) ) {
					((ExprUnaryOpContext)_localctx).op = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(266);
				expr(8);
				}
				break;
			case 5:
				{
				_localctx = new ExprCastContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(267);
				match(T__7);
				setState(268);
				referenceType();
				setState(269);
				match(T__8);
				setState(270);
				expr(7);
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(294);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,28,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(292);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,27,_ctx) ) {
					case 1:
						{
						_localctx = new ExprBOpMultContext(new ExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(274);
						if (!(precpred(_ctx, 6))) throw new FailedPredicateException(this, "precpred(_ctx, 6)");
						setState(275);
						((ExprBOpMultContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__25) | (1L << T__26) | (1L << T__27))) != 0)) ) {
							((ExprBOpMultContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(276);
						expr(7);
						}
						break;
					case 2:
						{
						_localctx = new ExprBOpAddContext(new ExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(277);
						if (!(precpred(_ctx, 5))) throw new FailedPredicateException(this, "precpred(_ctx, 5)");
						setState(278);
						((ExprBOpAddContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==T__22 || _la==T__23) ) {
							((ExprBOpAddContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(279);
						expr(6);
						}
						break;
					case 3:
						{
						_localctx = new ExprBOpCompContext(new ExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(280);
						if (!(precpred(_ctx, 4))) throw new FailedPredicateException(this, "precpred(_ctx, 4)");
						setState(281);
						((ExprBOpCompContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__28) | (1L << T__29) | (1L << T__30) | (1L << T__31))) != 0)) ) {
							((ExprBOpCompContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(282);
						expr(5);
						}
						break;
					case 4:
						{
						_localctx = new ExprBOpEqContext(new ExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(283);
						if (!(precpred(_ctx, 3))) throw new FailedPredicateException(this, "precpred(_ctx, 3)");
						setState(284);
						((ExprBOpEqContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==T__32 || _la==T__33) ) {
							((ExprBOpEqContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(285);
						expr(4);
						}
						break;
					case 5:
						{
						_localctx = new ExprBOpAndContext(new ExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(286);
						if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
						setState(287);
						((ExprBOpAndContext)_localctx).op = match(T__34);
						setState(288);
						expr(3);
						}
						break;
					case 6:
						{
						_localctx = new ExprBOpOrContext(new ExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(289);
						if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
						setState(290);
						((ExprBOpOrContext)_localctx).op = match(T__35);
						setState(291);
						expr(2);
						}
						break;
					}
					} 
				}
				setState(296);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,28,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class TypeContext extends ParserRuleContext {
		public TerminalNode PrimitiveType() { return getToken(JavaliParser.PrimitiveType, 0); }
		public ReferenceTypeContext referenceType() {
			return getRuleContext(ReferenceTypeContext.class,0);
		}
		public TypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_type; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JavaliVisitor ) return ((JavaliVisitor<? extends T>)visitor).visitType(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TypeContext type() throws RecognitionException {
		TypeContext _localctx = new TypeContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_type);
		try {
			setState(299);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,29,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(297);
				match(PrimitiveType);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(298);
				referenceType();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ReferenceTypeContext extends ParserRuleContext {
		public TerminalNode Identifier() { return getToken(JavaliParser.Identifier, 0); }
		public ArrayTypeContext arrayType() {
			return getRuleContext(ArrayTypeContext.class,0);
		}
		public ReferenceTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_referenceType; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JavaliVisitor ) return ((JavaliVisitor<? extends T>)visitor).visitReferenceType(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ReferenceTypeContext referenceType() throws RecognitionException {
		ReferenceTypeContext _localctx = new ReferenceTypeContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_referenceType);
		try {
			setState(303);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,30,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(301);
				match(Identifier);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(302);
				arrayType();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ArrayTypeContext extends ParserRuleContext {
		public TerminalNode Identifier() { return getToken(JavaliParser.Identifier, 0); }
		public TerminalNode PrimitiveType() { return getToken(JavaliParser.PrimitiveType, 0); }
		public ArrayTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_arrayType; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JavaliVisitor ) return ((JavaliVisitor<? extends T>)visitor).visitArrayType(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ArrayTypeContext arrayType() throws RecognitionException {
		ArrayTypeContext _localctx = new ArrayTypeContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_arrayType);
		try {
			setState(311);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case Identifier:
				enterOuterAlt(_localctx, 1);
				{
				setState(305);
				match(Identifier);
				setState(306);
				match(T__17);
				setState(307);
				match(T__18);
				}
				break;
			case PrimitiveType:
				enterOuterAlt(_localctx, 2);
				{
				setState(308);
				match(PrimitiveType);
				setState(309);
				match(T__17);
				setState(310);
				match(T__18);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class LiteralContext extends ParserRuleContext {
		public TerminalNode Boolean() { return getToken(JavaliParser.Boolean, 0); }
		public TerminalNode Integer() { return getToken(JavaliParser.Integer, 0); }
		public LiteralContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_literal; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JavaliVisitor ) return ((JavaliVisitor<? extends T>)visitor).visitLiteral(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LiteralContext literal() throws RecognitionException {
		LiteralContext _localctx = new LiteralContext(_ctx, getState());
		enterRule(_localctx, 46, RULE_literal);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(313);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__36) | (1L << Integer) | (1L << Boolean))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 18:
			return identifierAccess_sempred((IdentifierAccessContext)_localctx, predIndex);
		case 19:
			return expr_sempred((ExprContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean identifierAccess_sempred(IdentifierAccessContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return precpred(_ctx, 4);
		case 1:
			return precpred(_ctx, 3);
		case 2:
			return precpred(_ctx, 1);
		}
		return true;
	}
	private boolean expr_sempred(ExprContext _localctx, int predIndex) {
		switch (predIndex) {
		case 3:
			return precpred(_ctx, 6);
		case 4:
			return precpred(_ctx, 5);
		case 5:
			return precpred(_ctx, 4);
		case 6:
			return precpred(_ctx, 3);
		case 7:
			return precpred(_ctx, 2);
		case 8:
			return precpred(_ctx, 1);
		}
		return true;
	}

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3/\u013e\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\3\2\6\2\64\n\2\r\2\16\2\65\3\2\3\2\3\3\3\3\3\3\3\3\5\3>\n\3\3\3\3\3\3"+
		"\3\3\3\3\4\3\4\7\4F\n\4\f\4\16\4I\13\4\3\5\3\5\3\5\3\5\7\5O\n\5\f\5\16"+
		"\5R\13\5\3\5\3\5\3\6\3\6\5\6X\n\6\3\6\3\6\3\6\5\6]\n\6\3\6\3\6\3\6\7\6"+
		"b\n\6\f\6\16\6e\13\6\3\6\7\6h\n\6\f\6\16\6k\13\6\3\6\3\6\3\7\3\7\3\7\3"+
		"\7\3\7\3\7\7\7u\n\7\f\7\16\7x\13\7\3\b\3\b\3\b\3\b\3\b\3\b\5\b\u0080\n"+
		"\b\3\t\3\t\7\t\u0084\n\t\f\t\16\t\u0087\13\t\3\t\3\t\3\n\3\n\3\n\3\13"+
		"\3\13\3\13\3\13\3\13\5\13\u0093\n\13\3\13\3\13\3\f\3\f\3\f\3\f\3\f\3\f"+
		"\3\f\3\f\5\f\u009f\n\f\3\f\3\f\3\r\3\r\3\r\3\r\3\r\3\r\3\r\5\r\u00aa\n"+
		"\r\3\16\3\16\3\16\3\16\3\16\3\16\3\17\3\17\5\17\u00b4\n\17\3\17\3\17\3"+
		"\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\5"+
		"\20\u00c6\n\20\3\21\3\21\3\21\3\21\3\22\3\22\3\22\5\22\u00cf\n\22\3\22"+
		"\3\22\3\22\3\22\3\22\3\22\5\22\u00d7\n\22\3\22\3\22\5\22\u00db\n\22\3"+
		"\23\3\23\3\23\7\23\u00e0\n\23\f\23\16\23\u00e3\13\23\3\24\3\24\3\24\3"+
		"\24\3\24\3\24\5\24\u00eb\n\24\3\24\5\24\u00ee\n\24\3\24\3\24\3\24\3\24"+
		"\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\24\5\24\u00fd\n\24\3\24\7\24"+
		"\u0100\n\24\f\24\16\24\u0103\13\24\3\25\3\25\3\25\3\25\3\25\3\25\3\25"+
		"\3\25\3\25\3\25\3\25\3\25\3\25\3\25\5\25\u0113\n\25\3\25\3\25\3\25\3\25"+
		"\3\25\3\25\3\25\3\25\3\25\3\25\3\25\3\25\3\25\3\25\3\25\3\25\3\25\3\25"+
		"\7\25\u0127\n\25\f\25\16\25\u012a\13\25\3\26\3\26\5\26\u012e\n\26\3\27"+
		"\3\27\5\27\u0132\n\27\3\30\3\30\3\30\3\30\3\30\3\30\5\30\u013a\n\30\3"+
		"\31\3\31\3\31\2\4&(\32\2\4\6\b\n\f\16\20\22\24\26\30\32\34\36 \"$&(*,"+
		".\60\2\b\3\2\31\33\3\2\34\36\3\2\31\32\3\2\37\"\3\2#$\4\2\'\')*\2\u0154"+
		"\2\63\3\2\2\2\49\3\2\2\2\6G\3\2\2\2\bJ\3\2\2\2\nW\3\2\2\2\fn\3\2\2\2\16"+
		"\177\3\2\2\2\20\u0081\3\2\2\2\22\u008a\3\2\2\2\24\u008d\3\2\2\2\26\u009e"+
		"\3\2\2\2\30\u00a2\3\2\2\2\32\u00ab\3\2\2\2\34\u00b1\3\2\2\2\36\u00b7\3"+
		"\2\2\2 \u00c7\3\2\2\2\"\u00da\3\2\2\2$\u00dc\3\2\2\2&\u00ed\3\2\2\2(\u0112"+
		"\3\2\2\2*\u012d\3\2\2\2,\u0131\3\2\2\2.\u0139\3\2\2\2\60\u013b\3\2\2\2"+
		"\62\64\5\4\3\2\63\62\3\2\2\2\64\65\3\2\2\2\65\63\3\2\2\2\65\66\3\2\2\2"+
		"\66\67\3\2\2\2\678\7\2\2\38\3\3\2\2\29:\7\3\2\2:=\7+\2\2;<\7\4\2\2<>\7"+
		"+\2\2=;\3\2\2\2=>\3\2\2\2>?\3\2\2\2?@\7\5\2\2@A\5\6\4\2AB\7\6\2\2B\5\3"+
		"\2\2\2CF\5\b\5\2DF\5\n\6\2EC\3\2\2\2ED\3\2\2\2FI\3\2\2\2GE\3\2\2\2GH\3"+
		"\2\2\2H\7\3\2\2\2IG\3\2\2\2JK\5*\26\2KP\7+\2\2LM\7\7\2\2MO\7+\2\2NL\3"+
		"\2\2\2OR\3\2\2\2PN\3\2\2\2PQ\3\2\2\2QS\3\2\2\2RP\3\2\2\2ST\7\b\2\2T\t"+
		"\3\2\2\2UX\5*\26\2VX\7\t\2\2WU\3\2\2\2WV\3\2\2\2XY\3\2\2\2YZ\7+\2\2Z\\"+
		"\7\n\2\2[]\5\f\7\2\\[\3\2\2\2\\]\3\2\2\2]^\3\2\2\2^_\7\13\2\2_c\7\5\2"+
		"\2`b\5\b\5\2a`\3\2\2\2be\3\2\2\2ca\3\2\2\2cd\3\2\2\2di\3\2\2\2ec\3\2\2"+
		"\2fh\5\16\b\2gf\3\2\2\2hk\3\2\2\2ig\3\2\2\2ij\3\2\2\2jl\3\2\2\2ki\3\2"+
		"\2\2lm\7\6\2\2m\13\3\2\2\2no\5*\26\2ov\7+\2\2pq\7\7\2\2qr\5*\26\2rs\7"+
		"+\2\2su\3\2\2\2tp\3\2\2\2ux\3\2\2\2vt\3\2\2\2vw\3\2\2\2w\r\3\2\2\2xv\3"+
		"\2\2\2y\u0080\5\24\13\2z\u0080\5\22\n\2{\u0080\5\30\r\2|\u0080\5\32\16"+
		"\2}\u0080\5\34\17\2~\u0080\5\26\f\2\177y\3\2\2\2\177z\3\2\2\2\177{\3\2"+
		"\2\2\177|\3\2\2\2\177}\3\2\2\2\177~\3\2\2\2\u0080\17\3\2\2\2\u0081\u0085"+
		"\7\5\2\2\u0082\u0084\5\16\b\2\u0083\u0082\3\2\2\2\u0084\u0087\3\2\2\2"+
		"\u0085\u0083\3\2\2\2\u0085\u0086\3\2\2\2\u0086\u0088\3\2\2\2\u0087\u0085"+
		"\3\2\2\2\u0088\u0089\7\6\2\2\u0089\21\3\2\2\2\u008a\u008b\5\"\22\2\u008b"+
		"\u008c\7\b\2\2\u008c\23\3\2\2\2\u008d\u008e\5&\24\2\u008e\u0092\7\f\2"+
		"\2\u008f\u0093\5(\25\2\u0090\u0093\5\36\20\2\u0091\u0093\5 \21\2\u0092"+
		"\u008f\3\2\2\2\u0092\u0090\3\2\2\2\u0092\u0091\3\2\2\2\u0093\u0094\3\2"+
		"\2\2\u0094\u0095\7\b\2\2\u0095\25\3\2\2\2\u0096\u0097\7\r\2\2\u0097\u0098"+
		"\7\n\2\2\u0098\u0099\5(\25\2\u0099\u009a\7\13\2\2\u009a\u009f\3\2\2\2"+
		"\u009b\u009c\7\16\2\2\u009c\u009d\7\n\2\2\u009d\u009f\7\13\2\2\u009e\u0096"+
		"\3\2\2\2\u009e\u009b\3\2\2\2\u009f\u00a0\3\2\2\2\u00a0\u00a1\7\b\2\2\u00a1"+
		"\27\3\2\2\2\u00a2\u00a3\7\17\2\2\u00a3\u00a4\7\n\2\2\u00a4\u00a5\5(\25"+
		"\2\u00a5\u00a6\7\13\2\2\u00a6\u00a9\5\20\t\2\u00a7\u00a8\7\20\2\2\u00a8"+
		"\u00aa\5\20\t\2\u00a9\u00a7\3\2\2\2\u00a9\u00aa\3\2\2\2\u00aa\31\3\2\2"+
		"\2\u00ab\u00ac\7\21\2\2\u00ac\u00ad\7\n\2\2\u00ad\u00ae\5(\25\2\u00ae"+
		"\u00af\7\13\2\2\u00af\u00b0\5\20\t\2\u00b0\33\3\2\2\2\u00b1\u00b3\7\22"+
		"\2\2\u00b2\u00b4\5(\25\2\u00b3\u00b2\3\2\2\2\u00b3\u00b4\3\2\2\2\u00b4"+
		"\u00b5\3\2\2\2\u00b5\u00b6\7\b\2\2\u00b6\35\3\2\2\2\u00b7\u00c5\7\23\2"+
		"\2\u00b8\u00b9\7+\2\2\u00b9\u00ba\7\n\2\2\u00ba\u00c6\7\13\2\2\u00bb\u00bc"+
		"\7+\2\2\u00bc\u00bd\7\24\2\2\u00bd\u00be\5(\25\2\u00be\u00bf\7\25\2\2"+
		"\u00bf\u00c6\3\2\2\2\u00c0\u00c1\7(\2\2\u00c1\u00c2\7\24\2\2\u00c2\u00c3"+
		"\5(\25\2\u00c3\u00c4\7\25\2\2\u00c4\u00c6\3\2\2\2\u00c5\u00b8\3\2\2\2"+
		"\u00c5\u00bb\3\2\2\2\u00c5\u00c0\3\2\2\2\u00c6\37\3\2\2\2\u00c7\u00c8"+
		"\7\26\2\2\u00c8\u00c9\7\n\2\2\u00c9\u00ca\7\13\2\2\u00ca!\3\2\2\2\u00cb"+
		"\u00cc\7+\2\2\u00cc\u00ce\7\n\2\2\u00cd\u00cf\5$\23\2\u00ce\u00cd\3\2"+
		"\2\2\u00ce\u00cf\3\2\2\2\u00cf\u00d0\3\2\2\2\u00d0\u00db\7\13\2\2\u00d1"+
		"\u00d2\5&\24\2\u00d2\u00d3\7\27\2\2\u00d3\u00d4\7+\2\2\u00d4\u00d6\7\n"+
		"\2\2\u00d5\u00d7\5$\23\2\u00d6\u00d5\3\2\2\2\u00d6\u00d7\3\2\2\2\u00d7"+
		"\u00d8\3\2\2\2\u00d8\u00d9\7\13\2\2\u00d9\u00db\3\2\2\2\u00da\u00cb\3"+
		"\2\2\2\u00da\u00d1\3\2\2\2\u00db#\3\2\2\2\u00dc\u00e1\5(\25\2\u00dd\u00de"+
		"\7\7\2\2\u00de\u00e0\5(\25\2\u00df\u00dd\3\2\2\2\u00e0\u00e3\3\2\2\2\u00e1"+
		"\u00df\3\2\2\2\u00e1\u00e2\3\2\2\2\u00e2%\3\2\2\2\u00e3\u00e1\3\2\2\2"+
		"\u00e4\u00e5\b\24\1\2\u00e5\u00ee\7+\2\2\u00e6\u00ee\7\30\2\2\u00e7\u00e8"+
		"\7+\2\2\u00e8\u00ea\7\n\2\2\u00e9\u00eb\5$\23\2\u00ea\u00e9\3\2\2\2\u00ea"+
		"\u00eb\3\2\2\2\u00eb\u00ec\3\2\2\2\u00ec\u00ee\7\13\2\2\u00ed\u00e4\3"+
		"\2\2\2\u00ed\u00e6\3\2\2\2\u00ed\u00e7\3\2\2\2\u00ee\u0101\3\2\2\2\u00ef"+
		"\u00f0\f\6\2\2\u00f0\u00f1\7\27\2\2\u00f1\u0100\7+\2\2\u00f2\u00f3\f\5"+
		"\2\2\u00f3\u00f4\7\24\2\2\u00f4\u00f5\5(\25\2\u00f5\u00f6\7\25\2\2\u00f6"+
		"\u0100\3\2\2\2\u00f7\u00f8\f\3\2\2\u00f8\u00f9\7\27\2\2\u00f9\u00fa\7"+
		"+\2\2\u00fa\u00fc\7\n\2\2\u00fb\u00fd\5$\23\2\u00fc\u00fb\3\2\2\2\u00fc"+
		"\u00fd\3\2\2\2\u00fd\u00fe\3\2\2\2\u00fe\u0100\7\13\2\2\u00ff\u00ef\3"+
		"\2\2\2\u00ff\u00f2\3\2\2\2\u00ff\u00f7\3\2\2\2\u0100\u0103\3\2\2\2\u0101"+
		"\u00ff\3\2\2\2\u0101\u0102\3\2\2\2\u0102\'\3\2\2\2\u0103\u0101\3\2\2\2"+
		"\u0104\u0105\b\25\1\2\u0105\u0113\5\60\31\2\u0106\u0113\5&\24\2\u0107"+
		"\u0108\7\n\2\2\u0108\u0109\5(\25\2\u0109\u010a\7\13\2\2\u010a\u0113\3"+
		"\2\2\2\u010b\u010c\t\2\2\2\u010c\u0113\5(\25\n\u010d\u010e\7\n\2\2\u010e"+
		"\u010f\5,\27\2\u010f\u0110\7\13\2\2\u0110\u0111\5(\25\t\u0111\u0113\3"+
		"\2\2\2\u0112\u0104\3\2\2\2\u0112\u0106\3\2\2\2\u0112\u0107\3\2\2\2\u0112"+
		"\u010b\3\2\2\2\u0112\u010d\3\2\2\2\u0113\u0128\3\2\2\2\u0114\u0115\f\b"+
		"\2\2\u0115\u0116\t\3\2\2\u0116\u0127\5(\25\t\u0117\u0118\f\7\2\2\u0118"+
		"\u0119\t\4\2\2\u0119\u0127\5(\25\b\u011a\u011b\f\6\2\2\u011b\u011c\t\5"+
		"\2\2\u011c\u0127\5(\25\7\u011d\u011e\f\5\2\2\u011e\u011f\t\6\2\2\u011f"+
		"\u0127\5(\25\6\u0120\u0121\f\4\2\2\u0121\u0122\7%\2\2\u0122\u0127\5(\25"+
		"\5\u0123\u0124\f\3\2\2\u0124\u0125\7&\2\2\u0125\u0127\5(\25\4\u0126\u0114"+
		"\3\2\2\2\u0126\u0117\3\2\2\2\u0126\u011a\3\2\2\2\u0126\u011d\3\2\2\2\u0126"+
		"\u0120\3\2\2\2\u0126\u0123\3\2\2\2\u0127\u012a\3\2\2\2\u0128\u0126\3\2"+
		"\2\2\u0128\u0129\3\2\2\2\u0129)\3\2\2\2\u012a\u0128\3\2\2\2\u012b\u012e"+
		"\7(\2\2\u012c\u012e\5,\27\2\u012d\u012b\3\2\2\2\u012d\u012c\3\2\2\2\u012e"+
		"+\3\2\2\2\u012f\u0132\7+\2\2\u0130\u0132\5.\30\2\u0131\u012f\3\2\2\2\u0131"+
		"\u0130\3\2\2\2\u0132-\3\2\2\2\u0133\u0134\7+\2\2\u0134\u0135\7\24\2\2"+
		"\u0135\u013a\7\25\2\2\u0136\u0137\7(\2\2\u0137\u0138\7\24\2\2\u0138\u013a"+
		"\7\25\2\2\u0139\u0133\3\2\2\2\u0139\u0136\3\2\2\2\u013a/\3\2\2\2\u013b"+
		"\u013c\t\7\2\2\u013c\61\3\2\2\2\"\65=EGPW\\civ\177\u0085\u0092\u009e\u00a9"+
		"\u00b3\u00c5\u00ce\u00d6\u00da\u00e1\u00ea\u00ed\u00fc\u00ff\u0101\u0112"+
		"\u0126\u0128\u012d\u0131\u0139";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}