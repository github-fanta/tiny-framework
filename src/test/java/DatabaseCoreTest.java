import core.DatabaseCore;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by liq on 2018/5/7.
 */
public class DatabaseCoreTest {
    @Test
    public void getEntityTest(){
        String sql = "select name, contact, telephone, email, remark from customer where id = 2";
        Customer user  = DatabaseCore.getEntity(Customer.class, sql);
        Assert.assertEquals("custome", user.getName());
    }

    @Test
    public void getEntityListTest(){
        List<Customer> users = new ArrayList<Customer>();
        String sql = "select name, contact, telephone, email, remark from customer";
        users  = DatabaseCore.getListEntity(Customer.class, sql);
        Assert.assertEquals("customer3", users.get(1).getName());
    }

    @Test
    public void insertEntity(){
        HashMap<String, Object> map = new HashMap<String, Object>();
        //map.put("name", "3");
        map.put("name", "张三");
        //map.put("contact", "Rose");
        //map.put("telephone", "123");
        map.put("email", "sss@gmail");
        map.put("remark", "这是张三");
        DatabaseCore.insertEntity(Customer.class, map);
    }

    @Test
    public void updateEntityTest(){
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("name", "李四");
        //map.put("contact", "Rose");
        //map.put("telephone", "123");
        map.put("email", "rrr@gmail");
        map.put("remark", "这是张三");
        DatabaseCore.updateEntity(Customer.class,8, map);
    }

    @Test
    public void deleteEntityTest(){
        DatabaseCore.deleteEntity(Customer.class, 9);
    }
}
