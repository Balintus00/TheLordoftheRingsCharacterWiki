# The Lord of the Rings Character Wiki

## Kotlin Multiplatform List-Detail sample application

### Supported platforms

- Android
- JVM Desktop
- iOS

### Planned platforms to support

- Web (using WASM)

### Prerequisites

The application uses [The One API](https://the-one-api.dev/) as its backend datasource. To use the application you need
to first register on the API's webpage, and obtain an API key. After the API key is received, placed it into the
[local.properties](local.properties) file with *THE_ONE_API_KEY* key.

### Requirements

The detailed requirements are available in a [dedicated document][requirements].

### UI Design

I created the UI plans using [Figma][figma], and they can be found in the [related Figma project][figma-project]. 
The design follows [Material 3 guidelines][material3].

### Application Design

The detailed design of the implemented system is available in the [design documentation][design].

## Acknowledgements

- I learned how to store API keys on Kotlin Multiplatform with BuildKonfig from 
[Andrea Liu][andrea-liu]'s [article][buildkonfig-api-key-article].

## Contributing

Thank you for your interest in contributing to this project! At the moment, the repository is not open to external
contributions. However, this may change in the future as the project evolves. Stay tuned for updates!

[andrea-liu]: https://andrea-liu87.github.io/
[buildkonfig-api-key-article]: https://andrea8787.medium.com/storing-api-key-using-buildkonfig-on-kotlin-multiplatform-dddcbe560890
[design]: design.md
[figma]: https://www.figma.com/
[figma-project]: https://www.figma.com/design/kFmS6yBYlx0ENYEOT5F5wx/Lord-of-the-Rings-Character-Wiki---Mobil-Labor?node-id=0-1
[material3]: https://m3.material.io/
[requirements]: requirements.md