/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Techniques;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

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
 * Dataset class
 */
public class Dataset{  
    //Please change the file path, if needed 
    public static String dataPATH="C:\\Research\\Dataset\\Corona_bobina_1\\";    
    public static String outputPath="C:\\Research\\PDAutoClust\\Evaluation\\Corona_bobina_1\\";
    public static String Data=dataPATH+"data.txt";        
    public static String attributeName=dataPATH+"attNameInfo.txt";
      
    //This method finds number of records in a dataset
    int numberOfRecord(String datafile) throws FileNotFoundException, IOException{             
        String recordFile="";         
        int recordNumber=0;        
        recordFile=datafile;       
        FileReader frRecord = new FileReader(recordFile);
        BufferedReader inFileRecord=new BufferedReader(frRecord); 
        recordNumber=0;  
        while(inFileRecord.readLine()!=null){
            recordNumber++;
        }
        
        return recordNumber;
    }
    //This method finds number of attributes in a dataset
    int numberOfAttribute(String datafile) throws FileNotFoundException, IOException{  
        String attributeFile="";      
        int numberOfAttribute=0;       
        String delims="[;,\\t ]+";    
        attributeFile=datafile;            
        
        FileReader frAttribute = new FileReader(attributeFile);      
          
        BufferedReader inFileAttribute=new BufferedReader(frAttribute); 
        String firstLine=inFileAttribute.readLine();              
        String newStrFirstLine=firstLine.trim(); 
        String[] attrSize=newStrFirstLine.split(delims);
        numberOfAttribute=attrSize.length;            
        return numberOfAttribute;
    }
    
    //Domain Information of a Class Attribute
    public String[][] domainInfoOfClassAttribute(String[][] originalData, String[]attrInfo, String[]classValue){
        String [][]classAttributeInfo=new String[classValue.length][2];         
        int count=0;
        for(int i=0;i<classValue.length;i++){
            count=0;
            for(int j=0;j<originalData.length;j++){
                if(classValue[i] == null ? originalData[j][attrInfo.length] == null : classValue[i].equals(originalData[j][attrInfo.length])){
                    count++;
                }
            }
            classAttributeInfo[i][0]=classValue[i];
            classAttributeInfo[i][1]=String.valueOf(count); 
        }
        
        return classAttributeInfo;
    }
    
}
