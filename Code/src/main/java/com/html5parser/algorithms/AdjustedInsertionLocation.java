package com.html5parser.algorithms;

import org.w3c.dom.Node;

/**
 * Provides a location to insert a new Node.
 * 
 * @author JoséArmando
 *
 */
public class AdjustedInsertionLocation {
	private Node parent = null;
	/**
	 * If the insertion is before a node the next variable is the reference to
	 * insert before. Otherwise is null.
	 */
	private Node referenceNode = null;

	public AdjustedInsertionLocation(Node parent, Node referenceNode) {
		this.parent = parent;
		this.referenceNode = referenceNode;
	}

	public void insertElement(Node element) {
		parent.insertBefore(element, referenceNode);
	}

	public Node getParent() {
		return parent;
	}

	public void setParent(Node parent) {
		this.parent = parent;
	}

	public Node getReferenceNode() {
		return referenceNode;
	}

	public void setReferenceNode(Node referenceNode) {
		this.referenceNode = referenceNode;
	}

}
