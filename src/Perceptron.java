import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.lang.Math;

public class Perceptron {

	public static final double LEARNING_RATE = 0.05;
	public static final double TRAINING_RATE = 1;
	public  double[][][] INITIAL_WEIGHTS;
	double[][][] weights;
	private int inputSize;
	private double[][] inputNodes = new double[4+1][2+1];
	private double[] outputNodes =new double[4+1];
	private double[][] targetOutputs = new double[2+1][4+1];
	private int[] n;//number of neurons in [] layer
	//private double[] n;
	//INPUT_DIMENSION=n0=2;
	//OUTPUT_DIMENTSION=nL=1;
	//public void Perceptron(int noOfPattern){
		//inputNodes = new double[noOfPattern+1][2+1];
	//}
	public void ReadInputs(String fileName, int noOfPattern) {
		
		n = new int[3];//3 layers - input layer, hidden layer and output layer
		n[0]=2;
		n[1]=2;
		n[2]=1;
		//inputNodes = new double[noOfPattern+1][2+1];//[patterns][inputDimensions]
		//outputNodes = new double[noOfPattern+1];//[pattern]
		//targetOutputs = new double[2+1][noOfPattern+1];//[layers][patterns]
		//read inputs from the file
		try{
			Scanner scanner = new Scanner(new File(fileName));
			inputSize = scanner.nextInt();
			for (int i=1; i<noOfPattern; i++){//no. of patterns
				while(scanner.hasNextLine()){
					scanner.nextLine();
					for (int j=1; j<2+1; j++){//2 dimension of input
						inputNodes[i][j] = scanner.nextDouble();//assign input nodes
						System.out.println("IN: "+inputNodes[i][j]);
					}
					outputNodes[i] = scanner.nextDouble();//assign output nodes
					//System.out.println("out: "+outputNodes[i]);
				}
			}
			scanner.close();
		} catch (FileNotFoundException e){
			e.printStackTrace();
		}
		
	}
	
	public double[][] getInputNodes(int noOfPattern){
		System.out.println("2 2: "+inputNodes[4][1]);
		return inputNodes;
	}
	
	public double[] getOutputNodes(){
		
		//System.out.println("ON: "+outputNodes[1]);
		return outputNodes;
	}
	
	public double[][][] getWeights(){
		//initial weights with small-value numbers
		INITIAL_WEIGHTS = new double[2+1][2+1][2+1];//[layers][patterns][inputDimensions]
		this.weights = new double[2+1][2+1][2+1];//[layers][patterns][inputDimensions]
		for (int l=1; l<2+1; l++){ //two layers
			for (int i=1; i<n[l]+1; i++){
				for(int j=0; j<n[l-1]+1; j++){
					INITIAL_WEIGHTS[l][i][j] = Math.random()*0.2-0.1;//within(-0.1,0.1)
				}
			}
		}
		weights = INITIAL_WEIGHTS;
		return weights;
	}
	
	public double[][] load_pattern(int noOfPattern, double[][] input_nodes){
		//targetOutputs = new double[2+1][2+1];//[no. of layers][max no. of dimensions]
		for (int l=0; l<2; l++){ 
			targetOutputs[l][0]=1;//assign bias unit
		}
		for (int i=1; i<2+1; i++){
			targetOutputs[0][i]=input_nodes[noOfPattern][i];//assign input nodes to target output at layer0
			//System.out.println(input_nodes[noOfPattern][i]);
			System.out.println("TO: "+targetOutputs[0][i]);
		}
		return targetOutputs;
	}
	
	public double[][] forward_pass(int noOfPattern, double[][] targetOutputs, double[][][] weights){
		//calculate target outputs
		for (int l=1; l<2+1; l++){ //two layers, hidden layer and outer layer
			for (int i=1; i<n[l]+1; i++){//number of neurons in the layer
				double temp=0;
				for (int j=0; j<n[l-1]+1; j++){//number of neurons in the last layer
					temp += targetOutputs[l-1][j]*weights[l][i][j];
				}
				if (l==1){//for the hidden layer
					targetOutputs[l][i] = Math.tanh(temp);//tanh as the squashing function
				} else {//for the outer layer
					targetOutputs[l][i] = temp;
					//System.out.println("l: "+l+" i: "+i+" -- "+targetOutputs[l][i]);
				}
			}
		}
		return targetOutputs;
	}
	
	public double update_ms_error(double error, int noOfPattern, double[][] targetOutputs, double[] outputNodes){
		for(int i=1; i<n[2]+1; i++){//for the outer layer
			double temp = targetOutputs[2][i]-outputNodes[noOfPattern];
			error += temp*temp;
		}
		return error;
	}
	
	public double[][] calculate_errors(int noOfPattern, double[][] targetOutputs, double[] outputNodes, double[][][] weights){
		double[][] error_weight = new double[2+1][2+1];
		for (int i=0; i<n[2]+1; i++){
			error_weight[2][i]=1; // initialise error_weight at the outer layer to avoid error
		}
		for (int l=2; l>0; l--){//from the outer layer 
			double temp = 0;
			for (int i=1; i<n[l]+1; i++){
				if (l==2){
					error_weight[l][i] *= targetOutputs[l][i]-outputNodes[noOfPattern];
					//System.out.println("l: "+l+" i: "+i+" -- "+error_weight[l][i]);
				} else {
					for(int k=1; k<n[l+1]+1; k++){
						temp += weights[l+1][k][i]*error_weight[l+1][k];
						//System.out.println("weight: "+weights[l+1][k][i]);
						//System.out.println("errorweight: "+error_weight[l+1][k]);
					}
					//System.out.println(temp);
					error_weight[l][i]=temp*(1-targetOutputs[l][i]*targetOutputs[l][i]);//derivative of tanh is 1-f(x)^2
					//System.out.println("l: "+l+" i: "+i+" -- "+error_weight[l][i]);
				}
			}
		}
		return error_weight;//error contributed by unit in layer to the overall network
	}
	
	public double[][][] update_weights(int noOfPattern, double[][] error_weight, double[][][] weights, double[][] targetOutputs){
		//double[][] error_weight = errorWeight.clone();
		//double[][][] adjusted_weight = weights.clone();
		double[][][] adjusted_weight = new double[3][3][3];
		for (int l=1; l<2+1; l++){ //two layers, hidden layer and outer layer
			for (int i=1; i<n[l]+1; i++){//number of neurons in the layer
				for (int j=0; j<n[l-1]+1; j++){
					adjusted_weight[l][i][j] += TRAINING_RATE*error_weight[l][i]*targetOutputs[l-1][j];
					//System.out.println("TO: "+targetOutputs[l-1][j]);
				}
			}
		}
		System.out.println("aw: "+adjusted_weight[1][1][1]);
		return adjusted_weight;
	}
	
	public int applyActivationFunction(double weightedSum){
		int result=0;
		if (weightedSum>0){ //threshold value
			result=1;
		}
		return result;
	}
	public double[] adjustWeights(int[] data, double[] weights, double nodeOutputs){
		double[] adjustedWeights = new double[weights.length];
		for (int i=0; i<weights.length; i++){
			//adjustedWeights[i] = LEARNING_RATE * error *data[i]*(data[i][0]-nodeOutputs) + weights[i];
		}
		return adjustedWeights;
	}

	public void TAT() {
		
		//read inputs from the file
		try{
			Scanner scanner = new Scanner(new File("xordata.csv"));
			inputSize = scanner.nextInt();
			System.out.println(inputSize);
			inputNodes = new double[inputSize+1][2+1];//[patterns][inputDimensions]
			outputNodes = new double[inputSize+1];//[pattern]
			targetOutputs = new double[2+1][inputSize+1];//[layers][patterns]
			for (int i=1; i<inputSize+1; i++){//no. of patterns
				while(scanner.hasNextLine()){
					scanner.nextLine();
					for (int j=0; j<2; j++){//dimension of input
						//System.out.println("i: "+i+" j: "+j);
						inputNodes[i][j] = scanner.nextDouble();//assign input nodes
						//System.out.println(Double.toString(inputNodes[i][j]));
					}
					outputNodes[i] = scanner.nextDouble();//assign output nodes
					//System.out.println("out: "+outputNodes[i]);
				}
			}
			scanner.close();
		} catch (FileNotFoundException e){
			e.printStackTrace();
		}
		//assign bias unit
				for (int j=0; j<2; j++){
					inputNodes[0][j] = 1;//bias unit
					System.out.println(inputNodes[0][j]);
				}
				for (int l=0; l<2; l++){ //the input layer and hidden layer
					targetOutputs[l][0]=1;
				}
		
	}
}

