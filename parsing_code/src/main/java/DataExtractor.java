import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class DataExtractor {

    public String[] data;

    public HashMap<Integer, ArrayList<Integer>> sem1;
    public HashMap<Integer, ArrayList<Integer>> sem2;
    public int semCount;

    public int year;
    public Rules rules;

    public String sem2Delim;
    public String yearString;

    public DataExtractor(String data[], Rules rules, int semCount, int year){
        this.data = data;
        this.rules = rules;
        this.semCount = semCount;
        this.year = year;
        sem1 = rules.getSem1();

        if(year == 4){
            yearString = "FOURTH";
        }
        else if(year == 3){
            yearString = "THIRD";
        }
        else if(year == 2){
            yearString = "SECOND";
        }
        if(semCount == 2){
            sem2 = rules.getSem2();
            if(year == 4){
                sem2Delim = "410250";
                yearString = "FOURTH";
            }
            else if(year == 3){
                sem2Delim = "310250";
                yearString = "THIRD";
            }
            else if(year == 2){
                sem2Delim = "207003";
                yearString = "SECOND";
            }
        }

    }

    public int getIntScore(String rawScore){
        String score = rawScore.substring(0,3);
        try{
            int val = Integer.parseInt(score);
            return val;
        }
        catch (Exception e){
            return 0;
        }

    }



    public ArrayList<Student> extract(){
        System.out.println("Extracting...");

        ArrayList<Student> studentList = new ArrayList<Student>();
        //state 0: Garbage strings
        //state 1: first sem
        //state 2: second sem
        //state 3: gpa throughout years
        int state = 0;

        int dataLen = data.length;
        String rollNumber = "";
        ArrayList<Integer> sem1 = new ArrayList<Integer>();
        ArrayList<Integer> sem2 = new ArrayList<Integer>();
        ArrayList<Double> gradePoints = new ArrayList<Double>();


        for(int i = 0; i < dataLen; i++) {
            String line = data[i];

            //System.out.println(line);
            if (state == 0) {
//                System.out.println("state = 0");
                try{
                    if (year == 4 && line.startsWith("B150")) {
                        rollNumber = line.split("\\s+")[0];
                        System.out.println("Roll Number: " + rollNumber);
                    } else if (year == 3 && line.startsWith("T150")) {
                        rollNumber = line.split("\\s")[0];
                        System.out.println(rollNumber);
                    }
                    else if (year == 2 && line.startsWith("S150")) {
                        rollNumber = line.split("\\s")[0];
                        System.out.println(rollNumber);
                    }
                    if (!line.contains("SEM.:1")) {
                        continue;
                    }
                    state = 1;
                }
                catch (Exception e){
                    e.printStackTrace();
                }

            } else if (state == 1){
//                System.out.println("state = 1");
                //parse all entries
                String tokens[] = line.split("\\s+");
                if (tokens.length < 5) {
                    continue;
                }
                sem1 = readScores(1, i);

                if (semCount == 2) {
                    state = 2;

                } else {
                    state = 3;
                }

                continue;

            } else if (state == 2) {
//                System.out.println("state = 2");
                //parse all entries
                if (!line.contains(sem2Delim)) {
                    continue;
                }
                sem2 = readScores(2, i);
                System.out.println(sem2);
                state = 3;


            } else if (state == 3) {
                state = 3;
                if (year == 4) {

                    if (!line.contains(yearString)) {
                        continue;
                    }

                    gradePoints = new ArrayList<Double>();
                    String tokens[] = line.split("\\s+");
                    try {
                        String finalYear = tokens[4].substring(0, 4);


                        line = data[i + 1];
                        tokens = line.split("\\s+");
                        boolean diplomaStudent = false;
                        gradePoints.add(Double.parseDouble(tokens[3].substring(0, 4)));
                        gradePoints.add(Double.parseDouble(tokens[7].substring(0, 4)));
                        try {
                            gradePoints.add(Double.parseDouble(tokens[11].substring(0, 4)));
                        } catch (Exception e) {
                            diplomaStudent = true;
                        }

                        if (diplomaStudent) {
                            gradePoints.add(Double.parseDouble(finalYear));

                            double avg = 0;
                            for (Double val : gradePoints) {
                                avg += val;
                            }
                            avg /= 3;
                            gradePoints.add(0, avg);
                        } else {
                            gradePoints.add(Double.parseDouble(finalYear));
                        }
                    } catch (Exception e) {
                        gradePoints.clear();
                        gradePoints.addAll(Arrays.asList(0d, 0d, 0d, 0d));
                    }


                    Student student = new Student(rollNumber, sem1, sem2, gradePoints);
                    student.display();
                    studentList.add(student);
                    state = 0;

                }
                else {
                    System.out.println("Parsing");
                    //System.out.println("else");
                    if (line.contains(yearString)) {
                        System.out.println(line);
                        ArrayList<Double> gradePoint = new ArrayList<Double>();

                        try {
                            String tokens[] = line.split("\\s");
                            double value = Double.parseDouble(tokens[4].substring(0,tokens[4].length() - 1));
                            gradePoint.add(value);

                        } catch (Exception e) {
                            System.out.println("Pdf line: " + line);
                            e.printStackTrace();
                            gradePoint.add(0.0);
                        }
                        finally {
                            state = 0;
                            System.out.println("Finally");
                            Student student = new Student(rollNumber, sem1, sem2, gradePoint);

                            student.display();
                            studentList.add(student);
                        }

                    } else {
                        continue;

                    }
                }

            }
        }

        return studentList;

    }

    public ArrayList<Integer> readScores(int sem, int lineIndex){
        HashMap<Integer, ArrayList<Integer>> curr;
        if(sem == 1){
            curr = sem1;
        }
        else{
            curr = sem2;
        }

        ArrayList<Integer> scoreList = new ArrayList<Integer>();
        int bound;
        if(year == 4){
            bound = 10;
        }
        else{
            bound = 9;
        }

        int i = 0;
        try{
            int pos = 0, score = 0;
            i = lineIndex;
            String line = data[lineIndex];
            String tokens[] = line.split("\\s+");

            for(int lineNum = 1; lineNum <= bound; lineNum++){

                ArrayList<Integer> indices = curr.get(lineNum);
                System.out.println("Indices for : " + lineNum + " " + indices);

                for(int val : indices){
                    score = getIntScore(tokens[val]);
                    if(indices.size() > 1){
                        System.out.println(score);
                    }
                    scoreList.add(score);

                }
                i++;
                line = data[i];
                tokens = line.split("\\s+");

            }
        }
        catch (Exception e){
            System.out.println("Parse error");
            e.printStackTrace();
            System.out.println("Occurred at line number: " + i);
        }

        return scoreList;

    }

}
