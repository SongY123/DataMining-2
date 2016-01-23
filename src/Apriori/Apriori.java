package Apriori;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import javax.naming.spi.DirStateFactory.Result;

public class Apriori {
	static double minsup=0.144;
	static int row = 1000;
	static int col = 11;
	static int MINSUP=(int)(minsup*1000);
	public static class item implements Comparable<item>{
		int []it = new int[col];
		int count;
		int transcount;
		
		public item(){
			count = 0;
		}
		
		public int size(){
			return count;
		}
		public void settranscount(int tc){
			transcount = tc;
		}
		public void insert(int inserti){
			int i = 0;
			for(;i<col;i++)
				if(it[i]>inserti||it[i]==0)break;
			for(int j = col-1;j>i;j--)
				it[j] = it[j-1];
			it[i] = inserti;
			count++;
		}
		public int[]get(){
			int []temp = new int [count];
			for(int i = 0;i<count;i++)
				temp[i] = it[i];
			return temp;
		}
		
		public void printItem(){
			for(int i = 0;i<count;i++)
			System.out.print(it[i]+" ");
			System.out.printf("%.3f",((double)transcount)/1000);
		}
		
		public boolean isExists(int itemp){
			for(int i = 0;i<col;i++)
				if(it[i]==itemp)return true;
			return false;
		}
		
		public boolean isSubset(item itemp){
			for(int i = 0;i<count;i++)
				if(itemp.isExists(it[i])==false)return false;
			return true;
		}
		
		public item gen(item itemp){
			item result = new item();
			for(int i = 0;i<count;i++)
				if(result.isExists(it[i])==false)result.insert(it[i]);
			for(int i = 0;i<itemp.count;i++)
				if(result.isExists(itemp.it[i])==false)result.insert(itemp.it[i]);
			return result;
		}
		
		public boolean equals(Object o){
			if(o==null)return false;
			if(o==this)return true;
			if(o instanceof item){
				if(count!=((item)o).count)return false;
				for(int i = 0;i<count;i++)
					if(it[i]!=((item)o).it[i])return false;
				return true;
			}
			return false;
		}

		@Override
		public int compareTo(item itemp) {
			for(int i = 0;i<count&&i<itemp.count;i++){
				if(it[i]<itemp.it[i])return -1;
				else if(it[i]>itemp.it[i])return 1;
			}
			if(count<itemp.count)return -1;
			else if(count>itemp.count)return 1;
			return 0;
		}

	}
	
	static item []transaction = new item[row];
	static ArrayList <item>[]itemSet = new ArrayList[col];
	
	public static void  find_frequent_1_itemset(){
		for(int i = 0;i<col;i++){
			int count = 0;
			for(int j = 0;j<row;j++)
				if(transaction[j].isExists(itemSet[0].get(i).get()[0]))count++;
			if(count<MINSUP)itemSet[0].remove(i);
			else {
				item t = itemSet[0].get(i);
				itemSet[0].remove(i);
				t.settranscount(count);
				itemSet[0].add(i,t);
			}
			//System.out.println(count);
		}
	}
	
	public static ArrayList<item> apriori_gen(int index){
		ArrayList<item>result = new ArrayList<item>();
		
		//join
		for(int i = 0;i<itemSet[index].size();i++)
		{
			for(int j = i+1;j<itemSet[index].size();j++){
				item temp = itemSet[index].get(i).gen(itemSet[index].get(j));
				if(result.contains(temp)==false)result.add(temp);
			}
		}
		
		//prune
		for(int i = 0;i<result.size();){
			int count = 0;
			for(int j = 0;j<row;j++){
				if(result.get(i).isSubset(transaction[j]))count++;
			}
			if(count<MINSUP){
				result.remove(i);
			}
			else {
				item t = result.get(i);
				result.remove(i);
				t.settranscount(count);
				result.add(i, t);
				//t.printItem();
				i++;
			}
		}
		
		return result;
	}
	public static void main(String []args){
		File f = new File("src//assignment2-data.txt");
		InputStreamReader r;
		int all = 0;
		try {
			r = new InputStreamReader(new FileInputStream(f),"UTF-8");
			BufferedReader br = new BufferedReader(r);
			String line = null;
			int rowcount = 0;
			br.readLine();//ignore the first line
			while((line = br.readLine())!=null){
				String []result = line.split(" ");
				transaction[rowcount] = new item();
				for(int i = 0;i<col;i++){
					if(result[i].compareTo("1")==0){
						all++;
						transaction[rowcount].insert(i+1);
					}
				}
				//transaction[rowcount].printItem();
				/*
				for(int i = 0;i<col;i++)
					System.out.print(transaction[rowcount][i]+"   ");
				System.out.println("");
				*/
				
				rowcount++;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for(int i = 0;i<col;i++)
			itemSet[i] = new ArrayList<item>();

		for(int i = 0;i<col;i++)
		{
			item temp = new item();
			temp.insert(i+1);
			itemSet[0].add(temp);
		}
		//System.out.println(all);
		find_frequent_1_itemset();
		
		/*for(int i = 0;i<itemSet.size();i++)
			itemSet.get(i).printItem();
		*/
		for(int i = 1;i<col&&itemSet[i-1].size()>0;i++){
			itemSet[i] = apriori_gen(i-1);
			
			//System.out.println(i +"   " + itemSet[i].size());
			/*for(int j = 0;j<itemSet.size();j++)
				itemSet.get(j).printItem();
				*/
		}
		
		ArrayList<item> itemSetall = new ArrayList<item>();
		for(int i = 0;i<col;i++)
		{
			for(int j = 0;j<itemSet[i].size();j++)
			{
				if(itemSetall.contains(itemSet[i].get(j))==false)itemSetall.add(itemSet[i].get(j));
			}
		}
		//sort
		Collections.sort(itemSetall);
		
		try {
			PrintStream out = new PrintStream(new BufferedOutputStream(new FileOutputStream("result.txt")));
			System.setOut(out);
			for(int i = 0;i<itemSetall.size();i++){
				itemSetall.get(i).printItem();
				if(i<itemSetall.size()-1)System.out.println("");
			}
			out.close();
			System.setOut(System.out);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//System.out.println(itemSetall.size());
	}
}
