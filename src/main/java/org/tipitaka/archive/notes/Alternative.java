package org.tipitaka.archive.notes;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;

/**
 * Created by cmeier on 3/12/16.
 */

public class Alternative
{
  @JacksonXmlProperty(isAttribute = true, localName = "source-abbr")
  public String sourceAbbreviation;

  @JacksonXmlProperty(isAttribute = true)
  public String source;

  @JacksonXmlText
  public String text;

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Alternative)) {
      return false;
    }

    Alternative that = (Alternative) o;

    if (!sourceAbbreviation.equals(that.sourceAbbreviation)) {
      return false;
    }
    return text.equals(that.text);

  }

  @Override
  public int hashCode() {
    int result = sourceAbbreviation.hashCode();
    result = 31 * result + text.hashCode();
    return result;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("Alternative{");
    sb.append("lang='").append(sourceAbbreviation).append('\'');
    sb.append(", text='").append(text).append('\'');
    sb.append('}');
    return sb.toString();
  }
}
