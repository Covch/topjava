package ru.javawebinar.topjava;

import org.junit.rules.Stopwatch;
import org.junit.runner.Description;
import org.slf4j.Logger;

import java.util.concurrent.TimeUnit;

import static org.slf4j.LoggerFactory.getLogger;

public class TestUtils {

    public static class JUnitStopWatch extends Stopwatch {
        private static final Logger log = getLogger(JUnitStopWatch.class);
        private static StringBuilder allTestInfo = new StringBuilder();
        private static long wholeTimeMillis = 0;

        private static void logInfo(Description description, String status, long nanos) {
            long millis = TimeUnit.NANOSECONDS.toMillis(nanos);
            String currentTestInfo = String.format("Test %s %s\n\tspent\t%d\tms",
                    description.getMethodName(), status, millis);
            log.debug(currentTestInfo);
            wholeTimeMillis += millis;
            allTestInfo.append(currentTestInfo).append("\n");
        }

        public static String getAllTestInfo() {
            allTestInfo.append("Whole time: ").append(wholeTimeMillis).append(" milliseconds.");
            return allTestInfo.toString();
        }

        @Override
        protected void finished(long nanos, Description description) {
            logInfo(description, "finished", nanos);
        }
    }
}
