package com.annepolis.lexiconmeum.webapi.bff.textsearch.index;

import com.annepolis.lexiconmeum.webapi.bff.textsearch.domain.FormMatch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.util.*;

/**
 * A basic in-memory TrieAutocompleteIndex implementation for efficient prefix-based string lookup.
 *
 * <p>This implementation can also be used for suffix-based lookup by reversing
 * the input words before insertion, and reversing the results after retrieval.
 * This allows a single TrieAutocompleteIndex structure to support both prefix and suffix queries
 * without changing the core logic.
 *
 */
@Component
class TrieAutocompleteIndex implements AutocompleteIndexBackend {

    static final Logger logger = LogManager.getLogger(TrieAutocompleteIndex.class);

    private final TrieNode root;
    TrieNode getRoot() {
        return root;
    }

    public TrieAutocompleteIndex(){
        root = new TrieNode();
    }

    /**
     * Inserts a word form into the trie for efficient prefix lookups.
     *
     * <p>Each character is normalized to its base code point using Unicode NFD so that diacritics
     * are ignored during indexing and lookup. The original (non-normalized) character is kept
     * in the node content for reconstruction of the stored form when returning matches.
     *
     * <p>Notes:
     * - Input is case-sensitive (no case normalization is performed).
     * - Multiple lexeme IDs can be associated with the same word form; duplicates are de-duplicated.
     * - If {@code wordForm} is null/blank or {@code lexemeId} is null, the insert is ignored and a warning is logged.
     * - Not thread-safe; external synchronization is required for concurrent writes.
     *
     * <p>Complexity: O(n) time and O(n) space in the length of {@code wordForm}.
     *
     * @param wordForm the word form to index
     * @param lexemeId the UUID of the Lexeme to associate with this form
     */
    public void insert(String wordForm, UUID lexemeId) {
        if (wordForm == null || wordForm.isBlank() || lexemeId == null) {
            logger.warn("Ignoring insert of null/blank form or null lexemeId");
            return;
        }

        TrieNode node = getRoot();
        for (char wordCharacter : wordForm.toCharArray()) {

            //Normalize key so we can search without adding macrons
            String normalizedWordCharacter = Normalizer.normalize(Character.toString(wordCharacter), Normalizer.Form.NFD);
            Character normalizedCharacterKey = normalizedWordCharacter.charAt(0);

            //if key doesn't exist in childNode-map yet, add it with new associated node
            node.getCharacterNodeMap().putIfAbsent(normalizedCharacterKey, new TrieNode());

            //populate the node associated with the current char
            TrieNode childNode = node.getCharacterNodeMap().get(normalizedCharacterKey);

            //set content with non-normalized character
            childNode.setContent(wordCharacter);

            //traverse to next level
            node = childNode;
        }
        //mark end of word
        node.setEndOfWord(true);
        node.addLexemeId(lexemeId);
    }

    /**
     * Searches for words that start with a given prefix.
     * @param prefix The prefix to search for.
     * @param limit The maximum number of results to return.
     * @return A list of word forms that start with the given prefix.
     */

    @Override
    public List<FormMatch> searchForMatchingForms(String prefix, int limit) {
        List<FormMatch> results = new ArrayList<>();

        if (prefix == null || prefix.isBlank() || limit <= 0) {
            logger.debug("searchForMatchingForms called with invalid input: prefix='{}', limit={}", prefix, limit);
            return results;
        }

        TrieNode node = getRoot();

        // Navigate down the trie searching for each character of the prefix array
        for (char prefixCharacter : prefix.toCharArray()) {
            if (!node.getCharacterNodeMap().containsKey(prefixCharacter)) {
                // If the prefix is not found, return an empty list
                return results;
            }
            //traverse to down the tree to the next node/char in the prefix
            node = node.getCharacterNodeMap().get(prefixCharacter);
        }

        // Once we've reached the end of the prefix,
        // recursively collect all the chars/words branching off from it
        dfs(node, new StringBuilder(prefix), results, limit);

        return results;
    }


    /**
     * Depth-First Search (DFS) helper function to collect words from the TrieAutocompleteIndex.
     * @param node The current TrieNode being explored.
     * @param prefix The string built so far (represents the word in progress).
     * @param prefixMatchResults The list of found words.
     * @param wordLimit The maximum number of words to collect.
     */
        private void dfs(TrieNode node, StringBuilder prefix, List<FormMatch> prefixMatchResults, int wordLimit) {
        if (prefixMatchResults.size() >= wordLimit) {
            return;
        }

        if (node.isEndOfWord()) {
            // If we reach a valid word form, add it and associated lexeme keys to the prefixMatchResults
            for(UUID lexemeKey : node.getLexemeIds()){
                if (prefixMatchResults.size() >= wordLimit) break;

                FormMatch matchResult = new FormMatch(prefix.toString(), lexemeKey);
                prefixMatchResults.add(matchResult);
                logger.info("adding word: {}", matchResult);
            }
        }

        // navigate recursively down each word/branch of this prefix,
        // collecting its letters and appending them to our prefix
        for (var nodeEntry : node.getCharacterNodeMap().entrySet()) {
            prefix.append(nodeEntry.getValue().getContent());
            logger.debug("appending {}", nodeEntry.getValue().getContent());

            // traverse to next node
            dfs(nodeEntry.getValue(), prefix, prefixMatchResults, wordLimit);

            // We've reached our word limit or last node in this branch
            // and appended the current word to our results (above)
            // so delete each char of this branch from prefix as we backtrack,
            // in readiness for adding the next branch/word to the common prefix
            prefix.deleteCharAt(prefix.length() - 1);
            logger.debug("backtracking to parent prefix: {}", prefix );
        }
    }

    private static class TrieNode {
        private final Map<Character, TrieNode> characterNodeMap = new HashMap<>();
        private char content;
        private boolean isEndOfWord;
        private final Set<UUID> lexemeIds = new HashSet<>();

        public Map<Character, TrieNode> getCharacterNodeMap() {
            return characterNodeMap;
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


        public Set<UUID> getLexemeIds() {
            return lexemeIds;
        }

        public void addLexemeId(UUID key) {
            lexemeIds.add(key);
        }
    }
}
