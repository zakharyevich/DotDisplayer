package org.edmonds;
//
//  SCC.java
//  
//
//  Created by Andre Altmann on 1/17/13.
//
//  abstract class for finding Strongly Connected Components in a Graph

import java.util.*;


public abstract class SCC {

    public abstract List<Collection<Node>> runSCCsearch(AdjacencyList _edges);

}
