# Models Directory

LLM and ML model files go here. They are **excluded from git** (see `.gitignore`).

## Required Models (Phase 2)

| Model | File | Size | Purpose |
|---|---|---|---|
| Phi-3-mini 3.8B | `phi-3-mini-4k-instruct-q4_k_m.gguf` | ~2.2 GB | Text analysis + Llama Guard LoRA |
| Falconsai NSFW | `nsfw_model.onnx` | ~50 MB | Image safety classification |
| pdq hash DB | `pdq_hashes.bin` | Variable | CSAM perceptual hash database |

Models will be downloaded at first launch or via a setup script (TBD Phase 2).
