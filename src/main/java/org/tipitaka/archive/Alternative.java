package org.tipitaka.archive;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.tipitaka.archive.Notes.Version;

/**
 * Created by cmeier on 4/4/16.
 */
class Alternative
{

  private final String separator;

  private String text;

  public String getExtra() {
    return extra;
  }

  public Set<Entry<String, String>> entrySet() {
    return map.entrySet();
  }

  private final String extra;

  private Map<String, String> map = new LinkedHashMap<>();

  private String current;

  public Alternative(final String extra, final boolean separator) {
    this.extra = "null".equals(extra) ? null : extra;
    this.separator = separator ? ", " : " ";
  }


  public void setText(final String text) {
    this.text = text;
  }

  public String getText() {
    if (this.text != null) {
      return this.text;
    }

    String result = map.remove(current);
    if (result == null) {
      result = map.remove(Version.VIPASSANA_RESEARCH_INSTITUT.getName());
    }
    return result;
  }

  public void add(final String version, final String text) {
    map.put(version, text);
  }

  public void setCurrent(final String current) {
    this.current = current;
  }

  public int longest() {
    int max = 0;
    for (Entry<String,String> entry: map.entrySet()){
      int len = entry.getKey().length() + entry.getValue().length();
      if (len > max) max = len;
    }
    return max;
  }

  public String getNote() {
    Map<String, String> inverseMap = new LinkedHashMap<>();
    for (Entry<String, String> entry: map.entrySet()) {
      String text = entry.getValue();
      String version = entry.getKey();
      if (inverseMap.containsKey(text)) {
        String versions = inverseMap.get(text) + " " + version;
        inverseMap.put(text, versions);
      }
      else {
        inverseMap.put(text, version);
      }
    }

    StringBuilder result = new StringBuilder();
    boolean first = true;
    for (Map.Entry entry : inverseMap.entrySet()) {
      if (first) {
        first = false;
      }
      else {
        result.append(separator);
      }
      result.append((entry.getKey())).append(" (").append(entry.getValue()).append(")");
    }
    if (extra != null) result.append(extra);
    return result.toString();
  }
}
