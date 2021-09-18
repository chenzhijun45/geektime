/**
 * 模拟插入的数据
 */
public class TestData {

    private String name;
    private Integer age;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public TestData(String name, Integer age) {
        this.name = name;
        this.age = age;
    }

}
