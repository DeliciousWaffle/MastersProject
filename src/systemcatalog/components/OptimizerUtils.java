package systemcatalog.components;

import datastructures.relation.table.component.Column;
import datastructures.trees.querytree.operator.Operator;

import java.util.List;

/**
 * Class that offers additional functionality to the Optimizer class because that class
 * got very hairy and this keeps things somewhat clean.
 */
public final class OptimizerUtils {

    public static Operator createAggregateSelection(List<String> havingClauseAggregationTypes, List<Column> havingClauseColumns, List<String> havingClauseSymbols, List<String> havingClauseValues) {
        return null;
    }

    public static Operator createAggregation() {
        return null;
    }

    public static Operator createCartesianProduct() {
        return null;
    }

    public static Operator createCompoundSelection() {
        return null;
    }

    public static Operator createInnerJoin() {
        return null;
    }

    public static Operator createPipelinedExpression() {
        return null;
    }

    public static Operator createProjection() {
        return null;
    }

    public static Operator createRelation() {
        return null;
    }

    public static Operator createSimpleSelection() {
        return null;
    }
}