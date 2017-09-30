import java.util.ArrayList;
import java.util.List;

public class DTNode {
	public int label = -1;
	public int positiveCounter;
	public int negetiveCounter;
	public String attributeToSplit;
	public double entropy;
	public DTNode leftChild;
	public DTNode rightChild;
	public int depth;
	public int nodeNumber;
	public List<UseAttribute> usedAttributes = new ArrayList<UseAttribute>();
	public List<String[]> dataSet = new ArrayList<String[]>();
}
