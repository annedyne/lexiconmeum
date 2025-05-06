package com.annepolis.lexiconmeum.textsearch;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A basic in-memory TextSearchIndex implementation for efficient prefix-based string lookup.
 *
 * <p>This implementation can also be used for suffix-based lookup by reversing
 * the input words before insertion, and reversing the results after retrieval.
 * This allows a single TextSearchIndex structure to support both prefix and suffix queries
 * without changing the core logic.
 *
 * <p>Example usage for suffix search:
 * <pre>
 * // Insert reversed words into the trie
 * for (String word : words) {
 *     trie.insert(new StringBuilder(word).reverse().toString());
 * }
 *
 * // Search using reversed suffix, then reverse the results
 * List<String> reversedResults = trie.search(new StringBuilder("ing").reverse().toString(), 10);
 * List<String> results = reversedResults.stream()
 *     .map(w -> new StringBuilder(w).reverse().toString())
 *     .collect(Collectors.toList());
 * </pre>
 *
 * <p>This approach avoids the need for a separate Suffix TextSearchIndex structure and keeps
 * the implementation simple and reusable.
 */

@Component
class TextSearchIndex {

    static final Logger logger = LogManager.getLogger(TextSearchIndex.class);

    private final TrieNode root;// Root node of the TextSearchIndex
    TrieNode getRoot() {
        return root;
    }

    public TextSearchIndex(){
        root = new TrieNode();
    }

    /**
     * Inserts a word into the TextSearchIndex.
     * Each character of the word is stored in a linked structure.
     */
    public void insert(String word) {
        TrieNode node = getRoot();
        for (char ch : word.toCharArray()) {

            //Normalize key so we can search without adding macrons
            String normalized = Normalizer.normalize(Character.toString(ch), Normalizer.Form.NFD);
            Character normalizedKey = normalized.charAt(0);

            //if key doesn't exist in child-map yet, add it with new associated node
            node.getChildren().putIfAbsent(normalizedKey, new TrieNode());

            //populate the node associated with the current char
            TrieNode child = node.getChildren().get(normalizedKey);
            child.setParent(node);

            //set content with non-normalized character
            child.setContent(ch);

            //traverse to next level
            node = child;
        }
        //mark end of word
        node.setEndOfWord(true);
    }


    public void insert(List<String> words){
        for(String word: words){
            insert(word);
        }
    }

    /**
     * Searches for words that start with a given prefix.
     * @param prefix The prefix to search for.
     * @param limit The maximum number of results to return.
     * @return A list of words that start with the given prefix.
     */

    public List<String> search(String prefix, int limit) {
        List<String> results = new ArrayList<>();
        TrieNode node = getRoot();

        // Navigate down the trie searching for each character of the prefix array
        for (char ch : prefix.toCharArray()) { //word
            if (!node.getChildren().containsKey(ch)) {
                // If the prefix is not found, return an empty list
                return results;
            }
            //traverse to down the tree to the next node/char in the prefix
            node = node.getChildren().get(ch);
        }

        // Once we've reached the end of the prefix,
        // recursively collect all the chars/words branching off from it
        dfs(node, new StringBuilder(prefix), results, limit);
        return results;
    }

    /**
     * Depth-First Search (DFS) helper function to collect words from the TextSearchIndex.
     * @param node The current TrieNode being explored.
     * @param prefix The string built so far (represents the word in progress).
     * @param prefixMatchResults The list of found words.
     * @param wordLimit The maximum number of words to collect.
     */
        private void dfs(TrieNode node, StringBuilder prefix, List<String> prefixMatchResults, int wordLimit) {
        if (prefixMatchResults.size() >= wordLimit) {
            return;
        }

        if (node.isEndOfWord()) {
            // If we reach a valid word, add it to the prefixMatchResults
            prefixMatchResults.add(prefix.toString());
            logger.info("adding word: " + prefix);
        }

        // navigate recursively down each word/branch of this prefix,
        // collecting its letters and appending them to our prefix
        for (var nodeEntry : node.getChildren().entrySet()) {
            prefix.append(nodeEntry.getValue().getContent());
            logger.debug("appending " + nodeEntry.getValue().getContent());

            // traverse to next node
            dfs(nodeEntry.getValue(), prefix, prefixMatchResults, wordLimit);

            // We've reached our word limit or last node in this branch
            // and appended the current word to our results (above)
            // so delete each char of this branch from prefix as we backtrack,
            // in readiness for adding the next branch/word to the common prefix
            prefix.deleteCharAt(prefix.length() - 1);
            logger.debug("backtracking to parent prefix: " + prefix );
        }
    }

    private static class TrieNode {
        private final Map<Character, TrieNode> children = new HashMap<>();
        private TrieNode parent;
        private char content;
        private boolean isEndOfWord;

        public Map<Character, TrieNode> getChildren() {
            return children;
        }

        public void setParent(TrieNode parent) {
            this.parent = parent;
        }

        public char getContent() {
            return content;
        }

        public void setContent(char content) {
            this.content = content;
        }

        public boolean isEndOfWord() {
            return isEndOfWord;
        }

        public void setEndOfWord(boolean isEndOfWord) {
            this.isEndOfWord = isEndOfWord;
        }
    }
}
