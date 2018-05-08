package model;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by liq on 2018/5/8.
 */
public class ModelAndView {
    /**
     * 视图路径
     */
    private String viewPath;
    /**
     * 模型数据
     */
    private Map<String, Object> model = new HashMap<String, Object>();

    public ModelAndView(String viewPath, Map<String, Object> model) {
        this.viewPath = viewPath;
        this.model = model;
    }

    public String getViewPath() {
        return viewPath;
    }

    public Map<String, Object> getModel() {
        return model;
    }

    public void setModel(String key, Object value) {
        this.model.put(key, value);
    }
}
