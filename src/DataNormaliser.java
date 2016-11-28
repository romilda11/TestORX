import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class DataNormaliser{
	private double[] mean;
	private double[] SD; // standard deviation
	double[][] rawData;
	double[][] NData; //normalised data
	ArrayList<double[][]> originalPatterns;
	ArrayList<double[][]> newPatterns;
	
	public DataNormaliser(ArrayList<double[][]> patterns){
		this.originalPatterns = patterns;
		this.newPatterns = new ArrayList<double[][]>();
		for (int i=0; i<originalPatterns.size(); i++){
			double[] input = originalPatterns.get(i)[0];
			double[] target = originalPatterns.get(i)[1];
			double[][] pattern = {input.clone(),target.clone()};
			newPatterns.add(pattern);
		}
	}
	
	public DataNormaliser(String fileName) {
		try{
			Scanner scanner = new Scanner(new File(fileName));
			int inputSize = scanner.nextInt();
			this.mean = new double[inputSize];
			this.SD = new double[inputSize];
			scanner.nextLine();
			for (int i=0; i<mean.length; i++){
				mean[i] = scanner.nextDouble();
			}
			scanner.nextLine();
			for (int i=0; i<mean.length; i++){
				SD[i] = scanner.nextDouble();
			}
			scanner.close();
		} catch (FileNotFoundException e){
			e.printStackTrace();
		}
	}
	
	public void normaliseInputs() {
		mean = new double[originalPatterns.get(0)[0].length];
		SD = new double[originalPatterns.get(0)[0].length];
		//calculate the mean for each pattern
		for (int i=0; i<mean.length; i++){
			for (int j=0; j<originalPatterns.size(); j++){
				mean[i] += originalPatterns.get(j)[0][i];
			}
			mean[i] /= originalPatterns.size();
		}
		//calculate the standard deviation for each pattern
		for (int i=0; i<SD.length; i++){
			for (int j=0; j<originalPatterns.size(); j++){
				SD[i] += Math.pow((originalPatterns.get(j)[0][i] - mean[i]), 2);
			}
			SD[i] /= Math.sqrt(SD[i]/originalPatterns.size());
		}
		//normalise data
		for (int i=0; i<newPatterns.size(); i++){
			double[] inputs = newPatterns.get(i)[0];
			for (int j=0; j<mean.length; j++){
				if (SD[j]==0){
					inputs[j] = 0;
				} else {
					inputs[j] = (inputs[j]-mean[j])/SD[j];
				}
			}
		}
		
	}
	
	public ArrayList<double[][]> getNormaliseValidationData(ArrayList<double[][]> originalValidationPairs){
		ArrayList<double[][]> newValidationPairs = new ArrayList<double[][]>();
		for (int i=0; i<originalValidationPairs.size(); i++) {
			double[] input = originalValidationPairs.get(i)[0];
			double[] target = originalValidationPairs.get(i)[1];
			double[][] patterns = {input.clone(),target.clone()};
			newValidationPairs.add(patterns);
		}
		for (int i=0; i<newValidationPairs.size(); i++){
			double[] inputs = newValidationPairs.get(i)[0];
			for (int j=0; j<mean.length; j++){
				if (SD[j]==0){
					inputs[j]=0;
				} else {
					inputs[j]=(inputs[j]-mean[j])/SD[j];
				}
			}
		}
		return newValidationPairs;
	}
	
	public ArrayList<double[][]> getNewPatterns(){
		return newPatterns;
	}
	
	public double[] normaliseInput(double[] rawInput){
		double[] newInput = new double[rawInput.length];
		for (int i=0; i<newInput.length; i++){
			newInput[i] = (rawInput[i]-mean[i])/SD[i];
		}
		return newInput;
	}
	
	public void saveNormaliserToFile(String filename){
		try {
			FileWriter filewriter = new FileWriter(new File(filename), false);
			filewriter.write(mean.length+"\n");
			for (int i=0; i<mean.length; i++){
				filewriter.write(mean[i]+" ");
			}
			filewriter.write("\n");
			for (int i=0; i<mean.length; i++){
				filewriter.write(SD[i]+" ");
			}
			filewriter.close();
		} catch (IOException e){
			e.printStackTrace();
		}
	}
	
}



