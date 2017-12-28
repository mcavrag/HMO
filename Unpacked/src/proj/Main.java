package proj;

import java.util.ArrayList;

public class Main {

	public static int numberOfTests = 0;
	public static int numberOfMachines = 0;
	public static int numberOfResources = 0;
	
	public static ArrayList<Test> tests = new ArrayList<Test>();
	public static ArrayList<Machine> machines = new ArrayList<Machine>();
	public static ArrayList<Resource> resources = new ArrayList<Resource>();

	public static void main(String[] args) {
		if (args.length == 0) {
			System.out
					.println("No arguments provided! Please provide argument 'tsX.txt' where X is a number of the sequence!");
			return;
		}
		Utility helper = new Utility();
		helper.parseInput(args[0], tests, machines, resources, numberOfTests, numberOfMachines, numberOfResources);
		
		TabuSearch tabuSearch = new TabuSearch(5, numberOfTests, 3, machines, resources);

		Solution startSolution = tabuSearch.generateStartSolution(tests, machines, resources);
		
		tabuSearch.run(startSolution);

		/*for (Test test : tests) {
			System.out.print(test.getName() + " " + test.getTimeLength() + " ");
			printList1(test.getUsableMachines());
			System.out.print(" ");
			printList2(test.getReqResources());
			System.out.println();
		}*/
	}

	/*private static void printList2(ArrayList<Resource> reqResources) {
		if(reqResources == null) System.out.print(false);
		else {
			for (Resource t : reqResources) {
				System.out.print(t.getName() + " ");
			}
		}		
	}

	private static void printList1(ArrayList<Machine> list) {
		if(list == null) System.out.print(false);
		else {
			for (Machine t : list) {
				System.out.print(t.getName() + " ");
			}
		}
	}*/
}