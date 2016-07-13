/*
 * Author: John Massy-Greene(z3215661)
 * Date: 17/5/2016
 * Program Description: This program reads a file with n amount of tasks. Each task has a name, a release date and a deadline
 *                      The program then uses the tasks and the amount of cores provided to calculate a valid schedule
 *                      If a possible schedule is found then it creates a file with each task printed in non-decreasing order
 *                      the output file has the format of t1 s1....tn sn where tn=task name and sn=start time
 * 
 */

import java.util.regex.*;
import java.io.*;
public class TaskScheduler {
	
/*
 * Analysis of scheduler:
 * 
 * Complexity of functions used in this scheduler
 * (1) handleargs          = 7
 * (2) getAllTasks         = 69n+11
 * (3) sortTaskByRelease   = 40nlogn+43n+2(n+1)+11
 * (4) createValidSchedule = 40nlogn+59n+2(n+1)+10
 * (5) outputTofile        = 8n+6 
 *  
 * f(n) = (1)+(2)+(3)+(4)+(5)+15
 *      = 80nlogn + 4(n+1) + 179n + 60
 *      = O(nlogn)
 * 
 * 2 - creating a new linked list
 * 3 - creating 2 arrays and assigning value to num tasks
 * 2+(1) - calling the function handleArgs and using the value returned from it as a comparison
 * 1+(2) - calling the function get allTasks
 * 3 - numTasks value comes from the Linked list size and creating a new array
 * 1+(3) - calling the sortTaskByRelease function
 * 1 - creating the schedule array
 * 2+(4) - calling the createValidSchedule function and using its value as a comparison
 * (5) - calling the output to file function
 * 
 * 
 */
	//This function takes the tasks located in file 1, and creates a schedule
	//where the number of tasks that are able to be handled at any one moment
	//are dependent on the integer m. This schedule is outputed to file2.
	public static void scheduler(String file1, String file2, int m) {
		LinkedList Tasks = new LinkedList();
		Task[] sortByRelease = null;
		Task[] schedule = null;
		int numTasks = 0;
		//take all the arguments and find out if they are valid
		if(!handleArgs(file1, file2, m)){
			return;
		}
		//get all the tasks in the file and put them into a linked list called task.
		getAllTasks(file1, Tasks);
		
		numTasks = Tasks.size;
		sortByRelease = new Task[numTasks];
		//using the linked list sort the tasks by their release date
		sortTaskByRelease(Tasks, sortByRelease);

		schedule = new Task[numTasks];
		//using the tasks that are sorted by release date create a valid schedule
		if(!createValidSchedule(schedule, sortByRelease, m, numTasks)) {
			System.out.println("No feasible schedule exists");
			return;
		} else {
			//if their is a valid file then output each task to the file
			outputToFile(schedule, file2);
		}
	}
/*
 * Analysis of handleArgs:
 * f(n) = 7
 * 1 - comparison of file1 to null
 * 3 - creating a new file object, comparing it to the value that the exists function returns
 * 2 - comparing the file2 and m values
 * 1 - returning the value
 * 
 */
	private static boolean handleArgs(String file1, String file2, int m) {
		//if file1 is null or the file provided in the string doesn't exist then exit the program
		if(file1==null){
			System.out.println("file1 does not exist");
			System.exit(0);
		}
		File input = new File(file1);
		if(!input.exists()) {
			System.out.println("file1  does  not exist");
			System.exit(0);
		}
		//file2 which is the output file must be a string. If its null then we cannot print to the output
		if(file2==null) {
			System.out.println("Not enough output file information");
			return false;
		}
		//if the amount of cores provided are less than or equal to 0 then its not possible to create a schedule
		if(m<=0) {
			System.out.println("No feasible schedule exists");
			return false;
		}
		
		return true;
	}
	
/*
 * Analysis of getAllTasks:
 * f(n) = 69n + 11
 * 
 * Note: this analysis assumes that for the worst case scenario each tasks attributes are on their own seperate line.
 *       each task can be reduced to 3 attributes which makes some of operations triple as a result but its still linear time.
 * 
 * (1)      
 * Start of the function has complexity of: 11 
 * 6 - assigning values to 6 variables
 * 2 - assigning a value to pattern and compiling the pattern
 * 3 - assigning a value to input file and calling two constructors
 * 
 * (2)
 * outer while loop(inputfile.readline): 13
 * 3 - the while condition which assigns a value from readLine
 * 2 - assigning a value from p matcher
 * 1 - for the while loop condition(which in this case only runs once because theres one element per line(see assumptions)
 * 3 - assigning a value to an array element from a function call to matcher
 * 2 - incrementing i
 * 1 - comparison of i>0
 * 
 * (3)
 * if condition(i == maxattributes): 30
 * 22 - for calling the valid attributes function and assigning the value to newNode
 * 1  - if comparison to null
 * 6  - adding the node to the linked list
 * 1  - making i = 0
 * 
 * 
 * Taking into account that each element of a task(3) is on its own line and there are n elements. There are 3n elements to process
 * Thus (2) has a complexity of 3n*13 = 39n (4)
 * 
 * The if condition will only run once per 3 elements. Thus it runs n times
 * Thus (3) has a complexity of 30n (5)
 * 
 * Total complexity of the function is (1)+(4)+(5) = 11+39n+30n = 69n+11
 */
	
	private static void getAllTasks(String f, LinkedList t) {
		//3 atrributeds per task
		String[] attributes = new String[3];
		int i = 0;
		int maxAttributes = 3;
		String input;
		BufferedReader inputFile = null;
		Node newNode = null;
		//each variable in a task is made of of alphanumeric characters
		//there is the possibility of negative numbers so thats included for later
		//validation purposes
		String taskPatt = "\\s*([a-zA-Z0-9-]+)\\s*";
		Pattern p = Pattern.compile(taskPatt);
		
		try {
			inputFile = new BufferedReader(new FileReader(f));
		} catch (FileNotFoundException e) {
			System.out.print("file1 does not exist");
			System.exit(0);
		}
		
		try {
			//read each line of the file
			while((input=inputFile.readLine())!=null) {
				//match each element of a task
				Matcher m = p.matcher(input);
				while(m.find()) {
					attributes[i] = m.group(1);
					i++;
					//if 3 valid attributes have been found combine them into 1 task
					if(i==maxAttributes) {
						//check to see if the attributes are valid
						newNode = validAttributes(attributes);
						if(newNode!=null) {
							//add them into the linkedlist
							t.add(newNode);
							i = 0;
						} else {
							System.out.format("input error when reading the attributes of the task %s", attributes[0]);
							System.exit(0);
						}
					}
				}
				//if there are any attributes leftover then that means the file has not been written correctly
				if(i>0) {
					System.out.format("input  error when  reading  the  attributes  of  the  task %s", attributes[0]);
					System.exit(0);
				}
			}
		} catch (IOException e1) {
			System.out.println("Cannot read the file");
			System.exit(0);
		}
		
		
		try {
			//close the file to make sure we have no memory leaks
			inputFile.close();
		} catch (IOException e) {
			System.out.println("Cannot close file");
			System.exit(0);
		}
		return;
	}
/*
 * Analysis of validAttributes:
 * f(n) = 21
 * 
 * 1 - assigning value to new node
 * 3 - pattern matching the name(comparison to return value, function call and array element access)
 * 6 - 3 for pattern matching release time + 3 for turning the value into an integer
 * 7 - 6 for pattern matching the deadline time and turning the value into an integer and 1 for comparison to 1
 * 5 - creating a new node, calling its constructor and assigning its values 
 * 
 */
	public static Node validAttributes(String[] a) {
		Node newNode = null;
		int release;
		int deadline;
		//a valid name only has numbers and characters
		if(!Pattern.matches("[A-Za-z]+[A-Za-z0-9]*", a[0])) {
			return newNode;
		}
		//a valid release time only has numbers and is >=0
		if(!Pattern.matches("\\d+", a[1])) {
			return newNode;
		} else {
			release = Integer.parseInt(a[1]);
		}
		//a valid deadline only has numbers and is more than 0
		if(!Pattern.matches("\\d+", a[2])) {
			return newNode;
		} else {
			deadline = Integer.parseInt(a[2]);
			if(deadline<1) {
				return newNode;
			}
		}
		//a valid task must have a release that is less than a deadline
		if(release>=deadline) {
			return newNode;
		}
		//create a new node for linked list with the attributes of a task
		newNode = new Node(a[0], release, deadline);
		
		return newNode;
	}
/*
 * Analysis of sortTaskByRelease:
 * f(n) = 40nlogn+43n+2(n+1)+11
 * 
 * 2 - assigning a value to tasknum from the size of task array
 * 1 - assigning value to i
 * 2(n+1)+6 - calling the constructor of minHeap
 * 2 - assigning a value to node from the head of the linked list
 * 
 * 1st while loop:
 *   - 1        - comparison
 *   - 4        - accessing nodes key and assigning it a value from the release time
 *   - 18logn+9 - inserting into heap
 *   - 2        - getting the next node in an array
 *   - loop runs n times so times everything by n
 *   - final calculation:  18nlogn+16n
 *         
 * 2nd while loop:
 *   - 22logn+22 - removing the minimum value from the heap, assigning it to ctask and comparing it to null
 *   - 3         - accessing array element and assigning the value of an ctasks attribute
 *   - 2         - increment i
 *   - loop runs n times so times everything by n
 *   - final calculation: 22nlogn+27n
 */
	private static void sortTaskByRelease(LinkedList t, Task[] r) {
		int taskNum = t.size;
		Node ctask;
		int i=0;
		
		MinHeap releaseHeap = new MinHeap(taskNum);
		Node node = t.head;
		//from all the tasks in the linked list we can create a heap data structure
		//Because the heap is an array which requires to know it size when its created
		//we use the size of the linked list to let the array know its size
		while(node!=null) {
			node.key = node.value.release;
			releaseHeap.insert(node);
			node = node.next;
		}
		//remove the tasks from the heap so we can create an array that is ordered by
		//the release time of each task
		while((ctask=releaseHeap.removeMin())!=null) {
			r[i] = ctask.value;
			i++;
		}
	}
/*
 * Analysis of createValidSchedule:
 * f(n) = 40nlog + 59n + 2(n+1) + 10
 * 
 * The following assumptions have been made about the worst case scenario:
 *  - each task has the same release time and different deadlines
 *  - there's only one core so each task gets handled separately
 *  
 *   (1)
 *   start of the function has complexity of 2(n+1)+10:
 *   2 - creating and setting the value of time
 *   1 - numTasks value
 *   1 - i value
 *   2(n+1)+6 - creating a new minHeap
 *   
 *   (2)
  *  The outside loop(numTasks<maxTasks) has a complexity of 4:
 *   - 1 for the comparison of numTasks
 *   - 1 for assigning c to 0
 *   - 2 for incrementing time
 * 
 *   (3)
 *   while loop for tasks[i].release == time has complexity of 18logn+25:
 *   5 - the two conditions + the AND requirement
 *   5 - creating a new node
 *   4 - accessing the key attribute and assigning it a value from the tasks array deadline
 *   18logn + 9 - inserting a node into the heap(see analysis of that function)
 *   2 - incrementing i
 *   
 *   (4)
 *   while loop for cores and deadline has complexity of 22logn+34:
 *   5 - two conditional and AND requirement
 *   22logn+23 - removing the min value from the heap(see analysis of that function) and assigning
 *               that value to the ctask
 *   2 - assigning the start time to the cTask
 *   2 - assigning value to array
 *   2 - c increment
 *   
 *   So taking this knowledge and the assumptions:
 *   - function start and we get (1) => 2(n+1)+10
 *   - each task runs through (3) and gets put into the heap => 18nlogn+25n
 *   - each task runs through (2) and (4) and gets put into the correct time slot => 22nlogn+34n
 *   
 *    22nlogn+34n + 18nlogn+25n + 2(n+1) + 10 = 40nlog + 59n + 2(n+1) + 10
 * 
 */
	//function takes the sorted(by release) array of Tasks and sorts them also by deadline
	//and then stores them in the array s
	private static boolean createValidSchedule(Task[] s, Task[] tasks, int cores, int maxTasks) {
		int c;
		//numTasks is used to see if there are any more tasks to process
		int numTasks = 0;
		//the time variable is used for deciding when a task starts
		//the earliest time available for any task is the release time of the first element of the array
		//of sorted tasks.
		int time = tasks[0].release;
		int i=0;
		//create a new heap that is now ordered on both release and deadline times
		MinHeap deadlineHeap = new MinHeap(maxTasks);
		
		while(numTasks<maxTasks) {
			c = 0;
			//while the current tasks release time is equal to current start time put it into the deadline heap
			while(i<maxTasks && tasks[i].release == time) {
				Node cNode = new Node(tasks[i]);
				cNode.key = tasks[i].deadline;
				deadlineHeap.insert(cNode);
				i++;
			}
			//the number of cores dictates how many tasks we can pull out of the deadline heap at particular time
			while((c<cores) && (deadlineHeap.entries>0)){
				Task cTask = deadlineHeap.removeMin().value;
				//If the current time is more than the tasks deadline, then there is no possible
				//schedule
				if(cTask.deadline<=time) {
					return false;
				}
				//assign the current time to the tasks starting time
				cTask.startTime = time;
				s[numTasks] = cTask;
				numTasks++;
				//increment the number of cores used
				c++;
			}
			//increment the current time
			time++;
		}
		
		return true;
	}
/*
 * Analysis of outputToFile: f(n)=8n+6
 * 
 * 2 - creating the File and FileWriter objects
 * 2 - calling the file exists function and using its value for comparison
 * 1 - creating a new fileWriter object
 * 8n - For loop for printing the string to a file
 *    - 1n - calling the write function
 *    - 4n - calling variables from an element in an array twice
 *    - 2n - creating strings with spaces in them
 *    - 1n - combining the string together
 * 1 - calling the function for the file to close
 * 
 * 
 */
	//take the tasks in s and output them to a file with the name
	//provided by the outputFile string
	private static void outputToFile(Task[] s, String outputFile) {
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
			System.out.println("Cannot create or find output file");
			System.exit(0);
		}
		
		try {
			//write the name of each task and the time it starts to the file
			for(int i=0; i<s.length; i++) {
				out.write(s[i].name+" "+s[i].startTime+" ");
			}
		} catch (IOException e) {
			System.out.println("Cannot write to file");
			System.exit(0);
		}
		
		try {
			//close the file to prevent memory leaks
			out.close();
		} catch (IOException e) {
			System.out.println("Cannot close the file stream");
			System.exit(0);
		}
	}
}

class Task {
//each task has a name, release time, deadline time and if its schedulable then a starttime
	public String name;
	public int release;
	public int deadline;
	public int startTime;
/*
 * Each of these constructors is f(n) = 3
 */
	public Task() {
		this.name = null;
		this.release = -1;
		this.deadline = -1;
	}
	
	public Task(String name, int release, int deadline) {
		this.name = name;
		this.release = release;
		this.deadline = deadline;
	}
	
}
//Nodes that are used for both linked list and heap data structures
class Node {
	public int key;
	public Task value;
	public Node next;
	
	public Node() {
		this.key = -1;
		this.value = null;
		this.next = null;
	}
	public Node(Task element) {
		this.key = -1;
		this.value = element;
		this.next = null;
	}
/*
 * Constructor has f(n) = 6
 * 2 - assigning values to key and next
 * 4 - creating a task object and assigning it to the value
 */
	public Node(String name, int release, int deadline) {
		this.key = -1;
		this.value = new Task(name, release, deadline);
		this.next = null;
	}

}

//the linked list is used because at the beginning of the program we don't know how many tasks
//there are. So we create a linked list, find out the resulting size and use that size to inform
//the heap how big it should make its internal array
class LinkedList {
	public Node head;
	public int size;
	
/*
 * Each of these constructors have f(n) = 2
 * 
 */
	public LinkedList() {
		this.head = null;
		this.size = 0;
	}
	public LinkedList(Node head) {
		this.head = head;
		this.size = 1;
	}
/*
 * Analysis of add:
 * f(n) = 5
 * 1 - comparison of head
 * 1 - assigning head to a temporary node
 * 2 - accessing the new node next and assigning the temporary node to it
 * 1 - assigning a new head
 */
//this add function essentially acts like a stack. the first element in will be the last element
//to be taken out
	public void add(Node newNode) {
		if(head == null) {
			head = newNode;
			size++;
		} else {
			Node prevHead = this.head;
			newNode.next = prevHead;
			this.head = newNode;
			size++;
		}
	}
}
//a heap data structure that uses the minimum value to sort itself
//it maintains a height of logn by making sure each level of the tree
//is full before moving onto the next level
class MinHeap {
	Node[] heap;
	int entries;
//the position in array where the next element can be inserted
	int lastNode;
	int minPos = 1;
	int size;
/*
 * Analysis of constructor:
 * f(n) = 2(n+1)+6
 * 
 * 2(n+1) for going through each array element and assigning it the value null
 * 2 - calculating the size of the array and assigning and creating a new instance
 * 2 - for the assignment of values to entries and lastnode
 * 2 - for calculating the size of the array and assigning it to a dedicated variable
 */
	public MinHeap(int size) {
		this.heap = new Node[size+1];
		for(int i=0; i<size+1; i++) {
			this.heap[i] = null;
		}
		this.entries=0;
		this.lastNode=1;
		this.size = size+1;
	}
/*
 * Analysis of removeMin:
 * f(n) = 17 + (22logn+3) = 20 + 22logn
 * 
 * 22logn+3 comes from the analysis of bubbledown
 * 17 operations come from:
 * 
 * 2 - accessing the heap and assigning the value to currentMin
 * 2 - calculating one less than the last node and assigning that value
 * 2 - comparing the entry value twice
 * 4 - calling the swap function
 * 2 - for accessing the array element and assigning it null
 * 2 - calculating and assigning a new value to last node
 * 2 - calculating and assigning a new value to entries
 * 1 - returning from the function with minimum value
 */
	public Node removeMin() {
		Node currentMin = heap[minPos];
		//lastNode actually points to the next available space in the array so the actual last node is the one before it
		int actualLast = lastNode-1;
		//return null if there are no elements in the heap
		if(entries==0) {
			return null;
		}
		//no need to balance the heap if theres only one node
		if(entries==1) {
			heap[minPos] = null;
			lastNode--;
			entries--;
		} else {
			//bring the last element up to the root of the tree and then balance the tree
			//downwards
			swap(minPos, actualLast);
			heap[actualLast] = null;
			lastNode--;
			entries--;
			bubbleDown(minPos);
		}
		return currentMin;
	}
/*
 * Analysis of insert:
 * f(n) = 7 + (18logn + 2) = 18logn + 9 
 * 
 * 2 - accessing the heap array and assigning a value to it
 * 2 - calculation and assigning a value to the entries variable
 * 2 - calculation and assigning a value to the lastnode variable
 * 1 - for calling the bubbleUp function
 * 18logn+2 - for the bubbleUp function
 * 
 */
	public void insert(Node newNode) {
		//insert the node into the next available position and then make sure the tree is balanced
		heap[lastNode] = newNode;
		entries++;
		bubbleUp(lastNode);
		lastNode++;
	}

/*
 * Analysis of bubbleDown:
 * f(n) = 22logn + 3
 * 
 * The most amount of nodes this function can visit is the height of the tree which is equal to logn and the number of operations
 * that the function does per height that it visits is 22.
 * 
 * 2 - for calculating and assigning the left position number
 * 3 - for calculating and assigning the right position number
 * 7 - comparison of rightpos to the lastnode, 2x(array and variable access)+ comparison and the final assignment of minChild
 * 5 - comparing the keys of 2 items in an array
 * 4 - calling the swap function(please see the analysis of that function)
 * 1 - for calling bubbleUp
 * 
 * The final 3 operations are comparing the left and right positions to the last node and returning
 */
	public void bubbleDown(int pos) {
		//in order to mimick a tree, the left and right nodes are at variable positions in the array
		int leftPos = pos*2;
		int rightPos = ((pos*2)+1);
		int minChild;
		//make sure that we haven't gone past the last node
		if(rightPos >= lastNode) {
			if(leftPos >= lastNode) {
				return;
			} else {
				minChild = leftPos;
			}
		//compare the left and right to see which is the smallest key
		} else {
			if(heap[leftPos].key <= heap[rightPos].key) {
				minChild = leftPos;
			} else {
				minChild = rightPos;
			}
		}
		//if the position is more than the child which is smallest then
		//the tree is unbalanced so swap them and call the function again
		//to make sure the rest of the tree is balanced
		if(heap[pos].key > heap[minChild].key) {
			swap(pos, minChild);
			bubbleDown(minChild);
		}
		
	}
/*
 * Analysis of bubbleUp
 * f(n) = 18*logn+2 => O(logn)
 * 
 * The maximum number of times this function can call itself is equal to the height of the tree which is equal to logn
 * and the number of operations each time the function is called is 18.
 * 
 * 1 - for comparison of the current position to root
 * 7 - for the assignment of the parent and the calling of getParent function(see the analysis below)
 * 5 - for accessing the heap arrays elements, their keys and then comparing them 
 * 4 - for calling the function swap(1 + 3)
 * 1 - calling bubbleUp
 * 
 * The final +2 comes from comparing the position to the root and then returning
 */
	public void bubbleUp(int pos) {
		//if your at the root of the tree stop
		if(pos==minPos) {
			return;
		}
		//if the child is smaller than the parent then the tree is unbalanced
		//so swap them and make sure the rest of the tree is balanced
		int parent=getParent(pos);
		if(heap[pos].key < heap[parent].key) {
			swap(pos, parent);
			bubbleUp(parent);
		}
	}
	
/*
 * Analysis: f(n) = 5
 * 2 for comparison and calculation of mod i with 2 
 * 3 for the returning the longest of the two calculations which
 *    involves i-1 and then dividing the the result by 2	
 */
//find the parent node depending on whether the nodes position is odd(right child)
//or even(a left child)
	public int getParent(int i) {
		if((i%2)==0) {
			return (i/2);
		} else {
			return ((i-1)/2);
		}
	}
/*
 * Analysis: f(n) = 3
 * 3 for 3 assignments
 */
//swap the elements in the tree
	public void swap(int x, int y) {
		Node temp = heap[x];
		heap[x] = heap[y];
		heap[y] = temp;
	}
	
}
