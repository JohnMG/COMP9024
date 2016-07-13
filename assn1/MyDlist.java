/*
Author: John Massy-Greene(z3215661)
Date: 29/03/16
Program Description: MyDlist is a subclass of DList which implements its own functions.
					 Functions unique to MyDlist include cloneList(which clones the list given),
					 union(which produces a union of two linked list sets) and intersection(which gives the
					 intersection of two linked list sets)

*/
import java.util.*;
import java.io.*;

public class MyDlist extends DList {

//Standard constructor that calls the super-classes constructor to
//create an empty doubly linked list.
	public MyDlist() {
		super();
	}
	
//Second constructor which uses a string to decide how to build the doubly linked list
//If the string is equal to "stdin" then the linked list will build itself with the input
//gained from Standard Input(i.e. user defined console input)
//If the string is not equal to stdin then the string must be a file and the doubly linked list
//will contain the words in that file.
	public MyDlist(String f) {
		if(f.equals("stdin")) {
			readFromStdin();
		} else {
			readFromFile(f);
		}
	}

//printList gets the header of the list and goes through each Node in the list
//and prints the element of that node on its own line. The function will keep going
//till it reaches the tail of the list
	public void printList() {
		if(this.size > 0) {
			DNode node = this.header.next;
			while(node!=this.trailer) {
				System.out.println(node.element);
				node = node.next;
			}
		}
	}
//a static method that creates and returns a clone of the MyDlist given to it.
	public static MyDlist cloneList(MyDlist u) {
		//create an empty list
		MyDlist clone = new MyDlist();
		//get the node that is after the header sentinel
		DNode uNode = u.header.next;
		
		//if the size of the list given is 0 then don't need to go through the elements.
		if(u.size()!=0) {
			//while node isn't yet the trailer create a new DNode with the same element
			//as the current node. Then pass this DNode to the tail of the clone list.
			while(uNode != u.trailer) {
				DNode newClone = new DNode(uNode.element, null, null);
				clone.addLast(newClone);
				uNode = uNode.next;
			}
		}
		return clone;
	}
/*
 * Analysis of union = 6 + 5N + 5N + (8N^2 + 13N + 7) + 1
 *                   = 8N^2 + 23N + 14
 *                   
 * F(N) = N^2
 */
	public static MyDlist union(MyDlist u, MyDlist v) {
		//create a new empty MyDlist named union which will be the union of u and v
		MyDlist aUnion = new MyDlist();
		//create nodes to refer to the first Node of u and v
		DNode uNode = u.header.next;
		DNode vNode = v.header.next;
		//newNode will be used push Nodes onto the union
		DNode newNode = null;
		//Analysis = 6
		//4 for creating new elements and 2 for following references
		
		//get each Node of MyDlist u, create an new DNode with the same element of current uNode
		//then add this to the tail of the union.
		while(uNode!=u.trailer) {
			newNode = new DNode(uNode.getElement(),null, null);
			aUnion.addLast(newNode);
			uNode = uNode.next;
		}
		//Analysis = 5N
		//2 for creating a newNode and getting the node element
		//2 for assigning the next node of newNode to newNode
		//1 for calling method to add the newNode to the list
		
		//the exact same code as above except it uses MyDlist v
		while(vNode!=v.trailer) {
			newNode = new DNode(vNode.getElement(),null, null);
			aUnion.addLast(newNode);
			vNode = vNode.next;
		}
		//Analysis = 5N
		//Same as above
		
		//strip the duplicates out of the union. Because this is a set.
		//Analysis = N^2 (see the analsyis below).
		stripDuplicates(aUnion);
		
		return aUnion;
		//Analysis = 1.
	}
	
/*
 * Analsysis of stripDuplicates = 7 + 5N + 16(N(N+1)/2).
 *                              = (16N^2)/2 + 16N/2 + 5N + 7
 *                              = 8N^2 + 13N + 7  
 * 
 *	F(N) = O(n^2).
 */
	private static void stripDuplicates(MyDlist u) {
		//if the size of the new list is 0 then there cant be any duplicate elements.
		if(u.size() != 0 ) {
			//set the uNode to first node of the list u
			DNode uNode = u.header.next;
			DNode uNodeNext = null;
			DNode previous = null;
			DNode next = null;
			//a node created especially to make sure nodes are handled by the garbage collector.
			DNode garbage = null;
			//Analysis = 7. If conditional, creation of new nodes and following a reference
		
			//go through the list element by element
			while(uNode != u.trailer) {
				uNodeNext = uNode.next;
			//Analsis = 2N. Following a reference and assignment
				
				//however we only need to compare the all elements after the current element
				while(uNodeNext != u.trailer) {
					//if the two nodes contain the same element then get rid of the latter node.
					if(uNode.getElement().equals(uNodeNext.getElement())) {
						//get the previous and the next node to point to each other
						previous = uNodeNext.prev;
						next = uNodeNext.next;
						previous.setNext(next);
						next.setPrev(previous);
						//decrease the size of the overall list
						u.size--;
						//setup for garbage collection
						garbage = uNodeNext;
					}
					//Analysis = 12M. M = N(N+1)/2 because each time the number of comparisons get smaller by 1
					//3 for the if statement(following two references and comparison)
					//4 setting of references for the previous and next nodes
					//2 for actually setting the references
					//2 for the decreasing the size
					//1 for assignment
					uNodeNext = uNodeNext.next;
					//if there's an garbage element dereference its pointers
					//thus the it won't be used anywhere else in the program and can
					//be deleted.
					if(garbage != null) {
						garbage.setPrev(null);
						garbage.setNext(null);
						garbage = null;
					}
					//Analysis = 4((N(N+1)/2))
					//1 for if condition and 3 for the loop 
				}
				uNode = uNode.next;
				//Analysis = 2N - assignment and reference.
				
			}
		}
	}

/*
 * Analysis = (8(N^2) + 4N + 4) + 7
 * 			= 8N^2 + 4N + 11
 * 
 * F(N) = O(N^2)
 * 	
 */
	public static MyDlist intersection(MyDlist u, MyDlist v) {
		MyDlist intersect = null; 
		//Analysis = 1 
		
		//function finds the smaller list to perform the intersection on because
		//the intersection can only have as many elements as the smaller list
		if(u.size() <= v.size()) {
			intersect = actualIntersection(u,v);
		} else {
			intersect = actualIntersection(v,u);
		}
		return intersect;
		//Analysis = 6 + Analysis of actualIntersection. 3 for the refences and comparison of the if conditional
		//2 for method call and assigning the return value to intersect and 1 for the final return value.
	}
	

/*
 * Analysis = 8(N^2) + 4N + 4
 */
	private static MyDlist actualIntersection(MyDlist s, MyDlist b) {
		MyDlist intersect = new MyDlist();
		DNode sNode = s.header.next;
		DNode bNode = null;
		//Analysis = 4. 3 for assignments and 1 for reference
		while(sNode != s.trailer) {
			//for each node of the smaller list go through the bigger list
			//and compare each element to see if they're the same.
			bNode = b.header.next;
			while(bNode != b.trailer) {
				//if the elements are the same then create a new DNode
				//and add it to the tail of the new MyDlist
				if(sNode.getElement().equals(bNode.getElement())) {
					DNode newNode = new DNode(sNode.getElement(), null, null);
					intersect.addLast(newNode);
				}
				bNode = bNode.next;
				//Analysis of Inner Loop = 8(N^2) - N^2 each element gets compared N times.
				//                         3 for the if loop(2 references and a comparison)
				//                         2 for the newNode(assignment and reference to element
				//                         1 calling a method to add node to new list
				//                         2 reference to bNode next and assignment. 
			}
			sNode = sNode.next;
			//Analysis of Outer Loop = 4N. 2 for references and 2 for assignment
		}
		
		return intersect;
		//Analysis = 1
	}
	
	private void readFromStdin() {
		String input = null;
		Scanner sc = new Scanner(new ShieldInputStream(System.in));
//if the line given to standard input is an empty line then the user has no more
//input to give
		while((input=sc.nextLine()).isEmpty()==false) {
//create a new node with the users input and put it at the tail of the new MyDlist.
			DNode newNode = new DNode(input, null, null);
			this.addLast(newNode);
		}
		sc.close();
	}
	
	private void readFromFile(String file) {
		String[] elements;
		String input = null;
		BufferedReader inputFile = null;
	
		try {
			//get the text file
			inputFile = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e) {
			System.out.println("Cant find this file");
			return;
		}
		try {
			//read each line until there are no more lines
			while((input = inputFile.readLine())!=null){
				//split up the lines into words and store the words in an array
				elements = input.split(" ");
				//for each word in the string array create a new Node and
				//put it at the end of the list.
				for(int x=0; x<elements.length; x++){
					DNode newNode = new DNode(elements[x],null,null);
					this.addLast(newNode);
				}
			}
		//Close the file and handle exceptions.
		} catch (IOException e) {
			System.out.println("Can't read");
			return;
		}
		try {
			inputFile.close();
		} catch (IOException e) {
			System.out.println("cant close");
			return;
		}
	}
	
//This is a decorator class that is designed to take in System.in
//but makes sure that it doesn't close. This is being implemented
//because closing a scanner will result in System.in being closed
//and once closed it cannot be reopened. So each time MyDlist is called
//with stdin as the option I can close the scanner at the end of each constructor
//without closing System.in. This will help with garbage collection and memory
//leaks
	private class ShieldInputStream extends InputStream {
		private InputStream inputStream;
		
		private ShieldInputStream(InputStream reader) {
			this.inputStream = reader;
		}
	   @Override
	    public int read() throws IOException {
	        return this.inputStream.read();
	    }

	    @Override
	    public int read(byte[] b) throws IOException {
	        return this.inputStream.read(b);
	    }

	    @Override
	    public int read(byte[] b, int off, int len) throws IOException {
	        return this.inputStream.read(b, off, len);
	    }
	    @Override
		public void close() throws IOException {
			//nothing
		}
		
		
		
	}

}
