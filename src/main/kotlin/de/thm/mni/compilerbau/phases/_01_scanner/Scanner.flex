package de.thm.mni.compilerbau.phases._01_scanner;

import de.thm.mni.compilerbau.utils.SplError;
import de.thm.mni.compilerbau.phases._02_03_parser.Sym;
import de.thm.mni.compilerbau.absyn.Position;
import de.thm.mni.compilerbau.table.Identifier;
import de.thm.mni.compilerbau.CommandLineOptions;
import java_cup.runtime.*;
%%


%class Scanner
%public
%line
%column
%type Symbol
%cup
%eofval{
    return symbol(Sym.EOF);
%eofval}

%{
    public CommandLineOptions options = null;

       private Symbol symbol(int type) {
         return new Symbol(type, yyline + 1, yycolumn + 1);
       }

       private Symbol symbol(int type, Object value) {
         return new Symbol(type, yyline + 1, yycolumn + 1, value);
       }
%}

%%

// TODO (assignment 1): The regular expressions for all tokens need to be defined here.

[^]		{throw SplError.Companion.IllegalCharacter(new Position(yyline + 1, yycolumn + 1), yytext().charAt(0));}