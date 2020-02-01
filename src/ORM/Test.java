package ORM;

import dataBaseClasses.testjava;

public class Test {

	public static void main(String[] args) {
		ORMMetaData orm = new ORMMetaData();
		orm.getTables();
		DaoImple<testjava> d = new DaoImple<>();
		testjava t = new testjava();
		t.setfirstname("abdel");
		d.insert(t);

//		DaoImple<tabtest> d=new DaoImple<>();
//		adherent u=new adherent();u.setlogin("abdelaah");u.setnom("amon");
//		compte c= new compte();
//		tabtest tb = new tabtest();
//		tb.setcolonne1("abdel");
//		
//		 d.update(tb);
//		System.out.println("*****************************************");
//		for(tabtest i:d.find(tb)){
//			System.out.print(i.getcolonne1()+" ");
//			System.out.print(i.getcolonne2());
//			System.out.println();
//		}

	}

}
