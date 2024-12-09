package com.skapp.community.peopleplanner.util;

public class PeopleUtil {

	private PeopleUtil() {
		throw new IllegalStateException("Illegal instantiate");
	}

	/**
	 * return the given word with first letter uppercase
	 * @param word input word
	 * @return word
	 */
	public static String makeFirstLetterUpper(String word) {
		return word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase();
	}

	public static String getSearchString(String keyword) {
		return keyword.toLowerCase() + "%";
	}

}
