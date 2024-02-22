package application;

import java.text.DecimalFormat;
import java.util.Scanner;
import java.util.InputMismatchException;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

class Process {
    int pid;
    int arrivalTime;
    int burstTime;
    int completedTime;
    int turnaroundTime;
    int waitingTime;
    int priority;

    public Process(int pid, int arrivalTime, int burstTime, int priority) {
        this.pid = pid;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.priority = priority;
        this.completedTime = 0;
        this.turnaroundTime = 0;
        this.waitingTime = 0;
    }
}

public class NPPFX extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Scanner sc = new Scanner(System.in);
        boolean tryAgain = true;

        while (tryAgain) {
            System.out.println("--------------------------------------------------------");
            System.out.println("        Non-Preemptive Priority (NPP) Scheduling        ");
            System.out.println("--------------------------------------------------------");

            System.out.print("Enter the number of processes: ");
            int n = 0;
            boolean validInput = false;

            while (!validInput) {
                try {
                    n = sc.nextInt();
                    validInput = true;
                } catch (InputMismatchException e) {
                    System.out.println("Please input numerical numbers only.");
                    sc.nextLine();
                }
            }

            Process[] processes = new Process[n];

            for (int i = 0; i < n; i++) {
                validInput = false;
                int arrivalTime = 0;
                int burstTime = 0;
                int priority = 0;

                while (!validInput) {
                    try {
                        System.out.print("Enter Arrival Time for Process " + (i + 1) + ": ");
                        arrivalTime = sc.nextInt();
                        validInput = true;
                    } catch (InputMismatchException e) {
                        System.out.println("Please input numerical numbers only.");
                        sc.nextLine();
                    }
                }

                validInput = false;

                while (!validInput) {
                    try {
                        System.out.print("Enter Burst Time for Process " + (i + 1) + ": ");
                        burstTime = sc.nextInt();
                        validInput = true;
                    } catch (InputMismatchException e) {
                        System.out.println("Please input numerical numbers only.");
                        sc.nextLine();
                    }
                }

                validInput = false;

                while (!validInput) {
                    try {
                        System.out.print("Enter Priority for Process " + (i + 1) + ": ");
                        priority = sc.nextInt();
                        validInput = true;
                    } catch (InputMismatchException e) {
                        System.out.println("Please input numerical numbers only.");
                        sc.nextLine();
                    }
                }

                processes[i] = new Process(i + 1, arrivalTime, burstTime, priority);
            }

            for (int i = 0; i < n - 1; i++) {
                for (int j = 0; j < n - i - 1; j++) {
                    if (processes[j].priority < processes[j + 1].priority) {
                        Process temp = processes[j];
                        processes[j] = processes[j + 1];
                        processes[j + 1] = temp;
                    } else if (processes[j].priority == processes[j + 1].priority) {
                        if (processes[j].arrivalTime > processes[j + 1].arrivalTime) {
                            Process temp = processes[j];
                            processes[j] = processes[j + 1];
                            processes[j + 1] = temp;
                        }
                    }
                }
            }

            int currentTime = 0;
            double totalWaitingTime = 0;
            double totalTurnaroundTime = 0;

            System.out.println("\nProcess Table:");
            System.out.println("");
            System.out.println("-------------------------------------------------------------------------------------");
            System.out.println("| pid | Arrival Time | Burst Time | Completed Time | Turnaround Time | Waiting Time |");
            System.out.println("|-----------------------------------------------------------------------------------|");

            for (int i = 0; i < n; i++) {
                Process currentProcess = processes[i];
                currentTime = Math.max(currentTime, currentProcess.arrivalTime);
                currentProcess.completedTime = currentTime + currentProcess.burstTime;
                currentProcess.turnaroundTime = currentProcess.completedTime - currentProcess.arrivalTime;
                currentProcess.waitingTime = currentProcess.turnaroundTime - currentProcess.burstTime;

                totalWaitingTime += currentProcess.waitingTime;
                totalTurnaroundTime += currentProcess.turnaroundTime;

                System.out.printf("| %-3d | %-12d | %-10d | %-14d | %-15d | %-12d |\n",
                        currentProcess.pid, currentProcess.arrivalTime, currentProcess.burstTime,
                        currentProcess.completedTime, currentProcess.turnaroundTime, currentProcess.waitingTime);

                currentTime = currentProcess.completedTime;
            }

            System.out.println("-------------------------------------------------------------------------------------");

            DecimalFormat df = new DecimalFormat("0.00");

            System.out.println("\nTotal Time is: " + currentTime);
            System.out.println("\nAverage Waiting Time is: " + df.format(totalWaitingTime / n) + "ms");
            System.out.println("Average Turnaround Time is: " + df.format(totalTurnaroundTime / n) + "ms");
            System.out.println("");

            launchGanttChartWindow(primaryStage, completionTimes(processes), currentTime,
                    (float) (totalWaitingTime / n), (float) (totalTurnaroundTime / n));

            System.out.print("Process Completed. Do you want to Try Again? (Y/N): ");
            sc.nextLine();
            String tryAgainInput = sc.nextLine().trim().toUpperCase();

            if (!tryAgainInput.equals("Y")) {
                tryAgain = false;

                System.out.println("");
                System.out.println("This code was made by: Jon Anthony De Ocampo (using JavaFX)");
                System.out.println("BSCPE-2A | OS Finals");
            }
        }

        sc.close();
    }

    private int[] completionTimes(Process[] processes) {
        int n = processes.length;
        int[] completionTimes = new int[n];
        int currentTime = 0;

        for (int i = 0; i < n; i++) {
            currentTime += processes[i].burstTime;
            completionTimes[i] = currentTime;
        }

        return completionTimes;
    }

    private void launchGanttChartWindow(Stage primaryStage, int[] completionTimes, int totalTime,
            float averageWaitingTime, float averageTurnaroundTime) {
        Pane root = new Pane();
        double scale = 50.0;

        Text title = new Text("Gantt Chart Table");
        title.setFont(new Font("Times New Roman", 20));
        title.setX(30);
        title.setY(30);

        Text totalTimeText = new Text("Total Time: " + totalTime);
        totalTimeText.setFont(new Font("Times New Roman", 12));
        totalTimeText.setX(30);
        totalTimeText.setY(120);

        Text avgWaitingTimeText = new Text("Average Waiting Time: " + String.format("%.2fms", averageWaitingTime));
        avgWaitingTimeText.setFont(new Font("Times New Roman", 12));
        avgWaitingTimeText.setX(30);
        avgWaitingTimeText.setY(140);

        Text avgTurnaroundTimeText = new Text(
                "Average Turnaround Time: " + String.format("%.2fms", averageTurnaroundTime));
        avgTurnaroundTimeText.setFont(new Font("Times New Roman", 12));
        avgTurnaroundTimeText.setX(30);
        avgTurnaroundTimeText.setY(160);

        root.getChildren().addAll(title, totalTimeText, avgWaitingTimeText, avgTurnaroundTimeText);

        double xPos = 0;
        for (int i = 0; i < completionTimes.length; i++) {
            double width = (i == 0 ? completionTimes[i] : completionTimes[i] - completionTimes[i - 1]) * scale;

            Rectangle rect = new Rectangle(xPos, 50, width, 20);
            rect.setStroke(Color.BLACK);
            rect.setFill(Color.TRANSPARENT);

            Text text = new Text(xPos + 5, 65, "P" + (i + 1));

            root.getChildren().addAll(rect, text);
            xPos += width;
        }

        for (int i = 0; i <= totalTime; i++) {
            Text timeText = new Text(scale * i - (i < 10 ? 3 : 7), 85, String.valueOf(i));
            root.getChildren().add(timeText);
        }

        ScrollPane scrollPane = new ScrollPane(root);
        scrollPane.setPrefViewportWidth(600);
        scrollPane.setPrefViewportHeight(300);
        scrollPane.setPannable(true);

        primaryStage.setTitle("Print - Non-Preemptive Priority (NPP) Scheduling");
        primaryStage.setScene(new Scene(scrollPane));
        primaryStage.show();
    }
}
