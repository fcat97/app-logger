/**
 * AppLogger
 * Copyright (C) 2022 github/fCat97
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package media.uqab.libapplogger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class AppLogger {
    private static final String LOG_DIR_NAME = "log";
    private static String logDirPath;

    public static final String USAGE_APP = "app used: ";
    public static final String USAGE_CLASS = " used: ";

    private static AppLogger appLogger;

    public static AppLogger getInstance() {
        if (appLogger == null) appLogger = new AppLogger();

        return appLogger;
    }

    private synchronized void writeToFile(File file, String msg) {
        ExecutorUtils.submitTask(() -> {
            FileWriter fileWriter = null;
            BufferedWriter bufferedWriter = null;
            PrintWriter printWriter = null;

            try {
                fileWriter = new FileWriter(file, true);
                bufferedWriter = new BufferedWriter(fileWriter);
                printWriter = new PrintWriter(bufferedWriter);

                printWriter.println(getNewLine());
                printWriter.println(msg);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (printWriter != null) {
                        printWriter.close();
                    }
                } catch (Exception e) { e.printStackTrace(); }
                try {
                    if (bufferedWriter != null) {
                        bufferedWriter.close();
                    }
                } catch (Exception e) { e.printStackTrace(); }
                try {
                    if (fileWriter != null) {
                        fileWriter.close();
                    }
                } catch (Exception e) { e.printStackTrace(); }
            }
        });
    }

    private String getNewLine() {
        return DateTimeUtil.INSTANCE.getCurrentReadableTimeWithMillis() +" --->";
    }

    public void logInfo(String msg) {
        String fileName = DateTimeUtil.INSTANCE.formatDateTime(DateTimeUtil.INSTANCE.getThisMonthFirstMidnight(), "yyyy-MM-dd") + ".inf";
        writeToFile(new File(logDirPath, fileName), msg);
    }

    public void logFullTrace(String trace) {
        String fileName = DateTimeUtil.INSTANCE.formatDateTime(DateTimeUtil.INSTANCE.getThisMonthFirstMidnight(), "yyyy-MM-dd") + ".err";

        writeToFile(new File(logDirPath, fileName), trace);
    }

    public void logError(String msg) {
        try {
            String fileName = DateTimeUtil.INSTANCE.formatDateTime(DateTimeUtil.INSTANCE.getThisMonthFirstMidnight(), "yyyy-MM-dd") + ".err";

            // trace at which this method was get called
            // https://www.techiedelight.com/get-name-current-function-kotlin/
            StackTraceElement trace = Thread.currentThread().getStackTrace()[3];
            String threadName = Thread.currentThread().getName();
            String className = trace.getClassName();
            String lineNumber = String.valueOf(trace.getLineNumber());
            String methodName = trace.getMethodName();

            String traceInfo = threadName + "@" + className + "::" + methodName + "(" + lineNumber + "): ";

            writeToFile(new File(logDirPath, fileName), traceInfo +  msg);
        } catch (Exception ignored) {}
    }

    public void logDebug(String msg) {
        try {
            String fileName = DateTimeUtil.INSTANCE.formatDateTime(DateTimeUtil.INSTANCE.getThisMonthFirstMidnight(), "yyyy-MM-dd") + ".deb";

            // trace at which this method was get called
            // https://www.techiedelight.com/get-name-current-function-kotlin/
            StackTraceElement trace = Thread.currentThread().getStackTrace()[3];
            String threadName = Thread.currentThread().getName();
            String className = trace.getClassName();
            String lineNumber = String.valueOf(trace.getLineNumber());
            String methodName = trace.getMethodName();

            String traceInfo = threadName + "@" + className + "::" + methodName + "(" + lineNumber + "): ";

            writeToFile(new File(logDirPath, fileName), traceInfo + msg);
        } catch (Exception ignored) {}
    }

    public void logTest(String fileName, String msg) {
        String fName = fileName  + ".test";
        writeToFile(new File(logDirPath, fName), msg);
    }

    public static String getCallerLocation() {
        StackTraceElement trace = Thread.currentThread().getStackTrace()[4];
        String threadName = Thread.currentThread().getName();
        String className = trace.getClassName();
        String lineNumber = String.valueOf(trace.getLineNumber());
        String methodName = trace.getMethodName();

        return "[th]" + threadName + " [cls]" + className + "@" + methodName + "(" + lineNumber + ")";
    }

    public AppLogger logAppUsage(long startingTime) {
        long time = System.currentTimeMillis() - startingTime;
        logInfo(USAGE_APP + DateTimeUtil.INSTANCE.getReadableTime(time) +
                "\n-------------------------------------------------");
        return this;
    }
    public <T> AppLogger logUsageOf(Class<T> tClass, long startingTime) {
        long time = System.currentTimeMillis() - startingTime;
        logInfo(tClass.getName() + USAGE_CLASS + DateTimeUtil.INSTANCE.getReadableTime(time));
        return this;
    }
}
