/*-
 * Copyright 2013-2014 © Meikel Brandmeyer.
 * All rights reserved.
 *
 * Licensed under the EUPL V.1.1 (cf. file EUPL-v1.1 distributed with the
 * source code.) Translations in other european languages available at
 * https://joinup.ec.europa.eu/software/page/eupl.
 *
 * Alternatively, you may choose to use the software under the MIT license
 * (cf.  file MIT distributed with the source code).
 */

package clojuresque.clojars

class ClojarsExtension {
    def project

    ClojarsExtension(p) {
        project = p
    }

    void repo(repositories) {
        repositories.maven { url "http://clojars.org/repo" }
    }

    def credentials(Map creds=[:]) {
        def defaults = [
            username:     null,
            password:     null,
            url:          "https://clojars.org/repo",
            snapshotsUrl: null
        ]

        if (project.hasProperty("clojuresque.clojars.username"))
            defaults.username = project["clojuresque.clojars.username"]

        if (project.hasProperty("clojuresque.clojars.password"))
            defaults.password = project["clojuresque.clojars.password"]

        if (project.hasProperty("clojuresque.clojars.url"))
            defaults.url = project["clojuresque.clojars.url"]

        if (project.hasProperty("clojuresque.clojars.snapshotsUrl"))
            defaults.snapshotsUrl = project["clojuresque.clojars.snapshotsUrl"]

        defaults.plus(creds)
    }

    void deploy(Map cs=[:], upload, Closure projectSpec={}) {
        def creds = credentials(cs)

        if (creds.username == null) {
            upload.enabled = false
            return
        }

        if (creds.password == null) {
            upload.enabled = false
            return
        }

        project.afterEvaluate {
            upload.repositories {
                mavenDeployer {
                    configuration =
                        project.configurations.clojuresqueClojarsDeployerJars
                    repository(url: chooseUrl(isSnapshot(project.version), creds)) {
                        authentication(
                            userName: creds.username,
                            password: creds.password
                        )
                    }
                    project.configure(pom, projectSpec)
                    if (project.hasProperty("signing")) {
                        beforeDeployment { project.signing.signPom(it) }
                    }
                }
            }
        }
    }

    static isSnapshot(v)   { v?.endsWith("-SNAPSHOT") }
    static chooseUrl(s, c) { s ? (c.snapshotsUrl ?: c.url) : c.url }
}
