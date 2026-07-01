## Enhance Glosses

The "buildSense" method in the ParserSupport class extracts word glosses from wiktionary jsonl files and populates the glosses array for each Sense in a Lexeme.

In the jsonl for each wiktionary word Each set of gloss elements actually represents an indented list item for a given definition where each parent item of each list item is included in each gloss array. For example:

```json lines
{
  "raw_glosses": [
    "(literally):",
    "great, large, big; (of things) vast, extensive, spacious (of physical size or quantity)"
  ],
}
{
  "raw_glosses": [
    "(literally):",
    "especially:",
    "great, much, abundant, considerable (of measure, weight, quantity)"
  ],
}
{
  "raw_glosses": [
    "(literally):",
    "especially:",
    "(rare, of time) synonym of longus, multus"
  ]
}
{
  "raw_glosses": [
    "(literally):",
    "especially:",
    "loud, powerful, strong, mighty (of voice)"
  ],
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
