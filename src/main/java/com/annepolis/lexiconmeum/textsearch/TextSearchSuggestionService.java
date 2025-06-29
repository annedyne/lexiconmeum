package com.annepolis.lexiconmeum.textsearch;

import com.annepolis.lexiconmeum.shared.Lexeme;
import com.annepolis.lexiconmeum.shared.LexemeProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TextSearchSuggestionService implements TextSearchService<TextSearchSuggestionDTO>  {
    static final Logger logger = LogManager.getLogger(TextSearchSuggestionService.class);

    private final TextSearchTrieIndexService trieIndexService;
    private TextSearchSuggestionMapper textSearchSuggestionMapper;
    private final LexemeProvider lexemeProvider;

    public TextSearchSuggestionService (TextSearchTrieIndexService trieIndexService,
                                        TextSearchSuggestionMapper textSearchSuggestionMapper,
                                        LexemeProvider lexemeProvider) {

       this.trieIndexService = trieIndexService;
       this.textSearchSuggestionMapper = textSearchSuggestionMapper;
       this.lexemeProvider = lexemeProvider;
    }

    @Override
    public List<TextSearchSuggestionDTO> getWordsStartingWith(String prefix, int limit) {
        List<String> rawWords =  trieIndexService.getWordsStartingWith(prefix, limit);

        return enrichRawWords(rawWords);
    }

    List<TextSearchSuggestionDTO> enrichRawWords(List<String> rawWords) {
        return rawWords.stream()
                .map(rawWord -> {

                    UUID lexemeId = extractLexemeId(rawWord);
                    String word = extractForm(rawWord);
                    Optional<Lexeme> maybeLexeme = lexemeProvider.getLexemeIfPresent(lexemeId);
                    return maybeLexeme.map(lexeme -> textSearchSuggestionMapper.toTextSearchDTO(word, lexemeId, lexeme.getPosition()));

                })
                .flatMap(Optional::stream)
                .toList();
    }

    private UUID extractLexemeId(String word){
        int colonIndex = word.indexOf(':');
        if (colonIndex == -1 || colonIndex == word.length() - 1) {
            logger.error("warning: malformed form ID string {}", word);
            return null;
        }
        return UUID.fromString(word.substring(colonIndex + 1).trim());
    }
    private String extractForm(String word){
        int colonIndex = word.indexOf(':');
        if (colonIndex <= 0) {
            logger.error("warning: malformed form ID string {}", word);
            return null;
        }
        return word.substring(0, colonIndex).trim();
    }

    private String reverseForm(String formIdString){
        UUID id = extractLexemeId(formIdString);
        int colonIndex = formIdString.indexOf(':');
        String form = formIdString.substring(0, colonIndex).trim();
        return new StringBuilder(form).reverse().append(" : ").append(id).toString();
    }

    @Override
    public List<TextSearchSuggestionDTO> getWordsEndingWith(String suffix, int limit) {
        String reversedSuffix = new StringBuilder(suffix).reverse().toString();
        List<String> rawWords =  trieIndexService.getWordsEndingWith(reversedSuffix, limit);

        List<String> reversedWords = rawWords.stream()
                .map(this::reverseForm)
                .toList();

        return enrichRawWords(reversedWords);
    }
}
