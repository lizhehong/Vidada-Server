package com.elderbyte.ffmpeg;

import java.util.concurrent.TimeoutException;

public interface IShellExecutor {

    /**
     * Execute the given shell command. The new created process is returned.
     * This call is not blocking. Consider using {@link #executeAndWait}
     * if you want to wait upon process completion.
     * @param command
     * @return
     */
    Process execute(final String[] command);

    /**
     * Execute the given command and wait until the created process has completed
     * or the timeout is reached.
     *
     * @param command The command to execute
     * @param output Collect the output of the process in this string builder
     * @param timeout The timeout (Milliseconds) when the process will be canceled.
     * @return
     * @throws TimeoutException
     */
    int executeAndWait(String[] command, StringBuilder output, long timeout) throws TimeoutException;

}
