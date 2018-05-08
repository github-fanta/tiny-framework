import org.junit.Assert;
import org.junit.Test;
import util.StringUtil;

/**
 * Created by liq on 2018/5/7.
 */
public class StringUtilTest{

    @Test
    public void isEmptyTest(){
        String s = "   ";
        boolean isEmpty = StringUtil.isEmpty(s);
        Assert.assertEquals(true, isEmpty);
    }
}
