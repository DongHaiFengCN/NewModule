package doaing.order.module;
/**
 * 项目名称：Order
 * 类描述：
 * 创建人：donghaifeng
 * 创建时间：2017/12/22 10:37
 * 修改人：donghaifeng
 * 修改时间：2017/12/22 10:37
 * 修改备注：
 */

public class DishesMessage {

    //菜品名称
    String name;

    //菜品口味
    String dishesTaste;

    //菜品实体
   // DishesC dishesC;

    //加减操作 true +  false -
    boolean operation;


    //多数量状态下总价
    float total;

    //多数量状态下
    float count;



    public String getDishKindId() {
        return dishKindId;
    }

    public void setDishKindId(String dishKindId) {
        this.dishKindId = dishKindId;
    }

    //设置菜类id
    String dishKindId;


    public boolean isSingle() {
        return single;
    }

    public void setSingle(boolean single) {
        this.single = single;
    }

    boolean single;



    public DishesMessage() {
    }


    public boolean isOperation() {
        return operation;
    }

    public void setOperation(boolean operation) {
        this.operation = operation;
    }

    public String getDishesTaste() {
        return dishesTaste;
    }

    public void setDishesTaste(String dishesTaste) {
        this.dishesTaste = dishesTaste;
    }

/*    public DishesC getDishesC() {
        return dishesC;
    }

    public void setDishesC(DishesC dishesC) {
        this.dishesC = dishesC;
    }*/


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getCount() {
        return single ? 1.0f : count;
    }

    public void setCount(float count) {
        this.count = count;
    }

  /*  public float getTotal() {

        return (total > dishesC.getPrice() ? total : dishesC.getPrice());
    }*/

    public void setTotal(float total) {
        this.total = total;
    }
}
