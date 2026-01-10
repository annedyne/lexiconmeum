package com.annepolis.lexiconmeum.shared.util;

import java.text.Normalizer;


public class Utilities {

    public static String normalizeDiacritics(String text) {
        return Normalizer.normalize(text, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
    }
}
