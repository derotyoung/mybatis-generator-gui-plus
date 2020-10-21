package com.zzg.mybatis.generator.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Created by Owen on 6/18/16.
 */
public class StringUtilTest {

    @Test
    public void testDbStringToCamelStyle() {
        String result = MyStringUtils.dbStringToCamelStyle("person_address");
        Assertions.assertEquals("PersonAddress", result);
    }

    @Test
    public void testDbStringToCamelStyle_case2() {
        String result = MyStringUtils.dbStringToCamelStyle("person_address_name");
        Assertions.assertEquals("PersonAddressName", result);
    }

    @Test
    public void testDbStringToCamelStyle_case3() {
        String result = MyStringUtils.dbStringToCamelStyle("person_db_name");
        Assertions.assertEquals("PersonDbName", result);
    }

    @Test
    public void testDbStringToCamelStyle_case4() {
        String result = MyStringUtils.dbStringToCamelStyle("person_jobs_");
        Assertions.assertEquals("PersonJobs", result);
    }

    @Test
    public void testDbStringToCamelStyle_case5() {
        String result = MyStringUtils.dbStringToCamelStyle("a");
        Assertions.assertEquals("A", result);
    }

}
