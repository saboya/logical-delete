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

    private final static String GETTER_METHOD_NAME = "getDeletedState";
    private final static String SETTER_METHOD_NAME = "setLogicalDeleteState";
    private final static String STATIC_PROPERTY_NAME = "deletedStateProperty";
    private final static String STATIC_DELETED_VALUE_NAME = "deletedStateValue";
    private final static String SETTER_PARAM_NAME = "newValue";
    public final static int CLASS_NODE_ORDER = 1;

    @Override
    public void visit(ASTNode[] nodes, SourceUnit source) {
        if (!validate(nodes)) return;
        ClassNode classNode = (ClassNode) nodes[CLASS_NODE_ORDER];
        addDeletedProperty(classNode);
        implementDeletedDomainClassInterface(classNode);
        addInterfaceMethods(classNode);
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
        node.addProperty(
                STATIC_PROPERTY_NAME,
                Modifier.PUBLIC | Modifier.STATIC | Modifier.FINAL,
                ClassHelper.STRING_TYPE,
                new ConstantExpression(propertyName),
                null,
                null
        );
        node.addProperty(
                STATIC_DELETED_VALUE_NAME,
                Modifier.PUBLIC | Modifier.STATIC | Modifier.FINAL,
                ClassHelper.boolean_TYPE,
                new ConstantExpression(deletedState,true),
                null,
                null
        );
    }

    private void addInterfaceMethods(ClassNode node) {
        node.addMethod(makeGetterMethod(getPropertyName(node)));
        node.addMethod(makeSetterMethod(getPropertyName(node)));
    }

    private MethodNode makeGetterMethod(String propertyName) {
        MethodNode node = new MethodNode(
                GETTER_METHOD_NAME,
                Modifier.PUBLIC,
                ClassHelper.boolean_TYPE,
                Parameter.EMPTY_ARRAY,
                null,
                (BlockStatement)new AstBuilder().buildFromString(
                        CompilePhase.CANONICALIZATION, "return " + propertyName
                ).get(0)
        );
        return node;
    }

    private MethodNode makeSetterMethod(String propertyName) {
        Parameter[] params = new Parameter[1];
        params[0] = new Parameter(ClassHelper.boolean_TYPE,SETTER_PARAM_NAME);
        MethodNode node = new MethodNode(
                SETTER_METHOD_NAME,
                Modifier.PUBLIC,
                ClassHelper.VOID_TYPE,
                params,
                null,
                (BlockStatement)new AstBuilder().buildFromString(
                        CompilePhase.CANONICALIZATION, propertyName + " = " + SETTER_PARAM_NAME
                ).get(0)
        );
        return node;
    }

    private String getPropertyName(ClassNode node) {
        AnnotationNode annotation = GrailsASTUtils.findAnnotation(node, LogicalDelete.class);
        return getMemberStringValue(annotation,"property",getDefaultAnnotationArgumentValue(annotation));
    }

    private boolean getDeletedState(ClassNode node) {
        AnnotationNode annotation = GrailsASTUtils.findAnnotation(node, LogicalDelete.class);
        if(getMemberValue(annotation,"deletedValue") == null) {
            return true;
        }
        return (Boolean)getMemberValue(annotation, "deletedValue");
    }

    private String getDefaultAnnotationArgumentValue(AnnotationNode annotation) {
        ReturnStatement stmt = (ReturnStatement)annotation.getClassNode().getMethod("property", Parameter.EMPTY_ARRAY).getCode();
        return (String)((ConstantExpression)stmt.getExpression()).getValue();
    }

    private void implementDeletedDomainClassInterface(ClassNode node) {
        ClassNode iNode = new ClassNode(LogicalDeleteDomainClass.class);
        if (!iNode.implementsInterface(iNode)) {
            node.addInterface(iNode);
        }
    }

}
