/*
 * Author: John Massy-Greene(z3215661)
 * Date: 29/4/2016
 * Program Description: This program copies, merges and prints AVLTree using Java Swing. 
 * 						The copy function create a new tree with the same structure as the old tree and runs in O(n) time 
 * 						The merge function combines two trees into a new tree and runs in O(n+m) time where n is the
 * 						the size of the 1 tree and and m is the size of the other tree.
 *  
 *  Analysis Notes: In the following analysis primitive operations are considered to be the following:
 *                  function calls, comparisons, assignments, calculation and accessing a class's variable. 
 */
package net.datastructures;
import java.util.Comparator;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.ScrollPaneConstants;
import javax.swing.JScrollPane;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
public class ExtendedAVLTree<K,V> extends AVLTree<K,V> {
	
	//get the size of the screen for the swing printing application
	//reduce the size of the width and height by 100 pixels so it doesn't
	//cover the entire screen
	static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	static int screenWidth = screenSize.width-100;
	static int screenHeight = screenSize.height-100;
	
/*
 * Analysis of clone:
 * 
 * 11 Primitive Operations
 * 11+2 for creating a new node(+2 for accessing the element of the root of the tree)
 * 81N+2 - Please see the analysis of constructRestOfTree
 * F(N) = 81N + 26 = O(N)  
 * 
 */
	
	public static<K,V> AVLTree<K,V> clone(AVLTree<K,V> tree) {
		Comparator<K> compFunc = tree.C;
		AVLTree<K,V> clone = new AVLTree<K,V>(compFunc);
//create a new root node with the element of the node supplied by tree and then set the root of the clone tree
//and the size
		AVLNode<K,V> cRoot = new AVLNode<K,V>(tree.root.element(),null,null,null);
		clone.root = cRoot;
		
		//BTPosition<Entry<K,V>> oRoot = tree.root;
//using the roots go through the rest of the tree to construct the other nodes
//because we're constructing the tree manually we need to copy the size and number of entries from the old tree
		constructRestOfTree(cRoot, tree.root, clone);
		clone.size = tree.size;
		clone.numEntries = tree.numEntries;
		
		return clone;
	}
	
/*
 * Analysis of constructRestOfTree:
 * 
 * This function precisely goes through each element of the tree only once. But it will have different complexity for internal nodes vs external nodes
 * N = I + E (N = number of nodes in the tree, I = Internal Nodes, E = External nodes)
 * 
 * For each internal node there 79 primitive operations:
 *  -22 for setting up both left and right child's AVLNode
 *  -24 for setting the left and right children's relationship and the calling of the function
 *  -33 for setting the height of each node
 *  
 * For each external node there are only 2 operations:
 * - 1 for accessing the element and 1 for comparing its value
 * 
 * So the number of primitive operations for N nodes in a tree is:
 * N = 79I + 2E
 * 
 * The number of external nodes = I+1
 * So N = 79I + 2(I+1)
 *      = 81I + 2
 *      
 * so this function has a complexity of 81N + 2
 * 
 */
	private static<K,V> void constructRestOfTree(BTPosition<Entry<K,V>> copy, BTPosition<Entry<K,V>> orig, AVLTree<K,V> tree) {

//If the node to left or right is equal to null it means we have reached an external node
//thus we do not need to go further.
//set the elements of the left/right node and then set the parent and left/right references
//of each node
		if(orig.getLeft()!= null) {
			AVLNode<K,V> left = new AVLNode<K,V>(null,null,null,null);
			left.setElement(orig.getLeft().element());
			left.setParent(copy);
			copy.setLeft(left);
			constructRestOfTree(left, orig.getLeft(),tree);
			
		} 
		
		if(orig.getRight() !=null) {
			AVLNode<K,V> right = new AVLNode<K,V>(null,null,null,null);
			right.setElement(orig.getRight().element());
			right.setParent(copy);
			copy.setRight(right);
			constructRestOfTree(right, orig.getRight(),tree);
		}
//for each node we visit we need to set their height. If we do not set their
//height then if insert new elements into the clone tree, the tree will not know how to
//balance itself properly
		if(copy.element()!=null) {
			tree.setHeight(copy);
		}
		
	}
//function designed to take two trees and merge them to make one tree
/*
 * Analysis of merge
 * f(n+m) = 108(n+m) + 12n + 12m + 44 = O(n+m)
 *
 * let tree1 have size n
 * let tree2 have size m
 * 
 * 34 primitive operations come from:
 * 
 * 2 for getting the comparator function
 * 11 for creating the new AVL tree with the comparator
 * 10 for creating the 2 custom arrays
 * 11 for creating the custom and calculating the new amount of entries
 * 5 - accessing/assignment and calculation of the merged trees numentries 
 * 5 - accessing/assignment and calculation of the merged tree size
 * 
 * 12n comes from calling the function getInOrderList on tree1 with size n
 * 12m comes from calling the same function on tree2 with size m
 * 26(n+m) comes from calling the function mergeInOrderList on both the tree1 and tree2 arrays
 * 49(n+m) comes from calling the function listToTree on the merged list(which has a size of n+m)
 * 33(n+m) comes from calling the function finalSettingOfHeight on the merge tree(which has a size of n+m)
 * 
 * for further analysis of the methods used in merge please see their analysis sections.
 */
	public static<K,V> AVLTree<K,V> merge(AVLTree<K,V> tree1, AVLTree<K,V> tree2) {
		Comparator<K> comp = tree1.C;
		AVLTree<K,V> merge = new AVLTree<K,V>(comp);
		CustomArray<Entry<K,V>> tree1Nodes = new CustomArray<Entry<K,V>>(tree1.numEntries);
		CustomArray<Entry<K,V>> tree2Nodes = new CustomArray<Entry<K,V>>(tree2.numEntries);
		CustomArray<Entry<K,V>> mergeNodes = new CustomArray<Entry<K,V>>(tree1.numEntries+tree2.numEntries);
//programmer doesn't know how to compare the elements of the tree so we need to use the comparator function used by the tree
//get the elements of each tree in non-descending order and store them in our custom array
		getInOrderList(tree1Nodes, tree1.root, 0);
		getInOrderList(tree2Nodes, tree2.root, 0);
//now we create a new array which is the size of both both arrays combined and fill it with the elements of both arrays.
		mergeInOrderList(tree1Nodes, tree2Nodes, mergeNodes, comp);
//use the in-order array to create an actual tree. Going in-order traversal to post order traversal.
		merge.root = listToTree(mergeNodes, 0, (mergeNodes.size()-1));
//finally need to set the height of each the nodes in the tree so it can properly balance itself later on.
		finalSettingOfHeight(merge, merge.root);
		merge.numEntries = tree1.numEntries+tree2.numEntries;
		merge.size = (merge.numEntries*2)+1;
		return merge;
	}
	
//For an inorder list of the elements the left child will be visited first, followed by the node and then the right child
//use the i variable to keep track of how many elements the program has seen and insert it into the proper place in the array.
/*
 * Analysis of getInOrder List
 * 
 * This function is approximately f(N) = 12N
 * 
 * 6N - for determining if the element is null and calling the function 
 * 5N - for putting the element in the array
 * 1N - to return the value of i
 */
	private static<K,V> int getInOrderList(CustomArray<Entry<K,V>> list, BTPosition<Entry<K,V>> current, int i) {
		if(current.getLeft().element()!=null) {
			i = getInOrderList(list, current.getLeft(), i);
		}
		list.setEle(i, current.element());
		i = i+1;
		if(current.getRight().element()!= null) {
			i = getInOrderList(list, current.getRight(), i);
		}
		return i;
	}
	
/*
 * Analysis of mergeInOrderList:
 *
 * This function takes in two list of size n and m
 * approximately f(n+m) = 26(n+m)
 * 
 * let n+m=j 
 * 3j - for the assignment of the array indexes
 * 5j - for the while condition 
 * 11j- for using the comparison function and setting the element in the array
 * 7j - for assigning the remaining values to array
 */
	private static<K,V> void mergeInOrderList(CustomArray<Entry<K,V>> list1, CustomArray<Entry<K,V>> list2, CustomArray<Entry<K,V>> list3, Comparator <K> C) {
//the following numbers are the array indexes of the first, second and result arrays respectively
		int i1 = 0;
		int i2 = 0;
		int iR = 0;
		int comp;
//whenever the smaller arrays limit is reached stop the comparison		
		while((i1 < list1.size()) && (i2< list2.size())) {
			//using the tree comparator attribute. if the comp number is less or equal to 0 then
			//list1's element is smaller. Else the list2's element is smaller. Insert the smallest element first
			comp = C.compare(list1.getEle(i1).getKey(), list2.getEle(i2).getKey());
			if(comp <= 0) {
				list3.setEle(iR, list1.getEle(i1));
				i1++;
			} else {
				list3.setEle(iR, list2.getEle(i2));
				i2++;
			}
			iR++;
		}
		//we might be left with elements from the larger array. So just insert them at the end of the result list.
		while(i1 < list1.size()) {
			list3.setEle(iR, list1.getEle(i1));
			i1++;
			iR++;
		}
		while(i2 < list2.size()) {
			list3.setEle(iR, list2.getEle(i2));
			i2++;
			iR++;
		}
	}
	
//in order to create a manually balanced tree need to make each left and right the roughly the same size
//since we have an an array of n elements. Take the middle element, make it the current node and then
//then everything to the left of that element is the left subtree and everything to the right becomes the right subtree.
//the root of the left and right subtrees will be the middle of the remaining elements. Recursively repeat this process
//until one element is left and this becomes a internal node with two external children.
/*
 * Analysis of listToTree:
 * This function is approximately F(N) = 49N 
 * 
 * Despite the recursive nature of the function it hits each element of the array exactly once.
 * For each element the amount of operations is roughly as follows:
 *  4N - for calculation of the current node
 *  13N - for the creation of the AVLNode
 *  22N - for the creation of the left and right children
 *  4N - for setting the left and right child's parent
 *  4N - for setting the parents left and right children
 *  2N - for calling the function on the value and returning the node
 */
	private static<K,V> AVLNode<K,V> listToTree(CustomArray<Entry<K,V>> list, int start, int end) {
		int current = (start+((end-start)/2));
		AVLNode<K,V> node = new AVLNode<K,V>(list.getEle(current),null,null,null);
		AVLNode<K,V> left = new AVLNode<K,V>(null,null,null,null);
		AVLNode<K,V> right = new AVLNode<K,V>(null,null,null,null);
		//when the start equals the end it means you've reached an internal node that only has
		//external nodes as children
		if(start == end) {
			left.setParent(node);
			right.setParent(node);
			
			node.setLeft(left);
			node.setRight(right);
			return node;
		}
		//if current is equal to start it means your at the left-most branch of a particular subtree
		//and there are no more elements in the left hand-side to process
		if(current != start) {
			left = listToTree(list, start, current-1);
			node.setLeft(left);
			left.setParent(node);
		} else {
			//this process slightly skews the tree to the right so there maybe more internal nodes
			//with right subtrees and left subtrees that are just external nodes.
			left.setParent(node);
			node.setLeft(left);
		}
		right = listToTree(list, current+1, end);
		right.setParent(node);
		node.setRight(right);
		return node;
	}

//an auxiliary function for the merge function which does a post-order traversal in order to set
//the height of node in the tree.
/*
 * Analysis of finalSettingOfHeight:
 * This function is approximately F(N) = 33N. 
 * 
 * Setting the height of a node takes 33 operations(see earlier analysis)
 */
	public static<K,V> void finalSettingOfHeight(AVLTree<K,V> tree, BTPosition<Entry<K,V>> node) {
		if(node.getLeft().element()!=null) {
			finalSettingOfHeight(tree, node.getLeft());
		}
		if(node.getRight().element()!=null) {
			finalSettingOfHeight(tree, node.getRight());
		}
		tree.setHeight(node);
	}
	//the base function for the print function
	public static<K,V> void print(AVLTree<K,V> tree) {
		TreeWindow window = new TreeWindow();
		//make sure that we don't try to print an empty tree. We can display a screen but it will be empty like the tree.
		if(tree!=null) {
			if(tree.numEntries>0) {
				Canvas<K,V> trees = new Canvas<K,V>(tree);
			//the scrollpane class is used so that if a tree gets to big to fit on the screen then
			//the user can scroll to the right and down to view the each part of the tree.
				JScrollPane scroll = new JScrollPane(trees,ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, 
											 ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
				window.add(scroll);
			}
		}
			window.setVisible(true);
		
	}
	
	//class for the top level container of the swing graphics
	//used to contain the drawing of the AVLTree
	private static class TreeWindow extends JFrame {

		private static final long serialVersionUID = 1L;

		public TreeWindow() {
			initUI();
		}
		
		private void initUI() {
			setTitle("Assignment 2 - Drawing AVLTrees");
			//set the size relative to the users computer screen size and set it in the middle of the screen
			setSize(screenWidth,screenHeight);
			setLocationRelativeTo(null);
			//The user may want to display multiple trees at once, so when you close one frame
			//don't close all the others at the same time
			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			//because the container will be scrollable, don't allow resizing. Potentially could cause
			//graphics glitches.
			setResizable(false);
			
		}
	}
	
	public static class Canvas<K,V> extends JPanel {
		
		private static final long serialVersionUID = -8436502373026255497L;
		
		private AVLTree<K,V> aTree;
		private int width;
		private int height;
		private int nodes;
		private int treeHeight;
		private int ySpace;
		//xoff is the spacing for between nodes as well as making sure a node isn't printed directly on the side of the screenm
		private int xoff = 40;
		//readable font for string representation of the tree's keys
		public Font font = new Font("Times New Roman", Font.BOLD, 20);
		
		//canvas constructor takes the tree has input and uses it to set the appropriate dimensions for printing
		//it along the x and y axis'
		public Canvas(AVLTree<K,V> tree) {
			aTree = tree;
			nodes = tree.size+1;
			treeHeight = tree.height(tree.root())+1;
			//decent size for visibility of the trees nodes
			width = 80;
			height = 50;
			//make sure the nodes aren't placed too close to each other along the y-axis
			ySpace = (height*2);
		
		}
		
	    public Dimension getPreferredSize() {
	    	//The actual size of the container will depend on the size of the tree
	    	//so if the tree has a large depth that goes off the current screen size the container will have
	    	//the appropriate size. This means we can easily scroll
	        return new Dimension((nodes*width),ySpace*(treeHeight+1));
	    }
	    //overriding the super method for painting a tree
		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.setFont(font);
			doDrawing(g);
		}
		
		public void doDrawing(Graphics g) {
			
			BTPosition<Entry<K,V>> node = aTree.root;
			//the graphicNode is actually a dummy data structure used to help print the tree structure
			//so each node has its own place along the x-axis. This helps make sure we never have lines
			//crossing. The dummy node will increment its position variable for each node that is visited
			//Each node then uses this position to display its self at a set position on the x-axis
			GraphicNode pos = new GraphicNode();
			drawAVLTree(node, pos, 0, g);
		}
	
		private GraphicNode drawAVLTree(BTPosition<Entry<K,V>> node, GraphicNode pos, int height, Graphics g) {
			GraphicNode left = null;
			GraphicNode current = null;
			GraphicNode right = null;
//the tree does an inorder traversal to print each node
			if(node.getLeft()!=null) {
				left = drawAVLTree(node.getLeft(), pos, height+1, g);
			}
			
			if(node.element() == null) {
				current = new Rectangle(height,pos.accumulatePosition,g);
			} else {
				//Since the key may not be a string, we need to get the string representation of it.
				current = new Circle(height,pos.accumulatePosition, node.element().getKey().toString(), g);
			}
			pos.accumulatePosition+=1;
//Once we have found the left and right subtrees of a particular node. We can then draw lines to those particular subtrees
//using the x and y coordinates they have stored. This results in a clean drawing.
//Lines are drawn in a way that the line will be drawn to top of the left/right child. Also depending on whether its a left or right child
//we want the line to be positioned to either the left or the right of the node
			if(node.getRight()!=null) {
				right = drawAVLTree(node.getRight(), pos, height+1, g);
				Line rLine = new Line((current.x+current.w),(current.y+(current.h/2)),right.x+(right.w/2),right.y,g);
			}
			if(node.getLeft()!=null){
				Line lLine = new Line(current.x,(current.y+(current.h/2)),left.x+(left.w/2),left.y,g);
			}
			return current;
			
		}
//An auxiliary base class for the nodes of the tree. Each node can either be a rectangle or circle shape. Since the
//the parent of a node needs the x and y coordinates to draw a line to it we keep track of the nodes x,y position,
//the width and height, the position along the x-axis as well as its string representation of the key
		private class GraphicNode {
			public int x;
			public int y;
			public int w;
			public int h;
			public int position;
			public int accumulatePosition;
			public String key;
			
			public GraphicNode() {
				this.x = 0;
				this.y = 0;
				this.w = 0;
				this.h = 0;
				this.position = 0;
				//this constructor(especially the accumulatePosition variable) 
				//is basically used for the dummy array 
				this.accumulatePosition = -1;
				this.key = null;
			}
			
		}
//the graphic representation for the internal nodes of the tree
		private class Circle extends GraphicNode {
		
		    public Circle(int height, int position, String key, Graphics g) {
		    	this.position = position+1;
		    	//the x-coordinate is a function of the width, the nodes position
		    	//in terms of an inorder traversal and the small space buffer which is the xoff position
		    	this.x = (Canvas.this.width*this.position)+Canvas.this.xoff;
		    	//the y-coordinate is a function of the ySpace(height of the tree * 2) times the height of the
		    	//node in relation to its position in tree. Add to this the x-off which makes sure the top node
		    	//isn't printed at the absolute top position of the screen.
		    	this.y = (Canvas.this.ySpace*height)+Canvas.this.xoff;
		    	this.w = 80;
		    	this.h = w;
		    	this.key = key;
		    	this.paintCircle(g);
		    }
		
		    public void paintCircle(Graphics g){
		        g.setColor(Color.BLUE);
		        g.drawOval(x,y,w,h);
		        g.setColor(Color.BLUE);
		        g.fillOval(x,y,w,h);
		        //set the color of the text to be different than the node
		        //and draw it in the middle of the cirle
		        g.setColor(Color.BLACK);
		        g.drawString(this.key, x+(w/4), y+(w/2));
		    }
		}

//Graphical representation of the external nodes of the tree
		private class Rectangle extends GraphicNode {

			public Rectangle(int height, int position, Graphics g) {
		    	this.position = position+1;
		    	//the x and y coordinates are handled exactly the same as the internal nodes graphic representation.
		    	this.x = (Canvas.this.width*this.position)+Canvas.this.xoff;
		    	this.y = (Canvas.this.ySpace*height)+Canvas.this.xoff;
		    	this.w = 80;
		    	this.h = 50;
		    	this.paintRectangle(g);
			}
			
			public void paintRectangle(Graphics g) {
		        g.setColor(Color.BLACK);
		        g.drawRect(x,y,width,height);
		        g.setColor(Color.BLACK);
		        g.fillRect(x,y,width,height);
			}
		}
//graphic class that is used to connect left and right subtrees to a node in the tree
		private class Line extends GraphicNode {
			public int x1;
			public int x2;
			public int y1;
			public int y2;
//the constructor takes the two points(the x and y coordinates of the two nodes) in order
//to draw a line in a particular direction and appropriate length
			public Line(int p1, int h1, int p2, int h2, Graphics g) {
				this.x1 = p1;
				this.y1 = h1;
				this.x2 = p2;
				this.y2 = h2;
				this.paintLine(g);

			}
			public void paintLine(Graphics g) {
				g.setColor(Color.BLACK);
				g.drawLine(x1, y1, x2, y2);
			}
		}
	}
	
//this is an auxillery function used by the merge function in order to store the elements
//of the tree for easy access. 
	public static class CustomArray<E> {
		public E[] anArray;
		public int size;
		
		public CustomArray(int size) {
			anArray = (E[]) new Object[size];
		}
		
		public int size() {
			return this.anArray.length;
		}
		public void setEle(int i, E current) {
			this.anArray[i] = current;
		}
		public E getEle(int i) {
			return this.anArray[i];
		}
		
	}

}
