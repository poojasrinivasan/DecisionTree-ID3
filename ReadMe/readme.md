# Decision Tree Algorithm - ID3 with Pruning

### Synopsis
The ID3 algorithm to create a decision tree has been implemented in this project. Following are the features of this project - 
  - Dynamically creates the Decision Tree.
  - Independent of Number of Attributes and Training Data
  - After creation, tree has been pruned to get better accuracy

### Motivation

This project is done as a class assignment for CS6375 - Machine Learning. We have developed this code to get the real-world view of the ID3 algorithm, which was studied in class before the assignment was given.

### Software’s Used
Following are the software’s used to develop this project - 
  - JavaSE 1.8
  - IDE: Eclipse Oxygen 4.7

Following are the Java packages used in the code - 
  - java.util.*
  - java.io.BufferedReader
  - java.io.BufferedWriter
  - java.io.File
  - java.io.FileOutputStream
  - java.io.FileReader
  - java.io.IOException

### How to Run the Code 

Following are the steps to run the code - 
  - Download the project folder - "Decision Tree" and save in the local
  - Start Eclipse Oxygen 4.7
  - Open the project folder by clicking File > Open File and by selecting the saved project folder.
  - Run the project by clicking Run > Run.
  - Code needs 4 inputs from the user: 
    - Training Dataset Path
    - Validation Dataset Path
    - Testing Dataset Path
    - Pruning Factor
  - Code will run and provide the output file in project directory as out.txt.
  - In the output file user will find the following data: 
    - Decision Tree
    - Following data for pre and post pruning: 
      - Number of nodes and leaf nodes
      - Number of Training Instances and attributes
      - Number of Validation Instances and attributes
      - Number of Test Instances and attributes
      - Accuracy by testing using - Training data, Validation data, Testing data

### References
  - https://en.wikipedia.org/wiki/ID3_algorithm
  - http://www.cise.ufl.edu/~ddd/cap6635/Fall-97/Short-papers/2.htm
  - https://en.wikipedia.org/wiki/Pruning_(decision_trees)
  - https://www.ibm.com/support/knowledgecenter/en/SSEPGG_9.5.0/com.ibm.im.visual.doc/t_pruning.html
  - http://www.cs.princeton.edu/courses/archive/spr07/cos424/papers/mitchell-dectrees.pdf
