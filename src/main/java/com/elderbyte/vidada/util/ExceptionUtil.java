package com.elderbyte.vidada.util;

/**
 * Created by IsNull on 20.11.14.
 */
public final class ExceptionUtil {
    private ExceptionUtil(){ }

    public static String composeUserReadableMessage(Throwable e){
        return composeUserReadableMessage(e, new StringBuilder()).toString();
    }

    private static StringBuilder composeUserReadableMessage(Throwable e, StringBuilder sb){
        sb.append(e.getMessage());
        if(e.getCause() != null){
            sb.append(" ");
            return composeUserReadableMessage(e.getCause(), sb);
        }else {
            return sb;
        }
    }
}
