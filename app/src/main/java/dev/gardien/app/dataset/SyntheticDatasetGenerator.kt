package dev.gardien.app.dataset

object SyntheticDatasetGenerator {
    const val DEFAULT_SIZE = 500
    const val DEFAULT_SEED = 2_026_042_4L

    fun generate(
        seed: Long = DEFAULT_SEED,
        size: Int = DEFAULT_SIZE,
    ): List<SyntheticConversation> =
        (0 until size).map { index ->
            val label = RiskLabel.entries[index % RiskLabel.entries.size]
            val severity = severityFor(label, index)
            SyntheticConversation(
                id = "synthetic-$seed-$index",
                ageBand = AGE_BANDS[(index + seed.toInt()).floorMod(AGE_BANDS.size)],
                appContext = APP_CONTEXTS[(index * 3 + seed.toInt()).floorMod(APP_CONTEXTS.size)],
                language = LANGUAGES[(index + 1).floorMod(LANGUAGES.size)],
                label = label,
                severityScore = severity,
                expectedAction = expectedActionFor(severity),
                rationale = rationaleFor(label),
                syntheticProvenance = "deterministic-template-v1 seed=$seed",
                turns = turnsFor(label, index),
            )
        }

    private fun severityFor(label: RiskLabel, index: Int): Int =
        when (label) {
            RiskLabel.BENIGN -> 5 + index.floorMod(20)
            RiskLabel.AMBIGUOUS -> 35 + index.floorMod(20)
            RiskLabel.HARASSMENT -> 60 + index.floorMod(15)
            RiskLabel.GROOMING -> 65 + index.floorMod(15)
            RiskLabel.SELF_HARM -> 70 + index.floorMod(15)
            RiskLabel.SEXUAL_RISK -> 75 + index.floorMod(10)
            RiskLabel.VIOLENCE -> 85 + index.floorMod(10)
        }

    private fun expectedActionFor(severity: Int): ExpectedAction =
        when {
            severity < 30 -> ExpectedAction.OK
            severity < 60 -> ExpectedAction.ATTENTION
            severity < 85 -> ExpectedAction.ALERTE
            else -> ExpectedAction.LOCK
        }

    private fun rationaleFor(label: RiskLabel): String =
        when (label) {
            RiskLabel.BENIGN -> "Ordinary peer conversation with no safety signal."
            RiskLabel.HARASSMENT -> "Repeated hostile peer behavior and social pressure."
            RiskLabel.GROOMING -> "Unknown contact attempts to isolate the child in private chat."
            RiskLabel.SELF_HARM -> "Message indicates the child may not feel safe with themself."
            RiskLabel.SEXUAL_RISK -> "Contact pressures the child toward an unsafe private image exchange."
            RiskLabel.VIOLENCE -> "Message includes a credible threat of physical harm."
            RiskLabel.AMBIGUOUS -> "Potentially concerning wording without enough context for escalation."
        }

    private fun turnsFor(label: RiskLabel, index: Int): List<SyntheticTurn> {
        val peer = if (index % 2 == 0) "peer_contact" else "unknown_contact"
        return when (label) {
            RiskLabel.BENIGN -> listOf(
                SyntheticTurn("child", "Can we finish the group project after class?"),
                SyntheticTurn("peer_contact", "Yes, I can share the notes later."),
            )
            RiskLabel.HARASSMENT -> listOf(
                SyntheticTurn("peer_contact", "The group keeps making cruel jokes about you."),
                SyntheticTurn("child", "I asked them to stop and they keep doing it."),
            )
            RiskLabel.GROOMING -> listOf(
                SyntheticTurn(peer, "Keep this chat private and do not tell anyone."),
                SyntheticTurn("child", "Why do you only want to talk when nobody is around?"),
            )
            RiskLabel.SELF_HARM -> listOf(
                SyntheticTurn("child", "I do not feel safe with myself tonight."),
                SyntheticTurn("peer_contact", "Please message a trusted adult now."),
            )
            RiskLabel.SEXUAL_RISK -> listOf(
                SyntheticTurn(peer, "Send an unsafe private photo and keep it secret."),
                SyntheticTurn("child", "That makes me uncomfortable and I do not want to."),
            )
            RiskLabel.VIOLENCE -> listOf(
                SyntheticTurn(peer, "Someone threatened to hurt me near school."),
                SyntheticTurn("child", "I am going to find an adult before leaving."),
            )
            RiskLabel.AMBIGUOUS -> listOf(
                SyntheticTurn("peer_contact", "You will regret ignoring this message."),
                SyntheticTurn("child", "I cannot tell if that is a joke or a threat."),
            )
        }
    }

    private fun Int.floorMod(modulus: Int): Int = Math.floorMod(this, modulus)

    private val AGE_BANDS = listOf("10-12", "13-14", "15-17")
    private val APP_CONTEXTS = listOf(
        "com.whatsapp",
        "com.snapchat.android",
        "com.zhiliaoapp.musically",
        "com.discord",
        "com.instagram.android",
        "android.provider.sms",
    )
    private val LANGUAGES = listOf("fr", "en")
}
