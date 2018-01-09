package proj;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.MaximizeAction;

public class TabuSearch {

	private TabuList tabuList;
	private int numberOfNeighbors;
	final long stopCriteria;

	public TabuSearch(int maxTabuLength, int numberOfNeighbours,
			int stopCriteria) {
		tabuList = new TabuList(maxTabuLength);
		this.numberOfNeighbors = numberOfNeighbours;
		
		//this.stopCriteria = System.nanoTime() + stopCriteria * 1000 * 1000 * 1000L;
		
		this.stopCriteria = (long) (System.nanoTime() + 0.01 * 1000 * 1000 * 1000L);
	}

	public Solution run(Solution startSolution) {

		Solution bestSolution = startSolution;
		Solution currentSolution = startSolution;

		System.out.println("Start solution execution time is: "
				+ startSolution.getExecTime());

		do {

			ArrayList<Solution> candidateNeighbors = generateNeighbors(currentSolution);
			ArrayList<Solution> solutionsInTabu = new ArrayList<Solution>();

			tabuList.forEach(solutionsInTabu::add);

			Solution bestNeighbourFound = findBestNeighbor(candidateNeighbors,
					solutionsInTabu);

			if (bestNeighbourFound.getExecTime() <= bestSolution.getExecTime()) {
				bestSolution = bestNeighbourFound;
			}

			tabuList.add(bestNeighbourFound);

			currentSolution = bestNeighbourFound;
			
			if(bestNeighbourFound.getExecTime() == Integer.MAX_VALUE) System.out.println("Sjeba san.");

		} while (System.nanoTime() < stopCriteria);

		System.out.println("Best solution execution time is: "
				+ bestSolution.getExecTime());
		
		return bestSolution;
	}

	private Solution findBestNeighbor(ArrayList<Solution> candidateNeighbors,
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

			Solution neighbor = new Solution();

			ArrayList<Test> tests = new ArrayList<Test>();

			helper.copyTestLists(tests, currentSolution.getTestExecList());

			ArrayList<Machine> tmpListMachines = new ArrayList<Machine>();

			helper.copyMachineLists(tmpListMachines,
					currentSolution.getUsedMachines());

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

				Machine m1 = test1.getExecMachine();
				Machine m2 = test2.getExecMachine();

				// System.out.println(m1.getName() + " " + m2.getName());

				// regardless of solution feasibility, tests are swapped
				test1.setExecMachine(m2);
				test2.setExecMachine(m1);

				int swappedTime = test1.getStartExec();

				test1.setStartExec(test2.getStartExec());
				test2.setStartExec(swappedTime);

				checkTestsAfter(test1, neighbor.getTestExecList(), neighbor
						.getUsedMachines().get(neighbor.getUsedMachines().indexOf(m2)));

				checkTestsAfter(test2, neighbor.getTestExecList(), neighbor
						.getUsedMachines().get(neighbor.getUsedMachines().indexOf(m1)));
				
				calculateMachineExecTime(neighbor.getTestExecList(), neighbor.getUsedMachines());

				if (test2.canAssignToMachine(test1.getExecMachine())
						&& test1.canAssignToMachine(test2.getExecMachine())
						&& !checkIfResourceCollision(neighbor, test1)
						&& !checkIfResourceCollision(neighbor, test2)) {

					neighbor.setExecTime(calculateMaxExecTime(neighbor
							.getUsedMachines()));
				} else {
					neighbor.setExecTime(Integer.MAX_VALUE);
				}

				if (!checkIfContains(neighbors, neighbor)) {
					System.out.println("Test that were changed were: " +
					test1.getName() + " and " + test2.getName());
					
					printOut(neighbor);
					neighbors.add(neighbor);
					i++;
				} else {
					System.out.println("WTF");
				}
			}
		}

		return neighbors;
	}

	private void calculateMachineExecTime(ArrayList<Test> testExecList,
			ArrayList<Machine> usedMachines) {
		
		Collections.sort(testExecList, new Comparator<Test>() {
			@Override
			public int compare(Test t1, Test t2) {
				return Integer.compare(t1.getStartExec(), t2.getStartExec());
			}
		});
		for (Machine machine : usedMachines) {
			machine.setMaxExecTime(0);
			for (Test test : testExecList) {
				if(test.getExecMachine().getName().equals(machine.getName())) 
					machine.setMaxExecTime(test.getStartExec() + test.getTimeLength());
			}
		}
	}

	private boolean checkIfResourceCollision(Solution neighbor, Test swappedTest) {
		for (Test test : neighbor.getTestExecList()) {
			if (!test.getName().equals(swappedTest.getName())
					&& (test.getReqResources() != null)
					&& (swappedTest.getReqResources() != null)
					&& checkTimeDiff(test, swappedTest)) {
				for (Resource res : swappedTest.getReqResources()) {
					if (test.getReqResources().contains(res)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	private boolean checkTimeDiff(Test test, Test swappedTest) {
		int testEnd = test.getStartExec() + test.getTimeLength();
		int swappedTestEnd = swappedTest.getStartExec()
				+ swappedTest.getTimeLength();

		if (swappedTest.getStartExec() == test.getStartExec()
				|| swappedTestEnd == testEnd) {
			return true;
		} else if ((swappedTestEnd > test.getStartExec())
				&& (swappedTestEnd < testEnd)) {
			return true;
		} else if ((swappedTest.getStartExec() > test.getStartExec())
				&& (swappedTest.getStartExec() < testEnd)) {
			return true;
		}

		return false;
	}

	private boolean checkIfContains(ArrayList<Solution> neighbors,
			Solution neighbor) {
		for (Solution n : neighbors) {
			if (n.equals(neighbor))
				return true;
		}
		return false;
	}

	private void checkTestsAfter(Test swappedTest, ArrayList<Test> testList,
			Machine machine) {

		int timeDiff = 0;
		int swappedTestEnd = swappedTest.getStartExec()
				+ swappedTest.getTimeLength();

		for (Test test : testList) {
			if (test.getExecMachine().equals(machine)
					&& (!test.getName().equals(swappedTest.getName()))) {
				if (checkTimeDiff(test, swappedTest)) {
					timeDiff = Math.abs(swappedTestEnd - test.getStartExec());
					System.out.println(test.getStartExec() + " " + swappedTest.getStartExec());
					test.setStartExec(test.getStartExec() + timeDiff);
					System.out.println("usao " + test.getName() + " - " + swappedTest.getName());
					swappedTest = test;
					swappedTestEnd = swappedTest.getStartExec()
							+ swappedTest.getTimeLength();
				} else if (test.getStartExec() > swappedTestEnd
						&& (test.getReqResources() == null)) {

					timeDiff = test.getStartExec() - swappedTestEnd;
					test.setStartExec(test.getStartExec() - timeDiff);
					swappedTest = test;
					swappedTestEnd = swappedTest.getStartExec()
							+ swappedTest.getTimeLength();
				}
			}
		}
	}

	private void printOut(Solution neighbor) {
		ArrayList<Test> sortedTestList = new ArrayList<Test>();
		sortedTestList.addAll(neighbor.getTestExecList());

		Collections.sort(sortedTestList, new Comparator<Test>() {
			@Override
			public int compare(Test s1, Test s2) {
				return Integer.compare(s1.getStartExec(), s2.getStartExec());
			}
		});

		for (Test test : sortedTestList) {
			System.out.print("Test " + test.getName()
					+ " started executing at " + test.getStartExec()
					+ " on machine " + test.getExecMachine().getName()
					+ " and executed for " + test.getTimeLength());
			if (test.getReqResources() != null) {
				System.out.print(" and uses resources: ");
				for (Resource res : test.getReqResources()) {
					System.out.print(res.getName() + " ");
				}
			}
			System.out.println();
		}
		System.out.println("Max exec time for this neighbor is "
				+ neighbor.getExecTime());
		System.out.println();
	}

	public Solution generateStartSolution(ArrayList<Test> tests,
			ArrayList<Machine> machines, ArrayList<Resource> resources) {

		Utility helper = new Utility();

		Solution startSolution = new Solution();

		ArrayList<Test> tmpTestList = new ArrayList<Test>();
		helper.copyTestLists(tmpTestList, tests);

		ArrayList<Machine> execMachines = new ArrayList<Machine>();
		helper.copyMachineLists(execMachines, machines);

		ArrayList<Machine> machineTmpList = new ArrayList<Machine>();
		machineTmpList.addAll(execMachines);

		ArrayList<Resource> execResources = new ArrayList<Resource>();
		execResources.addAll(resources);

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

					Machine usedMachine = execMachines.get(execMachines
							.indexOf(randomMachine));

					if (machineTmpList.contains(randomMachine)) {
						System.out.println("Assigned test " + tmpTest.getName()
								+ " to machine " + randomMachine.getName());

						tmpTest.setExecMachine(randomMachine);
						usedMachine.setUsed(true);
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

							Resource usedResource = execResources
									.get(execResources.indexOf(resource));
							Test usedBy = execResources.get(
									execResources.indexOf(resource))
									.getUsedBy();

							if (execResources.get(
									execResources.indexOf(resource)).isUsed()) {
								tmpTest.setExecMachine(null);
								System.out
										.print("Couldn't assign resources to test "
												+ tmpTest.getName()
												+ " old start exec time was "
												+ tmpTest.getStartExec());

								tmpTest.setStartExec(usedBy.getTimeLength()
										+ usedBy.getStartExec());

								System.out.println(" and new is "
										+ tmpTest.getStartExec());

								machineTmpList.add(randomMachine);
								usedMachine.setUsed(false);
								shouldContinue = true;
								break;
							}

							usedResource.setUsed(true);

							usedResource.setUsedBy(tmpTest);

							System.out.println("Setting resource "
									+ usedResource.getName() + " used by test "
									+ usedResource.getUsedBy().getName()
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

					tmpTest.setStartExec(usedMachine.getMaxExecTime() > tmpTest
							.getStartExec() ? usedMachine.getMaxExecTime()
							: tmpTest.getStartExec());

					System.out.println(" and new is " + tmpTest.getStartExec());

					System.out.print("Old max exec time for machine "
							+ usedMachine.getName() + " assigned to test "
							+ tmpTest.getName() + " was "
							+ usedMachine.getMaxExecTime());

					usedMachine.setMaxExecTime(tmpTest.getStartExec()
							+ tmpTest.getTimeLength());

					System.out.println(" and new is "
							+ usedMachine.getMaxExecTime());

					startSolution.getTestExecList().add(tmpTest);
				}
			}
			machineTmpList.addAll(execMachines);

			// All the tests that weren't assigned in the iteration
			// Should have their start exec time increased to the max
			// exec time of the machine where a test that used the resources
			// was executed

			for (Test test : tmpTestList) {
				int maxWaitingTime = 0;
				if (test.getReqResources() != null) {
					for (Resource res : test.getReqResources()) {
						Resource usedResource = execResources.get(execResources
								.indexOf(res));
						Test usedTest = usedResource.getUsedBy();

						if (usedResource.isUsed()) {
							Machine usedMachine = execMachines.get(execMachines
									.indexOf(usedTest.getExecMachine()));
							if (maxWaitingTime < usedMachine.getMaxExecTime()) {
								maxWaitingTime = usedMachine.getMaxExecTime();
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
		startSolution.setUsedMachines(execMachines);

		for (Test test : startSolution.getTestExecList()) {
			System.out.println(test.getName() + ' ' + test.getStartExec() + ' '
					+ test.getExecMachine().getName());
		}

		startSolution.setExecTime(calculateMaxExecTime(startSolution
				.getUsedMachines()));

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
