package PatternMining;

import java.awt.EventQueue;
import java.awt.Frame;

import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.RowSorter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.netlib.util.doubleW;

import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.print.DocFlavor.STRING;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;

import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.awt.event.ActionEvent;
import weka.core.*;
import weka.filters.Filter;
import weka.filters.supervised.attribute.Discretize;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.border.Border;
import javax.swing.JProgressBar;

public class Main {

	private JFrame frmPatternAnalysis;
	private JTextField tfDataFilePath;
	private static JTable table;
	private DefaultTableModel model;
	private static int numberOfRows = 0;
	private static int numberOfColumns = 0;
	private static int numberOfBins = 4;
	private static List<Record> recordList;
	private static List<Record> discretizeRecordList;
	static Map<String, String>  freqpatterns= new HashMap<String, String>();	
	static Map<String, String>  closedpatterns= new HashMap<String, String>();
	static Map<String,Double> closedPatternGrowthRatelist= new HashMap<String,Double>();
	static Map<String,String> closedPatternAllrecordlist= new HashMap<String,String>();
	static boolean flag= false;
	RowSorter<TableModel> sorter;
	File dataFile;
	Timer timer;
	final static int interval=1000;
	int i;
	//JProgressBar progressbar;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Main window = new Main();
					window.frmPatternAnalysis.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Main() {
		initialize();
	}


	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmPatternAnalysis = new JFrame();
		frmPatternAnalysis.setTitle("Pattern Analysis");
		frmPatternAnalysis.setBounds(100, 100, 743, 420);
		frmPatternAnalysis.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmPatternAnalysis.getContentPane().setLayout(null);

		tfDataFilePath = new JTextField();
		tfDataFilePath.setBounds(111, 17, 314, 20);
		frmPatternAnalysis.getContentPane().add(tfDataFilePath);
		tfDataFilePath.setColumns(10);

		table = new JTable();
		JScrollPane scrollPane = new JScrollPane(table);
		table.setFillsViewportHeight(true);
		scrollPane.setBounds(40, 52, 484, 280);
		frmPatternAnalysis.getContentPane().add(scrollPane);

		JLabel label = new JLabel("Data File");
		label.setBounds(27, 20, 61, 20);
		frmPatternAnalysis.getContentPane().add(label);

		JButton btnClosepattern = new JButton("Close Pattern and Minimal Gen");
		btnClosepattern.setEnabled(false);

		JButton btnEP = new JButton("Jaccard Similarity");
		btnEP.setEnabled(false);
		JButton btnKCluster = new JButton("KMeans Clustering");
		btnKCluster.setEnabled(false);


		//get the input file from user
		JButton button = new JButton("Browse");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser chooser = new JFileChooser();
				chooser.setCurrentDirectory(new java.io.File("."));
				chooser.setDialogTitle("Select the file");
				chooser.addChoosableFileFilter(new FileNameExtensionFilter("Text File", "txt"));
				chooser.addChoosableFileFilter(new FileNameExtensionFilter("CSV", "csv"));
				chooser.setAcceptAllFileFilterUsed(true);
				chooser.setFileSelectionMode(JFileChooser.OPEN_DIALOG);
				chooser.showOpenDialog(null);
				dataFile = chooser.getSelectedFile();
				if (!dataFile.exists()) {
					JOptionPane.showMessageDialog(null, "File does not exist. Please check again", "Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				}

				if (dataFile != null) {

					numberOfColumns = 0;
					numberOfRows = 0;
					tfDataFilePath.setText(dataFile.getAbsolutePath());
					tfDataFilePath.setEnabled(false);
					ArrayList<String> columnNames = new ArrayList<String>();

					try {
						//store dataset into list
						String fname=(String) dataFile.getAbsolutePath();
						BufferedReader reader = new BufferedReader(new FileReader((String) dataFile.getAbsolutePath()));
						String line = null;
						recordList = new ArrayList<Record>();
						while ((line = reader.readLine()) != null) {
							numberOfRows++;
							String[] values = line.split(",");
							Record recordObj = null;
							for (String str : values) {
								numberOfColumns++;
								if (recordObj == null) {
									recordObj = new Record();
									recordObj.DataList = new ArrayList<String>();
									recordObj.DataList.add((str));
								}
								else {
									recordObj.DataList.add(str);
								}
							}
							recordList.add(recordObj);
						}
						reader.close();
						numberOfColumns = numberOfColumns / numberOfRows;


						for (int i = 1; i <= numberOfColumns ; i++) {
							columnNames.add("Col" + i);
						}
						String[] columns = columnNames.toArray(new String[columnNames.size()]);
						table.setModel(new DefaultTableModel(columns, 0));
						table.setAutoResizeMode(0);
						model = (DefaultTableModel) table.getModel();
						String[] columnData;
						for (Record recordObj : recordList) {
							columnData = recordObj.DataList.toArray(new String[recordObj.DataList.size()]);
							model.addRow(columnData);
						}
						model.fireTableRowsInserted(0, numberOfRows);
						sorter = new TableRowSorter<TableModel>(model);
						table.setRowSorter(sorter);

						DiscretizeMapping();
						btnClosepattern.setEnabled(true);
						btnEP.setEnabled(true);
						btnKCluster.setEnabled(true);
					} catch (Exception e1) {
						JOptionPane.showMessageDialog(null, "Some Error Occured. Please try again!!", "Error", JOptionPane.ERROR_MESSAGE);					
						e1.printStackTrace();
						return;
					}
				}
			}
		});
		button.setBounds(454, 16, 89, 23);
		frmPatternAnalysis.getContentPane().add(button);

		btnClosepattern.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try{
					//User input to get min support to find closed patterns
					String minSup = (String) JOptionPane.showInputDialog(new Frame(),
							"Enter min support in % (e.g 20)", "User input", JOptionPane.PLAIN_MESSAGE, null, null, null);

					findClosedpattern(minSup,"DiscretizedD.csv");
					findminGenerator(minSup,"DiscretizedD.csv");
					ClosedandMinGenerator();
				}catch(Exception ex)
				{

				}
			}
		});

		btnClosepattern.setBounds(534, 52, 170, 31);		
		frmPatternAnalysis.getContentPane().add(btnClosepattern);





		btnEP.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try{
					if(!flag)
					{
						//User input to get min support to find closed patterns
						String minSup = (String) JOptionPane.showInputDialog(new Frame(),
								"Enter min support in % (e.g 20)", "User input", JOptionPane.PLAIN_MESSAGE, null, null, null);
						findClosedpattern(minSup,"DiscretizedD.csv");
						findminGenerator(minSup,"DiscretizedD.csv");
						ClosedandMinGenerator();
					}
					//User input to get min growthrate threshold value to find closed patterns
					String minGrowthRate = (String) JOptionPane.showInputDialog(new Frame(),
							"Enter minimum growthrate (e.g. 1 )", "User input", JOptionPane.PLAIN_MESSAGE, null, null, null);

					findClosedEmergingPattern(Double.parseDouble((minGrowthRate)));
				}catch(Exception ex){}
			}
		});
		btnEP.setBounds(534, 110, 170, 31);
		frmPatternAnalysis.getContentPane().add(btnEP);


		btnKCluster.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String numCluster = (String) JOptionPane.showInputDialog(new Frame(),
						"Enter number of Cluster", "User input", JOptionPane.PLAIN_MESSAGE, null, null, null);
				SimpleKMeansCluster.KMeansEvaluater(dataFile.getName(),Integer.parseInt(numCluster));
			}
		});
		btnKCluster.setBounds(534, 168, 170, 31);
		frmPatternAnalysis.getContentPane().add(btnKCluster);
	}


	// function to create discretize mapping using eqaul-width binning and discretize given dataset
	public static void DiscretizeMapping() throws FileNotFoundException
	{
		PrintWriter pw= new PrintWriter("DiscretizationMap.csv");
		try
		{

			List<List<String>> columnRecordsList = new ArrayList<List<String>>();
			List<List<Integer>> intervalLists = new ArrayList<List<Integer>>();
			discretizeRecordList = new ArrayList<Record>();
			List<String> binrecordList ;
			int id=20;

			PrintWriter pw1= new PrintWriter("DiscretizedD.csv");
			DecimalFormat twoDForm = new DecimalFormat("#.###");
			for(int i=1;i<numberOfColumns;i++)
			{
				pw.printf("%-20s","Attr_"+ i);
			}
			pw.println();

			for(int j=1;j<numberOfColumns;j++)
			{

				List<Double> columnData = new ArrayList<Double>();
				binrecordList = new ArrayList<String>();
				List<Integer> intervalDistribution = new ArrayList<Integer>();
				for(Record recObj : recordList)
				{
					columnData.add(Double.parseDouble(recObj.DataList.get(j)));
				}

				double minValue =  Collections.min(columnData);
				double maxValue =Collections.max(columnData);
				// find bin interval
				double binWidth = (minValue+maxValue)/numberOfBins;
				//find interval for each bin 
				double limit1 = minValue+binWidth;
				double limit2 = minValue+binWidth+binWidth;
				double limit3 = minValue+binWidth+binWidth+binWidth;

				String fBin= "[-Inf,"+twoDForm.format(limit1) +"):"+(id+1);
				String sBin= "["+twoDForm.format(limit1) +","+twoDForm.format(limit2) +"):"+(id+2);
				String tBin= "["+twoDForm.format(limit2) +","+twoDForm.format(limit3) +"):"+(id+3);
				String frBin= "["+twoDForm.format(limit3) +",Inf):"+(id+4);


				binrecordList.add(fBin);
				binrecordList.add(sBin); 
				binrecordList.add(tBin);
				binrecordList.add(frBin);


				columnRecordsList.add(binrecordList);
				DecimalFormat df = new DecimalFormat("0");
				for(double value : columnData)
				{

					if(value < limit1)
					{
						intervalDistribution.add((id+1));
					}
					else if(value< limit2)
						intervalDistribution.add((id+2));
					else if(value <limit3)
						intervalDistribution.add((id+3));
					else
						intervalDistribution.add((id+4));

				}
				intervalLists.add(intervalDistribution);
				id=id+4;
			}
			// print output for Discretize data
			for (int k = 0; k < numberOfRows; k++) {
				StringJoiner sj= new StringJoiner(",");
				Record recordObj= new Record();
				recordObj.DiscretizeDataList = new ArrayList<String>();
				recordObj.DiscretizeDataList.add(recordList.get(k).DataList.get(0));
				for (List<Integer> intrvls : intervalLists) {					
					sj.add(intrvls.get(k).toString());	
					recordObj.DiscretizeDataList.add(intrvls.get(k).toString());	

				}	
				discretizeRecordList.add(recordObj);
				pw1.print(sj.toString());				
				pw1.println();


			}
			pw1.close();
			// print output for Discretization Mapping data
			for(int j=0 ;j <numberOfBins;j++)
			{
				for(int k=0;k<columnRecordsList.size();k++)
				{
					pw.printf("%-20s",columnRecordsList.get(k).get(j));	
				}
				pw.println();
			}

			pw.close();

		}catch(Exception ex)
		{}
		finally{
			if (pw != null) {
				pw.close(); // **** closing it flushes it and reclaims resources ****
			}
		}
	}

	// Method to find closed patterns from dataset
	public static void findClosedpattern(String minsup,String fname) {
		try {

			String mSupCommand= "-s"+minsup+"tc";
			ProcessBuilder p = new ProcessBuilder();

			p.command("fpgrowth.exe",mSupCommand,fname,"outputclosed.txt");
			p.start();

		} catch (IOException  e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

	}
	// Method to find minimal generator from dataset
	public static void findminGenerator(String minsup,String fname) {
		try {
			String mSupCommand= "-s"+minsup+"tg";
			ProcessBuilder p = new ProcessBuilder();

			p.command("fpgrowth.exe",mSupCommand,fname,"outputmg.txt");
			p.start();

		} catch (IOException  e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

	}
	// Method to print closed patterns, minimal generator and their support from dataset
	public static void ClosedandMinGenerator() throws IOException
	{
		Path path=Paths.get(".").toAbsolutePath().normalize();
		try {

			String f1= "outputclosed.txt";
			String f2= "outputmg.txt";
			boolean checkf1Exists = new File(f1).exists();			
			boolean checkf2Exists = new File(f2).exists();
			if((!checkf1Exists) &&(!checkf2Exists))
			{
				Thread.sleep(10000);
			}

			PrintWriter pw= new PrintWriter("ClosedAndMG.csv");

			Map<String, String>  minimalGenerator= new HashMap<String, String>();
			BufferedReader readerCP = new BufferedReader(new FileReader("outputclosed.txt"));
			BufferedReader readerMG = new BufferedReader(new FileReader("outputmg.txt"));
			DecimalFormat twoDForm = new DecimalFormat("#.##");
			String lineCP;
			String lineMG;
			String format= "%-40s%-40s%-20s";
			//System.out.println("Closed Pattern******* Support");

			pw.printf(format,  "Closed Pattern","Minimal Generator","Support Count (%)");
			pw.println();
			while( ((lineCP = readerCP.readLine())!= null)){ 
				String[] lineSplited = lineCP.split("\\(");
				String [] support= lineSplited[1].split("\\)");
				closedpatterns.put(lineSplited[0].toString(), support[0]);
				//	System.out.println(lineSplited[0].toString()+"*****"+support[0]);
			}

			while( ((lineMG = readerMG.readLine())!= null)){ 

				String[] lineSplited = lineMG.split("\\(");
				String [] support= lineSplited[1].split("\\)");
				minimalGenerator.put(lineSplited[0].toString(), support[0]);
				//	System.out.println(lineSplited[0].toString()+"*****"+support[0]);
			}
			readerCP.close();
			readerMG.close();
			//System.out.println("--------");
			Object[] keysCP = closedpatterns.keySet().toArray();
			Object[] keysMG = minimalGenerator.keySet().toArray();

			for(int i=0;i<closedpatterns.size();i++)
			{
				StringBuilder bf1 = new StringBuilder();
				StringBuilder bf2 = new StringBuilder();
				List<Integer>itemCpList= new ArrayList<Integer>();
				String CPSupport= closedpatterns.get(keysCP[i]);
				String itemset= keysCP[i].toString();
				String[] itemsetSplited = itemset.split(" ");
				for(String items: itemsetSplited)
				{
					int item= Integer.parseInt(items);					
					itemCpList.add(item);					

				}

				//System.out.println(keysCP[i]+"  CPSupp: "+CPSupport );
				bf1.append("{" +keysCP[i].toString() +"}");
				if(itemCpList.size()==1){
					bf2.append(" ");
					bf2.append("{"+ keysCP[i]+"}");
				}
				else{
					for(int j=0;j<minimalGenerator.size();j++)
					{
						List<Integer>itemMGList= new ArrayList<Integer>();
						String itemsetMG= keysMG[j].toString();
						String[] itemsetMGSplited = itemsetMG.split(" ");
						for(String items: itemsetMGSplited)
						{
							int item= Integer.parseInt(items);					
							itemMGList.add(item);			
						}
						if(itemCpList.containsAll(itemMGList))
						{
							String  MGSupport= minimalGenerator.get(keysMG[j]);
							if(CPSupport.equals(MGSupport))
							{
								//System.out.println(keysMG[j] +"  MGSupp: "+ MGSupport);
								bf2.append(" ");
								bf2.append("{"+keysMG[j]+"}");

							}
						}
					}
				}


				//System.out.println(buffer.toString());
				String output= String.format(format,bf1.toString(),bf2.toString(),twoDForm.format(Double.parseDouble((CPSupport))));
				pw.println(output);
				//	System.out.println("********");
			}
			pw.close();
			flag=true;
			JOptionPane.showMessageDialog(null, "Successfully found Closed patterns and Minimal Generators. Please check ClosedAndMG.csv.");
		} catch (Exception e) {

			e.printStackTrace();
		}
		finally{

			String f1= "outputclosed.txt";
			String f2= "outputmg.txt";
			boolean checkf1Exists = new File(f1).delete();			
			boolean checkf2Exists = new File(f2).delete();

		}

	}

	// Method to find closed emerging patterns from dataset
	public static void findClosedEmergingPattern(double minGrowthRate) {
		try {

			PrintWriter pw= new PrintWriter("ClosedEmergingP.csv");
			String format= "%-50s%-30s%-30s%-30s";			
			pw.printf(format, "Closed EmergingPatterns","GrowthRate","Support in C1","Support in C2");
			pw.println();

			Map<List<String>,Double> datasetlist= new HashMap<List<String>,Double>();

			List<String> bitsetList= new ArrayList<String>();
			Object[] keysCP = closedpatterns.keySet().toArray();
			List<String> emergingClosedPatterns = new ArrayList<String>();
			for(int i=0;i<closedpatterns.size();i++){
				StringBuilder buffer= new StringBuilder();
				List<String> closepatternList= new ArrayList<String>();
				String itemset= keysCP[i].toString();
				String[] itemsetSplited = itemset.split(" ");
				int supportC1=0;
				int supportC2=0;
				double growthrate=0.0;
				for(String items: itemsetSplited)
				{
					//int item= Integer.parseInt(items);					
					closepatternList.add(items);					

				}
				for(Record recObj : discretizeRecordList)
				{
					double ClassLabel= Double.parseDouble(recObj.DiscretizeDataList.get(0));
					List<String>itemlist= new ArrayList<String>();

					for(int j=1;j<recObj.DiscretizeDataList.size();j++)
					{
						itemlist.add(recObj.DiscretizeDataList.get(j).toString());
					}

					if(itemlist.containsAll(closepatternList)){
						if(ClassLabel==1)
							supportC1++;
						else
							supportC2++;					

						buffer.append("1");
						//System.out.println(recObj.DataList.toString() ); 
					}
					else
					{
						buffer.append("0");
					}
				}
				if(supportC1 > supportC2){
					if(supportC2==0)
						growthrate=Double.POSITIVE_INFINITY;
					else
						growthrate = supportC1/supportC2;
				}
				else{
					if(supportC1==0)
						growthrate=Double.POSITIVE_INFINITY;
					else
						growthrate = supportC2/supportC1;
				}
				if(growthrate>=minGrowthRate){

					emergingClosedPatterns.add(closepatternList.toString());
					closedPatternGrowthRatelist.put(closepatternList.toString(), growthrate);
					System.out.println(closepatternList.toString() +" : "+growthrate);
					String epsrecord= "" + growthrate + ";" + supportC1 + ";" + supportC2;
					closedPatternAllrecordlist.put(closepatternList.toString(),epsrecord);
					pw.printf(format,closepatternList.toString(),growthrate,supportC1,supportC2 );
					pw.println();
				}
				//if(!buffer.toString().isEmpty())
				//{
					bitsetList.add(buffer.toString());
					System.out.println(buffer.toString());
				//}
				buffer.setLength(0);
			}

			pw.close();
			CalculateJaccard(emergingClosedPatterns);



		} catch (Exception e) {

			e.printStackTrace();
		}

	}
	// Method to calculate Jaccard similarity of closed emerging patterns from dataset
	public static void CalculateJaccard(List<String> closedpatterns)
	{
		PrintWriter pw;
		PrintWriter pw1;
		String format= "%-60s%-60s";
		String formatEp= "%-50s%-30s%-30s%-30s";
		DecimalFormat twoDForm = new DecimalFormat("#.###");
		Map<String,Double> closedEPsMaxgrowthrate= new HashMap<String,Double>();
		Map<String,Double> closedEPsgrowthrate= new HashMap<String,Double>();
		try {
			pw = new PrintWriter("PSkEPJaccard.csv");
			pw1 = new PrintWriter("PSkEPs.csv");

			Jaccard jacObj = new Jaccard();
			double max = 0.0;
			List<String> sb = new ArrayList<String>();

			int count = 0;
			for(int i=0; i<closedpatterns.size();i++)
			{
				double jacSim = 0.0;
				for(int j=i+1;j<closedpatterns.size();j++)
				{
					//double jacSim = 0.0;
					if(closedpatterns.get(i).length()>3 && closedpatterns.get(j).length()>3)
					{
						jacSim = jacObj.similarity(closedpatterns.get(i), closedpatterns.get(j));
						if(max==0.0)
							max=jacSim;
						else
							max+=jacSim;
						count++;

						if((max)/count<max)
						{
							max = (max)/count;
							if(!sb.contains(closedpatterns.get(i)))
								sb.add(closedpatterns.get(i));
							if(!sb.contains(closedpatterns.get(j)))
								sb.add(closedpatterns.get(j));
						}
						else if(count > 1)
							count--;

						closedEPsgrowthrate.put(closedpatterns.get(i) +"  "+ closedpatterns.get(j), jacSim);						

					}

				}
			}
			//System.out.println(sb.toString());

			closedPatternGrowthRatelist = sortByValue(closedPatternGrowthRatelist);
			double gwAvg=0.0;
			String patternMax;
			double maximizefn = 0.0;
			Object[] keysGW = closedPatternGrowthRatelist.keySet().toArray();
			for(int i=closedPatternGrowthRatelist.size()-1;i>=0;i--)
			{
				//find set of k patterns to get maximum objective function value
				gwAvg = closedPatternGrowthRatelist.get(keysGW[i]); 
				patternMax = closedPatternGrowthRatelist.keySet().toArray()[i].toString();
				if(sb.contains(patternMax))
				{
					maximizefn = gwAvg * (1- max);
					System.out.print(maximizefn);
					pw1.println("Objective function Value:   " + twoDForm.format( maximizefn));
					pw1.println();
					pw1.println();
					pw1.printf(formatEp, "Closed EmergingPatterns","GrowthRate","Support in C1","Support in C2");
					pw1.println();
					//for(String str : sb){
					for(int j=0;j<sb.size();j++){
						String record= closedPatternAllrecordlist.get(sb.get(j));
						String [] records= record.split(";");
						System.out.print(sb.get(j)+ "    " + records[0] + "   "+ records[1] + "  "+ records[2]  );
						pw1.printf(formatEp,sb.get(j),records[0],records[1],records[2]);
						pw1.println();
					}

					//System.out.print(maximizefn);
					break;
				}
			}

			// Print Closed emerging patterns and their Jaccard similarity in a file
			Map<String,Double> sortedclosedEPsgrowthrate= sortByValue(closedEPsgrowthrate);			
			Object [] keys=sortedclosedEPsgrowthrate.keySet().toArray();
			pw.printf(format,"Closed EPS","Jaccard Similarity");
			pw.println();
			for(int i=sortedclosedEPsgrowthrate.size()-1;i>=0;i--)
			{
				pw.printf(format,keys[i], twoDForm.format(sortedclosedEPsgrowthrate.get(keys[i])));
				pw.println();
			}
		
			pw.close();
			pw1.close();
			JOptionPane.showMessageDialog(null, "Closed Emerging pattern found and Jaccard Similarty calcluated. Please check PSkEPs.csv and PSkEPJaccard.csv");

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	// Method for sorting
	public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
		Map<K, V> result = new LinkedHashMap<>();
		Stream<Entry<K, V>> st = map.entrySet().stream();

		st.sorted(Comparator.comparing(e -> e.getValue())).forEach(e -> result.put(e.getKey(), e.getValue()));

		return result;
	}
}
