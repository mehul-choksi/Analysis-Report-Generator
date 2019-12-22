import java.util.ArrayList;

public class Student {

    public String rollNumber;
    public ArrayList<Integer> sem1;
    public ArrayList<Integer> sem2;

    public ArrayList<Double> gradePoints;


    public Student(){
        sem1 = new ArrayList<Integer>();
        sem2 = new ArrayList<Integer>();
        gradePoints = new ArrayList<Double>();
    }

    public Student(String rollNumber, ArrayList<Integer> sem1, ArrayList<Integer> sem2, ArrayList<Double> gradePoints) {
        this.rollNumber = rollNumber;
        this.sem1 = sem1;
        this.sem2 = sem2;
        this.gradePoints = gradePoints;
    }

    public void display(){
        System.out.println("Roll no.: " + rollNumber);
        System.out.println("sem1: " + sem1);
        System.out.println("sem2: " + sem2);
        System.out.println("Grade points: " + gradePoints);
    }
}
