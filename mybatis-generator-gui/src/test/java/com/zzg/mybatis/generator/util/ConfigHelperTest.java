package com.zzg.mybatis.generator.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * Created by zouzhigang on 2016/9/18.
 */
public class ConfigHelperTest {

    @Test
    public void testFindConnectorLibPath_Oracle() {
        String path = ConfigHelper.findConnectorLibPath("Oracle");
        Assertions.assertTrue(path.contains("ojdbc"));
    }

    @Test
    public void testFindConnectorLibPath_Mysql() {
        String path = ConfigHelper.findConnectorLibPath("MySQL");
        Assertions.assertTrue(path.contains("mysql-connector"));
    }

    @Test
    public void testFindConnectorLibPath_PostgreSQL() {
        String path = ConfigHelper.findConnectorLibPath("PostgreSQL");
        Assertions.assertTrue(path.contains("postgresql"));
    }

    @Test
    public void testGetAllJDBCDriverJarPaths() {
        List<String> jarFilePaths = ConfigHelper.getAllJDBCDriverJarPaths();
        Assertions.assertTrue(jarFilePaths != null && jarFilePaths.size() > 0);
    }
}
