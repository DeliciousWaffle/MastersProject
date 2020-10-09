package datastructures.querytree.operator.types;

import datastructures.querytree.operator.Operator;

import java.util.ArrayList;
import java.util.List;

public class PipelinedExpression extends Operator {

    public static final String SYMBOL = "\uD835\uDCAB"; // fancy "P"
    private final String subscript;
    private final List<Operator> pipelinedOperators;

    public PipelinedExpression(List<Operator> pipelinedOperators, int subscript) {
        this.pipelinedOperators = pipelinedOperators;
        switch(subscript) {
            case 1:
                this.subscript = "₁";
                break;
            case 2:
                this.subscript = "₂";
                break;
            case 3:
                this.subscript = "₃";
                break;
            case 4:
                this.subscript = "₄";
                break;
            case 5:
                this.subscript = "₅";
                break;
            case 6:
                this.subscript = "₆";
                break;
            case 7:
                this.subscript = "₇";
                break;
            case 8:
                this.subscript = "₈";
                break;
            case 9:
                this.subscript = "₉";
                break;
            default:
                this.subscript = "₀";
                break;
        }
    }

    public PipelinedExpression(PipelinedExpression toCopy) {
        this.subscript = toCopy.subscript;
        this.pipelinedOperators = new ArrayList<>();
        toCopy.pipelinedOperators.forEach(e -> pipelinedOperators.add(e.copy(e)));
    }

    public List<Operator> getPipelinedOperators() {
        return pipelinedOperators;
    }

    public String getRelationalAlgebra() {

        StringBuilder relationalAlgebra = new StringBuilder();
        relationalAlgebra.append(SYMBOL).append(subscript).append(" = ");

        // if there is a join or cartesian product, will need to place between the correct relations,
        // otherwise it will come out as join, relation1, relation2 instead of relation1, join, relation2
        Operator joinOrCartesianProduct = null;

        for (Operator operator : pipelinedOperators) {
            if (operator.getType() == Type.INNER_JOIN || operator.getType() == Type.CARTESIAN_PRODUCT) {
                joinOrCartesianProduct = operator;
                break;
            }
            relationalAlgebra.append(operator.toString()).append(" [");
        }

        // remove " ["
        relationalAlgebra.delete(relationalAlgebra.length() - 2, relationalAlgebra.length());

        boolean hasJoinOrCartesianProduct = joinOrCartesianProduct != null;

        if (hasJoinOrCartesianProduct) {
            Operator firstRelation = null, secondRelation = null;
            for (Operator operator : pipelinedOperators) {
                if (operator.getType() == Type.RELATION || operator.getType() == Type.PIPELINED_EXPRESSION) {
                    if (firstRelation == null) {
                        firstRelation = operator;
                    } else {
                        secondRelation = operator;
                        break;
                    }
                }
            }
            assert firstRelation != null && secondRelation != null; // should never be thrown
            relationalAlgebra.append(" [").append(firstRelation.toString()).append(" ")
                    .append(joinOrCartesianProduct.toString())
                    .append(" ").append(secondRelation.toString()).append("]");
        } else {
            // adding "]"
            for (int i = 0; i < pipelinedOperators.size() - 1; i++) {
                relationalAlgebra.append("]");
            }
        }

        return relationalAlgebra.toString();
    }

    @Override
    public Type getType() {
        return Type.PIPELINED_EXPRESSION;
    }

    @Override
    public List<String> getReferencedColumnNames() {
        return new ArrayList<>();
    }

    @Override
    public Operator copy(Operator operator) {
        return new PipelinedExpression((PipelinedExpression) operator);
    }

    @Override
    public String toString() {
        return SYMBOL + subscript;
    }
}