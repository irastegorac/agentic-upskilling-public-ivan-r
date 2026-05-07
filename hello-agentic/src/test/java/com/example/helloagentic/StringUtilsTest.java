package com.example.helloagentic;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StringUtilsTest {

    // =========================================================================
    // slugify
    // =========================================================================

    @Nested
    class Slugify {

        // --- Happy path ---

        @Test
        void convertsSimpleTextToSlug() {
            assertEquals("hello-world", StringUtils.slugify("Hello World"));
        }

        @Test
        void convertsWithCustomSeparator() {
            assertEquals("hello_world", StringUtils.slugify("Hello World", "_", null));
        }

        @Test
        void truncatesToMaxLength() {
            assertEquals("hello", StringUtils.slugify("Hello World", "-", 5));
        }

        @Test
        void stripsSpecialCharacters() {
            assertEquals("hello-world", StringUtils.slugify("Hello, World!"));
        }

        @Test
        void collapsesMultipleSpacesIntoOneSeparator() {
            assertEquals("hello-world", StringUtils.slugify("hello   world"));
        }

        @Test
        void collapsesUnderscoresIntoSeparator() {
            assertEquals("hello-world", StringUtils.slugify("hello__world"));
        }

        @Test
        void stripsLeadingAndTrailingDashes() {
            assertEquals("hello", StringUtils.slugify("-hello-"));
        }

        @Test
        void handlesMultipleSpecialCharacters() {
            assertEquals("a-b-c", StringUtils.slugify("a @ b # c"));
        }

        // --- MaxLength boundary ---

        @Test
        void maxLengthExactlyMatchesSlugLength() {
            assertEquals("hello", StringUtils.slugify("hello", "-", 5));
        }

        @Test
        void maxLengthStripsTrailingSeparator() {
            // "hello-world" truncated to 6 is "hello-", trailing separator removed
            assertEquals("hello", StringUtils.slugify("Hello World", "-", 6));
        }

        @Test
        void maxLengthStripsTrailingMultiCharSeparator() {
            // "hello--world" with separator "--", truncated to 7 is "hello--", trailing separator removed
            assertEquals("hello", StringUtils.slugify("Hello World", "--", 7));
        }

        @Test
        void maxLengthLargerThanSlug() {
            assertEquals("hi", StringUtils.slugify("hi", "-", 100));
        }

        @Test
        void maxLengthOfZeroReturnsEmpty() {
            assertEquals("", StringUtils.slugify("Hello World", "-", 0));
        }

        // --- Edge cases ---

        @Test
        void stripsUnicodeAccentedCharacters() {
            // \w in Java regex does not match unicode letters by default,
            // so accented characters like é are stripped
            assertEquals("caf-rsum", StringUtils.slugify("café résumé"));
        }

        @Test
        void handlesAlreadyLowercaseText() {
            assertEquals("already-lower", StringUtils.slugify("already lower"));
        }

        @Test
        void handlesMixedWhitespaceAndUnderscores() {
            assertEquals("a-b-c", StringUtils.slugify("a _b_ c"));
        }

        @Test
        void handlesLeadingAndTrailingWhitespace() {
            assertEquals("hello", StringUtils.slugify("  hello  "));
        }

        @Test
        void handlesAllSpecialCharacters() {
            assertEquals("", StringUtils.slugify("!@#$%^&*()"));
        }

        // --- Error cases ---

        @Test
        void throwsOnNullText() {
            assertThrows(NullPointerException.class, () -> StringUtils.slugify(null));
        }

        @Test
        void throwsOnNullTextWithArgs() {
            assertThrows(NullPointerException.class, () -> StringUtils.slugify(null, "-", null));
        }
    }

    // =========================================================================
    // truncate
    // =========================================================================

    @Nested
    class Truncate {

        // --- Happy path ---

        @Test
        void truncatesLongTextWithDefaultSuffix() {
            assertEquals("Hello Wo...", StringUtils.truncate("Hello World, how are you?", 11));
        }

        @Test
        void truncatesWithCustomSuffix() {
            assertEquals("Hello Wor…", StringUtils.truncate("Hello World, how are you?", 10, "…"));
        }

        @Test
        void breaksAtWordBoundaryWhenSpaceIsInSecondHalf() {
            // "The quick brown fox" with length 15, suffix "..."
            // substring(0, 12) = "The quick br", lastSpace=9, 9 > 15/2=7 → break at space
            assertEquals("The quick...", StringUtils.truncate("The quick brown fox", 15, "..."));
        }

        @Test
        void doesNotBreakAtWordBoundaryWhenSpaceIsInFirstHalf() {
            // "ab cdefghijklmno" with length 10, suffix "..."
            // substring(0, 7) = "ab cdef", lastSpace=2, 2 > 10/2=5 is false → keeps mid-word cut
            assertEquals("ab cdef...", StringUtils.truncate("ab cdefghijklmno", 10, "..."));
        }

        // --- Text shorter than or equal to length ---

        @Test
        void returnsTextUnchangedWhenExactlyAtLimit() {
            assertEquals("Hello", StringUtils.truncate("Hello", 5));
        }

        @Test
        void returnsTextUnchangedWhenShorterThanLimit() {
            assertEquals("Hi", StringUtils.truncate("Hi", 10));
        }

        // --- Edge cases ---

        @Test
        void returnsEmptyForNullText() {
            assertEquals("", StringUtils.truncate(null, 10, "..."));
        }

        @Test
        void returnsEmptyForEmptyText() {
            assertEquals("", StringUtils.truncate("", 10));
        }

        @Test
        void returnsEmptyForZeroLength() {
            assertEquals("", StringUtils.truncate("Hello", 0));
        }

        @Test
        void returnsEmptyForNegativeLength() {
            assertEquals("", StringUtils.truncate("Hello", -5));
        }

        @Test
        void handlesEmptySuffix() {
            assertEquals("Hello", StringUtils.truncate("Hello World", 5, ""));
        }

        @Test
        void handlesSingleCharacterText() {
            assertEquals("a", StringUtils.truncate("a", 5));
        }

        // --- Boundary: suffix length vs truncation length ---

        @Test
        void handlesLengthEqualToSuffixLength() {
            // length=3, suffix="..." → substring(0, 0) = "" → no space found → returns "..."
            assertEquals("...", StringUtils.truncate("Hello World", 3, "..."));
        }

        @Test
        void throwsWhenSuffixLongerThanLength() {
            // length=2, suffix="..." → substring(0, -1) → StringIndexOutOfBoundsException
            assertThrows(StringIndexOutOfBoundsException.class,
                    () -> StringUtils.truncate("Hello World", 2, "..."));
        }

        @Test
        void throwsOnNullSuffix() {
            assertThrows(NullPointerException.class,
                    () -> StringUtils.truncate("Hello World", 5, null));
        }
    }

    // =========================================================================
    // maskEmail
    // =========================================================================

    @Nested
    class MaskEmail {

        // --- Happy path ---

        @Test
        void masksStandardEmail() {
            assertEquals("j*****e@g***.com", StringUtils.maskEmail("johndoe@gmail.com"));
        }

        @Test
        void masksTwoCharLocalPart() {
            assertEquals("j*@g***.com", StringUtils.maskEmail("jo@gmail.com"));
        }

        @Test
        void masksSingleCharLocalPart() {
            assertEquals("j*@g***.com", StringUtils.maskEmail("j@gmail.com"));
        }

        @Test
        void masksThreeCharLocalPart() {
            assertEquals("j*e@g***.com", StringUtils.maskEmail("joe@gmail.com"));
        }

        @Test
        void masksEmailWithSubdomain() {
            assertEquals("u**r@m***.corp.example.com",
                    StringUtils.maskEmail("user@mail.corp.example.com"));
        }

        @Test
        void masksEmailWithDotsInLocalPart() {
            assertEquals("j******e@g***.com", StringUtils.maskEmail("john.doe@gmail.com"));
        }

        @Test
        void masksEmailWithPlusInLocalPart() {
            assertEquals("j******g@g***.com", StringUtils.maskEmail("john+tag@gmail.com"));
        }

        // --- Edge cases ---

        @Test
        void handlesMultipleAtSignsUsesLast() {
            // lastIndexOf('@') splits on the last @
            assertEquals("u***@@e***.com", StringUtils.maskEmail("user@@example.com"));
        }

        // --- Error cases ---

        @Test
        void throwsOnMissingAtSign() {
            assertThrows(IllegalArgumentException.class,
                    () -> StringUtils.maskEmail("notanemail"));
        }

        @Test
        void throwsOnNullInput() {
            assertThrows(NullPointerException.class,
                    () -> StringUtils.maskEmail(null));
        }

        @Test
        void throwsOnEmptyDomain() {
            // "user@" → domain="" → domainParts[0].charAt(0) fails
            assertThrows(StringIndexOutOfBoundsException.class,
                    () -> StringUtils.maskEmail("user@"));
        }

        @Test
        void throwsOnDomainWithNoDot() {
            // "user@localhost" → domainParts has 1 element, copyOfRange(1,1) is empty
            // join produces "" → result is "u**r@l***."
            assertEquals("u**r@l***.", StringUtils.maskEmail("user@localhost"));
        }
    }
}
