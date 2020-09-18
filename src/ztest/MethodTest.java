package ztest;

import org.junit.jupiter.api.Test;
import systemcatalog.components.OptimizerUtilities;

import java.util.Arrays;

/**
 * Class used for testing methods.
 */
public class MethodTest {
    @Test
    public void blah() {
        System.out.println(OptimizerUtilities.minus(Arrays.asList("Col1", "Col2", "Col3", "Col4"), Arrays.asList("Col2", "Col7")));
    }

}
