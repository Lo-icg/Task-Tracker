package taskObject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Task {

	public Task(int id, String task) {
		this.description = task;
		this.id = id;
		status = Status.TODO; // default status
		timeCreated = LocalDateTime.now();
		System.out.println("Task added successfully (ID: " + id + ")\n");

		toJSONFile(); // save to JSON File
	}

	// Task Properties
	private int id;
	private String description;
	private Status status;
	private final LocalDateTime timeCreated;
	private LocalDateTime timeUpdated;
	
	static private DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("MMM-dd-yyyy HH:mm a");

	public int getId() {
		return id;
	}
	public String getDescription() {
		return description;
	}
	public String getTimeCreated() {
		return timeCreated.format(dateTimeFormat);
	}	
	public String getTimeUpdated() {
		return timeUpdated != null ? timeUpdated.format(dateTimeFormat) : "";
	}
	public void setStatus(Status status) {
		this.status = status;
		toJSONFile(); // update JSON FIle
	}
	public Status getStatus() {
		return status;
	}	

	public void update(String description) {
		this.description = description;
		timeUpdated = LocalDateTime.now();
		System.out.println("Task has been modified (ID: " + getId() + ")\n");

		toJSONFile(); // update JSON File
	}

	public enum Status {
		TODO, DONE, IN_PROGRESS;
	}

	private void toJSONFile() {

		var objData = String.format("""
				{
				"id": %s,                      
				"description": "%s",   
				"status": "%s",              
				"time_created_at": "%s",
				"time_updated_at": "%s"
				}
				""", getId(), getDescription(), getStatus(), getTimeCreated(), timeUpdated != null ? getTimeUpdated() : null);

		try (FileOutputStream out = new FileOutputStream("taskId" + getId() + ".json")){
			out.write(objData.getBytes());
		} catch (IOException e) {
			System.out.println("An error occurred: " + e.getMessage());
		}
	}
}