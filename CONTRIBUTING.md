# Contributing to Gardien

Thank you for your interest in contributing to Gardien! This project aims to protect children online while respecting their privacy.

## Code of Conduct

By participating, you agree to maintain a respectful and constructive environment. Given the sensitive nature of this project (child safety), we hold contributors to a high standard of ethical behavior.

## How to Contribute

### Reporting Bugs
- Use GitHub Issues with the `bug` label
- Include: device model, Android version, steps to reproduce
- **Never include real conversation data or personal information of minors**

### Suggesting Features
- Open an issue with the `enhancement` label
- Describe the use case and how it improves child safety or privacy

### Code Contributions
1. Fork the repository
2. Create a feature branch (`git checkout -b feature/my-feature`)
3. Commit your changes (`git commit -m 'Add: description'`)
4. Push to the branch (`git push origin feature/my-feature`)
5. Open a Pull Request

### Code Standards
- Kotlin code follows [Android Kotlin Style Guide](https://developer.android.com/kotlin/style-guide)
- All safety-critical code must include unit tests
- Evidence-related code must include integrity verification tests
- C++ code (llama.cpp JNI) follows the upstream style

## Legal

By contributing, you agree that your contributions will be licensed under:
- **AGPL-3.0 + Commons Clause** for source code
- **CC BY-NC-SA 4.0** for documentation

## Privacy & Ethics

- Never commit test data containing real conversations
- Never commit real CSAM hashes or detection datasets to the public repo
- Use synthetic/anonymized data for all testing
- When in doubt about ethical implications, open a discussion first