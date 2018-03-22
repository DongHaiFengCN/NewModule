package bean.kitchenmanage.member;

/**
 * @ClassName: CardDishes
 * @Description: 会员卡所关联的菜品
 * @author loongsun
 * @date 2014-7-29 上午1:06:02
 *
 */
public class CardDishesC
{

    /**
     * 关联的菜品品类
     */
    private String  cardDishesKindId;

    /**
     * 0,未选择；1、选择
     */
    private int ischecked;

    /**
     * 菜品的id
     */
   private String dishesId;

    public CardDishesC()
    {

    }





    public String getCardDishesKindId() {
        return cardDishesKindId;
    }

    public void setCardDishesKindId(String cardDishesKindId) {
        this.cardDishesKindId = cardDishesKindId;
    }

    public int getIschecked() {
        return ischecked;
    }

    public void setIschecked(int ischecked) {
        this.ischecked = ischecked;
    }

    public String getDishesId() {
        return dishesId;
    }

    public void setDishesId(String dishesId) {
        this.dishesId = dishesId;
    }
}
