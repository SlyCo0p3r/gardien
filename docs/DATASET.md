# Gardien Synthetic Dataset

## Phase 1.2 Objective

Phase 1.2 creates the dataset foundation required before model calibration. It does not train a model. It defines the taxonomy, schema, deterministic generator, validation rules, and safety constraints that Phase 2 will consume.

## Taxonomy

| Label | Intended Signal | Default Action Band |
|---|---|---|
| `BENIGN` | Ordinary teen conversation | `OK` |
| `AMBIGUOUS` | Concerning wording with limited context | `ATTENTION` |
| `HARASSMENT` | Repeated hostile peer pressure | `ALERTE` |
| `GROOMING` | Isolation or secrecy pressure from an unknown contact | `ALERTE` |
| `SELF_HARM` | Child may not feel safe with themself | `ALERTE` |
| `SEXUAL_RISK` | Pressure toward unsafe private image exchange | `ALERTE` |
| `VIOLENCE` | Credible physical threat | `LOCK` |

## Schema

Each synthetic conversation contains:

- `id`
- `ageBand`
- `appContext`
- `language`
- `label`
- `severityScore`
- `expectedAction`
- `rationale`
- `syntheticProvenance`
- `turns[]` with `speaker` and `text`

The in-app generator currently produces 500 deterministic conversations from safe templates. The seed is recorded in `syntheticProvenance`.

## Safety Rules

- No real conversations.
- No real minors' data.
- No real names, phone numbers, emails, URLs, screenshots, or app exports.
- No CSAM and no explicit sexualized minor content.
- Use safe descriptive placeholders for unsafe scenarios.
- Dataset validation must fail on obvious PII-like patterns.

## Phase 1.2 Exit Criteria

- 500 generated conversations.
- Coverage across all taxonomy labels.
- Deterministic output for a fixed seed.
- Validation rejects forbidden PII-like patterns.
- Phase 2 can load the schema without changing labels or action names.
