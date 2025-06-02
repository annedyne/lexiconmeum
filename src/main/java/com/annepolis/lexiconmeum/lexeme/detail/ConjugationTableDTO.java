package com.annepolis.lexiconmeum.lexeme.detail;

import com.annepolis.lexiconmeum.lexeme.detail.grammar.GrammaticalCase;
import com.annepolis.lexiconmeum.lexeme.detail.grammar.GrammaticalNumber;

import java.util.EnumMap;
import java.util.Map;

public class ConjugationTableDTO {

    Map<GrammaticalNumber, Map<GrammaticalCase, String>> table  = new EnumMap<>(GrammaticalNumber.class);

}
