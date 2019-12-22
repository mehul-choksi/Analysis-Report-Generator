import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;

public class SchemaGenerator {

    public static Connection connection;
    public ArrayList<String> sem1SubjectList;
    public ArrayList<String> sem2SubjectList;

    public String path;
    public String fileName;


    public SchemaGenerator(String path, String fileName){
        this.path = path;
        this.fileName = fileName;
        sem1SubjectList = new ArrayList<String>();
        sem2SubjectList = new ArrayList<String>();
        initializeSubjects(1);
        initializeSubjects(2);
    }

    public void initializeSubjects(int sem){
        ArrayList<String> subjects;
        if(sem == 1){
            subjects = sem1SubjectList;
        }
        else{
            subjects = sem2SubjectList;
        }
        try{
            BufferedReader br = new BufferedReader(new FileReader(new File(path + fileName + "_schema")));

            String line;
            while((line = br.readLine()) != null){
                String tokens[] = line.split("\\s");
                subjects.add(tokens[1]);
            }

        }
        catch (Exception e){
            e.printStackTrace();
        }
    }




}
