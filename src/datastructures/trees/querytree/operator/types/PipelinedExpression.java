package datastructures.trees.querytree.operator.types;

import datastructures.trees.querytree.operator.Operator;

import java.util.ArrayList;
import java.util.List;

public class PipelinedExpression extends Operator {

    public static final String SYMBOL = "\uD835\uDCAB";
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

        for (Operator operator : pipelinedOperators) {
            relationalAlgebra.append("[").append(operator.toString()).append(" ");
        }

        // remove " "
        relationalAlgebra.deleteCharAt(relationalAlgebra.length() - 1);

        // adding "]"
        pipelinedOperators.forEach(e -> relationalAlgebra.append("]"));

        return relationalAlgebra.toString();
    }

    @Override
    public Type getType() {
        return Type.PIPELINED;
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