package proj;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

public class TabuList implements Iterable<Solution> {

	private Queue<Solution> tabuList;
	private int maxSize;

	public TabuList(int maxSize) {
		tabuList = new LinkedList<Solution>();
		this.maxSize = maxSize;
	}

	@Override
	public Iterator<Solution> iterator() {
		return tabuList.iterator();
	}

	public void add(Solution solution) {
		if (tabuList.size() > maxSize) {
			tabuList.poll();
		}
		tabuList.add(solution);
	}

	public boolean contains(Solution solution) {
		return tabuList.contains(solution);
	}
}
