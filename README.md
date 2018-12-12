# Easy
[![License](https://img.shields.io/badge/license-MIT-green.svg)](LICENSE)
![IRC: #shard @ esper](https://img.shields.io/badge/irc-%23shard%20%40%20irc.esper.net-ff69b4.svg)

This repository contains the (heavily opinionated) shared library code and plugin framework for the [Shard](https://github.com/ShardPowered) PaperMC plugins.

I've went with dependency injection instead of statics. The project uses the [Guice](https://github.com/google/guice) dependency injection framework.

## Documentation

The documentation is still a work-in-progress. Feel free to look at [Snake](https://github.com/ShardPowered/snake) for an example.

## Repository

```groovy
repositories {
    maven {
        url = 'https://maven.tassu.me/'
        name = 'tassu-repo'
    }
}

dependencies {
    compileOnly "me.tassu:easy:(version)"
}
```

## License

- [MIT](LICENSE).
