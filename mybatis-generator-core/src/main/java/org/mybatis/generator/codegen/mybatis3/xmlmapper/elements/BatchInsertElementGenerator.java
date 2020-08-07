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
import org.mybatis.generator.api.dom.OutputUtilities;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.ListUtilities;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;

import java.util.LinkedList;
import java.util.List;

/**
 * @author derotyoung
 */
public class BatchInsertElementGenerator extends AbstractXmlElementGenerator {

    public BatchInsertElementGenerator() {
        super();
    }

    @Override
    public void addElements(XmlElement parentElement) {
        XmlElement answer = new XmlElement("insert");

        answer.addAttribute(new Attribute(
                "id", introspectedTable.getBatchInsertStatementId()));

        FullyQualifiedJavaType parameterType = FullyQualifiedJavaType
                .getNewListInstance();

        answer.addAttribute(new Attribute("parameterType",
                parameterType.getFullyQualifiedName()));

        StringBuilder insertClause = new StringBuilder();

        insertClause.append("insert into ");
        insertClause.append(introspectedTable.getFullyQualifiedTableNameAtRuntime());
        insertClause.append(" (");

        StringBuilder valuesClause = new StringBuilder();
        valuesClause.append("(");

        List<String> valuesClauses = new LinkedList<>();
        List<IntrospectedColumn> columns =
                ListUtilities.removeIdentityAndGeneratedAlwaysColumns(introspectedTable.getAllColumns());
        for (int i = 0; i < columns.size(); i++) {
            IntrospectedColumn introspectedColumn = columns.get(i);

            insertClause.append(MyBatis3FormattingUtilities.getEscapedColumnName(introspectedColumn));
            valuesClause.append(MyBatis3FormattingUtilities.getItemParameterClause(introspectedColumn));
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

        if (context.getPlugins()
                .sqlMapUpdateByExampleSelectiveElementGenerated(answer,
                        introspectedTable)) {
            parentElement.addElement(answer);
        }
    }

}
