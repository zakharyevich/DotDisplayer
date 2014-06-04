package org.edmonds;
//
//  Node.java
//  
//
//  Created by Andre Altmann on 1/14/13.
//  source code taken from:
//  http://algowiki.net/wiki/index.php?title=Node 
//

public class Node implements Comparable<Node> {

	final int name;
	boolean visited = false; // used for Kosaraju's algorithm and Edmonds's
								// algorithm
	int lowlink = -1; // used for Tarjan's algorithm
	int index = -1; // used for Tarjan's algorithm
	private String text;

	public Node(final int argName) {
		name = argName;
	}
	public Node(final int argName, final String text) {
		this.name = argName;
		this.text = text;
	}

	// not even used in Edmonds' alg
	public int compareTo(final Node argNode) {
		return argNode == this ? 0 : -1;
	}

	public String toString() {
		return name + ": visited-" + visited + ", lowlink-" + lowlink
				+ ", index" + index;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
}
