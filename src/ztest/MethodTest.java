package ztest;

import org.junit.jupiter.api.Test;
import systemcatalog.components.OptimizerUtilities;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Class used for testing methods.
 */
public class MethodTest {
    @Test
    public void blah() {
        Map<Integer, String> blah = new LinkedHashMap<>();
        blah.put(0, "a");
        blah.put(1, "b");
        blah.put(2, "c");
        blah.put(3, "d");

        OptimizerUtilities.putFirstElementLastOfLinkedHashMap(blah);
        System.out.println(blah);
    }

}
