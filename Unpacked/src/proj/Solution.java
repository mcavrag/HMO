package proj;

import java.util.ArrayList;

public class Solution {

	// Maybe should add ArrayList of Machines so the original list is untouched?
	private ArrayList<Test> testExecList;
	private int execTime;

	public Solution(ArrayList<Test> testExecList, int execTime,
			ArrayList<Machine> machineExecList) {
		this.testExecList = testExecList;
		this.execTime = execTime;
	}

	public Solution() {
		testExecList = new ArrayList<Test>();
		execTime = 0;
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
}
