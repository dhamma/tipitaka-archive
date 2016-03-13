package org.tipitaka.archive;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.tipitaka.search.RomanScriptHelper;

/**
 * Created by cmeier on 3/6/16.
 */
class Fuzzy
{

  static List<String> results = new LinkedList<String>();

  static String findMatchingText(final String altText, final String previous) {
    String[] words = previous.trim()
        .replaceFirst("’’ti$", "ti")
        .replaceFirst("’’$", "")
        .replaceAll("[‘–,]", "")
        .replaceAll("  ", " ")
        .split(" ");
    String[] parts = altText.replace(". ", "").split(" ");

    if (previous.length() == 0) return null;

    Map<Integer, String> results = new HashMap<>();

    //System.out.println(previous.length() + "<>" + Arrays.toString(words) + "<>" + Arrays.toString(parts));
    int count = parts.length;

    for (int k = 1; k < count - 1; k++) {
      int sum = 0;
      int l = 0;
      for (int i = 0; i < count; i++) {
        if (i != k) {
          int index = words.length - count + l++ + 1;
          //System.out.println(parts[i] + "<->" + words[index] + " " +Fuzzy.similar(parts[i], words[index]));
          //TODO can it be out of range ?
          if (index > -1) {
            int fuzz = Fuzzy.fuzzy(parts[i], words[index]);
            if (sum > -1 && fuzz < 155) sum += fuzz;
            else sum = -1;
          }
        }
      }
      if (sum > -1 && sum/count < 100 && words.length - count > -1) {
        return String.join(" ", Arrays.copyOfRange(words, words.length - count + 1, words.length));
      }
    }

    if (count > 3 && words.length > 1) {
      for (int k = 1; k < count - 2; k++) {
        if (Fuzzy.similar(parts[count - k], words[words.length - 1]) &&
            Fuzzy.similar(parts[0], words[words.length - 2])) {
          return words[words.length - 2] + " " + words[words.length - 1];
        }
      }
    }

    for (int k = 0; k < count; k++) {
      boolean match = true;
      StringBuilder found = new StringBuilder();
      int sum = 0;
      for (int i = k; i < count; i++) {
        int index = words.length - count + i;
        if (index > -1 && i < parts.length && index < words.length) {
          //System.out.println(parts[i] + "<->" + words[index] + " " +Fuzzy.similar(parts[i], words[index]));
          int fuzz = Fuzzy.fuzzy(parts[i], words[index]);
          if (sum > -1 && fuzz < 155) sum += fuzz;
          else sum = -1;
          match = match && fuzz < 100;
          found.append(words[index]).append(" ");
        }
      }
      if (match) {
        return found.toString().trim();
      }
      if (k == 0 && sum > -1 && sum/count < 100) {
        return found.toString().trim();
      }
    }



    for (int j = count-1; j > 0; j--) {
      //System.out.println("first----------------" + j);
      //boolean match = true;
      //StringBuilder found = new StringBuilder();
      //for (int i = j; i < count; i++) {
      //  int index = words.length - count + i;
      //  System.out.println(words.length + " " + count + " " + i);
      //  if (index > -1 && i < parts.length && index < words.length) {
      //    System.out.println(parts[i] + "<->" + words[index] + " " +Fuzzy.similar(parts[i], words[index]));
      //    match = match && (Fuzzy.similar(parts[i], words[index]));
      //    found.append(words[index]).append(" ");
      //  }
      //  if (!match) {
      //    break;
      //  }
      //}
      //if (match) {
      //  return found.toString().trim();
      //}
      //System.out.println("second----------------" + j);
      //match = j < count;
      //found = new StringBuilder();
      //for (int i = 0; i < j; i++) {
      //  int index = words.length - j + i;
      //  if (index > -1 && i < parts.length && index < words.length) {
      //    System.out.println(parts[i] + "<->" + words[index] + " " + Fuzzy.similar(parts[i], words[index]));
      //    match = match && (Fuzzy.similar(parts[i], words[index]));
      //    found.append(words[index]).append(" ");
      //  }
      //  if (!match) {
      //    break;
      //  }
      //}
      //if (match) {
      //  return found.toString().trim();
      //}


      StringBuilder found = new StringBuilder();
      for (int i = 0; i < j; i++) {
        int index = words.length - j + i;
        if (index > -1) {
          found.append(words[index]).append(" ");
        }
        String text = found.toString().trim();
        if (text.length() > 0) {
          String alt = altText;
          for (int k = count; k > 1; k--) {
            //System.err.println(found + " ~ " + alt + " " + Fuzzy.similar(alt, found.toString()));
            results.put(Fuzzy.fuzzy(alt, text), previous.replaceFirst("^.*" +
                text.replace(")", "[)]").replace("(", "[(]"), text));
            int ind = alt.lastIndexOf(' ');
            alt = alt.substring(0, ind);
          }
        }
      }
      if (!results.isEmpty()) {
        int min = Collections.min(results.keySet());
        if (min < 100) {
          return results.get(min);
        }
      }
    }
    return null;
  }

  static int fuzzy(String first, String second) {
    first = RomanScriptHelper.removeDiacritcals(first.replaceAll("‘|\\.", "")).toLowerCase().replaceAll("(.)\\1", "$1");
    second = RomanScriptHelper.removeDiacritcals(second.replaceAll("‘|\\.", "")).toLowerCase().replaceAll("(.)\\1", "$1");

    String[] firstLetters = first.split("");
    String[] secondLetters = second.split("");
    int firstIndex = -1;
    int lastIndex = -1;
    //System.err.println(Arrays.toString(firstLetters));
    //System.err.println(Arrays.toString(secondLetters));
    //System.err.println();
    int min = Math.min(firstLetters.length, secondLetters.length);
    for (int i = 0; i < min; i++) {
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
    // TODO what about firstDiff
    //System.err.println(firstIndex + " " + lastIndex);
    if (lastIndex == -1) {
      secondDiff = second.substring(0, second.length() - min);
      if (firstIndex > -1) {
        firstDiff = first.substring(0, first.length() - min);
      }
    }
    else if (firstIndex == -1) {
      secondDiff = second.substring(min);
    }
    else {
      if (firstIndex + lastIndex < first.length()) {
        firstDiff = first.substring(firstIndex, first.length() - lastIndex);
      }
      if (firstIndex + lastIndex < second.length()) {
        secondDiff = second.substring(firstIndex, second.length() - lastIndex);
      }
    }
    int firstResult = (100 * firstDiff.length()) / first.length();
    int secondResult = (100 * secondDiff.length()) / second.length();
    String result = first + "/" + firstDiff + " <> " + second.replaceAll("‘|’", "") + "/" + secondDiff + " " +
        (firstResult) +
        " + " + (secondResult) + " = " + (firstResult + secondResult);
    //System.err.println(result);
    //results.add(result);
    return firstResult + secondResult;
  }

  static boolean similar(String first, String second) {
    return fuzzy(first, second) < 100;
  }
}
