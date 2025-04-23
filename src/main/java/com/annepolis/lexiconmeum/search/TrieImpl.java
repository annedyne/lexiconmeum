package com.annepolis.lexiconmeum.search;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TrieImpl implements Trie {

    static final Logger logger = LogManager.getLogger(TrieImpl.class);

    private final TrieNode root;// Root node of the Trie
    TrieNode getRoot() {
        return root;
    }

    public TrieImpl (TrieNode root){
        this.root = root;
    }

    /**
     * Inserts a word into the Trie.
     * Each character of the word is stored in a linked structure.
     */
    @Override
    public void insert(String word) {
        TrieNode node = getRoot();
        for (char ch : word.toCharArray()) {
            //if key doesn't exist yet, add it with associated node
            node.getChildren().putIfAbsent(ch, new TrieNodeImpl());
            TrieNode child = node.getChildren().get(ch);
            child.setParent(node);
            child.setContent(ch);

            //traverse to next level
            node = child;
        }
        //mark end of word
        node.setEndOfWord(true);
    }

    @Override
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
    @Override
    public List<String> search(String prefix, int limit) {
        List<String> results = new ArrayList<>();
        TrieNode node = getRoot();

        // Navigate through the Trie to the last character of the prefix
        for (char ch : prefix.toCharArray()) { //word
            if (!node.getChildren().containsKey(ch)) {
                // If the prefix is not found, return an empty list
                return results;
            }
            //traverse to child containing this prefix character
            node = node.getChildren().get(ch);

        }

        // Perform a depth-first search (DFS) to collect all words
        // starting with this prefix
        dfs(node, new StringBuilder(prefix), results, limit);
        return results;
    }

    /**
     * Depth-First Search (DFS) helper function to collect words from the Trie.
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

        // Continue to pick up and append all characters under this prefix
        for (var nodeEntry : node.getChildren().entrySet()) {
            prefix.append(nodeEntry.getKey()); // Append character
            logger.info("appending " + nodeEntry.getKey());

            // traverse to next node
            dfs(nodeEntry.getValue(), prefix, prefixMatchResults, wordLimit);
            prefix.deleteCharAt(prefix.length() - 1); // Backtrack to parent  (remove last character)
            logger.info("backtracking to parent prefix: " + prefix );
        }
    }
}
