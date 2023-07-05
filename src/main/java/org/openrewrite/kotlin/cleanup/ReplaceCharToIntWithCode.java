/*
 * Copyright 2023 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.openrewrite.kotlin.cleanup;

import lombok.EqualsAndHashCode;
import lombok.Value;
import org.openrewrite.ExecutionContext;
import org.openrewrite.Recipe;
import org.openrewrite.TreeVisitor;
import org.openrewrite.java.MethodMatcher;
import org.openrewrite.java.tree.J;
import org.openrewrite.kotlin.KotlinParser;
import org.openrewrite.kotlin.KotlinVisitor;
import org.openrewrite.kotlin.tree.K;


@Value
@EqualsAndHashCode(callSuper = true)
public class ReplaceCharToIntWithCode extends Recipe {
    private static final MethodMatcher CHAR_TO_INT_METHOD_MATCHER = new MethodMatcher("kotlin.Char toInt()");
    private static J.FieldAccess charCodeTemplate = null;

    @Override
    public String getDisplayName() {
        return "Replace `Char#toInt()` with `Char#code`";
    }

    @Override
    public String getDescription() {
        return "Replace the usage of the deprecated `Char#toInt()` with `Char#code`. "
               + "Please ensure that your Kotlin version is 1.5 or later to support the `Char#code` property. "
               + "Note that the current implementation does not perform a Kotlin version check.";
    }

    @Override
    public TreeVisitor<?, ExecutionContext> getVisitor() {
        return new KotlinVisitor<ExecutionContext>() {
            @Override
            public J visitMethodInvocation(J.MethodInvocation method,
                                                            ExecutionContext executionContext) {
                if (CHAR_TO_INT_METHOD_MATCHER.matches(method) && method.getSelect() != null) {
                    J.FieldAccess codeTemplate = getCharCodeTemplate();
                    return codeTemplate.withTarget(method.getSelect()).withPrefix(method.getPrefix());
                }
                return super.visitMethodInvocation(method, executionContext);
            }
        };
    }

    @SuppressWarnings("all")
    private static J.FieldAccess getCharCodeTemplate() {
        if (charCodeTemplate == null) {
            K.CompilationUnit kcu = KotlinParser.builder().build()
                .parse("fun method(c : Char) {c.code}")
                .map(K.CompilationUnit.class::cast)
                .findFirst()
                .get();

            charCodeTemplate = (J.FieldAccess) ((J.MethodDeclaration) kcu.getStatements().get(0)).getBody().getStatements().get(0);
        }

        return charCodeTemplate;
    }
}