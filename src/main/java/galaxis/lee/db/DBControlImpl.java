package galaxis.lee.db;

import java.util.List;

/**
 * @Author: Lee
 * @Date: Created in 9:30 2019/9/12
 */
public interface DBControlImpl<E> {

    int add(E e);

    int delete(int id);

    int update(E e);

    List<E> select(int id);

}
