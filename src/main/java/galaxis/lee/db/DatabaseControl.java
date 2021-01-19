package galaxis.lee.db;

import java.util.List;

/**
 * @Author: Lee
 * @Date: Created in 9:29 2019/9/12
 *  操作数据库 增删改查
 */
public class DatabaseControl<E> implements DBControlImpl<E> {


    @Override
    public int add(E e) {
        return 0;
    }

    @Override
    public int delete(int id) {
        return 0;
    }

    @Override
    public int update(E e) {
        return 0;
    }

    @Override
    public List<E> select(int id) {
        return null;
    }
}
