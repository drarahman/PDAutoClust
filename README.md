PDAutoClust: An Automated Identification Approach for Partial Discharge Detection Using Density Based Clustering Without User Inputs
Md Anisur Rahman, Li-Minn Ang, Sr., Member, IEEE, Kah Phooi Seng, Sr., Member, IEEE

Md Anisur Rahman is with School of Computing & Mathematics, Charles Sturt University, NSW 2795, Australia. Email: arahman@csu.edu.au
Li-minn Ang is with School of Science and Engineering, University of the Sunshine Coast, Australia. E-mail: lang@usc.edu.au
Kah Phooi Seng is with School of AI & Advanced Computing, Xi’an Jiaotong Liverpool University, Suzhou 215123, China and School of Computer Science, Queensland University of Technology, Brisbane, QLD 4000, Australia. E-mail: kahphooi.seng@qut.edu.au

Steps:
1. The PDAutoClust project is created using netbean 8.0.2 and JDK 8.
2. Datasets folder contains the files for each dataset. Please change the files path in Dataset.java file. 

The format of attNameInfo.txt file is discussed below:

n,X,0.584136353,0.793126712
n,Y,0.319833237,0.466221967
c,Class,2,1,2

The format of attNameInfo.txt file indicates that the dataset has two numerical attributes (X and Y) and a class attribute (i.e., Class). Note that, the class attribute is only used for evaluation purposes.

n indicates the type of the attribute is numerical and c indicates the type of the attribute is categorical. X is the name of the attribute, 0.584136353 is the minimum value of X and 0.793126712 is the maximum value of X.



The format of data.txt file is discussed below:

The first two columns of the data.txt are the values of attributes X and Y. The last column contains the label of each record.

3. An R source code is used to display the clusters. However, PDAutoClust also has a DisplayClust.java (based on JavaFx) file to display the clusters. The viualization using R looks better than Java Fx that's why I used R source code on Output.txt which is produced by PDAutoClust. Please check the file path in DisplayClust.java to make sure that program can read the file properly.


