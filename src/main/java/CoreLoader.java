import core.BeanCore;
import core.ClassCore;
import core.DependInjectionCore;
import core.RequestMappingCore;
import util.ClassUtil;

/**
 * 加载相应的Core类
 */
public final class CoreLoader {

    public static void init(){
        Class<?>[] classList ={
                ClassCore.class,
                BeanCore.class,
                DependInjectionCore.class,
                RequestMappingCore.class
        };

        for(Class<?> clazz : classList){
            ClassUtil.loadClass(clazz.getName(), true);
        }
    }

}
