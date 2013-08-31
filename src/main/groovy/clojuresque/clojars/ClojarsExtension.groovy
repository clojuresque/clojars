/*-
 * Copyright 2013 © Meikel Brandmeyer.
 * All rights reserved.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
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

        if (project.hasProperty("clojuresque.clojars.snapshotsUrl")
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
