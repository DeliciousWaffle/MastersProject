package ztest;

import org.junit.jupiter.api.Test;
import systemcatalog.components.OptimizerUtilities;

import java.util.*;

/**
 * Class used for testing methods.
 */
public class MethodTest {
    @Test
    public void blah() {
        List<String> blah = Arrays.asList(
                "tab1.col1", "tab1.col2", "tab2.col2", "tab3.col3", "tab4.col4"
        );

        System.out.println(OptimizerUtilities.removePrefixedColumnNames(blah));
    }

}
