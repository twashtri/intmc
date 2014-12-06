import java.io.*;
import java.util.logging.*;

// fixme: this is a monolithic piece of code
public class intmc {
  // fixme: use a class for the integer machine: using OOP techniques
  static int[] memory;
  static int INST_SIZE = 100;
  static int AC = 0;
  static int IR = 0;
  static int PC = 0;
  static int SC = 0;
  static int INST = 0;
  static int DATA = 0;
  static int ZFLAG = 1;
  static String logFileName = "intmc.log";
  static Logger d = Logger.getLogger(intmc.class.getSimpleName());

  public static void main(String[] args) throws Exception {
    // init logging file
    FileHandler fh;
    try {
      fh = new FileHandler(logFileName);
      d.addHandler(fh);
      SimpleFormatter sf = new SimpleFormatter();
      fh.setFormatter(sf);
    } catch(SecurityException se) {
      se.printStackTrace();
    } catch(IOException ioe) {
      ioe.printStackTrace();
    }
    if(fh==null) {
      System.err.println("filehandler not initialized. logging might not work!");
    }

    // flow
    d.info("load memory from serialized array.");
    if(args.length != 1) {
      throw new Exception("[FATAL] no memfile specified as argument.");
    }
    File memfile = new File(args[0]);
    if (memfile.exists()) {
      load_memory(memfile);
      d.info("memfile loaded.");
      d.info("going to start machine.");
      run_machine();
    } else {
      bye("FATAL: memfile does not exist.");
    }
  }

  static void bye(String exc) throws Exception {
    throw new Exception(exc);
  }

  static void run_machine() throws IOException, Exception {
    for (int i=0; ; i++) {
      fetch();
      decode();
      process();
      shift();
    }
  }

  static void fetch() {
    IR = memory[PC];
    SC = 1;
  }

  static void decode() {
    INST = IR % INST_SIZE;
    DATA = IR / INST_SIZE;
  }

  // fixme: possibly use a map to store <instruction, action> pair
  static void process() throws IOException, Exception {
    switch(INST) {
      case 0:
        d.info("EXIT");
        bye("exit machine");
        break;
      case 10:
        d.info("LOAD");
        AC = DATA;
        break;
      case 11:
        d.info("LOADX");
        AC = memory[DATA];
        break;
      case 20:
        d.info("STORE");
        memory[DATA] = AC;
        break;
      case 30:
        d.info("ADD");
        AC = AC + DATA;
        break;
      case 31:
        d.info("ADDX");
        AC = AC + memory[DATA];
        break;
      case 40:
        d.info("SUB");
        AC = AC - DATA;
        break;
      case 41:
        d.info("SUBI");
        AC = AC - memory[DATA];
        break;
      case 42:
        d.info("CMP");
        ZFLAG = AC - DATA;
        break;
      case 43:
        d.info("CMPX");
        ZFLAG = AC - memory[DATA];
        break;
      case 50:
        d.info("MUL");
        AC = AC * DATA;
        break;
      case 51:
        d.info("MULX");
        AC = AC * memory[DATA];
        break;
      case 60:
        d.info("DIV");
        AC = AC / DATA;
        break;
      case 61:
        d.info("DIVX");
        AC = AC / memory[DATA];
        break;
      case 70:
        d.info("MOD");
        AC = AC % DATA;
        break;
      case 71:
        d.info("MODX");
        AC = AC % memory[DATA];
        break;
      case 9:
        d.info("GOTO");
        PC = DATA;
        break;
      case 91:
        d.info("JZ");
        if(ZFLAG == 0) {
          PC = DATA;
        }
        break;
      case 92:
        d.info("JNZ");
        if(ZFLAG != 0) {
          PC = DATA;
        }
        break;
      case 80:
        d.info("DISPREG");
        System.out.println("-------------------------");
        System.out.println("  AC = " + AC);
        System.out.println("  PC = " + PC);
        System.out.println("  SC = " + SC);
        System.out.println("  IR = " + IR);
        System.out.println("  INST = " + INST);
        System.out.println("  DATA = " + DATA);
        System.out.println("  ZFLAG = " + ZFLAG);
        System.out.println("-------------------------");
        break;
      case 81:
        d.info("PRINT");
        System.out.println("["+DATA+"]: "+memory[DATA]);
        break;
      default:
        d.info("NULL");
        break;
    }
  }

  static void shift() {
    PC = PC + SC;
  }

  // fixme: memory warrants the need to be a separate object
  static void load_memory(File file) throws Exception {
    d.info("load_memory: loading memory from serialized array file.");
    ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
    memory = (int[]) ois.readObject();
    ois.close();
    if(memory == null) {
      bye("null memory exception. memfile is possibly corrupted.");
    }
  }
}

