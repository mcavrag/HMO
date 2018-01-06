package proj;

import java.util.ArrayList;

public class Test implements Cloneable{

	private String name;
	private int timeLength;
	private int startExec = 0;
	private ArrayList<Machine> usableMachines = null;
	private ArrayList<Resource> reqResources = null;
	private Machine execMachine = null;

	public Test() {
	}

	public Test(String name, int timeLength, ArrayList<Machine> usableMachines,
			ArrayList<Resource> reqResources) {
		this.name = name;
		this.timeLength = timeLength;
		this.usableMachines = usableMachines;
		this.reqResources = reqResources;
	}

	public Test(Test test) {
		this.name = test.name;
		this.timeLength = test.timeLength;
		this.usableMachines = test.usableMachines;
		this.reqResources = test.reqResources;
	}

	@Override
	protected Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getTimeLength() {
		return timeLength;
	}

	public void setTimeLength(int timeLength) {
		this.timeLength = timeLength;
	}

	public Machine getExecMachine() {
		return execMachine;
	}

	public void setExecMachine(Machine execMachine) {
		this.execMachine = execMachine;
	}

	public ArrayList<Machine> getUsableMachines() {
		return usableMachines;
	}

	public void setUsableMachines(ArrayList<Machine> usableMachines) {
		this.usableMachines = usableMachines;
	}

	public ArrayList<Resource> getReqResources() {
		return reqResources;
	}

	public void setReqResources(ArrayList<Resource> reqResources) {
		this.reqResources = reqResources;
	}

	public boolean canAssignToMachine(Machine machine) {
		return usableMachines.contains(machine);
	}

	public boolean canAssignResource(Resource resource) {
		return reqResources.contains(resource);
	}

	public int getStartExec() {
		return startExec;
	}

	public void setStartExec(int startExec) {
		this.startExec = startExec;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((execMachine == null) ? 0 : execMachine.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((reqResources == null) ? 0 : reqResources.hashCode());
		result = prime * result + startExec;
		result = prime * result + timeLength;
		result = prime * result
				+ ((usableMachines == null) ? 0 : usableMachines.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Test))
			return false;
		Test other = (Test) obj;
		if (execMachine == null) {
			if (other.execMachine != null)
				return false;
		} else if (!execMachine.equals(other.execMachine))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (reqResources == null) {
			if (other.reqResources != null)
				return false;
		} else if (!reqResources.equals(other.reqResources))
			return false;
		if (startExec != other.startExec)
			return false;
		if (timeLength != other.timeLength)
			return false;
		if (usableMachines == null) {
			if (other.usableMachines != null)
				return false;
		} else if (!usableMachines.equals(other.usableMachines))
			return false;
		return true;
	}

}
