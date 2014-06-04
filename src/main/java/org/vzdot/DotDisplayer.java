package org.vzdot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;
import org.cesta.parsers.dot.DotLexer;
import org.cesta.parsers.dot.DotParser;
import org.cesta.parsers.dot.DotTree;
import org.edmonds.AdjacencyList;
import org.edmonds.Edge;
import org.edmonds.Node;

public class DotDisplayer {

	private static final String SUBGRAPH_ROOT = "SUBGRAPH_ROOT";
	private static final String EDGE_STRING = "+-->";
	private static final String NODE_STMT = "NODE_STMT";
	private static final String EDGE_STMT = "EDGE_STMT";
	private static final String STMT_LIST = "STMT_LIST";
	private Map<String, Node> nodeMap = new HashMap<String, Node>();
	private Integer nodeIndex = 0;
	private AdjacencyList adjacencyList = new AdjacencyList();

	public static void main(String args[]) throws Exception {

		String fileName = args[0];
		(new DotDisplayer()).print(fileName);

	}

	public void print(String fileName) throws IOException {
		DotDisplayer dotDisplayer = new DotDisplayer();
		try {
			dotDisplayer.extractEdges(fileName);
			Collection<RootedBranch> branches = RootedBranch
					.extractBranches(dotDisplayer.adjacencyList);
			dotDisplayer.printRootedBranch(branches);
		} catch (RecognitionException e) {
			e.printStackTrace();
		}
	}

	private Tree parseDot(String fileName) throws IOException,
			RecognitionException {
		DotLexer lex = new DotLexer(new ANTLRFileStream(fileName, "UTF8"));
		CommonTokenStream tokens = new CommonTokenStream(lex);
		DotParser parser = new DotParser(tokens);
		DotParser.graph_return r = parser.graph();
		CommonTreeNodeStream nodes = new CommonTreeNodeStream(r.getTree());
		DotTree walker = new DotTree(nodes);
		return walker.graph().getTree();

	}

	public void printRootedBranch(Collection<RootedBranch> branches) {
		for (RootedBranch branch : branches) {
			System.out.println(branch.getRoot().getText());
			printBranch(branch.getRoot(), branch, 1);
		}
	}

	private void printBranch(Node node, RootedBranch branch, int level) {
		int i = EDGE_STRING.length() * level;
		String shift = String.format("%1$" + i + "s", EDGE_STRING);
		ArrayList<Edge> adjacent = branch.getBranch().getAdjacent(node);
		if (adjacent == null)
			return;
		for (Edge ege : adjacent) {
			System.out.println(shift + ege.getTo().getText());
			printBranch(ege.getTo(), branch, level + 1);
		}

	}

	private void extractEdges(String fileName) throws IOException,
			RecognitionException {
		Tree graphRoot = parseDot(fileName);
		processGraph(graphRoot);
	}

	private void processGraph(Tree graphRoot) {
		Tree stmtLlist = getNodeByName(graphRoot, STMT_LIST);
		for (int i = 0; i < stmtLlist.getChildCount(); i++) {
			Tree stmt = stmtLlist.getChild(i);
			stmt.getType();
			if (EDGE_STMT.equals(stmt.toString())) {
				addEdge(stmt);
			} else if (SUBGRAPH_ROOT.equals(stmt.toString())) {
				processGraph(stmt);

			} else if (NODE_STMT.equals(stmt.toString())) {
				addNode(stmt);
			}
		}
	}

	private void addNode(Tree node) {
		getNode(node.toString());
	}

	private void addEdge(Tree parentNode) {
		Edge edge = null;
		while (parentNode != null) {
			Tree sourceNode = parentNode.getChild(0);
			if (sourceNode == null || sourceNode.getType() != 30) {
				return;
			}
			Tree dotEdge = parentNode.getChild(1);
			if (dotEdge == null || dotEdge.getType() != 19) {

				return;
			}
			Tree targetNode = dotEdge.getChild(0);
			if (targetNode.getType() != 30) {
				return;
			}
			edge = new Edge(getNode(sourceNode.toString()),
					getNode(targetNode.toString()), 1);
			adjacencyList.addEdge(edge);
			parentNode = dotEdge;
		}
		;

	}

	private Node getNode(String text) {
		Node node = nodeMap.get(text);
		if (node == null) {
			nodeIndex++;
			node = new Node(nodeIndex.intValue(), text);
			nodeMap.put(text, node);
		}
		return node;
	}

	private Tree getNodeByName(Tree tree, String TypeName) {
		for (int i = 0; i < tree.getChildCount(); i++) {
			Tree child = tree.getChild(i);
			if (TypeName.equals(child.toString())) {
				return child;
			}
		}
		return null;
	}

}