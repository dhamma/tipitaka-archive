package org.tipitaka.archive;

import java.io.File;

import org.tipitaka.archive.converter.ApiBuilder;

public class Main {

    enum Command {
        API
    }

    public static void main(String... args) throws Exception {
        if( args.length == 0){
            showHelp();
        }
        switch(Command.valueOf(args[0])) {
        case API: api(args); break;
        default: showHelp();
        }
    }

    private static void api(String... args) throws Exception {
        ApiBuilder api = new ApiBuilder();
        File path = new File("archive/api");
        api.build(path);
    }

    private static void showHelp() {
        System.err.println("usage:");
        for (Command com : Command.values()) {
            System.err.println("\tjava " + Main.class.getName() + " "
                    + com.toString().toLowerCase() + " <basedir> <script>");
        }
    }
}
