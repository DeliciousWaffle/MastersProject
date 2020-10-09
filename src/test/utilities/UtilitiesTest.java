package test.utilities;

import org.junit.jupiter.api.Test;
import utilities.OptimizerUtilities;

import java.util.*;

/**
 * Test class used for making sure that the Utilities class is performing as it should be.
 */
public class UtilitiesTest {
    @Test
    public void blah() {
        List<String> blah = Arrays.asList(
                "tab1.col1", "tab1.col2", "tab2.col2", "tab3.col3", "tab4.col4"
        );

        System.out.println(OptimizerUtilities.removePrefixedColumnNames(blah));
    }

}