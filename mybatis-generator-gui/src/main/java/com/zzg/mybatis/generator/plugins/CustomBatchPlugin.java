package com.zzg.mybatis.generator.plugins;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.OutputUtilities;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.ListUtilities;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;

import java.util.LinkedList;
import java.util.List;

import static org.mybatis.generator.internal.util.StringUtility.stringHasValue;

/**
 * Project: mybatis-generator-gui
 *
 * @author derotyoung on 2020/08/03.
 */
public class CustomBatchPlugin extends PluginAdapter {

    @Override
    public boolean validate(List<String> list) {
        return true;
    }

    @Override
    public boolean clientGenerated(Interface interfaze, IntrospectedTable introspectedTable) {
        List<Method> methods = interfaze.getMethods();

        Method method1 = methods.get(0);

        Method batchUpdateMethod = copyProperties(method1, "batchUpdate");
        Method batchUpdateSelectiveMethod = copyProperties(method1, "batchUpdateSelective");
        Method batchInsertMethod = copyProperties(method1, "batchInsert");

        interfaze.addMethod(batchUpdateMethod);
        interfaze.addMethod(batchUpdateSelectiveMethod);
        interfaze.addMethod(batchInsertMethod);

        return true;
    }

    @Override
    public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {

        XmlElement parentElement = document.getRootElement();

        addBatchUpdateElements(parentElement, introspectedTable);
        addBatchUpdateSelectiveElements(parentElement, introspectedTable);
        addBatchInsertElements(parentElement, introspectedTable);

        return super.sqlMapDocumentGenerated(document, introspectedTable);
    }

    private void addBatchUpdateElements(XmlElement parentElement, IntrospectedTable introspectedTable) {
        addBatchUpdateElements(parentElement, introspectedTable, false);
    }

    private void addBatchUpdateSelectiveElements(XmlElement parentElement, IntrospectedTable introspectedTable) {
        addBatchUpdateElements(parentElement, introspectedTable, true);
    }

    private void addBatchUpdateElements(XmlElement parentElement, IntrospectedTable introspectedTable, boolean selective) {
        XmlElement answer = new XmlElement("update");

        if (selective) {
            answer.addAttribute(new Attribute("id", "batchUpdateSelective"));
        } else {
            answer.addAttribute(new Attribute("id", "batchUpdate"));
        }

        answer.addAttribute(new Attribute("parameterType", "java.util.List"));

        StringBuilder sb = new StringBuilder();

        sb.append("update into ");
        sb.append(introspectedTable.getFullyQualifiedTableNameAtRuntime());
        answer.addElement(new TextElement(sb.toString()));

        XmlElement firstTrimElement = new XmlElement("trim");
        firstTrimElement.addAttribute(new Attribute("prefix", "set"));
        firstTrimElement.addAttribute(new Attribute("suffixOverrides", ","));

        IntrospectedColumn primaryKeyColumn = introspectedTable.getPrimaryKeyColumns().get(0);

        for (IntrospectedColumn introspectedColumn : ListUtilities.removeGeneratedAlwaysColumns(introspectedTable
                .getNonPrimaryKeyColumns())) {

            if (introspectedColumn.isSequenceColumn()
                    || introspectedColumn.getFullyQualifiedJavaType().isPrimitive()) {
                // if it is a sequence column, it is not optional
                // This is required for MyBatis3 because MyBatis3 parses
                // and calculates the SQL before executing the selectKey

                // if it is primitive, we cannot do a null check

                continue;
            }

            sb.setLength(0);
            sb.append(introspectedColumn.getJavaProperty());
            sb.append(" = case"); //$NON-NLS-1$
            XmlElement secondTrimElement = new XmlElement("trim");
            secondTrimElement.addAttribute(new Attribute("prefix", sb.toString()));
            secondTrimElement.addAttribute(new Attribute("suffix", "end,"));

            XmlElement foreachElement = new XmlElement("foreach");
            secondTrimElement.addAttribute(new Attribute("collection", "list"));
            secondTrimElement.addAttribute(new Attribute("index", "id"));
            secondTrimElement.addAttribute(new Attribute("item", "item"));

            sb.setLength(0);
            sb.append("when id = ");
            sb.append(getItemParameterClause(primaryKeyColumn));
            sb.append(" then ");
            sb.append(getItemParameterClause(introspectedColumn));
            TextElement textElement = new TextElement(sb.toString());

            if (selective) {
                XmlElement ifElement = new XmlElement("if");
                sb.setLength(0);
                sb.append("item.");
                sb.append(introspectedColumn.getJavaProperty());
                sb.append(" != null");
                ifElement.addAttribute(new Attribute("test", sb.toString()));
                ifElement.addElement(textElement);
                foreachElement.addElement(ifElement);
            } else {
                foreachElement.addElement(textElement);
            }

            secondTrimElement.addElement(foreachElement);

            firstTrimElement.addElement(secondTrimElement);
        }
        answer.addElement(firstTrimElement);

        sb.setLength(0);
        sb.append("where ");
        sb.append(primaryKeyColumn.getJavaProperty());
        sb.append(" in");
        answer.addElement(new TextElement(sb.toString()));

        XmlElement valuesForeachElement = new XmlElement("foreach");
        valuesForeachElement.addAttribute(new Attribute("open", "("));
        valuesForeachElement.addAttribute(new Attribute("close", ")"));
        valuesForeachElement.addAttribute(new Attribute("collection", "list"));
        valuesForeachElement.addAttribute(new Attribute("item", "item"));
        valuesForeachElement.addAttribute(new Attribute("separator", ", "));
        sb.setLength(0);
        sb.append(getItemParameterClause(primaryKeyColumn));
        valuesForeachElement.addElement(new TextElement(sb.toString()));

        answer.addElement(valuesForeachElement);

        XmlElement valuesTrimElement = new XmlElement("trim"); //$NON-NLS-1$
        valuesTrimElement.addAttribute(new Attribute("prefix", "values (")); //$NON-NLS-1$ //$NON-NLS-2$
        valuesTrimElement.addAttribute(new Attribute("suffix", ")")); //$NON-NLS-1$ //$NON-NLS-2$
        valuesTrimElement.addAttribute(new Attribute("suffixOverrides", ",")); //$NON-NLS-1$ //$NON-NLS-2$
        answer.addElement(valuesTrimElement);

        parentElement.addElement(answer);
    }

    private void addBatchInsertElements(XmlElement parentElement, IntrospectedTable introspectedTable) {
        XmlElement answer = new XmlElement("insert");

        answer.addAttribute(new Attribute("id", "batchInsert"));

        answer.addAttribute(new Attribute("parameterType", "java.util.List"));

        StringBuilder insertClause = new StringBuilder();

        insertClause.append("insert into ");
        insertClause.append(introspectedTable.getFullyQualifiedTableNameAtRuntime());
        insertClause.append(" (");

        StringBuilder valuesClause = new StringBuilder();
        valuesClause.append("(");

        List<String> valuesClauses = new LinkedList<>();
        List<IntrospectedColumn> columns = ListUtilities.removeIdentityAndGeneratedAlwaysColumns(introspectedTable.getAllColumns());
        for (int i = 0; i < columns.size(); i++) {
            IntrospectedColumn introspectedColumn = columns.get(i);

            insertClause.append(MyBatis3FormattingUtilities.getEscapedColumnName(introspectedColumn));
            valuesClause.append(getItemParameterClause(introspectedColumn));
            if (i + 1 < columns.size()) {
                insertClause.append(", ");
                valuesClause.append(", ");
            }

            if (valuesClause.length() > 80) {
                answer.addElement(new TextElement(insertClause.toString()));
                insertClause.setLength(0);
                OutputUtilities.xmlIndent(insertClause, 1);

                valuesClauses.add(valuesClause.toString());
                valuesClause.setLength(0);
                OutputUtilities.xmlIndent(valuesClause, 1);
            }
        }

        insertClause.append(')');
        answer.addElement(new TextElement(insertClause.toString()));

        answer.addElement(new TextElement("values"));

        valuesClause.append(')');
        valuesClauses.add(valuesClause.toString());

        XmlElement foreach = new XmlElement("foreach");
        foreach.addAttribute(new Attribute("collection", "list"));
        foreach.addAttribute(new Attribute("item", "item"));
        foreach.addAttribute(new Attribute("separator", ","));

        for (String clause : valuesClauses) {
            foreach.addElement(new TextElement(clause));
        }

        answer.addElement(foreach);

        parentElement.addElement(answer);
    }

    public static String getItemParameterClause(IntrospectedColumn introspectedColumn) {
        return getItemParameterClause(introspectedColumn, null);
    }

    public static String getItemParameterClause(IntrospectedColumn introspectedColumn, String prefix) {
        StringBuilder sb = new StringBuilder();

        sb.append("#{item."); //$NON-NLS-1$
        sb.append(introspectedColumn.getJavaProperty(prefix));
        sb.append(",jdbcType="); //$NON-NLS-1$
        sb.append(introspectedColumn.getJdbcTypeName());

        if (stringHasValue(introspectedColumn.getTypeHandler())) {
            sb.append(",typeHandler="); //$NON-NLS-1$
            sb.append(introspectedColumn.getTypeHandler());
        }

        sb.append('}');

        return sb.toString();
    }

    private Method copyProperties(Method source, String name) {
        Method method = new Method(name);
        method.setReturnType(source.getReturnType().get());
        method.setConstructor(source.isConstructor());
        method.setDefault(source.isDefault());
        method.setNative(source.isNative());
        method.setSynchronized(source.isSynchronized());
        method.setFinal(source.isFinal());
        method.setStatic(source.isStatic());
        method.setVisibility(source.getVisibility());
        addParameter(method);
        return method;
    }

    private void addParameter(Method method) {
        method.getParameters().clear();
        method.addParameter(new Parameter(new FullyQualifiedJavaType("List<Model>"), "list"));
    }

}
