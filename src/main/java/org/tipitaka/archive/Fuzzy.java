package org.tipitaka.archive;

import java.util.LinkedList;
import java.util.List;

import org.tipitaka.search.RomanScriptHelper;

/**
 * Created by cmeier on 3/6/16.
 */
class Fuzzy
{

  static List<String> results = new LinkedList<String>();


  static int fuzzy(String first, String second) {
    first = first.replaceAll("‘|’", "");
    second = second.replaceAll("‘|’", "");

    String[] firstLetters = RomanScriptHelper.removeDiacritcals(first).toLowerCase().split("");
    String[] secondLetters = RomanScriptHelper.removeDiacritcals(second).toLowerCase().split("");
    int firstIndex = -1;
    int lastIndex = -1;
    //System.err.print(Arrays.toString(preLetters));
    //System.err.print(Arrays.toString(letters));
    //System.err.print(text);
    for (int i = 0; i < Math.min(firstLetters.length, secondLetters.length); i++) {
      if (firstIndex == -1 && secondLetters[i].charAt(0) != firstLetters[i].charAt(0)) {
        firstIndex = i;
        if (lastIndex > -1) {
          break;
        }
      }
      if (lastIndex == -1 &&
          secondLetters[secondLetters.length - i - 1].charAt(0) !=
              firstLetters[firstLetters.length - i - 1].charAt(0)) {
        lastIndex = i;
        if (firstIndex > -1) {
          break;
        }
      }
    }
    String firstDiff = "";
    String secondDiff = "";
    if (firstIndex > -1 && lastIndex > -1) {
      if (firstIndex + lastIndex < first.length()) {
        firstDiff = first.substring(firstIndex, first.length() - lastIndex);
      }
      if (firstIndex + lastIndex < second.length()) {
        secondDiff = second.substring(firstIndex, second.length() - lastIndex);
      }
    }
    int result = (100 * firstDiff.length()) / first.length() + (100 * secondDiff.length()) / second.length();
    results.add(first + "/" + firstDiff + " <> " + second.replaceAll("‘|’", "") + "/" + secondDiff + " " +
        (firstDiff.length() + secondDiff.length()) +
        " <-> " + (first.length() + second.length()) + " " + result);
    return result;
  }

  static boolean similar(String first, String second) {
    return fuzzy(first, second) < 100;
  }
}
