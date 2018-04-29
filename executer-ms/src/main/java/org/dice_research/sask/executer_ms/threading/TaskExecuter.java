package org.dice_research.sask.executer_ms.threading;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TaskExecuter {

	private ExecutorService executorService;
	private Set<Runnable> taskSet;
	
	public TaskExecuter(Runnable task) {
		if(null == task) throw new IllegalArgumentException("task is null");

		this.executorService = Executors.newCachedThreadPool();
		this.taskSet = new HashSet<>();
		this.taskSet.add(task);
	}
	
	public TaskExecuter(Set<Runnable> taskSet) {
		
		if(null == taskSet) throw new IllegalArgumentException("taskSet is null");
			
		this.executorService = Executors.newFixedThreadPool(taskSet.size());
		this.taskSet = taskSet;
	}
	
	public void execute() {

		for(Runnable task : taskSet) {
			executorService.submit(task);
		}
		
		executorService.shutdown();
	}	
}
