import java.io.*;
import java.util.*;

public class Rules {

    String path;
    String fileName;


    HashMap<Integer, ArrayList<Integer>> sem1;
    HashMap<Integer, ArrayList<Integer>> sem2;
    public int semCount;


    public Rules(String path, String fileName, int semCount){
        this.path = path;
        this.fileName = fileName;
        this.semCount = semCount;
        sem1 = new HashMap<Integer, ArrayList<Integer>>();
        sem2 = new HashMap<Integer, ArrayList<Integer>>();
        setRules();
    }

    public void ruleGenerator(int sem){

        try{
            BufferedReader br = new BufferedReader(new FileReader(new File(path + fileName + "_" + sem)));

            String line;
            HashMap<Integer, ArrayList<Integer>> map = null;
            while((line = br.readLine()) != null){
                System.out.println("Line: " + line);
                String tokens[] = line.split("\\s+");

                int key = Integer.parseInt(tokens[0]);

                int n = tokens.length;

                for(int i = 1; i < n; i++){
                    int dataIndex = Integer.parseInt(tokens[i]);

                    if(sem == 1){
                        map = sem1;
                    }
                    else if(sem == 2){
                        map = sem2;
                    }

                    int mapIndex = Integer.parseInt(tokens[0]);
                    if(map.containsKey(mapIndex)){
                        map.get(mapIndex).add(dataIndex);
                    }
                    else{
                        ArrayList<Integer> list = new ArrayList<Integer>();
                        list.add(dataIndex);
                        map.put(mapIndex, list);
                    }
                }




            }
            System.out.println("Sem1 rules: " + sem1);
            System.out.println("Sem2 rules: " + sem2);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void setRules(){
        ruleGenerator(1);
        if(semCount == 2){
            ruleGenerator(2);
        }

    }

    public HashMap<Integer, ArrayList<Integer>> getSem1() {
        return sem1;
    }

    public HashMap<Integer, ArrayList<Integer>> getSem2() {
        return sem2;
    }
}
