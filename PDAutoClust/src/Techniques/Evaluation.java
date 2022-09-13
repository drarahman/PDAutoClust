/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Techniques;
import java.util.HashSet;
import java.util.Set;
/**
 *
 * @author Dr Md Anisur Rahman
 * School of Computing, Mathematics and Engineering, 
 * Charles Sturt University, Australia.
 * Email: arahman@csu.edu.au; javedcse@gmail.com
 * Date: 17/06/2022
 */

/**
 *
 * Evaluation class
 */
public class Evaluation {
   
    public Evaluation(){
    
    }
   //calculate f1 scores 
   public double f1Score(double [][]data, int [][] clusterClassValue){
        double[][] fm= new double[clusterClassValue.length][clusterClassValue[0].length];        
        double[][]precision=new double[clusterClassValue.length][clusterClassValue[0].length];
        double[][]recall=new double[clusterClassValue.length] [clusterClassValue[0].length];        
        double[] maxFm= new double[clusterClassValue[0].length];        
        double fmeasure=0;
        double[] recordInCluster=new double[clusterClassValue.length];
        double[] recordInClass=new double[clusterClassValue[0].length];
        int recordInSingleCluster=0;
        for(int i=0;i<clusterClassValue.length;i++){
            recordInSingleCluster=0;
            for(int j=0;j<clusterClassValue[0].length;j++){
               recordInSingleCluster+=clusterClassValue[i][j];
            }
            recordInCluster[i]=recordInSingleCluster;
        }

        for(int i=0;i<clusterClassValue[0].length;i++){
            recordInSingleCluster=0;
            for(int j=0;j<clusterClassValue.length;j++){
               recordInSingleCluster+=clusterClassValue[j][i];
            }
            recordInClass[i]=recordInSingleCluster;
        }

        for(int i=0;i<clusterClassValue[0].length;i++){
            for(int j=0;j<clusterClassValue.length;j++){
                if(recordInCluster[j]!=0){
                    precision[j][i]=clusterClassValue[j][i]/recordInCluster[j];
                    recall[j][i]=clusterClassValue[j][i]/recordInClass[i];
                }
            }
        }

        for(int i=0;i<clusterClassValue[0].length;i++){
            double max=0;            
            for(int j=0;j<clusterClassValue.length;j++){
                if(precision[j][i]!=0 && recall[j][i]!=0){
                    fm[j][i]=((2*precision[j][i]*recall[j][i])/(precision[j][i]+recall[j][i]));
                    
                    if(fm[j][i]>=max){
                       max=fm[j][i];
                    }
                    
                }
            }
            maxFm[i]=max;            
        }

        for(int i=0;i<clusterClassValue[0].length;i++){
            fmeasure+=((recordInClass[i]/data.length)*maxFm[i]);
        }       

        System.out.println("F1 score:"+fmeasure);
        return fmeasure;
     }
    
    //adjusted rand index
    public double adjustedRandIndex(double [][]data, int [][] clusterClassValue){
        double ari=0;
        int[][] contingencyTable= new int[clusterClassValue.length][clusterClassValue[0].length];        
        int[] rowTotal= new int[contingencyTable.length];
        int[] columnTotal= new int[contingencyTable[0].length];          
        int row=0;
        int col=0;
                
        for(int i=0;i<contingencyTable.length;i++){
            row=0;
            for(int j=0;j<contingencyTable[0].length;j++){
                row=row+clusterClassValue[i][j];
            }
            rowTotal[i]=row;
        }
        
        for(int i=0;i<contingencyTable[0].length;i++){
            col=0;
            for(int j=0;j<contingencyTable.length;j++){
                col=col+clusterClassValue[j][i];
            }
            columnTotal[i]=col;
        }
        
        double index=0;
        int r=2;
        int numerator=0;
        int denominator=0;
        
        for(int i=0;i<contingencyTable.length;i++){
            for(int j=0;j<contingencyTable[0].length;j++){
                if(clusterClassValue[i][j]-r>=0){
                    numerator=clusterClassValue[i][j]*(clusterClassValue[i][j]-1);
                    denominator=Factorial(r);
                    index+=(numerator*1.0/(denominator*1.0));
                }
            }
        }
        
        double expectedIndex=0;
        for(int i=0;i<rowTotal.length;i++){
            if(rowTotal[i]-r>=0){
                numerator=rowTotal[i]*(rowTotal[i]-1);
                denominator=Factorial(r);
                expectedIndex+=(numerator*1.0/(denominator*1.0));
            } 
        }
        
        double maxIndex=0;
        for(int i=0;i<columnTotal.length;i++){
            if(columnTotal[i]-r>=0){
                numerator=columnTotal[i]*(columnTotal[i]-1);
                denominator=Factorial(r);
                maxIndex+=(numerator*1.0/(denominator*1.0));
            } 
        }       

        
        double nc2=data.length*(data.length-1);
        
        ari=(index-((maxIndex*expectedIndex)/nc2))/((0.5*(maxIndex+expectedIndex))-((maxIndex*expectedIndex)/nc2));
        
        System.out.println("Adjusted Rand Index:"+ari);
        return ari;
    }
    
    //factorial calculation
    public int Factorial(int n){     
      int fact = 1;
      int i = 1;
      while(i<=n){
         fact=fact*i;
         i++;
      }      
      return fact;
    }
    
   //Normalized Mutual Information
    public double normalizedMutualInformation(String []recordLabel, int [][] clusterClassValue){
        double nmi=0;     
        int[] rowTotal= new int[clusterClassValue.length];        
        int[] classDistribution= new int[clusterClassValue[0].length];        
        Set<String> classSet=new HashSet<String>();
        
        for(int i=0;i<recordLabel.length;i++){
            classSet.add(recordLabel[i]);
        }
        
        String [] classValue=new String[classSet.size()];
        classSet.toArray(classValue);

        int count=0;
        for(int i=0;i<classValue.length;i++){
            count=0;
            for(int j=0;j<recordLabel.length;j++){
                if(classValue[i] == null ? recordLabel[j] == null : classValue[i].equals(recordLabel[j])){
                    count++;

                }
            }
            classDistribution[i]=count;        
        }
        
        
        double classEntropy=0;
        double P=0;
        
        for(int i=0;i<classDistribution.length;i++){
            if(classDistribution[i]>0){
                P=(double)classDistribution[i]/recordLabel.length;
                classEntropy+=(-(P*(Math.log(P)/(Math.log(2)))));  
            }
        }
 
        double clusterEntropy=0;
        P=0;        
        int[][] contingencyTable= new int[clusterClassValue.length][clusterClassValue[0].length]; 
        int[] columnTotal= new int[contingencyTable[0].length];  
        int row=0;
        int col=0;
        
        for(int i=0;i<clusterClassValue.length;i++){
            row=0;
            for(int j=0;j<clusterClassValue[0].length;j++){
                row=row+clusterClassValue[i][j];
            }
            rowTotal[i]=row;
        }
        
        for(int i=0;i<contingencyTable[0].length;i++){
            col=0;
            for(int j=0;j<contingencyTable.length;j++){
                col=col+clusterClassValue[j][i];
            }
            columnTotal[i]=col;
        }
        for(int i=0;i<rowTotal.length;i++){
            if(rowTotal[i]>0){
                P=(double)rowTotal[i]/recordLabel.length;
                clusterEntropy+=(-(P*(Math.log(P)/(Math.log(2)))));  
            }
        }
        
        double clusterClassEntropy=0;
        double Q=0;
        
        for(int i=0;i<clusterClassValue.length;i++){
            for(int j=0;j<clusterClassValue[0].length;j++){
                if(rowTotal[i]>0){
                    P=(double)clusterClassValue[i][j]/recordLabel.length;
                    Q=(clusterClassValue[i][j]*recordLabel.length)*1.0/(rowTotal[i]*columnTotal[j]);
                    if(P>0){
                        clusterClassEntropy+=(P*(Math.log(Q)/Math.log(2)));
                    }
                }
            }

        }
        
        nmi=clusterClassEntropy/Math.sqrt(classEntropy*clusterEntropy);
        System.out.println("Normalized Mutual Information:"+nmi);
   
        return nmi;
    }
        
    //calculate Purity
    public double calculatePurity(double[][]data, int [][] clusterClassValue){
        double purity=0;       
        double m=0;
        double[] pmax=new double[clusterClassValue.length];
        double tpmax=0;
        int [] recordsInCluster=new int[clusterClassValue.length];
        double max=0;
        int recordcounter=0;
        
        for(int i=0;i<clusterClassValue.length;i++){
            max=0;
            recordcounter=0;
            for(int k=0;k<clusterClassValue[i].length;k++){
                tpmax=clusterClassValue[i][k];
                if(tpmax>=max){
                    max=tpmax;
                }
                recordcounter+=tpmax;
            }
            pmax[i]=max;
            recordsInCluster[i]=recordcounter;
        }

        for(int i=0;i<clusterClassValue.length;i++){
            m=recordsInCluster[i];
            if(m!=0){
                purity+=((m/data.length)*((pmax[i])/m));
            }
        }
        
        System.out.println("Purity:"+purity);
        return purity;
    }   
}
