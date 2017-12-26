package proj;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class TabuSearch {

	private TabuList tabuList;
	private int numberOfNeighbours;
	private int stopCriteria;
	private int randomResourceNum;
	private ArrayList<Machine> machines;
	private ArrayList<Resource> resources;

	public TabuSearch(int maxTabuLength, int numberOfNeighbours,
			int stopCriteria, ArrayList<Machine> machines,
			ArrayList<Resource> resources) {
		tabuList = new TabuList(maxTabuLength);
		this.numberOfNeighbours = numberOfNeighbours;
		this.stopCriteria = stopCriteria;
		this.machines = machines;
		this.resources = resources;
	}

	public Solution run(Solution startSolution) {

		Solution bestSolution = startSolution;
		Solution currentSolution = startSolution;

		int criteriaCount = 0;

		while ((++criteriaCount) < stopCriteria) {

			ArrayList<Solution> candidateNeighbours = generateNeighbours(currentSolution);
			ArrayList<Solution> solutionsInTabu = new ArrayList<Solution>();

			tabuList.forEach(solutionsInTabu::add);

			// TODO
			
			/*Solution bestNeighbourFound = findBestNeighbor(candidateNeighbours,
					solutionsInTabu);
			if (bestNeighbourFound.getExecTime() < bestSolution.getExecTime()) {
				bestSolution = bestNeighbourFound;
			}*/

			tabuList.add(currentSolution);
			//currentSolution = bestNeighbourFound;
		}

		return bestSolution;
	}

	private Solution findBestNeighbor(ArrayList<Solution> candidateNeighbors,
			ArrayList<Solution> solutionsInTabu) {
		// TODO Auto-generated method stub
		return null;
	}

	// 2-OPT swap
	// TODO
	
	private ArrayList<Solution> generateNeighbours(Solution currentSolution) {
		Utility helper = new Utility();
		ArrayList<Solution> neighbours = new ArrayList<Solution>();

		ArrayList<Test> tests = currentSolution.getTestExecList();
		ArrayList<Machine> tmpListMachines = new ArrayList<Machine>();
		tmpListMachines.addAll(machines);
		
		int i = 0;
		while(i < numberOfNeighbours) {
			int randTest1 = ThreadLocalRandom.current().nextInt(0, tests.size());
			int randTest2 = ThreadLocalRandom.current().nextInt(0, tests.size());
			
			if(randTest1 != randTest2) {
				Test test1 = tests.get(randTest1);
				Test test2 = tests.get(randTest2);
				
				if(test2.canAssignToMachine(test1.getExecMachine()) && test1.canAssignToMachine(test2.getExecMachine())) {
					
					/*test2.setExecMachine(test1.getExecMachine());
					machines.get(machines.indexOf())
					i++;*/
				}
			}
		}
		return null;
	}

	public Solution generateStartSolution(ArrayList<Test> tests,
			ArrayList<Machine> machines, ArrayList<Resource> resources) {
		ArrayList<Machine> machineTmpList = new ArrayList<Machine>();
		machineTmpList.addAll(machines);

		Solution startSolution = new Solution();

		ArrayList<Test> tmpTestList = new ArrayList<Test>();
		tmpTestList.addAll(tests);

		Utility helper = new Utility();

		ArrayList<Integer> randomTestNumList = helper
				.randomNumberGenerator(tmpTestList.size());

		// Random assign tests on machines

		while (startSolution.getTestExecList().size() != tests.size()) {

			for (int i = 0; i < randomTestNumList.size(); i++) {
				Test tmpTest = tmpTestList.get(randomTestNumList.get(i));

				if ((tmpTest.getExecMachine() == null)
						&& !machineTmpList.isEmpty()) {
					
					int randomMachineNum = ThreadLocalRandom.current().nextInt(
							0, tmpTest.getUsableMachines().size());
					int randomResourceNum = -1;
					
					if (tmpTest.getReqResources() != null)
						randomResourceNum = ThreadLocalRandom.current()
								.nextInt(0, tmpTest.getReqResources().size());

					Machine randomMachine = tmpTest.getUsableMachines().get(
							randomMachineNum);
					
					if (machineTmpList.contains(randomMachine)) {
						tmpTest.setExecMachine(randomMachine);
						machines.get(machines.indexOf(randomMachine)).setUsed(
								true);
						machineTmpList.remove(randomMachine);		
					} else {
						continue;
					}

					if (randomResourceNum != -1) {
						Resource randomResource = tmpTest.getReqResources()
								.get(randomResourceNum);
						if (resources.get(resources.indexOf(randomResource))
								.isUsed()) {
							tmpTest.setExecMachine(null);
							machineTmpList.add(randomMachine);
							machines.get(machines.indexOf(randomMachine))
									.setUsed(false);
							continue;
						} else {
							resources.get(resources.indexOf(randomResource))
									.setUsed(true);
						}
					}

					tmpTestList.remove(tmpTest);
					randomTestNumList = helper
							.randomNumberGenerator(tmpTestList.size());

					tmpTest.setStartExec(machines.get(
							machines.indexOf(randomMachine)).getMaxExecTime());

					machines.get(machines.indexOf(randomMachine))
							.setMaxExecTime(
									machines.get(
											machines.indexOf(randomMachine))
											.getMaxExecTime()
											+ tmpTest.getTimeLength());

					startSolution.getTestExecList().add(tmpTest);
				} else if ((tmpTest.getExecMachine() == null)) {
					System.out.println("Usao");
				}
			}
			machineTmpList.addAll(machines);
			resources.forEach(res -> res.setUsed(false));
		}

		System.out.println(startSolution.getTestExecList().size());

		startSolution.setExecTime(calculateMaxExecTime());

		System.out.println(startSolution.getExecTime());

		return new Solution();
	}

	private int calculateMaxExecTime() {
		int max = 0;
		
		for (Machine mach : machines) {
			if (max < mach.getMaxExecTime())
				max = mach.getMaxExecTime();
		}
		
		return max;
	}
}
