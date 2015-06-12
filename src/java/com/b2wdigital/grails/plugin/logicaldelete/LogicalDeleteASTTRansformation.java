package com.b2wdigital.grails.plugin.logicaldelete;

import java.lang.reflect.Modifier;

import org.codehaus.groovy.ast.*;
import org.codehaus.groovy.ast.builder.AstBuilder;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.ReturnStatement;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.grails.compiler.injection.GrailsASTUtils;
import org.codehaus.groovy.transform.AbstractASTTransformation;
import org.codehaus.groovy.transform.GroovyASTTransformation;

@GroovyASTTransformation(phase = CompilePhase.CANONICALIZATION)
public class LogicalDeleteASTTRansformation extends AbstractASTTransformation {

    public final static int CLASS_NODE_ORDER = 1;

    @Override
    public void visit(ASTNode[] nodes, SourceUnit source) {
        if (!validate(nodes)) return;
        ClassNode classNode = (ClassNode) nodes[CLASS_NODE_ORDER];
        addDeletedProperty(classNode);
    }

    private boolean validate(ASTNode[] nodes) {
        return nodes != null && nodes[0] != null && nodes[1] != null;
    }

    private void addDeletedProperty(ClassNode node) {
        String propertyName = getPropertyName(node);
        boolean deletedState = getDeletedState(node);
        if (!GrailsASTUtils.hasOrInheritsProperty(node, propertyName)) {
            node.addProperty(
                    propertyName,
                    Modifier.PUBLIC,
                    ClassHelper.boolean_TYPE,
                    new ConstantExpression(!deletedState,true),
                    null,
                    null
            );
        }
    }

    private String getPropertyName(ClassNode node) {
        AnnotationNode annotation = GrailsASTUtils.findAnnotation(node, LogicalDelete.class);
        return getMemberStringValue(annotation,"property",getDefaultAnnotationArgumentValue(annotation));
    }

    private boolean getDeletedState(ClassNode node) {
        AnnotationNode annotation = GrailsASTUtils.findAnnotation(node, LogicalDelete.class);
        if(getMemberValue(annotation,"deletedState") == null) {
            return true;
        }
        return (Boolean)getMemberValue(annotation, "deletedState");
    }

    private String getDefaultAnnotationArgumentValue(AnnotationNode annotation) {
        ReturnStatement stmt = (ReturnStatement)annotation.getClassNode().getMethod("property", Parameter.EMPTY_ARRAY).getCode();
        return (String)((ConstantExpression)stmt.getExpression()).getValue();
    }
}
