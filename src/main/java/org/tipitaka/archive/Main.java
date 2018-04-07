package org.tipitaka.archive;

import java.io.File;

import org.tipitaka.archive.converter.ApiBuilder;
import org.tipitaka.archive.converter.NotesProcessor;

public class Main {

  enum Command {
    API, NOTES
  }

  public static void main(String... args) throws Exception {
    if( args.length != 1){
      showHelp();
    }
    switch(Command.valueOf(args[0])) {
    case API: api(args); break;
    case NOTES: notes(args); break;
    default: showHelp();
    }
  }

  private static void api(String... args) throws Exception {
    ApiBuilder builder = new ApiBuilder();
    File path = new File("archive/api");
    builder.build(path);
  }

  private static void notes(String... args) throws Exception {
    NotesProcessor builder = new NotesProcessor();
    File path = new File("archive/notes");
    builder.build(path);
  }

  private static void showHelp() {
    System.err.println("usage:");
    for (Command com : Command.values()) {
      System.err.println("\tjava " + Main.class.getName() + " "
                         + com.toString().toLowerCase() + " <basedir> <script>");
    }
  }
}
