package dev.reactant.reactant.utils.types;


import java.util.*;
import java.util.stream.Collectors;

public class DirectedAcyclicGraph<T> {
    private HashMap<T, Node<T>> nodes = new HashMap<>();

    public Node<T> addNode(T data) {
        Node<T> node = new Node<>(data);
        nodes.put(data, node);
        return node;
    }

    public HashMap<T, Node<T>> getNodes() {
        return nodes;
    }

    public Node<T> getNode(T data) {
        return nodes.get(data);
    }

    public Node<T> getNodeOrAdd(T data) {
        return !nodes.containsKey(data) ? addNode(data) : nodes.get(data);
    }


    public SolveResult<T> solveTopologicalOrdering() {
        return new Solver<>(this).getResult();
    }

    private static class Solver<T> {
        private DirectedAcyclicGraph<T> dag;
        private HashMap<Node<T>, Integer> nodeDepthMap = new HashMap<>();
        private SolveResult<T> result = new SolveResult<>();

        public Solver(DirectedAcyclicGraph<T> dag) {
            this.dag = dag;
            dag.nodes.forEach((data, node) -> solveDepth(node, new ArrayList<>()));
            result.solvedOrder = nodeDepthMap.entrySet().stream().sorted(Comparator.comparingInt(Map.Entry::getValue)).map(Map.Entry::getKey).collect(Collectors.toList());
        }

        public SolveResult<T> getResult() {
            return result;
        }

        private Integer solveDepth(Node<T> node, List<Node<T>> nodeWalkPath) {
            if (result.ignoredNodes.contains(node)) throw new IgnoredNodeException();
            if (nodeDepthMap.containsKey(node)) {
                return nodeDepthMap.get(node);
            } else {
                if (nodeWalkPath.contains(node)) {
                    nodeWalkPath.add(node);
                    result.ignoredNodes.add(node);
                    int cyclicConnectedIndex = nodeWalkPath.indexOf(node);
                    result.cyclicNodesLists.add(nodeWalkPath);
                    throw new CyclicGraphException(new ArrayList<>(nodeWalkPath.subList(cyclicConnectedIndex, nodeWalkPath.size() - 1)));
                }
                nodeWalkPath.add(node);
                int parentDepth;
                try {
                    parentDepth = node.parents.stream().map(parent -> solveDepth(parent, new ArrayList<>(nodeWalkPath))).max(Integer::compareTo).orElse(-1) + 1;
                } catch (CyclicGraphException | IgnoredNodeException e) {
                    result.ignoredNodes.add(node);
                    throw e;
                }
                nodeDepthMap.put(node, parentDepth);
                return parentDepth;
            }
        }

        private static class IgnoredNodeException extends RuntimeException {

        }

        private static class CyclicGraphException extends RuntimeException {
            private List<Node<?>> nodePath;

            public CyclicGraphException(List<Node<?>> nodePath) {
                super(nodePath.stream().map(node -> node.getData().toString()).collect(Collectors.joining("->")));
                this.nodePath = nodePath;
            }
        }
    }


    public static class SolveResult<T> {
        private Set<Node<T>> ignoredNodes = new HashSet<>();
        private Set<List<Node<T>>> cyclicNodesLists = new HashSet<>();
        private List<Node<T>> solvedOrder = new ArrayList<>();

        public Set<Node<T>> getIgnoredNodes() {
            return ignoredNodes;
        }

        public Set<List<Node<T>>> getCyclicNodesLists() {
            return cyclicNodesLists;
        }

        public List<Node<T>> getSolvedOrder() {
            return solvedOrder;
        }
    }


    public static class Node<T> implements Iterable<Node<T>> {
        private T data;
        private HashSet<Node<T>> childs = new HashSet<>();
        private HashSet<Node<T>> parents = new HashSet<>();

        private Node(T data) {
            this.data = data;
        }

        public boolean isLeaf() {
            return childs.isEmpty();
        }

        public boolean isRoot() {
            return parents.isEmpty();
        }

        public HashSet<Node<T>> getChilds() {
            return childs;
        }

        public HashSet<Node<T>> getParents() {
            return parents;
        }

        public void addChild(Node<T> child) {
            childs.add(child);
            child.parents.add(this);
        }

        public void addParent(Node<T> parent) {
            parents.add(parent);
            parent.childs.add(this);
        }

        public void removeChild(Node<T> child) {
            childs.remove(child);
            child.parents.remove(this);
        }

        public void removeParent(Node<T> parent) {
            parents.remove(parent);
            parent.childs.remove(this);
        }

        public T getData() {
            return data;
        }

        @Override
        public Iterator<Node<T>> iterator() {
            return childs.iterator();
        }
    }
}
