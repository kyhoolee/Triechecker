#
This is trie-based dictionary
Using for close word-searching

SpellApp:
- SpellApp.initIndo(indo_dict_path)
Initialize indo_dictionary -> CSV format
Each line: word, frequency

- SpellApp.indoTrieCorrect(String word)
Return 1 closest word

- SpellApp.indoSuggestList(String word)
Return List<Suggestion>  