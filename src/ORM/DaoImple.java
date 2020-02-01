package ORM;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class DaoImple<T> implements Dao<T> {
	Connection con;
	PreparedStatement pstm;
	Statement stm;

	public DaoImple() {
		con = SinglTonConnection.getConnection();

	}

	@Override
	public int insert(T o) {
		// System.out.println(o.toString());
		Class cls;
		int k = 0;
		Map<String, String> feilds = new HashMap<>();
		try {
			cls = Class.forName(o.getClass().getName());
			Field[] f = cls.getDeclaredFields();
			Method[] m = cls.getMethods();
			System.out.println(cls.getSimpleName());
			String query = "insert into " + cls.getSimpleName();

			for (Field fld : f) {
				String[] t = fld.getName().split("\\.");
				feilds.put(fld.getName() + ";" + fld.getType(), "get" + t[t.length - 1]);
			}
			String args = "(";
			String vals = "values(";
			int i = 0;
			for (Entry ele : feilds.entrySet()) {
				Method meth = cls.getDeclaredMethod((String) ele.getValue());
				if (i < feilds.size() - 1) {
					args = args + "" + ele.getKey().toString().split(";")[0] + ",";
					if (ele.getKey().toString().contains("String")) {
						vals = vals + "'" + meth.invoke(o) + "',";
					} else
						vals = vals + "" + meth.invoke(o) + ",";
					i++;
				} else {
					args = args + "" + ele.getKey().toString().split(";")[0] + ")";
					if (ele.getKey().toString().contains("String")) {
						vals = vals + "'" + meth.invoke(o) + "')";
					} else
						vals = vals + "" + meth.invoke(o) + ")";
				}

			}
			stm = con.createStatement();
			k = stm.executeUpdate(query + args + vals + ";");

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return k;
	}

	@Override
	public int update(T o) {

		String qry = "SELECT k.column_name FROM information_schema.table_constraints t JOIN information_schema.key_column_usage k USING(constraint_name,table_schema,table_name) WHERE t.constraint_type='PRIMARY KEY' AND t.table_schema='tpjava' AND t.table_name="
				+ "'" + o.getClass().getSimpleName() + "';";

		int k = 0;
		Map<String, String> feilds = new HashMap<>();
		String query = null;
		String vals = "";
		try {
			Class cls = null;
			stm = con.createStatement();
			ResultSet rs = stm.executeQuery(qry);
			// System.out.println(qry);
			String primaryKey = "";

			String p = "";
			if (rs.next()) {
				primaryKey = rs.getString(1);

				cls = Class.forName(o.getClass().getName());
				Field[] f = cls.getDeclaredFields();
				Method[] m = cls.getMethods();
				/// System.out.println(cls.getSimpleName());
				query = "update " + cls.getSimpleName() + " set ";

				for (Field fld : f) {
					String[] t = fld.getName().split("\\.");
					feilds.put(fld.getName() + ";" + fld.getType(), "get" + t[t.length - 1]);
				}

				for (Entry ele : feilds.entrySet()) {
					Method meth = cls.getDeclaredMethod((String) ele.getValue());
					if (!ele.getKey().toString().contains(primaryKey)) {

						if (ele.getKey().toString().contains("String"))
							vals += ele.getKey().toString().split(";")[0] + "='" + meth.invoke(o) + "',";
						else
							vals += ele.getKey().toString().split(";")[0] + "=" + meth.invoke(o) + ",";
						// System.out.println("ljj");
					} else
						p = ele.getKey().toString();

				}

			}
			String q;
			String[] mp = primaryKey.split("\\.");

			// System.out.println(mp[mp.length-1]+"");
			if (p.contains("String")) {
				q = vals.substring(0, vals.length() - 1) + " where " + primaryKey + "='"
						+ cls.getDeclaredMethod("get" + mp[mp.length - 1]).invoke(o) + "'";
			} else {
				q = vals.substring(0, vals.length() - 1) + " where " + primaryKey + "="
						+ cls.getDeclaredMethod("get" + mp[mp.length - 1]).invoke(o) + "";
			}
			// System.out.println(q);
			stm = con.createStatement();
			k = stm.executeUpdate(query + q + ";");
			// System.out.println(query+q+";");

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return k;
	}

	@Override
	public int delete(T o) {
		try {
			Class cls = Class.forName(o.getClass().getName());
			String qry = "SELECT k.column_name FROM information_schema.table_constraints t JOIN information_schema.key_column_usage k USING(constraint_name,table_schema,table_name) WHERE t.constraint_type='PRIMARY KEY' AND t.table_schema='tpjava' AND t.table_name="
					+ "'" + o.getClass().getSimpleName() + "';";

			stm = con.createStatement();
			ResultSet rs = stm.executeQuery(qry);
			// System.out.println(qry);
			String primaryKey = "";
			String p = "";

			if (rs.next()) {
				primaryKey = rs.getString(1);
				String[] mp = primaryKey.split("\\.");

				String q = "delete from " + o.getClass().getSimpleName() + " where " + primaryKey + "='"
						+ cls.getDeclaredMethod("get" + mp[mp.length - 1]).invoke(o) + "';";
				// sc System.out.println(q);
				int i = stm.executeUpdate(q);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	@Override
	public List<T> find(T o) {
		String qry = "select * from " + o.getClass().getSimpleName() + ";";
		List<T> list = new ArrayList<>();
		Map<String, String> feilds = new HashMap<>();
		try {
			Class cls = Class.forName(o.getClass().getName());
			Field[] fld = cls.getDeclaredFields();
			for (Field f : fld) {
				String[] t = f.getName().split("\\.");

				feilds.put(f.getName() + ";" + f.getType(), "set" + t[t.length - 1]);
			}
			stm = con.createStatement();
			ResultSet rs = stm.executeQuery(qry);
			while (rs.next()) {
				Object obj = cls.newInstance();
				int i = 1;
				for (Entry e : feilds.entrySet()) {
					// System.out.println(e.getValue().toString());
					if (e.getKey().toString().contains("String"))
						cls.getDeclaredMethod(e.getValue().toString(), new String().getClass()).invoke(obj,
								rs.getString(i));
					else if (e.getKey().toString().contains("int"))
						cls.getDeclaredMethod(e.getValue().toString(), new Integer(0).getClass()).invoke(obj,
								rs.getString(i));
					else if (e.getKey().toString().contains("float"))
						cls.getDeclaredMethod(e.getValue().toString(), new Float(0.0).getClass()).invoke(obj,
								rs.getString(i));
					i++;
				}
				list.add((T) obj);

			}
		} catch (Exception e) {

			e.printStackTrace();
		}
		return list;
	}

	@Override
	public List<T> findAll(T o, String... clause) {
		String whereClause = "where ";
		for (int i = 0; i < clause.length; i++) {
			if (i < clause.length - 1)
				whereClause = whereClause + clause[i] + "and";
			else
				whereClause = whereClause + clause[i] + ";";

		}
		String qry = "select * from " + o.getClass().getSimpleName() + whereClause + ";";
		List<T> list = new ArrayList<>();
		Map<String, String> feilds = new HashMap<>();
		try {
			Class cls = Class.forName(o.getClass().getName());
			Field[] fld = cls.getDeclaredFields();
			for (Field f : fld) {
				String[] t = f.getName().split("\\.");
				feilds.put(f.getName() + ";" + f.getType(), "set" + t[t.length - 1]);
			}
			stm = con.createStatement();
			ResultSet rs = stm.executeQuery(qry);
			while (rs.next()) {
				Object obj = cls.newInstance();
				int i = 1;
				for (Entry e : feilds.entrySet()) {
					System.out.println(e.getValue().toString());
					if (e.getKey().toString().contains("String"))
						cls.getDeclaredMethod(e.getValue().toString(), new String().getClass()).invoke(obj,
								rs.getString(i));
					else if (e.getKey().toString().contains("int"))
						cls.getDeclaredMethod(e.getValue().toString(), new Integer(0).getClass()).invoke(obj,
								rs.getString(i));
					else if (e.getKey().toString().contains("float"))
						cls.getDeclaredMethod(e.getValue().toString(), new Float(0.0).getClass()).invoke(obj,
								rs.getString(i));
					i++;
				}
				list.add((T) obj);

			}
		} catch (Exception e) {

			e.printStackTrace();
		}
		return list;
	}

}
