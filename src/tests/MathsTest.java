package tests;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import util.math.Maths;

class MathsTest {

    @org.junit.jupiter.api.Test
    void testEmptyArray() {
        Boolean[] test = new Boolean[0];

        Boolean[] testEnd = Maths.shiftArray(test);

        assertEquals(testEnd.length, test.length);
    }

    @org.junit.jupiter.api.Test
    void testArrayLength1() {
        Boolean[] test = new Boolean[]{true};

        Boolean[] testEnd = Maths.shiftArray(test);

        assertEquals(testEnd.length, test.length);
        assertTrue(testEnd[0]);
    }

    @org.junit.jupiter.api.Test
    void testArrayLength2() {
        Boolean[] test = new Boolean[]{true, true};

        Boolean[] testEnd = Maths.shiftArray(test);

        assertEquals(testEnd.length, test.length);
        assertTrue(testEnd[0]);
        assertTrue(testEnd[1]);
    }

    @org.junit.jupiter.api.Test
    void testArrayLength2_2() {
        Boolean[] test = new Boolean[]{false, true};

        Boolean[] testEnd = Maths.shiftArray(test);

        assertEquals(testEnd.length, test.length);
        assertTrue(testEnd[0]);
        assertFalse(testEnd[1]);
    }

    @org.junit.jupiter.api.Test
    void testArrayLength2_3() {
        Boolean[] test = new Boolean[]{true, false};

        Boolean[] testEnd = Maths.shiftArray(test);

        assertEquals(testEnd.length, test.length);
        assertFalse(testEnd[0]);
        assertTrue(testEnd[1]);
    }

    @org.junit.jupiter.api.Test
    void testArrayLength3() {
        Boolean[] test = new Boolean[]{true, true, false};

        Boolean[] testEnd = Maths.shiftArray(test);

        assertEquals(testEnd.length, test.length);
        assertArrayEquals(testEnd, new Boolean[]{false, true, true});
    }

}