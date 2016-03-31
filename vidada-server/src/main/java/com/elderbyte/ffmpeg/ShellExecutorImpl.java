package com.elderbyte.ffmpeg;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Java 6.0 - 7.0 implementation.
 *
 */
public class ShellExecutorImpl implements IShellExecutor {

    private static final Logger logger = LoggerFactory.getLogger(ShellExecutorImpl.class);

    protected static final ExecutorService PROCESS_THREAD_POOL = Executors.newCachedThreadPool();

	/**
     * Execute the given command and wait until the created process has completed
     * or the timeout is reached.
	 *
     * @param command The command to execute
     * @param output Collect the output of the process in this string builder
     * @param timeout The timeout (Milliseconds) when the process will be canceled.
	 * @return
     *
     * @throws java.util.concurrent.TimeoutException Thrown when the timeout is reached before the process has finished.
	 */
	public int executeAndWait(
			final String[] command,
			final StringBuilder output,
            final long timeout) throws TimeoutException {

		Process process = null;

		try {
            process = execute(command);

			if(output != null){
				// Collect all output from the process
                PROCESS_THREAD_POOL.submit(
                        new AsyncInputStreamReader(process.getInputStream(), output)
                );
			}
			boolean hasFinished = waitForProcess(process, timeout, TimeUnit.MILLISECONDS);
            if(hasFinished) {
                // The process has finished within the given timeout time
                return process.exitValue();
            }else{
                throw new TimeoutException("The process '" + ShellParser.toCommandLine(command) + "' took to long to complete.");
            }

		} catch (InterruptedException e) {
            logger.warn("Execution has been interrupted!", e);
		}finally{
			if(process != null){
                process.destroy();
			}
		}
		return -1;
	}

    /**
     * Wait for a process to finish.
     * @param process
     * @param timeout
     * @param unit
     * @return
     * @throws InterruptedException
     */
    private boolean waitForProcess(Process process, long timeout, TimeUnit unit)
            throws InterruptedException
    {
        long startTime = System.nanoTime();
        long rem = unit.toNanos(timeout);

        do {
            try {
                process.exitValue();
                return true;
            } catch(IllegalThreadStateException ex) {
                if (rem > 0)
                    Thread.sleep(
                            Math.min(TimeUnit.NANOSECONDS.toMillis(rem) + 1, 100));
            }
            rem = unit.toNanos(timeout) - (System.nanoTime() - startTime);
        } while (rem > 0);
        return false;
    }

	/**
	 * Execute the given shell command. The new created process is returned.
	 * This call is not blocking. Consider using <code>executeAndWait()</code>
	 * if you want to wait upon process completion.
	 *
	 * @param command
	 * @return
	 */
	public Process execute(final String[] command){

		ProcessBuilder builder = new ProcessBuilder(command);
		builder.redirectErrorStream(true);
		Process process = null;

		try {
            logger.debug("executing: " + ShellParser.toCommandLine(command));
			process = builder.start();
		} catch (IOException e) {
            logger.error("Failed to execute", e);
		}

		return process;
	}


    /**
     *
     */
    private static class AsyncInputStreamReader implements  Runnable {
        private final InputStream inputStream;
        private final StringBuilder outputBuilder;

        public AsyncInputStreamReader(InputStream inputStream, StringBuilder outputBuilder){
            this.inputStream = inputStream;
            this.outputBuilder = outputBuilder;
        }

        @Override
        public void run() {
            BufferedReader outReader = new BufferedReader(new InputStreamReader(inputStream));
            String outString;
            try {
                while((outString = outReader.readLine()) != null)
                {
                    logger.trace(">" + outString);
                    if(outputBuilder != null){
                        outputBuilder.append(outString);
                        outputBuilder.append(System.getProperty("line.separator"));
                    }
                }
            } catch (IOException e) {
                logger.error("Failed to read from command-line!", e);
            }
        }
    }

}
