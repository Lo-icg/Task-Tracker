package taskBuilder;

import java.util.Scanner;
import taskCommand.Command;
import taskObject.Task;
import taskObject.Task.Status;

public class TaskTracker extends TaskTrackerBuilder {

	private TaskTracker() {

		var running = true;

		try (Scanner read = new Scanner(System.in)) {

			while (running) {

				System.out.print("task-cli: ");
				inputData = read.nextLine();

				if (isValidInput()) {

					String rawData[] = splitInputData();
					
					command = rawData[0];

					if (command.equals(Command.ADD) || command.equals(Command.UPDATE)) {
						taskDescription = command.equals(Command.ADD) ? unwrap(rawData[1]) : unwrap(rawData[2]);
					}
					
					int taskId = !command.equals(Command.ADD) ? Integer.parseInt(rawData[1]) : 0;
					id = command.equals(Command.ADD) ? (id = id + 1) : id;

					switch (command) {

					case Command.ADD -> taskList.add(new Task(id, taskDescription));

					case Command.UPDATE -> updateIfExist(taskId);

					case Command.DELETE -> deleteIfExist(taskId);

					case Command.MARK_IN_PROGRESS -> mark(taskId, Status.IN_PROGRESS);

					case Command.MARK_DONE -> mark(taskId, Status.DONE);
					}

					clean();
					rawData = null;
				} 

				else if (inputData.equals(Command.LIST)) listAllTask();
				else if (inputData.equals(Command.LIST_TODO)) listStatus(Status.TODO);
				else if (inputData.equals(Command.LIST_DONE)) listStatus(Status.DONE);
				else if (inputData.equals(Command.LIST_IN_PROGRESS)) listStatus(Status.IN_PROGRESS);
				else System.out.println("Invalid Command");
			}
			
		} catch (Exception e) {
			System.out.println("An error occurred: " + e.getMessage());
		}
	}

	// launch TaskTracker
	public static void launch() {
		new TaskTracker();
	}

}
