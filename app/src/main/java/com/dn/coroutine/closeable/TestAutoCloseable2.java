package com.dn.coroutine.closeable;

/**
 * @Author zhangshuai
 * @Date 2022/2/9
 * @Emial zhangshuai@dushu365.com
 * @Description 方式一、打开.idea文件夹中的gradle.xml文件，新增标签：<option name="delegatedBuild" value="false" />
 */
public class TestAutoCloseable2 {

    public static void main(String[] args) {
        try (TestAutoCloseable.MyResource mr = new TestAutoCloseable.MyResource()) {
            mr.doSomeThing();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println("TestAutoCloseable2->main.finally....");
        }
    }
}
