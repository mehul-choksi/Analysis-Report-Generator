import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class Reducer {

    public ArrayList<Student> studentList;

    public String path;
    public String fileName;

    public TreeMap<Integer, ArrayList<Integer>> reductionSem1;
    public TreeMap<Integer, ArrayList<Integer>> reductionSem2;

    public int semCount;

    public Reducer(ArrayList<Student> studentList, String path, String fileName, int semCount){
        this.studentList = studentList;
        this.path = path;
        this.fileName = fileName;
        reductionSem1 = new TreeMap<Integer, ArrayList<Integer>>();
        reductionSem2 = new TreeMap<Integer, ArrayList<Integer>>();
        this.semCount = 2;
        setReduction(1);
        if(semCount == 2){
            setReduction(2);
        }


    }

    public void setReduction(int sem){
        TreeMap<Integer, ArrayList<Integer>> map;
        if(sem == 1){
            map = reductionSem1;
        }
        else{
            map = reductionSem2;
        }

        try{
            BufferedReader br = new BufferedReader(new FileReader(new File(path+fileName+"_reduction_"+sem)));

            String line;

            int curr = 0;
            while((line = br.readLine()) != null){
                String tokens[] = line.split("\\s");
                ArrayList<Integer> list = new ArrayList<Integer>();
                for(String token :tokens){
                    list.add(Integer.parseInt(token));
                }
                map.put(curr, list);
                curr++;
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void reduce(){
        System.out.println("Reducing...");
        for(Student student : studentList){

            student.sem1 = reduceSem(student.sem1, 1);
            student.sem2 = reduceSem(student.sem2,2);

        }
    }

    public ArrayList<Integer> reduceSem(ArrayList<Integer> scoreList, int sem){
        TreeMap<Integer, ArrayList<Integer>> map;

        if(sem == 1){
            map = reductionSem1;
        }
        else{
            map = reductionSem2;
        }

        ArrayList<Integer> transformed = new ArrayList<Integer>();

        for(Map.Entry<Integer, ArrayList<Integer>> entry : map.entrySet()){
            ArrayList<Integer> curr = entry.getValue();

            int sum = 0;
            for(int index : curr){
                sum += scoreList.get(index);
            }

            transformed.add(sum);
        }

        return transformed;
    }




}
