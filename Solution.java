import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Solution {
	
	static TwoThreeTree tree1 = new TwoThreeTree();
	
	public static void main(String [] args) throws IOException {
		/* Enter your code here. Read input from STDIN. Print output to STDOUT. Your class should be named Solution. */ 
        BufferedWriter output = new BufferedWriter(new OutputStreamWriter(System.out, "ASCII"), 4096);
        File file = new File("src/ba2/2-3-tree-range-update-sample-io/test2.in");
        Scanner sc = new Scanner(file); //replace "file" with "System.in" to test other files
        String dataSize = sc.nextLine();
        
        //only digest queries 1 & 2
        ArrayList<String> queries = new ArrayList<String>();
        while (sc.hasNextLine()){
            String line = sc.nextLine();
            
            if (line.startsWith("1")){
                String[] info = line.split("\\s+");
                String planet = info[1]; int fee = Integer.parseInt(info[2]);
                insert(planet, fee, tree1);
            }
            else if (line.startsWith("2")){
                String[] words = line.split("\\s+");
                String planet1 = words[1]; String planet2 = words[2]; int priceIncrease = Integer.parseInt(words[3]);
                String leftEnd = null;
                String rightEnd = null;
                if(planet1.compareToIgnoreCase(planet2) > 0) {
                    leftEnd = planet2; rightEnd = planet1;
                }
                else {
                    leftEnd = planet1; rightEnd = planet2;
                }
                printRange(tree1.root, leftEnd, rightEnd, priceIncrease, output);
            } else if (line.startsWith("3")){
                String[] words = line.split("\\s+");
                String planet = words[1];
                printPlanet(tree1.root, planet, output);
            } 
        }
        //printSubtree(tree1.root, output);
        output.flush();
	}
	
	static  void printSubtree(Node n, BufferedWriter output) throws IOException{
		if(n instanceof LeafNode) {
			LeafNode leaf = (LeafNode) n;
			output.write(leaf.guide + " " + leaf.value);
			output.newLine();
		}
		else if (n instanceof InternalNode) {
			printSubtree(((InternalNode)n).child0, output);
			printSubtree(((InternalNode)n).child1, output);
			if(((InternalNode)n).child2 != null) {
				printSubtree(((InternalNode)n).child2, output);
			}
		}
	}
	static void printPlanet(Node p, String x, BufferedWriter output) throws IOException {
	    if (p instanceof LeafNode){
	        LeafNode leaf = (LeafNode) p;
	        if(p.guide.compareToIgnoreCase(x) == 0){
	            output.write(leaf.value + "\n");
	        }
	        else {
	            output.write("-1 \n");
	        } 
	    }
	    else if (p instanceof InternalNode){
	        InternalNode internal = (InternalNode) p;
	        if (p.guide.compareToIgnoreCase(x) >= 0){
	            if (internal.child0.guide.compareToIgnoreCase(x) >= 0){
	                printPlanet(internal.child0, x, output);
	            } 
	            else if (internal.child0.guide.compareToIgnoreCase(x) < 0 && internal.child1.guide.compareToIgnoreCase(x) >= 0){
	                printPlanet(internal.child1, x, output);
	            }
	            else if (internal.child2 != null && internal.child1.guide.compareToIgnoreCase(x) < 0){
	                if (internal.child2.guide.compareToIgnoreCase(x) >= 0){
	                    printPlanet(internal.child2, x, output);
	                }
	            }
	        }
	        else if (p.guide.compareToIgnoreCase(x) < 0){
	            output.write("-1 \n");
	        }
	    }
	}
	
	static void searchPath(Node p, String xy, int increase, BufferedWriter output) throws IOException {
		if (p instanceof LeafNode){
	        LeafNode leaf = (LeafNode) p;
	        if(p.guide.compareToIgnoreCase(xy) == 0){
	            output.write(leaf.guide + " " + leaf.value + "\n");
	            leaf.value += increase;
	        }
	        else {
	            leaf.value += increase;
	            output.write(leaf.guide + " " + leaf.value + "\n");
	        }
	        
	    }
	    else if (p instanceof InternalNode){
	        InternalNode internal = (InternalNode) p;
	        if (p.guide.compareToIgnoreCase(xy) >= 0){
	            if (internal.child0.guide.compareToIgnoreCase(xy) >= 0){
	                searchPath(internal.child0, xy, increase, output);
	            } 
	            else if (internal.child0.guide.compareToIgnoreCase(xy) < 0 && internal.child1.guide.compareToIgnoreCase(xy) >= 0){
	                searchPath(internal.child1, xy, increase, output);
	            }
	            else if (internal.child2 != null && internal.child1.guide.compareToIgnoreCase(xy) < 0){
	                if (internal.child2.guide.compareToIgnoreCase(xy) >= 0){
	                    searchPath(internal.child2, xy, increase, output);
	                }
	            }
	        }
	        else if (p.guide.compareToIgnoreCase(xy) < 0){
	            internal.value += 0;
	        }
	    }
	}
	
	static void searchPathRecursive(String leftEnd, String rightEnd, int priceIncrease, BufferedWriter output) throws IOException {
		searchPath(tree1.root, leftEnd, priceIncrease, output);
		searchPath(tree1.root, rightEnd, priceIncrease, output);
	}
	
	
	/*
	 * Methods below are all from the Solution.java class in BA1 Project
	 * All print() methods are mine
	 * Remaining insert, doInsert, copyOut are all from the starter code
	 */
	static void insert(String key, int value, TwoThreeTree tree) {
	       // insert a key value pair into tree (overwrite existsing value
	       // if key is already present)
	    int h = tree.height;
	    if (h == -1) {
	    	LeafNode newLeaf = new LeafNode();
	    	newLeaf.guide = key;
	    	newLeaf.value = value; // So here, make an if clause, if guide is between range, increase value by priceIncrease 
	    	tree.root = newLeaf; 
	    	tree.height = 0;
	    }
	    else {
	    	WorkSpace ws = doInsert(key, value, tree.root, h);
	    	if (ws != null && ws.newNode != null) {
	    		// create a new root
	    		InternalNode newRoot = new InternalNode();
	    		if (ws.offset == 0) {
	    			newRoot.child0 = ws.newNode; 
	    			newRoot.child1 = tree.root;
	    		}
	    		else {
	    			newRoot.child0 = tree.root; 
	    			newRoot.child1 = ws.newNode;
	    		}
	    		resetGuide(newRoot);
	    		tree.root = newRoot;
	    		tree.height = h+1;
	    	}
	    }
	}
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

	       static void printAll(Node n, int change, BufferedWriter output) throws IOException{
	           if (n instanceof LeafNode){
	               LeafNode leaf = (LeafNode) n;
	               leaf.value += change;
	               //output.write(leaf.guide + " " + leaf.value);
	           }
	           else if (n instanceof InternalNode) {
	               printAll(((InternalNode)n).child0, change, output);
	               printAll(((InternalNode)n).child1, change, output);
	               if (((InternalNode)n).child2 != null){
	                   printAll(((InternalNode)n).child2, change, output);
	               }
	           }
	       }

	       static void printGE(Node p, String x, int change, BufferedWriter output) throws IOException{
	           
	           //basically, there's only the root of the tree, and if its bigger print it. Otherwise print nothing?? 
	           if (p instanceof LeafNode && p.guide.compareToIgnoreCase(x) >= 0){
	               LeafNode leaf = (LeafNode) p;
	               leaf.value += change;
	               //output.write(leaf.guide + " " + leaf.value + "\n");
	           } 
	           else if (p instanceof InternalNode) {
	               InternalNode internal = (InternalNode) p;
	               if (internal.guide.compareToIgnoreCase(x) >= 0){
	                   if (internal.child0.guide.compareToIgnoreCase(x) >= 0){
	                       printGE(internal.child0, x, change, output);
	                       printAll(internal.child1, change, output);
	                       if (internal.child2 != null){
	                           printAll(internal.child2, change, output);
	                       }
	                   }
	                   else if (internal.child1.guide.compareToIgnoreCase(x) >= 0){
	                       printGE(internal.child1, x, change, output);
	                       if (internal.child2 != null){
	                       printAll(internal.child2, change, output);
	                       }
	                   }
	                   else {
	                       printGE(internal.child2, x, change, output);
	                   } 
	               }
	           }
	       }
	       
	       static void printLE(Node p, String y, int change, BufferedWriter output) throws IOException{
	           if(p instanceof LeafNode && p.guide.compareTo(y) < 0){
	               LeafNode leaf = (LeafNode) p;
	               leaf.value += change;
	               //output.write(leaf.guide + " " + leaf.value + "/n");
	           }
	           else if(p instanceof InternalNode){
	               InternalNode internal = (InternalNode) p;
	               if(internal.guide.compareToIgnoreCase(y) <= 0){
	                   printAll(p, change, output);
	               }
	               else if(y.compareToIgnoreCase(internal.child0.guide) < 0){
	                   printLE(internal.child0, y, change, output);
	               }
	               else if(y.compareToIgnoreCase(internal.child1.guide) < 0){
	                   printAll(internal.child0, change, output);
	                   printLE(internal.child1, y, change, output);
	               }
	               else{
	                   printAll(internal.child0, change, output);
	                   printAll(internal.child1, change, output);
	                   printLE(internal.child2, y, change, output);
	               }
	           }
	       }
	       
	       
	       
	       static void printRange(Node p, String x, String y, int change, BufferedWriter output) throws IOException{
	           if(p instanceof LeafNode && p.guide.compareToIgnoreCase(x) >= 0 && p.guide.compareToIgnoreCase(y) <= 0){
	               LeafNode leaf = (LeafNode) p;
	               leaf.value += change;
	               //output.write(leaf.guide + " " + leaf.value); 
	               return;
	           }
	           else if (p instanceof InternalNode){
	               InternalNode internal = (InternalNode) p;
	               if (p.guide.compareToIgnoreCase(x) >= 0 && p.guide.compareToIgnoreCase(y) <= 0){
	                   if (internal.child0.guide.compareToIgnoreCase(x) >= 0){
	                       printGE(internal.child0, x, change, output);
	                       printAll(internal.child1, change, output);
	                       if (internal.child2 != null){
	                           printAll(internal.child2, change, output);
	                       }
	                   }
	                   else if (internal.child2 != null){
	                       if (internal.child1.guide.compareToIgnoreCase(x) > 0){
	                           printGE(internal.child1, x, change, output);
	                           printAll(internal.child2, change, output);
	                       } 
	                       else {
	                           printGE(internal.child2, x, change, output);
	                       }
	                   }
	                   else{
	                       printGE(internal.child1, x, change, output);
	                   } 
	               }
	               else if (internal.guide.compareTo(y) > 0) {
	                   if (internal.child0.guide.compareToIgnoreCase(y) > 0){
	                       printRange(internal.child0, x, y, change, output);
	                   }
	                   else if (x.compareToIgnoreCase(internal.child0.guide) <= 0){
	                       if (y.compareToIgnoreCase(internal.child1.guide) < 0){
	                           printGE(internal.child0, x, change, output);
	                           printLE(internal.child1, y, change, output);
	                       }
	                       else{
	                           printGE(internal.child0, x, change, output);
	                           printAll(internal.child1, change, output);
	                           printLE(internal.child2, y, change, output);
	                       }
	                   }
	                   else {
	                       if (y.compareToIgnoreCase(internal.child1.guide) < 0){
	                           printRange(internal.child1, x, y, change, output);
	                       
	                       }
	                       else if (x.compareToIgnoreCase(internal.child1.guide) <= 0){
	                           printGE(internal.child1, x, change, output);
	                           printLE(internal.child2, y, change, output);
	                       }
	                       else {
	                       printRange(internal.child2, x, y, change, output);
	                       }
	                   }
	               } 
	           }
	       }

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

	       static void insertNode(Node[] x, Node p, int sz, int pos) {
	       // insert p in x[0..sz) at position pos,
	       // moving existing extries to the right

	          for (int i = sz; i > pos; i--)
	             x[i] = x[i-1];

	          x[pos] = p;
	       }

	       static boolean resetGuide(InternalNode q) {
	       // reset q.guide, and return true if it changes.

	          String oldGuide = q.guide;
	          if (q.child2 != null)
	             q.guide = q.child2.guide;
	          else
	             q.guide = q.child1.guide;

	          return q.guide != oldGuide;
	       }


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



	    static class Node {
	       String guide;
	       int value;
	       // guide points to max key in subtree rooted at node
	    }

	    static class InternalNode extends Node {
	       Node child0, child1, child2;
	       // child0 and child1 are always non-null
	       // child2 is null iff node has only 2 children
	    }

	    static class LeafNode extends Node {
	       // guide points to the key

	    }

	    static class TwoThreeTree {
	       Node root;
	       int height;

	       TwoThreeTree() {
	          root = null;
	          height = -1;
	       }
	    }

	    static class WorkSpace {
	    // this class is used to hold return values for the recursive doInsert
	    // routine

	       Node newNode;
	       int offset;
	       boolean guideChanged;
	       Node[] scratch;
	    }
}

