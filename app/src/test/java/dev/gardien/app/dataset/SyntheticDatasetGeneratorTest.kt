package dev.gardien.app.dataset

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SyntheticDatasetGeneratorTest {
    @Test
    fun `generator is deterministic and creates five hundred conversations`() {
        val first = SyntheticDatasetGenerator.generate(seed = 42L)
        val second = SyntheticDatasetGenerator.generate(seed = 42L)

        assertEquals(500, first.size)
        assertEquals(first, second)
    }

    @Test
    fun `dataset covers every risk label`() {
        val labels = SyntheticDatasetGenerator.generate()
            .map { it.label }
            .toSet()

        assertEquals(RiskLabel.entries.toSet(), labels)
    }

    @Test
    fun `validator accepts generated dataset`() {
        val result = SyntheticDatasetValidator.validate(SyntheticDatasetGenerator.generate())

        assertTrue(result.errors.joinToString(), result.valid)
    }

    @Test
    fun `validator rejects obvious personal data patterns`() {
        assertTrue(SyntheticDatasetValidator.containsForbiddenPii("contact me at child@example.com"))
        assertTrue(SyntheticDatasetValidator.containsForbiddenPii("call +33 6 12 34 56 78"))
        assertTrue(SyntheticDatasetValidator.containsForbiddenPii("open https://example.com"))
        assertFalse(SyntheticDatasetValidator.containsForbiddenPii("peer_contact sent a synthetic fixture"))
    }
}
