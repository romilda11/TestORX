

public class Driver {
	

	public static void main(String[] args) {
		int epochNumber=0;
		double[][] input_nodes;
		double[] output_nodes;
		double[][][] weights;
		double[][] target_outputs;
		double error = 0;
		double[][] error_weight;
		int noOfPattern=4;
		//perceptron.Perceptron(noOfPattern).inputNodes;;
		
		Perceptron perceptron = new Perceptron();
		perceptron.ReadInputs("xordata.csv",4);
		input_nodes = perceptron.getInputNodes(4).clone();
		output_nodes = perceptron.getOutputNodes().clone();
		weights = perceptron.getWeights().clone();
		
		
		double[][][] wei = new double[3][3][3];
		
		for(int p=1; p<4+1; p++){
			System.out.println("p: "+p);
			target_outputs = perceptron.load_pattern(p, input_nodes).clone();
			target_outputs = perceptron.forward_pass(p, target_outputs, weights).clone();
			error = perceptron.update_ms_error(error, p, target_outputs, output_nodes);//accumulate the error
			error_weight = perceptron.calculate_errors(p, target_outputs, output_nodes, weights).clone();
			weights = perceptron.update_weights(p, error_weight, weights, target_outputs).clone();
			
		}
		
		while(epochNumber<100){
			//perceptron.ReadInputs("xordata.csv", 4);
			//error /= 4*1*4;//4*number of neurons in the outer layer*number of patterns
			//error = Math.sqrt(error);
			
			epochNumber++;
		}
	}
	
}


