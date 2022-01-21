import java.io.FileReader;
import java.io.File;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Stack;


public class Solution {
    private LinkedList<Integer> bits;

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

    private int bin2dec(ArrayList<Integer> bitSequence) {
        int dec = 0;

        for (int i=0; i < bitSequence.size(); ++i) {
            dec += bitSequence.get(i) * Math.pow(2, bitSequence.size() - 1 - i);
        }

        return dec;
    }

    private int parseLiteral() {
        int isParsing             = this.bits.poll();
        ArrayList<Integer> nibble = new ArrayList<>();

        while (isParsing == 1) {
            for (int i=0; i < 4; ++i) { nibble.add(this.bits.poll()); }
            isParsing = this.bits.poll();
        }

        for (int i=0; i < 4; ++i) { nibble.add(this.bits.poll()); }

        return bin2dec(nibble);
    }

    public void solveP1() {
        int versionSum  = 0;
        Integer version = null;
        Integer ID      = null;
        Integer lengthTypeID;

        while (!this.bits.isEmpty()) {

            if (version == null) { // version is null, start of a new packet
                ArrayList<Integer> versionBits = new ArrayList<>();
                ArrayList<Integer> idBits      = new ArrayList<>();

                for (int i=0; i < 3; ++i) { versionBits.add(this.bits.poll()); }
                for (int i=0; i < 3; ++i) { idBits.add(this.bits.poll()); }

                version = bin2dec(versionBits);
                ID      = bin2dec(idBits);
                versionSum += version;
            }

            if (ID == 4) { // parse a literal
                parseLiteral();
                version = null;
            } else { // parsing an operator
                lengthTypeID = this.bits.poll();

                if (lengthTypeID == 0) {
                    for (int i=0; i < 15; ++i) { this.bits.poll(); }
                    version = null;
                } else if (lengthTypeID  == 1) {
                    for (int i=0; i < 11; ++i) { this.bits.poll(); }
                    version = null;
                }
            }

            if (this.bits.size() <= 6) {
                System.out.println(this.bits);
                break;
            }
        }

        System.out.println("The version sum is: " + versionSum);
    }

    public int solve(int version, int ID) {
        if (ID == 4) {
            return parseLiteral();
        } else {
            int lengthTypeID = this.bits.poll();



            return 0;
        }
    }
}