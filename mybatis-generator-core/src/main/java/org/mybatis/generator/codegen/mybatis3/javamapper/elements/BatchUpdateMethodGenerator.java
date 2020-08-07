/**
 *    Copyright 2006-2019 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.mybatis.generator.codegen.mybatis3.javamapper.elements;

import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.codegen.mybatis3.javamapper.elements.AbstractJavaMapperMethodGenerator;

import java.util.Set;
import java.util.TreeSet;

public class BatchUpdateMethodGenerator extends
        AbstractJavaMapperMethodGenerator {

    public BatchUpdateMethodGenerator() {
        super();
    }

    @Override
    public void addInterfaceElements(Interface interfaze) {
        Method method = new Method(introspectedTable
                .getBatchUpdateStatementId());
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setAbstract(true);
        method.setReturnType(FullyQualifiedJavaType.getIntInstance());

        FullyQualifiedJavaType parameterType = FullyQualifiedJavaType
                .getNewListInstance();

        Set<FullyQualifiedJavaType> importedTypes = new TreeSet<>();
        FullyQualifiedJavaType listType = introspectedTable.getRules()
                .calculateAllFieldsClass();
        importedTypes.add(listType);

        parameterType.addTypeArgument(listType);
        importedTypes.add(parameterType);
        method.addParameter(new Parameter(parameterType, "list"));

        context.getCommentGenerator().addGeneralMethodComment(method,
                introspectedTable);

        addMapperAnnotations(method);
        
        if (context.getPlugins()
                .clientUpdateByExampleSelectiveMethodGenerated(method, interfaze,
                        introspectedTable)) {
            addExtraImports(interfaze);
            interfaze.addImportedTypes(importedTypes);
            interfaze.addMethod(method);
        }
    }

    public void addMapperAnnotations(Method method) {
        // extension point for subclasses
    }

    public void addExtraImports(Interface interfaze) {
        // extension point for subclasses
    }
}
