package dev.gardien.app.dataset

data class SyntheticConversation(
    val id: String,
    val ageBand: String,
    val appContext: String,
    val language: String,
    val label: RiskLabel,
    val severityScore: Int,
    val expectedAction: ExpectedAction,
    val rationale: String,
    val syntheticProvenance: String,
    val turns: List<SyntheticTurn>,
)

data class SyntheticTurn(
    val speaker: String,
    val text: String,
)
