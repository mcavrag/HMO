package proj;

import java.util.ArrayList;

public class Solution {

	private ArrayList<Test> testExecList;
	private ArrayList<Machine> usedMachines;
	private int execTime;

	public Solution() {
		testExecList = new ArrayList<Test>();
		execTime = 0;
		usedMachines = new ArrayList<Machine>();
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
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + execTime;
		result = prime * result
				+ ((testExecList == null) ? 0 : testExecList.hashCode());
		result = prime * result
				+ ((usedMachines == null) ? 0 : usedMachines.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Solution))
			return false;
		Solution other = (Solution) obj;
		if (execTime != other.execTime)
			return false;
		if (testExecList == null) {
			if (other.testExecList != null)
				return false;
		} else if (!testExecList.equals(other.testExecList))
			return false;
		if (usedMachines == null) {
			if (other.usedMachines != null)
				return false;
		} else if (!usedMachines.equals(other.usedMachines))
			return false;
		return true;
	}
	
	
}
