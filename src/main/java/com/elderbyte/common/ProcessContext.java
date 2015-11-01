package com.elderbyte.common;

import java.io.*;


public final class ProcessContext {


    public static ProcessContext executeAwait(String[] command) throws IOException {
        ProcessBuilder builder = new ProcessBuilder(command);
        Process process = builder.start();
        return new ProcessContext(process);
    }


    private ProcessContext(Process process) throws IOException {
        StringBuffer output = new StringBuffer();
        StringBuffer error = new StringBuffer();

        Reader stdout;
        Reader stderr;

        stdout = new InputStreamReader(process.getInputStream());
        stderr = new InputStreamReader(process.getErrorStream());
        char[] buffer = new char[1024];

        boolean done = false;
        boolean stdoutclosed = false;
        boolean stderrclosed = false;
        while (!done){
            boolean readSomething = false;
            // read from the process's standard output
            if (!stdoutclosed && stdout.ready()){
                readSomething = true;
                int read = stdout.read(buffer, 0, buffer.length);
                if (read < 0){
                    readSomething = true;
                    stdoutclosed = true;
                } else if (read > 0){
                    readSomething = true;
                    output.append(buffer, 0, read);
                }
            }
            // read from the process's standard error
            if (!stderrclosed && stderr.ready()){
                int read = stderr.read(buffer, 0, buffer.length);
                if (read < 0){
                    readSomething = true;
                    stderrclosed = true;
                } else if (read > 0){
                    readSomething = true;
                    error.append(buffer, 0, read);
                }
            }
            // Check the exit status only we haven't read anything,
            // if something has been read, the process is obviously not dead yet.
            if (!readSomething){
                try {
                    this.status = process.exitValue();
                    done = true;
                } catch (IllegalThreadStateException itx){
                    // Exit status not ready yet.
                    // Give the process a little breathing room.
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ix){
                        process.destroy();
                        throw new IOException("Interrupted - processes killed");
                    }
                }
            }
        }

        this.output = output.toString();
        this.error = error.toString();
    }


    private String output;
    private String error;
    private int status;



    public String getOutput(){
        return output;
    }


    public String getError(){
        return error;
    }

    public int getStatus(){
        return status;
    }
}
