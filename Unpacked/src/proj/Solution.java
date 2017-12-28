package proj;

import java.util.ArrayList;

public class Solution {

	private ArrayList<Test> testExecList;
	private ArrayList<Machine> usedMachines;
	private ArrayList<Resource> usedResources;
	private int execTime;

	public Solution() {
		testExecList = new ArrayList<Test>();
		execTime = 0;
		usedMachines = new ArrayList<Machine>();
		usedResources = new ArrayList<Resource>();
	}

	public ArrayList<Test> getTestExecList() {
		return testExecList;
	}

	public void setTestExecList(ArrayList<Test> testExecList) {
		this.testExecList = testExecList;
	}

	public int getExecTime() {
		return execTime;
	}

	public void setExecTime(int execTime) {
		this.execTime = execTime;
	}
	
	public void setUsedMachines(ArrayList<Machine> machines) {
		this.usedMachines = machines;
	}
	
	public ArrayList<Machine> getUsedMachines() {
		return this.usedMachines;
	}
	
	public void addResource(Resource r) {
		this.usedResources.add(r);
	}
	
	public ArrayList<Resource> getUsedResources() {
		return this.usedResources;
	}
	
	public void setUsedResources(ArrayList<Resource> resources) {
		this.usedResources = resources;
	}
}
