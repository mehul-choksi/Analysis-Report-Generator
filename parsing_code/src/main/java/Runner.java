import java.util.ArrayList;

public class Runner {

    public static void main(String args[]){
        String path = "/home/ash/workspace/reparse/";
        String fileName = "se_comp_may";
        Reader reader = new Reader(path,fileName);
        reader.read();

        int semCount = 2, year = 2;
        Rules rules = new Rules(path,fileName,semCount);


        DataExtractor dataExtractor = new DataExtractor(reader.getData(), rules,semCount,year);
        ArrayList<Student> studentList = dataExtractor.extract();

        studentList.get(0).display();
        //Reducer reducer = new Reducer(studentList, path, fileName, semCount);
        //reducer.reduce();
        //studentList = reducer.studentList;

        CSVDump csvDump = new CSVDump(path, fileName, studentList);
        csvDump.toCsv();



        //ElectiveAnalysis electiveAnalysis = new ElectiveAnalysis("410252", reader.getData());
        //electiveAnalysis.analyse();
    }
}
