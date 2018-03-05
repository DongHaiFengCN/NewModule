package bean.kitchenmanage.depot;

/**
 * 食材类型类文件
 *
 * Created by loongsun on 17/1/8.
 *
 * email: 125736964@qq.com
 */

public class MaterialKind
{
    /**
     * 公司唯一身份id,用于数据同步,做为唯一管道符
     */
    private String channelId;
    private String kindName;

    public MaterialKind(String kindName) {
        this.kindName = kindName;
    }

    public String getKindName() {
        return kindName;
    }

    public void setKindName(String kindName) {
        this.kindName = kindName;
    }
}
