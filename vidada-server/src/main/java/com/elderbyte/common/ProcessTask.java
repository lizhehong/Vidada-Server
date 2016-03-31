package com.elderbyte.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Represents an external running process
 * from which the output can be read
 * @author IsNull
 *
 */
public class ProcessTask extends Thread {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());


    private Process process;
	private final boolean printOutput;
	private final StringBuilder output;

	private Integer exitCode;


	/**
	 * Creates a new ProcessTask
	 * @param process
	 * @param output
	 * @param printOutput
	 */
	ProcessTask(Process process, StringBuilder output, boolean printOutput) {
		this.process = process;
		this.printOutput = printOutput;
		this.output = output;
	}

	@Override
	protected void finalize() throws Throwable {
		dispose();
		super.finalize();
	}


	@Override
	public synchronized void run() {
		try {
			BufferedReader outReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String outString = null;
			while((outString = outReader.readLine()) != null)
			{
                logger.debug(outString);
				if(output != null){
					output.append(outString);
					output.append(System.getProperty("line.separator"));
				}
			}
			if(process != null)
				setExitCode(process.waitFor());
		} catch (InterruptedException ignore) {
			return;
		} catch (IOException e) {
            logger.error("", e);
		}
	}

	public Integer getExitCode() {
		return exitCode;
	}

	private void setExitCode(Integer exitCode) {
		this.exitCode = exitCode;
	}

	/**
	 * Returns the output of this process
	 * @return
	 */
	public String getOutput(){
		if(output != null)
			return output.toString();
		return null;
	}

	public void dispose(){
		if(process != null){
			process.destroy();
			process = null;
		}
	}

}
