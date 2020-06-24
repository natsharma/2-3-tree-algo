package twothreetree;
import java.io.*;
import java.util.Scanner;

public class TwoThreeTreeAlgorithm {
	
	public static void main(String [] args) throws FileNotFoundException, IOException{
		
		TwoThreeTree tree1 = new TwoThreeTree();
		BufferedWriter output = new BufferedWriter(new OutputStreamWriter(System.out, "ASCII"), 4096);
		String FILE = "src/ba1/2-3-tree-range-query-sample-io/test1.in";
		File file = new File(FILE);
		Scanner sc = new Scanner(file);

		//Scanner sc = new Scanner(System.in);

		int dataSize = sc.nextInt();
		for (int i=1; i<=dataSize*2; i+= 2) {
			String k = sc.next();
			String val = sc.next();
			int v = Integer.parseInt(val);
			insert(k, v, tree1);
		}
		//assign index for query search
		while(sc.hasNext()) {
			String x = sc.next();
			String y = sc.next();
			if(x.compareTo(y) > 0) {
				printRange(tree1.root, y, x, output);
			} else {
				printRange(tree1.root, x, y, output);
			}
		}
		output.flush();
		//printAll(tree1.root);
	}
    /*
     * I wrote printAllRange method
     * it traverses through all the nodes but only print nodes in the specified Range [x, y]
     * runtime is worst case, so inefficient, but works fast on small trees
     */
    static void printAllRange(Node n, String x, String y){    
        if (n instanceof LeafNode){
            if (n.guide.compareToIgnoreCase(x) >= 0 && n.guide.compareToIgnoreCase(y) <= 0){
                LeafNode leaf = (LeafNode) n;
                System.out.println(leaf.guide + " " + leaf.value);
            }
        }
        else if (n instanceof InternalNode) {
            printAllRange(((InternalNode)n).child0, x, y);
            printAllRange(((InternalNode)n).child1, x, y);
            if (((InternalNode)n).child2 != null){
                printAllRange(((InternalNode)n).child2, x, y);
            }
        }
    }
    /* PrintAll method to print all the nodes in the subtree of node N */
    static void printAll(Node n, BufferedWriter output) throws IOException{
        if (n instanceof LeafNode){
            LeafNode leaf = (LeafNode) n;
            output.write(leaf.guide + " " + leaf.value);
            output.newLine();
        }
        else if (n instanceof InternalNode) {
            printAll(((InternalNode)n).child0, output);
            printAll(((InternalNode)n).child1, output);
            if (((InternalNode)n).child2 != null){
                printAll(((InternalNode)n).child2, output);
            }
        }
    }
	/* greater than or equal to, less than or equal to, algo more efficient than PrintAllRange() 
	 * it's different than the PrintAllRange() method because it doesn't traverse all the nodes
	 * it only traverses the nodes that are within the [x, y] range, GE and LE
	 * */
    static void printGE(Node p, String x, BufferedWriter output) throws IOException{
        //basically, there's only the root of the tree, and if its bigger print it. Otherwise print nothing?? 
        if (p instanceof LeafNode && p.guide.compareToIgnoreCase(x) >= 0){
            LeafNode leaf = (LeafNode) p;
            output.write(leaf.guide + " " + leaf.value + "\n");
        } 
        else if (p instanceof InternalNode) {
            InternalNode internal = (InternalNode) p;
            if (internal.guide.compareToIgnoreCase(x) >= 0){
                if (internal.child0.guide.compareToIgnoreCase(x) >= 0){
                    printGE(internal.child0, x, output);
                    printAll(internal.child1, output);
                    if (internal.child2 != null){
                        printAll(internal.child2, output);
                    }
                }
                else if (internal.child1.guide.compareToIgnoreCase(x) >= 0){
                    printGE(internal.child1, x, output);
                    if (internal.child2 != null){
                    printAll(internal.child2, output);
                    }
                }
                else {
                    printGE(internal.child2, x, output);
                } 
            }
        }
    }
    static void printLE(Node p, String y, BufferedWriter output) throws IOException{
        if(p instanceof LeafNode && p.guide.compareTo(y) < 0){
            LeafNode leaf = (LeafNode) p;
            output.write(leaf.guide + " " + leaf.value + "/n");
        }
        else if(p instanceof InternalNode){
            InternalNode internal = (InternalNode) p;
            if(internal.guide.compareToIgnoreCase(y) <= 0){
                printAll(p, output);
            }
            else if(y.compareToIgnoreCase(internal.child0.guide) < 0){
                printLE(internal.child0, y, output);
            }
            else if(y.compareToIgnoreCase(internal.child1.guide) < 0){
                printAll(internal.child0, output);
                printLE(internal.child1, y, output);
            }
            else{
                printAll(internal.child0, output);
                printAll(internal.child1, output);
                printLE(internal.child2, y, output);
            }
        }
    }
    static void printRange(Node p, String x, String y, BufferedWriter output) throws IOException{
        if(p instanceof LeafNode && p.guide.compareToIgnoreCase(x) >= 0 && p.guide.compareToIgnoreCase(y) <= 0){
            LeafNode leaf = (LeafNode) p;
            output.write(leaf.guide + " " + leaf.value); 
            output.newLine();
            return;
        }
        else if (p instanceof InternalNode){
            InternalNode internal = (InternalNode) p;
            if (p.guide.compareToIgnoreCase(x) >= 0 && p.guide.compareToIgnoreCase(y) <= 0){
                if (internal.child0.guide.compareToIgnoreCase(x) >= 0){
                    printGE(internal.child0, x, output);
                    printAll(internal.child1, output);
                    if (internal.child2 != null){
                        printAll(internal.child2, output);
                    }
                }
                else if (internal.child2 != null){
                    if (internal.child1.guide.compareToIgnoreCase(x) > 0){
                        printGE(internal.child1, x, output);
                        printAll(internal.child2, output);
                    } 
                    else {
                        printGE(internal.child2, x, output);
                    }
                }
                else{
                    printGE(internal.child1, x, output);
                } 
            }
            else if (internal.guide.compareTo(y) > 0) {
                if (internal.child0.guide.compareToIgnoreCase(y) > 0){
                    printRange(internal.child0, x, y, output);
                }
                else if (x.compareToIgnoreCase(internal.child0.guide) <= 0){
                    if (y.compareToIgnoreCase(internal.child1.guide) < 0){
                        printGE(internal.child0, x, output);
                        printLE(internal.child1, y, output);
                    }
                    else{
                        printGE(internal.child0, x, output);
                        printAll(internal.child1, output);
                        printLE(internal.child2, y, output);
                    }
                }
                else {
                    if (y.compareToIgnoreCase(internal.child1.guide) < 0){
                        printRange(internal.child1, x, y, output);
                    
                    }
                    else if (x.compareToIgnoreCase(internal.child1.guide) <= 0){
                        printGE(internal.child1, x, output);
                        printLE(internal.child2, y, output);
                    }
                    else {
                    printRange(internal.child2, x, y, output);
                    }
                }
            } 
        }
    }
	
	/*
	 * @Victor Shoup
	 * Starter Code below for the two three tree
	 * insert a key value pair into tree
	 * (overwrite existing value if key is already present)
	 */
	static void insert(String key, int value, TwoThreeTree tree) {
		int h = tree.height;
		if (h == -1) {
			LeafNode newLeaf = new LeafNode();
			newLeaf.guide = key;
			newLeaf.value = value;
			tree.root = newLeaf; 
			tree.height = 0;
		} else {
			WorkSpace ws = doInsert(key, value, tree.root, h);
			if (ws != null && ws.newNode != null) {
				// create a new root
				InternalNode newRoot = new InternalNode();
				if (ws.offset == 0) {
					newRoot.child0 = ws.newNode; 
					newRoot.child1 = tree.root;
				} else {
					newRoot.child0 = tree.root;
					newRoot.child1 = ws.newNode;
				}
				resetGuide(newRoot);
				tree.root = newRoot;
				tree.height = h+1;
			}
		}
	}
	/* auxiliary recursive routine to insert */
	   static WorkSpace doInsert(String key, int value, Node p, int h) {
		   // auxiliary recursive routine for insert
		      if (h == 0) {
		         // we're at the leaf level, so compare and 
		         // either update value or insert new leaf
		         LeafNode leaf = (LeafNode) p; //downcast
		         int cmp = key.compareTo(leaf.guide);

		         if (cmp == 0) {
		            leaf.value = value; 
		            return null;
		         }
		         // create new leaf node and insert into tree
		         LeafNode newLeaf = new LeafNode();
		         newLeaf.guide = key; 
		         newLeaf.value = value;

		         int offset = (cmp < 0) ? 0 : 1;
		         // offset == 0 => newLeaf inserted as left sibling
		         // offset == 1 => newLeaf inserted as right sibling
		         WorkSpace ws = new WorkSpace();
		         ws.newNode = newLeaf;
		         ws.offset = offset;
		         ws.scratch = new Node[4];
		         return ws;
		      }
		      else {
		         InternalNode q = (InternalNode) p; // downcast
		         int pos;
		         WorkSpace ws;

		         if (key.compareTo(q.child0.guide) <= 0) {
		            pos = 0; 
		            ws = doInsert(key, value, q.child0, h-1);
		         }
		         else if (key.compareTo(q.child1.guide) <= 0 || q.child2 == null) {
		            pos = 1;
		            ws = doInsert(key, value, q.child1, h-1);
		         }
		         else {
		            pos = 2; 
		            ws = doInsert(key, value, q.child2, h-1);
		         }

		         if (ws != null) {
		            if (ws.newNode != null) {
		               // make ws.newNode child # pos + ws.offset of q

		               int sz = copyOutChildren(q, ws.scratch);
		               insertNode(ws.scratch, ws.newNode, sz, pos + ws.offset);
		               if (sz == 2) {
		                  ws.newNode = null;
		                  ws.guideChanged = resetChildren(q, ws.scratch, 0, 3);
		               }
		               else {
		                  ws.newNode = new InternalNode();
		                  ws.offset = 1;
		                  resetChildren(q, ws.scratch, 0, 2);
		                  resetChildren((InternalNode) ws.newNode, ws.scratch, 2, 2);
		               }
		            }
		            else if (ws.guideChanged) {
		               ws.guideChanged = resetGuide(q);
		            }
		         }
		         return ws;
		      }
		   }
	/* copy children of q into x, and return # of children */
	   static int copyOutChildren(InternalNode q, Node[] x) {
		   // copy children of q into x, and return # of children

		      int sz = 2;
		      x[0] = q.child0; x[1] = q.child1;
		      if (q.child2 != null) {
		         x[2] = q.child2; 
		         sz = 3;
		      }
		      return sz;
	   }
	/* 
	 * insert p into x[0...sz] at position pos
	 * moving existing entries to the right
	 */
	   static void insertNode(Node[] x, Node p, int sz, int pos) {
		   // insert p in x[0..sz) at position pos,
		   // moving existing extries to the right
		      for (int i = sz; i > pos; i--)
		         x[i] = x[i-1];
		      x[pos] = p;
		   }
	/* reset q.guide, and return true if it changes */
	   static boolean resetGuide(InternalNode q) {
		   // reset q.guide, and return true if it changes.
		      String oldGuide = q.guide;
		      if (q.child2 != null)
		         q.guide = q.child2.guide;
		      else
		         q.guide = q.child1.guide;

		      return q.guide != oldGuide;
	   }
	/*
	 * reset q's children to x[pos...pos+sz], where sz is 2 or 3
	 * also resets guide, and returns the result of that
	 */
	   static boolean resetChildren(InternalNode q, Node[] x, int pos, int sz) {
		   // reset q's children to x[pos..pos+sz), where sz is 2 or 3.
		   // also resets guide, and returns the result of that

		      q.child0 = x[pos]; 
		      q.child1 = x[pos+1];

		      if (sz == 3) 
		         q.child2 = x[pos+2];
		      else
		         q.child2 = null;

		      return resetGuide(q);
	   }
}
class Node {
	String guide;
	//guide points to max key stored in subtree rooted at Node
}
class InternalNode extends Node {
	Node child0, child1, child2;
	// child0 and child1 are always non-null
	// child2 is null iff node has only 2 children
}
class LeafNode extends Node {
	//guide points to the key
	int value;
}
class TwoThreeTree {
	Node root;
	int height;
	public TwoThreeTree() {
		root = null;
		height = -1;
	}
}
class WorkSpace {
	//this class is used to hold return values for the recursive doInsert()
	//routine
	Node newNode;
	int offset;
	boolean guideChanged;
	Node[] scratch;
}