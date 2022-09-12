/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Techniques;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

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
 * Main method class for PDAutoClust
 */
public class RunMain{
    /**
     * @param args the command line arguments
     */ 
    private String mainMenu;
    BufferedReader stdIn;
    //StringTokenizer to parse string
    StringTokenizer tokenizer;
    //it will contain the records
    private String data[][];
    //keeps attribute name information
    private String[] attrName;
   //keeps type of attribute information
    private String[] attrType;
    //keeps type of attribute information
    private String[][] attrValue;
    //domain values of class attribute
    private String[] classValue;    
    Dataset ds;
    PDAutoClust PDC;

     /**
     * Class constructor.
     */
    public RunMain(){
        stdIn = new BufferedReader(new InputStreamReader(System.in)); 
        ds= new Dataset();
        PDC=new PDAutoClust();  
        mainMenu="\n---------------------PDAutoClust----------------------" +
                   "\nPlease select one of the options from following:\n" +
                   "1. Run PDAutoClust\n"+           
                   "0. Exit Program\n";       
    }
    
    //PDAutoClust and  input files preparation
    private void makeSelection() throws IOException{
        String response="";
       //keep running until exit is called
        while(true){
            //main menu
            System.out.println(mainMenu);
            //get user response
            try {
                response = stdIn.readLine();
            }
            catch(IOException e){
                System.out.println("There was a problem, the program will now exit. "+ e);
                System.exit(0);
            }           

           //read data files and run PDAutoClust 
            if(response.equals("1")){
                pdAutoClust();
            }
            //Data preparation for PDAutoClust 
            if(response.equals("2")){
                System.out.println("Used another project to generate data files");
                //DataPreparation();
            }
            //Exit program 
            if(response.equals("0")){
                break;
            }
        }

    }
    
    //read data files and run PDAutoClust
    private void pdAutoClust() throws IOException{
        String delims="[;,\\t ]+";  
        int numberOfAttribute=0;
        int numberOfRecord=0;        
        numberOfRecord=ds.numberOfRecord(Dataset.Data);  
        numberOfAttribute=ds.numberOfAttribute(Dataset.Data); 
        data=new String[numberOfRecord][numberOfAttribute];                
        FileReader frData=new FileReader(Dataset.Data);
        BufferedReader inFileData=new BufferedReader(frData);
        String lineData=inFileData.readLine();            
        String[] singleRowData=lineData.split(delims);            
        int row=0;
        while(lineData!=null){
            singleRowData=lineData.split(delims);                
            for(int col=0;col<singleRowData.length;col++){
                data[row][col]=singleRowData[col];
            }
            lineData=inFileData.readLine();
            row++;                                                    
        }
        row=0;            

        attrType= new String[numberOfAttribute-1];
        attrName= new String[numberOfAttribute-1];
        attrValue=new String[numberOfAttribute-1][];        
        FileReader frAttrValue = new FileReader(Dataset.attributeName);
        BufferedReader inFileAttrValue = new BufferedReader(frAttrValue);
        String lineAttrVal="";        
        int column=0;
        for(int a=0;a<numberOfAttribute-1;a++){
            lineAttrVal = inFileAttrValue.readLine();                                                    
            String[] singleRowAttrValue=lineAttrVal.split(delims);  
            singleRowAttrValue=lineAttrVal.split(delims);
            attrType[a]=singleRowAttrValue[0];
            attrName[a]=singleRowAttrValue[1];
            column=0;
            if("c".equals(attrType[a]) || "C".equals(attrType[a])){ 
                attrValue[a]=new String [Integer.parseInt(singleRowAttrValue[2])];
                for(int col=3;col<singleRowAttrValue.length;col++){
                    attrValue[a][column]=singleRowAttrValue[col];
                    column++;
                }
                column=0;
            }
            else{
                column=0;
                attrValue[a]=new String [2];
                for(int col=2;col<singleRowAttrValue.length;col++){
                    attrValue[a][column]=singleRowAttrValue[col];
                     column++;
                }
                column=0;                           
            }
        }
        lineAttrVal = inFileAttrValue.readLine();
        String[] classSingleRow=lineAttrVal.split(delims);
        classSingleRow=lineAttrVal.split(delims);                    
        classValue=new String [Integer.parseInt(classSingleRow[2])];
        int position=0;
        for(int c=3;c<classSingleRow.length;c++){                    
            classValue[position]=classSingleRow[c];  
            position++;
        }  
        position=0;        
        String datasetPath=Dataset.dataPATH;
        int lastIndexOfSlash=datasetPath.lastIndexOf("\\");
        String datasetnameWithoutSlash=datasetPath.substring(0, lastIndexOfSlash);
        int secondLastIndexOfSlash=datasetnameWithoutSlash.lastIndexOf("\\");
        String dataset=datasetPath.substring(secondLastIndexOfSlash+1, lastIndexOfSlash);        
        String[][] classAttributeInfo=ds.domainInfoOfClassAttribute(data, attrType, classValue); 
        double dataNumerical[][]=new double [data.length][data[0].length];
        //For our datasets, all the attributes are numerical except class attribute
        for(int d=0;d<dataNumerical.length;d++){
            for(int c=0;c<data[0].length;c++){
                dataNumerical[d][c]=Double.parseDouble(data[d][c]);
            }                
        }
        //Store the labels of records
        String[]recordLabel=new String[data.length];
        for(int i=0;i<data.length;i++){
            recordLabel[i]=data[i][data[0].length-1];
        }
        //Perform PDAutoClust
        PDC.performPDAutoClust(dataNumerical, recordLabel, attrType, attrName, classValue, classAttributeInfo, dataset);                       
    }
   
    //main method
    public static void main(String[] args) throws IOException{
        RunMain main = new RunMain();
        main.makeSelection();
    }

}
