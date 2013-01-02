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

import org.gradle.api.InvalidUserDataException
import org.gradle.api.Project
import org.gradle.api.tasks.Upload
import org.gradle.testfixtures.ProjectBuilder

import spock.lang.Specification

public class ClojarsExtensionTest extends Specification {
    def Project project = ProjectBuilder.builder().build()

    def setup() {
        project.apply plugin: ClojarsPlugin
    }

    def "extension is in sane state"() {
        expect:
        project.clojars.username == null
        project.clojars.password == null
        project.clojars.url      == null
    }

    def "deploy with empty user name fails"() {
        when:
        def upload = project.tasks.add(name: "upload", type: Upload)
        project.clojars {
            password = "password"
            deploy(upload)
        }

        then:
        thrown(InvalidUserDataException)
    }

    def "deploy with empty password fails"() {
        when:
        def upload = project.tasks.add(name: "upload", type: Upload)
        project.clojars {
            username = "user"
            deploy(upload)
        }

        then:
        thrown(InvalidUserDataException)
    }

    def "deploy with empty URL uses the default"() {
        when:
        def upload = project.tasks.add(name: "upload", type: Upload)
        project.clojars {
            username = "user"
            password = "password"
            deploy(upload)
        }

        then:
        project.clojars.url != null
        project.clojars.url.equals("https://clojars.org/repo")
    }

    def "deploy picks up properties"() {
        when:
        def upload = project.tasks.add(name: "upload", type: Upload)
        project["clojuresque.clojars.username"] = "user"
        project["clojuresque.clojars.password"] = "password"
        project["clojuresque.clojars.url"]      = "https://clojars.org/repo"
        project.clojars.deploy(upload)

        then:
        project.clojars.username != null
        project.clojars.username.equals("user")
        project.clojars.password != null
        project.clojars.password.equals("password")
        project.clojars.url      != null
        project.clojars.url.equals("https://clojars.org/repo")
    }

    def "deploy configures pom"() {
        when:
        def upload = project.tasks.add(name: "upload", type: Upload)
        project.clojars {
            username = "user"
            password = "password"
            deploy(upload) {
                project {
                    description "foobar"
                }
            }
        }

        then:
        upload.repositories.mavenDeployer.pom.model.description.equals("foobar")
    }
}
