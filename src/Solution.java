import java.io.FileReader;
import java.io.File;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Stack;


public class Solution {
    private LinkedList<Integer> bits;
    private Integer versions = 0;

    public Solution(String filename) { parse(filename); }

    private void parse(String filename) {
        this.bits = new LinkedList<>();

        try {
            File f             = new File(filename);
            FileReader fr      = new FileReader(f);
            BufferedReader br  = new BufferedReader(fr);

            for (Character c : br.readLine().toCharArray()) {
                int hex               = (int)Long.parseLong(c.toString(), 16);
                Stack<Integer> nibble = new Stack<>();

                for (int i=0; i < 4; ++i) {
                    nibble.add(hex & 0x1);
                    hex = hex >> 1;
                }

                while (!nibble.isEmpty()) { bits.add(nibble.pop()); }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Long bin2dec(ArrayList<Integer> bitSequence) {
        Long dec = (long)0;

        for (int i=0; i < bitSequence.size(); ++i) {
            dec += bitSequence.get(i) * (long)Math.pow(2, bitSequence.size() -  1 - i);
        }

        return dec;
    }

    private Long parseLiteral(LinkedList<Integer> bits) {
        int isParsing             =  bits.poll();
        ArrayList<Integer> nibble = new ArrayList<>();

        while (isParsing == 1) {
            for (int i=0; i < 4; ++i) { nibble.add(bits.poll()); }
            isParsing = bits.poll();
        }

        for (int i=0; i < 4; ++i) { nibble.add(bits.poll()); }

        return bin2dec(nibble);
    }

    private boolean allZeros(LinkedList<Integer> bits) {
        LinkedList<Integer> copy = (LinkedList<Integer>) bits.clone();

        while (!copy.isEmpty()) {
            if (copy.poll() != 0) { return false; }
        }

        return true;
    }

    private Long computeSum(ArrayList<Long> literals) {
        Long sum = (long)0;

        for (Long literal : literals) { sum += literal; }

        return sum;
    }

    private Long computeProduct(ArrayList<Long> literals) {
        Long prod = (long)1;

        for (Long literal : literals) { prod *= literal; }

        return prod;
    }

    private Long computeMin(ArrayList<Long> literals) {
        Long min = null;

        for (Long literal : literals) {
            if ((min == null) || (literal < min)) { min = literal; }
        }

        return min;
    }

    private Long computeMax(ArrayList<Long> literals) {
        Long max = null;

        for (Long literal : literals) {
            if ((max == null) || (literal > max)) { max = literal; }
        }

        return max;
    }

    private int greaterThan(ArrayList<Long> literals) {
        if (literals.get(0) > literals.get(1)) return 1;
        else return 0;
    }

    private int lessThan(ArrayList<Long> literals) {
        if (literals.get(0) < literals.get(1)) return 1;
        else return 0;
    }

    private int equal(ArrayList<Long> literals) {
        if (literals.get(0).equals(literals.get(1))) return 1;
        else return 0;
    }

    public Long solveRec(LinkedList<Integer> bits) {
        if (allZeros(bits)) {
            return (long)0;
        } else {
            ArrayList<Integer> versionBits = new ArrayList<>();
            ArrayList<Integer> idBits      = new ArrayList<>();

            // obtain version and ID numbers
            for (int i=0; i < 3; ++i) { versionBits.add(bits.poll()); }
            for (int i=0; i < 3; ++i) { idBits.add(bits.poll()); }
            int version = (int)(long)bin2dec(versionBits);
            int ID      = (int)(long)bin2dec(idBits);
            this.versions += version;

            if (ID == 4) { // parse a literal
                return parseLiteral(bits);
            } else {
                int lengthTypeId = bits.poll();
                Long size;

                ArrayList<Integer>  packetSize    = new ArrayList<>();
                LinkedList<Integer> subPacketBits = new LinkedList<>();
                ArrayList<Long> subPackets        = new ArrayList<>();

                if (lengthTypeId == 0) { // get the sub packet size / number of sub packets
                    for (int i=0; i < 15; ++i) { packetSize.add(bits.poll());}
                } else {
                    for (int i=0; i < 11; ++i) { packetSize.add(bits.poll()); }
                }

                size = bin2dec(packetSize);

                if (lengthTypeId == 0) { // obtain sub packets
                    for (int i=0; i < size; ++i) { subPacketBits.add(bits.poll()); }
                    while (!subPacketBits.isEmpty()) { subPackets.add(solveRec(subPacketBits)); }
                } else {
                    while (subPackets.size() < size) { subPackets.add(solveRec(bits)); }
                }

                switch (ID) {
                    case 0: return computeSum(subPackets);
                    case 1: return computeProduct(subPackets);
                    case 2: return computeMin(subPackets);
                    case 3: return computeMax(subPackets);
                    case 5: return (long)greaterThan(subPackets);
                    case 6: return (long)lessThan(subPackets);
                    case 7: return (long)equal(subPackets);
                    default: return (long)0;
                }
            }
        }
    }

    public void solve() {
        System.out.println(solveRec(this.bits));
        System.out.println(this.versions);
    }
}