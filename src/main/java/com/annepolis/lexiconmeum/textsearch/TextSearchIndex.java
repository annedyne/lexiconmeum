package com.annepolis.lexiconmeum.textsearch;

import java.util.List;

public interface TextSearchIndex {

    List<String> search(String prefix, int limit);
}
