package com.flakks.spelling.service;



import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.flakks.spelling.Dictionary;
import com.flakks.spelling.QueryMapper;
import com.flakks.spelling.SpecialRule;
import com.flakks.spelling.SpellingLookup;
import com.flakks.spelling.SpellingSuggestor;
import com.flakks.spelling.Suggestion;
import com.flakks.spelling.TrieNode;

public class SpellApp {
	public static Map<String, Dictionary> dictionaries;
	public static Map<String, TrieNode> trieNodes;
	public static Map<String, String> indoRootDict;

	public static void initIndo(String indo_dict_path, String root_indo_dict) {
		initIndo(indo_dict_path);
		initRootIndo(root_indo_dict);
	}
	public static void initIndo(String indo_dict_path) {
		List<String> lines = new ArrayList<String>();

		try {
			lines.addAll(Files.readAllLines(Paths.get(indo_dict_path),StandardCharsets.UTF_8));
		} catch (IOException e) {
			e.printStackTrace();
		}

		dictionaries = createIndoDictionary(lines);
		trieNodes = createTrieNodes(dictionaries);
	}
	
	public static void initRootIndo(String root_indo_dict) {
		indoRootDict = new HashMap<String, String>();
		
		List<String> lines = TextfileIO.readFile(root_indo_dict);
		for(String line : lines) {
			List<String> tokens = TextfileIO.parseLine(line);
			indoRootDict.put(tokens.get(0), tokens.get(1));
		}
	}

	public static Map<String, Dictionary> createIndoDictionary(
			List<String> lines) {
		Map<String, Dictionary> dictionaries = new HashMap<String, Dictionary>();

		Dictionary dictionary;
		dictionary = new Dictionary();
		dictionaries.put("id", dictionary);

		for (String line : lines) {
			String[] columns = line.split(" ");
			dictionary.put(columns[0].trim().toLowerCase(),
					Integer.parseInt(columns[1]));
		}

		return dictionaries;
	}

	public static Map<String, Dictionary> createDictionaries(List<String> lines) {
		Map<String, Dictionary> dictionaries = new HashMap<String, Dictionary>();

		for (String line : lines) {
			String[] columns = line.split("\t");

			Dictionary dictionary = dictionaries.get(columns[0]);

			if (dictionary == null) {
				dictionary = new Dictionary();
				dictionaries.put(columns[0], dictionary);
			}

			dictionary.put(columns[1].trim().toLowerCase(),
					Integer.parseInt(columns[2]));
		}

		return dictionaries;
	}

	public static Map<String, TrieNode> createTrieNodes(
			Map<String, Dictionary> dictionaries) {
		Map<String, TrieNode> rootNodes = new HashMap<String, TrieNode>();

		for (Map.Entry<String, Dictionary> entry : dictionaries.entrySet()) {
			TrieNode rootNode = rootNodes.get(entry.getKey());

			if (rootNode == null) {
				rootNode = new TrieNode();
				rootNodes.put(entry.getKey(), rootNode);
			}

			for (Map.Entry<String, Integer> dictionaryEntry : entry.getValue()
					.entrySet()) {
				rootNode.insert(dictionaryEntry.getKey(),
						dictionaryEntry.getValue());
			}
		}

		return rootNodes;
	}
	
	public static void initAll(String[] files) {
		List<String> lines = new ArrayList<String>();

		for (int i = 0; i < files.length; i++) {
			try {
				lines.addAll(Files.readAllLines(Paths.get(files[i]),StandardCharsets.UTF_8));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		dictionaries = createDictionaries(lines);
		trieNodes = createTrieNodes(dictionaries);
	}
	
	
	public static String indoCorrect(String word) {
		String result = word;
		
		if(indoRootDict.containsKey(word)) {
			return indoRootDict.get(word);
		}
		
		return result;
	}
	
	public static String indoTrieCorrect(String word) {
		long time = System.currentTimeMillis();
		String locale = "id";
		String query = word.toLowerCase();
		
		query = SpecialRule.deduplicates(query);
		SpellingLookup spellingLookup = new SpellingLookup(locale);
		QueryMapper queryMapper = new QueryMapper(spellingLookup);
			
		String mappedQuery = queryMapper.map(query);
			
		time = System.currentTimeMillis() - time;
		
		//System.out.println(mappedQuery + " " + time + " " + spellingLookup.getSumDistance());
		
		return mappedQuery;
	}
	
	public static List<Suggestion> indoSuggestList(String word) {
		String locale = "id";
		String query = word;
		
		List<Suggestion> suggestions = new SpellingSuggestor(locale).suggest(query);
		return suggestions;
	}
	
	public static JSONObject indoSuggest(String word) {
		long time = System.currentTimeMillis();
		String locale = "id";
		String query = word;
		
		List<Suggestion> suggestions = new SpellingSuggestor(locale).suggest(query);
		JSONArray jsonSuggestions = new JSONArray();

		for(Suggestion suggestion : suggestions)
			jsonSuggestions.put(new JSONObject().put("query", suggestion.getToken()).put("frequency", suggestion.getFrequency()));
	
		time = System.currentTimeMillis() - time;
					
		JSONObject json = new JSONObject();
		json.put("suggestions", jsonSuggestions);
		json.put("took", time);
		
		return json;
	}

	public static void main(String[] args) throws Exception {
		String indo_dict_path = "nlp_data/indo_dict/id_full.txt";
		SpellApp.initIndo(indo_dict_path);

		new HttpServer().start();
	}
}