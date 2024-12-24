package taskBuilder;

import java.util.LinkedList;
import java.util.List;

import taskObject.Task;
import taskObject.Task.Status;

public class TaskTrackerBuilder {

	static int id;

	protected List<Task> taskList = new LinkedList<>();
	protected String inputData;
	protected String command;
	protected String taskDescription;

	protected boolean isValidInput() {

		// Pattern
		String add = "add \\\"\\S[\\S\\s]*\\S\\\"";
		String update = "update \\d+ (?:\\\"\\S[\\S\\s]*\\S\\\")";
		String delete = "delete \\d+";
		String markInProgress = "mark-in-progress \\d+";
		String markDone = "mark-done \\d+";

		String pattern = String.format("%s|%s|%s|%s|%s", add, update, delete, markInProgress, markDone);
		return inputData.matches(pattern);
	}

	protected String[] splitInputData() {

		String c = inputData.split(" ")[0];

		if (c.equals("add")) return inputData.split(" ", 2);
		if (c.equals("update")) return inputData.split(" ", 3);

		return inputData.split(" ");
	}	

	protected String unwrap(String taskDescription) {
		if (taskDescription != null) return taskDescription.substring(1, taskDescription.length() - 1);
		return null;
	}

	protected void clean() {
		inputData = null;
		command = null;
		taskDescription = null;
	}

	protected void updateIfExist(int id) {

		var found = false;
		Task task = null;

		for (Task t : taskList) {
			if (id == t.getId()) {
				task = t;
				found = true;
				break;
			}
		}

		if (found && task != null) {

			if (task.getStatus() == Status.DONE) {
				System.out.println("Task id: " + task.getId() + " already done\n");
			} else task.update(taskDescription);
		}
		else System.out.println("TASK NOT FOUND. ID: " + id + "\n");
	}

	protected void deleteIfExist(int id) {

		var found = false;
		Task task = null;

		for (Task t : taskList) {
			if (id == t.getId()) {

				task = t;
				found = true;
				break;
			}
		}

		if (found && task != null) {
			taskList.remove(task);
			System.out.println("Task deleted successfully (ID: " + task.getId() + ")");
		} else System.out.println("TASK NOT FOUND. ID: " + id + "\n");

		System.out.println();
	}

	protected void listAllTask() {
		display(null);
	}

	protected void listStatus(Task.Status stats) {

		switch (stats) {
		case Status.TODO -> display(stats);
		case Status.DONE -> display(stats);
		case Status.IN_PROGRESS -> display(stats);
		}

		//System.out.println();
	}

	@SuppressWarnings("incomplete-switch")
	protected void mark(int id, Task.Status stats) {

		var notFound = true;
		for (Task t : taskList) {

			if (t.getId() == id) {

				switch (stats) {
				case Status.IN_PROGRESS -> {
					if (t.getStatus() == Status.IN_PROGRESS) System.out.println("Task id: " + t.getId() + " already in-progress\n");
					else if (t.getStatus() == Status.DONE) System.out.println("Task id: " + t.getId() + " already done\n");
					else {
						System.out.println("mark success\n");
						t.setStatus(Status.IN_PROGRESS);
					}
				}
				case Status.DONE -> {
					if (t.getStatus() == Status.TODO) System.out.println("Cannot mark as done, Task id: " + t.getId() + " is not in-progress\n");
					else if (t.getStatus() == Status.DONE) System.out.println("Task id: " + t.getId() + " already done\n");
					else {
						System.out.println("mark success\n");
						t.setStatus(Status.DONE);
					}
				}
				}
				notFound = false;  
				break;
			}
		}
		if (notFound) System.out.println("TASK NOT FOUND. ID: " + id + "\n");
	}

	private void display(Task.Status stats) {

		if (taskList.isEmpty()) {
			System.out.println("EMPTY TASK LIST");
			return;
		}

		var border = generate("borderFormat", stats);
		var fields = generate("columnFormat", stats);
		var records = generate("recordFormat", stats);

		StringBuilder data = new StringBuilder(border + fields + border + records);

		var ts = new LinkedList<Task>();
		for (Task t : taskList) {
			if (t.getStatus() == stats || stats == null) ts.add(t);
		}

		if (ts.isEmpty()) System.out.println(noTaskSaved());
		else System.out.print(data.toString());

		System.out.println();
	}

	private String generate(String data, Task.Status stats) {

		/*
		 * Table column fields -> 3
		 * +----+------+--------+
		 * | id | task | status |
		 * +----+------+--------+
		 * overall width length = 22
		 *   
		 * default width length of each field includes +2 additional for both leading and trailing white spaces:
		 *    
		 *   " id "     -> 2 + 2 = 4
		 *   " task "   -> 4 + 2 = 6
		 *   " status " -> 6 + 2 = 8 
		 *                       = 18 + 4(the four boundary represent as "|")
		 * overall width length  = 22
		 *  
		 *   
		 */

		// default width length of each field 
		int idLength = 2; // column " id "
		int tsLength = 4; // column " task "
		int stLength = 6; // column " status "
		int tcLength = 15; // column " Task created at "
		int tnLength = 15; // column " Task updated at "

		//find the max length of each column fields
		for (Task t : taskList) {
			if (String.valueOf(t.getId()).length() > idLength) idLength = String.valueOf(t.getId()).length();
			if (t.getDescription().length() > tsLength) tsLength = t.getDescription().length();
			if (t.getStatus().toString().length() > stLength) stLength = t.getStatus().toString().length();
			if (t.getTimeCreated().length() > tcLength) tcLength = t.getTimeCreated().length();
			if (t.getTimeUpdated().length() > tnLength) tnLength = t.getTimeUpdated().length();
		}

		// generate a border line for each column with specific length
		String idBorder = "-".repeat(idLength + 2);
		String tdBorder = "-".repeat(tsLength + 2);
		String stBorder = "-".repeat(stLength + 2);
		String tcBorder = "-".repeat(tcLength + 2);
		String tnBorder = "-".repeat(tnLength + 2);

		String borderFormat = "+" + idBorder + "+" + tdBorder + "+" + stBorder + "+" + tcBorder + "+" + tnBorder + "+\n";
		String columnFormat = String.format("| %-" + idLength + "s | %-" + tsLength + "s | %-" + stLength + "s | %-" + tcLength + "s | %-" + tnLength + "s |%n",
				"id", "Task", "Status", "Task created at", "Task updated at");

		if (data.equals("borderFormat")) return borderFormat;
		if (data.equals("columnFormat")) return columnFormat;
		if (data.equals("recordFormat")) {

			var ts = new LinkedList<Task>();
			StringBuilder recordFormat = new StringBuilder();

			for (Task t : taskList) {
				if (t.getStatus() == stats || stats == null) ts.add(t);
			}

			for (Task t : ts) {
				var record = String.format("| %-" + idLength + "s | %-" + tsLength + "s | %-" + stLength + "s | %-" + tcLength + "s | %-" + tnLength + "s |\n", 
						t.getId(), t.getDescription(), t.getStatus(), t.getTimeCreated(), t.getTimeUpdated());
				recordFormat.append(record);
				recordFormat.append(borderFormat);
			}
			return recordFormat.toString();
		}

		return null;
	}

	private String noTaskSaved() {
		return 
				"+----+------+--------+-----------------+------------------\n" +
				"| id | task | status | Task created at | Task updated at |\n" +
				"|--------------------+-----------------+-----------------|\n" +
				"|                   NO TASK SAVED                        |\n" +
				"+--------------------------------------------------------+";
	}
}
