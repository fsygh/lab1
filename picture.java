package picture;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.*;

import java.util.*;
import java.util.Random;

import javax.swing.*;
import javax.swing.JFileChooser;
import javax.swing.border.*;


public class picture {
    /*
     Define two hashmap

     map1 is the  (location of word) ->  word (like  0-> to)
     map2 is the word -> location of  word (like to-> 0)
    */
    static HashMap<String, String> map1 = new HashMap<String, String>();
    static HashMap<String, String> map2 = new HashMap<String, String>();
    static JFrame frame = new JFrame();

    /*
     target is the set of the location of the word in order
     tabStr is the input which divided by space and each one is a word
     graph  is the struct to storage the grapg
    */
    static String[] target;
    static String[] tabStr;
    static int[][] graph;

    /* Show graph and use the graphviz to paint the graph*/
    public static void showDirectedGraph(int[][] graph, String[] target) {
        GraphViz gViz = new GraphViz("G:\\nba",
                "F:\\Program Files (x86)\\Graphviz2.38\\bin\\dot.exe");
        int ch;
        int ch2;

        gViz.start_graph();

        for (int i = 0; i < (target.length - 1); i++) {
            ch = Integer.parseInt(tabStr[i]);
            ch2 = Integer.parseInt(tabStr[i + 1]);
            gViz.addln(map1.get(ch + "") + "->" + map1.get(ch2 + "")
            +"[label="+(graph[ch][ch2]+"")+"]"+";");
        }

        gViz.end_graph();

        try {
            gViz.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*Get the bridgeWord of name1 and name2 which main use by function of generateNewText */
    public static String GetBright(String name1, String name2) {
        String blank;
        String aim;
        blank = aim = "";

        String[] temp;
        int ch;
        int ch2;

        //Judge the name1 and name2 whether is exist
        if ((map1.containsValue(name1) == false) ||
                (map1.containsValue(name2) == false)) {
            return null;
        } else {
            ch = Integer.parseInt(map2.get(name1));
            ch2 = Integer.parseInt(map2.get(name2));

            //Get the bridgeWord
            for (int i = 0; i < map2.size(); i++) {
                if ((graph[ch][i] != 0) && (graph[i][ch2] != 0)) {
                    blank += (map1.get(i + "").toString() + " ");
                }
            }

            if ((blank == null) || blank.isEmpty()) {
                return null;
            } else {
                temp = blank.split(" ");

                for (int i = 0; i < temp.length; i++)
                    aim += (temp[i] + " ");
            }

            return aim;
        }
    }

    /*Get the queryBridgeWords of name1 and name2 which use by function queryBridgeWords */
    public static String queryBridgeWords(String name1, String name2) {
        String blank;
        String aim;
        int ch , ch2;
        String[] temp;

        blank = aim = "";

        if (map1.containsValue(name1) == false && map1.containsValue(name2)) /*Two case which name1 and name2 not exist*/
        {
            aim = "No " + name1 +"  " + name2 + " in the graph!";
        }
        else if(map1.containsValue(name1) == false) {
        	aim = "No " + name1 + " in the graph!";
        }
        else if (map1.containsValue(name2) == false) {
            aim = "No " + name2 + " in the graph!";
        }
        else {
            ch = Integer.parseInt(map2.get(name1));
            ch2 = Integer.parseInt(map2.get(name2));

            for (int i = 0; i < map2.size(); i++) {
                if ((graph[ch][i] != 0) && (graph[i][ch2] != 0)) {
                    blank += (map1.get(i + "").toString() + " ");
                }
            }

            if ((blank == null) || blank.isEmpty()) {
                aim = "No bridge words from " + name1 + " to " + name2 + "!";
            } else {
                temp = blank.split(" ");
                aim = "The bridge words from " + name1 + " to " + name2 +
                    " are: ";

                for (int i = 0; i < temp.length; i++)
                    aim += (temp[i] + " ");
            }
        }

        return aim;
    }

    /*Use the function of GetBright() and get the right txt*/
    public static String generateNewText(String input) {

    	input = input.trim().replaceAll("[^A-Za-z., \n?!:;¡ª_(){}\"\'-]","")
        		.replaceAll("[^A-Za-z]"," ").replaceAll(" +"," ").toLowerCase();
        String[] test = input.split(" ");
        String[] test2;
        System.out.print(input);
        String aim,blank;

        aim = blank = "";
        blank = test[0] + " ";

        /*For word1 -> word2  use the function of GetBright()
          If  have bridgeword the  add it to blank (like word1->bridgeword->word2)
          else add directly to the string P (like word1->word2)
        */
        for (int i = 0; i < (test.length - 1); i++) {
            if ((GetBright(test[i], test[i + 1]) == null) ||
                    GetBright(test[i], test[i + 1]).isEmpty()) {
                blank += (test[i + 1] + " ");
            } else {
                aim = GetBright(test[i], test[i + 1]);
                test2 = aim.split(" ");
                if(test2.length>1)
                {
                	Random random = new Random();
                	int h = random.nextInt(test2.length);
                    blank += (test2[h] + " " + test[i + 1] + " ");
                }
                else
                {
                	blank +=aim+" "+test[i+1]+" ";
                }
            }
        }

        return blank;
    }

    /*Use floyd to get the shortest path of any two points*/
    public static String ShortLine(String word1, String word2) {
    	 int ch,ch2,size;
         String blank = "";

         size = map2.size();
         if (map1.containsValue(word1) == false) /*Two case which word1 and word2 not exist*/ {
             blank = "No " + word1 + " in the graph!";
         } else if (map1.containsValue(word2) == false) {
             blank = "No " + word2 + " in the graph!";
         }
         else
         {
             ch = Integer.parseInt(map2.get(word1));
             ch2 = Integer.parseInt(map2.get(word2));

             int[][] P = new int[size][size];
             int[][] temp = new int[size][size];

             /* First creat a two-dimensional array(temp) which the same as graph
                because we will change the graph
                Next use floyd to get the result and creat a matrix(P)  which storage the intermediate nodes
                Final make string blank storage the final path and use graphviz to paint the graph
             */
             for (int i = 0; i < size; i++) {
                 for (int j = 0; j < size; j++) {
                     if (graph[i][j] != 0) {
                         temp[i][j] = graph[i][j];
                     } else if (i != j) {
                         temp[i][j] = 9999;
                     }
                 }
             }

             for (int i = 0; i < size; i++) {
                 for (int j = 0; j < size; j++) {
                     if (temp[i][j] != 9999) {
                         P[i][j] = j;
                     } else {
                         P[i][j] = 0;
                     }
                 }
             }

             for (int k = 0; k < size; ++k) {
                 for (int i = 0; i < size; ++i) {
                     for (int j = 0; j < size; ++j) {
                         if ((temp[i][k] + temp[k][j]) < temp[i][j]) {
                             temp[i][j] = temp[i][k] + temp[k][j];
                             P[i][j] = P[i][k];
                         }
                     }
                 }
             }

             int next = P[ch][ch2];

             if (next == 0) {
                 blank = "No path from " + word1 + " to " + word2 + "!";
             } else {
                 GraphViz gViz = new GraphViz("G:\\nba",
                         "F:\\Program Files (x86)\\Graphviz2.38\\bin\\dot.exe");

                 gViz.start_graph();

                 for (int i = 0; i < (tabStr.length - 1); i++)
                     gViz.addln(target[i] + "->" + target[i + 1]);

                 blank = map1.get(ch + "").toString();
                 gViz.addln(blank + "[color=red]" +";");
                 blank += "->";
                 gViz.addln(map1.get(ch+"").toString()+"->"+map1.get(ch2+"").toString()+"[color=red]"+ "[label="+temp[ch][ch2]+"]"+";");
                 while (next != ch2) {
                     blank += (map1.get(next + "") + "->");
                     next = P[next][ch2];
                 }

                 blank = blank + map1.get(ch2 + "").toString();
                 gViz.addln(map1.get(ch2 + "").toString() + "[color=red]" + ";");
                 gViz.addln(blank + "[color=blue]" + ";");
                 gViz.end_graph();

                 try {
                     gViz.run();
                 } catch (Exception e) {
                     e.printStackTrace();
                 }
                 try {
                 String cmdText = "cmd /c start " + "G:\\nba\\temp.gif";
                 Runtime.getRuntime().exec(cmdText);
                 } catch (IOException e) {
                 e.printStackTrace();
                 }
             }
         }

         return blank;
    }

    public static String randomWalk() {
    	 Random random = new Random();

         String blank="";
         String tmp="";

         int k=0;
         int h = random.nextInt(map1.size());
         int Y[][]=new int[map1.size()][map1.size()];
         while(true)
         {

             for(int i=0;i<map1.size();i++)
             {

                 if(graph[h][i]!=0 && Y[h][i]==0)
                 {
                      Y[h][i]=1;
                      tmp=map1.get(h+"")+"->"+map1.get(i+"")+" ";
                      blank+=tmp;
                      JOptionPane.showMessageDialog(picture.frame,tmp);
                      String Word1 = JOptionPane.showInputDialog(picture.frame,
                         "Yes/No(input Y or N) ");

                      if (Word1.equals("Y"))
                      {
                          h=i;
                          break;
                      }
                     else
                     {
                         return blank;
                     }

                 }
                 else
                 {
                     k++;
                 }
             }
             if(k==map1.size())
                 break;
             else
                 k=0;
         }
         return blank;
    }

    public static void main(String[] args) {
        picture picture = new picture();
        GuiGraph panel = new GuiGraph();
        frame.add(panel);
        frame.setTitle("Choice meau");
        frame.setSize(400, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}


class GuiGraph extends JPanel implements ActionListener {
    static JLabel  lable1      = new JLabel("Open file path:");
    static JPanel  area        = new JPanel(new GridLayout(3, 1));
    static JButton pathTxt     = new JButton("Get txt");
    static JButton graphInit   = new JButton("Init");
    static JButton graphCreat  = new JButton("Creat");
    static JButton graphShow   = new JButton("Show");
    static JButton button_p3_1 = new JButton("Bridge word");
    static JButton button_p3_2 = new JButton("New text add on bridge word");
    static JButton button_p3_3 = new JButton("Shortest path");
    static JButton button_p3_4 = new JButton("Random walk");

    public GuiGraph() {
        this.setLayout(new GridLayout(1, 3));

        JPanel p1 = new JPanel(new GridLayout(2, 2));
        JPanel p2 = new JPanel(new GridLayout(2, 4));
        JPanel p3 = new JPanel(new GridLayout(2, 4));

        area.add(p1);
        area.add(p2);
        area.add(p3);
        this.add(area);

        graphInit.addActionListener(this);
        pathTxt.addActionListener(this);
        graphCreat.addActionListener(this);
        graphShow.addActionListener(this);
        button_p3_1.addActionListener(this);
        button_p3_2.addActionListener(this);
        button_p3_3.addActionListener(this);
        button_p3_4.addActionListener(this);


        p1.setBorder(new TitledBorder("File"));
        p1.add(lable1);
        p1.add(pathTxt);

        p2.setBorder(new TitledBorder("Graph"));
        p2.add(graphInit);
        p2.add(graphCreat);
        p2.add(graphShow);

        p3.setBorder(new TitledBorder("Oprations"));
        p3.add(button_p3_1);
        p3.add(button_p3_2);
        p3.add(button_p3_3);
        p3.add(button_p3_4);

    }

    public void actionPerformed(ActionEvent event) {
        Object source = event.getSource();

        /*the button is open the txt and get standard sentence
          Use a file choose to get the path of the txt
          By file flow to get data
          Use regular expression gets standard data
        */
        if (source == pathTxt) {
            String filePath = "";

            JFileChooser jfc = new JFileChooser();
            jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            jfc.showDialog(new JLabel(), "select");

            File file = jfc.getSelectedFile();

            if (file.isDirectory()) {
                System.out.println("file:" + file.getAbsolutePath());
            } else if (file.isFile()) {
                filePath = file.getAbsolutePath();
            }

            GuiGraph.lable1.setText(filePath);
            filePath = GuiGraph.lable1.getText();

            try {
                File fil = new File(filePath);
                FileInputStream br = new FileInputStream(fil);

                char ch;
                String data = "";

                for (int i = 0; i < fil.length(); i++) {
                    ch = (char) br.read();
                    data = data + ch;
                }

                br.close();

                data = data.trim().replaceAll("[^A-Za-z., \n?!:;¡ª_(){}\"\'-]","")
                		.replaceAll("[^A-Za-z]"," ").replaceAll(" +"," ").toLowerCase();
                JOptionPane.showMessageDialog(picture.frame, data);
                picture.target = data.split(" ");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /*  The function is init the data

            Creat two hashmap to storage key->value
            map1 is the location of word ->   word (like  0-> to)
            map2 is the word -> location of word (like to-> 0)

            tabStr is the list of the location of word in sentence
        */
        if (source == graphInit) {
            String[] tab = new String[picture.target.length];
            int j = 0;
            String blank = "";

            for (int i = 0; i < picture.target.length; i++) {
                if (picture.map1.containsValue(picture.target[i]) == false) {
                    blank = j + "";
                    picture.map1.put(blank, picture.target[i]);
                    picture.map2.put(picture.target[i], blank);
                    j++;
                }
            }


            for (int i = 0; i < picture.target.length; i++) {
                blank = (picture.map2.get(picture.target[i])).toString();
                tab[i] = blank;
            }

            picture.tabStr = tab;
            JOptionPane.showMessageDialog(picture.frame, picture.map1);
        }

        /* The function is creat the graph

           Use the list of tarStr to creat graph[][]

           Use graphViz paint the graph
        */
        if (source == graphCreat) {
            int[][] graph = new int[picture.map1.size()][picture.map1.size()];
            int ch;
            int ch2;

            for (int i = 0; i < (picture.target.length - 1); i++) {
                ch = Integer.parseInt(picture.tabStr[i]);
                ch2 = Integer.parseInt(picture.tabStr[i + 1]);
                graph[ch][ch2]++;
            }

            picture.graph = graph;
            picture.showDirectedGraph(picture.graph, picture.target);
            JOptionPane.showMessageDialog(picture.frame, "Creat graph over!");
        }

        /* The function is show the graph

           Use cmd to open the graph
        */
        if (source == graphShow) {
            try {
                String cmdText = "cmd /c start " + "G:\\nba\\temp.gif";
                Runtime.getRuntime().exec(cmdText);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /* the function is find the bridge word of name1 and name2

           Use the function of queryBridgeWords(word1,word2) to get result
        */
        if (source == button_p3_1) {
            String Word1 = JOptionPane.showInputDialog(picture.frame,
                    "Input word 1:");
            String Word2 = JOptionPane.showInputDialog(picture.frame,
                    "Input word 2:");
            JOptionPane.showMessageDialog(picture.frame,
                picture.queryBridgeWords(Word1, Word2));
        }

        /* the function is out the new txt which add the bridge word between two word from user's input

          Use the function of GetBright(word1,word2) to get result of word1 and word2 and add the txt
        */
        if (source == button_p3_2) {
            String Word1 = JOptionPane.showInputDialog(picture.frame,
                    "Please input the new txt:");
            JOptionPane.showMessageDialog(picture.frame,
                picture.generateNewText(Word1));
        }

        /* the function is find the shortLine  of word1 and word2

           Use the function of showInputDialog(word1,word2) to get result

           Use graphViz to paint the path and cmd to open the picture
        */
        if (source == button_p3_3) {
            String Word1 = JOptionPane.showInputDialog(picture.frame,
                    "Input the word 1:");
            String Word2 = JOptionPane.showInputDialog(picture.frame,
                    "Input the word 2:");
            JOptionPane.showMessageDialog(picture.frame,
                picture.ShortLine(Word1, Word2));
        }

        /*The function is randomwalk the graph

          Every edge  will get a judge which go or not and finally  out the all path
        */
        if (source == button_p3_4) {
    	   String h=picture.randomWalk();
           System.out.print(h);
           try{
               File file = new File("G:\\nba\\tete.txt");
               PrintStream ps = new PrintStream(new FileOutputStream(file));

               ps.println(h);
             } catch (FileNotFoundException e) {
                   e.printStackTrace();
           }
           JOptionPane.showMessageDialog(picture.frame, "Write in txt over");
        }
    }


    public static void main(String[] args) {
        new GuiGraph();
    }
}


class GraphViz {
    private String runPath = "";
    private String dotPath = "";
    private String runOrder = "";
    private String dotCodeFile = "dotcode.txt";
    private String resultGif = "temp";
    private StringBuilder graph = new StringBuilder();
    Runtime runtime = Runtime.getRuntime();

    public GraphViz(String runPath, String dotPath, String nameGif) {
        this.runPath = runPath;
        this.dotPath = dotPath;
        this.resultGif = nameGif;
    }

    public GraphViz(String runPath, String dotPath) {
        this.runPath = runPath;
        this.dotPath = dotPath;
    }

    public void run() {
        File file = new File(runPath);
        file.mkdirs();
        writeGraphToFile(graph.toString(), runPath);
        creatOrder();

        try {
            runtime.exec(runOrder);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void creatOrder() {
        runOrder += (dotPath + " ");
        runOrder += runPath;
        runOrder += ("\\" + dotCodeFile + " ");
        runOrder += "-T gif ";
        runOrder += "-o ";
        runOrder += runPath;
        runOrder += ("\\" + resultGif + ".gif");
        System.out.println(runOrder);
    }

    public void writeGraphToFile(String dotcode, String filename) {
        try {
            File file = new File(filename + "\\" + dotCodeFile);

            if (!file.exists()) {
                file.createNewFile();
            }

            FileOutputStream fos = new FileOutputStream(file);
            fos.write(dotcode.getBytes());
            fos.close();
        } catch (java.io.IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public void add(String line) {
        graph.append("\t" + line);
    }

    public void addln(String line) {
        graph.append("\t" + line + "\n");
    }

    public void addln() {
        graph.append('\n');
    }

    public void start_graph() {
        graph.append("digraph G {\n");
    }

    public void end_graph() {
        graph.append("}");
    }
}
