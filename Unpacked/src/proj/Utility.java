package proj;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Utility {

	public boolean parseInput(String inputFileName, ArrayList<Test> tests,
			ArrayList<Machine> machines, ArrayList<Resource> resources) {
		File testSequenceFile = new File(inputFileName);
		String parseLine[] = null;
		BufferedReader reader = null;

		try {
			reader = new BufferedReader(new FileReader(testSequenceFile));
			String text = null;

			if ((text = reader.readLine()) != null) {
				if (!text.contains("Testsuite"))
					return false;
			}
			if ((text = reader.readLine()) != null) {
				if (!text.contains("Number of tests"))
					return false;
			}
			
			if ((text = reader.readLine()) != null) {
				if (!text.contains("Number of machines"))
					return false;
			}
			if ((text = reader.readLine()) != null) {
				if (!text.contains("Number of resources"))
					return false;
			}
			
			if ((text = reader.readLine()) != null) {
				if (!("".equals(text.trim())))
					return false;
			}

			while (!(text = reader.readLine()).equals("")) {
				String tmpParseTest = text.substring(text.indexOf('(') + 1,
						text.indexOf(')'));
				parseLine = tmpParseTest.split("," + "(?![^\\[]*\\])");

				Test newTest = new Test();

				String testName = parseLine[0].replace("'", "").trim();
				int testLength = Integer.parseInt(parseLine[1].trim());

				newTest.setName(testName);
				newTest.setTimeLength(testLength);

				String tmpParse = parseLine[2].trim().replace("[", "");
				tmpParse = tmpParse.replace("]", "");

				if (!("".equals(tmpParse))) {
					String parseMachines[] = tmpParse.trim().split(",");
					ArrayList<Machine> usableMachines = new ArrayList<Machine>();
					for (String string : parseMachines) {
						Machine newMachine = new Machine(string
								.replace("'", "").trim());
						usableMachines.add(newMachine);
					}
					newTest.setUsableMachines(usableMachines);
				}

				tmpParse = parseLine[3].trim().replace("[", "");
				tmpParse = tmpParse.replace("]", "");

				if (!("".equals(tmpParse))) {
					String parseResources[] = tmpParse.trim().split(",");
					ArrayList<Resource> reqResources = new ArrayList<Resource>();
					for (String string : parseResources) {
						Resource newResource = new Resource(string.replace("'",
								"").trim(), 1);
						reqResources.add(newResource);
					}
					newTest.setReqResources(reqResources);
				}
				tests.add(newTest);
			}

			while (!(text = reader.readLine()).equals("")) {
				String tmpParse = text
						.substring(text.indexOf('(') + 1, text.indexOf(')'))
						.trim().replace("'", "");

				Machine newMachine = new Machine(tmpParse);

				machines.add(newMachine);
			}

			for (Test test : tests) {
				if (test.getUsableMachines() == null)
					test.setUsableMachines(machines);
			}

			while ((text = reader.readLine()) != null) {
				String tmpParse = text.substring(text.indexOf('(') + 1,
						text.indexOf(')'));
				parseLine = tmpParse.split("," + "(?![^\\[]*\\])");


				String resourceName = parseLine[0].replace("'", "").trim();
				int resourceQuantity = Integer.parseInt(parseLine[1].trim());

				Resource newResource = new Resource(resourceName, resourceQuantity);

				resources.add(newResource);
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException e) {
				return false;
			}
		}

		return true;
	}
	
	public boolean writeToFile(String fileName)
	{
		return true;
	}
	
	public ArrayList<Integer> randomNumberGenerator(int numbers) {
		ArrayList<Integer> list = new ArrayList<Integer>();
        for (int i=0; i < numbers; i++) {
            list.add(new Integer(i));
        }
        Collections.shuffle(list);
	    return list;
	}

	public void copyTestLists(ArrayList<Test> tests, ArrayList<Test> testExecList) {
		
		for (Test test : testExecList) {
			tests.add((Test) test.clone());
		}
	}
	public void copyMachineLists(ArrayList<Machine> machines, ArrayList<Machine> usedMachinesList) {
		
		for (Machine machine : usedMachinesList) {
			machines.add((Machine) machine.clone());
		}
	}
	
	public void printOutSolution(Solution solution) {
		File outputFile = new File("out.txt");
		BufferedWriter out;
		try {
			out = new BufferedWriter(new FileWriter(outputFile));
			ArrayList<Test> testList = solution.getTestExecList();
			
			Collections.sort(testList, new Comparator<Test>() {
				@Override
				public int compare(Test s1, Test s2) {
					return Integer.compare(s1.getStartExec(), s2.getStartExec());
				}
			});
			
			for (Test t : testList) {
				out.write("'" + t.getName() + "'," + t.getStartExec() + ",'" + t.getExecMachine().getName() + "'\n");
			}
			
			out.flush();
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
