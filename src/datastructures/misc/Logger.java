package datastructures.misc;

import java.util.Stack;

public class Logger {

    private Stack<Log> logger;
    private boolean successfullyExecuted;

    public Logger() {
        this.logger = new Stack<>();
        this.successfullyExecuted = false;
    }

    public void log(Log log) {
        logger.push(log);
    }

    public String emptySimpleContent() {

        StringBuilder simpleContent = new StringBuilder();

        while(! logger.isEmpty()) {
            Log log = logger.pop();
            if(log.getType() == Log.Type.SIMPLE) {
                simpleContent.append(log);
                simpleContent.append("\n");
            }

        }

        // remove "\n" and other stuff
        boolean noContent = simpleContent.length() == 0;

        if(! noContent) {
            simpleContent.deleteCharAt(simpleContent.length() - 1);
        } else {
            simpleContent.append("Logger has no logs!");
        }

        return simpleContent.toString();
    }

    public String emptyAllContent() {

        StringBuilder allContent = new StringBuilder();

        while(! logger.isEmpty()) {
            allContent.append(logger.pop());
            allContent.append("\n");
        }

        // remove "\n" and other stuff
        boolean noContent = allContent.length() == 0;

        if(! noContent) {
            allContent.deleteCharAt(allContent.length() - 1);
        } else {
            allContent.append("Logger has no logs!");
        }

        return allContent.toString();
    }

    public void setSuccessfullyExecuted(boolean successfullyExecuted) {
        this.successfullyExecuted = successfullyExecuted;
    }

    public boolean wasSuccessfullyExecuted() {
        return successfullyExecuted;
    }

    public void clear() {
        logger = new Stack<>();
        successfullyExecuted = false;
    }
}