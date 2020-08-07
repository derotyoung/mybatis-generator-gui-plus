/**
 * Copyright 2006-2017 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mybatis.generator.codegen.mybatis3.xmlmapper.elements;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.ListUtilities;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;

/**
 * @author derotyoung
 */
public class BatchUpdateSelectiveElementGenerator extends AbstractXmlElementGenerator {

    public BatchUpdateSelectiveElementGenerator() {
        super();
    }

    @Override
    public void addElements(XmlElement parentElement) {
        XmlElement answer = new XmlElement("update");

        answer.addAttribute(new Attribute(
                "id", introspectedTable.getBatchUpdateSelectiveStatementId()));

        // answer.addAttribute(new Attribute("parameterType", "java.util.List"));
        FullyQualifiedJavaType parameterType = FullyQualifiedJavaType
                .getNewListInstance();

        answer.addAttribute(new Attribute("parameterType",
                parameterType.getFullyQualifiedName()));

        StringBuilder sb = new StringBuilder();

        sb.append("update ");
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
            sb.append(introspectedColumn.getActualColumnName());
            sb.append(" = case");
            XmlElement secondTrimElement = new XmlElement("trim");
            secondTrimElement.addAttribute(new Attribute("prefix", sb.toString()));
            secondTrimElement.addAttribute(new Attribute("suffix", "end,"));

            XmlElement foreachElement = new XmlElement("foreach");
            foreachElement.addAttribute(new Attribute("collection", "list"));
            foreachElement.addAttribute(new Attribute("index", "index"));
            foreachElement.addAttribute(new Attribute("item", "item"));

            sb.setLength(0);
            sb.append("when ");
            sb.append(primaryKeyColumn.getActualColumnName());
            sb.append(" = ");
            sb.append(MyBatis3FormattingUtilities.getItemParameterClause(primaryKeyColumn));
            sb.append(" then ");
            sb.append(MyBatis3FormattingUtilities.getItemParameterClause(introspectedColumn));
            TextElement textElement = new TextElement(sb.toString());

            XmlElement ifElement = new XmlElement("if");
            sb.setLength(0);
            sb.append("item.");
            sb.append(introspectedColumn.getJavaProperty());
            sb.append(" != null");
            ifElement.addAttribute(new Attribute("test", sb.toString()));
            ifElement.addElement(textElement);
            foreachElement.addElement(ifElement);

            secondTrimElement.addElement(foreachElement);

            firstTrimElement.addElement(secondTrimElement);
        }
        answer.addElement(firstTrimElement);

        sb.setLength(0);
        sb.append("where ");
        sb.append(primaryKeyColumn.getActualColumnName());
        sb.append(" in");
        answer.addElement(new TextElement(sb.toString()));

        XmlElement valuesForeachElement = new XmlElement("foreach");
        valuesForeachElement.addAttribute(new Attribute("open", "("));
        valuesForeachElement.addAttribute(new Attribute("close", ")"));
        valuesForeachElement.addAttribute(new Attribute("collection", "list"));
        valuesForeachElement.addAttribute(new Attribute("item", "item"));
        valuesForeachElement.addAttribute(new Attribute("separator", ", "));
        sb.setLength(0);
        sb.append(MyBatis3FormattingUtilities.getItemParameterClause(primaryKeyColumn));
        valuesForeachElement.addElement(new TextElement(sb.toString()));

        answer.addElement(valuesForeachElement);

        if (context.getPlugins()
                .sqlMapUpdateByPrimaryKeySelectiveElementGenerated(answer,
                        introspectedTable)) {
            parentElement.addElement(answer);
        }
    }
}
