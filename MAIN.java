package we;
import java.awt.*;
import java.io.*;
import java.util.*;
public class MAIN
{
	public String[] words_original;	//初始的所有单词数组,按读进来的数据中的次序依次将文章拆分成单词
	
	public Vector<String> edge=new Vector<>();//顶点数组
	
	public int vertex;//顶点个数
	
	public Map<String, Integer> edge_weight=new HashMap<String,Integer>();//边和权值
	
	public Vector<String> edge_edge=new Vector<String>();//依次存放边
	
	public Map<String, Integer> vertex_number=new HashMap<String,Integer>();//顶点对应的数字	
	
	public Map<Integer,String> number_vertex=new HashMap<Integer,String>();//数字对应的顶点
	
	public int [][] edge_matrix=null;//邻接矩阵
	
	int max_weight=100000;//无路径的初始值
	
	public GraphViz gv_1 = new GraphViz();//一个点到所有点的路径
	
	public void showDirectedGraph()//输出有向图
	{
		GraphViz gv = new GraphViz();
	     gv.addln(gv.start_graph());
	     /**
	      * 按循序遍历边容器，依次取出每条边和此边的权重，权重作为路径的label;
	      */
		for(int i=0;i<this.edge_edge.size();i++)
		{

			String add_edge=this.edge_edge.elementAt(i);//边
			String strweight=this.edge_weight.get(add_edge).toString();//边的权值
			String style=add_edge+"[ label="+strweight+"]"+";";
			gv.add(style);
		}
		gv.addln(gv.end_graph()); //将边传给dot
	    String type = "gif";
	    File out = new File("out." + type);   
	    
	    /**
	     * gv.getDotSource()--将图片转换成字符串格式
	     * gv.getGraph( gv.getDotSource(), type ) --将字符串转换成字节数组
	     * writeGraphToFile --将图片输出到文件out中
	     */
	    gv.writeGraphToFile( gv.getGraph( gv.getDotSource(), type ), out );
	    
	    try {
			Desktop.getDesktop().open(new File("OUT.GIF"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public String queryBridgeWords(String word1, String word2)//查询桥接词
	{
		
		String bridgewords="";//返回的字符串，记录bridgewords
		
		String temp=word1+" -> "+word2; //连个单词组成的边名称
		//如果顶点word1到word2有相邻路径则两个单词之间没有bridgeword
		if(this.edge_edge.contains(temp)==true)
		{
			return ("N0 bridge words from "+word1+" to "+word2+"!");
		}
		//如果两个单词不相邻，查看这两个单 词在不在顶点集中
		else if(this.vertex_number.containsKey(word1) && this.vertex_number.containsKey(word2))
		{
			int wordnum1=this.vertex_number.get(word1);
			int wordnum2=this.vertex_number.get(word2);
		
			for(int i=0;i<this.vertex;i++)
			{
				if(this.edge_matrix[wordnum1][i]!=this.max_weight)
				{
					if(this.edge_matrix[i][wordnum2]!=this.max_weight)
					{
						bridgewords+=this.number_vertex.get(i)+", ";			
					}
				}
			}
			//如果一个bridgeword都没有
			if(bridgewords=="")
			{
				return ("No bridge words from "+word1+" to "+word2+"!");
			}
			//如果找到了
			bridgewords="The bridge words from "+word1+" to "+word2+" are: "+bridgewords;
		}
		//word1或者word2不在顶点集中
		else if(this.vertex_number.containsKey(word1)==false || this.vertex_number.containsKey(word2)==false)
		{
			bridgewords=bridgewords+"No "+word1+" or "+word2+ " in the graph!";
			return bridgewords;
		}
		//若单词在文本中，但是没有bridge word或者两个单词之间有路径
		else {
			return ("No bridge words from "+word1+" to "+word2+"!");
		}
		return bridgewords;
	}
	
	
	public String generateNewText(String inputText)//根据桥接词生成新文本
	{
		//将inputText按照空格拆分拆分成单词，依次放入字符串数组中,inputText本身并不变
		String strtemp=inputText.replaceAll("[^a-zA-Z]+", " ").toLowerCase();
		String[] inputWords=strtemp.split("[\\s]");
	    String result="";
	    
		for(int i=0;i<inputWords.length-1;i++)
		{
			result=result+inputWords[i]+" ";   //加入输入文本的第一个单词
			Vector<String> vectemp=new Vector<>();//存放所有桥词，后面随机产生下表选取其中一个
			//如果两个单词都在原来的文本中
			if(this.vertex_number.containsKey(inputWords[i]) && this.vertex_number.containsKey(inputWords[i+1]))
			{
				int wordnum1=this.vertex_number.get(inputWords[i]);
				int wordnum2=this.vertex_number.get(inputWords[i+1]);
				for(int j=0;j<this.vertex;j++)
				{
					if(this.edge_matrix[wordnum1][j]!=this.max_weight)
					{
						if(this.edge_matrix[j][wordnum2]!=this.max_weight)
						{
							if(this.edge_matrix[wordnum1][wordnum2]==this.max_weight)//两个单词之间不能有路径
							{
								vectemp.add(this.number_vertex.get(j));
							}
						}
					}
				}
			}
			//如果vectemp不空，即里面有bridge word,从其中随机选取一个
			if(vectemp.isEmpty()==false)
			{
				Random r=new Random();
				int index=r.nextInt(vectemp.size());//产生vectemp长度之内的随机数
				result=result+vectemp.get(index)+" ";
				vectemp.clear();
			}
		}
		result=result+inputWords[inputWords.length-1];//加上输入文本最后一个单词
		return result;
	}
	
	public String calcShortestPath(String word1, String word2)//最短路径
	{
		Vector<String> min_pass_edge=new Vector<>();//记录两点间的路径（包括这俩个点）
		String returnstring="";//记录亮点间的路径（包括亮点）
		//记录两个顶点之间的最短路径所经过的边和对应的权值
		Map<String, Integer> min_edge_weigth=new HashMap<String, Integer>();//亮点最短路径经过的边和权值
		//先判断这两个单词是否在顶点集中
		if(this.vertex_number.containsKey(word1)==false || this.vertex_number.containsKey(word2)==false)
		{
			return  "No "+word1+" or "+word2+ "in the graph!";
		}
		//String resultstring="";//返回值
		int[][] D=new int[this.vertex][this.vertex];
		int[][] P=new int[this.vertex][this.vertex];//表示编号为i,j边之间的最短路径
		for(int i=0;i<this.vertex;i++)
		{
			for(int j=0;j<this.vertex;j++)
			{
				D[i][j]=this.edge_matrix[i][j];
				P[i][j]=-1;
			}
		}

		for(int k=0;k<this.vertex;k++)
		{
			for(int i=0;i<this.vertex;i++)
			{
				for(int j=0;j<this.vertex;j++)
				{
					if(D[i][k]+D[k][j] <D[i][j])
					{
						D[i][j]=D[i][k]+D[k][j];
						P[i][j]=k;
					}
				}
			}
		}
		 
		int i=this.vertex_number.get(word1);
		int j=this.vertex_number.get(word2);
		 min_pass_edge.add(word1);
		if(D[i][j]!=this.max_weight &&i!=j)
       { 
           print_minpass(P, i, j,min_pass_edge);
       }
		min_pass_edge.add(word2);
		//如果两个单词不可达
		if(min_pass_edge.size()==2 && min_pass_edge.get(0)==word1&&min_pass_edge.get(1)==word2&&this.edge_edge.contains(word1+" -> "+word2)==false)
		{
			return "No pass from "+word1+" to "+word2+ " !";
		}
		//将路径上的顶点加入returnresult中
		for(int k=0;k<min_pass_edge.size()-1;k++)
		{
			returnstring=returnstring+min_pass_edge.get(k)+" -> ";
		}
		returnstring=returnstring+min_pass_edge.get(min_pass_edge.size()-1)+" -> ";//加入最后一个单词
		//展示最短路径图
		 //将两点间的最短路径经过的边和权值加入min_edge_weight中,
		 for(int k=0;k<min_pass_edge.size()-1;k++)
		 {
			 String frist=min_pass_edge.get(k);
			 String next=min_pass_edge.get(k+1);
			 String tempstring=frist+" -> "+next;//边
			 int fristnum=this.vertex_number.get(frist);	//两个顶点的编号
			 int nextnumm=this.vertex_number.get(next);
			 int tempweigt=this.edge_matrix[fristnum][nextnumm];//权值
			 min_edge_weigth.put(tempstring, tempweigt);
		 }
		 //	绘制边的时候，如果边在min_edge_weight中用凸显的颜色标识否则用默认颜色
		GraphViz gv = new GraphViz();
	    gv.addln(gv.start_graph());	
	    for(int k=0;k<this.edge_edge.size();k++)
		{			
			String add_edge=this.edge_edge.elementAt(k);//一对边
			String strweight=this.edge_weight.get(add_edge).toString();//边的权值
			String style=null;	
			//如果边add_edge在min_edge_weight中,则颜色为红色
			if(min_edge_weigth.containsKey(add_edge))
			{
				 style=add_edge+"[ color=red,label="+strweight+"]"+";";
			}
		
			//否则为默认
			else {
				style=add_edge+"[ label="+strweight+"]"+";";
			}				
		
			gv.add(style);
		}
			
		gv.addln(gv.end_graph());
       //System.out.println(gv.getDotSource());  
		String type = "gif";
	    File out = new File("minpass." + type);    
	    gv.writeGraphToFile( gv.getGraph( gv.getDotSource(), type ), out );		 
	    try {
			Desktop.getDesktop().open(new File("minpass.GIF"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return returnstring;
	}
	
	public String randomWalk()//随机游走
	{   
		//随机游走的路径
		String strresult="";
		//记录出现过的边，如果重复出现则结束
		Vector<String> vecresult=new Vector<>();
		//产生顶点个数vertex范围之内的一个数字,将该数字对应的顶点作为起始位置
		Random r=new Random();
		//第一个顶点对应的编号
		int frist_number=r.nextInt(vertex);
		//将第一个边加入strresult中
		strresult=strresult+this.number_vertex.get(frist_number)+" ";
		
		Scanner in=new Scanner(System.in);
		System.out.print("是否继续(Y/N): ");
		String choose1=in.next();
		if(choose1.equals("N") ||choose1.equals("n"))
		{
			in.close();
			return strresult;
		}
		if(choose1.equals("Y") ||choose1.equals("y"))
		{
			System.out.println(strresult);
		}
		
		while(true)
		{		
				
			Vector<String> vectemp=new Vector<>();
		    for(int j=0;j<vertex;j++)
		    {
		    	if(edge_matrix[frist_number][j]!=this.max_weight)
		    	{	
		    		vectemp.add(this.number_vertex.get(j));
		    	}
		    }
		    
		  //如果vectemp空，即frist_number顶点没有出路，则结束
		    if(vectemp.isEmpty())
		    {
		    	break;
		    }
		    //vectemp里面是遇上一个边frist_number有路径的所有顶点
		    if(vectemp.isEmpty()==false)//如frist_number对应的边有下一条边
		    {
		    	//从vectemp中随选出一个顶点
		    	Random r1=new Random();
				int next_number=r1.nextInt(vectemp.size());
				//如果边 "strat_number -> next "没出现过则继续，否则结束并返回结果,从vectemp中取出随即边vectemp.get(next_number)
				String temp=this.number_vertex.get(frist_number)+" -> "+vectemp.get(next_number);
				
				if(vecresult.isEmpty()==true)
				{
					vecresult.add(temp);
					strresult=strresult+vectemp.get(next_number)+" ";
					//next_number对应的边成为新的起始点frist_number，
					frist_number=this.vertex_number.get(vectemp.get(next_number));
					
					 System.out.print("是否继续(Y/N): ");
					 String choose=in.next();
					if(choose.equals("Y")||choose.equals("y"))
					{
						System.out.println(strresult);
						continue;
					}
					else {
						break;
					}
					
				}
				//若该边temp在vecresutl出现过则结束
				 if(vecresult.get(0).equals(temp)==false)
				{
					//没有出现过则将，这对边temp放入vecresult中
					vecresult.add(temp);
					strresult=strresult+vectemp.get(next_number)+" ";
					//next_number对应的边成为新的起始点frist_number，
					frist_number=this.vertex_number.get(vectemp.get(next_number));
				}	
				 else 
				{
					strresult=strresult+vectemp.get(next_number)+" ";
					break;
				}
		    }
		    
		    System.out.print("是否继续(Y/N): ");
			String choose=in.next();
			if(choose.equals("Y")||choose.equals("y"))
			{
				System.out.println(strresult);
				continue;
			}
			else {
				break;
			}
		}
		in.close();
		return  strresult;
	}

	public static void main(String[] args) 
	{
			MAIN obj1=new MAIN();
			String str=new String();
			Scanner cin=new Scanner(System.in);
			while(true)
			{
				System.out.println("1- 从文件读取数据! 2-手动写入数据!");
				System.out.print("请输入选择:");
				String choose=cin.nextLine();
				if(choose.equals("1"))
				{
					/**
					 * 从文件读入数据，并对其进行处理，最后将所有单词按文本中的循序拆分复制给obj1.words_original
					 */
					str="";
					System.out.print("请输入文件路径:");
					
					String StrFilename=cin.nextLine();
					// StrFilename="input.txt";
					try {
						File file1=new File(StrFilename);
						FileReader fr=new FileReader(file1);
						BufferedReader br=new BufferedReader(fr);
						
						String temp=null;
						int i=1;
						while((temp=br.readLine()) != null)
						{
							if(i==1)
							{
								str=str+temp;//第一行无空格
							}
							else {
								str=str+" "+temp;//每读一行加一个空格
							}
							i++;
						}
						br.close();
					}catch (Exception e) {
						e.printStackTrace();
					}
					
					break;
				}
				if(choose.equals("2"))
				{
					System.out.print("请输入文本:");
					str=cin.nextLine();
					break;
				}
			}
		
			String str1=str.replaceAll("[^a-zA-Z]+", " ");
			String str2=str1.toLowerCase();
			obj1.words_original=str2.split("[\\s]");
	
			for(int i=0;i<obj1.words_original.length-1;i++)//生成图，按照顺序得到每条边对应的字符串，并获得其权值
			{
				String temp=obj1.words_original[i]+" -> "+obj1.words_original[i+1];
				
				//第一次加入时权值为1
				if(obj1.edge_weight.containsKey(temp)==false)
				{
					obj1.edge_edge.add(temp);//将新的一对边放进vector edge_dege后面(若重复出现则按照第一次插入的为准
					obj1.edge_weight.put(temp, 1);
				}
				//如果已经有了边和权值，则权值加一
				else {
					int nutemp=obj1.edge_weight.get(temp);
					nutemp+=1;
					obj1.edge_weight.put(temp, nutemp);
				}
			}
			/**	
			 * 初始化 vertex_number   ; number_vertex
			 * 从头到尾遍历原来的单词表，对每一个单词按照文本中出现的次数给予0....的值，若有重复出现的单词，则以第一次给予的值为准
			 * 给每个定点赋给一个值，来表示每个定点在邻接矩阵的表示的数字
			 */
			int number=-1;//每条边对应的数字，从零开始
			for(int i=0;i<obj1.words_original.length;i++)//遍历单词表，按照次序给单词赋值
			{   
				//vertex_number，number_vertex每个key和value相反
				if( obj1.vertex_number.containsKey(obj1.words_original[i])==false)
				{
					number+=1;
					obj1.vertex_number.put(obj1.words_original[i], number);
					obj1.number_vertex.put(number,obj1.words_original[i]);
					obj1.edge.add(obj1.words_original[i]);
				}
			}
			obj1.vertex=obj1.number_vertex.size();
			//邻接矩阵初始化，全部为max_weight
			obj1.edge_matrix=new int[obj1.vertex][obj1.vertex];
			for(int i=0;i<obj1.vertex;i++)//初始化邻接矩阵
			{
				for(int j=0;j<obj1.vertex;j++)
				{
					obj1.edge_matrix[i][j]=obj1.max_weight;
				}
			}
			
			for(int i=0;i<obj1.vertex;i++)//创建有向图
			{
				for(int j=0;j<obj1.vertex;j++)
				{
					String edgei=obj1.number_vertex.get(i);
					String edgej=obj1.number_vertex.get(j);
					//如果这两个边之间有路径
					if(obj1.edge_weight.containsKey(edgei+" -> "+edgej))
					{
						int weight=obj1.edge_weight.get(edgei+" -> "+edgej);
						obj1.edge_matrix[i][j]=weight;
					}
				}
			}
		int flag=1;
		while(flag!=0)
		{
			System.out.println("请选择功能");
			System.out.println("1.展示有向图");
			System.out.println("2.查询桥接词");
			System.out.println("3.生成新文本");
			System.out.println("4.两个单词最短路径");
			System.out.println("5.一个单词最短路径");
			System.out.println("6.随机游走");
			System.out.println("0.结束");
			String choose=cin.nextLine();
			if(choose.equals("1"))
		{ 
				obj1.showDirectedGraph();//展示有向图}
		}
			else if (choose.equals("2"))
			{	
			System.out.println("******************* 查询两个单词之间的桥词******************* :");
			System.out.print("please input word 1 :");
			String word1=cin.nextLine();
			System.out.print("please input word 2 :");
			String word2=cin.nextLine();	
			String BridgeWords=obj1.queryBridgeWords(word1, word2);
			System.out.println(BridgeWords);
			}
			else if(choose.equals("3"))
			{
			System.out.println(" \n******************* 根据桥词形成新新文本*******************  :");
			System.out.print("请输入文本 :");
			String NewTex=cin.nextLine();
			System.out.println(obj1.generateNewText(NewTex));
			}
			else if(choose.equals("4"))
			{
			System.out.println(" \n ******************* 两个单词之间的最短路径:******************* ");
			System.out.print("please input word 1 :");
			String word1=cin.nextLine();
			System.out.print("please input word 2 :");
			String word2=cin.nextLine();
	  		 String minpass=obj1.calcShortestPath(word1, word2);
			 System.out.println("两点间的最短路径是： "+minpass);
			}
			else if(choose.equals("5"))
			{
			 System.out.println(" \n ******************* 一个单词到所有单词之间的最短路径:******************* ");
			 obj1.gv_1.addln(obj1.gv_1.start_graph());	
			 System.out.print("请输入一个单词:");
			 String word3=cin.nextLine();
			 obj1.calcShortestPath(word3);
			}
			else if(choose.equals("6"))
			{
			 System.out.println(" \n *******************随机游走*******************");
	        String ResutlRanWalk=obj1.randomWalk();
	        System.out.print(ResutlRanWalk);
   		     cin.close();	
			
			try 
			{
				File file1=new File("outrandwalk.txt");
				FileWriter fr=new FileWriter(file1);
				fr.write(ResutlRanWalk);
				fr.close();
				
			}catch (Exception e) 
			{
				e.printStackTrace();
			}
			
		    try 
		    {
				Desktop.getDesktop().open(new File("outrandwalk.txt"));
			} catch (IOException e) 
		    {
				e.printStackTrace();
			}
			}
			else if(choose.equals("0"))
			{
				System.out.println("谢谢使用！");
				System.exit(-1);
			}
		}
	}
		
	public void print_minpass(int p[][],int i,int j,Vector<String> min_pass_edge)//记录路径
	{
		int k=p[i][j];
		if(k != -1)
		{
			print_minpass(p, i, k,min_pass_edge);
			min_pass_edge.addElement(this.number_vertex.get(k));
			print_minpass(p, k, j,min_pass_edge);	
		}
	}
	
	
	 public void calcShortestPath(String word1)
	 {
		 	String strcolor="";
			try {
				File file1=new File("color.txt");
				FileReader fr=new FileReader(file1);
				BufferedReader br=new BufferedReader(fr);
				
				String temp=null;
				int i=1;
				while((temp=br.readLine()) != null)
				{
					if(i==1)
					{
						strcolor=strcolor+temp;//第一行无空格
					}
					else {
						strcolor=strcolor+" "+temp;//每读一行加一个空格
					}
					i++;
				}
				br.close();
			}catch (Exception e) {
				e.printStackTrace();}
			String str1=strcolor.replaceAll("[^a-zA-Z]+", " ");
			String str2=str1.toLowerCase();
			String[] colors=str2.split("[\\s]");
			
	
			String c;
			 for(int i=0;i<this.edge.size();i++)
			 {
				 if(colors.length-1==i)
				 {
					  c=colors[0];
				 }
				else {
					 c=colors[i];
				}
				 if(this.edge.get(i).equals(word1) ==false&&this.edge.contains(word1))
				 {
					 this.calcShortestPath(word1, this.edge.get(i),c);
				 }
			 }
			 
			    for(int k=0;k<this.edge_edge.size();k++)
				{			
					String add_edge=this.edge_edge.elementAt(k);//边
					String strweight=this.edge_weight.get(add_edge).toString();//权值
					this.gv_1.add(add_edge+"[ label="+strweight+"]"+";");
				}
			    
			    this.gv_1.addln(this.gv_1.end_graph());  
				String type = "gif";
			    File out = new File("minpass_1." + type);    
			    this.gv_1.writeGraphToFile( this.gv_1.getGraph( this.gv_1.getDotSource(), type ), out );
			    
			    try {
					Desktop.getDesktop().open(new File("minpass_1." + type));
				} catch (IOException e) {
					e.printStackTrace();
				}
	 }
	 

		public void calcShortestPath(String word1, String word2,String cl) //最短路径
		{
			Vector<String> min_pass_edge=new Vector<>();	//记录两点间的路径（包括这俩个点）	
			
			int[][] D=new int[this.vertex][this.vertex];
			int[][] P=new int[this.vertex][this.vertex];//表示编号为i,j边之间的最短路径
			for(int i=0;i<this.vertex;i++)
			{
				for( int j=0;j<this.vertex;j++)
				{
					D[i][j]=this.edge_matrix[i][j];
					P[i][j]=-1;
				}
			}
			
			for(int k=0;k<this.vertex;k++)
			{
				for(int i=0;i<this.vertex;i++)
				{
					for(int j=0;j<this.vertex;j++)
					{
						if(D[i][k]+D[k][j] <D[i][j])
						{
							D[i][j]=D[i][k]+D[k][j];
							P[i][j]=k;
						}
					}
				}
			}
			 
			int i=this.vertex_number.get(word1);
			int j=this.vertex_number.get(word2);
			 min_pass_edge.add(word1);
			if(D[i][j]!=this.max_weight &&i!=j)
	        { 
	            print_minpass(P, i, j,min_pass_edge);
	        }
			min_pass_edge.add(word2);
			//将亮点间的最短路径经过的边和权值加入min_edge_weight中,
			 for(int k=0;k<min_pass_edge.size()-1;k++)//加入两点间的最短路径的边和权值
			 {
				 String frist=min_pass_edge.get(k);
				 String next=min_pass_edge.get(k+1);
				 String tempstring=frist+" -> "+next;//边
	
				 String style=tempstring+"[ color="+cl+"]"+";";
				 this.gv_1.add(style); 
			 }
		}
}




