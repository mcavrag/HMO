package proj;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class TabuSearch {

	private TabuList tabuList;
	private int numberOfNeighbours;
	private int stopCriteria;
	private int randomResourceNum;

	public TabuSearch(int maxTabuLength, int numberOfNeighbours,
			int stopCriteria) {
		tabuList = new TabuList(maxTabuLength);
		this.numberOfNeighbours = numberOfNeighbours;
		this.stopCriteria = stopCriteria;
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

			/*
			 * Solution bestNeighbourFound =
			 * findBestNeighbor(candidateNeighbours, solutionsInTabu); if
			 * (bestNeighbourFound.getExecTime() < bestSolution.getExecTime()) {
			 * bestSolution = bestNeighbourFound; }
			 */

			tabuList.add(currentSolution);
			// currentSolution = bestNeighbourFound;
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
		tmpListMachines.addAll(currentSolution.getUsedMachines());

		int i = 0;
		while (i < numberOfNeighbours) {
			int randTest1 = ThreadLocalRandom.current()
					.nextInt(0, tests.size());
			int randTest2 = ThreadLocalRandom.current()
					.nextInt(0, tests.size());

			if (randTest1 != randTest2) {
				Test test1 = tests.get(randTest1);
				Test test2 = tests.get(randTest2);

				if (test2.canAssignToMachine(test1.getExecMachine())
						&& test1.canAssignToMachine(test2.getExecMachine())) {

					/*
					 * test2.setExecMachine(test1.getExecMachine());
					 * machines.get(machines.indexOf()) i++;
					 */
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

		ArrayList<Machine> execMachines = new ArrayList<Machine>();
		execMachines.addAll(machines);

		ArrayList<Resource> execResources = new ArrayList<Resource>();
		execResources.addAll(resources);

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

					Machine randomMachine = tmpTest.getUsableMachines().get(
							randomMachineNum);

					if (machineTmpList.contains(randomMachine)) {
						tmpTest.setExecMachine(randomMachine);
						execMachines.get(execMachines.indexOf(randomMachine))
								.setUsed(true);
						machineTmpList.remove(randomMachine);
					} else {
						continue;
					}

					boolean shouldContinue = false;

					if (tmpTest.getReqResources() != null) {
						for (Resource resource : tmpTest.getReqResources()) {
							if (resource.isUsed()) {
								tmpTest.setExecMachine(null);
								machineTmpList.add(randomMachine);
								execMachines.get(
										execMachines.indexOf(randomMachine))
										.setUsed(false);
								shouldContinue = true;
								break;
							}

							execResources.get(execResources.indexOf(resource))
									.setUsed(true);
						}
						if (shouldContinue)
							continue;
					}

					tmpTestList.remove(tmpTest);
					randomTestNumList = helper
							.randomNumberGenerator(tmpTestList.size());

					tmpTest.setStartExec(execMachines.get(
							execMachines.indexOf(randomMachine))
							.getMaxExecTime());

					execMachines.get(execMachines.indexOf(randomMachine))
							.setMaxExecTime(
									execMachines.get(
											execMachines.indexOf(randomMachine))
											.getMaxExecTime()
											+ tmpTest.getTimeLength());

					startSolution.getTestExecList().add(tmpTest);
				}
			}
			machineTmpList.addAll(machines);
			execResources.forEach(res -> res.setUsed(false));
		}
		startSolution.getUsedMachines().addAll(execMachines);
		startSolution.getUsedResources().addAll(execResources);

		System.out.println(startSolution.getTestExecList().size());
		System.out.println(startSolution.getUsedMachines().size());
		System.out.println(startSolution.getUsedResources().size());
		
		startSolution.setExecTime(calculateMaxExecTime(startSolution.getUsedMachines()));

		System.out.println(startSolution.getExecTime());

		return new Solution();
	}

	private int calculateMaxExecTime(ArrayList<Machine> usedMachines) {
		int max = 0;

		for (Machine mach : usedMachines) {
			if (max < mach.getMaxExecTime())
				max = mach.getMaxExecTime();
		}

		return max;
	}
}
