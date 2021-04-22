public class CheckSum {
    static final int maskOfAllOnes = (1<<16) - 1;

    public static int byteToInt(byte b){
        return ((int)b + 256) & 255;
    }

    public static int sumByTwoBytes (byte [] data){
        int sum = 0;
        for (int i = 0; i < data.length; i += 2){
            int nextSymbol = 0;
            if (i + 1 < data.length) nextSymbol = byteToInt(data[i+1]);
            sum += (byteToInt(data[i]) << 8) + nextSymbol;
            sum &= maskOfAllOnes;
        }
        return sum;
    }

    public static int getCheckSum (byte [] data){
        return sumByTwoBytes(data) ^ maskOfAllOnes;
    }

    public static boolean verifyCheckSum (byte [] data, int checkSum){
        if (checkSum > maskOfAllOnes) return false;
        int sum = (sumByTwoBytes(data) + checkSum) & maskOfAllOnes;
        return sum == maskOfAllOnes;
    }

    public static void main (String argv[]){

        int maxTestSize = 1000;
        int numTests = 5;
        byte [][] data = new byte [numTests][maxTestSize];

        data[0] = new byte[]{};
        data[1] = new byte[]{(byte)255, (byte)255, 0};
        data[2] = new byte[]{23, 45, 67, 124};
        data[3] = "Hello world!".getBytes();

        data[4] = new byte [1000];
        for (int i = 0; i < 1000; i++) data[4][i] = (byte)0;

        int [] rightAnswers = {65535, 0, 42326, 36370, 65535};

        for (int test = 0; test < numTests; test++) {

            assert getCheckSum(data[test]) == rightAnswers[test] : "getCheckSum failed on test " + test;
            assert verifyCheckSum(data[test], rightAnswers[test]) :
                    "verifyCheckSum failed on test " + test + " (false negative)";

            // corrupt random bit in checkSum
            int randomBit = (int)Math.floor(Math.random() * 16);
            assert !verifyCheckSum(data[test], rightAnswers[test] ^ (1<<randomBit)) :
                    "verifyCheckSum failed on test " + test + " (false positive)";
        }

    }
}
