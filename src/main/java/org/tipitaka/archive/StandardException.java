package org.tipitaka.archive;

public class StandardException extends Exception
{

    public StandardException(){
        super();
    }

    public StandardException(String msg){
        super(msg);
    }

    public StandardException(String msg, Exception e){
        super(msg, e);
    }

}
