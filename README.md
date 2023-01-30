**Citation:**
**M. A. Rahman, L. M. Ang, and K. P. Seng, An Automated Identification Approach for Partial Discharge Detection Using Density-Based Clustering Without User Inputs, IEEE Transactions on Artificial Intelligence, vol. X, no. X, pp. XX–XX, 2023.**

https://ieeexplore.ieee.org/document/10018452

**Title of the paper**: PDAutoClust: An Automated Identification Approach for Partial Discharge Detection Using Density Based Clustering Without User Inputs

Authors:
Md Anisur Rahman is with School of Computing, Mathematics & Engineering, Charles Sturt University, NSW 2795, Australia. Email: arahman@csu.edu.au;
Li-minn Ang is with School of Science and Engineering, University of the Sunshine Coast, Australia. E-mail: lang@usc.edu.au;
Kah Phooi Seng is with School of AI & Advanced Computing, Xi’an Jiaotong Liverpool University, Suzhou 215123, China and School of Computer Science, Queensland University of Technology, Brisbane, QLD 4000, Australia. E-mail: kahphooi.seng@qut.edu.au

**Abstract**

A recently published state-of-the-art density-based clustering technique called ICFSFDP for partial discharge detection requires various sensitive user-defined input parameters. This paper presents a parameter-free clustering technique called PDAutoClust for partial discharge detection. The PDAutoClust algorithm can produce high-quality clusters for partial discharge datasets without requiring user-defined input parameters. PDAutoClust produces high-quality clustering results by utilizing a vein-based density clustering approach. A cluster vein is produced by using multivariate kernel density estimation (KDE) and a unique neighborhood set (UNS). We compared the performance of PDAutoClust against ICFSFDP and seven other state-of-the-art density-based and non-density-based clustering techniques by using four partial discharge datasets in terms of adjusted rand index, normalized mutual information, F1-score, and purity. Another contribution of the paper is a novel merging technique used with PDAutoClust to merge small non-viable clusters that a clustering technique may produce. PDAutoClust produces the final clusters for a dataset by merging the non-viable clusters that a clustering technique may produce. We also evaluate the performance of PDAutoClust with a merging technique versus PDAutoClust without the merging technique using four datasets. Simulation results for PDAutoClust with the merging technique show good performance compared to ICFSFDP and seven other state-of-the-art clustering techniques. We also performed an ablation study to demonstrate the importance of the steps involved in PDAutoClust.

**Information:**
1. The PDAutoClust project is created using netbean 8.0.2 and JDK 8.
2. Dataset contains four folders for four datasets. Each dataset folder has two files namely attNameInfo.txt and data.txt. Please check the file path in Dataset.java. 

    public static String dataPATH="C:\\Research\\Datasets\\Corona_bobina_1\\";    
    public static String outputPath="C:\\Research\\PDAutoClust\\Evaluation\\Corona_bobina_1\\";
    
    public static String Data=dataPATH+"data.txt";        
    public static String attributeName=dataPATH+"attNameInfo.txt";
    

The format of a attNameInfo.txt file is discussed below:

n,X,0.584136353,0.793126712 

n,Y,0.319833237,0.466221967 

c,Class,2,1,2 

The format of attNameInfo.txt file indicates that the dataset has two numerical attributes (X and Y) and a class attribute (i.e., Class). Note that, the class attribute is only used for evaluation purposes.

n indicates the type of the attribute is numerical and c indicates the type of the attribute is categorical. X is the name of the attribute, 0.584136353 is the minimum value of X and 0.793126712 is the maximum value of X.

The format of a data.txt file is discussed below:

The first two columns of the data.txt are the values of attributes X and Y. The last column contains the label of each record.

3. An R source code (ShowCluster.R) is used to display the clusters. However, PDAutoClust also has a DisplayClust.java (based on JavaFx) file to display the clusters. It seems visualization using R looks better than Java Fx that's why we used ShowCluster.R to display the clusters in Output.txt file. To display the clusters using Java Fx, please check the file path in DisplayClust.java to make sure that program can read the file properly.

4. RunMain.java contains the main method
5. The program produces two files namely output.txt and Evaluation.csv.

