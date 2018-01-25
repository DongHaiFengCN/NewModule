package doaing.order.module;

/**
 * 项目名称：Order
 * 类描述：
 * 创建人：donghaifeng
 * 创建时间：2017/9/8 14:21
 * 修改人：donghaifeng
 * 修改时间：2017/9/8 14:21
 * 修改备注：
 */

public enum DatabaseSource {

    CouchBase("CouchBase"),
    MySQL("mysql"),
    SQLite("SQLite");


    private String name;

    DatabaseSource(String name) {

        this.name = name;
    }
    @Override
    public String toString() {

        return  name ;
    }

}
