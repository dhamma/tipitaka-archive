package org.tipitaka.archive.notes;

/**
 * Created by cmeier on 3/12/16.
 */
public enum Version {
  SRI_LANKA("sī."), PALI_TEXT_SOCIETY("pī."), VIPASSANA_RESEARCH_INSTITUT("vri"),
  CAMBODIAN("ka."), THAI("syā."), KAM("kaṃ."), TI("ṭī."), UNKNOWN("?");

  public static final String SUFFIX = "_";

  private final String abbrevation;

  Version(String abbreviation) {
    this.abbrevation = abbreviation;
  }

  String getAbbrevation() {
    return this.abbrevation;
  }

  String getName() {
    return name().toLowerCase().replace("_", " ");
  }

  static Version toVersion(String name) {
    if (name.endsWith(SUFFIX)) name = name.substring(0, name.length() - 1);
    if (UNKNOWN.abbrevation.equals(name)) return UNKNOWN;
    if (VIPASSANA_RESEARCH_INSTITUT.abbrevation.equals(name)) return VIPASSANA_RESEARCH_INSTITUT;
    if (name.endsWith(",")) name = name.substring(0, name.length() - 1);
    if (!name.endsWith(".")) name += ".";
    for (Version version: values()) {
      if (version.getAbbrevation().equals(name)) {
        return version;
      }
    }
    return null;
  }
}
