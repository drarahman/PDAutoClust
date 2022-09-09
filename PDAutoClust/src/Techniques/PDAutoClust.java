/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
*/
package Techniques;
 
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Vector;
import java.io.BufferedWriter;
import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 *
 * @author Dr Md Anisur Rahman
 * School of Computing, Mathematics and Engineering, 
 * Charles Sturt University, Australia
 * Email: arahman@csu.edu.au; javedcse@gmail.com
 * Date: 17/06/2022
 */

/**
 *
 * PDAutoClust Algorithm
 */
public class PDAutoClust{
    Evaluation evaluation;
    private StringBuilder evaluationHeading=new StringBuilder();
    Map <Integer, Boolean> neighborStatus=new HashMap();
    Map<Integer, Boolean> visitStatus = new HashMap();
    public String outputFile;    
    public PDAutoClust(){
        evaluation=new Evaluation(); 
    }
    
    //PDAutoClust method
    public void performPDAutoClust(double[][]data, String[] recordLabel, String[]attrType, String[]attrName, String[] classValue, String[][]classAttributeInfo, String dataset){

        String Mode="";
        String Path=Dataset.outputPath;        
        StringBuilder Evaluation=new StringBuilder();          
        System.out.println("Running PDAutoClust.....");
           
        //Euclidean distance
       double [][] r2rDistance=r2rEuclideanDistance(data, attrType, Path, Mode); 
        //Kernel Bandwidth
        double[] kernelBandwidth=kernelBandwidth(data, attrType);
        //Kernel density
        double[] kernelDensity=multivariateKernelDensity(r2rDistance, kernelBandwidth, data, attrType);
        //Kernel similarity
        double[][] kernelSimilarity=kernelSimilarity(r2rDistance, kernelDensity);

        Vector<Integer> recordIndex=new Vector();
        for(int i=0;i<r2rDistance.length;i++){
            recordIndex.addElement(i);      
        } 
        
        //PDAutoClust core method
        Mode="PDAutoClust"; 
        Vector<Integer>[] recordInCluster=pdAutoClustCoreMethod(data, recordLabel, recordIndex, Path, Mode, r2rDistance, kernelDensity, kernelSimilarity, attrType, attrName, classValue, dataset);
        
        evaluationHeading.append("ARI");
        evaluationHeading.append(",");  
        evaluationHeading.append("NMI");
        evaluationHeading.append(",");
        evaluationHeading.append("F-measure");
        evaluationHeading.append(",");  
        evaluationHeading.append("Purity");
        StringBuilder result=clusterEvaluation(recordInCluster, data, attrType, recordLabel, classValue, Path, Mode, dataset);
        Evaluation.append(evaluationHeading);
        Evaluation.append("\n");
        Evaluation.append(result);
        Mode="Evaluation";
        writeResult(Path, Mode, Evaluation);
        
        //Write cluster info in an Output.txt file for visualization
        writeClusteringOutput(data, attrType, attrName, recordInCluster, Path, Mode, dataset); 
 
    }
    
    //write clusters in a file and display in a GUI
    public void writeClusteringOutput(double[][]data, String[]attrType, String[] attrName, Vector<Integer>[] recordInCluster, String Path, String Mode, String dataset){
        StringBuilder clusterWithRecord=new StringBuilder();
        for(int i=0;i<attrType.length;i++){
            clusterWithRecord.append(attrName[i]);
            clusterWithRecord.append(" ");            
        }
  
        
        clusterWithRecord.append("Cluster");
        clusterWithRecord.append("\n");
 
        for(int i=0;i<recordInCluster.length-1;i++){
            if(!recordInCluster[i].isEmpty()){
                for(int j=0;j<recordInCluster[i].size();j++){
                    for(int a=0; a<data[0].length-1;a++){
                        clusterWithRecord.append(data[recordInCluster[i].elementAt(j)][a]);
                        clusterWithRecord.append(" ");
                    }
                    clusterWithRecord.append(i+1);
                    clusterWithRecord.append("\n");
                }
            }            
        }
        

        if(!recordInCluster[recordInCluster.length-1].isEmpty()){
            for(int j=0;j<recordInCluster[recordInCluster.length-1].size()-1;j++){
                for(int a=0; a<data[0].length-1;a++){
                    clusterWithRecord.append(data[recordInCluster[recordInCluster.length-1].elementAt(j)][a]);
                    clusterWithRecord.append(" ");
                }
                clusterWithRecord.append(recordInCluster.length);
                clusterWithRecord.append("\n");
            }
            

            for(int a=0; a<data[0].length-1;a++){               
                clusterWithRecord.append(data[recordInCluster[recordInCluster.length-1].elementAt(recordInCluster[recordInCluster.length-1].size()-1)][a]);
                clusterWithRecord.append(" ");
            }
            clusterWithRecord.append(recordInCluster.length);

        }            
        try{
            BufferedWriter fw = new BufferedWriter(new FileWriter(new File(Path+"Output.txt")));
            fw.write(clusterWithRecord.toString());
            fw.flush();
            fw.close();
        }
        catch (IOException e) {
        }
        //Display Clusters
        javafx.application.Application.launch(DisplayCluster.class);
        
    }
   
    //calculate Kernel Density
    public double[] multivariateKernelDensity(double [][] r2rD, double[] kernelBandwidth, double[][]data, String[]attrType){
       
        double [] kernelDensity=new double[r2rD.length];   
 
        double KDE=0;
        double piConstant=Math.pow(1/Math.sqrt(2*3.1415),attrType.length);
 
        double distance=0;
        double bandwidth=0;    
        double KDB=0;

        
        for(int i=0;i<data.length;i++){
            KDE=0;
            for(int j=0;j<data.length;j++){         
                distance=0;
                for(int a=0;a<attrType.length;a++){
                        bandwidth=kernelBandwidth[a];                        
                        distance+=(-(1.0/2.0)*(Math.pow(((data[i][a]-data[j][a])/(bandwidth)),2)/2));

                }                

                KDB=piConstant*(Math.pow(2.718281, distance));
                
                for(int b=0;b<kernelBandwidth.length;b++){
                  KDB=KDB/kernelBandwidth[b]; 
                }
                KDE=KDE+KDB;
                

            }
  
            KDE=(1.0/data.length)*KDE;
 
 
            kernelDensity[i]=KDE;
        }
        
        return kernelDensity;
    }
    
    //calculate kernel bandwidth
    double[] kernelBandwidth(double[][] data, String[]attrType){
        double []kernelBandwidth=new double[attrType.length];
        
        double []stdev=standardDeviation(data, attrType);      
 
        for(int a=0;a<attrType.length;a++){
            if("n".equals(attrType[a])){               
                kernelBandwidth[a]=stdev[a]*Math.pow((4.0/((attrType.length+2)*data.length)),(1.0/(attrType.length+4.0)));               
          
            }
        }      
        

        return kernelBandwidth;
    }
    
    //calculate standard deviation
    double[] standardDeviation(double[][] data, String[]attrType){      
        double []stdev=new double[attrType.length];
 
        double average=0;
        double sd = 0;
 
        for(int a=0;a<attrType.length;a++){
            if("n".equals(attrType[a])){
            average=0;
            for(int i=0;i<data.length;i++){
                average+=data[i][a];           
            }
            average=average/data.length;
 
            sd = 0;
            for(int i=0; i<data.length; i++){
                sd += Math.pow((data[i][a] - average),2);
            }
            stdev[a]=Math.sqrt(sd/data.length);
            }
        }
 
 
        return stdev;
    }
    //calculate euclidean distance
    public double[][] r2rEuclideanDistance(double[][] data, String[] attrType, String Path, String Mode){
        double [][] r2rD=new double[data.length][data.length];
        double distance=0;

        for(int i=0;i<data.length;i++){
            for(int j=0;j<data.length;j++){                
                if(j>i){
                    distance=0;
                    for(int k=0;k<attrType.length;k++){                           
                        distance+=(Math.pow((data[i][k]-data[j][k]),2));
                    }
                    distance=Math.sqrt(distance);
                    r2rD[i][j]=distance; 
                }
            }
        }
 
        for(int i=0;i<r2rD.length;i++){
            for(int j=0;j<r2rD.length;j++){
                if(j>i){
                    r2rD[j][i]=r2rD[i][j];
                }
            }           
        }         
 
        return r2rD;
    }
         
    //calculate Kernel Similarity
    public double[][] kernelSimilarity(double [][] r2rD, double[]kernelDensity){
        double [][] kernelSimilarity=new double[kernelDensity.length][kernelDensity.length];   

        for(int i=0;i<kernelDensity.length;i++){
            for(int j=0;j<kernelDensity.length;j++){               
                kernelSimilarity[i][j]=(1.0+Math.abs(kernelDensity[i]-kernelDensity[j]))*r2rD[i][j];             
            }           
        }


        return kernelSimilarity;
    }
 
    //PDAutoClust core method
    public Vector<Integer>[] pdAutoClustCoreMethod(double[][]data,String[]recordLabel, Vector<Integer> recordIndex, String Path, String Mode, double[][] r2rDistance, double[] kernelDensity, double[][] kernelSimilarity, String[]attrType, String[]attrName, String[] classValue, String dataset){
        Vector<Vector<Integer>> Cluster=new Vector<Vector<Integer>>();
        Vector<Integer> recordIndexInCluster;
        Vector<Integer> neighborRecord;
        Vector<Integer> neighborOfNeighbor;
        boolean Continue=true;      
        Map<Integer, Boolean> clusterStatus = new HashMap();        
        for(int i=0;i<data.length;i++){
            visitStatus.put(i,false);
            clusterStatus.put(i, false);
        }

        Map <Integer, Double> kernelDensityMap=kernelDensityMap(kernelDensity, recordIndex);
        Map <Integer, Integer> unsDensityMap=unsDensityMap(r2rDistance, recordIndex, dataset, Path);
        int densestRecord=0;
        Set<Integer> neighborSet=new TreeSet<>();
        boolean neighborOfNeighborSearchContinue=true; 

        while(Continue==true){
            neighborOfNeighborSearchContinue=true; 
            Continue=visitStatus.containsValue(false);            
            if(Continue==false){ //Continue=false when there is no false in VisitStatus
                break;
            }
            recordIndexInCluster=new Vector();
            densestRecord=findNotVisitedUCNDensestRecord(unsDensityMap, visitStatus, recordIndex);
            neighborRecord=findNeighborOfDensestRecordWithoutRadius(r2rDistance, densestRecord, recordIndex);            
            if(neighborRecord.size()<2){
                break;
            }
            visitStatus.replace(densestRecord, true);
            recordIndexInCluster.addElement(densestRecord); 
            neighborStatus=new HashMap();
            neighborSet=new TreeSet<>();
            neighborSet.addAll(neighborRecord);  
            while(neighborOfNeighborSearchContinue==true){//search for veins
                for(int j=0;j<neighborRecord.size();j++){
                    visitStatus.replace(neighborRecord.elementAt(j), true);
                }
                //Kernel Similarity using IVDPC similarity
                neighborOfNeighbor=findVeinsOfNeighborBasedOnUNS(kernelDensityMap, kernelSimilarity, r2rDistance, neighborRecord, recordIndex);
                neighborStatus=new HashMap();                
                neighborSet.addAll(neighborOfNeighbor); //growing the size of the initial cluster based on the neighbors
                if(neighborOfNeighbor.isEmpty()){
                    neighborOfNeighborSearchContinue=false;
                }                
                neighborRecord=neighborOfNeighbor; 
            }
            recordIndexInCluster.addAll(neighborSet);
            Cluster.add(recordIndexInCluster); 
            Continue=visitStatus.containsValue(false);                  
        }        

        Vector<Integer>[] veinCluster= new Vector[Cluster.size()];
        for(int i=0;i<veinCluster.length;i++){
            veinCluster[i]=new Vector();
        }
        for(int i=0;i<veinCluster.length;i++){
             for(int j=0;j<Cluster.get(i).size();j++){
                 veinCluster[i].addElement(Cluster.get(i).get(j));
             }           
        }
        //Partition the remaining records of a dataset
        Vector<Integer>[] initialCluster=partitionRemainingRecord(data, r2rDistance,  recordIndex, veinCluster, attrType, attrName, Path, Mode, dataset);
        //Merging small clusters
        Vector<Integer>[] mergeClusterFinal=mergingCluster(data, attrType, attrName, dataset, r2rDistance, recordIndex, initialCluster, unsDensityMap, Path, Mode);

        return mergeClusterFinal;
    }
   
    //uns density map
    public Map<Integer, Integer> unsDensityMap(double[][] r2rDistance, Vector<Integer>recordIndex, String dataset, String Path){ 
        Map<Integer, Integer> density = new HashMap();
        double distance=0;
        int count=0;

        StringBuilder densityInfo=new StringBuilder();
        densityInfo.append("Record");
        densityInfo.append(",");
        densityInfo.append("UNS Density");
        densityInfo.append("\n");   

        boolean status=false;

        for(int i=0;i<recordIndex.size();i++){ //will calculate for every record
            count=0;         
            for(int j=0;j<recordIndex.size();j++){ // every record    
                if(i!=j){
                    status=true;
                    distance=r2rDistance[recordIndex.elementAt(i)][recordIndex.elementAt(j)];                  
                    for(int k=0;k<recordIndex.size();k++){ // checking if j has minimum distance with any k, if not then j has the minimum distance with i
                        if(j!=k){
                            if(r2rDistance[recordIndex.elementAt(k)][recordIndex.elementAt(j)]<distance){
                                status=false;
                                break;
                            }
                        }
                    }

                    if(status==true){
                       count++;
                    }
                }             
            }

            density.put(recordIndex.elementAt(i), count); 

            densityInfo.append(recordIndex.elementAt(i));
            densityInfo.append(",");
            densityInfo.append(count);
            densityInfo.append("\n");   
        }
            
        return density;
    }
    
    //kernel density map
    public Map<Integer, Double> kernelDensityMap(double []kernelDensity, Vector<Integer>recordIndex){ 
            Map<Integer, Double> Density = new HashMap();      

            for(int i=0;i<recordIndex.size();i++){           
                Density.put(recordIndex.elementAt(i), kernelDensity[i]); 

            }


        return Density;
    }
    
    //find not visited densest record
    public int findNotVisitedUCNDensestRecord(Map<Integer, Integer> density, Map<Integer, Boolean> vStatus, Vector<Integer> recordIndex){ 

            int densestRecord=0;
            double maxDensity=0;

            for(int i=0;i<vStatus.size();i++){
                if(vStatus.get(i)==false){
                    if(density.get(recordIndex.elementAt(i))>=maxDensity){               
                        maxDensity=density.get(recordIndex.elementAt(i));
                        densestRecord=recordIndex.elementAt(i);
                    }
                }
            }

        return densestRecord;
    }

    //find the veins of the clusters
    public Vector<Integer> findVeinsOfNeighborBasedOnUNS(Map<Integer, Double> kernelDensityMap, double[][] kernelSimilarity, double[][] r2rDistance, Vector<Integer> neighborRecord, Vector<Integer> recordIndex){
        Vector<Integer> neighborOfNeighbor=new Vector(); 
        boolean status=true;
        boolean connectionStatus=true;
        double min=0;
        int neighborIndex=0;
        double KS=0;        
        for(int j=0;j<recordIndex.size();j++){
            if(visitStatus.get(recordIndex.elementAt(j))==false){
                if(!Objects.equals(recordIndex.elementAt(j), neighborRecord.elementAt(0))){
                    min=r2rDistance[recordIndex.elementAt(j)][neighborRecord.elementAt(0)];
                    neighborIndex=neighborRecord.elementAt(0);                    
                    for(int c=1;c<neighborRecord.size();c++){ //which record of neighbour has the minimum distance with record j
                        if(!Objects.equals(recordIndex.elementAt(j), neighborRecord.elementAt(c))){
                            if(r2rDistance[recordIndex.elementAt(j)][neighborRecord.elementAt(c)]<min){
                                min=r2rDistance[recordIndex.elementAt(j)][neighborRecord.elementAt(c)];
                                neighborIndex=neighborRecord.elementAt(c);
                            }
                        }
                    }
                }
                status=true; 
                connectionStatus=true;
                for(int k=0;k<recordIndex.size();k++){ // distance between record j and any other record is not less than min
                    if(!Objects.equals(recordIndex.elementAt(j), recordIndex.elementAt(k))){                                               
                        if(!Objects.equals(recordIndex.elementAt(k), neighborIndex)){
                            if(r2rDistance[recordIndex.elementAt(j)][recordIndex.elementAt(k)]<min){
                                status=false;                          
                                break;
                            } 
                        }
                    }
                }
                if(status==false){
                    if(neighborIndex!=recordIndex.elementAt(j)){ //when j is not connected 
                        KS=kernelSimilarity[neighborIndex][recordIndex.elementAt(j)];
                        if((kernelDensityMap.get(neighborIndex)<kernelDensityMap.get(recordIndex.elementAt(j)))){
                            for(int s=0;s<kernelSimilarity.length;s++){
                                if(visitStatus.get(s)==false){
                                    if(s!=recordIndex.elementAt(j)){
                                        if(kernelSimilarity[recordIndex.elementAt(j)][s]<KS){
                                            connectionStatus=false;
                                            break;
                                        }
                                    }
                                }
                            }
                            if(connectionStatus==true){                                
                                neighborOfNeighbor.addElement(recordIndex.elementAt(j));
                            }
                        }
                    }                    
                }
                else{                    
                    neighborOfNeighbor.addElement(recordIndex.elementAt(j));
                }
            }
        }

        return neighborOfNeighbor;       
    }

    //partition the remaining records
    public Vector<Integer>[] partitionRemainingRecord(double[][]data, double[][] r2rDistance, Vector<Integer> recordIndex,  Vector<Integer>[] veinsOfCluster, String[]attrType, String[]attrName, String Path, String Mode, String dataset){
            Vector<Integer>[] recordInCluster= new Vector[veinsOfCluster.length];
            for(int i=0;i<recordInCluster.length;i++){
                recordInCluster[i]=new Vector();
            }     

            double min=0;     
            int clusterIndex=0;
            int count=0;
            for(int j=0;j<recordIndex.size();j++){
                if(visitStatus.get(recordIndex.elementAt(j))==false){
                    clusterIndex=0;                
                    min=r2rDistance[recordIndex.elementAt(j)][veinsOfCluster[0].elementAt(0)];  
                    for(int a=0;a<veinsOfCluster.length;a++){                                      
                        for(int c=0;c<veinsOfCluster[a].size();c++){
                            if(r2rDistance[recordIndex.elementAt(j)][veinsOfCluster[a].elementAt(c)]<min){
                                min=r2rDistance[recordIndex.elementAt(j)][veinsOfCluster[a].elementAt(c)]; 
                                clusterIndex=a;
                            }
                        }                
                    }
                    count++;               
                    recordInCluster[clusterIndex].addElement(recordIndex.elementAt(j));
                }
            }      
    

            Vector<Integer>[] recordInClusterFinal= new Vector[veinsOfCluster.length];
            for(int i=0;i<veinsOfCluster.length;i++){
                recordInClusterFinal[i]=new Vector();
            }

            for(int i=0;i<recordInClusterFinal.length;i++){
                for(int v=0;v<veinsOfCluster[i].size();v++){
                    recordInClusterFinal[i].addElement(veinsOfCluster[i].elementAt(v));
                }
                for(int r=0;r<recordInCluster[i].size();r++){
                    recordInClusterFinal[i].addElement(recordInCluster[i].elementAt(r));
                }
            }



            int totalrecord=0;
            for(int i=0;i<recordInClusterFinal.length;i++){
                totalrecord+=recordInClusterFinal[i].size();
            }
            
        return recordInClusterFinal;       
    }

    //Merge the clusters based on shared region
    public Vector<Integer>[] mergingCluster(double[][] data, String[]attrType, String[]attrName, String dataset, double[][] r2rDistance, Vector<Integer> recordIndex, Vector<Integer>[] recordInCluster, Map <Integer, Integer> unsDensityMap, String Path, String Mode){
        boolean mergeStatus=true;
        Vector<Integer>[] recordInClusterSharedZone= new Vector[recordInCluster.length];        
        for(int i=0;i<recordInClusterSharedZone.length;i++){
            recordInClusterSharedZone[i]=new Vector();
        }

        Vector<Integer>[] mergingCluster;

        double maxShared=0;
        int Index=0;
        int iteration=0;
        int cIndex=0;
        boolean search=true;
        int size=0;
        double[][] sharedZoneInfo;        
        int tempIndex=0;
        int merging=0;
        Vector <Double> radiusForSharedRegion=new Vector();
        Set<Integer> finalMergingSet;
        Set<Integer> mergeSet;
        Set<Integer> mergeAllSet;        
        while(mergeStatus==true){
            mergingCluster=new Vector[recordInCluster.length];        
            for(int i=0;i<mergingCluster.length;i++){
                mergingCluster[i]=new Vector();
            }        
            maxShared=0; 
            Index=0;
            cIndex=0;
            tempIndex=0;
            search=true;            
            mergeSet=new HashSet<Integer>();           
            mergeAllSet=new HashSet<Integer>();       
            merging=0;
            if(iteration==0){
                sharedZoneInfo=findSharedRegion(r2rDistance, recordInCluster, unsDensityMap, Path, Mode, iteration);
            }
            else{
                sharedZoneInfo=findSharedRegionNext(r2rDistance, recordInCluster, unsDensityMap, Path, Mode, iteration, radiusForSharedRegion);
            }
            finalMergingSet = new HashSet<Integer>();           
            for(int s=0;s<sharedZoneInfo.length;s++){   //storing the distinct cluster in the FinalMergingSet       
                finalMergingSet.add((int)sharedZoneInfo[s][2]);        
            }
            if(finalMergingSet.size()>2){
                while(search==true){
                    Index=0;               
                    for(int i=0;i<sharedZoneInfo.length;i++){ //the following block of code finds the cluster that has the max shared region with another cluster SharedZoneInfo[i][7]
                        if(mergeAllSet.contains((int)sharedZoneInfo[i][0])==false && mergeAllSet.contains((int)sharedZoneInfo[i][2])==false){
                            if(sharedZoneInfo[i][3]>maxShared){
                                maxShared=sharedZoneInfo[i][3];
                                Index=i;
                            }
                        }
                    }
                    if(mergeAllSet.contains((int)sharedZoneInfo[Index][0])==false && mergeAllSet.contains((int)sharedZoneInfo[Index][2])==false){
                        mergeSet.add((int)sharedZoneInfo[Index][0]);
                        mergeSet.add((int)sharedZoneInfo[Index][2]);                    
                        mergeAllSet.add((int)sharedZoneInfo[Index][0]); //MergeAllSet helps to identify if the cluster is already merged
                        mergeAllSet.add((int)sharedZoneInfo[Index][2]); 
                    }
                    if(mergeSet.size()>0){ //checking if an iteration found some clusters to be merged
                        mergingCluster[tempIndex].addAll(mergeSet); 
                        merging++;
                        mergeSet=new HashSet<Integer>();
                        maxShared=0; 
                        tempIndex++;
                    }               
                    else{
                        search=false;
                    }
                }
                if(merging>0){
                    radiusForSharedRegion=new Vector();
                    size=0;
                    for(int i=0;i<mergingCluster.length;i++){
                        if(!mergingCluster[i].isEmpty()){
                            size=size+1;
                        }                    
                    }
                    recordInClusterSharedZone=new Vector[size+(recordInCluster.length-mergeAllSet.size())];  // RecordInClusterSharedZone will contain all the clusters after merging             
                    for(int i=0;i<recordInClusterSharedZone.length;i++){
                        recordInClusterSharedZone[i]=new Vector();
                    }

                    for(int i=0;i<mergingCluster.length;i++){ //enter merged clusters into RecordInClusterSharedZone and max radius into RadiusForSharedRegion
                        if(!mergingCluster[i].isEmpty()){
                            for(int j=0;j<mergingCluster[i].size();j++){ 
                                for(int c=0;c<recordInCluster[mergingCluster[i].elementAt(j)].size();c++){
                                    recordInClusterSharedZone[cIndex].addElement(recordInCluster[mergingCluster[i].elementAt(j)].elementAt(c)); 
                                }
                            }
                            if(sharedZoneInfo[mergingCluster[i].elementAt(0)][4]>sharedZoneInfo[mergingCluster[i].elementAt(1)][4]){
                                radiusForSharedRegion.add(cIndex, sharedZoneInfo[mergingCluster[i].elementAt(0)][4]); 
                            }

                            else{
                                radiusForSharedRegion.add(cIndex, sharedZoneInfo[mergingCluster[i].elementAt(1)][4]); 
                            }
                            cIndex++;
                        }                    
                    }

                    for(int i=0;i<recordInCluster.length;i++){//enter non merged clusters into RecordInClusterSharedZone and and the radius into RadiusForSharedRegion
                        if(mergeAllSet.contains(i)==false){
                            for(int j=0;j<recordInCluster[i].size();j++){
                               recordInClusterSharedZone[cIndex].addElement(recordInCluster[i].elementAt(j)); 
                            }
                            radiusForSharedRegion.add(cIndex, sharedZoneInfo[i][4]);
                            cIndex++;
                        }                   
                    }
                    recordInCluster=new Vector[recordInClusterSharedZone.length];  
                    recordInCluster=recordInClusterSharedZone; 
                    iteration++;
                    if(recordInClusterSharedZone.length<=2){                            
                        mergeStatus=false;                     
                    }
                }
                else{
                   mergeStatus=false; 
                }
            }            
            else{
               recordInClusterSharedZone=new Vector[2];               
                for(int i=0;i<recordInClusterSharedZone.length;i++){
                    recordInClusterSharedZone[i]=new Vector();
                }
                for(int j=0;j<recordInCluster[0].size();j++){
                    recordInClusterSharedZone[0].addElement(recordInCluster[(int)sharedZoneInfo[0][0]].elementAt(j));
                }
                for(int j=0;j<recordInCluster[1].size();j++){
                    recordInClusterSharedZone[1].addElement(recordInCluster[(int)sharedZoneInfo[1][0]].elementAt(j));
                }               
                for(int i=2;i<sharedZoneInfo.length;i++){
                    if((int)sharedZoneInfo[i][2]==0){
                        for(int j=0;j<recordInCluster[i].size();j++){
                            recordInClusterSharedZone[0].addElement(recordInCluster[(int)sharedZoneInfo[i][0]].elementAt(j));
                        }
                    }
                    else{
                        for(int j=0;j<recordInCluster[i].size();j++){
                            recordInClusterSharedZone[1].addElement(recordInCluster[(int)sharedZoneInfo[i][0]].elementAt(j));
                        }
                    }                    
                }              

               mergeStatus=false; 
            }
        }
            
        return recordInClusterSharedZone; 
    }
    //find shared region information
    public double[][] findSharedRegion(double[][] r2rDistance, Vector<Integer>[] finalCluster, Map <Integer, Integer> unsDensityMap, String Path, String Mode, int itertaion){
            double [][] sharedZoneInfo= new double[finalCluster.length][5]; 
            double average=0;
            int total=0;
            int max=0;
            int tempMax=0;
            int maxIndex=0;
            
            for(int i=0;i<finalCluster.length;i++){
                average=0;
                total=0;
                //average radius
                if(finalCluster[i].size()>=2){
                    for(int j=0;j<finalCluster[i].size()-1;j++){
                        for(int c=j+1;c<finalCluster[i].size();c++){
                            average=average+r2rDistance[finalCluster[i].elementAt(j)][finalCluster[i].elementAt(c)];    
                            total=total+1;                            
                        }                
                    }
                }
                average =(average/total);                     
                sharedZoneInfo[i][0]=i;
                sharedZoneInfo[i][1]=average;

                //find maximum shared region with another cluster             
                max=0;
                maxIndex=0;
                for(int k=0;k<finalCluster.length;k++){
                     if(i!=k){
                         tempMax=0; 
                         for(int j=0;j<finalCluster[i].size();j++){// the cluster for which we need to find shared region
                            for(int c=0;c<finalCluster[k].size();c++){ // another to which the shared region is the maximum
                                if(r2rDistance[finalCluster[i].elementAt(j)][finalCluster[k].elementAt(c)]<sharedZoneInfo[i][1]){
                                    tempMax++;
                                }                        
                            }
                         }
                        if(tempMax>max){
                            maxIndex=k;
                            max=tempMax;
                        }
                    }               
                }
                sharedZoneInfo[i][2]=maxIndex;  
                sharedZoneInfo[i][3]=max;  
                sharedZoneInfo[i][4]=average; 
            } 
           
           
        return sharedZoneInfo;       
    }

    //find the shared region information
    public double[][] findSharedRegionNext(double[][] r2rDistance, Vector<Integer>[] finalCluster, Map <Integer, Integer> unsDensityMap, String Path, String Mode, int itertaion, Vector<Double> radiusForSharedRegion){
        double [][] sharedZoneInfo= new double[finalCluster.length][5]; 
        double average=0;
        int total=0;
        int max=0;
        int tempMax=0;
        int maxIndex=0; 
        
        for(int i=0;i<finalCluster.length;i++){
            average=0;
            total=0;
            //average radius
            if(finalCluster[i].size()>=2){
                for(int j=0;j<finalCluster[i].size()-1;j++){
                    for(int c=j+1;c<finalCluster[i].size();c++){
                        average=average+r2rDistance[finalCluster[i].elementAt(j)][finalCluster[i].elementAt(c)];    
                        total=total+1;                            
                    }                
                }
            }
            average =(average/total);                     
            sharedZoneInfo[i][0]=i;
            sharedZoneInfo[i][1]=average;

            //find maximum shared region with another cluster             
            max=0;
            maxIndex=0;
            for(int k=0;k<finalCluster.length;k++){
                 if(i!=k){
                     tempMax=0; 
                     for(int j=0;j<finalCluster[i].size();j++){// the cluster for which we need to find shared region
                        for(int c=0;c<finalCluster[k].size();c++){ // another to which the shared region is the maximum
                            if(r2rDistance[finalCluster[i].elementAt(j)][finalCluster[k].elementAt(c)]<sharedZoneInfo[i][1]){                            
                                tempMax++;                                
                            }                        
                        }
                     }
                    if(tempMax>max){
                        maxIndex=k;
                        max=tempMax;
                    }
                }               
            }
            sharedZoneInfo[i][2]=maxIndex;        
            sharedZoneInfo[i][3]=max;  
            sharedZoneInfo[i][4]=radiusForSharedRegion.elementAt(i); 
        }        
      
        return sharedZoneInfo;       
    }
    
    //find the neighbors of the densest record
    public Vector<Integer> findNeighborOfDensestRecordWithoutRadius(double[][] r2rDistance, int maxIndex, Vector<Integer> recordIndex){
        Vector<Integer> neighborRecord=new Vector();
        double distance=0;       
        boolean status=true;
        Vector<Integer> densestAndNeighborRecord=new Vector();
        for(int i=0;i<recordIndex.size();i++){
            if(visitStatus.get(recordIndex.elementAt(i))==false){ //either i or RecordIndex.elementAt(i) as i = RecordIndex.elementAt(i)
                if(maxIndex!=i){
                    status=true;
                    distance=r2rDistance[recordIndex.elementAt(maxIndex)][recordIndex.elementAt(i)];              
                    for(int k=0;k<recordIndex.size();k++){
                        if(i!=k){
                            if(r2rDistance[recordIndex.elementAt(k)][recordIndex.elementAt(i)]<distance){
                                status=false;
                                break;
                            }  
                        }
                    }             
                    if(status==true){                
                       neighborRecord.addElement(recordIndex.elementAt(i));  
                    }
                }
            }
        }

        //DensestAndNeighborRecord contains neighbors including the densest record
        densestAndNeighborRecord.addElement(maxIndex);
        densestAndNeighborRecord.addAll(neighborRecord);       
        return neighborRecord;
    }

     //write results in a file
    void writeResult(String Path, String Mode, StringBuilder cluster){

        try{
            BufferedWriter fw = new BufferedWriter(new FileWriter(new File(Path+Mode+".csv")));
            fw.write(cluster.toString());
            fw.flush();
            fw.close();
        }
        catch (IOException e) {
        }

    }

    //cluster evaluation
    public StringBuilder clusterEvaluation(Vector<Integer>[] recordInCluster, double[][]data, String[]attrType, String[] recordLabel, String[]classValue, String Path, String Mode, String Dataset){
        StringBuilder resultCSV=new StringBuilder();
        int classSize=classValue.length; 
        int[][] clusterClassValue=new int[recordInCluster.length][classSize];
        for(int i=0;i<recordInCluster.length;i++){
            for(int j=0;j<recordInCluster[i].size();j++){
                 for(int k=0;k<classValue.length;k++){
                    if(recordLabel[recordInCluster[i].elementAt(j)].equals(classValue[k])){
                        clusterClassValue[i][k]+=1;
                    }
                 }
            }
        }

        //ARI, NMI, F-1 score, and purity
        double ari=evaluation.adjustedRandIndex(data, clusterClassValue);
        double nmi=evaluation.normalizedMutualInformation(recordLabel, clusterClassValue);
        double fmeasure=evaluation.f1Score(data, clusterClassValue);       
        double purity=evaluation.calculatePurity(data, clusterClassValue);
        resultCSV.append(ari);
        resultCSV.append(","); 
        resultCSV.append(nmi);
        resultCSV.append(","); 
        resultCSV.append(fmeasure);
        resultCSV.append(","); 
        resultCSV.append(purity);
        return resultCSV;
    }
}
