package proj;

public class Machine implements Cloneable {

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
		if (this.name == null) {
			if (other.getName() != null)
				return false;
		} else if (!this.name.equals(other.getName()))
			return false;
		return true;
	}
}
