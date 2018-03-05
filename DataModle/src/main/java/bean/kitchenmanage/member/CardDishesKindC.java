package bean.kitchenmanage.member;

import android.os.Parcel;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: CardDishesKind
 * @Description: 会员卡所关联的菜品品类
 * @author loongsun
 * @date 2014-7-29 上午1:06:02
 *
 */
public class CardDishesKindC{

    /**
     * 所属卡类id
     */
    private String   cardTypeCId;
    /**
     * 所关联菜品品类id
     */
    private String  dishKindCId;
    /**
     * 0,未选择；1、选择
     */
    private  int ischecked;
    /**
     * 所关联的菜品
     */
    private List<CardDishesC> cardDishesList;

    public CardDishesKindC()
    {
    }


    protected CardDishesKindC(Parcel in) {
        cardTypeCId = in.readString();
        dishKindCId = in.readString();
        ischecked = in.readInt();
    }



    public String getCardTypeCId() {
        return cardTypeCId;
    }

    public void setCardTypeCId(String cardTypeCId) {
        this.cardTypeCId = cardTypeCId;
    }

    public String getDishKindCId() {
        return dishKindCId;
    }

    public void setDishKindCId(String dishKindCId) {
        this.dishKindCId = dishKindCId;
    }

    public int getIschecked() {
        return ischecked;
    }

    public void setIschecked(int ischecked) {
        this.ischecked = ischecked;
    }

    public List<CardDishesC> getCardDishesList()
    {
        if(cardDishesList==null)
            cardDishesList=new ArrayList<>();
        return cardDishesList;
    }

    public void setCardDishesList(List<CardDishesC> cardDishesList) {
        this.cardDishesList = cardDishesList;
    }

    public void addCardDishes(CardDishesC obj)
    {
        if(null==cardDishesList)
            cardDishesList=new ArrayList<CardDishesC>();
        cardDishesList.add(obj);
    }


}
