package proj;

public class Resource {
	private String name;
	private int quantity;
	private boolean isUsed;
	private Test usedBy;

	public Resource() {
		this.name = null;
		this.quantity = 0;
		this.isUsed = false;
		this.usedBy = null;
	}
	
	public Resource(String name, int quantity) {
		this.name = name;
		this.quantity = quantity;
		this.isUsed = false;
		this.usedBy = null;
	}
	
	public Resource(String name) {
		this.name = name;
		this.quantity = 1;
		this.isUsed = false;
		this.usedBy = null;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	
	public boolean isUsed() {
		return isUsed;
	}

	public void setUsed(boolean isUsed) {
		this.isUsed = isUsed;
	}

	public Test getUsedBy() {
		return usedBy;
	}

	public void setUsedBy(Test usedBy) {
		this.usedBy = usedBy;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + quantity;
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
		Resource other = (Resource) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name.trim()))
			return false;
		if (quantity != other.quantity)
			return false;
		return true;
	}
}
