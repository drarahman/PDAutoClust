#install.packages('cluster')
#install.packages("fpc")

library(cluster)
library(fpc)
dir()
getwd()
setwd("C:\\Research\\PDAutoClust\\Evaluation\\PDAutoClust\\Corona_bobina_1\\")
data <- read.csv(file="Output.txt", head=TRUE, sep=" ")
str(data)
X1=data$X
X2=data$Y
NumberOfCluster=length(data$Cluster)
plot(X1, X2, col= factor(data$Cluster), main="Clusters", type="p", xlab="X axis", ylab="Y axis")
