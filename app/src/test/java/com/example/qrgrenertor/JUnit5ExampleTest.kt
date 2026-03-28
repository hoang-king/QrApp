package com.example.qrgrenertor

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

/**
 * JUnit 5 Example Unit Test
 */
class JUnit5ExampleTest {

    @Test
    @DisplayName("Kiểm tra phép cộng cơ bản")
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2, "2 + 2 phải bằng 4")
    }

    @ParameterizedTest
    @ValueSource(strings = ["", "  ", "\t", "\n"])
    @DisplayName("Kiểm tra chuỗi trống với ParameterizedTest")
    fun isBlank_ShouldReturnTrueForBlankStrings(input: String) {
        assert(input.isBlank())
    }
}
