package com.annepolis.lexiconmeum.webapi.bff.lexemedetail.dtoassembly;

import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalCase;
import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalGender;
import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.MorphologicalSubtype;
import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.PartOfSpeech;
import com.annepolis.lexiconmeum.shared.model.grammar.partofspeech.SyntacticSubtype;
import com.annepolis.lexiconmeum.webapi.bff.lexemedetail.dtoassembly.inflection.InflectionTableDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Schema(description = "Contains details of a given lexeme.")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LexemeDetailResponse {

    String lemma;
    UUID lexemeId;

    @Schema(example = "VERB")
    PartOfSpeech partOfSpeech;

    @Schema( description = "Omitted when null.", example = "INTERROGATIVE")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    SyntacticSubtype syntacticSubtype;

    @Schema( description = "Omitted when null.", example = "DEPONENT")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    MorphologicalSubtype morphologicalSubtype;

    @Schema( description = "Omitted when null.")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    GrammaticalGender grammaticalGender;

    @Schema( description = "Omitted when null.")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    GrammaticalCase governedCase;

    @Schema(description = "omitted when empty", example = """
           ["to love", "to be fond of, like, admire"]
           """)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    List<String> definitions = new ArrayList<>();

    //INFLECTION RELATED FIELDS
    @Schema(example = "1st conjugation")
    String inflectionClass;

    @Schema( description = "For verbs only. omitted when empty", example = """
           [
                    "amō",
                    "amāre",
                    "amāvī",
                    "amātus sum"
           ]
           """)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    List<String> principalParts = new ArrayList<>();

    @Schema(
            description = "Polymorphic inflection table",
            example = """
                    {
                      "type": "VerbForms",
                      "conjugations": [
                        {
                          "type": "ConjugationTableDTO",
                          "mood": "Indicative",
                          "voice": "ACTIVE",
                          "tenses": [
                            {
                              "altName": "Simple Present",
                              "defaultName": "Present",
                              "forms": ["amō","amās","amat", "amāmus","amātis","amant"]
                            },
                            {
                              "altName": "Past Progressive",
                              "defaultName": "Imperfect",
                              "forms": ["amābam","amābās", "amābat","amābāmus", "amābātis", "amābant"
                              ]
                            }
                          ]
                        }
                      ],
                      "participles": [
                        {
                          "type": "ParticipleTableDTO",
                          "gender": "masculine",
                          "tenses": [
                            {
                              "altName": "Present Active Participle",
                              "defaultName": "Present Active Participle",
                              "declensions": {
                                "SINGULAR": {
                                  "NOMINATIVE": "amāns",
                                  "GENITIVE": "amantis",
                                  "DATIVE": "amantī",
                                  "ACCUSATIVE": "amantem",
                                  "ABLATIVE": "amante",
                                  "VOCATIVE": "amāns"
                                },
                                "PLURAL": {
                                  "NOMINATIVE": "amantēs",
                                  "GENITIVE": "amantium",
                                  "DATIVE": "amantibus",
                                  "ACCUSATIVE": "amantēs",
                                  "ABLATIVE": "amantibus",
                                  "VOCATIVE": "amantēs"
                                }
                              }
                            }
                          ]
                        }
                      ]
                    }
                    """
    )
    @JsonProperty("inflectionTable")
    InflectionTableDTO inflectionTableDTO;

    public String getLemma() {
        return lemma;
    }

    public void setLemma(String lemma) {
        this.lemma = lemma;
    }

    public PartOfSpeech getPartOfSpeech() {
        return partOfSpeech;
    }

    public void setPartOfSpeech(PartOfSpeech partOfSpeech) {
        this.partOfSpeech = partOfSpeech;
    }

    public SyntacticSubtype getSyntacticSubtype() {
        return syntacticSubtype;
    }

    public void setSyntacticSubtype(SyntacticSubtype syntacticSubtype) {
        this.syntacticSubtype = syntacticSubtype;
    }

    public MorphologicalSubtype getMorphologicalSubtype() {
        return morphologicalSubtype;
    }

    public void setMorphologicalSubtype(MorphologicalSubtype morphologicalSubtype) {
        this.morphologicalSubtype = morphologicalSubtype;
    }

    public UUID getLexemeId() {
        return lexemeId;
    }

    public void setLexemeId(UUID lexemeId) {
        this.lexemeId = lexemeId;
    }

    public GrammaticalGender getGrammaticalGender() {
        return grammaticalGender;
    }

    public void setGrammaticalGender(GrammaticalGender grammaticalGender) {
        this.grammaticalGender = grammaticalGender;
    }

    public GrammaticalCase getGovernedCase() {
        return governedCase;
    }

    public void setGovernedCase(GrammaticalCase governedCase) {
        this.governedCase = governedCase;
    }

    public List<String> getDefinitions() {
        return definitions;
    }

    public void addDefinition(String definition) {
        this.getDefinitions().add(definition);
    }


    public String getInflectionClass() {
        return inflectionClass;
    }

    public void setInflectionClass(String inflectionClass) {
        this.inflectionClass = inflectionClass;
    }

    public List<String> getPrincipalParts() {
        return principalParts;
    }

    public void addPrincipalPart(String principalPart) {
        this.getPrincipalParts().add(principalPart);
    }


    public InflectionTableDTO getInflectionTableDTO() {
        return inflectionTableDTO;
    }

    public void setInflectionTableDTO(InflectionTableDTO inflectionTableDTO) {
        this.inflectionTableDTO = inflectionTableDTO;
    }
}
