# Enhance Glosses

## Phase 1
The "buildSense" method in the ParserSupport class extracts word glosses from wiktionary jsonl files and populates the glosses array for each Sense in a Lexeme.

In the jsonl for each wiktionary word Each set of gloss elements actually represents an indented list item for a given definition where each parent item of each list item is included in each gloss array. For example:

```json lines
{
  "senses": [
    {
      "raw_glosses": [
        "(literally):",
        "great, large, big; (of things) vast, extensive, spacious (of physical size or quantity)"
      ],
      "glosses": [
        "great, large, big; (of things) vast, extensive, spacious (of physical size or quantity)"
      ]
    },
    {
      "raw_glosses": [
        "(literally):",
        "especially:",
        "great, much, abundant, considerable (of measure, weight, quantity)"
      ],
      "glosses": [
        "especially:",
        "great, much, abundant, considerable (of measure, weight, quantity)"
      ]
    }
  ]
}

```
Currently, DefinitionsSectionContributor flattens them into an array in the LexemDetailResponse DTO which gets serialized to this: 
```json
{
  "definitions": [
    "(literally):",
    "great, large, big; (of things) vast, extensive, spacious (of physical size or quantity)",
    "(literally):",
    "especially:",
    "great, much, abundant, considerable (of measure, weight, quantity)",
    "(literally):",
    "especially:",
    "(rare, of time) synonym of longus, multus",
    "(literally):",
    "especially:",
    "loud, powerful, strong, mighty (of voice)",
    "(figurative):",
    "(in general) great, grand, mighty, noble, lofty, important, of great weight or importance, momentous",
    "(figurative):",
    "(in particular):",
    "advanced in years, of great age, aged (of age, with nātu)",
    "(figurative):",
    "(in particular):",
    "(in specifications of value, in the neutral absolute) high, dear, of great value, at a high price"
  ]
}
```
But we want to properly reflect the hierarchical structure of the glosses so that they look more like this (minus the numbering):

1. (literally):
    1. great, large, big; (of things) vast, extensive, spacious (of physical size or quantity)
    2. especially:
        1. great, much, abundant, considerable (of measure, weight, quantity)
        2. (rare, of time) synonym of longus, multus 
        3. loud, powerful, strong, mighty (of voice)

Response JSON Example:
```json
{
  "definitions": [
    {
      "text": "(literally):",
      "children": [
        {
          "text": "great, large, big; (of things) vast, extensive, spacious (of physical size or quantity)"
        },
        {
          "text": "especially:",
          "children": [
            {
              "text": "great, much, abundant, considerable (of measure, weight, quantity)"
            },
            {
              "text": "(rare, of time) synonym of longus, multus"
            },
            {
              "text": "loud, powerful, strong, mighty (of voice)"
            }
          ]
        }
      ]
    }
    ]
  }
```
## Fallback to 'glosses'

Not all Wiktionary sense nodes include a `raw_glosses` field (e.g. `amo`). In `buildSense`, after checking `raw_glosses`, fall back to the `glosses` array if `raw_glosses` is absent or empty. This keeps backward compatibility for entries that only have `glosses`, while preferring the richer `raw_glosses` data when available.


## Phase 2

### Add 'Short Definition'

Add a sibling node 'shortDefinition' to 'definitions' DTO. Populate it from the last element of the glosses array from the first sense in the Lexeme. (See DefinitionsSectionContributor class)

resulting JSON:
```json
{
  "shortDefinition": "to love, like; to be fond of",
  "definitions": [
    {
      "text": "(literally):",
      "children": [ { "text": "great, large, big; ..." } ]
    }
  ]
}
```

Add a test that checks that 'DefinitionsSectionContributor' populates shortDefinition' field. You probably need to add a DefinitionsSectionContributorTest class 