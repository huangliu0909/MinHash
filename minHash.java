import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class minHash {

    public static int[][] result;
    public static Map<Integer, Integer> setMap;
    public static List<Integer> set_id_list;
    public static int set_id;
    public static Map<Integer, Integer> elementMap;
    public static List<Integer> element_id_list;
    public static int element_id;
    public static int setNum;
    public static int elementNum;
    public static Map<Integer, List<Integer>> terms;
    public static int row_f ;
    public static int hashNum ;

    public static void main(String[] args) throws FileNotFoundException {
        //row_f = 1000;
        //set_f = 1000;
        //hashNum = 1000;

        int[] hash_a = {100,1000};
        int[] row_a = {500,1000,2000,4000,8000, 10000};
        for(int i = 0 ; i < row_a.length; i ++){
            row_f = row_a[i];
            hashNum = hash_a[0];
            run();
        }


    }
    
    public static void run() throws FileNotFoundException{

        //每行形如“i,j”，表明第i个集合包含了元素j
        String filePath = "src\\linux_distinct.txt";
        File f=new File(filePath);
        result = new int[10000][10000];//最多有一百个元素
        for(int i = 0; i < 10000; i++)
            for(int j = 0; j < 10000; j++)
                result[i][j] = 0;
        Scanner input = new Scanner(f);

        setMap = new HashMap<>();elementMap = new HashMap<>();
        set_id_list = new ArrayList<>();element_id_list = new ArrayList<>();
        set_id = 0;element_id = 0;setNum = 0; elementNum = 0;
        terms = new HashMap<>();
        int row = 0;
        while(input.hasNext())
        {
            row ++;

            if(row>row_f)
                break;


            String in=input.nextLine();
            if(setNum >= 10000 || elementNum >= 10000) break;
            addToSet(in);
            //System.out.print(setNum + " " + elementNum + " \n");

        }
        input.close();

        long start = new Date().getTime();
        List<int[]> hashresult = hashProducer(elementNum, hashNum);
        double[][] equal = similarityCalculate(hashresult);
        long end = new Date().getTime();
        double ff = 0;
        double fff = 0;


        double time = (double)(end - start)/1000;

        long start_naive = new Date().getTime();
        for(int i = 0; i < equal.length; i++) {
            for(int j = i + 1; j < equal.length; j++) {
                if(equal[i][j] > 0){
                    compare(terms.get(i),terms.get(j));
                    ff +=1;
                    if(compare(terms.get(i),terms.get(j)) == 0 )    fff += 1;

                }
            }
        }
        long end_naive = new Date().getTime();
        double time_naive = (double)(end_naive - start_naive)/1000;
        System.out.print("row number: " + (row - 1)+ " hash number: " + hashNum  + "  set number: " + (setNum ) + " takes time: " );
        System.out.print(time + "s   naive takes time: " + time_naive + "s\n" );
    }

    public static double compare(List<Integer> a, List<Integer> b){

        double f = 0;
        for(int i = 0 ; i < a.size(); i++){
            if(b.contains(a.get(i))){
                f += 1;

            }
            //System.out.print(a.get(i) + " " + b.get(i) + "aaa ");
        }

        //System.out.print(f + (a.size() +"  " +  (b.size()) + "\n"));
        return f/(a.size() + b.size() - f);
    }
    
    public static void addToSet(String s) {
        
        String[] contendString = s.split(" ");
        int set = Integer.parseInt(contendString[0]) - 1;
        int element = Integer.parseInt(contendString[1]) - 1;
        int sM = 0;
        int eM = 0;
        if(!setMap.containsKey(set)){
            sM = set_id;
            setMap.put(set, set_id);
            set_id += 1;  set_id_list.add(set);
            setNum += 1;

        }
        else sM = setMap.get(set);

        if(!elementMap.containsKey(element)) {
            elementMap.put(element, element_id);
            eM = element_id;
            element_id += 1;element_id_list.add(element);
            elementNum += 1;

        }
        else eM = elementMap.get(element);

        //result横为元素，纵为集合
        //System.out.print(" aaa" +eM+"bbb" + sM + "\n");
        result[eM][sM] = 1;

        if(!terms.containsKey(sM)){
            List<Integer> l = new ArrayList<>();
            l.add(sM);
            terms.put(sM,l);
        }
        else{
            terms.get(sM).add(eM);
        }
    }
    
    
    public static List<int[]> hashProducer(int elementNum, int hashNum) {
        //确定不同的hash序列,即不同的元素排列方式
        List<int[]> hashresult = new ArrayList<>();
        for(int i = 0; i < hashNum; i ++) {
            int[] hash = new int[elementNum];
            for(int j = 0; j < elementNum; j++) {
                hash[j] = j;
            }
            
            for(int j = 0; j < elementNum; j++) {
                Random r = new Random();
                int p = r.nextInt(j + 1);
                int tmp = hash[j];
                hash[j] = hash[p];
                hash[p] = tmp;
            }
            //printArr(hash);
            //System.out.println();
            hashresult.add(hash);
        }
        
        return hashresult;
    }
    
    public static void printArr(int[] hash){

        for(int i=0;i<hash.length;i++){

            System.out.print(hash[i]+" ");

        }

    }
    
    public static int[] minHashCalculate(int[] hash) {
        //根据一个确定的哈希序列生成minHash
        int[] minhash = new int[setNum];
        for(int i = 0; i < setNum; i++)
            for(int j = 0; j < elementNum; j++)
                if(result[hash[j]][i] == 1) {
                    minhash[i] = j;
                    break;
                }
        //printArr(minhash);
        //System.out.println();
        return minhash;
        
    }
    
    public static double[][] similarityCalculate(List<int[]> hashresult) {
        double[][] equal = new double[setNum][setNum];
        for(int i = 0; i < setNum; i++)
            for(int j = 0; j < setNum; j++)
                equal[i][j] = 0;
        for(int i = 0; i< hashresult.size();i++)
        {
           // System.out.print(111);
           int[] hash = hashresult.get(i);
           int[] minhash = minHashCalculate(hash);
           for(int k = 0; k < setNum; k++)
               for(int l = k; l <setNum; l++) {
                   if(minhash[k] == minhash[l])
                   {
                       //System.out.print(000);
                       equal[k][l] += 1;
                       if(k != l) equal[l][k] ++;
                   }
               }
        }
        
        for(int i = 0; i < setNum; i++)
            for(int j = 0; j < setNum; j++)
                equal[i][j] /= hashresult.size();//minhash相等的概率
        
        return equal;
    }
}
