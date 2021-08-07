package jvm;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ResourceUtils;

import java.io.*;

/**
 * 题目：自定义一个 Classloader，加载一个 Hello.xlass 文件，执行 hello 方法，此文件内容是一个 Hello.class 文件所有字节（x=255-x）处理后的文件
 * 说明：.xlass我重命名成了.class 没有为什么 看不大舒服 -.-
 *
 *
 * git
 * 展示内存参数关系图片：resources下的堆.png
 */
@Slf4j
public class MyClassLoad extends ClassLoader {

    public static void main(String[] args) throws Exception {
        Class<?> clazz = new MyClassLoad().findClass("Hello");
        Object o = clazz.newInstance();
        /*Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            System.out.print(method + ",");
        }
        System.out.println();*/
        clazz.getMethod("hello").invoke(o);
    }


    @SuppressWarnings("all")
    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        BufferedInputStream bis = null;
        ByteArrayOutputStream bos = null;
        try {
            File file = ResourceUtils.getFile("classpath:Hello.class");
            bos = new ByteArrayOutputStream((int) file.length());
            bis = new BufferedInputStream(new FileInputStream(file));
            int buf_size = 1024;
            byte[] buffer = new byte[buf_size];
            int len;
            while (-1 != (len = bis.read(buffer, 0, buf_size))) {
                bos.write(buffer, 0, len);
            }
            byte[] bytes = bos.toByteArray();
            //执行还原操作
            /*int a = 255;
            byte s = (byte) a;*/
            byte s = -1;
            int length = bytes.length;
            for (int i = 0; i < length; i++) {
                byte realByte = (byte) (s - bytes[i]);
                bytes[i] = realByte;
            }
            return defineClass(name, bytes, 0, length);
        } catch (Exception e) {
            log.error("ERR = {}", e.getMessage());
        } finally {
            try {
                bis.close();
                bos.close();
            } catch (Exception e) {
                log.error("FINALLY BLOCK ERR = {}", e.getMessage());
            }
        }
        return null;
    }

}
