package com.elderbyte.common;

/**
 * Created by isnull on 31/03/16.
 */
public class NotSupportedException extends RuntimeException{
    public NotSupportedException(){

    }
    public NotSupportedException(String message){
        super(message);
    }
    public NotSupportedException(String message, Throwable e){
        super(message, e);
    }
}
