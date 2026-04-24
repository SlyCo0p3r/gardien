package dev.gardien.app.dataset

object SyntheticDatasetValidator {
    private val emailPattern = Regex("[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}", RegexOption.IGNORE_CASE)
    private val phonePattern = Regex("\\b(?:\\+?\\d[\\d .-]{7,}\\d)\\b")
    private val urlPattern = Regex("https?://|www\\.", RegexOption.IGNORE_CASE)

    fun validate(conversations: List<SyntheticConversation>): DatasetValidationResult {
        val errors = mutableListOf<String>()

        if (conversations.size < SyntheticDatasetGenerator.DEFAULT_SIZE) {
            errors += "Dataset must contain at least ${SyntheticDatasetGenerator.DEFAULT_SIZE} conversations."
        }

        val labels = conversations.map { it.label }.toSet()
        RiskLabel.entries.filterNot(labels::contains).forEach { missing ->
            errors += "Missing label coverage: $missing."
        }

        conversations.forEach { conversation ->
            if (conversation.syntheticProvenance.isBlank()) {
                errors += "${conversation.id} is missing synthetic provenance."
            }
            if (conversation.severityScore !in 0..100) {
                errors += "${conversation.id} severity is outside 0..100."
            }
            conversation.turns.forEach { turn ->
                if (containsForbiddenPii(turn.text)) {
                    errors += "${conversation.id} contains forbidden PII-like text."
                }
            }
        }

        return DatasetValidationResult(valid = errors.isEmpty(), errors = errors)
    }

    fun containsForbiddenPii(text: String): Boolean =
        emailPattern.containsMatchIn(text) ||
            phonePattern.containsMatchIn(text) ||
            urlPattern.containsMatchIn(text)
}
