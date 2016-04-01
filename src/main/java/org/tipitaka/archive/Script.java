/**
 *
 */
package org.tipitaka.archive;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.Properties;

public class Script
{

  public final String name;

  public final String tipitakaOrgName;

  private final Properties words = new Properties();

  Script(String name, String tipitakaOrgName) {
    this.name = name;
    this.tipitakaOrgName = tipitakaOrgName;
  }

  public void put(String key, String value) {
    words.put(key, value);
  }

  public String get(String key) {
    return words.getProperty(key);
  }

  public void save(File file) throws IOException {
    save(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
  }

  public void save(Writer writer) throws IOException {
    words.store(writer, name + " - " + tipitakaOrgName);
  }

  public void load(File file) throws IOException {
    load(new InputStreamReader(new FileInputStream(file), "UTF-8"));
  }

  public void load(Reader reader) throws IOException {
    words.load(reader);
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("Script{");
    sb.append("name='").append(name).append('\'');
    sb.append(", tipitakaOrgName='").append(tipitakaOrgName).append('\'');
    sb.append(", words=").append(words);
    sb.append('}');
    return sb.toString();
  }
}