package dev.gardien.app.dataset

data class DatasetValidationResult(
    val valid: Boolean,
    val errors: List<String>,
)
