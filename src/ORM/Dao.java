package ORM;

import java.util.List;

public interface Dao<T> {
	public int insert(T o);
	public int update(T o);
	public int delete(T o);
	public List<T> find(T o);
	public List<T> findAll(T o,String ...String);

}
