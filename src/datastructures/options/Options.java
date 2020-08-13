package datastructures.options;

/**
 * Holds option data.
 */
public class Options {

    private boolean usingLightMode, usingSecurityChecker, usingVerifier;

    public Options() {
        this.usingLightMode = false;
        this.usingSecurityChecker = false;
        this.usingVerifier = false;
    }

    public Options(boolean usingLightMode, boolean usingSecurityChecker, boolean usingVerifier) {
        this.usingLightMode = usingLightMode;
        this.usingSecurityChecker = usingSecurityChecker;
        this.usingVerifier = usingVerifier;
    }

    // getters
    public boolean isUsingLightMode() {
        return usingLightMode;
    }

    public boolean isUsingSecurityChecker() {
        return usingSecurityChecker;
    }

    public boolean isUsingVerifier() {
        return usingVerifier;
    }

    // setters
    public void setUsingLightMode(boolean usingLightMode) {
        this.usingLightMode = usingLightMode;
    }

    public void setUsingSecurityChecker(boolean usingSecurityChecker) {
        this.usingSecurityChecker = usingSecurityChecker;
    }

    public void setUsingVerifier(boolean usingVerifier) {
        this.usingVerifier = usingVerifier;
    }
}