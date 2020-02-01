package ORM;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class ORMMetaData {
	private Connection cnx;
	private Statement stm;
	private ResultSet rs1;
	private ResultSet rs2;
	private ResultSetMetaData rsmt;
	private DatabaseMetaData dbmt;
	
	private FileWriter fw;
	private BufferedWriter br;
	
	Map<String,Map<String,String>> dbTables=new HashMap<String, Map<String,String>>();
	public ORMMetaData() {
		cnx=SinglTonConnection.getConnection();
		
	}
	public  void getTables() {
		 String ty[]= {"TABLE"};
		 HashMap<String ,String> columns;
		try {
			String tableName;
			stm=cnx.createStatement();
			dbmt= cnx.getMetaData();
			rs1=dbmt.getTables(null, null, "%", ty);
			
			while(rs1.next()) {
				tableName=rs1.getString(3);
				rs2=stm.executeQuery("select * from "+tableName+";");
				rsmt=rs2.getMetaData();
				columns=new HashMap<>();
				for(Integer i=1;i<=rsmt.getColumnCount();i++) {	
					//System.out.prIntegerln(rsmt.getColumnName(i)+" "+ rsmt.getColumnTypeName(i));
					columns.put(rsmt.getColumnName(i), rsmt.getColumnTypeName(i));
					}
				dbTables.put(tableName, columns);
			 	}
				} catch (SQLException e) {
		
			e.printStackTrace();
		}
		
		for(Entry<String, Map<String, String>> entry:dbTables.entrySet()) {
			//scSystem.out.println("----------------");
		    MapToClasses(entry.getKey(), entry.getValue());
		}	
	}
	
	
	private void MapToClasses(String className,Map<String ,String> columns) {
		
		try {
			File f= new File("src");
			//System.out.prIntegerln(f.getAbsolutePath());
			boolean isAlreadyExists=false;
			for(File file:f.listFiles()) {
				if(f.getName().equals("dataBaseClasses")) isAlreadyExists=true;
			}
			if(!isAlreadyExists) {
				new File(f.getAbsolutePath()+"/dataBaseClasses").mkdirs();
			}
			fw= new FileWriter(f.getAbsolutePath()+"/dataBaseClasses/"+className+".java");
			br= new BufferedWriter(fw);
			br.write("package dataBaseClasses;\npublic class "+className+" {\n \n");
			for(Entry <String,String> entry : columns.entrySet()){
			 //System.out.prIntegerln(entry.getKey()+"  "+entry.getValue());	
			
			if(entry.getValue().equalsIgnoreCase("varchar")) {
				
		    	br.write(" private  String "+entry.getKey()+";\n");
		    	br.write("public void set"+entry.getKey()+"(String "+entry.getKey()+"){\n this."+entry.getKey()+"="+entry.getKey()+";\n}");
				br.write("\npublic String get"+entry.getKey()+"(){\n return this."+entry.getKey()+";\n}");
		     }
		     else if(entry.getValue().equalsIgnoreCase("int")){
		    	 br.write("private Integer "+entry.getKey()+";\n");
				 br.write("public void set"+entry.getKey()+"(Integer "+entry.getKey()+"){\n this."+entry.getKey()+"="+entry.getKey()+";\n}");
				 br.write("\npublic Integer get"+entry.getKey()+"(){\n return this."+entry.getKey()+";\n}");
		     }else if(entry.getValue().equalsIgnoreCase("float")) {
		    	 br.write("private Float "+entry.getKey()+";\n");
				 br.write("public void set"+entry.getKey()+"(Float "+entry.getKey()+"){\n this."+entry.getKey()+"="+entry.getKey()+";\n}");
				 br.write("\npublic Float get"+entry.getKey()+"(){\n return this."+entry.getKey()+";\n}");
		     }
		    br.write("\n // property:"+entry.getKey()+"\n");
			}
			//end of for
			
			br.write("}");
			br.close();
			fw.close();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		 
	
	}
}
