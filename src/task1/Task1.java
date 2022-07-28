package task1;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Task1 {

    public static void main(String[] args) throws IOException {
        try {
            //load the file
            File SicFile = new File("inSIC.txt");
            //reading the file using scanner
            Scanner reader = new Scanner(SicFile);
            //separate 3 arrays
            List<String> labels = new ArrayList<>();
            List<String> instructions = new ArrayList<>();
            List<String> reference = new ArrayList<>();
            List<String> loc_ctr = new ArrayList<>();
            List<String> obj_code = new ArrayList<>();
            List<String> symbol = new ArrayList<>();
            List<String> address = new ArrayList<>();
            List<String> T = new ArrayList<>();
            List<String> location = new ArrayList<>();
            String data;
            //while file still has lines..
            while (reader.hasNextLine()) {
                data = reader.nextLine();
                // the "\\s+" is to escape all kinds of spaces whether tabs or whitespaces
                if (data.split("\\s+").length != 0) {
                    labels.add(data.split("\\s+")[0]);
                    instructions.add(data.split("\\s+")[1]);
                }
                //bat3amel m3 l data.split 3la enaha array 3shan heya btakhod l splitted elements w thotohom f array
                //if the line in the SIC file has 3 strings in it keep adding to the array..
                if (data.split("\\s+").length == 3) {
                    reference.add(data.split("\\s+")[2]);
                } // else if the line has 2 strings (labels and reference) then add hashtags 
                else if (labels.get(labels.size() - 1).equalsIgnoreCase("END")) { // this is to make the 3 arrays of the same size
                    int index = instructions.size() - 1;
                    reference.add(instructions.get(index));
                    instructions.set(index, labels.get(index));
                    labels.set(index, "####");
                } else if (data.split("\\s+").length == 2) { // this is to make the 3 arrays of the same size
                    reference.add("####");
                }

            }

            //<----------------LOCATION COUNTER:--------------------------------------------------------------------------------------->
            loc_ctr.add(reference.get(0));
            loc_ctr.add(reference.get(0));

            String tempMem = reference.get(0);
            //now checkig what to do in each of the following cases (RESW,RESB,BYTE X'', BYTE C'')
            for (int j = 1; j < labels.size(); j++) {
                //checks whether there are any empty elements in the arraylist to replace them with hashtags
                if (labels.get(j).isEmpty()) {
                    labels.set(j, "####");
                }
                //if instructions has RESW multiply by 3, convert to hex, then add to array
                if (instructions.get(j).equalsIgnoreCase("RESW")) {
                    int multiply = Integer.parseInt(reference.get(j)) * 3;
                    int addition = Integer.parseInt(tempMem, 16) + multiply;// convert hex string to decimal + the multiplication process
                    tempMem = Integer.toHexString(addition);
                    loc_ctr.add(tempMem);
                } //if instructions has RESB add decimals, convert to hex, then add to array
                else if (instructions.get(j).equalsIgnoreCase("RESB")) {
                    int add = Integer.parseInt(tempMem, 16) + Integer.parseInt(reference.get(j));
                    tempMem = Integer.toHexString(add);
                    loc_ctr.add(tempMem);
                }//if instructions has BYTE 
                else if (instructions.get(j).equalsIgnoreCase("BYTE")) {
                    //if it starts with X count each 2 hex as a 1 byte, then add to array
                    if (reference.get(j).startsWith("X")) {
                        String temp = reference.get(j);
                        int byteX = (temp.length() - 3) / 2; // -3 ly heya C''
                        int add = Integer.parseInt(tempMem, 16) + byteX;
                        tempMem = Integer.toHexString(add);
                        loc_ctr.add(tempMem);
                    }// if it starts with C count length then add to array
                    else if (reference.get(j).startsWith("C")) {
                        String temp = reference.get(j);
                        int byteC = temp.length() - 3; // -3 ly heya C''
                        int add = Integer.parseInt(tempMem, 16) + byteC;
                        tempMem = Integer.toHexString(add);
                        loc_ctr.add(tempMem);
                    }//if reference has a , in it  

                } else if (instructions.get(j).equalsIgnoreCase("WORD")) {
                    if (reference.get(j).contains(",")) {
                        location.add(reference.get(j).split(",")[0]);
                        location.add(reference.get(j).split(",")[1]);
                        int add = Integer.parseInt(tempMem, 16) + location.size() * 3;
                        tempMem = Integer.toHexString(add);
                        loc_ctr.add(tempMem);
                    }
                }//if the instructions doesn't contain RESW, RESB and BYTE keep adding 3   
                else {
                    //initiate a temp to make the addition process, then add it to the loc_ctr array
                    int addThree = Integer.parseInt(tempMem, 16) + 3; // convert hex string to decimal
                    tempMem = Integer.toHexString(addThree);
                    loc_ctr.add(tempMem);
                }

            }//remove additional location counter that needs to be discarded
            loc_ctr.remove(loc_ctr.size() - 1);

            //<-----SYMBOL TABLE---------------------------------------------------------------------------------------------------------------->
            for (int j = 1; j < address.size(); j++) {
                if (!labels.get(j).matches("####")) {
                    symbol.add(labels.get(j));
                    address.add(loc_ctr.get(j));
                }
            }

            //opcodes
            String[][] OPTAB = new String[59][3];

            OPTAB[0] = new String[]{"FIX", "1", "C4"};
            OPTAB[1] = new String[]{"FLOAT", "1", "C0"};
            OPTAB[2] = new String[]{"HIO", "1", "F4"};
            OPTAB[3] = new String[]{"NORM", "1", "C8"};
            OPTAB[4] = new String[]{"SIO", "1", "F0"};
            OPTAB[5] = new String[]{"TIO", "1", "F8"};
            OPTAB[6] = new String[]{"ADDR", "2", "90"};
            OPTAB[7] = new String[]{"CLEAR", "2", "B4"};
            OPTAB[8] = new String[]{"COMPR", "2", "A0"};
            OPTAB[9] = new String[]{"DIVR", "2", "9C"};
            OPTAB[10] = new String[]{"MULR", "2", "98"};
            OPTAB[11] = new String[]{"RMO", "2", "AC"};
            OPTAB[12] = new String[]{"SHIFTL", "2", "A4"};
            OPTAB[13] = new String[]{"SHIFTR", "2", "A8"};
            OPTAB[14] = new String[]{"SUBR", "2", "94"};
            OPTAB[15] = new String[]{"SVC", "2", "B0"};
            OPTAB[16] = new String[]{"TIXR", "2", "B8"};
            OPTAB[17] = new String[]{"ADD", "3", "18"};
            OPTAB[18] = new String[]{"ADDF", "3", "58"};
            OPTAB[19] = new String[]{"AND", "3", "40"};
            OPTAB[20] = new String[]{"COMP", "3", "28"};
            OPTAB[21] = new String[]{"COMPF", "3", "88"};
            OPTAB[22] = new String[]{"DIV", "3", "24"};
            OPTAB[23] = new String[]{"DIVF", "3", "64"};
            OPTAB[24] = new String[]{"J", "3", "3C"};
            OPTAB[25] = new String[]{"JEQ", "3", "30"};
            OPTAB[26] = new String[]{"JGT", "3", "34"};
            OPTAB[27] = new String[]{"JLT", "3", "38"};
            OPTAB[28] = new String[]{"JSUB", "3", "48"};
            OPTAB[29] = new String[]{"LDA", "3", "00"};
            OPTAB[30] = new String[]{"LDB", "3", "68"};
            OPTAB[31] = new String[]{"LDCH", "3", "50"};
            OPTAB[32] = new String[]{"LDF", "3", "70"};
            OPTAB[33] = new String[]{"LDL", "3", "08"};
            OPTAB[34] = new String[]{"LDS", "3", "6C"};
            OPTAB[35] = new String[]{"LDT", "3", "74"};
            OPTAB[36] = new String[]{"LDX", "3", "04"};
            OPTAB[37] = new String[]{"LPS", "3", "D0"};
            OPTAB[38] = new String[]{"MUL", "3", "20"};
            OPTAB[39] = new String[]{"MULF", "3", "60"};
            OPTAB[40] = new String[]{"OR", "3", "44"};
            OPTAB[41] = new String[]{"RD", "3", "D8"};
            OPTAB[42] = new String[]{"RSUB", "3", "4C"};
            OPTAB[43] = new String[]{"SSK", "3", "EC"};
            OPTAB[44] = new String[]{"STA", "3", "0C"};
            OPTAB[45] = new String[]{"STB", "3", "78"};
            OPTAB[46] = new String[]{"STCH", "3", "54"};
            OPTAB[47] = new String[]{"STF", "3", "80"};
            OPTAB[48] = new String[]{"STI", "3", "D4"};
            OPTAB[49] = new String[]{"STL", "3", "14"};
            OPTAB[50] = new String[]{"STS", "3", "7C"};
            OPTAB[51] = new String[]{"STSW", "3", "E8"};
            OPTAB[52] = new String[]{"STT", "3", "84"};
            OPTAB[53] = new String[]{"STX", "3", "10"};
            OPTAB[54] = new String[]{"SUB", "3", "1C"};
            OPTAB[55] = new String[]{"SUBF", "3", "5C"};
            OPTAB[56] = new String[]{"TD", "3", "E0"};
            OPTAB[57] = new String[]{"TIX", "3", "2C"};
            OPTAB[58] = new String[]{"WD", "3", "DC"};

//            //OBJECT CODE::
//            obj_code.add("------");
//            for (int j = 0; j < instructions.size(); j++) {
//                //if instructions has RESW don't add object code
//                if (instructions.get(j).equalsIgnoreCase("RESW")) {
//                    obj_code.add("------");
//                }//if instructions or labels has END don't add object code 
//                else if (instructions.get(j).equalsIgnoreCase("END") || labels.get(j).equalsIgnoreCase("END")) {
//                    obj_code.add("------");
//                }//if instructions has RESB don't add object code
//                else if (instructions.get(j).equalsIgnoreCase("RESB")) {
//                    obj_code.add("------");
//                } //if instructions has RSUB add opcode only 
//                else if (instructions.get(j).equalsIgnoreCase("RSUB")) {
//                    String RSUB = "0000";
//                    String op;
//                    for (int k = 0; k < 59; k++) {
//                        if (OPTAB[k][0].equalsIgnoreCase("RSUB")) {
//                            op = OPTAB[k][2] + RSUB;
//                            obj_code.add(op);
//                        }
//                    }
//                } //if reference has ",X" 
//                else if (reference.get(j).endsWith(",X")) {
//                    String split = reference.get(j).substring(0, reference.get(j).length() - 2);//ssubstring to remove ,X at the end of the reference
//                    String op;
//                    int index = symbol.indexOf(split);
//                    int loc = Integer.parseInt(address.get(index), 16);
//                    int ob = loc + 32768; // (32768)10 is the decimal of (8000)16
//                    for (int k = 0; k < 59; k++) {
//                        if (OPTAB[k][0].equalsIgnoreCase(instructions.get(j))) {
//                            op = OPTAB[k][2] + Integer.toHexString(ob);
//                            obj_code.add(op);
//                        }
//                    }
//                } //if instructions has WORD convert ref to hex, then add to object code
//                //                                else if (instructions.get(j).equalsIgnoreCase("WORD")) {
//                //                                    String b = Integer.toHexString(Integer.parseInt(reference.get(j)));//get the hex string of the reference
//                //                                    int c = Integer.parseInt(b);//then format it as integer
//                //                                    obj_code.add(String.format("%06d", c));
//                //                                }
//                //if instructions has BYTE 
//                else if (instructions.get(j).equalsIgnoreCase("BYTE")) {
//                    //if it starts with X, add it to object code as it is
//                    if (reference.get(j).startsWith("X")) {
//                        String temp = reference.get(j);
//                        obj_code.add(temp.substring(2, temp.length() - 1));
//                    }// if it starts with C, get ASCII code, then add to object code array
//                    else if (reference.get(j).startsWith("C")) {
//                        String temp = reference.get(j);//temp that stores the reference
//                        String split = temp.substring(2, temp.length() - 1);//gets the substring bteween C''
//                        String ascii = "";
//                        for (int i = 0; i < split.length(); i++) {
//                            ascii += Integer.toHexString((int) split.charAt(i));//gets the Hex string of the integer ASCII code
//                        }
//                        obj_code.add(ascii);
//                    }
//                } //if the instructions doesn't contain RESW, RESB, BYTE, RSUB, WORD,                 
//                //ref or inst don't contain "END", and ref doesn't end with ",X" keep getting opcode+address
//                else {
//                    String op;
//                    int symindex = symbol.indexOf(reference.get(j));
//                    for (int k = 0; k < 59; k++) {
//                        if (OPTAB[k][0].equals(instructions.get(j))) {
//                            String addr = address.get(symindex);
//                            op = OPTAB[k][2] + addr;
//                            obj_code.add(op);
//                        }
//                    }
//                }
//            }
//
//            //printing process
            for (int i = 0; i < instructions.size(); i++) {
                System.out.println( "\t" + labels.get(i) + "\t" + instructions.get(i) + "\t" + reference.get(i));
            }
            System.out.println("location arraylist "+location.get(0));
            System.out.println("loc size "+loc_ctr.size());
            System.out.println("inst size "+instructions.size());
            System.out.println("label size "+labels.size());
            System.out.println("ref size "+reference.size());

//            // symbol table
//            System.out.println("\nSYMBOL TABLE:");
//            for (int j = 0; j < symbol.size(); j++) {
//                System.out.println(symbol.get(j) + "\t" + address.get(j));
//            }
            //HTE
            String s = loc_ctr.get(0);
            String start = String.format("%6s", s).replace(' ', '0');
            String e = loc_ctr.get(loc_ctr.size() - 1);
            String end = String.format("%6s", e).replace(' ', '0');
            int L = Integer.parseInt(end, 16) - Integer.parseInt(start, 16);
            String wl = Integer.toHexString(L);
            String wholeLength = String.format("%6s", wl).replace(' ', '0');
            //H record 
            String H = labels.get(0) + " ^ " + start + " ^ " + wholeLength;
            String obj = "";
            int currentAddress = 0;
            int lastAddress = 0;
            int length = 0;

            for (int i = 1; i < obj_code.size(); i++) {

                if (i < 11) {
                    if (instructions.get(i).equalsIgnoreCase("RESW") && instructions.get(i).equalsIgnoreCase("RESB") && instructions.get(i).equalsIgnoreCase("END")) {
                        break;
                    } else {
                        currentAddress = Integer.parseInt(loc_ctr.get(i), 16);
                        lastAddress = Integer.parseInt(loc_ctr.get(i), 16);
                        length = lastAddress - currentAddress;
                        obj += " " + Integer.toHexString(length) + "^" + obj_code.get(i);
                    }

                }

            }
            T.add(obj);
            //E
            String E = start;
            System.out.println("\nHTE:");
            System.out.println("H--> " + H);
            System.out.println("T--> " + start + " ^ " + T);
            System.out.println("E--> " + E);

            reader.close();

        } catch (FileNotFoundException e) {
            System.out.println("ERROR!!");
            e.printStackTrace();
        }
    }

}
