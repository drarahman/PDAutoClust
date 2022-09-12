/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Techniques;


import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Stream;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;


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
 * DisplayCluster class
 */
public class DisplayCluster extends Application{
    
   
    @Override
    public void start(Stage stage) throws Exception{            
        
        String delims="[;,\\t ]+"; 
        int numberOfRecord=0;
        // Get data from the file            
        File file = new File(Dataset.outputPath+"Output.txt");
        //File file = new File(output);
        Scanner record = new Scanner(file);
        //ignore the first line it is header of the file
        String header=record.nextLine();
        String[] attribute=header.split(delims);

        while(record.hasNextLine()){
            record.nextLine();
            numberOfRecord++;

        }

        String[][] recordWithCluster=new String[numberOfRecord][attribute.length];          

        Scanner input = new Scanner(file);
        //ignore header
        input.nextLine();

        int row=0;           
        while(input.hasNextLine()){
            for(int i=0;i<attribute.length;i++){
                recordWithCluster[row][i]=input.next();
            }
            row++;                    
        }            
        input.close();            


        Double[] xValue = new Double[numberOfRecord];
        Double[] yValue = new Double[numberOfRecord];

        for(int i=0;i<numberOfRecord;i++){
         xValue[i]= Double.valueOf(recordWithCluster[i][0]);
        }

        for(int i=0;i<numberOfRecord;i++){
         yValue[i]= Double.valueOf(recordWithCluster[i][1]);
        }

        Double xMin = Collections.min(Arrays.asList(xValue));
        Double xMax = Collections.max(Arrays.asList(xValue));

        Double yMin = Collections.min(Arrays.asList(yValue));
        double yMax = Collections.max(Arrays.asList(yValue));


        String[] cluster = new String[numberOfRecord];   
        for(int i=0;i<numberOfRecord;i++){
         cluster[i]= recordWithCluster[i][attribute.length-1];
        }
        Set<String> uniqueCluster = new HashSet<String>();

        for(int i=0;i<cluster.length;i++){
            uniqueCluster.add(cluster[i]);
        }

        int numberOfCluster=uniqueCluster.size();


        String[] label=new String[numberOfCluster];
        uniqueCluster.toArray(label);            

        XYChart.Series<Number, Number>[] clusterChart = Stream.<XYChart.Series<Number, Number>>generate(XYChart.Series::new).limit(numberOfCluster).toArray(XYChart.Series[]::new);
        for(Integer i=0;i<numberOfCluster;i++){
            clusterChart[i].setName("Cluster"+label[i]+"");
        }

        Number x=0;
        Number y=0;

        for(int i=0;i<recordWithCluster.length;i++){
            for(int j=0;j<clusterChart.length;j++){
                if(recordWithCluster[i][attribute.length-1].equals(label[j])){
                    x=Double.valueOf(recordWithCluster[i][0]);
                    y=Double.valueOf(recordWithCluster[i][1]);
                    clusterChart[j].getData().add(new XYChart.Data<Number, Number>(x, y));
                }
            }
        }


        Double xRange=(xMax-xMin)/50;   
        Double yRange=(yMax-yMin)/50;

        //Create axes and the chart itself
        NumberAxis xAxis = new NumberAxis(xMin-xRange, xMax+xRange, 1);
        NumberAxis yAxis = new NumberAxis(yMin-yRange, yMax+yRange, 1);
        ScatterChart<Number,Number> plot = new ScatterChart<Number, Number>(xAxis, yAxis);           

        // Plot the clusters on the plot         
        for(int i=0;i<label.length;i++){
           plot.getData().add(clusterChart[i]); 
        }

        // Add to the scene on the stage
        Scene scene = new Scene(plot, 800, 800);
        stage.setScene(scene);
        stage.setTitle("Clusters");
        stage.show();
             
    }
}
