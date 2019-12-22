import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
/*
public class ElectiveAnalysis {

    String prefix;
    HashMap<String, String> codeToSubject;
    String data[];
    HashMap<String, ArrayList<Integer>> scoreMap;
    public ElectiveAnalysis(String prefix, String data[]){
        this.prefix = prefix;
        codeToSubject =new HashMap<String, String>();
        codeToSubject.put("410252A", "DSP");
        codeToSubject.put("410252B", "Compilers");
        codeToSubject.put("410252C", "ERTOS");
        codeToSubject.put("410252D", "SC");

        scoreMap = new HashMap<String, ArrayList<Integer>>();

        this.data = data;
    }

    public void analyse(){
        for(String line : data){
            if(line.contains(prefix)){
                String tokens[] = line.split("\\s+");
                int val = getValue(tokens[5]);
                String subject = tokens[1];

                if(scoreMap.containsKey(subject)){
                    scoreMap.get(subject).add(val);
                }
                else{
                    ArrayList<Integer> list = new ArrayList<Integer>();
                    list.add(val);
                    scoreMap.put(subject, list);
                }
            }

        }

        for(Map.Entry<String, ArrayList<Integer>> entry : scoreMap.entrySet()){
            String key = entry.getKey();
            ArrayList<Integer> list = entry.getValue();
            int sum = 0;
            int min = 100;
            int max = 0;

            int studentCount = 0;
            for(int val : list){
                sum += val;
                min = Math.min(min, val);
                max = Math.max(max,val);
                studentCount++;
            }

            int avg = sum/studentCount;

            System.out.println("Subject: " + codeToSubject.get(key));
            System.out.println("Student count for the subject: " + studentCount);
            System.out.println("Average score: " + avg );
            System.out.println("Max score: " + max );
            System.out.println("Min score: " + min );
        }
    }

    public int getValue(String score){
        try{
            int val = Integer.parseInt(score.split("\\/")[0]);
            return val;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return 0;
    }

}
*/