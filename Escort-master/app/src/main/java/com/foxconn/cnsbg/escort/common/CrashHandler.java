package com.foxconn.cnsbg.escort.common;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.Thread.UncaughtExceptionHandler;

public class CrashHandler implements UncaughtExceptionHandler{
    private static CrashHandler crashHandler;
    private String logPath;

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (crashHandler != null) {
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(logPath, true);
                PrintStream printStream = new PrintStream(fileOutputStream);
                ex.printStackTrace(printStream);
                printStream.flush();
                printStream.close();
                fileOutputStream.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.exit(1);
        }
    }

    public void init(String path) {
        logPath = path;
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    private CrashHandler() {
    }

    public static CrashHandler getInstance() {
        if (crashHandler == null) {
            synchronized (CrashHandler.class) {
                crashHandler = new CrashHandler();
            }
        }
        return crashHandler;
    }
}