/**
 * MIT License
 * <p>
 * Copyright (c) 2019 Nikita Vasilev
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE  LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package fir.needle.joint.collections;

import fir.needle.joint.colleclions.SimpleParametrizedPrefixTree;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UrlPrefixTreeTest {
    private SimpleParametrizedPrefixTree<String> tree;
    private List<SimpleParametrizedPrefixTree.Parameter> pathParams;

    @Nested
    class TestsWithOneSlashDelimiter {
        @BeforeEach
        void setUp() {
            tree = new SimpleParametrizedPrefixTree<>("/");
            pathParams = new LinkedList<>();
        }

        @Test
        void testInsertWithNonOverlappingUrlsAndNoPathParams() {
            final Map<String, String> toInsert = new TreeMap<>();
            toInsert.put("/sum/first/second", "SumValue");
            toInsert.put("/sub/first/second", "SubValue");
            toInsert.put("/mul/first/second", "MulValue");
            toInsert.put("/div/first/second", "DivValue");

            for (final Map.Entry<String, String> crtPair : toInsert.entrySet()) {
                tree.insert(crtPair.getKey(), crtPair.getValue());
            }

            for (final Map.Entry<String, String> crtPair : toInsert.entrySet()) {
                assertEquals(crtPair.getValue(), tree.find(crtPair.getKey(), pathParams));
            }
        }

        @Test
        void testInsertWithOverlappingUrlsAndPathParams() {
            tree.insert("/sum/{firstAddend}/{secondAddend}", "SumValue");
            tree.insert("/sub/first/second", "SubValue");
            tree.insert("/mul/{first}/{second}", "MulValue");
            tree.insert("/div/first/second", "DivValue");

            final String path = "/sum/firstAddend/42/secondAddend/24";

            assertEquals("SumValue", tree.find(path, pathParams));
            assertEquals(2, pathParams.size());
            assertEquals("firstAddend", pathParams.get(0).getName());
            assertEquals("42", path.substring(pathParams.get(0).getStartIndex(),
                    pathParams.get(0).getStartIndex() + pathParams.get(0).getLength()));
            assertEquals("secondAddend", pathParams.get(1).getName());
            assertEquals("24", path.substring(pathParams.get(1).getStartIndex(),
                    pathParams.get(1).getStartIndex() + pathParams.get(1).getLength()));
        }

        @Test
        void testInsertOnDuplicatedUrlsWithNoPathParams() {
            final Map<String, String> toInsert = new TreeMap<>();
            toInsert.put("/sum/first/second", "SumValue");
            toInsert.put("/sub/first/second", "SubValue");
            toInsert.put("/mul/first/second", "MulValue");
            toInsert.put("/div/first/second", "DivValue");

            for (final Map.Entry<String, String> crtPair : toInsert.entrySet()) {
                tree.insert(crtPair.getKey(), crtPair.getValue());
            }

            assertThrows(IllegalStateException.class, () -> tree.insert("/sum/first/second", "NewValue"));
        }

        @Test
        void testInsertOnOneUrlOverlapsPathParamsOfAnotherOne() {
            final Map<String, String> toInsert = new TreeMap<>();
            toInsert.put("/sum/{first}/{second}", "SumValue");
            toInsert.put("/sub/first/second", "SubValue");
            toInsert.put("/mul/first/second", "MulValue");
            toInsert.put("/div/{first}/{second}", "DivValue");

            for (final Map.Entry<String, String> crtPair : toInsert.entrySet()) {
                tree.insert(crtPair.getKey(), crtPair.getValue());
            }

            assertThrows(IllegalStateException.class, () -> tree.insert("/div/first/second", "NewValue"));
        }

        @Test
        void testInsertOnOneUrlIsSubUrlOfAnotherOne() {
            final Map<String, String> toInsert = new TreeMap<>();
            toInsert.put("/sum/{first}/{second}", "SumValue");
            toInsert.put("/sub/first/second", "SubValue");
            toInsert.put("/mul/first/second", "MulValue");
            toInsert.put("/div/{first}/{second}", "DivValue");

            for (final Map.Entry<String, String> crtPair : toInsert.entrySet()) {
                tree.insert(crtPair.getKey(), crtPair.getValue());
            }

            assertThrows(IllegalStateException.class, () -> tree.insert("/div", "NewValue"));
        }

        @Test
        void testInsertOnTwoEqualUrlsWithParams() {
            final Map<String, String> toInsert = new TreeMap<>();
            toInsert.put("/sum/{first}/{second}", "SumValue");
            toInsert.put("/sub/first/second", "SubValue");
            toInsert.put("/mul/first/second", "MulValue");
            toInsert.put("/div/{first}/{second}", "DivValue");

            for (final Map.Entry<String, String> crtPair : toInsert.entrySet()) {
                tree.insert(crtPair.getKey(), crtPair.getValue());
            }

            assertThrows(IllegalStateException.class, () -> tree.insert("/sum/{first}/{second}", "NewValue"));
        }

        @Test
        void testInsertOnOneUrlIsSubUrlOfAnother() {
            final Map<String, String> toInsert = new TreeMap<>();
            toInsert.put("/sum/{first}/{second}", "SumValue");
            toInsert.put("/sub/first/second", "SubValue");
            toInsert.put("/mul/first/second", "MulValue");
            toInsert.put("/div/{first}/{second}", "DivValue");

            for (final Map.Entry<String, String> crtPair : toInsert.entrySet()) {
                tree.insert(crtPair.getKey(), crtPair.getValue());
            }

            assertThrows(IllegalStateException.class, () -> tree.insert("/sum/{first}", "NewValue"));
        }

        @Test
        void testInsertOnOneUrlIsPermutationOfAnother() {
            tree.insert("/sum/{first}/{second}", "SumValue");
            tree.insert("/{first}/sum/{second}", "TestValue");

            assertEquals("SumValue", tree.find("/sum/first/42/second/42", pathParams));
            assertEquals("TestValue", tree.find("/first/42/sum/second/42", pathParams));
        }
    }

    @Nested
    class TestsWithTreeCharsDelimiter {
        private final String delimiter = "ABC";

        @BeforeEach
        void setUp() {
            tree = new SimpleParametrizedPrefixTree<>(delimiter);
            pathParams = new LinkedList<>();
        }

        @Test
        void testInsertWithNonOverlappingUrlsAndNoPathParams() {
            final Map<String, String> toInsert = new TreeMap<>();
            toInsert.put(delimiter + "sum" + delimiter + "first" + delimiter + "second", "SumValue");
            toInsert.put(delimiter + "sub" + delimiter + "first" + delimiter + "second", "SubValue");
            toInsert.put(delimiter + "mul" + delimiter + "first" + delimiter + "second", "MulValue");
            toInsert.put(delimiter + "div" + delimiter + "first" + delimiter + "second", "DivValue");

            for (final Map.Entry<String, String> crtPair : toInsert.entrySet()) {
                tree.insert(crtPair.getKey(), crtPair.getValue());
            }

            for (final Map.Entry<String, String> crtPair : toInsert.entrySet()) {
                assertEquals(crtPair.getValue(), tree.find(crtPair.getKey(), pathParams));
            }
        }

        @Test
        void testInsertWithOverlappingUrlsAndPathParams() {
            tree.insert(delimiter + "sum" + delimiter + "{firstAddend}" + delimiter + "{secondAddend}", "SumValue");
            tree.insert(delimiter + "sub" + delimiter + "first" + delimiter + "second", "SubValue");
            tree.insert(delimiter + "mul" + delimiter + "{first}" + delimiter + "{second}", "MulValue");
            tree.insert(delimiter + "div" + delimiter + "first" + delimiter + "second", "DivValue");

            final String path = delimiter + "sum" + delimiter + "firstAddend" + delimiter + "42" + delimiter +
                    "secondAddend" + delimiter + "24";

            assertEquals("SumValue", tree.find(path, pathParams));
            assertEquals(2, pathParams.size());
            assertEquals("firstAddend", pathParams.get(0).getName());
            assertEquals("42", path.substring(pathParams.get(0).getStartIndex(),
                    pathParams.get(0).getStartIndex() + pathParams.get(0).getLength()));
            assertEquals("secondAddend", pathParams.get(1).getName());
            assertEquals("24", path.substring(pathParams.get(1).getStartIndex(),
                    pathParams.get(1).getStartIndex() + pathParams.get(1).getLength()));
        }

        @Test
        void testInsertOnDuplicatedUrlsWithNoPathParams() {
            final Map<String, String> toInsert = new TreeMap<>();
            toInsert.put(delimiter + "sum" + delimiter + "first" + delimiter + "second", "SumValue");
            toInsert.put(delimiter + "sub" + delimiter + "first" + delimiter + "second", "SubValue");
            toInsert.put(delimiter + "mul" + delimiter + "first" + delimiter + "second", "MulValue");
            toInsert.put(delimiter + "div" + delimiter + "first" + delimiter + "second", "DivValue");

            for (final Map.Entry<String, String> crtPair : toInsert.entrySet()) {
                tree.insert(crtPair.getKey(), crtPair.getValue());
            }

            assertThrows(IllegalStateException.class, () -> tree.insert("/sum/first/second", "NewValue"));
        }

        @Test
        void testInsertOnOneUrlOverlapsPathParamsOfAnotherOne() {
            final Map<String, String> toInsert = new TreeMap<>();
            toInsert.put(delimiter + "sum" + delimiter + "{first}" + delimiter + "{second}", "SumValue");
            toInsert.put(delimiter + "sub" + delimiter + "first" + delimiter + "second", "SubValue");
            toInsert.put(delimiter + "mul" + delimiter + "first" + delimiter + "second", "MulValue");
            toInsert.put(delimiter + "div" + delimiter + "{first}" + delimiter + "{second}", "DivValue");

            for (final Map.Entry<String, String> crtPair : toInsert.entrySet()) {
                tree.insert(crtPair.getKey(), crtPair.getValue());
            }

            assertThrows(IllegalStateException.class, () -> tree.insert("/div/first/second", "NewValue"));
        }

        @Test
        void testInsertOnOneUrlIsSubUrlOfAnotherOne() {
            final Map<String, String> toInsert = new TreeMap<>();
            toInsert.put(delimiter + "sum" + delimiter + "{first}" + delimiter + "{second}", "SumValue");
            toInsert.put(delimiter + "sub" + delimiter + "first" + delimiter + "second", "SubValue");
            toInsert.put(delimiter + "mul" + delimiter + "first" + delimiter + "second", "MulValue");
            toInsert.put(delimiter + "div" + delimiter + "{first}" + delimiter + "{second}", "DivValue");

            for (final Map.Entry<String, String> crtPair : toInsert.entrySet()) {
                tree.insert(crtPair.getKey(), crtPair.getValue());
            }

            assertThrows(IllegalStateException.class, () -> tree.insert("/div", "NewValue"));
        }

        @Test
        void testInsertOnTwoEqualUrlsWithParams() {
            final Map<String, String> toInsert = new TreeMap<>();
            toInsert.put(delimiter + "sum" + delimiter + "{first}" + delimiter + "{second}", "SumValue");
            toInsert.put(delimiter + "sub" + delimiter + "first" + delimiter + "second", "SubValue");
            toInsert.put(delimiter + "mul" + delimiter + "first" + delimiter + "second", "MulValue");
            toInsert.put(delimiter + "div" + delimiter + "{first}" + delimiter + "{second}", "DivValue");

            for (final Map.Entry<String, String> crtPair : toInsert.entrySet()) {
                tree.insert(crtPair.getKey(), crtPair.getValue());
            }

            assertThrows(IllegalStateException.class, () -> tree.insert("/sum/{first}/{second}", "NewValue"));
        }

        @Test
        void testInsertOnOneUrlIsSubUrlOfAnother() {
            final Map<String, String> toInsert = new TreeMap<>();
            toInsert.put(delimiter + "sum" + delimiter + "{first}" + delimiter + "{second}", "SumValue");
            toInsert.put(delimiter + "sub" + delimiter + "first" + delimiter + "second", "SubValue");
            toInsert.put(delimiter + "mul" + delimiter + "first" + delimiter + "second", "MulValue");
            toInsert.put(delimiter + "div" + delimiter + "{first}" + delimiter + "{second}", "DivValue");

            for (final Map.Entry<String, String> crtPair : toInsert.entrySet()) {
                tree.insert(crtPair.getKey(), crtPair.getValue());
            }

            assertThrows(IllegalStateException.class, () -> tree.insert("/sum/{first}", "NewValue"));
        }

        @Test
        void testInsertOnOneUrlIsPermutationOfAnother() {
            tree.insert(delimiter + "sum" + delimiter + "{first}" + delimiter + "{second}", "SumValue");
            tree.insert(delimiter + "{first}" + delimiter + "sum" + delimiter + "{second}", "TestValue");

            final String usualUrl =
                    delimiter + "sum" + delimiter + "first" + delimiter + "42" + delimiter + "second" + delimiter +
                            "42";
            final String permutedUrl =
                    delimiter + "first" + delimiter + "42" + delimiter + "sum" + delimiter + "second" + delimiter +
                            "42";

            assertEquals("SumValue", tree.find(usualUrl, pathParams));
            assertEquals("TestValue", tree.find(permutedUrl, pathParams));
        }
    }
}