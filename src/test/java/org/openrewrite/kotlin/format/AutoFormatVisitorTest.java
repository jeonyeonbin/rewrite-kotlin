/*
 * Copyright 2021 the original author or authors.
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
package org.openrewrite.kotlin.format;

import org.junit.jupiter.api.Test;
import org.openrewrite.test.RecipeSpec;
import org.openrewrite.test.RewriteTest;

import static org.openrewrite.kotlin.Assertions.kotlin;

class AutoFormatVisitorTest implements RewriteTest {

    @Override
    public void defaults(RecipeSpec spec) {
        spec.recipe(new AutoFormat());
    }

    @Test
    void keepMaximumBetweenHeaderAndPackage() {
        rewriteRun(
          kotlin(
            """
              /*
               * This is a sample file.
               */




              package com.intellij.samples

              class Test {
              }
              """,
            """
              /*
               * This is a sample file.
               */


              package com.intellij.samples

              class Test {
              }
              """
          )
        );
    }

    @Test
    void tabsAndIndents() {
        rewriteRun(
          kotlin(
            """
              open class Some {
                  private val f: (Int) -> Int = { a: Int -> a * 2 }
                  fun foo(): Int {
                  val test: Int = 12
                  for (i in 10..42) {
                  println(when {
                  i < test -> -1
                  i > test -> 1
                  else -> 0
                  })
                  }
                  if (true) {
                  }
                  while (true) {
                      break
                  }
                  try {
                      when (test) {
                          12 -> println("foo")
                          in 10..42 -> println("baz")
                          else -> println("bar")
                      }
                  } catch (e: Exception) {
                  } finally {
                  }
                  return test
                  }

                  fun multilineMethod(
                          foo: String,
                          bar: String
                          ) {
                      foo
                              .length
                  }

                  fun expressionBodyMethod() =
                      "abc"
              }

              class AnotherClass<T : Any> : Some()
              """,
            """
              open class Some {
                  private val f: (Int) -> Int = {   a: Int ->
                       a * 2
                  }

                  fun foo(): Int {
                      val test: Int = 12
                      for (i in 10..42) {
                          println(when {
                              i < test -> -1
                              i > test -> 1
                              else -> 0
                          })
                      }
                      if (true) {
                      }
                      while (true) {
                          break
                      }
                      try {
                          when (test) {
                              12 ->
                                  println("foo")
                             \s
                               in 10..42 ->
                                  println("baz")
                             \s
                              else ->
                                  println("bar")
                             \s
                          }
                      } catch (e: Exception) {
                      } finally {
                      }
                      return test
                  }

                  fun multilineMethod(
                            foo: String,
                            bar: String
                            ) {
                      foo
                              .length
                  }

                  fun expressionBodyMethod() =
                     \s
                          "abc"
                 \s
              }

              class AnotherClass <T : Any> : Some()
              """
          )
        );
    }

    @Test
    void classConstructor() {
        rewriteRun(
          kotlin(
            """
              package com.netflix.graphql.dgs.client.codegen

              class BaseProjectionNode (
                      val type: Int = 1
                      ) {
              }
              """
          )
        );
    }
}

