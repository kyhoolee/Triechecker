package com.flakks.spelling;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SpecialRule {
	
	public static String deduplicates(String origin) {
		String result = "";
		
		List<Set<Character>> table = new ArrayList<>();
		Set<Character> current = new HashSet<>();
		for(int i = 0 ; i < origin.length() ; i ++) {
			Character c = origin.charAt(i);
			if(table.size() == 0) {
				current.add(c);
				table.add(current);
				result += c;
			} else {
				if(current.size() > 0) {
					if(current.contains(c)) {
						// Nothing to do
					} else {
						current = new HashSet<>();
						current.add(c);
						table.add(current);
						result += c;
					}
				} else {
					current.add(c);
					table.add(current);
				}
			}
		}
		
		
		return result;
		
	}

}
