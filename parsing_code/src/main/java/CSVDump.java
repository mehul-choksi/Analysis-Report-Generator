import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

public class CSVDump {

    public ArrayList<Student> students;
    public String path;
    public String fileName;

    public CSVDump(String path, String fileName, ArrayList<Student> students){
        this.path = path;
        this.fileName = fileName;
        this.students = students;
    }

    public void toCsv(){

        ArrayList<Integer> scores;

        ArrayList<Double> gpa;
        try{
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(path+fileName+".csv")));

            for(Student s : students){
                StringBuilder sb = new StringBuilder(s.rollNumber);

                scores = s.sem1;
                //System.out.println("sem1 = " + scores);
                for(Integer marks : scores){
                    sb.append("," + marks);
                }

                scores = s.sem2;

                for(Integer marks : scores){
                    sb.append("," + marks);
                }


                gpa = s.gradePoints;

                for(Double yearPointer : gpa){
                    sb.append("," + yearPointer);
                }

                bw.write(sb.toString() + "\n");
            }

            bw.close();

        }
        catch (Exception e){

        }




    }

}
