package model;

/**
 * 返回json数据对象
 * Created by liq on 2018/5/9.
 */
public class Json {

    /**
     * 模型数据， 框架会将其写入HttpServletResponse对象中，直接输出至浏览器。
     */
    private Object model;

    public Json(Object model) {
        this.model = model;
    }

    public Object getModel() {
        return model;
    }
}
