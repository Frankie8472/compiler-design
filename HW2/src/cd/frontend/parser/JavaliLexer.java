// Generated from /run/media/Josua/Daten/Josua/Uni/6. Semester/Compiler Design/compiler-design/HW2/src/cd/frontend/parser/Javali.g4 by ANTLR 4.7
package cd.frontend.parser;

	// Java header
	//package cd.frontend.parser;

import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class JavaliLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.7", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		T__9=10, T__10=11, T__11=12, T__12=13, T__13=14, T__14=15, T__15=16, T__16=17, 
		T__17=18, T__18=19, T__19=20, T__20=21, T__21=22, T__22=23, BinaryOp=24, 
		PrimitiveType=25, Integer=26, Boolean=27, Identifier=28, COMMENT=29, LINE_COMMENT=30, 
		WS=31, ErrorCharacter=32;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] ruleNames = {
		"T__0", "T__1", "T__2", "T__3", "T__4", "T__5", "T__6", "T__7", "T__8", 
		"T__9", "T__10", "T__11", "T__12", "T__13", "T__14", "T__15", "T__16", 
		"T__17", "T__18", "T__19", "T__20", "T__21", "T__22", "BinaryOp", "MultOp", 
		"AddOp", "CompOp", "EqOp", "AndOp", "OrOp", "PrimitiveType", "Integer", 
		"Decimal", "Hex", "Boolean", "Identifier", "Letter", "Digit", "HexDigit", 
		"COMMENT", "LINE_COMMENT", "WS", "ErrorCharacter"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'class'", "'extends'", "'{'", "'}'", "','", "';'", "'void'", "'('", 
		"')'", "'='", "'write'", "'writeln'", "'if'", "'else'", "'while'", "'return'", 
		"'new'", "'['", "']'", "'read'", "'.'", "'this'", "'null'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, null, null, null, null, null, null, null, null, null, 
		null, null, null, null, null, null, null, null, null, null, null, null, 
		"BinaryOp", "PrimitiveType", "Integer", "Boolean", "Identifier", "COMMENT", 
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


	public JavaliLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "Javali.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\"\u0138\b\1\4\2\t"+
		"\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\4"+
		",\t,\3\2\3\2\3\2\3\2\3\2\3\2\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\4\3\4\3"+
		"\5\3\5\3\6\3\6\3\7\3\7\3\b\3\b\3\b\3\b\3\b\3\t\3\t\3\n\3\n\3\13\3\13\3"+
		"\f\3\f\3\f\3\f\3\f\3\f\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\16\3\16\3\16"+
		"\3\17\3\17\3\17\3\17\3\17\3\20\3\20\3\20\3\20\3\20\3\20\3\21\3\21\3\21"+
		"\3\21\3\21\3\21\3\21\3\22\3\22\3\22\3\22\3\23\3\23\3\24\3\24\3\25\3\25"+
		"\3\25\3\25\3\25\3\26\3\26\3\27\3\27\3\27\3\27\3\27\3\30\3\30\3\30\3\30"+
		"\3\30\3\31\3\31\3\31\3\31\3\31\3\31\5\31\u00bd\n\31\3\32\3\32\3\33\3\33"+
		"\3\34\3\34\3\34\3\34\3\34\3\34\5\34\u00c9\n\34\3\35\3\35\3\35\3\35\5\35"+
		"\u00cf\n\35\3\36\3\36\3\36\3\37\3\37\3\37\3 \3 \3 \3 \3 \3 \3 \3 \3 \3"+
		" \5 \u00e1\n \3!\3!\5!\u00e5\n!\3\"\3\"\3\"\7\"\u00ea\n\"\f\"\16\"\u00ed"+
		"\13\"\5\"\u00ef\n\"\3#\3#\3#\3#\5#\u00f5\n#\3#\6#\u00f8\n#\r#\16#\u00f9"+
		"\3$\3$\3$\3$\3$\3$\3$\3$\3$\5$\u0105\n$\3%\3%\3%\7%\u010a\n%\f%\16%\u010d"+
		"\13%\3&\3&\3\'\3\'\3(\3(\5(\u0115\n(\3)\3)\3)\3)\7)\u011b\n)\f)\16)\u011e"+
		"\13)\3)\3)\3)\3)\3)\3*\3*\3*\3*\7*\u0129\n*\f*\16*\u012c\13*\3*\3*\3+"+
		"\6+\u0131\n+\r+\16+\u0132\3+\3+\3,\3,\3\u011c\2-\3\3\5\4\7\5\t\6\13\7"+
		"\r\b\17\t\21\n\23\13\25\f\27\r\31\16\33\17\35\20\37\21!\22#\23%\24\'\25"+
		")\26+\27-\30/\31\61\32\63\2\65\2\67\29\2;\2=\2?\33A\34C\2E\2G\35I\36K"+
		"\2M\2O\2Q\37S U!W\"\3\2\b\5\2\'\',,\61\61\4\2--//\4\2C\\c|\4\2CHch\4\2"+
		"\f\f\17\17\5\2\13\f\17\17\"\"\2\u0142\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2"+
		"\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2"+
		"\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3"+
		"\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3"+
		"\2\2\2\2+\3\2\2\2\2-\3\2\2\2\2/\3\2\2\2\2\61\3\2\2\2\2?\3\2\2\2\2A\3\2"+
		"\2\2\2G\3\2\2\2\2I\3\2\2\2\2Q\3\2\2\2\2S\3\2\2\2\2U\3\2\2\2\2W\3\2\2\2"+
		"\3Y\3\2\2\2\5_\3\2\2\2\7g\3\2\2\2\ti\3\2\2\2\13k\3\2\2\2\rm\3\2\2\2\17"+
		"o\3\2\2\2\21t\3\2\2\2\23v\3\2\2\2\25x\3\2\2\2\27z\3\2\2\2\31\u0080\3\2"+
		"\2\2\33\u0088\3\2\2\2\35\u008b\3\2\2\2\37\u0090\3\2\2\2!\u0096\3\2\2\2"+
		"#\u009d\3\2\2\2%\u00a1\3\2\2\2\'\u00a3\3\2\2\2)\u00a5\3\2\2\2+\u00aa\3"+
		"\2\2\2-\u00ac\3\2\2\2/\u00b1\3\2\2\2\61\u00bc\3\2\2\2\63\u00be\3\2\2\2"+
		"\65\u00c0\3\2\2\2\67\u00c8\3\2\2\29\u00ce\3\2\2\2;\u00d0\3\2\2\2=\u00d3"+
		"\3\2\2\2?\u00e0\3\2\2\2A\u00e4\3\2\2\2C\u00ee\3\2\2\2E\u00f4\3\2\2\2G"+
		"\u0104\3\2\2\2I\u0106\3\2\2\2K\u010e\3\2\2\2M\u0110\3\2\2\2O\u0114\3\2"+
		"\2\2Q\u0116\3\2\2\2S\u0124\3\2\2\2U\u0130\3\2\2\2W\u0136\3\2\2\2YZ\7e"+
		"\2\2Z[\7n\2\2[\\\7c\2\2\\]\7u\2\2]^\7u\2\2^\4\3\2\2\2_`\7g\2\2`a\7z\2"+
		"\2ab\7v\2\2bc\7g\2\2cd\7p\2\2de\7f\2\2ef\7u\2\2f\6\3\2\2\2gh\7}\2\2h\b"+
		"\3\2\2\2ij\7\177\2\2j\n\3\2\2\2kl\7.\2\2l\f\3\2\2\2mn\7=\2\2n\16\3\2\2"+
		"\2op\7x\2\2pq\7q\2\2qr\7k\2\2rs\7f\2\2s\20\3\2\2\2tu\7*\2\2u\22\3\2\2"+
		"\2vw\7+\2\2w\24\3\2\2\2xy\7?\2\2y\26\3\2\2\2z{\7y\2\2{|\7t\2\2|}\7k\2"+
		"\2}~\7v\2\2~\177\7g\2\2\177\30\3\2\2\2\u0080\u0081\7y\2\2\u0081\u0082"+
		"\7t\2\2\u0082\u0083\7k\2\2\u0083\u0084\7v\2\2\u0084\u0085\7g\2\2\u0085"+
		"\u0086\7n\2\2\u0086\u0087\7p\2\2\u0087\32\3\2\2\2\u0088\u0089\7k\2\2\u0089"+
		"\u008a\7h\2\2\u008a\34\3\2\2\2\u008b\u008c\7g\2\2\u008c\u008d\7n\2\2\u008d"+
		"\u008e\7u\2\2\u008e\u008f\7g\2\2\u008f\36\3\2\2\2\u0090\u0091\7y\2\2\u0091"+
		"\u0092\7j\2\2\u0092\u0093\7k\2\2\u0093\u0094\7n\2\2\u0094\u0095\7g\2\2"+
		"\u0095 \3\2\2\2\u0096\u0097\7t\2\2\u0097\u0098\7g\2\2\u0098\u0099\7v\2"+
		"\2\u0099\u009a\7w\2\2\u009a\u009b\7t\2\2\u009b\u009c\7p\2\2\u009c\"\3"+
		"\2\2\2\u009d\u009e\7p\2\2\u009e\u009f\7g\2\2\u009f\u00a0\7y\2\2\u00a0"+
		"$\3\2\2\2\u00a1\u00a2\7]\2\2\u00a2&\3\2\2\2\u00a3\u00a4\7_\2\2\u00a4("+
		"\3\2\2\2\u00a5\u00a6\7t\2\2\u00a6\u00a7\7g\2\2\u00a7\u00a8\7c\2\2\u00a8"+
		"\u00a9\7f\2\2\u00a9*\3\2\2\2\u00aa\u00ab\7\60\2\2\u00ab,\3\2\2\2\u00ac"+
		"\u00ad\7v\2\2\u00ad\u00ae\7j\2\2\u00ae\u00af\7k\2\2\u00af\u00b0\7u\2\2"+
		"\u00b0.\3\2\2\2\u00b1\u00b2\7p\2\2\u00b2\u00b3\7w\2\2\u00b3\u00b4\7n\2"+
		"\2\u00b4\u00b5\7n\2\2\u00b5\60\3\2\2\2\u00b6\u00bd\5\63\32\2\u00b7\u00bd"+
		"\5\65\33\2\u00b8\u00bd\5\67\34\2\u00b9\u00bd\59\35\2\u00ba\u00bd\5;\36"+
		"\2\u00bb\u00bd\5=\37\2\u00bc\u00b6\3\2\2\2\u00bc\u00b7\3\2\2\2\u00bc\u00b8"+
		"\3\2\2\2\u00bc\u00b9\3\2\2\2\u00bc\u00ba\3\2\2\2\u00bc\u00bb\3\2\2\2\u00bd"+
		"\62\3\2\2\2\u00be\u00bf\t\2\2\2\u00bf\64\3\2\2\2\u00c0\u00c1\t\3\2\2\u00c1"+
		"\66\3\2\2\2\u00c2\u00c9\7>\2\2\u00c3\u00c4\7>\2\2\u00c4\u00c9\7?\2\2\u00c5"+
		"\u00c9\7@\2\2\u00c6\u00c7\7@\2\2\u00c7\u00c9\7?\2\2\u00c8\u00c2\3\2\2"+
		"\2\u00c8\u00c3\3\2\2\2\u00c8\u00c5\3\2\2\2\u00c8\u00c6\3\2\2\2\u00c98"+
		"\3\2\2\2\u00ca\u00cb\7?\2\2\u00cb\u00cf\7?\2\2\u00cc\u00cd\7#\2\2\u00cd"+
		"\u00cf\7?\2\2\u00ce\u00ca\3\2\2\2\u00ce\u00cc\3\2\2\2\u00cf:\3\2\2\2\u00d0"+
		"\u00d1\7(\2\2\u00d1\u00d2\7(\2\2\u00d2<\3\2\2\2\u00d3\u00d4\7~\2\2\u00d4"+
		"\u00d5\7~\2\2\u00d5>\3\2\2\2\u00d6\u00d7\7d\2\2\u00d7\u00d8\7q\2\2\u00d8"+
		"\u00d9\7q\2\2\u00d9\u00da\7n\2\2\u00da\u00db\7g\2\2\u00db\u00dc\7c\2\2"+
		"\u00dc\u00e1\7p\2\2\u00dd\u00de\7k\2\2\u00de\u00df\7p\2\2\u00df\u00e1"+
		"\7v\2\2\u00e0\u00d6\3\2\2\2\u00e0\u00dd\3\2\2\2\u00e1@\3\2\2\2\u00e2\u00e5"+
		"\5C\"\2\u00e3\u00e5\5E#\2\u00e4\u00e2\3\2\2\2\u00e4\u00e3\3\2\2\2\u00e5"+
		"B\3\2\2\2\u00e6\u00ef\7\62\2\2\u00e7\u00eb\4\63;\2\u00e8\u00ea\5M\'\2"+
		"\u00e9\u00e8\3\2\2\2\u00ea\u00ed\3\2\2\2\u00eb\u00e9\3\2\2\2\u00eb\u00ec"+
		"\3\2\2\2\u00ec\u00ef\3\2\2\2\u00ed\u00eb\3\2\2\2\u00ee\u00e6\3\2\2\2\u00ee"+
		"\u00e7\3\2\2\2\u00efD\3\2\2\2\u00f0\u00f1\7\62\2\2\u00f1\u00f5\7z\2\2"+
		"\u00f2\u00f3\7\62\2\2\u00f3\u00f5\7Z\2\2\u00f4\u00f0\3\2\2\2\u00f4\u00f2"+
		"\3\2\2\2\u00f5\u00f7\3\2\2\2\u00f6\u00f8\5O(\2\u00f7\u00f6\3\2\2\2\u00f8"+
		"\u00f9\3\2\2\2\u00f9\u00f7\3\2\2\2\u00f9\u00fa\3\2\2\2\u00faF\3\2\2\2"+
		"\u00fb\u00fc\7h\2\2\u00fc\u00fd\7c\2\2\u00fd\u00fe\7n\2\2\u00fe\u00ff"+
		"\7u\2\2\u00ff\u0105\7g\2\2\u0100\u0101\7v\2\2\u0101\u0102\7t\2\2\u0102"+
		"\u0103\7w\2\2\u0103\u0105\7g\2\2\u0104\u00fb\3\2\2\2\u0104\u0100\3\2\2"+
		"\2\u0105H\3\2\2\2\u0106\u010b\5K&\2\u0107\u010a\5K&\2\u0108\u010a\5M\'"+
		"\2\u0109\u0107\3\2\2\2\u0109\u0108\3\2\2\2\u010a\u010d\3\2\2\2\u010b\u0109"+
		"\3\2\2\2\u010b\u010c\3\2\2\2\u010cJ\3\2\2\2\u010d\u010b\3\2\2\2\u010e"+
		"\u010f\t\4\2\2\u010fL\3\2\2\2\u0110\u0111\4\62;\2\u0111N\3\2\2\2\u0112"+
		"\u0115\5M\'\2\u0113\u0115\t\5\2\2\u0114\u0112\3\2\2\2\u0114\u0113\3\2"+
		"\2\2\u0115P\3\2\2\2\u0116\u0117\7\61\2\2\u0117\u0118\7,\2\2\u0118\u011c"+
		"\3\2\2\2\u0119\u011b\13\2\2\2\u011a\u0119\3\2\2\2\u011b\u011e\3\2\2\2"+
		"\u011c\u011d\3\2\2\2\u011c\u011a\3\2\2\2\u011d\u011f\3\2\2\2\u011e\u011c"+
		"\3\2\2\2\u011f\u0120\7,\2\2\u0120\u0121\7\61\2\2\u0121\u0122\3\2\2\2\u0122"+
		"\u0123\b)\2\2\u0123R\3\2\2\2\u0124\u0125\7\61\2\2\u0125\u0126\7\61\2\2"+
		"\u0126\u012a\3\2\2\2\u0127\u0129\n\6\2\2\u0128\u0127\3\2\2\2\u0129\u012c"+
		"\3\2\2\2\u012a\u0128\3\2\2\2\u012a\u012b\3\2\2\2\u012b\u012d\3\2\2\2\u012c"+
		"\u012a\3\2\2\2\u012d\u012e\b*\2\2\u012eT\3\2\2\2\u012f\u0131\t\7\2\2\u0130"+
		"\u012f\3\2\2\2\u0131\u0132\3\2\2\2\u0132\u0130\3\2\2\2\u0132\u0133\3\2"+
		"\2\2\u0133\u0134\3\2\2\2\u0134\u0135\b+\2\2\u0135V\3\2\2\2\u0136\u0137"+
		"\13\2\2\2\u0137X\3\2\2\2\23\2\u00bc\u00c8\u00ce\u00e0\u00e4\u00eb\u00ee"+
		"\u00f4\u00f9\u0104\u0109\u010b\u0114\u011c\u012a\u0132\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}