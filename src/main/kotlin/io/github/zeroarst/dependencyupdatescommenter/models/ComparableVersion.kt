package io.github.zeroarst.dependencyupdatescommenter.models

import io.github.zeroarst.dependencyupdatescommenter.models.ComparableVersion.Item.Companion.BIGINTEGER_ITEM
import io.github.zeroarst.dependencyupdatescommenter.models.ComparableVersion.Item.Companion.INT_ITEM
import io.github.zeroarst.dependencyupdatescommenter.models.ComparableVersion.Item.Companion.LIST_ITEM
import io.github.zeroarst.dependencyupdatescommenter.models.ComparableVersion.Item.Companion.LONG_ITEM
import io.github.zeroarst.dependencyupdatescommenter.models.ComparableVersion.Item.Companion.STRING_ITEM
import java.lang.IllegalStateException
import java.lang.StringBuilder
import java.math.BigInteger
import java.util.*
import java.util.ArrayDeque

/**
 *
 *
 * Generic implementation of version comparison.
 *
 *
 * Features:
 *
 *  * mixing of '`-`' (hyphen) and '`.`' (dot) separators,
 *  * transition between characters and digits also constitutes a separator:
 * `1.0alpha1 => [1, 0, alpha, 1]`
 *  * unlimited number of version components,
 *  * version components in the text can be digits or strings,
 *  *
 * String qualifiers are ordered lexically (case insensitive), with the following exceptions:
 *
 *  *  'snapshot' &lt; '' &lt; 'sp'
 *
 * and alias -&gt; replacement (all case insensitive):
 *
 *  *  'a' -&gt; 'alpha'
 *  *  'b' -&gt; 'beta'
 *  *  'm' -&gt; 'milestone'
 *  *  'cr' -&gt; 'rc'
 *  *  'final' -&gt; ''
 *  *  'final' -&gt; ''
 *  *  'final' -&gt; ''
 *
 *
 *  *
 * Following semver rules is encouraged, and some qualifiers are discouraged (no matter the case):
 *
 *  *  The usage of 'CR' qualifier is discouraged. Use 'RC' instead.
 *  *  The usage of 'final', 'ga', and 'release' qualifiers is discouraged. Use no qualifier instead.
 *  *  The usage of 'SP' qualifier is discouraged. Increment the patch version instead.
 *
 * For other qualifiers, natural ordering is used (case insensitive):
 *
 *  *  alpha = a &lt; beta = b &lt; milestone = m &lt; rc = cr &lt; snapshot &lt; '' = final = ga = release &lt; sp
 *
 *
 *  * a hyphen usually precedes a qualifier, and is always less important than digits/number, for example
 * 1.0.RC2 &lt; 1.0-RC3 &lt; 1.0.1 ; but prefer '1.0.0-RC1' over '1.0.0.RC1'
 *
 *
 * @see [Version Order Specification](https://maven.apache.org/pom.html.Version_Order_Specification)
 *
 * @author [Kenney Westerhof](mailto:kenney@apache.org)
 * @author [Hervé Boutemy](mailto:hboutemy@apache.org)
 */
class ComparableVersion(version: String) : Comparable<ComparableVersion> {
    private var value: String? = null
    var canonical: String? = null
        get() {
            if (field == null) {
                field = items.toString()
            }
            return field
        }
        private set

    internal var items: ListItem? = null

    val hasQualifier: Boolean
        get() {
            return items?.any { it is ListItem } ?: false
        }

    internal interface Item {
        operator fun compareTo(item: Item?): Int
        val type: Int
        val isNull: Boolean

        companion object {
            const val INT_ITEM = 3
            const val LONG_ITEM  = 4
            const val BIGINTEGER_ITEM  = 0
            const val STRING_ITEM  = 1
            const val LIST_ITEM  = 2
        }
    }

    /**
     * Represents a numeric item in the version item list that can be represented with an int.
     */
    private class IntItem : Item {
        private val value: Int

        override val type: Int = INT_ITEM

        private constructor() {
            value = 0
        }

        internal constructor(str: String) {
            value = str.toInt()
        }

        override val isNull: Boolean
            get() = value == 0

        override fun compareTo(item: Item?): Int {
            return if (item == null) {
                if (value == 0) 0 else 1 // 1.0 == 1, 1.1 > 1
            } else when (item.type) {
                INT_ITEM -> {
                    val itemValue = (item as IntItem).value
                    value.compareTo(itemValue)
                }
                LONG_ITEM, BIGINTEGER_ITEM -> -1
                STRING_ITEM -> 1 // 1.1 > 1-sp
                LIST_ITEM -> 1 // 1.1 > 1-1
                else -> throw IllegalStateException("invalid item: " + item.javaClass)
            }
        }

        override fun equals(o: Any?): Boolean {
            if (this === o) {
                return true
            }
            if (o == null || javaClass != o.javaClass) {
                return false
            }
            val intItem = o as IntItem
            return value == intItem.value
        }

        override fun hashCode(): Int {
            return value
        }

        override fun toString(): String {
            return value.toString()
        }

        companion object {
            val ZERO = IntItem()
        }
    }

    /**
     * Represents a numeric item in the version item list that can be represented with a long.
     */
    private class LongItem internal constructor(str: String) : Item {

        private val value: Long

        override val type: Int = LONG_ITEM

        init {
            value = str.toLong()
        }

        override val isNull: Boolean
            get() = value == 0L

        override fun compareTo(item: Item?): Int {
            return if (item == null) {
                if (value == 0L) 0 else 1 // 1.0 == 1, 1.1 > 1
            } else when (item.type) {
                INT_ITEM -> 1
                LONG_ITEM -> {
                    val itemValue = (item as LongItem).value
                    value.compareTo(itemValue)
                }
                BIGINTEGER_ITEM -> -1
                STRING_ITEM -> 1 // 1.1 > 1-sp
                LIST_ITEM -> 1 // 1.1 > 1-1
                else -> throw IllegalStateException("invalid item: " + item.javaClass)
            }
        }


        override fun equals(o: Any?): Boolean {
            if (this === o) {
                return true
            }
            if (o == null || javaClass != o.javaClass) {
                return false
            }
            val longItem = o as LongItem
            return value == longItem.value
        }

        override fun hashCode(): Int {
            return (value xor (value ushr 32)).toInt()
        }

        override fun toString(): String {
            return value.toString()
        }
    }

    /**
     * Represents a numeric item in the version item list.
     */
    private class BigIntegerItem internal constructor(str: String?) : Item {
        private val value: BigInteger

        override val type: Int = BIGINTEGER_ITEM

        init {
            value = BigInteger(str)
        }

        override val isNull: Boolean
            get() = BigInteger.ZERO == value

        override fun compareTo(item: Item?): Int {
            return if (item == null) {
                if (BigInteger.ZERO == value) 0 else 1 // 1.0 == 1, 1.1 > 1
            } else when (item.type) {
                INT_ITEM, LONG_ITEM -> 1
                BIGINTEGER_ITEM -> value.compareTo((item as BigIntegerItem).value)
                STRING_ITEM -> 1 // 1.1 > 1-sp
                LIST_ITEM -> 1 // 1.1 > 1-1
                else -> throw IllegalStateException("invalid item: " + item.javaClass)
            }
        }

        override fun equals(o: Any?): Boolean {
            if (this === o) {
                return true
            }
            if (o == null || javaClass != o.javaClass) {
                return false
            }
            val that = o as BigIntegerItem
            return value == that.value
        }

        override fun hashCode(): Int {
            return value.hashCode()
        }

        override fun toString(): String {
            return value.toString()
        }
    }

    /**
     * Represents a string in the version item list, usually a qualifier.
     */
    private class StringItem(value: String, followedByDigit: Boolean) : Item {
        private val value: String

        override val type: Int = STRING_ITEM

        init {
            var value = value
            if (followedByDigit && value.length == 1) {
                // a1 = alpha-1, b1 = beta-1, m1 = milestone-1
                when (value[0]) {
                    'a' -> value = "alpha"
                    'b' -> value = "beta"
                    'm' -> value = "milestone"
                    else -> {}
                }
            }
            this.value = ALIASES.getOrDefault(value, value)
        }

        override val isNull: Boolean
            get() = QUALIFIERS.indexOf(value) == RELEASE_VERSION_INDEX

        override fun compareTo(item: Item?): Int {
            return if (item == null) {
                // 1-rc < 1, 1-ga > 1
                QUALIFIERS.indexOf(value).compareTo(RELEASE_VERSION_INDEX)
            } else when (item.type) {
                INT_ITEM, LONG_ITEM, BIGINTEGER_ITEM -> -1 // 1.any < 1.1 ?
                STRING_ITEM -> compareQualifiers(
                    value,
                    (item as StringItem).value
                )
                LIST_ITEM -> -1 // 1.any < 1-1
                else -> throw IllegalStateException("invalid item: " + item.javaClass)
            }
        }

        override fun equals(o: Any?): Boolean {
            if (this === o) {
                return true
            }
            if (o == null || javaClass != o.javaClass) {
                return false
            }
            val that = o as StringItem
            return value == that.value
        }

        override fun hashCode(): Int {
            return value.hashCode()
        }

        override fun toString(): String {
            return value
        }

        companion object {
            private val QUALIFIERS = listOf("snapshot", "", "sp")
            private val ALIASES: MutableMap<String, String> = HashMap(4)

            init {
                ALIASES["cr"] = "rc"
                ALIASES["final"] = ""
                ALIASES["ga"] = ""
                ALIASES["release"] = ""
            }

            /**
             * An index value for the empty-string qualifier. This one is used to determine if a given qualifier makes
             * the version older than one without a qualifier, or more recent.
             */
            private val RELEASE_VERSION_INDEX = QUALIFIERS.indexOf("")

            /**
             * Returns a comparable value for a qualifier.
             *
             * This method takes into account the ordering of known qualifiers then unknown qualifiers with lexical
             * ordering.
             *
             * just returning an Integer with the index here is faster, but requires a lot of if/then/else to check for -1
             * or QUALIFIERS.size and then resort to lexical ordering. Most comparisons are decided by the first character,
             * so this is still fast. If more characters are needed then it requires a lexical sort anyway.
             *
             * @param qualifier
             * @return an equivalent value that can be used with lexical comparison
             */
            @Deprecated("Use {@link #compareQualifiers(String, String)} instead")
            fun comparableQualifier(qualifier: String): String {
                val index = QUALIFIERS.indexOf(qualifier) + 1
                return if (index == 0) "0-$qualifier" else index.toString()
            }

            /**
             * Compare the qualifiers of two artifact versions.
             *
             * @param qualifier1 qualifier of first artifact
             * @param qualifier2 qualifier of second artifact
             * @return a negative integer, zero, or a positive integer as the first argument is less than, equal to, or
             * greater than the second
             */
            fun compareQualifiers(qualifier1: String, qualifier2: String): Int {
                val i1 = QUALIFIERS.indexOf(qualifier1)
                val i2 = QUALIFIERS.indexOf(qualifier2)

                // if both pre-release, then use natural lexical ordering
                return if (i1 == -1 && i2 == -1) {
                    // alpha < beta < ea < milestone < preview < rc
                    qualifier1.compareTo(qualifier2)
                } else i1.compareTo(i2)

                // 'other qualifier' < 'snapshot' < '' < 'sp'
            }
        }
    }

    /**
     * Represents a version list item. This class is used both for the global item list and for sub-lists (which start
     * with '-(number)' in the version specification).
     */
    internal class ListItem : ArrayList<Item?>(), Item {
        override val isNull: Boolean
            get() = size == 0

        override val type: Int = LIST_ITEM

        fun normalize() {
            for (i in size - 1 downTo 0) {
                val lastItem = get(i)
                if (lastItem!!.isNull) {
                    // remove null trailing items: 0, "", empty list
                    removeAt(i)
                } else if (lastItem !is ListItem) {
                    break
                }
            }
        }

        override fun compareTo(item: Item?): Int {
            if (item == null) {
                if (size == 0) {
                    return 0 // 1-0 = 1- (normalize) = 1
                }
                // Compare the entire list of items with null - not just the first one, MNG-6964
                for (i in this) {
                    val result = i!!.compareTo(null)
                    if (result != 0) {
                        return result
                    }
                }
                return 0
            }
            return when (item.type) {
                INT_ITEM, LONG_ITEM, BIGINTEGER_ITEM -> -1 // 1-1 < 1.0.x
                STRING_ITEM -> 1 // 1-1 > 1-sp
                LIST_ITEM -> {
                    val left: Iterator<Item?> = iterator()
                    val right: Iterator<Item?> = (item as ListItem).iterator()
                    while (left.hasNext() || right.hasNext()) {
                        val l = if (left.hasNext()) left.next() else null
                        val r = if (right.hasNext()) right.next() else null

                        // if this is shorter, then invert the compare and mul with -1
                        val result = l?.compareTo(r) ?: if (r == null) 0 else -1 * r.compareTo(l)
                        if (result != 0) {
                            return result
                        }
                    }
                    0
                }
                else -> throw IllegalStateException("invalid item: " + item.javaClass)
            }
        }

        override fun toString(): String {
            val buffer = StringBuilder()
            for (item in this) {
                if (buffer.isNotEmpty()) {
                    buffer.append(if (item is ListItem) '-' else '.')
                }
                buffer.append(item)
            }
            return buffer.toString()
        }

        /**
         * Return the contents in the same format that is used when you call toString() on a List.
         */
        fun toListString(): String {
            val buffer = StringBuilder()
            buffer.append("[")
            for (item in this) {
                if (buffer.length > 1) {
                    buffer.append(", ")
                }
                if (item is ListItem) {
                    buffer.append(item.toListString())
                } else {
                    buffer.append(item)
                }
            }
            buffer.append("]")
            return buffer.toString()
        }
    }

    init {
        parseVersion(version)
    }

    fun parseVersion(version: String) {
        var thisVersion = version
        value = thisVersion
        items = ListItem()
        thisVersion = thisVersion.lowercase()
        var list = items!!
        val stack: Deque<Item> = ArrayDeque()
        stack.push(list)
        var isDigit = false
        var startIndex = 0
        for (i in thisVersion.indices) {
            val c = thisVersion[i]
            if (c == '.') {
                if (i == startIndex) {
                    list.add(IntItem.ZERO)
                } else {
                    list.add(parseItem(isDigit, thisVersion.substring(startIndex, i)))
                }
                startIndex = i + 1
            } else if (c == '-') {
                if (i == startIndex) {
                    list.add(IntItem.ZERO)
                } else {
                    list.add(parseItem(isDigit, thisVersion.substring(startIndex, i)))
                }
                startIndex = i + 1
                list.add(ListItem().also { list = it })
                stack.push(list)
            } else if (Character.isDigit(c)) {
                if (!isDigit && i > startIndex) {
                    // 1.0.0.RC1 < 1.0.0-RC2
                    // treat .RC as -RC
                    if (!list.isEmpty()) {
                        list.add(ListItem().also { list = it })
                        stack.push(list)
                    }
                    list.add(StringItem(thisVersion.substring(startIndex, i), true))
                    startIndex = i
                    list.add(ListItem().also { list = it })
                    stack.push(list)
                }
                isDigit = true
            } else {
                if (isDigit && i > startIndex) {
                    list.add(parseItem(true, thisVersion.substring(startIndex, i)))
                    startIndex = i
                    list.add(ListItem().also { list = it })
                    stack.push(list)
                }
                isDigit = false
            }
        }
        if (thisVersion.length > startIndex) {
            // 1.0.0.RC1 < 1.0.0-RC2
            // treat .RC as -RC
            if (!isDigit && !list.isEmpty()) {
                list.add(ListItem().also { list = it })
                stack.push(list)
            }
            list.add(parseItem(isDigit, thisVersion.substring(startIndex)))
        }
        while (!stack.isEmpty()) {
            list = stack.pop() as ListItem
            list.normalize()
        }
    }

    override fun compareTo(o: ComparableVersion): Int {
        return items!!.compareTo(o.items)
    }

    override fun toString(): String {
        return value!!
    }

    override fun equals(o: Any?): Boolean {
        return o is ComparableVersion && items == o.items
    }

    override fun hashCode(): Int {
        return items.hashCode()
    }

    companion object {
        private const val MAX_INTITEM_LENGTH = 9
        private const val MAX_LONGITEM_LENGTH = 18
        private fun parseItem(isDigit: Boolean, buf: String): Item {
            var buf = buf
            if (isDigit) {
                buf = stripLeadingZeroes(buf)
                if (buf.length <= MAX_INTITEM_LENGTH) {
                    // lower than 2^31
                    return IntItem(buf)
                } else if (buf.length <= MAX_LONGITEM_LENGTH) {
                    // lower than 2^63
                    return LongItem(buf)
                }
                return BigIntegerItem(buf)
            }
            return StringItem(buf, false)
        }

        private fun stripLeadingZeroes(buf: String?): String {
            if (buf == null || buf.isEmpty()) {
                return "0"
            }
            for (i in buf.indices) {
                val c = buf[i]
                if (c != '0') {
                    return buf.substring(i)
                }
            }
            return buf
        }
        // CHECKSTYLE_OFF: LineLength
        /**
         * Main to test version parsing and comparison.
         *
         *
         * To check how "1.2.7" compares to "1.2-SNAPSHOT", for example, you can issue
         * <pre>java -jar ${maven.repo.local}/org/apache/maven/maven-artifact/${maven.version}/maven-artifact-${maven.version}.jar "1.2.7" "1.2-SNAPSHOT"</pre>
         * command to command line. Result of given command will be something like this:
         * <pre>
         * Display parameters as parsed by Maven (in canonical form) and comparison result:
         * 1. 1.2.7 == 1.2.7
         * 1.2.7 &gt; 1.2-SNAPSHOT
         * 2. 1.2-SNAPSHOT == 1.2-snapshot
        </pre> *
         *
         * @param args the version strings to parse and compare. You can pass arbitrary number of version strings and always
         * two adjacent will be compared
         */
        // CHECKSTYLE_ON: LineLength
        @JvmStatic
        fun main(args: Array<String>) {
            println(
                "Display parameters as parsed by Maven (in canonical form and as a list of tokens) and"
                    + " comparison result:"
            )
            if (args.isEmpty()) {
                return
            }
            var prev: ComparableVersion? = null
            var i = 1
            for (version in args) {
                val c = ComparableVersion(version)
                if (prev != null) {
                    val compare = prev.compareTo(c)
                    println(
                        "   " + prev.toString() + ' ' + (if (compare == 0) "==" else if (compare < 0) "<" else ">")
                            + ' ' + version
                    )
                }
                println(
                    i++.toString() + ". " + version + " -> " + c.canonical + "; tokens: " + c.items!!.toListString()
                )
                prev = c
            }
        }
    }
}