package proj;

public class Machine {

	private String name;
	private int maxExecTime;
	private boolean isUsed;

	public Machine() {
		name = null;
		maxExecTime = 0;
		isUsed = false;
	}

	public Machine(String name) {
		this.name = name;
		maxExecTime = 0;
		setUsed(false);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getMaxExecTime() {
		return maxExecTime;
	}

	public void setMaxExecTime(int maxExecTime) {
		this.maxExecTime = maxExecTime;
	}

	public boolean isUsed() {
		return isUsed;
	}

	public void setUsed(boolean isUsed) {
		this.isUsed = isUsed;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Machine other = (Machine) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
}
