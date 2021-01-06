/*
 * Copyright 2020 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package software.amazon.smithy.go.codegen;

import java.util.ArrayList;
import software.amazon.smithy.codegen.core.CodegenException;
import software.amazon.smithy.jmespath.ExpressionVisitor;
import software.amazon.smithy.jmespath.JmespathExpression;
import software.amazon.smithy.jmespath.ast.AndExpression;
import software.amazon.smithy.jmespath.ast.ComparatorExpression;
import software.amazon.smithy.jmespath.ast.CurrentExpression;
import software.amazon.smithy.jmespath.ast.ExpressionTypeExpression;
import software.amazon.smithy.jmespath.ast.FieldExpression;
import software.amazon.smithy.jmespath.ast.FilterProjectionExpression;
import software.amazon.smithy.jmespath.ast.FlattenExpression;
import software.amazon.smithy.jmespath.ast.FunctionExpression;
import software.amazon.smithy.jmespath.ast.IndexExpression;
import software.amazon.smithy.jmespath.ast.LiteralExpression;
import software.amazon.smithy.jmespath.ast.MultiSelectHashExpression;
import software.amazon.smithy.jmespath.ast.MultiSelectListExpression;
import software.amazon.smithy.jmespath.ast.NotExpression;
import software.amazon.smithy.jmespath.ast.ObjectProjectionExpression;
import software.amazon.smithy.jmespath.ast.OrExpression;
import software.amazon.smithy.jmespath.ast.ProjectionExpression;
import software.amazon.smithy.jmespath.ast.SliceExpression;
import software.amazon.smithy.jmespath.ast.Subexpression;
import software.amazon.smithy.model.shapes.StructureShape;

/**
 * Visitor for Jmespath expressions.
 */
public class JmespathVisitor implements ExpressionVisitor<Void> {

    // Execution context is the current "head" of the execution. This is scope on which the expression
    // is currently operating across. It is imperative that this is kept up to date on expression.accept and return.
    private String executionContext;

    private int scopeCount;

    private JmespathExpression jmesExpression;

    private final GoWriter writer;
    private StructureShape inputShape;
    private final StructureShape outputShape;
    private final String accessor;

    public JmespathVisitor(
            GoWriter writer,
            StructureShape outputShape,
            String accessor,
            JmespathExpression expression
    ) {
        this.writer = writer;
        this.outputShape = outputShape;
        this.accessor = accessor;
        this.jmesExpression = expression;
    }

    public JmespathVisitor(
            GoWriter writer,
            StructureShape inputShape,
            StructureShape outputShape,
            String accessor,
            JmespathExpression expression
    ) {
        this.writer = writer;
        this.inputShape = inputShape;
        this.outputShape = outputShape;
        this.accessor = accessor;
        this.jmesExpression = expression;
    }

    public void run() {
        executionContext = "";
        jmesExpression.accept(this);
        writer.write("$L := $L", accessor, executionContext);
    }

    private String makeNewScope(String prefix) {
        scopeCount += 1;
        return prefix + scopeCount;
    }
//
//    @Override
//    public Void visitComparator(ComparatorExpression expression) {
//        JmespathExpression leftexpr = expression.getLeft();
//        JmespathExpression rightexpr = expression.getRight();
//
//        String leftAccessor = "leftAccessor";
//        String rightAccessor = "rightAccessor";
//
//
//        leftexpr.accept(new JmespathVisitor(writer, inputShape, outputShape, leftAccessor));
//        rightexpr.accept(new JmespathVisitor(writer, inputShape, outputShape, rightAccessor));
//
//        writer.write("$L $L $L", leftAccessor, expression.getComparator().toString(), rightAccessor);
//        return null;
//    }
//
//    @Override
//    public Void visitCurrentNode(CurrentExpression expression) {
//        return null;
//    }
//
//    @Override
//    public Void visitExpressionType(ExpressionTypeExpression expression) {
//        return null;
//    }
//
//    @Override
//    public Void visitFlatten(FlattenExpression expression) {
//        return null;
//    }
//
//    @Override
//    public Void visitFunction(FunctionExpression expression) {
//        expression.arguments.forEach(expr -> {
//            expr.accept(this);
//            switch (expression.getName()) {
//                case "length":
//                    writer.write("len($L)", this.accessor);
//                    break;
//                default:
//                    throw new CodegenException(
//                            String.format("support for function %s not implemented", expression.getName()));
//            }
//        });
//
//        return null;
//    }
//
//    @Override
//    public Void visitField(FieldExpression expression) {
//
//
//
//        writer.write(".$L", expression.);
//
//        // TODO: visit shapes correctly
////        switch (expression.getName()) {
////            case "output":
////                expression.
////            case "input":
////            default:
////
////        }
//
//        return null;
//    }
//
//    @Override
//    public Void visitIndex(IndexExpression expression) {
//        return null;
//    }
//
//    @Override
//    public Void visitLiteral(LiteralExpression expression) {
//        return null;
//    }
//
//    @Override
//    public Void visitMultiSelectList(MultiSelectListExpression expression) {
//        return null;
//    }
//
//    @Override
//    public Void visitMultiSelectHash(MultiSelectHashExpression expression) {
//        return null;
//    }
//
//    @Override
//    public Void visitAnd(AndExpression expression) {
//        return null;
//    }
//
//    @Override
//    public Void visitOr(OrExpression expression) {
//        return null;
//    }
//
//    @Override
//    public Void visitNot(NotExpression expression) {
//        return null;
//    }
//
//    @Override
//    public Void visitProjection(ProjectionExpression expression) {
//        return null;
//    }
//
//    @Override
//    public Void visitFilterProjection(FilterProjectionExpression expression) {
//        return null;
//    }
//
//    @Override
//    public Void visitObjectProjection(ObjectProjectionExpression expression) {
//        return null;
//    }
//
//    @Override
//    public Void visitSlice(SliceExpression expression) {
//        return null;
//    }
//
//    @Override
//    public Void visitSubexpression(Subexpression expression) {
//        expression.getLeft().accept(this);
//        expression.getRight().accept(this);
//
//        return null;
//    }


    @Override
    public Void visitComparator(ComparatorExpression expression) {

        String executionContextInitial = executionContext;
        String comparator = expression.getComparator().toString();

        expression.getLeft().accept(this);
        String leftContext = executionContext;

        executionContext = executionContextInitial;

        expression.getRight().accept(this);
        String rightContext = executionContext;

        executionContext = String.format("(%s %s %s)", leftContext, comparator, rightContext);
        return null;
    }

    @Override
    public Void visitCurrentNode(CurrentExpression expression) {
        // Fall through as visitCurrentNode is saying that there is a noop here. Execution context does not change.
        return null;
    }

    @Override
    public Void visitExpressionType(ExpressionTypeExpression expression) {
        throw new CodegenException("Jmespath  visitor not implemented ExpressionTypeExpression");
    }

    @Override
    public Void visitFlatten(FlattenExpression expression) {
        expression.getExpression().accept(this);
        String flatScope = makeNewScope("flat_");
        writer.write("$L: any[] = [].concat(...$L);", flatScope, executionContext);
        executionContext = flatScope;
        return null;
    }

    @Override
    public Void visitFunction(FunctionExpression expression) {
        ArrayList<String> executionContexts = new ArrayList<>();

        String orginalExecutionContext = this.executionContext;
        expression.arguments.forEach((JmespathExpression argExpression) -> {
            argExpression.accept(this);
            switch (expression.getName()) {
                case "length":
                    executionContext = String.format("len(%s)", executionContext);
                    break;
                case "contains":
                    executionContexts.add(executionContext);
                    this.executionContext = orginalExecutionContext;
                    break;
                default:
                    throw new CodegenException("TypeScriptJmesPath visitor has not implemented function: "
                            + expression.getName());
            }
        });

        if (expression.getName().equals("contains")) {
            executionContext = String.format("Strings.Contains(%s,%s)", executionContexts, executionContext);
        }
        return null;
    }

    @Override
    public Void visitField(FieldExpression expression) {
        switch (expression.getName()) {
            case "input":
                executionContext += inputShape.getId().getName();
                break;
            case "output":
                executionContext += outputShape.getId().getName();
                break;
            default:
                executionContext += ".";
                executionContext += expression.getName();
                break;
        }
        return null;
    }

    @Override
    public Void visitIndex(IndexExpression expression) {
        if (expression.getIndex() >= 0) {
            executionContext += ("[" + expression.getIndex() + "]");
        } else {
            executionContext += "[" + executionContext + ".length";
            executionContext += " - " + Math.abs(expression.getIndex()) + "]";
        }
        return null;
    }

    @Override
    public Void visitLiteral(LiteralExpression expression) {
        switch (expression.getType()) {
            case STRING:
                executionContext = "\"" + expression.getValue().toString() + "\"";
                break;
            case OBJECT:
            case ARRAY:
                // TODO: resolve JMESPATH OBJECTS and ARRAY types as literals
                throw new CodegenException("TypeScriptJmesPath visitor has not implemented resolution of ARRAY and"
                        + " OBJECT literials ");
            default:
                // All other options are already valid js literials.
                // (BOOLEAN, ANY, NULL, NUMBER, EXPRESSION)
                executionContext = expression.getValue().toString();
                break;
        }
        return null;
    }

    @Override
    public Void visitMultiSelectList(MultiSelectListExpression expression) {
        ArrayList<String> evaluators = new ArrayList<String>();

        String executionContextInital = executionContext;

        expression.getExpressions().forEach((JmespathExpression exp) -> {
            exp.accept(this);
            evaluators.add(executionContext);
            executionContext = executionContextInital;
        });

        String resultScope = makeNewScope("result_");
        writer.write("let $L = [];", resultScope);
        for (String evaluator : evaluators) {
            writer.write("$L.push($L);", resultScope, evaluator);
        }
        writer.write("$L = $L;", executionContext, resultScope);

        return null;
    }

    @Override
    public Void visitMultiSelectHash(MultiSelectHashExpression expression) {
        throw new CodegenException("TypeScriptJmesPath visitor not implemented MultiSelectHashExpression");
    }

    @Override
    public Void visitAnd(AndExpression expression) {
        String initialContext = executionContext;

        expression.getLeft().accept(this);
        String leftContext = executionContext;
        executionContext = initialContext;

        expression.getRight().accept(this);
        String rightContext = executionContext;

        executionContext = String.format("(%s && %s)", leftContext, rightContext);
        return null;
    }

    @Override
    public Void visitOr(OrExpression expression) {
        String initialContext = executionContext;

        expression.getLeft().accept(this);
        String leftContext = executionContext;
        executionContext = initialContext;

        expression.getRight().accept(this);
        String rightContext = executionContext;

        executionContext = String.format("((%s || %s) && (%s || %s)) ", leftContext, rightContext, rightContext,
                leftContext);

        return null;
    }

    @Override
    public Void visitNot(NotExpression expression) {
        expression.getExpression().accept(this);
        executionContext = String.format("(!%s)", executionContext);
        return null;
    }

    @Override
    public Void visitObjectProjection(ObjectProjectionExpression expression) {
        expression.getLeft().accept(this);

        String element = makeNewScope("element_");
        String result = makeNewScope("objectProjection_");
        writer.openBlock("let $L = Object.values($L).map(($L: any) => {", "});", result,
                executionContext, element, () -> {
                    executionContext = element;
                    expression.getRight().accept(this);
                    writer.write("return $L;", executionContext);
                });
        executionContext = result;
        return null;
    }

    @Override
    public Void visitProjection(ProjectionExpression expression) {
        expression.getLeft().accept(this);

        if (!(expression.getRight() instanceof CurrentExpression)) {
            String element = makeNewScope("element_");
            String result = makeNewScope("projection_");
            writer.openBlock("let $L = $L.map(($L: any) => {", "});", result,
                    executionContext, element, () -> {
                        executionContext = element;
                        expression.getRight().accept(this);
                        writer.write("return $L;", executionContext);
                    });
            executionContext = result;
        }
        return null;
    }

    @Override
    public Void visitFilterProjection(FilterProjectionExpression expression) {

        expression.getLeft().accept(this);

        expression.getRight().accept(this);

        String elementScope = makeNewScope("element_");
        String resultScope = makeNewScope("filterRes_");
        writer.openBlock("let $L = $L.filter(($L: any) => {", "});", resultScope,
                executionContext, elementScope, () -> {
                    executionContext = elementScope;
                    expression.getComparison().accept(this);
                    writer.write("return $L;", executionContext);
                });

        executionContext = resultScope;
        return null;
    }

    @Override
    public Void visitSlice(SliceExpression expression) {
        throw new CodegenException("TypeScriptJmesPath visitor not implemented SliceExpression");
    }

    @Override
    public Void visitSubexpression(Subexpression expression) {
        expression.getLeft().accept(this);
        expression.getRight().accept(this);
        return null;
    }
}
