package de.thm.mni.compilerbau.phases._01_scanner

enum class TokenType {
    TYPE, PROC, ARRAY, OF, REF, VAR, IF, ELSE, WHILE,
    PAR_L, PAR_R, BRA_L, BRA_R, CUR_L, CUR_R,
    OP_EQ, OP_NE, OP_LT, OP_LE, OP_GT, OP_GE, OP_ADD, OP_SUB, OP_MUL, OP_DIV,
    COLON, COMMA, SEMIC, ASSIGN,
    IDENTIFIER,
    INTEGER,
    EOF,
    ERROR
}