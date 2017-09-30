import java.util.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class DecisionTree implements Cloneable {

	// Declaring class variables
	static List<String[]> dataSet = new ArrayList<String[]>();
	static List<String> attributes = new ArrayList<String>();
	static List<String[]> dataSetForTesting = new ArrayList<String[]>();
	static List<String> attributesForTesting = new ArrayList<String>();
	static int numberOfTrainingInstances = 0;
	static int numberOfTrainingAttributes = 0;
	int numberOfNodes = 0;
	static int numberOfLeafNodes = 0;
	static double accuracy = 0.0;
	static List<Integer> parentOftheLeafs = new ArrayList<Integer>();

	// Main method
	public static void main(String[] args) throws Exception {

		// Class object to access non static methods
		DecisionTree obj = new DecisionTree();

		// Scanner object - to read input data from console
		Scanner sc = new Scanner(System.in);

		// Writing object - to print the decision tree and other o/p to file
		// "out.txt"
		File fout = new File("out.txt");
		FileOutputStream fos = new FileOutputStream(fout);
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

		// Start of the TRY Block
		try {

			// Reading Inputs

			
			 String pathOfTrainingData = sc.nextLine(); 
			 String pathOfValidationData = sc.nextLine(); 
			 String pathOfTestingData = sc.nextLine(); 
			 double pruningFactor = Double.parseDouble(sc.nextLine());
			 

			// Call populate data method to load the data-set from a CSV file to
			// the Class
			// variables
			obj.populateData(pathOfTrainingData);

			// Create the root node by calling createRoot method
			DTNode root = obj.createRoot();

			// Print the Decision Tree to the file
			obj.printDT(root, bw);

			// Used to give space between DT and Pre-Pruned data in the output
			// file
			bw.newLine();
			bw.newLine();
			bw.newLine();
			bw.newLine();

			// *****Print the Pre-Prunned data in the output file******

			String[] datasettype = new String[3];
			datasettype[0] = "training";
			datasettype[1] = "validation";
			datasettype[2] = "testing";
			String[] pathofdataset = new String[3];
			pathofdataset[0] = pathOfTrainingData;
			pathofdataset[1] = pathOfValidationData;
			pathofdataset[2] = pathOfTestingData;
			// For Training Data
			accuracy = obj.accuracyOfTheModel(pathofdataset[0], root);

			bw.write("Pre Prunned Accuracy");
			bw.newLine();
			bw.write("---------------------------");
			bw.newLine();
			bw.write("Total number of nodes in the tree = " + obj.countNodes(root));
			bw.newLine();
			bw.write("Number of leaf nodes in the tree = " + obj.numberOfLeafNodes(root));
			bw.newLine();
			obj.printData(root, bw, datasettype[0]);

			// For Validation Data
			accuracy = obj.accuracyOfTheModel(pathofdataset[1], root);
			obj.printData(root, bw, datasettype[1]);

			// For Testing Data
			accuracy = obj.accuracyOfTheModel(pathofdataset[2], root);
			obj.printData(root, bw, datasettype[2]);

			// Print successful message
			System.out.println("Successfully created and Tested the tree");

			// Pruning Testing
			bw.write("Post Prunned Accuracy");
			bw.newLine();
			bw.write("---------------------------");
			bw.newLine();
			double maxaccuracy = 0.0;
			DTNode maxroot = root;

			for (int index = 0; index < 3; index++) {
				for (int i = 0; i < 10; i++) {

					// Returns the set of leaves pruned
					Set<Integer> parentofleafs = obj.pruningDT(root, pruningFactor);
					// Clear the List
					dataSetForTesting.clear();
					attributesForTesting.clear();
					accuracy = obj.accuracyOfTheModel(pathofdataset[index], root);
					if (maxaccuracy < accuracy) {
						maxaccuracy = accuracy;
						maxroot = root;
					}

					int[] nodescount = obj.findCountOfNodesAfterPruning(maxroot);
					if (index == 0 && i == 9) {
						obj.printDT(maxroot, bw);
						bw.newLine();
						bw.write("Total number of nodes in the tree = " + nodescount[0]);
						bw.newLine();
						bw.write("Number of leaf nodes in the tree = " + nodescount[1]);
						bw.newLine();
					}
					// reverting parent of leaves index to -1
					for (Integer nodenumber : parentofleafs) {
						obj.findparticularParent(root, nodenumber);
					}
				}
				obj.printData(maxroot, bw, datasettype[index]);
			}

		}

		// Catch Statement Block
		catch (Exception ex) {
			System.out.println(ex);
		}

		// Finally Block to close the connections writer and reader
		finally {
			bw.close();
			sc.close();
		}
	}

	private void printData(DTNode root, BufferedWriter bw, String typeofDataSet) throws IOException {
		bw.write("Number of " + typeofDataSet + " instances = " + dataSetForTesting.size());
		bw.newLine();
		bw.write("Number of " + typeofDataSet + " attributes = " + attributesForTesting.size());
		bw.newLine();
		bw.write("Accuracy of the model on the " + typeofDataSet + " dataset = " + String.format("%.2f", accuracy * 100)
				+ "%");
		bw.newLine();
		bw.newLine();
		// Clear the List
		dataSetForTesting.clear();
		attributesForTesting.clear();
	}

	// Method to populate the Class variables with the input file
	private void populateData(String path) throws IOException {

		// Reader object
		BufferedReader reader = new BufferedReader(new FileReader(path));

		// Lines to store each line of the input from the Input file
		List<String> lines = new ArrayList<>();

		// Temporary variable for iteration
		String line = null;
		while ((line = reader.readLine()) != null) {
			if (line.equals(""))
				continue;
			else
				lines.add(line);
		}
		// Close reader
		reader.close();

		// Populate attribute and data set class variables
		attributes.addAll(Arrays.asList(lines.get(0).split(",")));
		attributes.remove(attributes.size() - 1);
		for (int i = 1; i < lines.size(); i++) {
			dataSet.add(lines.get(i).split(","));
		}

		// Calculate the size of data and the number of attributes
		numberOfTrainingInstances = dataSet.size();
		numberOfTrainingAttributes = attributes.size();
	}

	// Create Node method to initialize the root node, from where the tree will
	// expand
	private DTNode createRoot() {
		int positiveLabel = 0; // Counter to store the positive labels - 1
		int negetiveLabel = 0; // Counter to store the negative labels - 0

		// Loop to calculate each counter previously declared. It will help to
		// calculate
		// Entropy
		for (String[] data : dataSet) {
			String lastValue = data[data.length - 1];
			if (lastValue.equals("1")) {
				positiveLabel++;
			} else {
				negetiveLabel++;
			}
		}

		// Initialize the root
		DTNode root = new DTNode();
		numberOfNodes++; // Increase the number of node counter
		root.positiveCounter = positiveLabel;
		root.negetiveCounter = negetiveLabel;
		root.nodeNumber = numberOfNodes;
		root.entropy = calculateEntropy(positiveLabel, negetiveLabel); // Entropy
																		// Calculation
																		// for
																		// the
																		// root
		root.dataSet = dataSet;
		root.depth = 0;
		buildTree(root);
		return root;
	}

	// Recursive Method to Build the Decision Tree
	private void buildTree(DTNode root) {
		// Stopping Condition : Pure Node
		if (root.positiveCounter == 0 || root.negetiveCounter == 0) {
			if (root.positiveCounter == 0) {
				root.label = 0;
			}
			if (root.negetiveCounter == 0) {
				root.label = 1;
			}
			numberOfLeafNodes++; // Increase the leaf node counter
		}

		// Stopping Condition : No available nodes to split
		else if (root.usedAttributes.size() == attributes.size()) {
			if (root.positiveCounter >= root.negetiveCounter) {
				root.label = 1;
			} else {
				root.label = 0;
			}
			numberOfLeafNodes++; // Increase the leaf node counter
		}

		// Split and recursion call
		else {
			// Best attribute to split the tree
			int posOfBestAttribute = findBestAttribute(root);
			root.attributeToSplit = attributes.get(posOfBestAttribute);

			// Create two object for left & right Child of the parent
			DTNode leftChild = new DTNode();
			DTNode rightChild = new DTNode();

			// Calculate the positive and negative counter for both left and
			// right child
			for (String[] data : root.dataSet) {
				String classLabel = data[data.length - 1];
				// Left Child
				if (data[posOfBestAttribute].equals("0")) {
					leftChild.dataSet.add(data);
					if (classLabel.equals("1")) {
						leftChild.positiveCounter++;
					} else {
						leftChild.negetiveCounter++;
					}
				}
				// Right Child
				else if (data[posOfBestAttribute].equals("1")) {
					rightChild.dataSet.add(data);
					if (classLabel.equals("1")) {
						rightChild.positiveCounter++;
					} else {
						rightChild.negetiveCounter++;
					}
				}
			}

			// Store already used attributes from parent and the new attribute,
			// which is
			// used to split it
			leftChild.usedAttributes.addAll(root.usedAttributes);
			rightChild.usedAttributes.addAll(root.usedAttributes);
			leftChild.usedAttributes.add(new UseAttribute(attributes.get(posOfBestAttribute), "0"));
			rightChild.usedAttributes.add(new UseAttribute(attributes.get(posOfBestAttribute), "1"));

			// Calculate Entropy for each Child
			leftChild.entropy = calculateEntropy(leftChild.positiveCounter, leftChild.negetiveCounter);
			rightChild.entropy = calculateEntropy(rightChild.positiveCounter, rightChild.negetiveCounter);

			// Map Depth to the child taking the depth from root
			leftChild.depth = root.depth + 1;
			rightChild.depth = root.depth + 1;

			// Map Child to the parent
			root.leftChild = leftChild;
			numberOfNodes++; // Increase the Node Counter
			root.leftChild.nodeNumber = numberOfNodes;

			root.rightChild = rightChild;
			numberOfNodes++;// Increase the Node Counter
			root.rightChild.nodeNumber = numberOfNodes;

			// Recursion Call
			buildTree(root.leftChild);
			buildTree(root.rightChild);
		}
	}

	// Method to find the best attribute to Split, among the available ones
	private int findBestAttribute(DTNode parent) {

		// Calculate the available attributes
		List<String> availableAttributes = new ArrayList<String>();
		for (String attr : attributes) {
			availableAttributes.add(attr);
		}
		if (parent.usedAttributes != null) {
			for (UseAttribute attr : parent.usedAttributes) {
				if (attr != null) {
					availableAttributes.remove(attr.attribute);
				}
			}
		}

		// Calculate the position of the attributes using Information Gain and
		// return
		// the position
		int maxAttrPos = -1;
		double maxIG = 0.0;
		for (String attribute : availableAttributes) {
			int pos = getPositionOfTheAttribute(attribute);
			double IG = calculateInformationGain(parent, pos);
			if (IG >= maxIG) {
				maxIG = IG;
				maxAttrPos = pos;
			}
		}
		return maxAttrPos;
	}

	// Method to Calculate Information Gain using formula
	private double calculateInformationGain(DTNode parent, int attrPos) {
		double informationGain = 0.0;
		int leftPosCounter = 0;
		int leftNegCounter = 0;
		int rightPosCounter = 0;
		int rightNegCounter = 0;
		double leftEntropy = 0.0;
		double rightEntropy = 0.0;
		double wightedAvgEntropy = 0.0;
		for (String[] data : parent.dataSet) {
			if (data[attrPos].equals("0")) {
				if (data[data.length - 1].equals("1"))
					leftPosCounter++;
				else
					leftNegCounter++;
			} else {
				if (data[data.length - 1].equals("1"))
					rightPosCounter++;
				else
					rightNegCounter++;
			}
		}

		leftEntropy = calculateEntropy(leftPosCounter, leftNegCounter);
		rightEntropy = calculateEntropy(rightPosCounter, rightNegCounter);

		wightedAvgEntropy = ((double) (leftPosCounter + leftNegCounter) / (double) parent.dataSet.size()) * leftEntropy
				+ ((double) (rightPosCounter + rightNegCounter) / (double) parent.dataSet.size()) * rightEntropy;
		informationGain = parent.entropy - wightedAvgEntropy;

		return informationGain;
	}

	// Method to calculate the Entropy
	private double calculateEntropy(int posCounter, int negCounter) {
		double entropy = 0.0;
		if (posCounter != 0 && negCounter != 0) {
			double probForPositive = ((double) posCounter / (double) (posCounter + negCounter));
			double probForNegetive = ((double) negCounter / (double) (posCounter + negCounter));
			entropy = (-probForPositive * (Math.log(probForPositive) / Math.log(2)))
					+ (-probForNegetive * (Math.log(probForNegetive) / Math.log(2)));

		}
		return entropy;
	}

	// Method to get the position of the attributes in the main attribute list
	// by
	// passing the value
	private int getPositionOfTheAttribute(String attr) {
		int pos = -1;
		for (int i = 0; i < attributes.size(); i++) {
			if (attributes.get(i).equals(attr)) {
				pos = i;
				break;
			}
		}
		return pos;
	}

	// Recursive Printing method
	private void printDT(DTNode root, BufferedWriter bw) throws IOException {
		// Left child of the Root
		if (root.leftChild.label != -1) {
			for (int i = 0; i < root.depth; i++) {
				bw.write("|  ");
			}
			bw.write(root.leftChild.usedAttributes.get(root.depth).attribute + " = "
					+ root.leftChild.usedAttributes.get(root.depth).value + " : " + root.leftChild.label);
			bw.newLine();
		} else {
			for (int i = 0; i < root.depth; i++) {
				bw.write("|  ");
			}

			bw.write(root.leftChild.usedAttributes.get(root.depth).attribute + " = "
					+ root.leftChild.usedAttributes.get(root.depth).value + " : ");
			bw.newLine();
			// recursion call
			printDT(root.leftChild, bw);
		}

		// Right Child
		if (root.rightChild.label != -1) {
			for (int i = 0; i < root.depth; i++) {
				bw.write("|  ");
			}

			bw.write(root.rightChild.usedAttributes.get(root.depth).attribute + " = "
					+ root.rightChild.usedAttributes.get(root.depth).value + " : " + root.rightChild.label);
			bw.newLine();
		} else {
			for (int i = 0; i < root.depth; i++) {
				bw.write("|  ");
			}
			bw.write(root.rightChild.usedAttributes.get(root.depth).attribute + " = "
					+ root.rightChild.usedAttributes.get(root.depth).value + " : ");
			bw.newLine();

			// Recursion Call
			printDT(root.rightChild, bw);
		}
	}

	// Method to calculate the accuracy
	private double accuracyOfTheModel(String pathOfTestingDataset, DTNode root) throws IOException {

		int testingDatasetSize = 0;
		int correctCounter = 0;
		double accuracy = 0.0;

		// Read Testing DataSet
		BufferedReader reader = new BufferedReader(new FileReader(pathOfTestingDataset));
		List<String> lines = new ArrayList<>();
		String line = null;
		while ((line = reader.readLine()) != null) {
			if (line.equals(""))
				continue;
			else
				lines.add(line);
		}
		reader.close();

		// Initialize testing attribute list and dataSet
		attributesForTesting.addAll(Arrays.asList(lines.get(0).split(",")));
		attributesForTesting.remove(attributesForTesting.size() - 1);
		for (int i = 1; i < lines.size(); i++) {
			dataSetForTesting.add(lines.get(i).split(","));
		}
		testingDatasetSize = lines.size() - 1;
		String[] dataInstanceForTesting = new String[attributesForTesting.size()];

		// for each data for testing check the label from the tree with the
		// ground truth
		for (String[] data : dataSetForTesting) {
			String groundTruth = new String();

			for (int i = 0; i < data.length - 1; i++) {
				dataInstanceForTesting[i] = data[i];
			}
			groundTruth = data[data.length - 1];

			// method call to get the appropriate label from the Decision Tree
			int classLabel = getClassLabel(root, attributesForTesting, dataInstanceForTesting);

			// Compare ground truth with classLabel
			if (groundTruth.equals(Integer.toString(classLabel))) {
				correctCounter++;
			}
		}

		// Calculate the accuracy and return
		accuracy = (double) correctCounter / (double) testingDatasetSize;
		return accuracy;
	}

	// Method to return the calculated label from the DT for a particular Input
	private int getClassLabel(DTNode root, List<String> attributesForTesting, String[] dataInstanceForTesting) {
		DTNode tempRoot = new DTNode();
		tempRoot = root;
		while (tempRoot.label == -1) {
			int pos = -1;
			for (int i = 0; i < attributesForTesting.size(); i++) {
				if (attributes.get(i).equals(tempRoot.attributeToSplit)) {
					pos = i;
					break;
				}
			}
			if (pos == -1) {
				return -1;
			} else {
				if (dataInstanceForTesting[pos].equals("0"))
					tempRoot = tempRoot.leftChild;
				else
					tempRoot = tempRoot.rightChild;
			}
		}
		return tempRoot.label;
	}

	private Set<Integer> pruningDT(DTNode root, double pruningFactor) {

		int numberOfNodesToPrune = 0;

		DTNode tempRoot = root;
        Set<Integer> totalparentleavespruned=new HashSet<Integer>();
		numberOfNodesToPrune = (int) (pruningFactor * numberOfNodes);
		Set<Integer> parentofleafs = new HashSet<Integer>();
		while (numberOfNodesToPrune > 0) {
			parentOftheLeafs.clear();
			calculateParentOfLeafs(tempRoot);

			if (numberOfNodesToPrune <= parentOftheLeafs.size() * 2) {
				if (numberOfNodesToPrune % 2 != 0) {
					numberOfNodesToPrune = numberOfNodesToPrune + 1;
				}
				while (parentofleafs.size() != numberOfNodesToPrune / 2) {
					int index = new Random().nextInt(parentOftheLeafs.size());
					Integer randomeValue = parentOftheLeafs.get(index);
					parentofleafs.add(randomeValue);
				}
				for (int no : parentofleafs) {
					deleteNode(tempRoot, no);
					totalparentleavespruned.add(no);
					numberOfNodesToPrune -= 2;
				}
			} else {

				for (int no : parentOftheLeafs) {
					if (numberOfNodesToPrune > 0) {
						deleteNode(tempRoot, no);
						totalparentleavespruned.add(no);
						numberOfNodesToPrune -= 2;
					}
				}
			}
		}

		try {
			// Clear the List
			dataSetForTesting.clear();
			attributesForTesting.clear();
			accuracy = accuracyOfTheModel("test_set1.csv", tempRoot);
		} catch (Exception e) {
			System.out.println(e);
		}
		return totalparentleavespruned;
	}

	private void findparticularParent(DTNode root, int numberofParent) {
		if (root.leftChild != null && root.rightChild != null) {
			if (root.nodeNumber == numberofParent) {
				root.label = -1;

			} else {
				findparticularParent(root.leftChild, numberofParent);
				findparticularParent(root.rightChild, numberofParent);
			}
		}

	}

	private void deleteNode(DTNode root, int numberToDelete) {
		if (root.leftChild != null && root.rightChild != null) {
			if (root.nodeNumber == numberToDelete) {
				if (root.positiveCounter >= root.negetiveCounter) {
					root.label = 1;
				} else {
					root.label = 0;
				}

			} else {
				deleteNode(root.leftChild, numberToDelete);
				deleteNode(root.rightChild, numberToDelete);
			}
		}

	}

	private void calculateParentOfLeafs(DTNode root) {
		if (root.leftChild != null && root.rightChild != null) {
			if (root.leftChild.label != -1 && root.rightChild.label != -1) {
				parentOftheLeafs.add(root.nodeNumber);
			} else {
				calculateParentOfLeafs(root.leftChild);
				calculateParentOfLeafs(root.rightChild);
			}
		}

	}
	// For Leafs

	private int numberOfLeafNodesPruned(DTNode root) {
		{
			if (root == null)
				return 0;

			if (root.label != -1 && root.leftChild != null && root.rightChild != null && root.leftChild.label != -1
					&& root.rightChild.label != -1)
				return 1;
			else
				return numberOfLeafNodesPruned(root.leftChild) + numberOfLeafNodesPruned(root.rightChild);
		}
	}
	// For Leafs

	private int numberOfLeafNodes(DTNode root) {

		{
			if (root == null)
				return 0;
			if (root.leftChild == null && root.rightChild == null)
				return 1;
			else
				return numberOfLeafNodes(root.leftChild) + numberOfLeafNodes(root.rightChild);
		}
	}

	// Total Nodes
	private int countNodes(DTNode root) {
		int c = 1;

		if (root == null)
			return 0;
		else {
			c += countNodes(root.leftChild);
			c += countNodes(root.rightChild);
			return c;
		}
	}

	private int[] findCountOfNodesAfterPruning(DTNode root) {
		int nodesCount[] = new int[2];
		DTNode tempRoot = root;
		int totalnodes = 0, leafnodes = 0;
		leafnodes = numberOfLeafNodes(tempRoot);
		leafnodes = numberOfLeafNodes(tempRoot) - numberOfLeafNodesPruned(tempRoot) * 2
				+ numberOfLeafNodesPruned(tempRoot);
		totalnodes = countNodes(tempRoot);
		totalnodes = totalnodes - numberOfLeafNodesPruned(tempRoot) * 2;
		nodesCount[0] = totalnodes;
		nodesCount[1] = leafnodes;
		return nodesCount;

	}
}
