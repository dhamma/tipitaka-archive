package org.tipitaka.archive;

import java.io.File;

import org.tipitaka.archive.creators.ApiCreator;
import org.tipitaka.archive.creators.NotesCreator;

public class Main {

  enum Command {
    API, NOTES
  }

  public static void main(String... args) throws Exception {
    if( args.length == 0){
      showHelp();
    }
    switch(Command.valueOf(args[0])) {
    case API: api(args); break;
    case NOTES: notes(args); break;
    default: showHelp();
    }
  }

  private static void api(String... args) throws Exception {
    ApiCreator creator = new ApiCreator();
    File path = new File("archive/api");
    creator.create(path);
  }

  private static void notes(String... args) throws Exception {
    NotesCreator creator = new NotesCreator();
    File path = new File("archive/notes");
    boolean interactive = args.length == 2 && args[1].contains("interactive");
    creator.create(path, interactive);
  }

  private static void showHelp() {
    System.err.println("usage:");
    for (Command com : Command.values()) {
      System.err.println("\tjava " + Main.class.getName() + " "
                         + com.toString().toLowerCase());
    }
  }
}
