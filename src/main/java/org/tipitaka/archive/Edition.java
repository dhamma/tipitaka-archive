package org.tipitaka.archive;

/**
 * Created by cmeier on 4/1/16.
 */
enum Edition
{
  MYANMAR("Myanmar"),
  PALI_TEXT_SOCIETY("Pali Text Society"),
  VIPASSANA_RESOURCE_INSTITUT("Vipassana Research Institut"),
  THAI("Thai");

  private final String name;


  Edition(String name) {
    this.name = name;
  }

  static Edition from(String name) {
    // use the first letter to find the right edition
    name = name.substring(0, 1);
    for (Edition edition : values()) {
      if (edition.name.startsWith(name)) {
        return edition;
      }
    }
    return null;
  }

  String display(){
    return name;
  }
}
