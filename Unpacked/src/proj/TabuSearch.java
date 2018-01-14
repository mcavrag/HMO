package proj;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.MaximizeAction;

public class TabuSearch {

	class IntHolder {
		public Integer value = new Integer(0);
	}

	private TabuList tabuList;
	private int numberOfNeighbors;
	final long stopCriteria;

	int iterationCount = 1;

	public TabuSearch(int maxTabuLength, int numberOfNeighbours,
			int stopCriteria) {
		tabuList = new TabuList(maxTabuLength);
		this.numberOfNeighbors = numberOfNeighbours;

		// this.stopCriteria = System.nanoTime() + stopCriteria * 1000 * 1000 *
		// 1000L;

		this.stopCriteria = (long) (System.nanoTime() + 60 * 1000 * 1000 * 1000L);
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

			if (bestNeighbourFound.getExecTime() == Integer.MAX_VALUE) {
				currentSolution = startSolution;
			}

			iterationCount++;

			if (iterationCount == 2) {
				System.out.println("Prvu prosao.");
			}

		} while (System.nanoTime() < stopCriteria);

		System.out.println("Best solution execution time is: "
				+ bestSolution.getExecTime());

		Utility helper = new Utility();
		helper.printOutSolution(bestSolution);
		// printOut(bestSolution);

		for (Test test : bestSolution.getTestExecList()) {
			System.out.println(test.getName() + ' ' + test.getStartExec() + ' '
					+ test.getExecMachine().getName());
		}

		return bestSolution;
	}

	private Solution findBestNeighbor(ArrayList<Solution> candidateNeighbors,
			ArrayList<Solution> solutionsInTabu) {

		candidateNeighbors
				.removeIf(sol -> checkIfContains(solutionsInTabu, sol));

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

	int returnFirst(int x, int y) {
		return x;
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

			int count = 0;

			Solution neighbor = new Solution();

			ArrayList<Test> tests = new ArrayList<Test>();

			helper.copyTestLists(tests, currentSolution.getTestExecList());

			ArrayList<Machine> tmpListMachines = new ArrayList<Machine>();

			helper.copyMachineLists(tmpListMachines,
					currentSolution.getUsedMachines());

			neighbor.setTestExecList(tests);
			neighbor.setUsedMachines(tmpListMachines);

			while (count < 1) {

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

					if (m1.getName().equals(m2.getName()))
						continue;

					// System.out.println(m1.getName() + " " + m2.getName());

					// regardless of solution feasibility, tests are swapped
					test1.setExecMachine(m2);
					test2.setExecMachine(m1);

					int oldTimeEnd1 = test1.getStartExec()
							+ test1.getTimeLength();

					int oldTimeEnd2 = test2.getStartExec()
							+ test2.getTimeLength();

					int swappedTime1 = test1.getStartExec();
					int swappedTime2 = test2.getStartExec();

					test1.setStartExec(swappedTime2);
					test2.setStartExec(swappedTime1);

					int newTimeEnd1 = test1.getStartExec()
							+ test1.getTimeLength();

					int newTimeEnd2 = test2.getStartExec()
							+ test2.getTimeLength();

					int operation1;
					int operation2;

					if (newTimeEnd1 > oldTimeEnd2)
						operation1 = 1;
					else if (newTimeEnd1 < oldTimeEnd2)
						operation1 = 2;
					else
						operation1 = 0;

					if (operation1 != 0) {
						checkTestsAfter(
								test1,
								neighbor.getTestExecList(),
								neighbor.getUsedMachines().get(
										neighbor.getUsedMachines().indexOf(m2)),
								operation1, false);
					}

					if (newTimeEnd2 > oldTimeEnd1)
						operation2 = 1;
					else if (newTimeEnd2 < oldTimeEnd1)
						operation2 = 2;
					else
						operation2 = 0;

					if (operation2 != 0) {
						checkTestsAfter(
								test2,
								neighbor.getTestExecList(),
								neighbor.getUsedMachines().get(
										neighbor.getUsedMachines().indexOf(m1)),
								operation2, false);
					}

					calculateMachineExecTime(neighbor.getTestExecList(),
							neighbor.getUsedMachines());

					if (test2.canAssignToMachine(m1)
							&& test1.canAssignToMachine(m2)
							&& !checkIfResourceCollision(neighbor)) {

						neighbor.setExecTime(calculateMaxExecTime(neighbor
								.getUsedMachines()));
						// System.out.println("Tests that were changed were: "
						// + test1.getName() + " and " + test2.getName());
					} else {
						neighbor.setExecTime(Integer.MAX_VALUE);
						break;
					}

					if (neighbor.getExecTime() != Integer.MAX_VALUE) {

						for (Test test : neighbor.getTestExecList()) {
							if (test.getExecMachine().getName()
									.equals(test1.getExecMachine().getName())
									&& checkTimeDiff(test, test1)) {
								System.out.println("Zajeb");
								System.out.println(test.getStartExec()
										+ " --- " + test1.getStartExec());
								System.out.println(test.getName() + " - "
										+ test1.getName() + " on operation "
										+ operation1);
								System.out.println("Swapped Tests were "
										+ test1.getName() + "("
										+ test1.getStartExec() + ")" + " and "
										+ test2.getName() + "("
										+ test2.getStartExec() + ")"
										+ test1.getExecMachine().getName()
										+ "<->"
										+ test2.getExecMachine().getName());
								System.out.println("Old time for t1 is "
										+ oldTimeEnd1 + " and new for t2 is "
										+ newTimeEnd2);
								System.out.println("Old time for t2 is "
										+ oldTimeEnd2 + " and new for t1 is "
										+ newTimeEnd1);

								checkTestsAfter(
										test1,
										neighbor.getTestExecList(),
										neighbor.getUsedMachines().get(
												neighbor.getUsedMachines()
														.indexOf(m2)),
										operation1, true);

								System.out.println("Zeznulo se u "
										+ iterationCount);
								neighbor.setExecTime(Integer.MAX_VALUE);
							}
						}
						for (Test test : neighbor.getTestExecList()) {
							if (test.getExecMachine().getName()
									.equals(test2.getExecMachine().getName())
									&& checkTimeDiff(test, test2)) {
								System.out.println("Zajeb2");
								System.out.println(test.getStartExec()
										+ " --- " + test2.getStartExec());
								System.out.println(test.getName() + " - "
										+ test2.getName() + " on operation "
										+ operation2);
								System.out.println("Swapped Tests were "
										+ test1.getName() + "("
										+ test1.getStartExec() + ")" + " and "
										+ test2.getName() + "("
										+ test2.getStartExec() + ")"
										+ test1.getExecMachine().getName()
										+ "<->"
										+ test2.getExecMachine().getName());

								System.out.println("Old time for t1 is "
										+ oldTimeEnd1 + " and new for t2 is "
										+ newTimeEnd2);
								System.out.println("Old time for t2 is "
										+ oldTimeEnd2 + " and new for t1 is "
										+ newTimeEnd1);

								checkTestsAfter(
										test2,
										neighbor.getTestExecList(),
										neighbor.getUsedMachines().get(
												neighbor.getUsedMachines()
														.indexOf(m1)),
										operation2, true);

								System.out.println("Zeznulo se u "
										+ iterationCount);
								neighbor.setExecTime(Integer.MAX_VALUE);
							}
						}

					}
					count++;
				}
			}
			if (!checkIfContains(neighbors, neighbor)) {
				// printOut(neighbor);
				neighbors.add(neighbor);
				i++;
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
				if (test.getExecMachine().getName().equals(machine.getName()))
					machine.setMaxExecTime(test.getStartExec()
							+ test.getTimeLength());
			}
		}
	}

	private boolean checkIfResourceCollision(Solution neighbor) {
		for (Test test1 : neighbor.getTestExecList()) {
			for (Test test2 : neighbor.getTestExecList()) {
				if (!test1.getName().equals(test2.getName())
						&& (test1.getReqResources() != null)
						&& (test2.getReqResources() != null)
						&& checkTimeDiff(test1, test2)) {
					for (Resource res1 : test2.getReqResources()) {
						for (Resource res2 : test1.getReqResources()) {
							if (res2.getName().equals(res1.getName()))
								return true;
						}
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

		if ((swappedTestEnd > test.getStartExec())
				&& (swappedTestEnd < testEnd)) {
			return true;
		} else if ((swappedTest.getStartExec() > test.getStartExec())
				&& (swappedTest.getStartExec() < testEnd)) {
			return true;
		} else if ((test.getStartExec() > swappedTest.getStartExec())
				&& testEnd < swappedTestEnd) {
			return true;
		} else if (swappedTestEnd == testEnd
				&& test.getStartExec() > swappedTest.getStartExec()) {
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

	// When you find a collision, swap all tests inside the collision and
	// return the last test swapped. After that check all the tests between
	// the swapped tests and their collision and do that until the last test
	// is checked.

	private void checkTestsAfter(Test swappedTest, ArrayList<Test> testList,
			Machine machine, int operation, boolean debug) {

		int timeDiff = 0;
		int swappedTestEnd = swappedTest.getStartExec()
				+ swappedTest.getTimeLength();

		int originalSwappedLen = swappedTest.getTimeLength();

		Collections.sort(testList, new Comparator<Test>() {
			@Override
			public int compare(Test t1, Test t2) {
				return Integer.compare(t1.getStartExec(), t2.getStartExec());
			}
		});

		if (debug) {
			for (Test test : testList) {
				System.out.println(test.getName() + " " + test.getStartExec()
						+ " " + test.getExecMachine().getName());
			}
		}

		for (Test test : testList) {
			if (test.getExecMachine().equals(machine)
					&& (!test.getName().equals(swappedTest.getName()))) {

				if (checkTimeDiff(test, swappedTest) && (operation == 1)) {
					timeDiff = swappedTestEnd - test.getStartExec();
					if (debug)
						System.out.println("stara vremena za " + test.getName()
								+ " " + test.getStartExec() + " --- "
								+ swappedTest.getName() + " "
								+ swappedTest.getTimeLength());
					test.setStartExec(test.getStartExec() + timeDiff);
					if (debug)
						System.out.println("usao + " + test.getName() + " - "
								+ swappedTest.getName());
					swappedTest.setTimeLength(swappedTest.getTimeLength()
							+ test.getTimeLength());
					swappedTestEnd = swappedTest.getStartExec()
							+ swappedTest.getTimeLength();
					if (debug)
						System.out.println("nova vremena za " + test.getName()
								+ " " + test.getStartExec() + " --- "
								+ swappedTest.getName() + " "
								+ swappedTest.getTimeLength());
				} else if (!checkTimeDiff(test, swappedTest)
						&& (operation == 1)) {
					if (debug) {
						System.out.println("usao + " + test.getName() + " - "
								+ swappedTest.getName());
						System.out.println(test.getStartExec() + "->"
								+ test.getStartExec() + test.getTimeLength()
								+ " ---- " + swappedTest.getStartExec() + "->"
								+ swappedTest.getStartExec()
								+ swappedTest.getTimeLength());

					}

				}

				if (test.getStartExec() > swappedTestEnd
						&& (test.getReqResources() == null) && (operation == 2)) {
					timeDiff = test.getStartExec() - swappedTestEnd;
					// System.out.println(test.getStartExec() + " "
					// + swappedTest.getStartExec());
					test.setStartExec(test.getStartExec() - timeDiff);
					// System.out.println("usao - " + test.getName() + " - "
					// + swappedTest.getName());
					swappedTest = test;
					swappedTestEnd = swappedTest.getStartExec()
							+ swappedTest.getTimeLength();
				}
			}
		}

		if (swappedTest.getTimeLength() != originalSwappedLen && operation == 1) {
			swappedTest.setTimeLength(originalSwappedLen);
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

								if (tmpTest.getStartExec() < usedBy
										.getTimeLength()
										+ usedBy.getStartExec())
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
										&& resource.getUsedBy().getName()
												.equals(tmpTest.getName())) {
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

						if (test.getStartExec() < maxWaitingTime)
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
			System.out.println("'" + test.getName() + "',"
					+ test.getStartExec() + ",'"
					+ test.getExecMachine().getName() + "'");
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
