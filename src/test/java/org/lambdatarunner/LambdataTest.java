package org.lambdatarunner;

import static org.junit.Assert.assertEquals;
import static org.lambdatarunner.Lambdata.datum;
import static org.lambdatarunner.Lambdata.specs;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Tests to be used to see how LambdataRunner interacts with various test runners.
 */
@RunWith(LambdataRunner.class)
public class LambdataTest {
    @Test
    @DescribeAs("my data is {0}")
    public TestSpecs creatingSingleParamTest() {
        return specs(
            i -> assertEquals((int) i,  i * 1),
            datum(1),
            datum(2),
            datum(3));
    }

    @Test
    @DescribeAs("my data is {0} and {1}")
    public TestSpecs creatingDoubleParamTest() {
        return specs(
            (a, b) -> assertEquals(a + b, b + a),
            datum(2, 7),
            datum(3, 23));
    }

    @Test
    @DescribeAs("does {0} = {1}")
    @Ignore
    public TestSpecs canFail() {
        return specs(
            (a, b) -> assertEquals(a, b),
            datum(1,1),
            datum(1,2),
            datum(2,2));
    }

    @Test
    public TestSpecs noDescription() {
        return specs(
            (a,b) -> assertEquals(a,b),
            datum(1,1),
            datum(2,2));
    }

    @Test
    public TestSpecs noData() {
        return specs((a,b) -> assertEquals(a,b));
    }

    @Test
    public TestSpecs complicatedTest() {
        return specs(
            (a, b) -> {
                int expected = a;
                int actual = b;
                assertEquals(expected, actual);
            },
            datum(1, 1),
            datum(2, 2)
            );
    }

    @Test
    @Ignore
    public TestSpecs badData() {
        throw new RuntimeException("crap");
    }

    @Test
    @DescribeAs("{0} + {1}")
    public TestSpecs testAddition() {
      return specs(
        (a, b, expected) -> {
          assertEquals((int) expected, a + b);
        },
        datum(1, 2, 3),
        datum(100, 11, 111),
        datum(28, 14, 42));
    }

    @Test
    public TestSpecs testLongParsing() {
      return specs(
        number -> { // the compiler can infer that number is of type long
          assertEquals(number, Long.valueOf(Long.toString(number)));
        },
        datum(1L),
        datum(100L),
        datum(1000L));
   }

    @Test
    @DescribeAs("for string ''{0}''")
    public TestSpecs testStringLength() {
        return specs(
            (string, length) -> assertEquals((int) length, string.length()),
            datum("", 0),
            datum("a", 1),
            datum("hello", 5));
    }
}
