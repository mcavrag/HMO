package proj;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.concurrent.ThreadLocalRandom;

public class TabuSearch {

	private TabuList tabuList;
	private int numberOfNeighbors;
	final long stopCriteria;

	public TabuSearch(int maxTabuLength, int numberOfNeighbours,
			int stopCriteria) {
		tabuList = new TabuList(maxTabuLength);
		this.numberOfNeighbors = numberOfNeighbours;
		this.stopCriteria = System.nanoTime() + stopCriteria * 1000 * 1000 * 1000L;;
	}

	public Solution run(Solution startSolution) {

		Solution bestSolution = startSolution;
		Solution currentSolution = startSolution;
		
		System.out.println("Start solution execution time is: " + startSolution.getExecTime());

		do {

			ArrayList<Solution> candidateNeighbors = generateNeighbors(currentSolution);
			ArrayList<Solution> solutionsInTabu = new ArrayList<Solution>();

			tabuList.forEach(solutionsInTabu::add);

			Solution bestNeighbourFound = findBestNeighbor(candidateNeighbors,
					solutionsInTabu);

			if (bestNeighbourFound.getExecTime() < bestSolution.getExecTime()) {
				bestSolution = bestNeighbourFound;
			}

			tabuList.add(bestNeighbourFound);

			currentSolution = bestNeighbourFound;
			
		} while(System.nanoTime() < stopCriteria);
		
		System.out.println("Best solution execution time is: " + bestSolution.getExecTime());
		
		return bestSolution;
	}

	private Solution findBestNeighbor(
			ArrayList<Solution> candidateNeighbors,
			ArrayList<Solution> solutionsInTabu) {

		candidateNeighbors.removeIf(sol -> solutionsInTabu.contains(sol));
		
		// Sort the candidate neighbors by the execution time
		Collections.sort(candidateNeighbors, new Comparator<Solution>() {
			@Override
			public int compare(Solution s1, Solution s2) {
				return Integer.compare(s1.getExecTime(), s2.getExecTime());
			}
		});

		// Get the neighbor with the lowest execution time
		return candidateNeighbors.get(0);
	}

	// 2-OPT swap
	// TODO
	// this needs testing
	private ArrayList<Solution> generateNeighbors(Solution currentSolution) {
		ArrayList<Solution> neighbors = new ArrayList<Solution>();
		Utility helper = new Utility();

		ArrayList<Integer> randomNumbers = helper
				.randomNumberGenerator(currentSolution.getTestExecList().size());

		int i = 0;

		while (i < numberOfNeighbors) {

			ArrayList<Test> tests = new ArrayList<Test>();
			tests.addAll(currentSolution.getTestExecList());

			ArrayList<Machine> tmpListMachines = new ArrayList<Machine>();
			tmpListMachines.addAll(currentSolution.getUsedMachines());

			Solution neighbor = new Solution();
			neighbor.setTestExecList(tests);
			neighbor.setUsedMachines(tmpListMachines);

			int randTest1 = ThreadLocalRandom.current().nextInt(0,
					randomNumbers.size());
			int randTest2 = ThreadLocalRandom.current().nextInt(0,
					randomNumbers.size());

			if (randTest1 != randTest2) {
				
				Test test1 = neighbor.getTestExecList().get(
						randomNumbers.get(randTest1));
				Test test2 = neighbor.getTestExecList().get(
						randomNumbers.get(randTest2));

				ArrayList<Resource> commonResources = new ArrayList<Resource>();

				if (test2.getReqResources() != null) {
					commonResources.addAll(test2.getReqResources());
				}

				if (test1.getReqResources() != null)
					commonResources.retainAll(test1.getReqResources());

				Machine m1 = test1.getExecMachine();
				Machine m2 = test2.getExecMachine();

				// regardless of solution feasibility, tests are swapped
				test1.setExecMachine(m2);
				test2.setExecMachine(m1);

				test1.setStartExec(tmpListMachines.get(
						tmpListMachines.indexOf(m2)).getMaxExecTime());

				neighbor.getUsedMachines()
						.get(tmpListMachines.indexOf(m2))
						.setMaxExecTime(
								tmpListMachines
										.get(tmpListMachines.indexOf(m2))
										.getMaxExecTime()
										- test2.getTimeLength()
										+ test1.getTimeLength());

				test2.setStartExec(tmpListMachines.get(
						tmpListMachines.indexOf(m1)).getMaxExecTime());

				neighbor.getUsedMachines()
						.get(tmpListMachines.indexOf(m1))
						.setMaxExecTime(
								tmpListMachines
										.get(tmpListMachines.indexOf(m1))
										.getMaxExecTime()
										- test1.getTimeLength()
										+ test2.getTimeLength());

				if (test2.canAssignToMachine(test1.getExecMachine())
						&& test1.canAssignToMachine(test2.getExecMachine())
						&& commonResources.isEmpty()) {

					neighbor.setExecTime(calculateMaxExecTime(neighbor
							.getUsedMachines()));
				} else {
					neighbor.setExecTime(Integer.MAX_VALUE);
				}

				if(!neighbors.contains(neighbor)) {
					neighbors.add(neighbor);
					i++;
				}
			}
		}
		return neighbors;
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
				System.out.println("Started assigning test "
						+ tmpTest.getName() + " with start exec time "
						+ tmpTest.getStartExec());

				if ((tmpTest.getExecMachine() == null)
						&& !machineTmpList.isEmpty()) {

					System.out.println("The test " + tmpTest.getName()
							+ " hasn't been executed yet");

					int randomMachineNum = ThreadLocalRandom.current().nextInt(
							0, tmpTest.getUsableMachines().size());

					Machine randomMachine = tmpTest.getUsableMachines().get(
							randomMachineNum);

					if (machineTmpList.contains(randomMachine)) {
						System.out.println("Assigned test " + tmpTest.getName()
								+ " to machine " + randomMachine.getName());

						tmpTest.setExecMachine(randomMachine);
						execMachines.get(execMachines.indexOf(randomMachine))
								.setUsed(true);
						machineTmpList.remove(randomMachine);
					} else {
						System.out.println("Couldn't assign test "
								+ tmpTest.getName() + " to machine "
								+ randomMachine.getName());
						continue;
					}

					boolean shouldContinue = false;

					if (tmpTest.getReqResources() != null) {
						for (Resource resource : tmpTest.getReqResources()) {
							if (execResources.get(
									execResources.indexOf(resource)).isUsed()) {
								tmpTest.setExecMachine(null);
								System.out
										.print("Couldn't assign resources to test "
												+ tmpTest.getName()
												+ " old start exec time was "
												+ tmpTest.getStartExec());

								tmpTest.setStartExec(execResources
										.get(execResources.indexOf(resource))
										.getUsedBy().getTimeLength()
										+ execResources
												.get(execResources
														.indexOf(resource))
												.getUsedBy().getStartExec());

								System.out.println(" and new is "
										+ tmpTest.getStartExec());
								machineTmpList.add(randomMachine);
								execMachines.get(
										execMachines.indexOf(randomMachine))
										.setUsed(false);
								shouldContinue = true;
								break;
							}

							execResources.get(execResources.indexOf(resource))
									.setUsed(true);
							execResources.get(execResources.indexOf(resource))
									.setUsedBy(tmpTest);

							System.out
									.println("Setting resource "
											+ execResources.get(
													execResources
															.indexOf(resource))
													.getName()
											+ " used by test "
											+ execResources
													.get(execResources
															.indexOf(resource))
													.getUsedBy().getName()
											+ " to true");
						}
						if (shouldContinue) {
							for (Resource resource : execResources) {
								if (resource.getUsedBy() != null
										&& resource.getUsedBy().equals(tmpTest)) {
									System.out.println("Setting resource "
											+ resource.getName()
											+ " used by test "
											+ tmpTest.getName() + " to false");
									resource.setUsed(false);
									resource.setUsedBy(null);
								}
							}
							continue;
						}

					}

					tmpTestList.remove(tmpTest);
					randomTestNumList = helper
							.randomNumberGenerator(tmpTestList.size());

					System.out.print("Old start exec time for test "
							+ tmpTest.getName() + " was "
							+ tmpTest.getStartExec());

					tmpTest.setStartExec(execMachines.get(
							execMachines.indexOf(randomMachine))
							.getMaxExecTime() > tmpTest.getStartExec() ? execMachines
							.get(execMachines.indexOf(randomMachine))
							.getMaxExecTime() : tmpTest.getStartExec());

					System.out.println(" and new is " + tmpTest.getStartExec());

					System.out.print("Old max exec time for machine "
							+ execMachines.get(
									execMachines.indexOf(randomMachine))
									.getName()
							+ " assigned to test "
							+ tmpTest.getName()
							+ " was "
							+ execMachines.get(
									execMachines.indexOf(randomMachine))
									.getMaxExecTime());

					execMachines.get(execMachines.indexOf(randomMachine))
							.setMaxExecTime(
									tmpTest.getStartExec()
											+ tmpTest.getTimeLength());

					System.out.println(" and new is "
							+ execMachines.get(
									execMachines.indexOf(randomMachine))
									.getMaxExecTime());

					startSolution.getTestExecList().add(tmpTest);
				}
			}
			machineTmpList.addAll(machines);

			// All the tests that weren't assigned in the iteration
			// Should have their start exec time increased to the max
			// exec time of the machine where a test that used the resources
			// was executed

			for (Test test : tmpTestList) {
				int maxWaitingTime = 0;
				if (test.getReqResources() != null) {
					for (Resource res : test.getReqResources()) {
						if (execResources.get(execResources.indexOf(res))
								.isUsed()) {
							if (maxWaitingTime < execResources
									.get(execResources.indexOf(res))
									.getUsedBy().getExecMachine()
									.getMaxExecTime()) {
								maxWaitingTime = execResources
										.get(execResources.indexOf(res))
										.getUsedBy().getExecMachine()
										.getMaxExecTime();
							}
						}
					}
					if (maxWaitingTime != 0) {
						System.out.print("Old start exec time for test "
								+ test.getName() + " after one iteration was "
								+ test.getStartExec());

						test.setStartExec(maxWaitingTime);

						System.out
								.println(" and new is " + test.getStartExec());
					}
				}
			}

			execResources.forEach(res -> {
				res.setUsed(false);
				res.setUsedBy(null);
			});
		}
		System.out.println();
		startSolution.getUsedMachines().addAll(execMachines);
		startSolution.getUsedResources().addAll(execResources);

		for (Test test : startSolution.getTestExecList()) {
			System.out.println(test.getName() + ' ' + test.getStartExec() + ' '
					+ test.getExecMachine().getName());
		}

		startSolution.setExecTime(calculateMaxExecTime(startSolution
				.getUsedMachines()));
		
		startSolution.getUsedMachines().forEach(m -> m.setUsed(false));
		
		startSolution.getUsedResources().forEach(r -> {
			r.setUsed(false);
			r.setUsedBy(null);
		});

		System.out.println();
		System.out.println(startSolution.getExecTime());
		System.out.println();

		return startSolution;
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
