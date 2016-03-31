package com.elderbyte.ffmpeg;

import java.util.concurrent.TimeoutException;

/**
 * Utility to execute commands in the shell.
 *
 * While you can use the "shell" on most systems, note that paths and generally parameters
 * might get encoded / escaped differently.
 *
 * You have to ensure that passed commands are valid for the current OS.
 *
 * @author IsNull
 *
 */
public class ShellExec {

    private static IShellExecutor shellExecutor = new ShellExecutorImpl();

    /**
     *
     * @param newExecutor
     */
    public static void setShellExecutor(IShellExecutor newExecutor){
        shellExecutor = newExecutor;
    }

	/**
     * Execute the given command and wait until the created process has completed
     * or the timeout is reached.
	 *
     * @param command The command to execute
     * @param output Collect the output of the process in this string builder
     * @param timeout The timeout (Milliseconds) when the process will be canceled.
	 * @return
     *
     * @throws TimeoutException Thrown when the timeout is reached before the process has finished.
	 */
	public static int executeAndWait(
			final String[] command,
			final StringBuilder output,
            final long timeout) throws TimeoutException {
        return shellExecutor.executeAndWait(command, output, timeout);
	}


	/**
	 * Execute the given shell command. The new created process is returned.
	 * This call is not blocking. Consider using <code>executeAndWait()</code>
	 * if you want to wait upon process completion.
	 *
	 * @param command
	 * @return
	 */
	public static Process execute(final String[] command){
        return shellExecutor.execute(command);
	}

}
