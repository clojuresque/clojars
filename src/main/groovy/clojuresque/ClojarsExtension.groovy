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

import groovy.lang.Closure

public class ClojarsExtension {
    def String username = null
    def String password = null
    def String url      = null

    void deploy(Upload upload, Closure projectSpec={}) {
        Project p = upload.project

        if (username == null) {
            if (p.hasProperty("clojuresque.clojars.username")) {
                username = p["clojuresque.clojars.username"]
            } else {
                throw new InvalidUserDataException("Clojars user name missing!")
            }
        }

        if (password == null) {
            if (p.hasProperty("clojuresque.clojars.password")) {
                password = p["clojuresque.clojars.password"]
            } else {
                throw new InvalidUserDataException("Clojars password missing!")
            }
        }

        if (url == null) {
            if (p.hasProperty("clojuresque.clojars.url")) {
                url = p["clojuresque.clojars.url"]
            } else {
                url = "https://clojars.org/repo"
            }
        }

        upload.repositories {
            mavenDeployer {
                configuration = p.configurations.clojuresqueClojarsDeployerJars
                repository(url: this.url) {
                    authentication(
                        userName: this.username,
                        password: this.password
                    )
                }
                p.configure(pom, projectSpec)
                if (p.hasProperty("signing")) {
                    beforeDeployment { p.signing.signPom(it) }
                }
            }
        }
    }
}
