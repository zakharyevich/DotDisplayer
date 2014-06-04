package org.vzdot;
import java.util.*;

import org.edmonds.AdjacencyList;
import org.edmonds.Edge;
import org.edmonds.Edmonds;
import org.edmonds.Edmonds_Andre;
import org.edmonds.Node;
import org.edmonds.SCC;
import org.edmonds.TarjanSCC;



public class RootedBranch {
	private Node root;
    private AdjacencyList branch;

	public RootedBranch(Node node, AdjacencyList branch) {
	 this.root = node;
	 this.branch = branch;
	}

	public static List<RootedBranch> extractBranches(
			AdjacencyList edges) {
		List<RootedBranch> branches = new ArrayList<RootedBranch>();

		SCC sccSsearch = new TarjanSCC();
		List<Collection<Node>> strongConnectedComponents = sccSsearch
				.runSCCsearch(edges);

		Map<Node, AdjacencyList> extractRootedComponents = extractRootedComponents(
				edges, strongConnectedComponents);
		for (Node node : extractRootedComponents.keySet()) {

			Edmonds edmonds = new Edmonds_Andre();

			AdjacencyList branch = edmonds.getMinBranching(node,
					extractRootedComponents.get(node));
			branches.add(new RootedBranch(node, branch));
		}
		return branches;
	}

	private static Map<Node, AdjacencyList> extractRootedComponents(
			AdjacencyList edges,
			List<Collection<Node>> strongConnectedComponents) {
		class ComponentWithRoot {
			private Collection<Node> component;
			private Node root;

			public ComponentWithRoot(Collection<Node> component, Node root) {
				this.setComponent(component);
				this.setRoot(root);
			}

			public void setComponent(Collection<Node> component) {
				this.component = component;
			}

			public Node getRoot() {
				return root;
			}

			public void setRoot(Node root) {
				this.root = root;
			}

		}
		Map<Collection<Node>, ComponentWithRoot> rootedComponents = new HashMap<Collection<Node>, ComponentWithRoot>();
		Map<Collection<Node>, ComponentWithRoot> connectedComponents = new HashMap<Collection<Node>, ComponentWithRoot>();

		for (Collection<Node> componentToTest : strongConnectedComponents) {
			Node rootNode = null;
			rootedComponents.put(componentToTest, new ComponentWithRoot(
					componentToTest, null));

			Collection<Node> rootComponent = null;
			for (Collection<Node> componentToCheck : strongConnectedComponents) {
				if (componentToCheck == componentToTest) {
					continue;
				}
				rootNode = hasConnection(componentToCheck, componentToTest,
						edges);
				if (rootNode != null) {
					rootComponent = componentToCheck;
					rootedComponents.remove(componentToTest);
					connectedComponents.put(componentToTest, null);
					break;
				}

			}
			if (rootNode != null) {
				if (!connectedComponents.containsKey(rootComponent)
						|| connectedComponents.get(rootComponent).root == null) {
					rootedComponents.put(rootComponent, new ComponentWithRoot(
							rootComponent, rootNode));
				}
			}
		}
		Map<Node, AdjacencyList> rootedAdjacencyList = new HashMap<Node, AdjacencyList>();
		for (ComponentWithRoot componentWithRoot : rootedComponents.values()) {
			Node root = componentWithRoot.getRoot();
			if (root == null) {
				for (Node node : componentWithRoot.component) {
					root = node;
					break;
				}
			}
			Set<Edge> newAdjacencySet = new HashSet<Edge>();
			addAdjacency(root, newAdjacencySet, edges);
			AdjacencyList newAdjacencyList = new AdjacencyList();
			for (Edge edge : newAdjacencySet) {
				newAdjacencyList.addEdge(edge);
			}
			rootedAdjacencyList.put(root, newAdjacencyList);
		}
		return rootedAdjacencyList;
	}

	private static void addAdjacency(Node node, Set<Edge> newAdjacencySet,
			AdjacencyList edges) {
		if (edges.getAdjacent(node) == null)
			return;
		for (Edge edge : edges.getAdjacent(node)) {
			if (!newAdjacencySet.contains(edge)) {
				newAdjacencySet.add(edge);
				addAdjacency(edge.getFrom(), newAdjacencySet, edges);
				addAdjacency(edge.getTo(), newAdjacencySet, edges);
			}
		}
	}

	private static Node hasConnection(Collection<Node> componentSource,
			Collection<Node> componentTarget, AdjacencyList edges) {

		Set<Node> toNodes = new HashSet<Node>();
		toNodes.addAll(componentTarget);
		for (Node v : componentSource) {
			ArrayList<Edge> adjacent = edges.getAdjacent(v);
			if (adjacent != null) {
				for (Edge e : adjacent)
					if (toNodes.contains(e.getTo()))
						return e.getFrom();
			}
		}
		return null;
	}

	public Node getRoot() {
		return root;
	}

	public void setRoot(Node root) {
		this.root = root;
	}

	public AdjacencyList getBranch() {
		return branch;
	}

	public void setBranch(AdjacencyList branch) {
		this.branch = branch;
	}

}
