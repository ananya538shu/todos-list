import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

class Task {
    private String title;
    private boolean completed;

    public Task(String title) {
        this.title = title;
        this.completed = false;
    }

    public String getTitle() {
        return title;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void markCompleted() {
        completed = true;
    }

    @Override
    public String toString() {
        return (completed ? "[Completed] " : "[Pending] ")
                + title;
    }
}

public class task3 {

    private static final String FILE_NAME = "tasks.txt";

    private static ArrayList<Task> tasks = new ArrayList<>();
    private static Scanner sc = new Scanner(System.in);

    // File Handling: Load tasks from file
    public static void loadTasks() {
        File file = new File(FILE_NAME);

        if (!file.exists()) {
            return;
        }

        try (BufferedReader br =
                     new BufferedReader(new FileReader(file))) {

            String line;

            while ((line = br.readLine()) != null) {
                String[] data = line.split("\\|", 2);

                if (data.length == 2) {
                    Task task = new Task(data[1]);

                    if (data[0].equals("true")) {
                        task.markCompleted();
                    }

                    tasks.add(task);
                }
            }

        } catch (IOException e) {
            System.out.println("Error loading tasks: "
                    + e.getMessage());
        }
    }

    // File Handling: Save tasks to file
    public static void saveTasks() {
        try (BufferedWriter bw =
                     new BufferedWriter(new FileWriter(FILE_NAME))) {

            for (Task task : tasks) {
                bw.write(task.isCompleted() + "|"
                        + task.getTitle());
                bw.newLine();
            }

        } catch (IOException e) {
            System.out.println("Error saving tasks: "
                    + e.getMessage());
        }
    }

    // Add a new task
    public static void addTask() {
        System.out.print("Enter task name: ");
        String title = sc.nextLine().trim();

        if (title.isEmpty() || title.contains("|")) {
            System.out.println("Invalid task name!");
            return;
        }

        tasks.add(new Task(title));
        saveTasks();

        System.out.println("Task added successfully!");
    }

    // Display all tasks
    public static void viewTasks() {
        if (tasks.isEmpty()) {
            System.out.println("No tasks available.");
            return;
        }

        System.out.println("\n------ YOUR TASKS ------");

        for (int i = 0; i < tasks.size(); i++) {
            System.out.println((i + 1) + ". "
                    + tasks.get(i));
        }
    }

    // Mark a task as completed
    public static void completeTask() {
        viewTasks();

        if (tasks.isEmpty()) {
            return;
        }

        try {
            System.out.print("Enter task number: ");
            int number = Integer.parseInt(sc.nextLine());

            if (number < 1 || number > tasks.size()) {
                System.out.println("Invalid task number!");
                return;
            }

            tasks.get(number - 1).markCompleted();
            saveTasks();

            System.out.println("Task completed successfully!");

        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
        }
    }

    // Delete a task
    public static void deleteTask() {
        viewTasks();

        if (tasks.isEmpty()) {
            return;
        }

        try {
            System.out.print("Enter task number to delete: ");
            int number = Integer.parseInt(sc.nextLine());

            if (number < 1 || number > tasks.size()) {
                System.out.println("Invalid task number!");
                return;
            }

            tasks.remove(number - 1);
            saveTasks();

            System.out.println("Task deleted successfully!");

        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
        }
    }

    // Multithreading: Background reminder
    static class ReminderThread extends Thread {
        @Override
        public void run() {
            try {
                while (!isInterrupted()) {
                    Thread.sleep(60000);

                    long pending = tasks.stream()
                            .filter(task -> !task.isCompleted())
                            .count();

                    if (pending > 0) {
                        System.out.println(
                                "\n[Reminder] You have "
                                + pending
                                + " pending task(s)!");
                        System.out.print("Choose an option: ");
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public static void main(String[] args) {
        loadTasks();

        ReminderThread reminder = new ReminderThread();
        reminder.setDaemon(true);
        reminder.start();

        while (true) {
            System.out.println("\n===== TO-DO LIST MANAGER =====");
            System.out.println("1. Add Task");
            System.out.println("2. View Tasks");
            System.out.println("3. Complete Task");
            System.out.println("4. Delete Task");
            System.out.println("5. Exit");
            System.out.print("Choose an option: ");

            try {
                int choice = Integer.parseInt(sc.nextLine());

                switch (choice) {
                    case 1:
                        addTask();
                        break;

                    case 2:
                        viewTasks();
                        break;

                    case 3:
                        completeTask();
                        break;

                    case 4:
                        deleteTask();
                        break;

                    case 5:
                        saveTasks();
                        System.out.println("Thank you for using To-Do List!");
                        sc.close();
                        return;

                    default:
                        System.out.println(
                                "Invalid choice! Try again.");
                }

            } catch (NumberFormatException e) {
                System.out.println(
                        "Please enter a number from 1 to 5.");
            }
        }
    }
}