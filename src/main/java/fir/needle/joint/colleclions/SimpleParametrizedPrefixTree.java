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
package fir.needle.joint.colleclions;

import java.util.List;

public class SimpleParametrizedPrefixTree<V> {
    private Node<V> root;
    private final CharSequence delimiter;

    public SimpleParametrizedPrefixTree(final CharSequence delimiter) {
        this.delimiter = delimiter;
    }

    public V find(final CharSequence path, final List<Parameter> pathParams) {
        return find(root, path, delimiter.length(), pathParams);
    }

    public void insert(final String path, final V value) {
        root = insert(root, path.split(delimiter.toString()), 1, value);
    }

    private boolean match(final CharSequence pathPart, final CharSequence path, final int searchStartIndex) {
        return areEqual(pathPart, path, searchStartIndex) && areEqual(delimiter, path,
            searchStartIndex + pathPart.length());

    }

    private boolean areEqual(final CharSequence first, final CharSequence second, final int shiftForSecond) {
        for (int i = 0; i < first.length() && i + shiftForSecond < second.length(); i++) {
            if (first.charAt(i) != second.charAt(i + shiftForSecond)) {
                return false;
            }
        }

        return true;
    }

    private int findNextDelimiterStartIndex(final CharSequence path, final int searchStartIndex) {
        int delimiterIndex = 0;
        for (int i = searchStartIndex; i < path.length(); i++) {
            if (path.charAt(i) != delimiter.charAt(delimiterIndex++)) {
                delimiterIndex = 0;
                continue;
            }

            if (delimiterIndex >= delimiter.length()) {
                return i - delimiter.length() + 1;
            }
        }

        return path.length();
    }

    private V find(
            final Node<V> crtNode,
            final CharSequence path,
            final int pathPartStartIndex,
            final List<Parameter> pathParams) {

        if (crtNode == null || pathPartStartIndex >= path.length()) {
            return null;
        }

        final String patterPathPart = crtNode.getKey();
        if (!match(patterPathPart, path, pathPartStartIndex)) {
            return find(crtNode.getBrother(), path, pathPartStartIndex, pathParams);
        }

        int newPartStartIndex;
        if (crtNode.isParam()) {
            newPartStartIndex = pathPartStartIndex + patterPathPart.length() + delimiter.length();

            if (newPartStartIndex >= path.length()) {
                return null;
            }

            final int paramLength = findNextDelimiterStartIndex(path, newPartStartIndex) - newPartStartIndex;
            pathParams.add(new Parameter(crtNode.getKey(), newPartStartIndex, paramLength));

            newPartStartIndex += paramLength + delimiter.length();
        } else {
            newPartStartIndex = pathPartStartIndex + patterPathPart.length() + delimiter.length();
        }

        return crtNode.isLeaf() && newPartStartIndex >= path.length() ?
            crtNode.getValue() :
            find(crtNode.getChild(), path, newPartStartIndex, pathParams);
    }

    private Node<V> insert(final Node<V> crtNode, final String[] pathChunks, final int pathIndex, final V value) {
        if (pathIndex >= pathChunks.length) {
            if (crtNode != null) {
                throw new IllegalStateException("New url overlaps with existing one!!!");
            } else {
                return null;
            }
        }

        String crtPathChunk = pathChunks[pathIndex];
        final boolean isParam = crtPathChunk.startsWith("{") && crtPathChunk.endsWith("}");
        crtPathChunk = isParam ? crtPathChunk.substring(1, crtPathChunk.length() - 1) : crtPathChunk;

        if (crtNode != null) {
            if (crtNode.getKey().equals(crtPathChunk)) {
                if (pathIndex + 1 >= pathChunks.length) {
                    throw new IllegalStateException("New url overlaps with existing one!!!");
                }
                crtNode.setChild(insert(crtNode.getChild(), pathChunks, pathIndex + 1, value));
            } else {
                crtNode.setBrother(insert(crtNode.getBrother(), pathChunks, pathIndex, value));
            }
            return crtNode;
        }

        final Node<V> newNode = new Node<>(crtPathChunk, isParam);

        newNode.setChild(insert(newNode.getChild(), pathChunks, pathIndex + 1, value));
        if (newNode.getChild() == null) {
            newNode.setValue(value);
        }

        return newNode;
    }

    private static class Node<V> {
        private final String key;
        private final boolean isParam;
        private V value;

        private Node<V> child;
        private Node<V> brother;

        Node(final String key, final boolean isParam) {
            this.key = key;
            this.isParam = isParam;
        }

        void setChild(final Node<V> child) {
            this.child = child;
        }

        void setBrother(final Node<V> brother) {
            this.brother = brother;
        }

        public void setValue(final V value) {
            this.value = value;
        }

        V getValue() {
            return value;
        }

        String getKey() {
            return key;
        }

        boolean isParam() {
            return isParam;
        }

        boolean isLeaf() {
            return value != null;
        }

        Node<V> getChild() {
            return child;
        }

        Node<V> getBrother() {
            return brother;
        }
    }

    public static class Parameter {
        private final String name;
        private final int startIndex;
        private final int length;

        public Parameter(final String name, final int startIndex, final int length) {
            this.name = name;
            this.startIndex = startIndex;
            this.length = length;
        }

        public String getName() {
            return name;
        }

        public int getStartIndex() {
            return startIndex;
        }

        public int getLength() {
            return length;
        }
    }
}