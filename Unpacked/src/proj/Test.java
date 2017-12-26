package proj;

import java.util.ArrayList;

public class Test {

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

}
