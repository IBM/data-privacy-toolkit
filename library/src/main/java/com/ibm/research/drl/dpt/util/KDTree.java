/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2017                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.util;

import java.util.*;
import java.util.stream.Collectors;

import static org.apache.commons.math3.util.FastMath.cos;
import static org.apache.commons.math3.util.FastMath.sin;

public class KDTree<T extends KDTree.CartesianPoint> {
    private KDTreeNode<T> root;

    private static final Comparator<CartesianPoint> X_COMPARATOR = Comparator.comparingDouble(o -> o.x);

    private static final Comparator<CartesianPoint> Y_COMPARATOR = Comparator.comparingDouble(o -> o.y);

    private static final Comparator<CartesianPoint> Z_COMPARATOR = Comparator.comparingDouble(o -> o.z);

    static final int X_AXIS = 0;
    static final int Y_AXIS = 1;
    static final int Z_AXIS = 2;

    /**
     * Default constructor.
     */

    KDTree(List<T> list) {
        super();
        int k = 3;
        root = createNode(list, k, 0);
    }

    private KDTreeNode<T> createNode(List<T> list, int k, int depth) {
        if (list == null || list.size() == 0)
            return null;

        int axis = depth % k;
        if (axis == X_AXIS) {
            list.sort(X_COMPARATOR);
        } else if (axis == Y_AXIS) {
            list.sort(Y_COMPARATOR);
        } else {
            list.sort(Z_COMPARATOR);
        }

        KDTreeNode<T> node = null;
        final List<T> less = new ArrayList<>(list.size());
        final List<T> more = new ArrayList<>(list.size());
        if (list.size() > 0) {
            int medianIndex = list.size() / 2;
            node = new KDTreeNode<>(list.get(medianIndex), k, depth);
            // Process list to see where each non-median point lies
            for (int i = 0; i < list.size(); i++) {
                if (i == medianIndex) continue;

                T p = list.get(i);
                if (KDTreeNode.compareTo(depth, k, p, node.point) <= 0) {
                    less.add(p);
                } else {
                    more.add(p);
                }
            }

            if ((medianIndex - 1 >= 0) && less.size() > 0) {
                node.lesser = createNode(less, k, depth + 1);
                node.lesser.parent = node;
            }

            if ((medianIndex <= list.size() - 1) && more.size() > 0) {
                node.greater = createNode(more, k, depth + 1);
                node.greater.parent = node;
            }
        }

        return node;
    }

    public boolean contains(T value) {
        if (value == null || root == null)
            return false;

        KDTreeNode<T> node = getNode(this, value);
        return (node != null);
    }

    private KDTreeNode<T> getNode(KDTree<T> tree, T value) {
        if (tree == null || tree.root == null || value == null)
            return null;

        KDTreeNode<T> node = tree.root;
        while (true) {
            if (node.point.equals(value)) {
                return node;
            } else if (KDTreeNode.compareTo(node.depth, node.k, value, node.point) <= 0) {
                // Lesser
                if (node.lesser == null) {
                    return null;
                }
                node = node.lesser;
            } else {
                // Greater
                if (node.greater == null) {
                    return null;
                }
                node = node.greater;
            }
        }
    }

    Collection<T> nearestNeighbourSearch(int k, T value) {
        if (value == null || root == null) {
            return Collections.emptyList();
        }

        TreeSet<KDTreeNode<T>> results = new TreeSet<>(new ComparatorAgainstFixedPoint(value));

        // Find the closest leaf node
        KDTreeNode<T> prev = null;
        KDTreeNode<T> node = root;
        while (node != null) {
            if (KDTreeNode.compareTo(node.depth, node.k, value, node.point) <= 0) {
                // Lesser
                prev = node;
                node = node.lesser;
            } else {
                // Greater
                prev = node;
                node = node.greater;
            }
        }
        KDTreeNode<T> leaf = prev;

        final Set<KDTreeNode<T>> examined = new HashSet<>();

        node = leaf;
        while (node != null) {
            // Search node
            searchNode(value, node, k, results, examined);
            node = node.parent;
        }

        return results.stream().map(treeNode -> treeNode.point).collect(Collectors.toList());
    }

    private void searchNode(T value, KDTreeNode<T> node, int K, TreeSet<KDTreeNode<T>> results, Set<KDTreeNode<T>> examined) {
        examined.add(node);

        KDTreeNode<T> lastNode = null;
        double lastDistance = Double.MAX_VALUE;
        if (results.size() > 0) {
            lastNode = results.last();
            lastDistance = lastNode.point.euclideanDistance(value);
        }
        Double nodeDistance = node.point.euclideanDistance(value);
        if (nodeDistance.compareTo(lastDistance) < 0) {
            if (results.size() == K && lastNode != null)
                results.remove(lastNode);
            results.add(node);
        } else if (nodeDistance.equals(lastDistance)) {
            results.add(node);
        } else if (results.size() < K) {
            results.add(node);
        }
        lastNode = results.last();
        lastDistance = lastNode.point.euclideanDistance(value);

        int axis = node.depth % node.k;
        KDTreeNode<T> lesser = node.lesser;
        KDTreeNode<T> greater = node.greater;

        if (lesser != null && !examined.contains(lesser)) {
            examined.add(lesser);

            double nodePoint;
            double valuePlusDistance;

            if (axis == X_AXIS) {
                nodePoint = node.point.x;
                valuePlusDistance = value.x - lastDistance;
            } else if (axis == Y_AXIS) {
                nodePoint = node.point.y;
                valuePlusDistance = value.y - lastDistance;
            } else {
                nodePoint = node.point.z;
                valuePlusDistance = value.z - lastDistance;
            }

            if (valuePlusDistance <= nodePoint) {
                searchNode(value, lesser, K, results, examined);
            }
        }
        if (greater != null && !examined.contains(greater)) {
            examined.add(greater);

            final double nodePoint;
            final double valuePlusDistance;
            if (axis == X_AXIS) {
                nodePoint = node.point.x;
                valuePlusDistance = value.x + lastDistance;
            } else if (axis == Y_AXIS) {
                nodePoint = node.point.y;
                valuePlusDistance = value.y + lastDistance;
            } else {
                nodePoint = node.point.z;
                valuePlusDistance = value.z + lastDistance;
            }

            if (valuePlusDistance >= nodePoint)
                searchNode(value, greater, K, results, examined);
        }
    }

    private  static class ComparatorAgainstFixedPoint<T extends KDTree.CartesianPoint> implements Comparator<KDTreeNode<T>> {
        private final CartesianPoint point;

        ComparatorAgainstFixedPoint(CartesianPoint point) {
            this.point = point;
        }

        @Override
        public int compare(KDTreeNode o1, KDTreeNode o2) {
            Double d1 = point.euclideanDistance(o1.point);
            Double d2 = point.euclideanDistance(o2.point);
            if (d1.compareTo(d2) < 0)
                return -1;
            else if (d2.compareTo(d1) < 0)
                return 1;
            return o1.point.compareTo(o2.point);
        }
    }

    private static class KDTreeNode<T extends CartesianPoint> implements Comparable<KDTreeNode<T>> {
        private final T point;
        private final int k;
        private final int depth;

        private KDTreeNode<T> parent = null;
        private KDTreeNode<T> lesser = null;
        private KDTreeNode<T> greater = null;

        KDTreeNode(T point, int k, int depth) {
            this.point = point;
            this.k = k;
            this.depth = depth;
        }

        static <T extends CartesianPoint> int compareTo(int depth, int k, T o1, T o2) {
            int axis = depth % k;
            if (axis == X_AXIS)
                return X_COMPARATOR.compare(o1, o2);
            if (axis == Y_AXIS)
                return Y_COMPARATOR.compare(o1, o2);
            return Z_COMPARATOR.compare(o1, o2);
        }

        @Override
        public int hashCode() {
            return 31 * (this.k + this.depth + this.point.hashCode());
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null)
                return false;
            if (!(obj instanceof KDTreeNode))
                return false;

            KDTreeNode<T> kdNode = (KDTreeNode<T>) obj;
            return this.compareTo(kdNode) == 0;
        }

        @Override
        public int compareTo(KDTreeNode<T> o) {
            return compareTo(depth, k, this.point, o.point);
        }
    }

    public static class CartesianPoint implements Comparable<CartesianPoint> {
        final double x;
        final double y;
        final double z;

        CartesianPoint(double x, double y) {
            this.x = x;
            this.y = y;
            this.z = 0;
        }

        public CartesianPoint(Double latitude, Double longitude) {
            x = cos(Math.toRadians(latitude)) * cos(Math.toRadians(longitude));
            y = cos(Math.toRadians(latitude)) * sin(Math.toRadians(longitude));
            z = sin(Math.toRadians(latitude));
        }

        public double euclideanDistance(CartesianPoint other) {
            return Math.sqrt(Math.pow((other.x - this.x), 2) + Math.pow((other.y - this.y), 2) + Math.pow((other.z - this.z), 2));
        }


        @Override
        public int hashCode() {
            int result;
            long temp;
            temp = Double.doubleToLongBits(x);
            result = (int) (temp ^ (temp >>> 32));
            temp = Double.doubleToLongBits(y);
            result = 31 * result + (int) (temp ^ (temp >>> 32));
            temp = Double.doubleToLongBits(z);
            result = 31 * result + (int) (temp ^ (temp >>> 32));
            return result;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof CartesianPoint)) return false;

            CartesianPoint that = (CartesianPoint) o;

            return Double.compare(that.x, x) == 0 && Double.compare(that.y, y) == 0 && Double.compare(that.z, z) == 0;
        }

        @Override
        public int compareTo(CartesianPoint o) {
            int xComp = X_COMPARATOR.compare(this, o);
            if (xComp != 0)
                return xComp;
            int yComp = Y_COMPARATOR.compare(this, o);
            if (yComp != 0)
                return yComp;
            return Z_COMPARATOR.compare(this, o);
        }
    }
}