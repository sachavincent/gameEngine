package tests;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import util.math.Maths;

class MathsTest {

    @org.junit.jupiter.api.Test
    void testEmptyArray() {
        boolean[] test = new boolean[0];

        boolean[] testEnd = Maths.shiftArray(test);

        assertEquals(testEnd.length, test.length);
    }

    @org.junit.jupiter.api.Test
    void testArrayLength1() {
        boolean[] test = new boolean[]{true};

        boolean[] testEnd = Maths.shiftArray(test);

        assertEquals(testEnd.length, test.length);
        assertTrue(testEnd[0]);
    }

    @org.junit.jupiter.api.Test
    void testArrayLength2() {
        boolean[] test = new boolean[]{true, true};

        boolean[] testEnd = Maths.shiftArray(test);

        assertEquals(testEnd.length, test.length);
        assertTrue(testEnd[0]);
        assertTrue(testEnd[1]);
    }

    @org.junit.jupiter.api.Test
    void testArrayLength2_2() {
        boolean[] test = new boolean[]{false, true};

        boolean[] testEnd = Maths.shiftArray(test);

        assertEquals(testEnd.length, test.length);
        assertTrue(testEnd[0]);
        assertFalse(testEnd[1]);
    }

    @org.junit.jupiter.api.Test
    void testArrayLength2_3() {
        boolean[] test = new boolean[]{true, false};

        boolean[] testEnd = Maths.shiftArray(test);

        assertEquals(testEnd.length, test.length);
        assertFalse(testEnd[0]);
        assertTrue(testEnd[1]);
    }

    @org.junit.jupiter.api.Test
    void testArrayLength3() {
        boolean[] test = new boolean[]{true, true, false};

        boolean[] testEnd = Maths.shiftArray(test);

        assertEquals(testEnd.length, test.length);
        assertArrayEquals(testEnd, new boolean[]{false, true, true});
    }

}