import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Comparator;

import org.junit.Test;

import components.sortingmachine.SortingMachine;

/**
 * JUnit test fixture for {@code SortingMachine<String>}'s constructor and
 * kernel methods.
 *
 * @author Kaleb Agbobli
 * @author Andrew Pavel
 *
 */
public abstract class SortingMachineTest {

    /**
     * Invokes the appropriate {@code SortingMachine} constructor for the
     * implementation under test and returns the result.
     *
     * @param order
     *            the {@code Comparator} defining the order for {@code String}
     * @return the new {@code SortingMachine}
     * @requires IS_TOTAL_PREORDER([relation computed by order.compare method])
     * @ensures constructorTest = (true, order, {})
     */
    protected abstract SortingMachine<String> constructorTest(Comparator<String> order);

    /**
     * Invokes the appropriate {@code SortingMachine} constructor for the
     * reference implementation and returns the result.
     *
     * @param order
     *            the {@code Comparator} defining the order for {@code String}
     * @return the new {@code SortingMachine}
     * @requires IS_TOTAL_PREORDER([relation computed by order.compare method])
     * @ensures constructorRef = (true, order, {})
     */
    protected abstract SortingMachine<String> constructorRef(Comparator<String> order);

    /**
     *
     * Creates and returns a {@code SortingMachine<String>} of the
     * implementation under test type with the given entries and mode.
     *
     * @param order
     *            the {@code Comparator} defining the order for {@code String}
     * @param insertionMode
     *            flag indicating the machine mode
     * @param args
     *            the entries for the {@code SortingMachine}
     * @return the constructed {@code SortingMachine}
     * @requires IS_TOTAL_PREORDER([relation computed by order.compare method])
     * @ensures <pre>
     * createFromArgsTest = (insertionMode, order, [multiset of entries in args])
     * </pre>
     */
    private SortingMachine<String> createFromArgsTest(Comparator<String> order,
            boolean insertionMode, String... args) {
        SortingMachine<String> sm = this.constructorTest(order);
        for (int i = 0; i < args.length; i++) {
            sm.add(args[i]);
        }
        if (!insertionMode) {
            sm.changeToExtractionMode();
        }
        return sm;
    }

    /**
     *
     * Creates and returns a {@code SortingMachine<String>} of the reference
     * implementation type with the given entries and mode.
     *
     * @param order
     *            the {@code Comparator} defining the order for {@code String}
     * @param insertionMode
     *            flag indicating the machine mode
     * @param args
     *            the entries for the {@code SortingMachine}
     * @return the constructed {@code SortingMachine}
     * @requires IS_TOTAL_PREORDER([relation computed by order.compare method])
     * @ensures <pre>
     * createFromArgsRef = (insertionMode, order, [multiset of entries in args])
     * </pre>
     */
    private SortingMachine<String> createFromArgsRef(Comparator<String> order,
            boolean insertionMode, String... args) {
        SortingMachine<String> sm = this.constructorRef(order);
        for (int i = 0; i < args.length; i++) {
            sm.add(args[i]);
        }
        if (!insertionMode) {
            sm.changeToExtractionMode();
        }
        return sm;
    }

    /**
     * Comparator<String> implementation to be used in all test cases. Compare
     * {@code String}s in lexicographic order.
     */
    private static final class StringLT implements Comparator<String> {

        @Override
        public int compare(String s1, String s2) {
            return s1.compareToIgnoreCase(s2);
        }

    }

    /**
     * Comparator instance to be used in all test cases.
     */
    private static final StringLT ORDER = new StringLT();

    /*
     * Sample test cases.
     */

    @Test
    public final void testConstructor() {
        SortingMachine<String> m = this.constructorTest(ORDER);
        SortingMachine<String> mExpected = this.constructorRef(ORDER);
        assertEquals(mExpected, m);
    }

    @Test
    public final void testAddEmpty() {
        SortingMachine<String> m = this.createFromArgsTest(ORDER, true);
        SortingMachine<String> mExpected = this.createFromArgsRef(ORDER, true, "green");
        m.add("green");
        assertEquals(mExpected, m);
    }

    /*
     * Added test cases.
     */

    @Test
    public final void testAddSeveralEntries() {
        SortingMachine<String> m = this.createFromArgsTest(ORDER, true);
        SortingMachine<String> mExpected = this.createFromArgsRef(ORDER, true, "pear",
                "apple", "orange", "banana");
        m.add("pear");
        m.add("apple");
        m.add("orange");
        m.add("banana");
        assertEquals(mExpected, m);
        assertEquals(4, m.size());
        assertTrue(m.isInInsertionMode());
    }

    @Test
    public final void testAddDuplicateEntries() {
        SortingMachine<String> m = this.createFromArgsTest(ORDER, true, "apple", "apple",
                "banana");
        SortingMachine<String> mExpected = this.createFromArgsRef(ORDER, true, "apple",
                "apple", "banana");
        assertEquals(mExpected, m);
        assertEquals(3, m.size());
    }

    @Test
    public final void testChangeToExtractionModeEmpty() {
        SortingMachine<String> m = this.createFromArgsTest(ORDER, true);
        m.changeToExtractionMode();
        assertFalse(m.isInInsertionMode());
        assertEquals(0, m.size());
    }

    @Test
    public final void testChangeToExtractionModeOneEntry() {
        SortingMachine<String> m = this.createFromArgsTest(ORDER, true, "pear");
        m.changeToExtractionMode();
        assertFalse(m.isInInsertionMode());
        assertEquals(1, m.size());
        assertEquals("pear", m.removeFirst());
        assertEquals(0, m.size());
    }

    @Test
    public final void testChangeToExtractionModeMultipleEntries() {
        SortingMachine<String> m = this.createFromArgsTest(ORDER, true, "pear", "apple",
                "orange", "banana");
        m.changeToExtractionMode();
        assertFalse(m.isInInsertionMode());
        assertEquals(4, m.size());
        assertEquals("apple", m.removeFirst());
        assertEquals("banana", m.removeFirst());
        assertEquals("orange", m.removeFirst());
        assertEquals("pear", m.removeFirst());
        assertEquals(0, m.size());
    }

    @Test
    public final void testRemoveFirstDuplicates() {
        SortingMachine<String> m = this.createFromArgsTest(ORDER, false, "banana",
                "apple", "banana", "apple", "apple");
        assertEquals("apple", m.removeFirst());
        assertEquals("apple", m.removeFirst());
        assertEquals("apple", m.removeFirst());
        assertEquals("banana", m.removeFirst());
        assertEquals("banana", m.removeFirst());
        assertEquals(0, m.size());
    }

    @Test
    public final void testRemoveFirstReverseSortedEntries() {
        SortingMachine<String> m = this.createFromArgsTest(ORDER, false, "e", "d", "c",
                "b", "a");
        assertEquals("a", m.removeFirst());
        assertEquals("b", m.removeFirst());
        assertEquals("c", m.removeFirst());
        assertEquals("d", m.removeFirst());
        assertEquals("e", m.removeFirst());
        assertEquals(0, m.size());
    }

    @Test
    public final void testOrder() {
        SortingMachine<String> m = this.constructorTest(ORDER);
        assertSame(ORDER, m.order());
        m.add("zebra");
        assertSame(ORDER, m.order());
        m.changeToExtractionMode();
        assertSame(ORDER, m.order());
    }

    @Test
    public final void testSizeAsEntriesAreRemoved() {
        SortingMachine<String> m = this.createFromArgsTest(ORDER, false, "delta", "alpha",
                "charlie", "bravo");
        assertEquals(4, m.size());
        m.removeFirst();
        assertEquals(3, m.size());
        m.removeFirst();
        assertEquals(2, m.size());
        m.removeFirst();
        assertEquals(1, m.size());
        m.removeFirst();
        assertEquals(0, m.size());
    }

}
