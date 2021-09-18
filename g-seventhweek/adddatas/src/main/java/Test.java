import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Random;
import java.util.UUID;

/**
 * 以下 method00-method05 分别比较了不同情况下插入100w条数据的耗时情况
 * 测试环境：Mysql数据库 InnoDB存储引擎 t_test表
 *          Win10家庭版  CPU（i7-10510U 1.8GHz 2.3GHz） RAM（16G）
 *          建表语句：
 *          CREATE TABLE `t_test`  (
 *              `id` int(11) NOT NULL AUTO_INCREMENT,
 *              `name` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
 *              `age` int(11) NULL DEFAULT NULL,
 *              PRIMARY KEY (`id`) USING BTREE
 *          ) ENGINE = InnoDB AUTO_INCREMENT = 3000023 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;
 *
 *  测试结果：
 *      1.PreparedStatement循环插入，每次自动提交事务。
 *          耗时：659568毫秒 659秒  11分钟
 *      2.PreparedStatement循环插入，关闭自动提交事务，仅提交一次事务。
 *          耗时：82919毫秒 82秒  1.37分钟
 *      3.PreparedStatement批量插入，事务自动提交。
 *          耗时：6260毫秒 6秒
 *      4.PreparedStatement批量插入，关闭事务自动提交，仅提交一次事务，本质和第3种一样。
 *          耗时：6260毫秒 6秒
 *      5.Statement循环插入，每次自动提交事务。
 *          耗时：625121毫秒 625秒  10.4分钟
 *      6.Statement批量插入，关闭事务自动提交，仅提交一次事务。
 *          耗时：52833毫秒  52秒
 *
 *  总结：
 *      1.事务多次提交会消耗大量时间，因为会把数据从内存持久化到磁盘，涉及磁盘IO。所以[关闭事务自动提交的循环插入]比[事务自动提交的循环插入]快得多
 *      2.SQL语句多次解析会消耗大量时间，因为多次解析会增加数据库连接的IO开销，此外日志量（binlog和InnoDB的相关日志）也会增加，会增加日志刷盘的数据量和频率。所以[批量插入]比[关闭事务自动提交的循环插入]快得多
 *      3.PreparedStatement效率高于Statement，因为前者会进行预编译。推荐使用PreparedStatement，代码可读性好，且防SQL注入。
 *      4.数据按照（主键）顺序插入更快，因为不按照顺序插入，会存在页分裂和合并，导致更多的计算和磁盘IO。
 *      TODO 疑问：PreparedStatement效率高于Statement，为什么测试结果的5比1要快一点呢。是偶然还是我哪里操作不对吗？希望助教老师解答，谢谢~
 *      TODO      此外上面1、2、3、4的分析是否正确，以及是否有更完善的分析，同样希望助教老师点评指正，谢谢~
 */
public class Test {

    /**
     * 累计插入数据条数：100w
     */
    private static final int total = 1000000;


    public static void main(String[] args) throws Exception {
        /**
         * 数据库连接
         * 注意：需设置 rewriteBatchedStatements=true 才会开启批量插入操作 否则和单次插入没区别
         */
        Class.forName("com.mysql.cj.jdbc.Driver");
        String url = "jdbc:mysql://localhost:3306/czj?useUnicode=true&characterEncoding=utf8&rewriteBatchedStatements=true&serverTimezone=GMT%2B8";
        String username = "root";
        String pwd = "root";
        Connection connection = DriverManager.getConnection(url, username, pwd);

        //准备100w条数据
        connection.setAutoCommit(false);
        TestData[] data = new TestData[total];
        for (int i = 0; i < total; i++) {
            data[i] = new TestData(UUID.randomUUID().toString().substring(0, 16), 10 + new Random().nextInt(30));
        }

        //执行批量插入
        method05(connection, data);

        //关闭连接
        connection.close();
    }

    /**
     * PreparedStatement循环单次插入 即循环执行preparedStatement.executeUpdate(); 事务自动提交
     * 659568毫秒 659秒  11分钟
     */
    public static void method00(Connection connection, TestData[] data) throws Exception {
        String sql = "INSERT INTO t_test (name, age) values(?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);

        System.out.println("执行批量插入操作...");
        long start = System.currentTimeMillis();
        for (int i = 0; i < total; i++) {
            TestData obj = data[i];
            preparedStatement.setString(1, obj.getName());
            preparedStatement.setInt(2, obj.getAge());
            preparedStatement.executeUpdate();
        }
        long end = System.currentTimeMillis();
        System.out.println("耗时=" + (end - start) + "毫秒");
    }

    /**
     * PreparedStatement循环单次插入 即循环执行preparedStatement.executeUpdate(); 事务统一提交
     * 82919毫秒 82秒  1.37分钟
     */
    public static void method01(Connection connection, TestData[] data) throws Exception {
        //关闭事务自动提交
        connection.setAutoCommit(false);
        String sql = "INSERT INTO t_test (name, age) values(?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);

        System.out.println("执行批量插入操作...");
        long start = System.currentTimeMillis();
        //方式一执行批量插入
        for (int i = 0; i < total; i++) {
            TestData obj = data[i];
            preparedStatement.setString(1, obj.getName());
            preparedStatement.setInt(2, obj.getAge());
            preparedStatement.executeUpdate();
        }

        //提交事务
        connection.commit();
        long end = System.currentTimeMillis();
        System.out.println("耗时=" + (end - start) + "毫秒");
    }

    /**
     * PreparedStatement批量插入 即 preparedStatement.addBatch() 然后 preparedStatement.executeBatch(); 事务自动提交
     * 6260毫秒 6秒
     */
    public static void method02(Connection connection, TestData[] data) throws Exception {
        String sql = "INSERT INTO t_test (name, age) values(?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);

        System.out.println("执行批量插入操作...");
        long start = System.currentTimeMillis();
        //方式一执行批量插入
        for (int i = 0; i < total; i++) {
            TestData obj = data[i];
            preparedStatement.setString(1, obj.getName());
            preparedStatement.setInt(2, obj.getAge());
            preparedStatement.addBatch();
        }
        preparedStatement.executeBatch();

        long end = System.currentTimeMillis();
        System.out.println("耗时=" + (end - start) + "毫秒");
    }

    /**
     * PreparedStatement批量插入 即 preparedStatement.addBatch() 然后 preparedStatement.executeBatch(); 事务非自动提交
     * 6112毫秒 6秒
     */
    public static void method03(Connection connection, TestData[] data) throws Exception {
        //关闭事务自动提交
        connection.setAutoCommit(false);
        String sql = "INSERT INTO t_test (name, age) values(?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);

        System.out.println("执行批量插入操作...");
        long start = System.currentTimeMillis();
        //方式一执行批量插入
        for (int i = 0; i < total; i++) {
            TestData obj = data[i];
            preparedStatement.setString(1, obj.getName());
            preparedStatement.setInt(2, obj.getAge());
            preparedStatement.addBatch();
        }
        preparedStatement.executeBatch();

        //提交事务
        connection.commit();

        long end = System.currentTimeMillis();
        System.out.println("耗时=" + (end - start) + "毫秒");
    }

    /**
     * Statement循环单次插入 即循环执行preparedStatement.executeUpdate(); 事务自动提交
     * 625121毫秒 625秒  10.4分钟
     */
    public static void method04(Connection connection, TestData[] data) throws Exception {
        Statement statement = connection.createStatement();

        System.out.println("执行批量插入操作...");
        long start = System.currentTimeMillis();
        for (int i = 0; i < total; i++) {
            Integer age = data[i].getAge();
            String name = data[i].getName();
            String sql = "INSERT INTO t_test (name, age) values(" + "\'" + name + "\'" + ',' + age + ')';
            statement.executeUpdate(sql);
        }
        long end = System.currentTimeMillis();
        System.out.println("耗时=" + (end - start) + "毫秒");
    }


    /**
     * Statement批量插入 即 statement.addBatch(sql) 然后 statement.executeBatch(); 事务统一提交
     * 52833毫秒  52秒
     */
    public static void method05(Connection connection, TestData[] data) throws Exception {
        //关闭事务自动提交
        connection.setAutoCommit(false);
        Statement statement = connection.createStatement();

        System.out.println("执行批量插入操作...");
        long start = System.currentTimeMillis();
        for (int i = 0; i < total; i++) {
            Integer age = data[i].getAge();
            String name = data[i].getName();
            String sql = "INSERT INTO t_test (name, age) values(" + "\'" + name + "\'" + ',' + age + ')';
            statement.addBatch(sql);
        }
        statement.executeBatch();
        //提交事务
        connection.commit();

        long end = System.currentTimeMillis();
        System.out.println("耗时=" + (end - start) + "毫秒");
    }

}
