package com.b2wdigital.grails.plugin.gormlogicaldelete;

import java.lang.reflect.Modifier;

import org.codehaus.groovy.ast.*;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.stmt.ReturnStatement;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.grails.compiler.injection.GrailsASTUtils;
import org.codehaus.groovy.transform.AbstractASTTransformation;
import org.codehaus.groovy.transform.GroovyASTTransformation;

@GroovyASTTransformation(phase = CompilePhase.CANONICALIZATION)
public class GormLogicalDeleteASTTRansformation extends AbstractASTTransformation {

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
        AnnotationNode annotation = GrailsASTUtils.findAnnotation(node, GormLogicalDelete.class);
        return userlandGetMemberStringValue(annotation, "property", getDefaultAnnotationArgumentValue(annotation));
    }

    private boolean getDeletedState(ClassNode node) {
        AnnotationNode annotation = GrailsASTUtils.findAnnotation(node, GormLogicalDelete.class);
        if(getMemberValue(annotation,"deletedState") == null) {
            return true;
        }
        return (Boolean)getMemberValue(annotation, "deletedState");
    }

    /**
     * Backported to support Grails < 2.4
     *
     * @see <a href="https://github.com/groovy/groovy-core/commit/5f9797c00dbfcead70fdef85d35bb060f5e8caa1">5f9797c</a>
     */
    public static String userlandGetMemberStringValue(AnnotationNode node, String name, String defaultValue) {
        final String STRING = "<DummyUndefinedMarkerString-DoNotUse>"; // from groovy.transform.Undefined
        final Expression member = node.getMember(name);
        if (member != null && member instanceof ConstantExpression) {
            Object result = ((ConstantExpression) member).getValue();
            if (result != null && result instanceof String && STRING.equals((String) result)) result = null;
            if (result != null) return result.toString();
        }
        return defaultValue;
    }

    private String getDefaultAnnotationArgumentValue(AnnotationNode annotation) {
        ReturnStatement stmt = (ReturnStatement)annotation.getClassNode().getMethod("property", Parameter.EMPTY_ARRAY).getCode();
        return (String)((ConstantExpression)stmt.getExpression()).getValue();
    }
}
