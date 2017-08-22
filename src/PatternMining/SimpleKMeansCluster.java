package PatternMining;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

import javax.swing.JOptionPane;

import weka.clusterers.SimpleKMeans;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.CSVLoader;

public class SimpleKMeansCluster {


	public static void ConvertTextToCSV(String fileName) {
		try {
			BufferedReader bReader = new BufferedReader(new FileReader(fileName));
			PrintWriter out = new PrintWriter(fileName.substring(0, fileName.lastIndexOf(".")) + ".csv");
			String str = null;
			while ((str = bReader.readLine()) != null) {
				out.println(str);
			}

			out.close();
			bReader.close();
		} catch (Exception ex) {
		}
	}
	
	//method for KMeans clustering using Weka package
	public static void KMeansEvaluater(String filename,int numcluster) {
		try
		{
			PrintWriter pw= new PrintWriter("kMeansClustering.csv");
			//Convert test file to CSV
			ConvertTextToCSV(filename);
			CSVLoader loader = new CSVLoader();
			loader.setSource(new File(filename.substring(0, filename.lastIndexOf(".")) + ".csv"));			
			loader.setNoHeaderRowPresent(true);
			// Load dataset
			Instances data = loader.getDataSet();
			// options for Kmeans cluster evaluation

			String options="-init 0 -max-candidates 100 -periodic-pruning 10000 -min-density 2.0 -t1 -1.25 -t2 -1.0 -N "+ numcluster+" -A \"weka.core.EuclideanDistance -R first-last\" -I 500 -num-slots 1 -S 10";
			SimpleKMeans kmeans = new SimpleKMeans();
			kmeans.setOptions(weka.core.Utils.splitOptions(options));
			//Build KMeans Cluster
			kmeans.buildClusterer(data);
			System.out.println(kmeans.toString());
			pw.println(kmeans.toString());			

			pw.close();
			JOptionPane.showMessageDialog(null, "KMeans cluster evaluted. Please check kMeansClustering.csv");


		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
}