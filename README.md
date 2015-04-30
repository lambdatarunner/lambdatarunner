# lambdatarunner
A Junit runner using lambdas to parameterize tests

A problem with creating parameterized tests in JUnit has been that the methods for doing so tended both to separate the data from the code, and lacked type safety, in that there was no compile-time check that the data provided matched the data the test expects. Lambdatarunner is a JUnit test runner that seeks to solve this problem by using lambdas to express the test code, and combining them directly with data. An example can show it best:

```java
@RunWith(LambdataRunner.class)
public class StringTest {
    @Test
    @DescribeAs("\"{0}\".substring({1}, {2})")
    public TestSpecs testSubString() {
        return specs((s, start, end, expected) -> assertEquals(expected, s.substring(start, end)),
            datum("hello", 0, 5, "hello"),
            datum("flexible", 1, 4, "lex"),
            datum("hello", 5, 5, ""));
    }
}
```

LambdataRunner is compatible with maven and eclipse, and likely others.

Presently, this project is in alpha status. Please share any thoughts you have about how the API could be improved!