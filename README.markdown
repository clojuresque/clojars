# clojuresque/clojars – a Clojars plugin for Gradle

## What is Gradle?

[Gradle][] is a build system written in Java and [Groovy][]. One advantage
of [Gradle][] is, that the build script itself is also a [Groovy][] script.
That means whatever can be done in [Groovy][] can be done in the build
script. This is useful to abstract away common patterns in the build like
repetition or conditional decisions.

On top of that [Gradle][] provides a convenient build system which comes
in form of different plugins. Each plugin defines certain conventions which
(if followed) automasie 95% of the build completely. Still the conventions
are freely configurable to be adapted to different project structures.

## What is clojuresque?

[clojuresque][cg] is now a plugin for [Gradle][], which adds [Clojure][clj]
support. It allows compilation with automatic namespace recognition. The
plugin is based on the Java plugin and hooks into the standard configurations
and archives.

The [Clojars][cr] sub plugin adds some configuration convenience to
deploy to the “clojars” community repository. It sets up a https transport
for any given upload task. A convenience method for repository definition
is also provided.

## Usage

    apply plugin: 'clojars'
    
    clojars {
        username = "your-clojars-login"
        password = "your-clojars-password"

        repo(repositories)
        deploy(uploadArchives) {
            project {
                licenses {
                    license {
                        name 'MIT License'
                        url 'http://opensource.org/licenses/MIT'
                        distribution 'repo'
                    }
                }
                …
            }
        }
    }

-- 
Meikel Brandmeyer <mb@kotka.de>
Erlensee, January 2013

[Gradle]: http://www.gradle.org
[Groovy]: http://groovy.codehaus.org
[clj]:    http://clojure.org
[cg]:     http://bitbucket.org/clojuresque
[cr]:     http://clojars.org
