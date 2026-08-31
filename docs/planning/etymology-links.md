# Problem to solve:

Currently, identical, looking autocomplete suggestions can appear in the suggestion drop-down on the front-end.
This happens when both these conditions exist:
- There are separate etymologies of the same 'word' (i.e. the same string of letters) which makes 
them unique 'lexemes'
- Two or more of them are of the same part-of-speech (pos) which means they are indistinguishable in the suggestion 
  drop-down which only shows the word and its part-of-speech.

This is a bad user-experience because you don't know which is which at a glance, and you have to click on each 
individually to see the details. This is especially annoying when you know what definition you're looking for, so 
you just have to click until you get to the one you want.

# Solutions
Since the first etymology is usually the most common one, we could return only the first etymology as a suggestion 
and add links to the other etymologies with the 'etymology' text in the first etymology's details page, so the user 
could click to the other etymologies easily from there.


# Model Update 
Add references to other etymologies of a given 'lemma' in each associated Lexeme. Should we include them as a list 
of Lexemes, or as a list of Lexeme ids? Or as a list of 'Etymology Links'? List of lexemes is the most purely DDD I 
guess. A list of id's maybe leaks the 'data model' into the 'object model'. List of 'Etymology Link' leaks the 
front-end model into the back-end. 