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

package clojuresque

import org.gradle.api.tasks.Upload
import org.gradle.testfixtures.ProjectBuilder

import spock.lang.Ignore
import spock.lang.Specification

public class ClojarsExtensionTest extends Specification {
    def project = ProjectBuilder.builder().build()

    def setup() {
        project.apply plugin: "clojars"
    }

    def "username is picked up by credentials"() {
        expect:
        project.
            clojars.
            credentials(username: "user").
            username == "user"
    }

    def "password is picked up by credentials"() {
        expect:
        project.
            clojars.
            credentials(password: "passw0rd").
            password == "passw0rd"
    }

    def "url is picked up by credentials"() {
        expect:
        project.
            clojars.
            credentials(url: "http://kotka.de").
            url == "http://kotka.de"
    }

    def "an empty URL turns into clojars default"() {
        expect:
        project.
            clojars.
            credentials().
            url == "https://clojars.org/repo"
    }

    def "deploy picks up properties"() {
        given:
        def creds = [
            username: "user",
            password: "passw0rd",
            url:      "http://kotka.de"
        ]

        when:
        project["clojuresque.clojars.username"] = creds.username
        project["clojuresque.clojars.password"] = creds.password
        project["clojuresque.clojars.url"]      = creds.url

        then:
        project.clojars.credentials() == creds
    }

    def "deploy with empty user name fails"() {
        when:
        def upload = project.tasks.add(name: "upload", type: Upload)
        upload.enabled = true
        project.clojars {
            deploy(upload, password: "password")
        }

        then:
        upload.enabled == false
    }

    def "deploy with empty password fails"() {
        when:
        def upload = project.tasks.add(name: "upload", type: Upload)
        upload.enabled = true
        project.clojars {
            deploy(upload, username: "user")
        }

        then:
        upload.enabled == false
    }

    def "deploy with credentials does not fail"() {
        when:
        def upload = project.tasks.add(name: "upload", type: Upload)
        upload.enabled = true
        project.clojars {
            deploy(upload, username: "user", password: "passw0rd")
        }

        then:
        upload.enabled == true
    }

    @Ignore
    def "deploy configures pom"() {
        when:
        def upload = project.tasks.add(name: "upload", type: Upload)
        project.clojars {
            username = "user"
            password = "password"
            deploy(upload, username: "user", password: "passw0rd") {
                project {
                    description "foobar"
                }
            }
        }

        then:
        upload.repositories.mavenDeployer.pom.model.description.equals("foobar")
    }
}
