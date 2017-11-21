package main;

import java.util.*;

public class SpanningTree {

    class NodePair {
        Node cameFrom;

        public NodePair(Node cameFrom, Node current) {
            this.cameFrom = cameFrom;
            this.current = current;
        }

        Node current;
    }

    public void mutateIntoSpanningTree(ArrayList<Node> nodes) {
        // A visited list, and a copy of all the nodes, but without their edgesTo list!
        ArrayList<Node> newGraph = new ArrayList<>();
        Map<Node,Boolean> visited = new HashMap<>();
        for ( Node node : nodes ) {
            visited.put(node,false);
            Node newNode = new Node(node.position,node.id);
            newGraph.add(newNode);
        }

        if ( nodes.size() == 0 ) {
            return;
        }

        Queue<NodePair> bfs = new LinkedList<>();
        bfs.add(new NodePair(null,nodes.get(0)));

        while(!bfs.isEmpty()) {
            NodePair nodePair = bfs.remove();
            if ( visited.get(nodePair.current) ) {
                continue; // If node is already visited, skip it!
            } else {
                // IF we mark a nodePair as newly visited, then add this edge onto the graph!
                visited.put(nodePair.current, true);
                // Except for the first vistied node, which is not an edge!
                if ( nodePair.cameFrom != null ) {
                    newGraph.get(nodePair.cameFrom.id).edgesTo.add(newGraph.get(nodePair.current.id));
                    newGraph.get(nodePair.current.id).edgesTo.add(newGraph.get(nodePair.cameFrom.id));
                }
            }

            for ( Node adjacent : nodePair.current.edgesTo ) {
                bfs.add(new NodePair(nodePair.current,adjacent));
            }
        }

        // Now mutate the old graph
        nodes.clear();
        for (Node node : newGraph ) {
            nodes.add(node);
        }

    }
}
