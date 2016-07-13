/*
 * Author: John Massy-Greene(z3215661)
 * Date: 3/6/2016
 * Program Description: This program has 3 primary functions:
 *                      1. Given a word, create a compressed compact suffix trie
 *                      2. Given a pattern, determine whether the pattern occurs as a substring within the word
 *                      3. Given two DNA sequences, find the longest common subsequences of the two sequences, print it to a given
 *                         file and also return the degree of similarity between the sequences.
 * 
 */

import java.io.*;
import java.util.regex.*;

public class CompressedSuffixTrie {

	public Node root;
	public char[] word;
	
/*
 * Analysis of CompresssedSuffixTrie:
 * f(n) = ((n-1(51(n-1)+51)/2) + 25n + 40 
 * O(n) = n^2
 * 
 * Note: This analysis is based on the simple case that string provided is of length n and all characters are the same
 *       This was the easiest base case to analyze as other cases have too many variables to analyse. Of course the best case
 *       is a String where all characters are unique in which case the complexity would just be O(n) because the splitchild doesn't
 *       need to be called.
 * 
 * Note2: Please see the analysis of getSequenceFromFile and splitChild to confirm their complexities       
 *   
 * (1) - basic operations before the for loop
 * 7 - creating a new root node
 * 25n+10 - getting the word from the file provided
 * 1 - if comparison
 * 4 - assigning values to word and end
 * 
 * (2) - for loop
 * (2a) - first character used in the word is 16
 * 2 - assinging a value to c from an element in an array
 * 5 - comparing the value from method getLink to null
 * 11 - creating a new node and putting it into the links of the rootNode
 * 
 * (2b) - characters 2..N have basic operations 55
 * 2 - assigning value to c from an element in an array
 * 5 - comparing the value from method getLink to null
 * 45 - see analysis of splitChild
 * 
 * For (2b) because a suffix tree demands that each time a character appears it have its own branch, this means that
 * for a string where each character is the same, each character is going to have to go through each previous inserted
 * character to find the appropriate depth to insert itself. Thus the amount of operations grow in proportion to
 * to the position of the character in the word. The amount of operations then become a summation function from 2..N of 55
 * 
 * (2b) thus becomes ((n-1)*(55(n-1)+55)/2) -> (3)
 * 
 * (1)+(3)+(2a) = ((n-1)*((55(n-1)+55)/2))+18+25n+22
 *              = ((n-1)*((55(n-1)+55)/2))+25n+40
 * 
 */

//The constructor to build the a compressed suffix trie from a word that is found in the file f
	public CompressedSuffixTrie(String f) {
		int c;
		this.root = new Node();
		//get the word from the file f and build the compressed suffix trie from it
		char[] suffixWord = getSequenceFromFile(f);
		if(suffixWord!=null) {
			this.word = suffixWord;
			int end = word.length-1;
			//go from the last character to the first and build the suffix trie that way
			for(int i=word.length-1; i>=0; i--) {
				c = word[i];
				//if the character that is at the beginning of the suffix trie hasn't been seen then
				//automatically include it as a new child in the suffix
				if(root.getLinks(c) == null){
					root.putLinks(c, new Node(i,end));
				} else {
				//if the character has already been seen within the suffix trie then we need to split the node
				//that character currently is placed at.
					splitChild(word, root.getLinks(c), i , c, end);
				}
			}
		}
	}
	
/*
 * Analysis of getSequenceFromFile:
 * f(n) = 25n+14
 * 
 * Node: For the worst case this assumes that each letter of the DNA sequence is on its own line
 * 
 * (1) 
 * 7 - assigning of multiple values to variable until the first try statement
 * 
 * (2)
 * While loop = 20
 * 3 - calling the readline method, assigning the value to input file and comparing it to null
 * 2 - assigning a value to the matcher variable
 * 2 - calling find and comparing its value
 * 1 - for loop runs once because there's only one character on each line
 * 2 - accessing the character in a string and assigning it to the character
 * 3 - calling the iswhitespace method and comparing its value
 * 7 - 5 to add a character to the linked list and 2 to create a new node
 * 
 * (3) - 4
 * 2 - creating a new char array whose size is determined by the linkedlist size
 * 2 - assigning linked lists head to the Node current
 * 
 * (4)
 * for loop = 5
 * 3 - accessing array element and assigning it a value from current node
 * 2 - going to the next node
 * 
 * (5) - 3
 * 1 - putting the value of seq into result
 * 1 - closing the input
 * 1 - return result
 * 
 * (2) this runs n times because each element is on its own line.
 *     Thus is has a complexity = 20n 
 * (4) accessing each element in the linkedlist means it runs n times.
 *     Thus the complexity = 5n
 * 
 * Total complexity of the function is: (1)+(2)+(3)+(4)+(5) = 7+20n+4+5n+3 = 25n+14
 * 
 */
	
//this function opens a file, obtains the DNA sequence and returns it
	private static char[] getSequenceFromFile(String f) {
		char[] result = null;
		BufferedReader inputFile = null;
		//linked list is used to determine how many characters are in the file so
		//we can later assign the appropriate space to a character array
		LinkedList preSequence = new LinkedList();
		//DNA sequences are only made from the characters A,C,G and T. Spaces are present in the file
		String pattern = "^[ACGT\\s]*$";
		Pattern p = Pattern.compile(pattern);
		Matcher m = null;
		char c;
		String input;
		char[] seq;
		
		try {
			inputFile = new BufferedReader(new FileReader(f));
		} catch (FileNotFoundException e) {
			System.out.println("File f not found");
			return null;
		}
		
		try {
			//grab the line from the file
			while((input=inputFile.readLine())!=null) {
				//see if the line only contains the correct characters
				m = p.matcher(input);
				if(!m.find()) {
					System.out.println("File has non-DNA characters");
					inputFile.close();
					return null;
				}
				for(int i=0; i<input.length(); i++) {
					c = input.charAt(i);
					//if the character is whitespace then don't include it in the sequence
					if(!Character.isWhitespace(c)) {
						//add it to the linked list so we can determine the size later
						preSequence.add(new Node(c));
					}
				}
			}
			//the words size can finally be obtained from the linked list
			seq = new char[preSequence.size];
			Node current = preSequence.head;
			//the linked list is essentially a Stack or a LIFO structure
			//so grab the characters and put them in reverse order in the new character array
			for(int i = preSequence.size-1; i>=0; i--) {
				seq[i] = current.element;
				current = current.next;
			}
			result = seq;
			
				
		} catch (IOException e1) {
			System.out.println("Cannot read from file f. Cannot obtain sequence");
			return null;
		}
		
		try {
			inputFile.close();
		} catch (IOException e) {
			System.out.println("Cannot close the file. Cannot obtain sequence");
			return null;
		}
		//finally return the DNA character array 
		return result;
	}
/*
 * Analysis of splitChild:
 * f(n) = 45
 * 
 * Note: Full analysis of this function is provided in CompressedSuffixTrie. This analysis however gives the basic operations based on 
 * 		 the assumption is based on the case that the suffix being generated is based on a string of length n with all characters the same.
 * 		 With all characters the same the function on performs the operations in the first branch of the if statement.
 * 
 * 7 - for setting up the variables in the start of the function
 * 
 * while loop = 13 - this only runs once because each node in the suffix tree will only have one character
 * 6 - for the while loop conditions
 * 3 - 2 array element accesses and comparison
 * 4 - incrementing the i and index2 variables
 * 
 * 1 - if condition
 * 2 - assigning a value to c from the array
 * 5 - calling the get links function and assigning the value to child
 * 5 - assigning values to currents start and end variables
 * 1 - comparing child to null
 * 11 - calling putLinks(4) and creating a new node(7)   
 * 
 */
//This function is a helper function which is used in conjunction with the constructor
//if a character is already present in the suffix trie. Then split the node or the split the children
//of the node in order to create compact representation of the suffixes in the trie
	public void splitChild(char[] word, Node current, int index, int chara, int maxend) {
		int start = current.start;
		int end = current.end;
		int index2 = index;
		int i = start;
		boolean match = true;
		Node child;
		
		//determine the amount of characters within a previous generated suffix that the
		//new suffix matches. 
		while(i<=end && match) {
			if(word[i] == word[index2]){
				i++;
				index2++;
			} else {
				match = false;
			}
		}
		//if the new suffix matches current suffix or is longer
		//then either create a new child or find the child within the
		//current suffix to place the new suffix under
		if(i > end) {
			int c = word[index2];
			child = current.getLinks(c);
			current.start = index;
			current.end = index2-1;
			if(child == null) {
				current.putLinks(c, new Node(index2,maxend));
			} else {
				splitChild(word, current.getLinks(c),index2, c, maxend);
			}
		//if the current suffix is longer than the new suffix but some of the
		//characters match then split the current suffix into a parent and a child node
		//where the child node is the characters in the suffix that don't match the new suffix
		} else {
			int splitStart = i;
			int splitEnd = end;
			current.start = index;
			current.end = index2-1;
			int c1 = word[splitStart];
			int c2 = word[index2];
			current.putLinks(c1, new Node(splitStart, splitEnd));
			current.putLinks(c2, new Node(index2, maxend));
		}
		
	}
/*
 * Analysis of findString:
 * 
 * n = length of the pattern provided
 * f(n) = 14n+9 -> O(n)
 * 
 * Note: This analysis uses the assumption that in the worst case each character in the string is its own branch
 *       in the Suffix Trie. This means than in each run of the for loop the algorithm needs to find the next node
 *       for use in comparisons.
 * 
 * 
 * (1)
 * 6 - assigning values to variables before the for loop
 * 
 * (2) - for loop = 14
 * 2 - assigning value to c from an array eleement
 * 1 - if condition comparison
 * 5 - assigning current a value from getlinks method
 * 1 - if condition comparison
 * 4 - assigning value to cStart and cEnd
 * 3 - if conditional comparison of array elements
 * 1 - assign value to endposition
 * 2 - incrementing cStart
 * 
 * (3)
 * 3 - returning a value that needs to be calculated
 * 
 * (2) The for loop runs for n times so it becomes 14n => (4)
 * 
 * The combined operations form a complexity of:
 * (4)+(1)+(3) = 14n+6+3 = 14n+9
 * 
 */
//Function is used to determine if a substring is present within the suffix trie
	public int findString(String s){
		int c;
		char[] ss = s.toCharArray();
		Node current = this.root;
		int cStart = 0;
		int cEnd = -1;
		//endPosition will be the index of the character within the word that the substring stops at
		int endPosition = 0;
		
		for(int i=0; i<ss.length; i++) {
			c = ss[i];
			//if the starting index is more than the ending index then we have reached the end
			//of a suffix branch and thus need to find another branch to compare the substrings
			//characters to
			if(cStart > cEnd) {
				current = current.getLinks(c);
				if(current == null) {
					return -1;
				} else {
					cStart = current.start;
					cEnd = current.end;
				}
			}
			//if the characters within the word and substring match keep going
			if(ss[i] == this.word[cStart]) {
				endPosition = cStart;
				cStart++;
			} else {
				return -1;
			}
		}
		//the endPosition minus the length of the substring is the
		//starting position of the substring within the word
		return endPosition-ss.length+1;
	}
/*
 * n = size of word1
 * m = size of word2
 * 
 * f(n,m) = 15nm + 18(n+m) + 28n + 28m + 77  => O(nm)
 * 
 * Note: For deeper analysis of the functions used within similarityAnalyser please see their respective analysis's
 * 
 * 1 - assigning value to result
 * 25n+15 - getting a word from f1 and assigning it to word1
 * 25m+15 - getting a word from f2 and assigning it to word2
 * 3 - if comparison which requires 3 comparisons
 * 4 - assigning values to w1size and w2size from word1/2 length
 * 1 - creating a 2 dimensional matrix for the LCS
 * (3n+3m)+1 - calling the function initializeTheLCS
 * (15nm+4)+1 - calling the function populateMatrix
 * (18(n+m)+13)+2 - calling the function findSequence and assigning the returned value to word3
 * 1 - if comparison
 * 7 - calculating the degree of similarity between the two DNA sequences
 * 7+1 = 8 - calling function outputToFile
 * 1 - returning the degree of similarity between two DNA sequences
 * 
 */

//This function takes two DNA sequences and computes the longest common subsequence between them
//it then outputs the LCS to a provided output file. It also returns the degree of similarity between
//the DNA sequences
	public static float similarityAnalyser(String f1, String f2, String f3) {
		float result = 0;
		int w1size;
		int w2size;
		char[] word1;
		char[] word2;
		char[] word3;
		int[][] lcs;
		//get the DNA sequences from the two files
		word1 = getSequenceFromFile(f1);
		word2 = getSequenceFromFile(f2);
		if(word1==null || word2==null) {
			System.out.println("Either file1 or file2 do not contain a correct sequence.");
			return result;
		}
		
		w1size = word1.length;
		w2size = word2.length;
		lcs = new int[w1size+1][w2size+1];
		//populate the matrix with the initial values and then start comparing the the two sequences
		initialiseTheLCS(lcs,w1size, w2size);
		populateMatrix(lcs, word1, word2);
		//grab the LCS from the matrix table and return it to the word
		word3 = findSequence(lcs, word1, word2);
		if(word3!=null) {
			//the degree of similarity = length of the LCS/max(length of sequence1,length of sequence2)
			result = (float) word3.length/Math.max(word1.length, word2.length);
			outputToFile(f3, word3);
		}
		
		return result;
	}
/*
 * Analysis of outputToFile
 * f(n) = 8
 * 
 * 2 - assigning values to FileWriter out and file
 * 2 - if conditional
 * 1 - creating a new filewriter
 * 1 - writing the string to the file
 * 1 - closing the output stream
 * 
 */
//A small helper function which outputs the found LCS to an output file
	private static void outputToFile(String outputFile, char[] sequence) {
		FileWriter out = null;
		File file = new File(outputFile);
		
		//if the file exists make sure not append to the file but to rewrite it
		//else create a new file with the name provided
		try {
			if(file.exists()){
				out = new FileWriter(outputFile, false);
			} else {
				out = new FileWriter(outputFile);
			}
		} catch (IOException e) {
			System.out.println("Cannot create or find output file. Exiting.");
			return;
		}
		//write the LCS string to the file
		try {
			out.write(sequence);
		} catch (IOException e1) {
			System.out.println("Cannot write to the file. Exiting");
		}
		
		//close the input stream to avoid memory leaks.
		try {
			out.close();
		} catch (IOException e) {
			System.out.println("Cannot close the file. Exiting");
			return;
		}
	}
/*
 * Analysis of findSequence
 * n = size of w1
 * m = size of w2
 * f(n,m) = 18(n+m) + 13
 * 
 * (1) - 12
 * 4 - assigning values to w1size and w2size from the length of the arrays
 * 3 - assinging a value to seqLength from lcs matrix
 * 5 - assigning values to result,i,x,y variables
 * 
 * (2) while loop(using the highest cost branch) - 18
 * 3 - for the multiple conditionals
 * 5 - if conditional which compares two array elements which have to be calculated
 * 4 - assigning a value to a array element from another array element
 * 6 - decrementing x,y,z
 * 
 * (3) - 1
 * 1 - returning the result
 * 
 * (4)
 * Since the most amount of elements that can be compared in the while loop the sum of the size of word1(n) and the size of
 * word2(m) (2) goes from being 18 => 18(n+m)
 * 
 * f(n,m) = (1)+(4)+(3) = 12+18(n+m)+1 = 18(n+m) + 13
 */
//this method finds the actual longest common subsequence word which is built from
//LCS matrix
	private static char[] findSequence(int[][] lcs, char[] w1, char[] w2) {
		int w1size = w1.length;
		int w2size = w2.length;
		//the length of the LCS is found from using the absolute last value in the matrix
		int seqLength = lcs[w1size][w2size];

		char[] result = new char[seqLength];
		//i is used to store the LCS characters in the variable result
		//however it is used to go from the last character to the first
		int i = seqLength-1;
		int x = w1size;
		int y = w2size;
		
		if(seqLength>=0) {
			while(x>0 && y>0) {
				//if a matching character has been found then store the value in the result
				//and then go diagonally up the LCS
				if(w1[x-1] == w2[y-1]) {
					result[i] = w1[x-1];
					x--;
					y--;
					i--;
				} else if(lcs[x-1][y] > lcs[x][y-1]) {
						x--;
				} else {
						y--;
				}
			}
		} else {
			return null;
		}
		return result;
	}
/*
 * Analysis of initialiseTheLCS:
 * n = size of word1(w1)
 * m = size of word2(w2)
 * 
 * f(n,m) = 3n+3m
 * 
 * 3n - assigning values to along the w1 axis of the array n times
 * 3m - assigning values to along the w2 axis of the array n times
 */
	
//this is a small helper function that just initializes the matrix
//to make sure that the first characters compared have a value to
//increment
	private static void initialiseTheLCS(int[][] lcs, int w1, int w2) {
		for(int x=0; x<=w1; x++) {
			lcs[x][0] = 0;
		}
		for(int y=0; y<=w2; y++) {
			lcs[0][y] = 0;
		}
	}
	
/*
 * Analysis of populateMatrix:
 * n = size of w1
 * m = size of w2
 * 
 * f(n,m) =15nm + 4
 * 
 * (1)
 *  4 - assigning values to the w1size and w2size from the array length
 *  
 * (2) - 2 for loop basic operations(for the branch with the most costly operations) - 15
 * 5 - if conditional which compares 2 array elements which need to be calculated beforehand
 * 10 - assigning a value to lcs from the value returned by the max operation which compares two values from array elements
 * 
 * (3)
 * The outer loop runs n times and the inner loop runs m times so the total operations for (2) becomes 15nm
 * 
 * f(n,m) = (1)+(3) = 15nm + 4
 */
	
//this method takes the two words and populates the LCS matrix with which characters are the same
//in each word
	private static void populateMatrix(int[][] lcs, char[] w1, char[] w2) {
		int w1size = w1.length;
		int w2size = w2.length;
		
		for(int x=1; x<=w1size; x++) {
			for(int y=1; y<=w2size; y++) {
				if(w1[x-1] == w2[y-1]) {
					//if the characters are the same take the left/up horizontal value and incremement it by 1
					lcs[x][y] = lcs[x-1][y-1]+1;
				}else{
					//characters aren't the same therefore take the largest value from the value in the matrix
					//to the left or up which is larger
					lcs[x][y] = Math.max(lcs[x-1][y], lcs[x][y-1]);
				}
			}
		}
	}
	
}
//this class has a dual purpose to work with both the linked list and suffix trie
class Node {
//when used for the suffix trie the node stores the links to its children in a hash table. The
//the children a stored according to the character that it starts with. Since alphabetical characters
//in Java can also be casted to their ASCII/Unicode value at a tiny operational cost, they can be stored
//in an array using the arrays index as their value. To make sure the smallest amount of space is used each
//ASCII value is modded by 5. This means each node has an array of length 5 instead of the absurd length of 84
//which is the normal ASCII value of T.
	
  /*A = 65 % 5 = 0
	C = 67 % 5 = 2
	G = 71 % 5 = 1
	T = 84 % 5 = 4*/
	private final static int HASHLINKS = 5;
	private final static int[] linkage = {0,2,1,4};

//start and end represent the indexes a particular suffix is located within a word
	public int start;
	public int end;
	public Node[] links;

//the variables required to work with a linked list
	public char element;
	public Node next;

/*
 * Analysis of Node(element) constructor:
 * f(n) = 1
 * - 1 assignment of variable
 */
//constructor for the node to be used with the linked list
	public Node(char element) {
		this.element = element;
	}
	
/*
 * Analysis of both Node(int start, int end) and Node() constructors:
 * f(n) = 11
 * - 3 for assigning values to 3 variables
 * - 8 for accessing an array and assigning a value to it
 * 
 */
//two constructors for Node that for when the Node is used in the suffix trie
	public Node(int start, int end) {
		this.start = start;
		this.end = end;
		
		links = new Node[HASHLINKS];
		links[0] = null;
		links[2] = null;
		links[1] = null;
		links[4] = null;
	}
	
	public Node() {
		this.start = -1;
		this.end = -1;
		
		links = new Node[HASHLINKS];
		links[0] = null;
		links[2] = null;
		links[1] = null;
		links[4] = null;
	}
	
	public void print(Node node,int s) {
		String space="";
		for(int d=0; d<s; d++) {
			space += "    ";
		}
		if(node.start!=-1) {
			System.out.format(space+"Start: %d, End: %d\n", node.start, node.end);
		}
		for(int i:linkage) {
			if(node.links[i]!=null) {
				print(node.links[i],s+1);
			}
		}
	}
/*
 * Analysis of putLinks
 * f(n) = 3
 * 3 - calculate the position in the array to access and then assign it the node
 */
//perform the hash of the character and store the child of the node appropriately
	public void putLinks(int i, Node node) {
		links[(i%5)] = node;
	}
/*
 * Analysis of getLinks
 * f(n) = 3
 * 3 - calculate position in the array to access and then return the node
 */
//perform the hash of the character and return the desired child
	public Node getLinks(int i) {
		return links[(i%5)];
	}
}

//linked list which is used to originally store the number of characters from the file
//and determine the length so as to then create the appropriate character array
class LinkedList {
	public Node head;
	public int size;
	
/*
 * Analysis of constructors:
 * f(n) = 2
 *  Assigning values to each of the variables of the linked list
 */
	public LinkedList() {
		this.head = null;
		this.size = 0;
	}
	public LinkedList(Node node) {
		this.head = node;
		this.size = 1;
	}
/*
 * Analysis of add:
 * f(n) = 2+1+2
 *      = 5
 *  2 for assigning the head of the list to the current nodes next
 *  1 for assigning the current node to be the head
 *  2 for incrementing the size
 * 
 */
//add acts likes a stack. So the word is stored in reversed order
//but this method involves less computational work
	public void add(Node node) {
		node.next = this.head;
		head = node;
		size++;
	}
	
}